package com.example.myday.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.User;
import com.amplifyframework.datastore.generated.model.UserSharedTask;
import com.example.myday.Activities.HomeActivity;
import com.example.myday.Classes.SharedSwipeAction;
import com.example.myday.Classes.SharedTask;
import com.example.myday.Classes.SharedTaskRecyclerAdapter;
import com.example.myday.Classes.SwipeAction;
import com.example.myday.Classes.Task;
import com.example.myday.Classes.TaskRecyclerAdapter;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SharedTasksFragment extends Fragment implements SharedTaskRecyclerAdapter.OnTaskLongClickListener{

    View rootView;
    static ArrayList<SharedTask> sharedTasks = new ArrayList<>();
    SharedTaskRecyclerAdapter taskRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shared_tasks, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            sharedTasks = savedInstanceState.getParcelableArrayList("sharedTasks");
        }

        // Obtain the required views
        rootView = view.findViewById(R.id.constraintLayout);

        // Hide the floating action button
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if (homeActivity != null) {
            homeActivity.setAddTaskButtonVisibility(View.VISIBLE);
            // Set an on click listener for the floating action button
            homeActivity.setAddButtonOnClickListener("sharedTasks");

            // Create a reference to the recycler view of tasks
            RecyclerView sharedTasksRecyclerView = view.findViewById(R.id.sharedTasksRecyclerView);

            if (sharedTasksRecyclerView != null) {
                // Attach the recycler adapter to the recycler view
                taskRecyclerAdapter = new SharedTaskRecyclerAdapter(sharedTasks);
                taskRecyclerAdapter.setOnTaskLongClickListener(this);
                sharedTasksRecyclerView.setAdapter(taskRecyclerAdapter);

                // Delete the item from the list by a swiping action
                SharedSwipeAction swipeAction = new SharedSwipeAction(taskRecyclerAdapter, sharedTasksRecyclerView);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAction);
                itemTouchHelper.attachToRecyclerView(sharedTasksRecyclerView);
                taskRecyclerAdapter.notifyDataSetChanged();
            }

            // fetch the list of shared tasks from the backend
            fetchSharedTasks();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchSharedTasks() {
        // Get the current user's ID
        String userId = Amplify.Auth.getCurrentUser().getUserId();

        // Attempt to fetch the shared tasks from the database
        Amplify.API.query(
                ModelQuery.get(User.class, userId),
                response -> rootView.post(() -> {
//                    Log.i("sharedTasks", response.getData().getSharedTasks().toString());
                    sharedTasks.clear();
//                    Log.i("sharedTasks", response.getData().toString());
                    for (UserSharedTask sharedTask : response.getData().getSharedTasks()) {
                        SharedTask newTask = new SharedTask(sharedTask.getSharedTask().getId(), sharedTask.getSharedTask()
                                .getName(), sharedTask.getSharedTask().getDescription(),
                                sharedTask.getSharedTask().getTime(), sharedTask.getSharedTask()
                                .getPriority(), sharedTask.getSharedTask().getCompleted());
                        sharedTasks.add(newTask);
                        taskRecyclerAdapter.notifyDataSetChanged();
                    }
                }), error -> rootView.post(() -> {
                    Snackbar snackbar = Snackbar.make(rootView, "There was an error generating tasks", Snackbar.LENGTH_LONG);
                    snackbar.show();
                })
        );
    }

    // Save the state of the activity after the orientation changes or activity gets recreated
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("sharedTasks", sharedTasks);
    }

    @Override
    public void onTaskLongClick(SharedTask task) {
        // Obtain user's ID
        String userId = Amplify.Auth.getCurrentUser().getUserId();

        // Set up the edit task dialog fragment
        EditSharedTaskFragment editTaskFragment = new EditSharedTaskFragment();

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

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag("dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        editTaskFragment.show(ft, "dialog");
    }
}
