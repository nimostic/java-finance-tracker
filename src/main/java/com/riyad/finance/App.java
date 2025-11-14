package com.riyad.finance;

import com.riyad.finance.dao.TransactionDAO;
import com.riyad.finance.dao.UserDAO;
import com.riyad.finance.ui.LoginUI;

import javax.swing.*;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Path txFile = Path.of("data/transactions.txt");
                TransactionDAO txDAO = new TransactionDAO(txFile);

                Path userFile = Path.of("data/users.txt");
                UserDAO userDAO = new UserDAO(userFile);

                LoginUI loginUI = new LoginUI(userDAO, txDAO);
                loginUI.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start: " + ex.getMessage());
            }
        });
    }
}
