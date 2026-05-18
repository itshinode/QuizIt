package com.apexcore.quizit;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.Locale;

/**
 * StudyActivity handles the review session for a subject.
 * Features unified navigation, score tracking, and smooth animations.
 */
public class StudyActivity extends AppCompatActivity {
    private static final String KEY_INDEX = "current_index";
    private static final String KEY_SCORE = "correct_count";
    private static final String KEY_REVEALED = "is_revealed";
    private static final String KEY_SELECTED_KEY = "selected_mcq_key";

    private Deck currentDeck;
    private int currentIndex = 0;
    private int correctCount = 0;
    private boolean isRevealed = false;
    private String selectedMCQKey = null;

    private TextView tvDisplay, tvProgressCount;
    private ProgressBar studyProgress;
    private MaterialCardView cardQuestion;
    private MaterialButton btnA, btnB, btnC, btnD;
    private MaterialButton btnCorrect, btnWrong, btnNextCard;
    private LinearLayout layoutMCQ, layoutManualReveal;
    private View layoutStudyContent, layoutSummary;
    private TextView tvFinalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_INDEX);
            correctCount = savedInstanceState.getInt(KEY_SCORE);
            isRevealed = savedInstanceState.getBoolean(KEY_REVEALED);
            selectedMCQKey = savedInstanceState.getString(KEY_SELECTED_KEY);
        }

        initializeUI();
        loadSessionData();
        setupListeners();
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, currentIndex);
        outState.putInt(KEY_SCORE, correctCount);
        outState.putBoolean(KEY_REVEALED, isRevealed);
        outState.putString(KEY_SELECTED_KEY, selectedMCQKey);
    }

    private void initializeUI() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        tvDisplay = findViewById(R.id.tvDisplay);
        tvProgressCount = findViewById(R.id.tvProgressCount);
        studyProgress = findViewById(R.id.studyProgress);
        cardQuestion = findViewById(R.id.cardQuestion);
        
        btnA = findViewById(R.id.btnOptionA);
        btnB = findViewById(R.id.btnOptionB);
        btnC = findViewById(R.id.btnOptionC);
        btnD = findViewById(R.id.btnOptionD);
        btnCorrect = findViewById(R.id.btnCorrect);
        btnWrong = findViewById(R.id.btnWrong);
        btnNextCard = findViewById(R.id.btnNextCard);
        
        layoutMCQ = findViewById(R.id.layoutMCQ);
        layoutManualReveal = findViewById(R.id.layoutManualReveal);
        layoutStudyContent = findViewById(R.id.layoutStudyContent);
        layoutSummary = findViewById(R.id.layoutSummary);
        tvFinalScore = findViewById(R.id.tvFinalScore);

        findViewById(R.id.btnFinishStudy).setOnClickListener(v -> finish());
    }

    private void loadSessionData() {
        int deckIndex = getIntent().getIntExtra("deck_index", -1);
        DataManager.ensureDecksLoaded(this);

        if (deckIndex >= 0 && deckIndex < DataManager.allDecks.size()) {
            currentDeck = DataManager.allDecks.get(deckIndex);
            if (currentDeck == null || currentDeck.getCards().isEmpty()) {
                finish();
                return;
            }
            studyProgress.setMax(currentDeck.getCards().size());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(currentDeck.getDeckName());
            }
        } else {
            finish();
        }
    }

    private void setupListeners() {
        btnA.setOnClickListener(v -> handleMCQSelection("A", btnA));
        btnB.setOnClickListener(v -> handleMCQSelection("B", btnB));
        btnC.setOnClickListener(v -> handleMCQSelection("C", btnC));
        btnD.setOnClickListener(v -> handleMCQSelection("D", btnD));

        btnCorrect.setOnClickListener(v -> {
            correctCount++;
            moveToNext();
        });
        btnWrong.setOnClickListener(v -> moveToNext());
        btnNextCard.setOnClickListener(v -> moveToNext());
    }

    private void updateUI() {
        if (currentDeck == null || currentIndex >= currentDeck.getCards().size()) {
            showSummary();
            return;
        }

        Flashcard card = currentDeck.getCards().get(currentIndex);
        studyProgress.setProgress(currentIndex + 1);
        tvProgressCount.setText(String.format(Locale.getDefault(), "%d/%d", currentIndex + 1, currentDeck.getCards().size()));

        layoutMCQ.setVisibility(View.GONE);
        layoutManualReveal.setVisibility(View.GONE);
        btnNextCard.setVisibility(View.GONE);
        resetMCQButtons();

        if (card.isMCQ()) {
            layoutMCQ.setVisibility(View.VISIBLE);
            tvDisplay.setOnClickListener(null);
            tvDisplay.setText(card.getQuestion());
            setupMCQOptions(card);
            if (isRevealed) restoreMCQFeedback(card);
        } else {
            if (isRevealed) {
                tvDisplay.setText(card.getAnswer());
                layoutManualReveal.setVisibility(View.VISIBLE);
            } else {
                tvDisplay.setText(card.getQuestion());
                tvDisplay.setOnClickListener(v -> revealManualAnswer(card.getAnswer()));
            }
        }
    }

    private void revealManualAnswer(String answer) {
        isRevealed = true;
        tvDisplay.setOnClickListener(null);
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(150);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                tvDisplay.setText(answer);
                Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(150);
                tvDisplay.startAnimation(fadeIn);
                animateIn(layoutManualReveal);
            }
        });
        tvDisplay.startAnimation(fadeOut);
    }

    private void setupMCQOptions(Flashcard card) {
        setMCQButtonsEnabled(true);
        updateOptionButton(btnA, card, 0);
        updateOptionButton(btnB, card, 1);
        updateOptionButton(btnC, card, 2);
        updateOptionButton(btnD, card, 3);
    }

    private void updateOptionButton(MaterialButton btn, Flashcard card, int index) {
        if (card.getOptions() != null && card.getOptions().size() > index) {
            btn.setText(card.getOptions().get(index));
            btn.setVisibility(View.VISIBLE);
        } else {
            btn.setVisibility(View.GONE);
        }
    }

    private void handleMCQSelection(String selection, MaterialButton selectedButton) {
        if (isRevealed) return;
        Flashcard card = currentDeck.getCards().get(currentIndex);
        isRevealed = true;
        selectedMCQKey = selection;
        setMCQButtonsEnabled(false);

        if (selection.equals(card.getAnswer())) {
            correctCount++;
            styleButtonCorrect(selectedButton);
        } else {
            styleButtonWrong(selectedButton);
            highlightCorrectOption(card.getAnswer());
        }
        animateIn(btnNextCard);
    }

    private void restoreMCQFeedback(Flashcard card) {
        setMCQButtonsEnabled(false);
        highlightCorrectOption(card.getAnswer());
        if (selectedMCQKey != null && !selectedMCQKey.equals(card.getAnswer())) {
            MaterialButton wrongBtn = getButtonByKey(selectedMCQKey);
            if (wrongBtn != null) styleButtonWrong(wrongBtn);
        }
        btnNextCard.setVisibility(View.VISIBLE);
    }

    private void styleButtonCorrect(MaterialButton btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.successLight)));
        btn.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.successGreen)));
        btn.setTextColor(ContextCompat.getColor(this, R.color.successGreen));
    }

    private void styleButtonWrong(MaterialButton btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.errorLight)));
        btn.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.errorRed)));
        btn.setTextColor(ContextCompat.getColor(this, R.color.errorRed));
    }

    private void highlightCorrectOption(String key) {
        MaterialButton btn = getButtonByKey(key);
        if (btn != null) styleButtonCorrect(btn);
    }

    private MaterialButton getButtonByKey(String key) {
        switch (key) {
            case "A": return btnA;
            case "B": return btnB;
            case "C": return btnC;
            case "D": return btnD;
            default: return null;
        }
    }

    private void resetMCQButtons() {
        ColorStateList normalStroke = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.option_unselected_stroke));
        ColorStateList normalText = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.textPrimary));
        ColorStateList normalBg = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));
        MaterialButton[] buttons = {btnA, btnB, btnC, btnD};
        for (MaterialButton b : buttons) {
            b.setBackgroundTintList(normalBg);
            b.setStrokeColor(normalStroke);
            b.setTextColor(normalText);
        }
    }

    private void setMCQButtonsEnabled(boolean enabled) {
        btnA.setEnabled(enabled); btnB.setEnabled(enabled); btnC.setEnabled(enabled); btnD.setEnabled(enabled);
    }

    private void animateIn(View view) {
        view.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(300);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(fadeIn);
    }

    private void moveToNext() {
        currentIndex++;
        isRevealed = false;
        selectedMCQKey = null;
        if (currentIndex < currentDeck.getCards().size()) {
            updateUI();
        } else {
            showSummary();
        }
    }

    private void showSummary() {
        layoutStudyContent.setVisibility(View.GONE);
        layoutSummary.setVisibility(View.VISIBLE);
        int total = currentDeck.getCards().size();
        int percent = (total > 0) ? (int) (((float) correctCount / total) * 100) : 0;
        tvFinalScore.setText(String.format(Locale.getDefault(), getString(R.string.final_score_format), correctCount, total, percent));
    }
}
