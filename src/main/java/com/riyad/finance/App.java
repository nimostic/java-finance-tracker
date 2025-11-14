package com.riyad.finance;

import com.riyad.finance.dao.TransactionDAO;
import com.riyad.finance.ui.FinanceAppUI;

import javax.swing.*;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Path to the text file storage
                Path db = Path.of("data/transactions.txt");

                // Initialize DAO
                TransactionDAO dao = new TransactionDAO(db);

                // Launch the Swing GUI
                FinanceAppUI ui = new FinanceAppUI(dao);
                ui.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start: " + ex.getMessage());
            }
        });
    }
}