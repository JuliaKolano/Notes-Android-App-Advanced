package com.example.myday.Activities;

import android.annotation.SuppressLint;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.example.myday.Classes.SwipeAction;
import com.example.myday.Classes.Task;
import com.example.myday.Classes.TaskRecyclerAdapter;
import com.example.myday.Fragments.EditSharedTaskFragment;
import com.example.myday.Fragments.EditTaskFragment;
import com.example.myday.Fragments.MakeSharedTaskFragment;
import com.example.myday.Fragments.MakeTaskFragment;
import com.example.myday.Fragments.NotesFragment;
import com.example.myday.Fragments.ProfileFragment;
import com.example.myday.Fragments.SharedTasksFragment;
import com.example.myday.R;
import com.example.myday.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements MakeTaskFragment.DialogListener,
                                                    EditTaskFragment.DialogListener,
                                                    MakeSharedTaskFragment.DialogListener,
                                                    EditSharedTaskFragment.DialogListener,
                                                    TaskRecyclerAdapter.OnTaskLongClickListener {

    // Instantiate required variables
    int currentThemeId = R.style.Theme_MyDay;
    int toolbarBackgroundColour;
    int searchBarColour;
//    int profileViewBackgroundColour;     // Failed attempt at changing the background of the profile fragment
    static ArrayList<Task> tasks = new ArrayList<>();
    ActivityMainBinding binding;
    TaskRecyclerAdapter taskRecyclerAdapter;
    GradientDrawable searchBarBackground;
    View rootView;
    SearchView searchBar;

    @SuppressLint({"NotifyDataSetChanged", "NonConstantResourceId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restore the currentThemeId and toolbarBackgroundColour from savedInstanceState
        if (savedInstanceState != null) {
            currentThemeId = savedInstanceState.getInt("currentThemeId");
            toolbarBackgroundColour = savedInstanceState.getInt("toolbarBackgroundColour");

            searchBarColour = savedInstanceState.getInt("strokeColour");
            searchBarBackground = createGradientDrawable(searchBarColour);

            // Failed attempt at changing the background of the profile fragment
//            profileViewBackgroundColour = savedInstanceState.getInt("profileViewBackgroundColour");

            tasks = savedInstanceState.getParcelableArrayList("tasks");
        } else {
            // Failed attempt at changing the background of the profile fragment
//            profileViewBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_purple);
            searchBarColour = ContextCompat.getColor(getApplicationContext(), R.color.blue);
            searchBarBackground = createGradientDrawable(searchBarColour);
        }

        // Apply the UI theme
        applyTheme();

        // Create or restore the activity and set up the view
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.constraintLayout);

        //Set up binding for easier navigation
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the background colour of the toolbar
        if (toolbarBackgroundColour != 0) {
            toolbar.setBackgroundColor(toolbarBackgroundColour);
        }

        // Set up the search bar
        searchBar = findViewById(R.id.searchView);
        searchBar.clearFocus();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTasks(newText);
                return true;
            }
        });

        // Set up the background drawable for the search bar
        if (searchBarBackground != null) {
            searchBar.setBackground(searchBarBackground);
        }

        // Create a reference to the recycler view of tasks
        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);

        if (taskRecyclerView != null) {
            // Attach the recycler adapter to the recycler view
            taskRecyclerAdapter = new TaskRecyclerAdapter(tasks);
            taskRecyclerAdapter.setOnTaskLongClickListener(this);
            taskRecyclerView.setAdapter(taskRecyclerAdapter);

            // Delete the item from the list by a swiping action
            if (savedInstanceState == null) {
                SwipeAction swipeAction = new SwipeAction(taskRecyclerAdapter, taskRecyclerView);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAction);
                itemTouchHelper.attachToRecyclerView(taskRecyclerView);
                taskRecyclerAdapter.notifyDataSetChanged();
            }
        }

        // fetch the list of tasks from the backend
        fetchTasks();

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

        // Set the default selected icon to the home icon
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        // Make the bottom menu bar navigate to the corresponding screens
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.home:
                    removeFragment();
                    break;
                case R.id.sharedTasks:
                    replaceFragment(new SharedTasksFragment());
                    break;
                case R.id.notes:
                    replaceFragment(new NotesFragment());
                    break;
            }
            return true;
        });
    }

    // Change the theme based on the currentThemeId
    private void applyTheme() {
        int themeId = currentThemeId;
        setTheme(themeId);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchTasks() {
        // Get the current user's ID
        String userId = Amplify.Auth.getCurrentUser().getUserId();

        // Attempt to fetch the list of tasks from the database
        Amplify.API.query(
                ModelQuery.list(com.amplifyframework.datastore.generated.model.Task.class,
                com.amplifyframework.datastore.generated.model.Task.USER_ID.contains(userId)),
                (response) -> runOnUiThread(() -> {
                        tasks.clear();
                    for (com.amplifyframework.datastore.generated.model.Task task : response.getData()) {
                        Task newTask = new Task(task.getId(), task.getName(), task.getDescription(),
                                task.getTime(), task.getPriority(), task.getCompleted());
                        tasks.add(newTask);
                        taskRecyclerAdapter.notifyDataSetChanged();
                    }
                }),
                error -> runOnUiThread(() -> {
                    Snackbar snackbar = Snackbar.make(binding.getRoot(), "There was an error generating tasks", Snackbar.LENGTH_LONG);
                    snackbar.show();
                })
        );
    }

    // Filter the tasks from the list based on the name, description, time, or priority
    private void filterTasks(String text) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getName().toLowerCase().contains(text.toLowerCase()) ||
                    task.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                    task.getTime().toLowerCase().contains(text.toLowerCase()) ||
                    task.getPriority().toLowerCase().contains(text.toLowerCase())) {
                filteredTasks.add(task);
            }
        }
        taskRecyclerAdapter.setFilteredTasks(filteredTasks);
    }

    // Make a drawable file programmatically by providing it with properties
    private GradientDrawable createGradientDrawable(int strokeColour) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadius(20);
        gradientDrawable.setStroke(5, strokeColour);

        return gradientDrawable;
    }

    // Replace the frame layout with a fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    // Remove any fragments that are currently displayed
    private void removeFragment() {
        // Remove current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
        }

        // Make the floating action button visible and change its on click listener
        setAddTaskButtonVisibility(View.VISIBLE);
        setAddButtonOnClickListener("home");
    }

    // Control the visibility of the floating action button
    public void setAddTaskButtonVisibility(int visibility) {
        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setVisibility(visibility);
    }

    public void setAddButtonOnClickListener(String currentScreen) {
        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);

        switch (currentScreen) {
            case "home":
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
                break;
            case "sharedTasks":
                addTaskButton.setOnClickListener(v -> {
                    MakeSharedTaskFragment makeSharedTaskFragment = new MakeSharedTaskFragment();

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("notAlertDialog", true);

                    makeSharedTaskFragment.setArguments(bundle);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("dialog");
                    if (fragment != null) {
                        ft.remove(fragment);
                    }
                    ft.addToBackStack(null);

                    makeSharedTaskFragment.show(ft, "dialog");
                });
                break;
        }
    }

    // Failed attempt at changing the background of the profile fragment
