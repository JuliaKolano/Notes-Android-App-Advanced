package com.example.myday.Classes;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.example.myday.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Objects;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder> {

    private ArrayList<Task> tasks;
    private OnTaskLongClickListener longClickListener;

    public TaskRecyclerAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setOnTaskLongClickListener(OnTaskLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    // Set the task list to include the tasks from the filtered task list
    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredTasks(ArrayList<Task> filteredTasks) {
        this.tasks = filteredTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_task, parent, false);
        return new TaskViewHolder(view, tasks);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(ArrayList<Task> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }

    public Task getTaskAtPosition(int position) {
        return tasks.get(position);
    }

    public void removeTask(int position) {
        if (position != -1) {
            tasks.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void restoreDeletedTask(Task task, int position) {
        tasks.add(position, task);
        notifyItemInserted(position);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView card;
        private final TextView taskNameView;
        private final TextView descriptionView;
        private final TextView timeView;
        private final TextView priorityView;
        private final ArrayList<Task> tasks;

        public TaskViewHolder(@NonNull View itemView, ArrayList<Task> tasks) {
            super(itemView);
            card = itemView.findViewById(R.id.taskCardView);
            taskNameView = itemView.findViewById(R.id.taskNameView);
            descriptionView = itemView.findViewById(R.id.descriptionView);
            timeView = itemView.findViewById(R.id.timeView);
            priorityView = itemView.findViewById(R.id.priorityView);
            this.tasks = tasks;
        }

        public void bind(Task task) {
            taskNameView.setText(task.getName());
            descriptionView.setText(task.getDescription());
            timeView.setText(String.format("Time: %s", task.getTime()));
            priorityView.setText(String.format("Priority: %s", task.getPriority()));

            // Set checked status and handle click events
            card.setChecked(Objects.equals(task.getCompleted(), "true"));

            // Allow the task to be checked or unchecked
            card.setOnClickListener(v -> {
                // get user's id
                String userId = Amplify.Auth.getCurrentUser().getUserId();

                boolean isChecked = !card.isChecked();
                card.setChecked(isChecked);
                task.setCompleted(isChecked ? "true": "false");
                // Notify the array adapter of the isChecked change
                tasks.set(getAbsoluteAdapterPosition(), task);
                notifyItemChanged(getAbsoluteAdapterPosition());

                // Update the completed status of the task
                Amplify.API.mutate(ModelMutation.update(
                        com.amplifyframework.datastore.generated.model.Task.builder().userId(userId).name(task.getName())
                                .description(task.getDescription()).time(task.getTime()).priority(task.getPriority())
                                .completed(task.getCompleted()).id(task.getId()).build()),
                response -> {}, error -> {});

            });

            // Allow the user to edit their tasks when long pressing on the task card
            card.setOnLongClickListener(v -> {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                    longClickListener.onTaskLongClick(tasks.get(position));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }
}

