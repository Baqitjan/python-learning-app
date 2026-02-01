package com.example.mobileapp.ui.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobileapp.R;
import com.example.mobileapp.data.model.Answer;
import com.example.mobileapp.data.model.Question;

import java.util.List;

public class QuizFragment extends Fragment {

    private QuizViewModel viewModel;
    private TextView questionNumberTextView, questionTextView, finalScoreTextView, xpGainedTextView, resultTitleTextView;
    private RadioGroup answersRadioGroup;
    private RadioButton[] answerRadioButtons = new RadioButton[4];
    private Button nextButton, finishQuizButton;
    private LinearLayout quizContainer, resultsContainer;

    private Question currentQuestion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int lessonId = getArguments() != null ? getArguments().getInt("lessonId") : 0;

        QuizViewModel.Factory factory = new QuizViewModel.Factory(requireActivity().getApplication(), lessonId);
        viewModel = new ViewModelProvider(this, factory).get(QuizViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        observeViewModel();

        nextButton.setOnClickListener(v -> handleNextButtonClick());
        finishQuizButton.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void initializeViews(View view) {
        quizContainer = view.findViewById(R.id.quizContainer);
        resultsContainer = view.findViewById(R.id.resultsContainer);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        questionTextView = view.findViewById(R.id.questionTextView);
        answersRadioGroup = view.findViewById(R.id.answersRadioGroup);
        answerRadioButtons[0] = view.findViewById(R.id.answer1RadioButton);
        answerRadioButtons[1] = view.findViewById(R.id.answer2RadioButton);
        answerRadioButtons[2] = view.findViewById(R.id.answer3RadioButton);
        answerRadioButtons[3] = view.findViewById(R.id.answer4RadioButton);
        nextButton = view.findViewById(R.id.nextButton);
        resultTitleTextView = view.findViewById(R.id.resultTitleTextView);
        finalScoreTextView = view.findViewById(R.id.finalScoreTextView);
        xpGainedTextView = view.findViewById(R.id.xpGainedTextView);
        finishQuizButton = view.findViewById(R.id.finishQuizButton);
    }

    private void observeViewModel() {
        viewModel.getQuiz().observe(getViewLifecycleOwner(), quiz -> {
            if (quiz == null || quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
                Toast.makeText(getContext(), "Failed to load quiz.", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }
        });

        viewModel.getCurrentQuestion().observe(getViewLifecycleOwner(), question -> {
            if (question != null) {
                this.currentQuestion = question;
                updateQuestionUI();
            }
        });

        viewModel.isQuizFinished().observe(getViewLifecycleOwner(), isFinished -> {
            if (isFinished != null && isFinished) {
                showResults();
            }
        });
    }

    private void updateQuestionUI() {
        answersRadioGroup.clearCheck();
        questionTextView.setText(currentQuestion.getText());
        List<Answer> answers = currentQuestion.getAnswers();

        for (int i = 0; i < answerRadioButtons.length; i++) {
            if (i < answers.size()) {
                answerRadioButtons[i].setVisibility(View.VISIBLE);
                answerRadioButtons[i].setText(answers.get(i).getText());
                answerRadioButtons[i].setTag(answers.get(i).getId());
            } else {
                answerRadioButtons[i].setVisibility(View.GONE);
            }
        }
        
        // Update question number text
        int currentIndex = viewModel.getQuiz().getValue().getQuestions().indexOf(currentQuestion);
        int totalQuestions = viewModel.getQuiz().getValue().getQuestions().size();
        questionNumberTextView.setText("Question " + (currentIndex + 1) + "/" + totalQuestions);
        
        // Change button text to "Submit" on the last question
        if (currentIndex == totalQuestions - 1) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText("Next");
        }
    }

    private void handleNextButtonClick() {
        int selectedRadioButtonId = answersRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = getView().findViewById(selectedRadioButtonId);
        int answerId = (int) selectedRadioButton.getTag();
        viewModel.selectAnswer(currentQuestion.getId(), answerId);

        viewModel.nextQuestion();
    }

    private void showResults() {
        quizContainer.setVisibility(View.GONE);
        resultsContainer.setVisibility(View.VISIBLE);

        viewModel.getQuizResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                resultTitleTextView.setText(result.isPassed() ? "Congratulations!" : "Keep Trying!");
                finalScoreTextView.setText("Your score: " + result.getCorrectCount() + "/" + result.getTotalQuestions());
                xpGainedTextView.setText("+" + result.getXpGained() + " XP");
            }
        });
    }
}
