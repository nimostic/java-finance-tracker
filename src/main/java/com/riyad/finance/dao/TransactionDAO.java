package com.riyad.finance.dao;


import com.riyad.finance.model.Transaction;
import com.riyad.finance.storage.FileStorage;
import com.riyad.finance.util.DateUtil;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class TransactionDAO {
    private static final String SEP = "|";
    private final FileStorage storage;


    public TransactionDAO(Path filePath) {
        this.storage = new FileStorage(filePath);
    }


    public List<Transaction> findAll() throws IOException {
        List<String> lines = storage.readAllLines();
        List<Transaction> list = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\" + SEP, -1);
// id|date|type|category|amount|description
            String id = parts[0];
            LocalDate date = DateUtil.parseDate(parts[1]);
            Transaction.Type type = Transaction.Type.valueOf(parts[2]);
            String category = parts[3];
            BigDecimal amount = new BigDecimal(parts[4]);
            String description = parts.length > 5 ? parts[5] : "";
            list.add(new Transaction(id, date, type, category, amount, description));
        }
// sort newest first
        list.sort(Comparator.comparing(Transaction::getDate).reversed());
        return list;
    }


    public void save(Transaction tx) throws IOException {
// append
        String line = encode(tx);
        storage.appendLine(line);
    }


    public void update(Transaction tx) throws IOException {
        List<Transaction> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(tx.getId())) {
                all.set(i, tx);
                found = true;
                break;
            }
        }
        if (!found) throw new NoSuchElementException("Transaction not found: " + tx.getId());
        writeList(all);
    }


    public void delete(String id) throws IOException {
        List<Transaction> all = findAll();
        List<Transaction> filtered = all.stream().filter(t -> !t.getId().equals(id)).collect(Collectors.toList());
        writeList(filtered);
    }


    private void writeList(List<Transaction> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Transaction t : list) lines.add(encode(t));
        storage.writeAllLines(lines);
    }


    private String encode(Transaction tx) {
// escape '|' in description/category if necessary (simple replace)
        String desc = tx.getDescription() == null ? "" : tx.getDescription().replace(SEP, "/");
        String cat = tx.getCategory() == null ? "" : tx.getCategory().replace(SEP, "/");
        return String.join(SEP,
                tx.getId(),
                DateUtil.formatDate(tx.getDate()),
                tx.getType().name(),
                cat,
                tx.getAmount().toPlainString(),
                desc
        );
    }
}