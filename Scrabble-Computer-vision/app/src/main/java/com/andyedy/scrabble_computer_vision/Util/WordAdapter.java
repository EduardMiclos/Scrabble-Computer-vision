package com.andyedy.scrabble_computer_vision.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andyedy.scrabble_computer_vision.R;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder>{

    private List<String> words;
    private LayoutInflater inflater;

    public WordAdapter(Context context, List<String> wordList) {

        words = wordList;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView wordTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            wordTextView = (TextView) itemView.findViewById(R.id.word_text_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View wordView = inflater.inflate(R.layout.word_list_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(wordView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String word = words.get(position);
        holder.wordTextView.setText(word);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
