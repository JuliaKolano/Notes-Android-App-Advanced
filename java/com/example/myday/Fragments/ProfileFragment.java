package com.example.myday.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.amplifyframework.api.ApiException;
import com.amplifyframework.api.graphql.GraphQLResponse;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.User;
import com.example.myday.Activities.HomeActivity;
import com.example.myday.Activities.LoginActivity;
import com.example.myday.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    View rootView;
    public View profileView;
//    int profileViewBackgroundColour;     // Failed attempt at changing the background of the profile fragment
    MaterialTextView nameView;
    MaterialTextView emailView;
    TextInputEditText editNameView;
    ImageView profileImageView;
    ImageView saveButton;
    MaterialButton logoutButton;
    String userId;
    String email;
    String name;
    String notesId;
    String profilePicture;
    Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Obtain data about the user from database or saved instance state
        if (savedInstanceState == null) {
            fetchUser();
            // Failed attempt at changing the background of the profile fragment
//            profileViewBackgroundColour = ContextCompat.getColor(requireContext(), R.color.light_purple);
        } else {
            userId = savedInstanceState.getString("userId");
            name = savedInstanceState.getString("name");
            email = savedInstanceState.getString("email");
            notesId = savedInstanceState.getString("notesId");
            profilePicture = savedInstanceState.getString("profilePicture");
            // Failed attempt at changing the background of the profile fragment
//            profileViewBackgroundColour = savedInstanceState.getInt("profileViewBackgroundColour");
        }

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain all the required views
        rootView = view.findViewById(R.id.rootLayout);
        profileView = view.findViewById(R.id.profileView);
        nameView = view.findViewById(R.id.nameView);
        emailView = view.findViewById(R.id.emailView);
        editNameView = view.findViewById(R.id.editNameView);
        profileImageView = view.findViewById(R.id.profilePicture);
        saveButton = view.findViewById(R.id.saveName);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Failed attempt at changing the background of the profile fragment
//        profileView.setBackgroundColor(profileViewBackgroundColour);

        // Hide the floating action button
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if (homeActivity != null) {
            homeActivity.setAddTaskButtonVisibility(View.GONE);
        }

        // Set the contents of the view elements
        populateViews();

        // Set up an on click listener on the logout button
        logoutButton.setOnClickListener(v -> logOut());

        // Set up an on click listener on the name layout
        nameView.setOnClickListener(v -> allowNameEdit());

        // Set up an on click listener on the save button
        saveButton.setOnClickListener(v -> saveName());

        // Set up an on click listener on the profile image view
        profileImageView.setOnClickListener(v -> chooseImage());
    }

    // Gets invoked after the chooseImage method
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {
            Log.i("result: ", String.valueOf(resultCode));
            // Delete the old picture from S3 bucket
            deleteOldImage();

            // Create a unique key for the S3 bucket image file reference
            assert data != null;
            imageUri = data.getData();
            profilePicture = (name + Calendar.getInstance().getTime()).replace(" ", "");

            // upload the chosen image to the database
            uploadImage(imageUri, profilePicture);
        }
    }

    // Failed attempt at changing the background of the profile fragment
//    @SuppressLint("ResourceAsColor")
//    public void updateProfileViewBackground(int colour) {
//        profileViewBackgroundColour = colour;
//
//        if (profileView != null) {
//            profileView.setBackgroundColor(profileViewBackgroundColour);
//        }
//    }

    // Let the user choose an image from gallery or use their camera
    private void chooseImage() {
        ImagePicker.with(this)
                .crop(1f, 1f)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    private void deleteOldImage() {
        Amplify.Storage.remove(
                profilePicture,
                result -> {},
                error -> rootView.post(() -> {
                    Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
                    snackbar.show();
                })
        );
    }

    private void uploadImage(Uri uri, String key) {
        try {
            InputStream imageFile = requireActivity().getContentResolver().openInputStream(uri);
            Amplify.Storage.uploadInputStream(
                    key,
                    imageFile,
                    result -> saveProfilePicture(profilePicture),
                    storageFailure -> rootView.post(() -> {
                        Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(storageFailure.getMessage()), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    })
            );
        } catch (FileNotFoundException error) {
            rootView.post(() -> {
                Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
                snackbar.show();
            });
        }
    }

    // Save the key to the S3 bucket inside of the user's profilePicture field
    private void saveProfilePicture(String profilePicture) {
        Amplify.API.mutate(ModelMutation.update(
                        User.builder().email(email).name(name).id(userId).userNotesId(notesId).profilePicture(profilePicture).build()),
                this::onSaveImageSuccess, this::onSaveImageFailed
        );
    }

    // Display the profile picture after it's uploaded to the S3 bucket
    private void onSaveImageSuccess(GraphQLResponse<User> userGraphQLResponse) {
        rootView.post(() -> profileImageView.setImageURI(imageUri));
    }

    private void onSaveImageFailed(ApiException error) {
        rootView.post(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    // Allow the user to edit their name
    private void allowNameEdit() {
        nameView.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        editNameView.setVisibility(View.VISIBLE);
        editNameView.setText(name);
    }

    // Save the edited name to the database
    private void saveName() {
        String updatedName = Objects.requireNonNull(editNameView.getText()).toString();

        Amplify.API.mutate(ModelMutation.update(
                User.builder().email(email).name(updatedName).id(userId).userNotesId(notesId).profilePicture(profilePicture).build()),
                this::onSaveNameSuccess, this::onSaveNameFailed
        );
    }

    private void onSaveNameSuccess(GraphQLResponse<User> userGraphQLResponse) {
        rootView.post(() -> {
            name = userGraphQLResponse.getData().getName();
            populateViews();

            editNameView.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            nameView.setVisibility(View.VISIBLE);
        });
    }

    private void onSaveNameFailed(ApiException error) {
        rootView.post(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    private void fetchUser() {
        userId = Amplify.Auth.getCurrentUser().getUserId();
        Amplify.API.query(
                ModelQuery.get(User.class, userId),
                response -> {
                    email = response.getData().getEmail();
                    name = response.getData().getName();
                    notesId = response.getData().getUserNotesId();
                    profilePicture = response.getData().getProfilePicture();
                    populateViews();
                }, error -> {
                    Snackbar snackbar = Snackbar.make(rootView, "Error Fetching User Data", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        );
    }

    // Populate the email view with database data
    private void populateViews() {
        // Set the email text
        if (email != null) { emailView.setText(email); }
        // Set the name text
        if (name != null) { nameView.setText(name); }
        // Set the profile picture
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Amplify.Storage.downloadFile(
                    profilePicture,
                    new File(requireActivity().getApplicationContext().getFilesDir() + "/ProfilePicture.txt"),
                    result -> {
                        // Convert the image file to an Uri object and display it
                        URI imageURI = result.getFile().toURI();
                        Log.i("imageURI", String.valueOf(imageURI));
                        imageUri = Uri.parse(imageURI.toString());
                        profileImageView.setImageURI(imageUri);
                    },
                    error -> rootView.post(() -> {
                        Snackbar snackbar = Snackbar.make(rootView, "Failed fetching profile picture", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    })
            );
        }
    }

    // Log out the user and redirect them to the login page
    private void logOut() {
        Amplify.Auth.signOut(
                () -> {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }, error -> {
                    Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        );
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("userId", userId);
        outState.putString("name", name);
        outState.putString("email", email);
        outState.putString("notesId", notesId);
        outState.putString("profilePicture", profilePicture);
        // Failed attempt at changing the background of the profile fragment
//        outState.putInt("profileViewBackgroundColour", profileViewBackgroundColour);
    }
}