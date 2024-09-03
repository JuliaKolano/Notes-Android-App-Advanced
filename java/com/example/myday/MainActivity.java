package com.example.myday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.example.myday.Activities.HomeActivity;
import com.example.myday.Activities.LoginActivity;
import com.example.myday.Classes.SwipeAction;
import com.example.myday.Classes.Task;
import com.example.myday.Classes.TaskRecyclerAdapter;
import com.example.myday.Fragments.MakeTaskFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MakeTaskFragment.DialogListener {

    // Instantiate required variables
    int currentThemeId = R.style.Theme_MyDay;
    int toolbarBackgroundColour;
    ArrayList<Task> tasks;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AuthUser currentUser = Amplify.Auth.getCurrentUser();
        Intent intent;
        if (currentUser == null) {
            // Go to login screen
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        } else {
            // Go to home screen
            intent = new Intent(getApplicationContext(), HomeActivity.class);
        }
        // Start activity
        startActivity(intent);
        finish();

        // Restore the currentThemeId and toolbarBackgroundColour from savedInstanceState
        if (savedInstanceState != null) {
            currentThemeId = savedInstanceState.getInt("currentThemeId");
            toolbarBackgroundColour = savedInstanceState.getInt("toolbarBackgroundColour");
        }

        // Apply the UI theme
        applyTheme();

        // Create or restore the activity
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the background colour of the toolbar
        if (toolbarBackgroundColour != 0) {
            toolbar.setBackgroundColor(toolbarBackgroundColour);
        }

        // Create a reference to the recycler view of tasks
        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);

        // Receive the parcelable array list through the intent
        tasks = getIntent().getParcelableArrayListExtra("tasks");

        // Error handling for the null pointer exception:
        // Check if the array list exists
        if (tasks != null && taskRecyclerView != null) {
                // Create and attach a new recycler adapter to the recycler view
                TaskRecyclerAdapter taskRecyclerAdapter = new TaskRecyclerAdapter(this, tasks);
                taskRecyclerView.setAdapter(taskRecyclerAdapter);

                // Delete the item from the list by a swiping action
                SwipeAction swipeAction = new SwipeAction(taskRecyclerAdapter, taskRecyclerView);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAction);
                itemTouchHelper.attachToRecyclerView(taskRecyclerView);
                taskRecyclerAdapter.notifyDataSetChanged();
        }

        // Make the task button open the make task fragment
        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> {
            MakeTaskFragment makeTaskFragment = new MakeTaskFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("notAlertDialog", true);

            makeTaskFragment.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("dialog");
            if (fragment != null) {
                ft.remove(fragment);
            }
            ft.addToBackStack(null);

            makeTaskFragment.show(ft, "dialog");
        });
    }

    // Change the theme based on the currentThemeId
    private void applyTheme() {
        int themeId = currentThemeId;
        setTheme(themeId);
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        // if I ever need to obtain the task name from the MakeTaskFragment
        // if i need to obtain different values, change the implementation of MakeTaskFragment
    }

    @Override
    // Set up the menu using an inflater
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    // Toolbar options updating the theme and toolbar background colour variables
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.purpleTheme:
                currentThemeId = R.style.Theme_MyDay;
                toolbarBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_purple);
                break;
            case R.id.blueTheme:
                currentThemeId = R.style.Theme_Blue;
                toolbarBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_blue);
                break;
            case R.id.yellowTheme:
                currentThemeId = R.style.Theme_Yellow;
                toolbarBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_yellow);
                break;
        }
        recreate();
        return super.onOptionsItemSelected(item);
    }

    // Save the state of the activity after the orientation changes or activity gets recreated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentThemeId", currentThemeId);
        outState.putInt("toolbarBackgroundColour", toolbarBackgroundColour);
        outState.putParcelableArrayList("tasks", tasks);
    }

    // Restore the instanceState of the activity
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tasks = savedInstanceState.getParcelableArrayList("tasks");
        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);

        // Restore the tasks array from savedInstanceState
        if (tasks != null && taskRecyclerView != null) {
                TaskRecyclerAdapter taskRecyclerAdapter = new TaskRecyclerAdapter(this, tasks);
                taskRecyclerView.setAdapter(taskRecyclerAdapter);

                taskRecyclerAdapter.notifyDataSetChanged();
        }
    }
}