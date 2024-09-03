package com.example.myday.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthResetPasswordResult;
import com.amplifyframework.core.Amplify;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //obtain the reference to the constraint layout
        rootView = findViewById(R.id.rootLayout);
    }

    public void onSendCodeClicked(View view) {
        // Obtain the email from the user
        EditText emailText = findViewById(R.id.editTextEmail);

        // Attempt to send the password reset code to the user
        Amplify.Auth.resetPassword(emailText.getText().toString(),
                this::onSendCodeSuccess, this::onSendCodeFailed
        );
    }

    private void onSendCodeSuccess(AuthResetPasswordResult authResetPasswordResult) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void onSendCodeFailed(AuthException error) {
        // Display an error message to the user
        this.runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }
}
