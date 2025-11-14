package com.riyad.finance.dao;

import com.riyad.finance.model.Transaction;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final Path file;

    public TransactionDAO(Path file){
        this.file = file.toAbsolutePath();
        try { if(!file.toFile().exists()) file.toFile().createNewFile(); } catch(IOException e){ e.printStackTrace(); }
    }

    public void save(Transaction t) throws IOException {
        try(BufferedWriter w = new BufferedWriter(new FileWriter(file.toFile(), true))){
            w.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                    t.getId(), t.getDate(), t.getType(), t.getCategory(),
                    t.getAmount(), t.getDescription().replace(",", ";"), t.getUsername()));
            w.newLine();
        }
    }

    public void update(Transaction updated) throws IOException {
        List<Transaction> list = findAll();
        for(int i=0; i<list.size(); i++){
            if(list.get(i).getId().equals(updated.getId())){
                list.set(i, updated);
                break;
            }
        }
        saveAll(list);
    }

    public void delete(String id) throws IOException {
        List<Transaction> list = findAll();
        list.removeIf(t -> t.getId().equals(id));
        saveAll(list);
    }

    private void saveAll(List<Transaction> list) throws IOException {
        try(BufferedWriter w = new BufferedWriter(new FileWriter(file.toFile(), false))){
            for(Transaction t: list){
                w.write(String.format("%s,%s,%s,%s,%s,%s,%s",
                        t.getId(), t.getDate(), t.getType(), t.getCategory(),
                        t.getAmount(), t.getDescription().replace(",", ";"), t.getUsername()));
                w.newLine();
            }
        }
    }

    public List<Transaction> findAll() throws IOException {
        List<Transaction> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(file.toFile()))){
            String line;
            while((line=r.readLine())!=null){
                String[] p = line.split(",",7);
                if(p.length==7){
                    list.add(new Transaction(
                            p[0], LocalDate.parse(p[1]),
                            Transaction.Type.valueOf(p[2]),
                            p[3], new BigDecimal(p[4]), p[5], p[6]
                    ));
                }
            }
        }
        return list;
    }
}
