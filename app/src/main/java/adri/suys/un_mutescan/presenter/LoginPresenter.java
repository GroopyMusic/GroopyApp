package adri.suys.un_mutescan.presenter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.activities.LoginActivity;
import adri.suys.un_mutescan.apirest.RestService;
import adri.suys.un_mutescan.utils.UnMuteDataHolder;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.security.BCrypt;

public class LoginPresenter {

    private RestService restCommunication;
    private String password;
    private static final int NOT_FOUND = 404;
    private static final int CANT_ACCESS = 403;
    private static final int OK = 400;
    private static final int WRONG_CREDENTIAL = 405;
    private LoginActivity view;

    public LoginPresenter(LoginActivity view){
        this.view = view;
        restCommunication = new RestService(view);
        restCommunication.setUserPresenter(this);
    }

    public void logUser(String username){
        restCommunication.loginUser(username);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginActivity getView() {
        return view;
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

    public void handleJSONObject(JSONObject response, Gson gson) {
        User user = gson.fromJson(response.toString(), User.class);
        UnMuteDataHolder.setUser(user);
        switch (validateUser(user, password)) {
            case NOT_FOUND:
                view.showToast(view.getResources().getString(R.string.user_not_found));
                break;
            case CANT_ACCESS:
                view.showToast(view.getResources().getString(R.string.user_cant_access));
                break;
            case OK:
                view.showToast(view.getResources().getString(R.string.user_logged, user.getName()));
                view.changeScreen();
                break;
            default:
                view.showToast(view.getResources().getString(R.string.user_wrong_credentials));
                break;
        }
        view.hideProgressBar();
    }

    public void handleVolleyError(VolleyError error) {
        String message = "";
        if (error instanceof NoConnectionError || error instanceof TimeoutError){
            message = view.getResources().getString(R.string.volley_error_no_connexion);
        } else if (error instanceof AuthFailureError){
            message = view.getResources().getString(R.string.volley_error_server_error);
        } else if (error instanceof ServerError){
            message = view.getResources().getString(R.string.volley_error_server_error);
        } else if (error instanceof NetworkError) {
            message = view.getResources().getString(R.string.volley_error_server_error);
        } else if (error instanceof ParseError){
            message = view.getResources().getString(R.string.volley_error_server_error);
        }
        view.showToast(message);
        view.hideProgressBar();
    }
}
