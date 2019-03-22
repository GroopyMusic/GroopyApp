package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.presenter.LoginPresenter;

public class LoginActivity extends Activity {

    private EditText usernameInput;
    private EditText pwdInput;
    private ProgressBar progressBar;
    private LoginPresenter presenter;
    private Button showPassword;
    private boolean isShown = false;
    private final static String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        configActionBar();
        initViewElements();
        if (savedInstanceState != null){
            pwdInput.setText(savedInstanceState.getString(KEY_USERNAME));
        }
        presenter = new LoginPresenter(this);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_USERNAME, pwdInput.getText().toString());
    }

    /**
     * Log the user with the username and the password he has typed in the Textfield
     * @param v the view linked to the Activity
     */
    public void loginViaUm(View v) {
        String username = usernameInput.getText().toString();
        presenter.setPassword(pwdInput.getText().toString());
        if (!username.equals("")){
            progressBar.setVisibility(View.VISIBLE);
            presenter.logUser(username.toLowerCase());
        } else {
            showToast(getResources().getString(R.string.user_no_login));
        }
    }

    /**
     * Changes the screen to the one that has all the events (those linked to the user) listed.
     */
    public void changeScreen(){
        startActivity(new Intent(this, EventListActivity.class));
    }

    /**
     * Hide the ProgressBar
     */
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void initViewElements() {
        usernameInput = findViewById(R.id.input_username);
        pwdInput = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        showPassword = findViewById(R.id.password_visibility);
        handleShowHidePwd();
    }

    private void handleShowHidePwd(){
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShown) {
                    pwdInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShown = true;
                    showPassword.setBackgroundResource(R.drawable.ic_visibility_off_green_24dp);
                } else {
                    pwdInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShown = false;
                    showPassword.setBackgroundResource(R.drawable.ic_visibility_green_24dp);
                }
            }
        });
    }
}
