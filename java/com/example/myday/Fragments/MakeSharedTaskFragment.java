package com.example.myday.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.SharedTask;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.UserSharedTask;
import com.example.myday.Classes.User;
import com.example.myday.Classes.UserListAdapter;
import com.example.myday.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MakeSharedTaskFragment extends DialogFragment {

    List<com.example.myday.Classes.User> users;
    SparseBooleanArray selectedUsers;
    UserListAdapter userListAdapter;
    SharedTask sharedTask;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Fetch the users from the database
        fetchUsers();

        if (getArguments() != null) {
            if (getArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert Dialog");
        builder.setMessage("Alert Dialog inside Dialog Fragment");

        builder.setPositiveButton("Ok", (dialog, which) -> dismiss());

        builder.setNegativeButton("Cancel", (dialog, which) -> dismiss());
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_make_shared_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get references to required views
        TextInputEditText taskName = view.findViewById(R.id.taskNameFragment);
        TextInputEditText taskDescription = view.findViewById(R.id.descriptionFragment);
        Chip timeChip = view.findViewById(R.id.timeChip);
        Chip priorityChip = view.findViewById(R.id.priorityChip);
        Chip collaboratorsChip = view.findViewById(R.id.collaboratorsChip);
        TimePicker timePicker = view.findViewById(R.id.timeFragment);
        Spinner prioritySpinner = view.findViewById(R.id.priorityFragment);
        ListView collaboratorsView = view.findViewById(R.id.collaboratorsListView);

        // Make the time picker display in 24h mode
        timePicker.setIs24HourView(true);

        // Populate the spinner with priority values
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.priorityArray, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(arrayAdapter);

        // Set up time chip click listener
        timeChip.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            //Toggle visibility of TimePicker based on chip selection
            if (isChecked) {
                timePicker.setVisibility(View.VISIBLE);
            } else {
                timePicker.setVisibility(View.GONE);
            }
        }));

        // Set up the priority chip listener
        priorityChip.setOnCheckedChangeListener((((buttonView, isChecked) -> {
            if (isChecked) {
                prioritySpinner.setVisibility(View.VISIBLE);
            } else {
                prioritySpinner.setVisibility(View.GONE);
            }
        })));

        // Set up the collaborators chip listener
        collaboratorsChip.setOnCheckedChangeListener((((buttonView, isChecked) -> {
            if (isChecked) {
                collaboratorsView.setVisibility(View.VISIBLE);
            } else {
                collaboratorsView.setVisibility(View.GONE);
            }
        })));

        // Populate the collaborators list view
        userListAdapter = new UserListAdapter(this.getContext(), users);
        collaboratorsView.setAdapter(userListAdapter);

        // Create a new task and save it in the database
        MaterialButton addButton = view.findViewById(R.id.addButtonFragment);
        addButton.setOnClickListener(view1 -> {

            // Set up the dialog listener
            MakeSharedTaskFragment.DialogListener dialogListener = (MakeSharedTaskFragment.DialogListener) getActivity();

            // Obtain the values from the view elements
            String taskNameString = Objects.requireNonNull(taskName.getText()).toString();
            String taskDescriptionString = Objects.requireNonNull(taskDescription.getText()).toString();
            String taskTimeString = timePicker.getHour() + ":" + timePicker.getMinute();
            String taskPriorityString = prioritySpinner.getSelectedItem().toString();
            // Get the selected users
            selectedUsers = userListAdapter.getSelectedUsers();

            // Attempt to save the shared task data in the database
            Amplify.API.mutate(ModelMutation.create(
                    SharedTask.builder().name(taskNameString).description(taskDescriptionString)
                            .time(taskTimeString).priority(taskPriorityString).completed("false").build()),
                    response -> {
                        sharedTask = response.getData();

                        // Fetch all the user objects of the selected users
                        for (int i = 0; i < selectedUsers.size(); i++) {
                            int key = selectedUsers.keyAt(i);
                            if (selectedUsers.get(key)) {
                                com.example.myday.Classes.User selectedUser = users.get(key);
                                fetchUser(selectedUser.getId());
                            }
                        }

                        assert dialogListener != null;
                        dialogListener.onFinishEditDialog("Task Created");
                    },
                    error -> {
                        assert dialogListener != null;
                        dialogListener.onFinishEditDialog("Error Creating Task");
                    });
            dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("API123", "onCreate");

        boolean setFullScreen = false;
        if (getArguments() != null) {
            setFullScreen = getArguments().getBoolean("fullScreen");
        }

        if (setFullScreen) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface DialogListener {
        void onFinishEditDialog(String snackbarMessage);
    }

    private void fetchUsers() {
        users = new ArrayList<>();

        Amplify.API.query(
                ModelQuery.list(com.amplifyframework.datastore.generated.model.User.class),
                (response) -> {
                    for (com.amplifyframework.datastore.generated.model.User user : response.getData()) {
                        // Populate the list of collaborators
                        users.add(new User(user.getId(), user.getName()));
                    }
                }, error -> {}
        );
    }

    private void fetchUser(String userId) {
        Amplify.API.query(
                ModelQuery.get(com.amplifyframework.datastore.generated.model.User.class, userId),
                response -> {
                    com.amplifyframework.datastore.generated.model.User user = response.getData();
                    createConnection(sharedTask, user);
                }, error -> {}
        );
    }

    private void createConnection(SharedTask sharedTask, com.amplifyframework.datastore.generated.model.User user) {
        Amplify.API.mutate(ModelMutation.create(
                UserSharedTask.builder().sharedTask(sharedTask).user(user).build()),
                response -> {},
                error -> {}
        );
    }
}
