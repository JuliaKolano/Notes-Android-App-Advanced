package com.example.myday.Classes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.example.myday.R;
import com.google.android.material.snackbar.Snackbar;

public class SharedSwipeAction extends ItemTouchHelper.Callback {

    private final SharedTaskRecyclerAdapter adapter;
    private final RecyclerView recyclerView;

    public SharedSwipeAction(SharedTaskRecyclerAdapter adapter, RecyclerView recyclerView) {
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        Snackbar snackbar = Snackbar.make(recyclerView, "Task not permanently deleted", Snackbar.LENGTH_LONG);
        snackbar.show();

        // Removed this functionality since it breaks the database model
        // Get position of swiped item
//        int position = viewHolder.getLayoutPosition();

        //Remove item from the dataset
//        SharedTask taskToDelete = adapter.getTaskAtPosition(position);

//        if (taskToDelete != null) {
//            // Delete the task from the array list
//            adapter.removeTask(position);
//
//            // Attempt to delete the task from the database
//            String userId = Amplify.Auth.getCurrentUser().getUserId();
//            Amplify.API.mutate(ModelMutation.delete(
//                            com.amplifyframework.datastore.generated.model.SharedTask.builder().name(taskToDelete.getName())
//                                    .description(taskToDelete.getDescription()).time(taskToDelete.getTime()).priority(taskToDelete.getPriority())
//                                    .completed(taskToDelete.getCompleted()).id(taskToDelete.getId()).build()),
//                    response -> {
//                        // Show Snackbar with an undo option
//                        Snackbar snackbar = Snackbar.make(recyclerView, R.string.snackbarDeleteDone, Snackbar.LENGTH_LONG);
//                        snackbar.setAction("UNDO", v -> restoreTask(taskToDelete, position, userId));
//                        snackbar.show();
//                    }, error -> {
//                        Snackbar snackbar = Snackbar.make(recyclerView, "Error deleting task", Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                    });
//        }
//    }
//
//    private void restoreTask(SharedTask taskToDelete, int position, String userId) {
//        adapter.restoreDeletedTask(taskToDelete, position);
//        recyclerView.scrollToPosition(position);
//
//        // Attempt to create the task again in the database
//        Amplify.API.mutate(ModelMutation.create(
//                com.amplifyframework.datastore.generated.model.SharedTask.builder().name(taskToDelete.getName())
//                        .description(taskToDelete.getDescription()).time(taskToDelete.getTime()).priority(taskToDelete.getPriority())
//                        .completed(taskToDelete.getCompleted()).id(taskToDelete.getId()).build()
//        ), response -> {
//            Snackbar snackbar = Snackbar.make(recyclerView, "Task restored", Snackbar.LENGTH_LONG);
//            snackbar.show();
//        }, error -> {
//            Snackbar snackbar = Snackbar.make(recyclerView, "Couldn't restore task", Snackbar.LENGTH_LONG);
//            snackbar.show();
//        });
    }
}