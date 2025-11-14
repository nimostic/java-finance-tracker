package com.riyad.finance.dao;

import com.riyad.finance.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private final File file = new File("data/users.txt");

    public UserDAO() {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean exists(String username) throws IOException {
        return findAll().stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public void save(User user) throws IOException {
        if (exists(user.getUsername())) {
            throw new IOException("Username already exists!");
        }

        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(user.getUsername() + "," + user.getPassword() + "\n");
        }
    }

    public Optional<User> login(String username, String password) throws IOException {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username)
                        && u.getPassword().equals(password))
                .findFirst();
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 2) {
                    users.add(new User(p[0], p[1]));
                }
            }
        }
        return users;
    }
}
