package com.example.myday.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.myday.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class MakeTaskFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

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
        return inflater.inflate(R.layout.fragment_make_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to required views
        TextInputEditText taskName = view.findViewById(R.id.taskNameFragment);
        TextInputEditText taskDescription = view.findViewById(R.id.descriptionFragment);
        Chip timeChip = view.findViewById(R.id.timeChip);
        Chip priorityChip = view.findViewById(R.id.priorityChip);
        TimePicker timePicker = view.findViewById(R.id.timeFragment);
        Spinner prioritySpinner = view.findViewById(R.id.priorityFragment);

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

        // Set the text of the task name edit text view
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("taskName"))) {
            taskName.setText(getArguments().getString("taskName"));
        }

        // Create a new task and save it in the database
        MaterialButton addButton = view.findViewById(R.id.addButtonFragment);
        addButton.setOnClickListener(view1 -> {
            // Set up the dialog listener
            DialogListener dialogListener = (DialogListener) getActivity();

            // Obtain user data
            String userId = Amplify.Auth.getCurrentUser().getUserId();

            // Obtain the values from the view elements
            String taskNameString = Objects.requireNonNull(taskName.getText()).toString();
            String taskDescriptionString = Objects.requireNonNull(taskDescription.getText()).toString();
            String taskTimeString = timePicker.getHour() + ":" + timePicker.getMinute();
            String taskPriorityString = prioritySpinner.getSelectedItem().toString();

            // Attempt to save the task data in the database
            Amplify.API.mutate(ModelMutation.create(
                    Task.builder().userId(userId).name(taskNameString).description(taskDescriptionString)
                            .time(taskTimeString).priority(taskPriorityString).completed("false").build()),
                    response -> {
                        assert dialogListener != null;
                        dialogListener.onFinishEditDialog("Task Created");
                    },
                    error -> {
                        assert dialogListener != null;
                        dialogListener.onFinishEditDialog("Error Creating Task");
                    }
            );
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
}
