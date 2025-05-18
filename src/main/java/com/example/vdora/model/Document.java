package com.example.vdora.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record Document(String id, String path) {

    public String getName() {
        return new java.io.File(path).getName();
    }
    public String getContent() throws IOException {
        return Files.readString(Path.of(path));
    }
} 