package com.example.mobileapp.ui.lessons;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.LessonSimpleResponse;

import java.util.ArrayList;
import java.util.List;

public class LessonRefAdapter extends RecyclerView.Adapter<LessonRefAdapter.ViewHolder> {

    private List<LessonSimpleResponse> lessons = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonSimpleResponse lesson = lessons.get(position);
        holder.lessonNumber.setText(String.valueOf(position + 1) + ".");
        holder.lessonTitle.setText(lesson.getTitle());
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void setLessons(List<LessonSimpleResponse> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lessonNumber;
        TextView lessonTitle;

        ViewHolder(View view) {
            super(view);
            lessonNumber = view.findViewById(R.id.lesson_number);
            lessonTitle = view.findViewById(R.id.lesson_title);
            
            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(lessons.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(LessonSimpleResponse lesson);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
