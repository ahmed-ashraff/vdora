package com.example.vdora.search;

import com.example.vdora.model.Document;
import com.example.vdora.model.SearchResult;
import com.example.vdora.model.DocumentVector;
import com.example.vdora.processor.TextProcessor;

import java.util.*;
import java.util.function.BiFunction;


public class Searcher {
    private final TextProcessor textProcessor;
    private final Map<String, DocumentVector> documentVectors;
    private final Map<String, Document> documents;

    public Searcher(Map<String, DocumentVector> documentVectors, Map<String, Document> documents) {
        this.textProcessor = new TextProcessor();
        this.documentVectors = documentVectors;
        this.documents = documents;
    }

    // Default: return all relevant results
    public List<SearchResult> search(String query, int choice) {
        return search(query, documents.size(), choice);
    }

    public List<SearchResult> search(String query, int n, int choice) {
        List<String> queryTerms = textProcessor.process(query);
        DocumentVector queryVector = DocumentVector.fromTerms(queryTerms);

        BiFunction<DocumentVector, DocumentVector, Double> similarityFunction;
        switch (choice) {
            case 1 -> similarityFunction = this::calculateCosineSimilarity;
            case 2 -> similarityFunction = this::calculateJacquardSimilarity;
            default -> throw new IllegalArgumentException("Invalid similarity choice: " + choice);
        }

        List<SearchResult> results = new ArrayList<>();
        for (var entry : documentVectors.entrySet()) {
            String docId = entry.getKey();
            DocumentVector docVector = entry.getValue();

            double similarity = similarityFunction.apply(queryVector, docVector);

            if (similarity == 0) continue;
            results.add(new SearchResult(documents.get(docId), similarity));
        }

        // Sort by similarity score and return top n
        results.sort((a, b) -> Double.compare(b.score(), a.score()));
        return results.subList(0, Math.min(n, results.size()));
    }

    private double calculateJacquardSimilarity(DocumentVector queryVector, DocumentVector docVector) {
        Set<String> queryTerms = queryVector.getTermWeights().keySet();
        Set<String> docTerms = docVector.getTermWeights().keySet();

        Set<String> intersection = new HashSet<>(queryTerms);
        intersection.retainAll(docTerms);

        Set<String> union = new HashSet<>(queryTerms);
        union.addAll(docTerms);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }


    private double calculateCosineSimilarity(DocumentVector queryVector, DocumentVector docVector) {
        double dotProduct = 0.0;
        for (String term : queryVector.getTermWeights().keySet()) {
            dotProduct += queryVector.getTermWeight(term) * docVector.getTermWeight(term);
        }

        double queryMagnitude = queryVector.getMagnitude();
        double docMagnitude = docVector.getMagnitude();

        if (queryMagnitude == 0 || docMagnitude == 0) {
            return 0.0;
        }

        return dotProduct / (queryMagnitude * docMagnitude);
    }
} 