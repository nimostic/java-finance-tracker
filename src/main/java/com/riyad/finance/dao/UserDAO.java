package com.riyad.finance.dao;

import com.riyad.finance.model.User;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private final Path file;

    public UserDAO(Path file){
        this.file = file.toAbsolutePath();
        try { if(!file.toFile().exists()) file.toFile().createNewFile(); } catch(IOException e){ e.printStackTrace(); }
    }

    public void save(User u) throws IOException {
        try(BufferedWriter w = new BufferedWriter(new FileWriter(file.toFile(), true))){
            w.write(u.getUsername()+","+u.getPassword());
            w.newLine();
        }
    }

    public List<User> findAll() throws IOException {
        List<User> list = new ArrayList<>();
        try(BufferedReader r = new BufferedReader(new FileReader(file.toFile()))){
            String line;
            while((line=r.readLine())!=null){
                String[] p = line.split(",",2);
                if(p.length==2) list.add(new User(p[0], p[1]));
            }
        }
        return list;
    }

    public Optional<User> login(String username, String password) throws IOException {
        return findAll().stream().filter(u->u.getUsername().equals(username)&&u.getPassword().equals(password)).findFirst();
    }
}
