package com.riyad.finance.storage;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


public class FileStorage {
    private final Path filePath;


    public FileStorage(Path filePath) {
        this.filePath = filePath;
    }


    public synchronized void ensureFileExists() throws IOException {
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }


    public synchronized List<String> readAllLines() throws IOException {
        ensureFileExists();
        return Files.readAllLines(filePath, StandardCharsets.UTF_8);
    }


    public synchronized void writeAllLines(List<String> lines) throws IOException {
        ensureFileExists();
        Path temp = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        Files.move(temp, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }


    public synchronized void appendLine(String line) throws IOException {
        ensureFileExists();
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }
}