package com.example.myday.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.ApiException;
import com.amplifyframework.api.graphql.GraphQLResponse;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.result.AuthSignInResult;
import com.amplifyframework.auth.result.AuthSignUpResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Notes;
import com.amplifyframework.datastore.generated.model.User;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class ConfirmEmailActivity extends AppCompatActivity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);

        //obtain the reference to the constraint layout
        rootView = findViewById(R.id.rootLayout);
    }

    public void onVerifyClicked(View view) {
        // Obtain verification code from the user
        EditText verificationCodeText = findViewById(R.id.editTextVerificationCode);

        // Attempt to verify the user's account
        Amplify.Auth.confirmSignUp(getEmail(), verificationCodeText.getText().toString(),
                this::onVerifySuccess, this::onFailed
        );
    }

    private void onVerifySuccess(AuthSignUpResult authSignUpResult) {
        // Automatically log in user into the application

        // Obtain the email and password from previous activity
        String email = getEmail();
        String password = getPassword();

        // Attempt to log in the user
        Amplify.Auth.signIn(email, password, this::onLoginSuccess, this::onFailed);
    }

    private void onLoginSuccess(AuthSignInResult authSignInResult) {
        // Create the user's notes object
        Amplify.API.mutate(ModelMutation.create(
                Notes.builder().content("").build()),
                this::onNotesCreateSuccess, this::onFailed);
    }

    private void onNotesCreateSuccess(GraphQLResponse<Notes> notesGraphQLResponse) {
        // Obtain the user's data
        String userId = Amplify.Auth.getCurrentUser().getUserId();
        String email = getEmail();
        // Obtain user's name from the email
        String name = email.substring(0, email.indexOf('@'));
        // Obtain notes ID
        String notesId = notesGraphQLResponse.getData().getId();

        // Save user's data into the database
        Amplify.API.mutate(ModelMutation.create(
                User.builder().email(email).name(name).id(userId).userNotesId(notesId).profilePicture("").build()),
                this::onSaveSuccess, this::onFailed);
    }

    private void onSaveSuccess(GraphQLResponse<User> userGraphQLResponse) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void onFailed(AuthException error) {
        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    private void onFailed(ApiException error) {
        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    private String getEmail() {
        return getIntent().getStringExtra("email");
    }

    private String getPassword() {
        return getIntent().getStringExtra("password");
    }
}
