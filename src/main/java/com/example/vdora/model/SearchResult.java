package com.example.vdora.model;

public record SearchResult(Document document, double score) {

    @Override
    public String toString() {
        return String.format("Document: %s, Score: %.4f", document.getName(), score);
    }
} 