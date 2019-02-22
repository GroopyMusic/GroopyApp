package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;

import java.util.Observable;
import java.util.Observer;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.apirest.RestUser;
import adri.suys.un_mutescan.model.User;
import adri.suys.un_mutescan.security.BCrypt;

public class MainActivity extends Activity implements Observer {

    private Button loginUmBtn;
    private Button loginFacebookBtn;
    private EditText usernameInput;
    private EditText pwdInput;
    private TextView forgottenPassword;
    private RestUser restCommunication;
    private String password;
    private ProgressBar progressBar;
    private static int WRONG_CREDENTIAL = 405;
    private static int NOT_FOUND = 404;
    private static int CANT_ACCESS = 403;
    private static int OK = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            User user = (User) o;
            if (validateUser(user, password) == NOT_FOUND){
                Toast.makeText(this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
            } else if (validateUser(user, password) == CANT_ACCESS) {
                Toast.makeText(this, R.string.user_cant_access, Toast.LENGTH_SHORT).show();
            } else if (validateUser(user, password) == OK) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.user_wrong_credentials, Toast.LENGTH_SHORT).show();
            }
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    // private methods

    private void initViewElements() {
        loginFacebookBtn = findViewById(R.id.login_facebook_btn);
        loginUmBtn = findViewById(R.id.login_um_btn);
        usernameInput = findViewById(R.id.input_username);
        pwdInput = findViewById(R.id.input_password);
        forgottenPassword = findViewById(R.id.forgotten_pwd);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        setClickActions();
    }

    private void setClickActions() {
        loginFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViaFacebook();
            }
        });
        loginUmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViaUm();
            }
        });
        forgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPassword();
            }
        });
    }

    private void loginViaFacebook() {
        User user = new User(412, "adrien.suys@gmail.com", "", "");
        Intent intent = new Intent(MainActivity.this, EventActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void loginViaUm() {
        String username = usernameInput.getText().toString();
        password = pwdInput.getText().toString();
        if (!username.equals("")){
            progressBar.setVisibility(View.VISIBLE);
            restCommunication.loginUser(username);
        } else {
            Toast.makeText(this, R.string.user_no_login, Toast.LENGTH_SHORT).show();
        }
    }

    private void askForPassword() {
        // do something to get the password back
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
        int WORKLOAD = 12;
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(input, salt);
    }

}
