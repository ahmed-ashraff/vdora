package com.example.vdora.search;

import com.example.vdora.model.Document;
import com.example.vdora.model.SearchResult;
import com.example.vdora.model.DocumentVector;
import com.example.vdora.processor.TextProcessor;

import java.util.*;

public class Searcher {
    private final TextProcessor textProcessor;
    private final Map<String, DocumentVector> documentVectors;
    private final Map<String, Document> documents;

    public Searcher(Map<String, DocumentVector> documentVectors, Map<String, Document> documents) {
        this.textProcessor = new TextProcessor();
        this.documentVectors = documentVectors;
        this.documents = documents;
    }

    // Default: return all results
    public List<SearchResult> search(String query) {
        return search(query, documents.size());
    }

    public List<SearchResult> search(String query, int n) {
        var queryTerms = textProcessor.process(query);
        DocumentVector queryVector = DocumentVector.fromTerms(queryTerms);

        // Calculate cosine similarity for each document
        List<SearchResult> results = new ArrayList<>();
        for (var entry : documentVectors.entrySet()) {
            String docId = entry.getKey();
            DocumentVector docVector = entry.getValue();
            double similarity = calculateCosineSimilarity(queryVector, docVector);
            if(similarity == 0) continue;
            results.add(new SearchResult(documents.get(docId), similarity));
        }

        // Sort by similarity score and return top n
        results.sort((a, b) -> Double.compare(b.score(), a.score()));
        return results.subList(0, Math.min(n, results.size()));
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