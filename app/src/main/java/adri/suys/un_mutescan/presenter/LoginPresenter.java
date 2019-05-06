package adri.suys.un_mutescan.presenter;

import android.content.IntentSender;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Date;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.LoginActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.security.BCrypt;
import adri.suys.un_mutescan.viewinterfaces.LoginViewInterface;

public class LoginPresenter {

    private final RestService restCommunication;
    private String username;
    private String password;

    private static final int NOT_FOUND = 404;
    private static final int CANT_ACCESS = 403;
    private static final int OK = 400;
    private static final int WRONG_CREDENTIAL = 405;
    private static final int RC_SAVE = 999;

    private final LoginViewInterface view;
    private final GoogleApiClient credentialsClient;
    private boolean hasCredentialsStocked;
    private Credential credential;
    private boolean mIsResolving;

    public LoginPresenter(LoginActivity view){
        this.view = view;
        restCommunication = new RestService(view);
        restCommunication.setUserPresenter(this);
        CredentialsOptions credentialsOptions = new CredentialsOptions.Builder().forceEnableSaveDialog().build();
        credentialsClient = new GoogleApiClient.Builder(view)
                .addConnectionCallbacks(view)
                .enableAutoManage(view, 0, view)
                .addApi(Auth.CREDENTIALS_API, credentialsOptions)
                .build();
    }

    public void logUser(boolean isInternetConnected, String username){
        if (isInternetConnected){
            this.username = username;
            this.password = view.getPassword();
            restCommunication.loginUser(username);
        } else {
            User userInDB = view.retrieveUser();
            if (userInDB != null && userInDB.getUsername().equals(username) && userInDB.isTokenStillActive()){
                handleNewConnection(userInDB);
                view.showNoAccessToInternetToast();
                view.changeScreen();
            } else {
                view.showConnectionProblemToast();
            }
        }
    }

    public void logUserSmartlock() {
        this.password = credential.getPassword();
        this.username = credential.getId();
        restCommunication.loginUser(username);
    }

    public void handleJSONObject(JSONObject response, Gson gson) {
        User user = gson.fromJson(response.toString(), User.class);
        switch (validateUser(user, password)) {
            case NOT_FOUND:
                view.showUnvalidUsernameToast();
                break;
            case CANT_ACCESS:
                view.showCantUseAppToast();
                break;
            case OK:
                if (!hasCredentialsStocked) saveCredentials();
                handleNewConnection(user);
                view.showHelloToast(user.getName());
                view.changeScreen();
                break;
            default:
                view.showBadCredentialsToast();
                break;
        }
        view.hideProgressBar();
    }

    public void handleVolleyError(VolleyError error) {
        if (error instanceof NoConnectionError || error instanceof TimeoutError){
            view.showNoConnectionRetryToast();
        } else if (error instanceof AuthFailureError){
            view.showServerConnectionProblemToast();
        } else if (error instanceof ServerError){
            view.showServerConnectionProblemToast();
        } else if (error instanceof NetworkError) {
            view.showServerConnectionProblemToast();
        } else if (error instanceof ParseError){
            view.showServerConnectionProblemToast();
        }
        view.hideProgressBar();
    }

    public LoginViewInterface getView() {
        return view;
    }

    public void setmIsResolving(boolean mIsResolving) {
        this.mIsResolving = mIsResolving;
    }

    public void retrieveCredentials(){
        CredentialRequest request = new CredentialRequest.Builder().setPasswordLoginSupported(true).build();
        Auth.CredentialsApi.request(credentialsClient, request).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(CredentialRequestResult credentialRequestResult) {
                        Status status = credentialRequestResult.getStatus();
                        if (credentialRequestResult.getStatus().isSuccess()) {
                            onCredentialRetrieved(credentialRequestResult.getCredential());
                        } else if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                            view.hideProgressBar();
                        } else {
                            view.hideProgressBar();
                            System.out.println("Unrecognized status code: " + status.getStatusCode());
                        }
                    }
                }
        );
    }

    public void changeCreds(){
        deleteCredential(credential);
        hasCredentialsStocked = false;
    }

    private void deleteCredential(Credential cred){
        Auth.CredentialsApi.delete(credentialsClient,
                cred).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    // yeah
                } else {
                    // oops
                }
            }
        });
    }

    private void onCredentialRetrieved(Credential credential){
        hasCredentialsStocked = true;
        this.credential = credential;
        view.hideProgressBar();
        view.showSmartLockPopUp(credential.getId());
    }

    private void saveCredentials(){
        Credential credential = new Credential.Builder(username).setPassword(password).build();
        Auth.CredentialsApi.save(credentialsClient, credential).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()){
                    view.showCredentialsSavedToast();
                } else {
                    view.resolveResult(status, RC_SAVE, mIsResolving);
                }
            }
        });
    }

    private int validateUser(User user, String input){
        if (user.getError().equals("Cet utilisateur n'existe pas.")){
            return NOT_FOUND;
        }
        if (user.getError().equals("Vous ne pouvez pas utiliser l'application. Vous devez être gestionnaire de campagnes.")){
            return CANT_ACCESS;
        }
        if (checkPwd(user.getPassword(), input)){
            return OK;
        } else {
            return WRONG_CREDENTIAL;
        }
    }

    private boolean checkPwd(String password, String input) {
        String userHash = password.substring(0, 2) + "a" + password.substring(3);
        String computedHash = hashPassword(input);
        boolean verifyPassword = checkPassword(input, userHash);
        boolean verifyComputed = checkPassword(input, computedHash);
        return verifyPassword && verifyComputed;
    }

    private boolean checkPassword(String input, String userHash) {
        if(null == userHash || !userHash.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }
        return BCrypt.checkpw(input, userHash);
    }

    private String hashPassword(String input) {
        int workload = 12; // arbitrary choice
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(input, salt);
    }

    private void handleNewConnection(User user){
        user.setLastConnection(new Date().getTime());
        UnMuteDataHolder.setUser(user);
        view.backUpUser();
    }
}
