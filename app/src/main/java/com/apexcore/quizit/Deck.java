package com.apexcore.quizit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Deck implements Serializable {
    private String deckName;
    private List<Flashcard> cards;

    public Deck(String deckName) {
        this.deckName = deckName;
        this.cards = new ArrayList<>();
    }

    public String getDeckName() { return deckName; }
    public List<Flashcard> getCards() { return cards; }
    public void addCard(Flashcard card) { this.cards.add(card); }
}