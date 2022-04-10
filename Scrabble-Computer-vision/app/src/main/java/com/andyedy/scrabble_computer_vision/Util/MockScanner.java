package com.andyedy.scrabble_computer_vision.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockScanner implements LetterScanner{
    @Override
    public List<Letter> getLetters() {
        List<Letter> letters = new ArrayList<>();

        letters.add(new Letter("A", 1));
        letters.add(new Letter("D", 2));
        letters.add(new Letter("E", 1));
        letters.add(new Letter("U", 1));
        letters.add(new Letter("R", 1));

        Collections.sort(letters);
        return letters;
    }
}
