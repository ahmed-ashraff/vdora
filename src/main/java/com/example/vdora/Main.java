package com.example.vdora;

import java.util.Scanner;
import com.example.vdora.index.Indexer;
import com.example.vdora.model.Document;
import com.example.vdora.model.SearchResult;
import com.example.vdora.search.Searcher;
import io.github.cdimascio.dotenv.Dotenv;
import com.example.vdora.loader.DocumentLoader;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String documentsPath = dotenv.get("DOCUMENTS_PATH");

        try (DocumentLoader loader = new DocumentLoader(documentsPath)) {
            Map<String, Document> documents = loader.loadDocuments();

            Indexer indexer = new Indexer();
            for (Document doc : documents.values()) {
                indexer.indexDocument(doc);
            }
            indexer.calculateIdf();

            Searcher searcher = new Searcher(indexer.getDocumentVectors(), documents);

            Scanner scanner = new Scanner(System.in);
            System.out.println("I'm Vdora, your text explorer!. Type your query or 'exit' to quit.");

            while (true) {
                System.out.print(">>> ");
                String query = scanner.nextLine().trim();

                if (query.equalsIgnoreCase("exit")) {
                    System.out.println("¡Sankar la tasrek! Thanks for exploring with Vdora.");
                    break;
                }

                var results = searcher.search(query);
                if (results.isEmpty()) {
                    System.out.println("No Documents found.");
                } else {
                    System.out.println("Search results:");
                    for (SearchResult result : results) {
                        System.out.println(result);
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during document processing or search: ");
            e.printStackTrace();
        }
    }
}