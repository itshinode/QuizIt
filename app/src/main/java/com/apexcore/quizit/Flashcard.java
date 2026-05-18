package com.apexcore.quizit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single flashcard which can be a manual question/answer pair
 * or a multiple-choice question.
 */
public class Flashcard implements Serializable {
    public static final int TYPE_MANUAL = 0;
    public static final int TYPE_MULTIPLE_CHOICE = 2;

    private String question = "";
    private String answer = "";
    private int type = TYPE_MANUAL;
    private List<String> options = new ArrayList<>();

    /**
     * Default constructor for serialization compatibility.
     */
    public Flashcard() {
    }

    public Flashcard(String question, String answer, List<String> options) {
        this.question = question != null ? question : "";
        this.answer = answer != null ? answer : "";
        this.options = (options != null) ? new ArrayList<>(options) : new ArrayList<>();
        this.type = TYPE_MULTIPLE_CHOICE;
    }

    public Flashcard(String question, String answer, int type) {
        this.question = question != null ? question : "";
        this.answer = answer != null ? answer : "";
        this.type = type;
        this.options = new ArrayList<>();
    }

    public String getQuestion() { return question != null ? question : ""; }
    public String getAnswer() { return answer != null ? answer : ""; }
    public int getType() { return type; }
    public List<String> getOptions() { 
        if (options == null) options = new ArrayList<>();
        return options; 
    }

    /**
     * Helper to check if the card is Multiple Choice.
     */
    public boolean isMCQ() {
        return type == TYPE_MULTIPLE_CHOICE;
    }
}
