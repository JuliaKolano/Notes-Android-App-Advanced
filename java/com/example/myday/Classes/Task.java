package com.example.myday.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
public class Task implements Parcelable {

    //global variables
    private final String id;
    private final String name;
    private final String description;
    private final String time;
    private final String priority;
    private String completed;

    //constructor method
    public Task(String id, String name, String description, String time, String priority, String completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.time = time;
        this.priority = priority;
        this.completed = completed;
    }

    //getter methods
    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() { return time; }

    public String getPriority() { return priority; }

    public String getCompleted() { return completed; }

    // setter method
    public void setCompleted(String completed) {
        this.completed = completed;
    }

    protected Task(Parcel in) {
        id = in.readString();
       name = in.readString();
       description = in.readString();
       time = in.readString();
       priority = in.readString();
       completed = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(time);
        dest.writeString(priority);
        dest.writeString(completed);
    }
}
