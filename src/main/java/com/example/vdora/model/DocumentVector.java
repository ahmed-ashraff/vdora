package com.example.vdora.model;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class DocumentVector {
    private final Map<String, Double> termWeights;
    private double magnitude;

    public DocumentVector() {
        this.termWeights = new HashMap<>();
        this.magnitude = 0.0;
    }

    public void addTerm(String term, double weight) {
        termWeights.put(term, weight);
        magnitude += weight * weight;
    }

    public double getTermWeight(String term) {
        return termWeights.getOrDefault(term, 0.0);
    }

    public double getMagnitude() {
        return Math.sqrt(magnitude);
    }


    public Map<String, Double> getTermWeights() {
        return termWeights;
    }

    public static DocumentVector fromTerms(List<String> terms) {
        DocumentVector vector = new DocumentVector();
        Map<String, Integer> termFrequencies = new HashMap<>();

        // Calculate term frequencies
        for (String term : terms) {
            termFrequencies.merge(term, 1, Integer::sum);
        }

        // Calculate TF weights
        for (var entry : termFrequencies.entrySet()) {
            double tf = 1 + Math.log(entry.getValue());
            vector.addTerm(entry.getKey(), tf);
        }

        return vector;
    }
} 