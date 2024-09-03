package com.example.myday.Classes;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myday.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class UserListAdapter extends BaseAdapter {
    private final Context context;
    private final List<User> users;
    private final SparseBooleanArray selectedUsers;

    public UserListAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        this.selectedUsers = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_user, parent, false);
        }

        User user = users.get(position);
        TextView userName = convertView.findViewById(R.id.userTextView);
        MaterialCheckBox checkBox = convertView.findViewById(R.id.userCheckBox);

        userName.setText(user.getName());
        checkBox.setChecked(selectedUsers.get(position));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> selectedUsers.put(position, isChecked));
        return convertView;
    }

    public SparseBooleanArray getSelectedUsers() {
        return selectedUsers;
    }
}
