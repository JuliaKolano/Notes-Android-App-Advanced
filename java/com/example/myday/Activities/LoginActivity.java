package com.example.myday.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.core.Amplify;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //obtain the reference to the constraint layout
        rootView = findViewById(R.id.rootLayout);
    }

    public void onLoginClicked (View view) {
        // obtain login details from the user
        EditText emailText = findViewById(R.id.editTextEmail);
        EditText passwordText = findViewById(R.id.editTextPassword);

        // Attempt to log in the user
        Amplify.Auth.signIn(emailText.getText().toString(), passwordText.getText().toString(),
                this::onLoginSuccess, this::onLoginFailed
        );
    }

    private void onLoginSuccess(AuthSignInResult authSignInResult) {
        // Redirect the user to the home screen
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void onLoginFailed(AuthException error) {
        // Display an error message to the user
        this.runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    public void onForgotPasswordClicked (View view) {
        // Redirect the user to the forgot password page
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void onSignUpClicked (View view) {
        // Redirect the user to the sign up page
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}
