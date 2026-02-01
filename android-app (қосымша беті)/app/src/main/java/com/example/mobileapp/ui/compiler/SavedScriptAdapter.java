package com.example.mobileapp.ui.compiler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.SavedScript;

import java.util.List;

public class SavedScriptAdapter extends ArrayAdapter<SavedScript> {

    private final OnScriptInteractionListener listener;

    public interface OnScriptInteractionListener {
        void onScriptClicked(SavedScript script);
        void onDeleteClicked(SavedScript script);
    }

    public SavedScriptAdapter(@NonNull Context context, @NonNull List<SavedScript> scripts, OnScriptInteractionListener listener) {
        super(context, 0, scripts);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_saved_script, parent, false);
        }

        SavedScript script = getItem(position);

        TextView scriptNameTextView = convertView.findViewById(R.id.scriptNameTextView);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        if (script != null) {
            scriptNameTextView.setText(script.getName());

            convertView.setOnClickListener(v -> listener.onScriptClicked(script));
            deleteButton.setOnClickListener(v -> listener.onDeleteClicked(script));
        }

        return convertView;
    }
}