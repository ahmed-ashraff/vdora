package com.example.vsm.processor;


import org.tartarus.snowball.ext.PorterStemmer;

import java.util.*;

public class TextProcessor {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "has", "he",
        "in", "is", "it", "its", "of", "on", "that", "the", "to", "was", "were",
        "will", "with"
    ));

    private final PorterStemmer stemmer;

    public TextProcessor() {
        this.stemmer = new PorterStemmer();
    }

    public List<String> process(String text) {
        String[] words = text.toLowerCase().split("\\W+");
        List<String> tokens = new ArrayList<>();

        for (String word : words) {
            if (word.isEmpty() || STOP_WORDS.contains(word)) {
                continue;
            }

            stemmer.setCurrent(word);
            stemmer.stem();
            tokens.add(stemmer.getCurrent());
        }

        return tokens;
    }
} 