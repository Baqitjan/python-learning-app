package com.example.mobileapp.data.local.converters;

import androidx.room.TypeConverter;

import com.example.mobileapp.data.model.Question;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class QuestionListConverter {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static List<Question> stringToQuestionList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Question>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String questionListToString(List<Question> questions) {
        return gson.toJson(questions);
    }
}