package com.apexcore.quizit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of Flashcards.
 * Optimized for Gson serialization and state persistence.
 */
public class Deck implements Serializable {
    private String deckName;
    private List<Flashcard> cards;

    /**
     * Default constructor for Gson.
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }

    public Deck(String deckName) {
        this.deckName = deckName;
        this.cards = new ArrayList<>();
    }

    /**
     * Copy constructor for creating a deep copy of a deck.
     */
    public Deck(Deck other) {
        this.deckName = other.deckName;
        this.cards = (other.cards != null) ? new ArrayList<>(other.cards) : new ArrayList<>();
    }

    public String getDeckName() {
        return deckName != null ? deckName : "";
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public List<Flashcard> getCards() {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        return cards;
    }

    public void addCard(Flashcard card) {
        if (card != null) {
            getCards().add(card);
        }
    }
}
