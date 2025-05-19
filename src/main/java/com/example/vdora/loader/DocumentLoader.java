package com.example.vdora.loader;

import com.example.vdora.model.Document;

import java.util.Map;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class DocumentLoader implements AutoCloseable {
    private static final int BATCH_SIZE = 100;
    private static final int BLOCK_SIZE = 1024 * 1024; // 1MB block size
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    
    private final String documentsPath;
    private final ExecutorService executor;
    private final AtomicInteger idCounter;
    private final Map<String, Document> documents;

    public DocumentLoader(String documentsPath) {
        this.documentsPath = documentsPath;
        this.executor = Executors.newFixedThreadPool(NUM_THREADS);
        this.idCounter = new AtomicInteger(1);
        this.documents = new ConcurrentHashMap<>();
    }

    public Map<String, Document> loadDocuments() throws IOException {
        System.out.println("Loading documents from: " + documentsPath);
        
        try (Stream<Path> paths = Files.walk(Paths.get(documentsPath))) {
            List<Path> filePaths = paths
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

            int totalFiles = filePaths.size();
            System.out.println("Found " + totalFiles + " files to process");

            // Process files in blocks
            for (int i = 0; i < filePaths.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, filePaths.size());
                List<Path> batch = filePaths.subList(i, end);
                processBatch(batch);
            }
        } finally {
            executor.shutdown();
        }

        System.out.println("Successfully loaded " + documents.size() + " documents");
        return documents;
    }

    private void processBatch(List<Path> batch) {
        List<Future<Document>> futures = batch.stream()
            .map(path -> executor.submit(() -> {
                try {
                    return processDocument(path);
                } catch (Exception e) {
                    System.err.println("Error processing file: " + path);
                    e.printStackTrace();
                    return null;
                }
            }))
            .toList();

        for (Future<Document> future : futures) {
            try {
                Document doc = future.get();
                if (doc != null) {
                    documents.put(doc.id(), doc);
                }
            } catch (Exception e) {
                System.err.println("Error getting document from future");
                e.printStackTrace();
            }
        }
//        System.out.printf("Processed %d/%d files (%.1f%%)\n",
//            end, totalFiles, (end * 100.0 / totalFiles));
    }

    private Document processDocument(Path path) throws IOException {
        String id = String.valueOf(idCounter.getAndIncrement());
        readFileInBlocks(path);
        return new Document(id, path.toString());
    }

    private void readFileInBlocks(Path path) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
        
        try (InputStream inputStream = Files.newInputStream(path);
             ReadableByteChannel channel = Channels.newChannel(inputStream)) {
            
            while (channel.read(buffer) != -1) {
                buffer.flip();
                String ignore = StandardCharsets.UTF_8.decode(buffer).toString();
                buffer.clear();
            }
        }
    }

    @Override
    public void close() {
        executor.shutdown();
    }
} 