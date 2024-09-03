package com.example.myday.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amplifyframework.api.ApiException;
import com.amplifyframework.api.graphql.GraphQLResponse;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Notes;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.User;
import com.example.myday.Activities.HomeActivity;
import com.example.myday.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class NotesFragment extends Fragment {

    View rootView;
    MaterialTextView notesContentView;
    TextInputEditText notesEditContentView;
    MaterialButton editButton;
    MaterialButton saveButton;

    String notesContent;
    String notesID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set the contents of the notes text view
        setNotesContent(savedInstanceState);

        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the views
        rootView = view.findViewById(R.id.rootLayout);
        notesContentView = view.findViewById(R.id.notesContent);
        notesEditContentView = view.findViewById(R.id.notesEditContent);
        saveButton = view.findViewById(R.id.saveNotesButton);
        editButton = view.findViewById(R.id.editNotesButton);

        //set up the notes content view
        notesContentView.setText(notesContent);

        // Hide the floating action button
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if (homeActivity != null) {
            homeActivity.setAddTaskButtonVisibility(View.GONE);
        }


        // Set up an on click listener for the edit button
        editButton.setOnClickListener(v -> allowNotesEdit());

        // Set up an on click listener for the save button
        saveButton.setOnClickListener(v -> saveEditedNotes());
    }

    private void setNotesContent(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fetchNotesId();
        } else {
            notesContent = savedInstanceState.getString("notesContent");
            notesID = savedInstanceState.getString("notesID");
        }
    }

    // Fetch the ID of the user's notes
    private void fetchNotesId() {
        // Get the current Cognito user's ID
        String userId = Amplify.Auth.getCurrentUser().getUserId();

        // Get the user's data from database
        Amplify.API.query(
                ModelQuery.get(User.class, userId),
                (response) -> {
                    notesID = response.getData().getUserNotesId(); //TODO
                    fetchNotesContent(notesID);
                }, (error) -> {
                    Snackbar snackbar = Snackbar.make(rootView, "Error Fetching Notes", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        );
    }

    // Fetch the contents of the notes and it to the text of the text view
    private void fetchNotesContent(String notesId) {
        Amplify.API.query(
                ModelQuery.get(Notes.class, notesId),
                (response) -> {
                    notesContent = response.getData().getContent().trim(); //TODO
                    notesContentView.setText(notesContent);
                },
                (error) -> {
                    Snackbar snackbar = Snackbar.make(rootView, "Error Fetching Notes", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        );
    }

    private void allowNotesEdit() {
        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        notesContentView.setVisibility(View.GONE);
        notesEditContentView.setVisibility(View.VISIBLE);
        notesEditContentView.setText(notesContent);
    }

    private void saveEditedNotes() {
        String updatedNotesContent = Objects.requireNonNull(notesEditContentView.getText()).toString();
        updatedNotesContent = updatedNotesContent.replaceAll("\t", " ");
        updatedNotesContent = updatedNotesContent.replaceAll("\n", " ").trim();

        Amplify.API.mutate(ModelMutation.update(
                Notes.builder().id(notesID).content(updatedNotesContent).build()),
                this::onSuccess, this::onFailed
            );
    }

    private void onSuccess(GraphQLResponse<Notes> notesGraphQLResponse) {
        rootView.post(() -> {
            Snackbar snackbar = Snackbar.make(rootView, "Notes Saved", Snackbar.LENGTH_LONG);
            snackbar.show();
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            notesEditContentView.setVisibility(View.GONE);
            notesContentView.setVisibility(View.VISIBLE);
            fetchNotesContent(notesID);
        });
    }

    private void onFailed(ApiException error) {
        rootView.post(() -> {
            Snackbar snackbar = Snackbar.make(rootView, Objects.requireNonNull(error.getMessage()), Snackbar.LENGTH_LONG);
            snackbar.show();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("notesContent", notesContent);
        outState.putString("notesId", notesID);
    }
}
