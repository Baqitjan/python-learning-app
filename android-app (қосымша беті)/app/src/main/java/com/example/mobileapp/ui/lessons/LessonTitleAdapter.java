package com.example.mobileapp.ui.lessons;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class LessonTitleAdapter extends RecyclerView.Adapter<LessonTitleAdapter.ViewHolder> {

    private List<String> lessonTitles = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(lessonTitles.get(position));

        // Set background color from the array
        TypedArray colors = holder.itemView.getContext().getResources().obtainTypedArray(R.array.card_colors);
        int color = colors.getColor(position % colors.length(), 0);
        ((MaterialCardView) holder.itemView).setCardBackgroundColor(color);
        colors.recycle();
    }

    @Override
    public int getItemCount() {
        return lessonTitles.size();
    }

    public void setLessonTitles(List<String> titles) {
        this.lessonTitles = titles;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.item_title);
        }
    }
}
