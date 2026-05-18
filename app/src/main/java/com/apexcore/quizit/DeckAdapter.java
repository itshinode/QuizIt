package com.apexcore.quizit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Optimized RecyclerView Adapter for displaying subjects (decks).
 * Uses a listener interface for safe interaction handling.
 */
public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private final List<Deck> decks;
    private final OnDeckClickListener listener;

    public interface OnDeckClickListener {
        void onDeckClick(int position);
        void onDeckLongClick(int position);
    }

    public DeckAdapter(List<Deck> decks, OnDeckClickListener listener) {
        this.decks = decks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        Deck deck = decks.get(position);
        if (deck != null) {
            holder.tvName.setText(deck.getDeckName());
            
            int count = deck.getCards() != null ? deck.getCards().size() : 0;
            String countText = count + (count == 1 ? " CARD" : " CARDS");
            holder.tvCount.setText(countText);
            
            // Handle primary click on the card row
            holder.itemView.setOnClickListener(v -> {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeckClick(currentPos);
                }
            });

            // Handle long click for management options
            holder.itemView.setOnLongClickListener(v -> {
                int currentPos = holder.getBindingAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeckLongClick(currentPos);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return (decks != null) ? decks.size() : 0;
    }

    static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvCount;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvDeckName);
            tvCount = itemView.findViewById(R.id.tvCardCount);
            
            // Ensure the item view is clickable to receive touch events from the adapter
            itemView.setClickable(true);
            itemView.setFocusable(true);
        }
    }
}
