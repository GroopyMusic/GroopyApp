package adri.suys.un_mutescan.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.SQLOutput;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.presenter.LoginPresenter;

public class LoginActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText usernameInput;
    private EditText pwdInput;
    private ProgressBar progressBar;
    private LoginPresenter presenter;
    private Button showPassword;
    private boolean isShown = false;
    private final static String KEY_USERNAME = "username";
    private static final int RC_SAVE = 999;

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
    public void onResume() {
        super.onResume();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                showToast(getResources().getString(R.string.credentials_saved));
            } else {

            }
            showToast(getResources().getString(R.string.user_logged, usernameInput.getText().toString()));
            changeScreen();
        }
        presenter.setmIsResolving(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("onConnected");
        if (isInternetConnected()) {
            presenter.retrieveCredentials();
        } else {
            hideProgressBar();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed");
    }

    /**
     * Log the user with the username and the password he has typed in the Textfield
     * @param v the view linked to the Activity
     */
    public void loginViaUm(View v) {
        String username = usernameInput.getText().toString();
        if (!username.equals("")){
            showProgressBar();
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

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public String getUsername(){
        return usernameInput.getText().toString();
    }

    public String getPassword(){
        return pwdInput.getText().toString();
    }

    public void showSmartLockPopUp(String username) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        String message = getResources().getString(R.string.right_account, username);
        dialogBuilder.setMessage(message).setTitle("Smart Lock");
        dialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        showProgressBar();
                        presenter.logUserSmartlock();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        presenter.changeCreds();
                    }
                });
        AlertDialog alert = dialogBuilder.create();
        alert.setTitle("Smart Lock");
        alert.show();
    }

    /////////////////////
    // private methods //
    /////////////////////

    private void initViewElements() {
        usernameInput = findViewById(R.id.input_username);
        pwdInput = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        showProgressBar();
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