//    private ProfileFragment getProfileFragment() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        return (ProfileFragment) fragmentManager.findFragmentById(R.id.frameLayout);
//    }

    // Failed attempt at changing the background of the profile fragment
//    private void updateProfileViewBackground(int colour) {
//        profileViewBackgroundColour = colour;
//        ProfileFragment profileFragment = getProfileFragment();
//        if (profileFragment != null) {
//                profileFragment.updateProfileViewBackground(profileViewBackgroundColour);
//        }
//    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onFinishEditDialog(String snackbarMessage) {
        // Display a snackbar message after attempting to create a task
        runOnUiThread(() -> {
            fetchTasks();
            Snackbar snackbar = Snackbar.make(binding.getRoot(), snackbarMessage, Snackbar.LENGTH_LONG);
            snackbar.show();
        });
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
            case R.id.search:
                if (searchBar.getVisibility() == View.GONE) {
                    searchBar.setVisibility(View.VISIBLE);
                } else { searchBar.setVisibility(View.GONE); }
                break;
            case R.id.purpleTheme:
                currentThemeId = R.style.Theme_MyDay;
                toolbarBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_purple);
                searchBarColour = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                // Failed attempt at changing the background of the profile fragment
//                profileViewBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_purple);
//                updateProfileViewBackground(profileViewBackgroundColour);
                recreate();
                break;
            case R.id.blueTheme:
                currentThemeId = R.style.Theme_Blue;
                toolbarBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_blue);
                searchBarColour = ContextCompat.getColor(getApplicationContext(), R.color.green);
                // Failed attempt at changing the background of the profile fragment
//                profileViewBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_blue);
//                updateProfileViewBackground(profileViewBackgroundColour);
                recreate();
                break;
            case R.id.yellowTheme:
                currentThemeId = R.style.Theme_Yellow;
                toolbarBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_yellow);
                searchBarColour = ContextCompat.getColor(getApplicationContext(), R.color.purple);
                // Failed attempt at changing the background of the profile fragment
//                profileViewBackgroundColour = ContextCompat.getColor(getApplicationContext(), R.color.light_yellow);
//                updateProfileViewBackground(profileViewBackgroundColour);
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Save the state of the activity after the orientation changes or activity gets recreated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentThemeId", currentThemeId);
        outState.putInt("toolbarBackgroundColour", toolbarBackgroundColour);
        outState.putInt("strokeColour", searchBarColour);
        // Failed attempt at changing the background of the profile fragment
//        outState.putInt("profileViewBackgroundColour", profileViewBackgroundColour);
        outState.putParcelableArrayList("tasks", tasks);
    }

    // Restore the instanceState of the activity
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tasks = savedInstanceState.getParcelableArrayList("tasks");
        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);

        taskRecyclerAdapter.setTasks(tasks);
        taskRecyclerAdapter.notifyDataSetChanged();

        SwipeAction swipeAction = new SwipeAction(taskRecyclerAdapter, taskRecyclerView);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAction);
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);
    }

    @Override
    public void onTaskLongClick(Task task) {
        // Obtain user's ID
        String userId = Amplify.Auth.getCurrentUser().getUserId();

        // Set up the edit task dialog fragment
        EditTaskFragment editTaskFragment = new EditTaskFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        bundle.putString("taskName", task.getName());
        bundle.putString("taskDescription", task.getDescription());
        bundle.putString("taskTime", task.getTime());
        bundle.putString("taskPriority", task.getPriority());
        bundle.putString("taskID", task.getId());
        bundle.putString("taskCompleted", task.getCompleted());
        bundle.putString("userID", userId);
        editTaskFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        editTaskFragment.show(ft, "dialog");
    }
}
