package com.example.mobileapp.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Lesson;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_list_item, parent, false);
        return new LessonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson currentLesson = lessons.get(position);
        holder.lessonTitleTextView.setText(currentLesson.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(currentLesson.getImageUrl())
                .placeholder(R.drawable.ic_placeholder) // Show this while loading
                .error(R.drawable.ic_placeholder)     // Show this if loading fails
                .into(holder.lessonImageView);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    class LessonViewHolder extends RecyclerView.ViewHolder {
        private final TextView lessonTitleTextView;
        private final ImageView lessonImageView;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonTitleTextView = itemView.findViewById(R.id.lessonTitleTextView);
            lessonImageView = itemView.findViewById(R.id.lessonImageView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(lessons.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Lesson lesson);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}