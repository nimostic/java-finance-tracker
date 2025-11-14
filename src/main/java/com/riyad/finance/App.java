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
                // Transaction file
                Path txFile = Path.of("data/transactions.txt");
                TransactionDAO txDAO = new TransactionDAO(txFile);

                // UserDAO now has NO constructor arguments
                UserDAO userDAO = new UserDAO();

                // Start Login UI
                LoginUI loginUI = new LoginUI(userDAO, txDAO);
                loginUI.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start: " + ex.getMessage());
            }
        });
    }
}
