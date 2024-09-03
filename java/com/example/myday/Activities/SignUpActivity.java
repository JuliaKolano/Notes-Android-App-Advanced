package com.example.myday.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //obtain the reference to the constraint layout
        rootView = findViewById(R.id.rootLayout);
    }

    public void onSignUpClicked (View view) {
        // Obtain registration details from the user
        EditText emailText = findViewById(R.id.editTextEmail);
        EditText passwordText = findViewById(R.id.editTextPassword);
        EditText confirmPasswordText = findViewById(R.id.editTextConfirmPassword);

        // Check if the passwords match
        if (passwordText.getText().toString().equals(
                confirmPasswordText.getText().toString())) {

            // Attempt to create an account for the user
            Amplify.Auth.signUp(
                    emailText.getText().toString(), passwordText.getText().toString(),
                    AuthSignUpOptions.builder().userAttribute(
                            AuthUserAttributeKey.email(), emailText.getText().toString()).build(),
                    this::onSignUpSuccess, this::onSignUpFailed);
        } else {
            Snackbar snackbar = Snackbar.make(rootView, "The passwords don't match", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void onSignUpSuccess(AuthSignUpResult authSignUpResult) {
        // Redirect the user to the confirm email screen
        // And pass the email and password along with the intent
        EditText emailText = findViewById(R.id.editTextEmail);
        EditText passwordText = findViewById(R.id.editTextPassword);

        Intent intent = new Intent(this, ConfirmEmailActivity.class);
        intent.putExtra("email", emailText.getText().toString());
        intent.putExtra("password", passwordText.getText().toString());

        startActivity(intent);
    }

    private void onSignUpFailed(AuthException error) {
        // Display an error message to the user
        this.runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    public void onLoginClicked (View view) {
            // Redirect the user to the sign up page
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
    }
}
