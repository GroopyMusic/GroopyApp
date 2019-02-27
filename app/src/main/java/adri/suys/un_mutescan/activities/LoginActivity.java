package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.NoConnectionError;

import java.util.Observable;
import java.util.Observer;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestUser;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.security.BCrypt;

public class LoginActivity extends Activity implements Observer {

    private EditText usernameInput;
    private EditText pwdInput;
    private RestUser restCommunication;
    private String password;
    private ProgressBar progressBar;
    private static final int NOT_FOUND = 404;
    private static final int CANT_ACCESS = 403;
    private static final int OK = 400;
    private static final int WRONG_CREDENTIAL = 405;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        configActionBar();
        initViewElements();
        restCommunication = new RestUser(this);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof NoConnectionError){
            String message = getResources().getString(R.string.no_connexion);
            showToast(message);
        } else {
            User user = (User) o;
            UnMuteDataHolder.setUser(user);
            switch (validateUser(user, password)) {
                case NOT_FOUND:
                    showToast(getResources().getString(R.string.user_not_found));
                    break;
                case CANT_ACCESS:
                    showToast(getResources().getString(R.string.user_cant_access));
                    break;
                case OK:
                    showToast(getResources().getString(R.string.user_logged));
                    startActivity(new Intent(this, EventListActivity.class));
                    break;
                default:
                    showToast(getResources().getString(R.string.user_wrong_credentials));
                    break;
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Log the user with the username and the password he has typed in the Textfield
     * @param v the view linked to the Activity
     */
    public void loginViaUm(View v) {
        String username = usernameInput.getText().toString();
        password = pwdInput.getText().toString();
        if (!username.equals("")){
            progressBar.setVisibility(View.VISIBLE);
            restCommunication.loginUser(username);
        } else {
            showToast(getResources().getString(R.string.user_no_login));
        }
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void initViewElements() {
        usernameInput = findViewById(R.id.input_username);
        pwdInput = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private int validateUser(User user, String input){
        if (user.getError().equals("Cet utilisateur n\'existe pas.")){
            return NOT_FOUND;
        }
        if (user.getError().equals("Vous ne pouvez pas utiliser l\\'application. Vous devez être gestionnaire de campagnes.")){
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

}
