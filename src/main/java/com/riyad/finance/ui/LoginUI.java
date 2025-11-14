package com.riyad.finance.ui;

import com.riyad.finance.dao.TransactionDAO;
import com.riyad.finance.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginUI extends JFrame {
    private final UserDAO userDAO;
    private final TransactionDAO txDAO;

    public LoginUI(UserDAO userDAO, TransactionDAO txDAO){
        super("Login - Finance Tracker");
        this.userDAO = userDAO;
        this.txDAO = txDAO;

        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3,2,10,10));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        add(loginBtn);
        add(registerBtn);

        loginBtn.addActionListener(e->{
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            try{
                if(userDAO.login(username,password).isPresent()){
                    JOptionPane.showMessageDialog(this,"Login successful!");
                    dispose();
                    new FinanceAppUI(userDAO, txDAO, username).setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(this,"Invalid credentials","Error",JOptionPane.ERROR_MESSAGE);
                }
            } catch(IOException ex){ ex.printStackTrace(); }
        });

        registerBtn.addActionListener(e->{
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if(username.isEmpty()||password.isEmpty()){ JOptionPane.showMessageDialog(this,"Enter username and password"); return; }
            try { userDAO.save(new com.riyad.finance.model.User(username,password));
                JOptionPane.showMessageDialog(this,"User registered successfully!");
            } catch(IOException ex){ ex.printStackTrace(); }
        });

        setLocationRelativeTo(null);
    }
}
