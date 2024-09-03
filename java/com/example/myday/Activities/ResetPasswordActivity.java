package com.example.myday.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.core.Amplify;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //obtain the reference to the constraint layout
        rootView = findViewById(R.id.rootLayout);
    }

    public void onResetPasswordClicked(View view) {
        // Obtain user's code and their new password
        EditText resetCodeText = findViewById(R.id.editTextResetCode);
        EditText newPasswordText = findViewById(R.id.editTextNewPassword);
        EditText confirmPasswordText = findViewById(R.id.editTextConfirmPassword);

        // Check if the passwords match
        if (newPasswordText.getText().toString().equals(
                confirmPasswordText.getText().toString())) {

            // Attempt to reset the password for the user
            Amplify.Auth.confirmResetPassword(
                    newPasswordText.getText().toString(), resetCodeText.getText().toString(),
                    this::onResetPasswordSuccess, this::onResetPasswordFailed
            );

        } else {
            Snackbar snackbar = Snackbar.make(rootView, "The passwords don't match", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void onResetPasswordSuccess() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void onResetPasswordFailed(AuthException error) {
        // Display an error message to the user
        this.runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }
}
