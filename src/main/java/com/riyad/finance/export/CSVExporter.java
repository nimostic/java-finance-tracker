package com.riyad.finance.export;

import com.riyad.finance.model.Transaction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CSVExporter {

    /**
     * Export a list of transactions to a CSV file
     * @param list List of transactions
     * @param out Path to output CSV file
     * @throws IOException if writing fails
     */
    public static void exportToCsv(List<Transaction> list, Path out) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            // Header row
            w.write("id,date,type,category,amount,description");
            w.newLine();

            // Data rows
            for (Transaction t : list) {
                String line = String.format("%s,%s,%s,%s,%s,%s",
                        t.getId(),
                        t.getDate(),
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getDescription().replace(",", ";")); // replace comma to avoid CSV break
                w.write(line);
                w.newLine();
            }
        }
    }
}
