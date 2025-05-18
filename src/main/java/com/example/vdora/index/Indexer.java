package com.example.vdora.index;

import com.example.vdora.model.Document;
import com.example.vdora.model.DocumentVector;
import com.example.vdora.processor.TextProcessor;

import java.io.IOException;
import java.util.*;

public class Indexer {
    private final TextProcessor textProcessor;
    private final Map<String, DocumentVector> documentVectors; // docId -> docVector
    private final Map<String, Set<String>> invertedIndex; // term -> docIds
    private final Map<String, Double> idfScores; // term -> idfScore

    public Indexer() {
        this.textProcessor = new TextProcessor();
        this.documentVectors = new HashMap<>();
        this.invertedIndex = new HashMap<>();
        this.idfScores = new HashMap<>();
    }

    public void indexDocument(Document document) throws IOException {
        String content = document.getContent();
        List<String> processedTerms = textProcessor.process(content);
        
        DocumentVector vector = DocumentVector.fromTerms(processedTerms);
        documentVectors.put(document.id(), vector);

        for (String term : processedTerms) {
            invertedIndex.computeIfAbsent(term, k -> new HashSet<>()).add(document.id());
        }
    }

    public void calculateIdf() {
        int totalDocuments = documentVectors.size();

        for (var entry : invertedIndex.entrySet()) {
            String term = entry.getKey();
            int documentFrequency = entry.getValue().size();
            double idf = Math.log((double) totalDocuments / documentFrequency);
            idfScores.put(term, idf);
        }

        // Apply IDF to document vectors
        for (var vector : documentVectors.values()) {
            for (String term : vector.getTermWeights().keySet()) {
                double tf = vector.getTermWeight(term);
                double idf = idfScores.getOrDefault(term, 0.0);
                vector.addTerm(term, tf * idf);
            }
        }
    }

    public Map<String, DocumentVector> getDocumentVectors() {
        return documentVectors;
    }
} 