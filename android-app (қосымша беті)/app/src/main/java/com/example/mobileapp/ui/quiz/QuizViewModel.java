package com.example.mobileapp.ui.quiz;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.data.model.Question;
import com.example.mobileapp.data.model.Quiz;
import com.example.mobileapp.data.model.QuizAnswerSubmission;
import com.example.mobileapp.data.model.QuizResultResponse;
import com.example.mobileapp.data.repository.AppRepository;

import java.util.HashMap;
import java.util.Map;

public class QuizViewModel extends AndroidViewModel {

    private final AppRepository repository;
    private final int lessonId;

    private final LiveData<Quiz> quiz;
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isQuizFinished = new MutableLiveData<>(false);

    private final Map<Integer, Integer> userAnswers = new HashMap<>();
    private LiveData<QuizResultResponse> quizResult;

    public QuizViewModel(@NonNull Application application, int lessonId) {
        super(application);
        this.repository = new AppRepository(application);
        this.lessonId = lessonId;
        this.quiz = repository.getQuizByLessonId(lessonId);
    }

    public LiveData<Quiz> getQuiz() {
        return quiz;
    }

    public LiveData<Question> getCurrentQuestion() {
        return Transformations.switchMap(quiz, q -> Transformations.map(currentQuestionIndex, index -> {
            if (q != null && q.getQuestions() != null && index < q.getQuestions().size()) {
                return q.getQuestions().get(index);
            }
            return null;
        }));
    }

    public LiveData<Boolean> isQuizFinished() {
        return isQuizFinished;
    }

    public void selectAnswer(int questionId, int answerId) {
        userAnswers.put(questionId, answerId);
    }

    public void nextQuestion() {
        Integer currentIndex = currentQuestionIndex.getValue();
        if (currentIndex != null && quiz.getValue() != null && quiz.getValue().getQuestions() != null) {
            int nextIndex = currentIndex + 1;
            if (nextIndex < quiz.getValue().getQuestions().size()) {
                currentQuestionIndex.setValue(nextIndex);
            } else {
                // Last question answered, ready to submit
                submitQuiz();
            }
        }
    }

    private void submitQuiz() {
        QuizAnswerSubmission submission = new QuizAnswerSubmission(lessonId, userAnswers);
        quizResult = repository.submitQuiz(submission);
        isQuizFinished.setValue(true);
    }

    public LiveData<QuizResultResponse> getQuizResult() {
        return quizResult;
    }
    
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final int lessonId;

        public Factory(@NonNull Application application, int lessonId) {
            this.application = application;
            this.lessonId = lessonId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new QuizViewModel(application, lessonId);
        }
    }
}
