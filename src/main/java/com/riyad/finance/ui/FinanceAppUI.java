package com.riyad.finance.ui;

import com.riyad.finance.dao.UserDAO;
import com.riyad.finance.dao.TransactionDAO;
import com.riyad.finance.export.CSVExporter;
import com.riyad.finance.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class FinanceAppUI extends JFrame {

    private final UserDAO userDAO;
    private final TransactionDAO txDAO;
    private final String currentUser;

    private JLabel totalIncomeLbl;
    private JLabel totalExpenseLbl;
    private JLabel balanceLbl;

    private final DefaultTableModel tableModel;
    private final JTable table;

    public FinanceAppUI(UserDAO userDAO, TransactionDAO txDAO, String currentUser) {
        super("Finance Tracker - Logged in as: " + currentUser);
        this.userDAO = userDAO;
        this.txDAO = txDAO;
        this.currentUser = currentUser;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==========================
        // TABLE
        // ==========================
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Date", "Type", "Category", "Amount", "Description"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ==========================
        // TOOLBAR BUTTONS
        // ==========================
        JPanel toolbar = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export CSV");
        JButton logoutBtn = new JButton("Logout");

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(delBtn);
        toolbar.add(refreshBtn);
        toolbar.add(exportBtn);
        toolbar.add(logoutBtn);

        add(toolbar, BorderLayout.NORTH);

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> refreshTable());
        exportBtn.addActionListener(e -> onExport());
        logoutBtn.addActionListener(e -> onLogout());

        // ==========================
        // SUMMARY PANEL (Styled)
        // ==========================
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));
        summaryPanel.setBackground(new Color(245, 245, 245));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);

        totalIncomeLbl = new JLabel("Total Income: 0.00");
        totalExpenseLbl = new JLabel("Total Expense: 0.00");
        balanceLbl = new JLabel("Balance: 0.00");

        totalIncomeLbl.setFont(labelFont);
        totalExpenseLbl.setFont(labelFont);
        balanceLbl.setFont(labelFont);

        totalIncomeLbl.setForeground(new Color(0, 128, 0)); // Green
        totalExpenseLbl.setForeground(new Color(180, 0, 0)); // Red
        balanceLbl.setForeground(new Color(0, 70, 140)); // Blue

        summaryPanel.add(totalIncomeLbl);
        summaryPanel.add(totalExpenseLbl);
        summaryPanel.add(balanceLbl);

        add(summaryPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    // ==========================
    // ADD TRANSACTION
    // ==========================
    private void onAdd() {
        TransactionDialog dlg = new TransactionDialog(this, currentUser);
        dlg.setFields(java.time.LocalDate.now().toString(), Transaction.Type.EXPENSE, "General", "0.00", "");
        dlg.setVisible(true);

        if (!dlg.isSaved()) return;

        Transaction tx = dlg.getTransaction(UUID.randomUUID().toString());

        try {
            txDAO.save(tx);
            refreshTable();
        } catch (IOException ex) {
            showError(ex);
        }
    }

    // ==========================
    // EDIT TRANSACTION
    // ==========================
    private void onEdit() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        String id = (String) tableModel.getValueAt(r, 0);
        try {
            Transaction existing = txDAO.findAll().stream()
                    .filter(t -> t.getId().equals(id))
                    .findFirst().orElse(null);
            if (existing == null) return;

            TransactionDialog dlg = new TransactionDialog(this, currentUser);
            dlg.setFields(existing.getDate().toString(), existing.getType(), existing.getCategory(),
                    existing.getAmount().toPlainString(), existing.getDescription());

            dlg.setVisible(true);
            if (!dlg.isSaved()) return;

            Transaction updated = dlg.getTransaction(id);
            updated.setUsername(currentUser);
            txDAO.update(updated);
            refreshTable();

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ==========================
    // DELETE
    // ==========================
    private void onDelete() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first");
            return;
        }

        String id = (String) tableModel.getValueAt(r, 0);

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete selected transaction?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            txDAO.delete(id);
            refreshTable();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ==========================
    // EXPORT CSV
    // ==========================
    private void onExport() {
        try {
            Path out = Path.of("data/export.csv");
            CSVExporter.exportToCsv(txDAO.findAll(), out);
            JOptionPane.showMessageDialog(this, "Exported to " + out.toAbsolutePath());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ==========================
    // REFRESH TABLE + SUMMARY
    // ==========================
    private void refreshTable() {
        try {
            List<Transaction> all = txDAO.findAll();
            List<Transaction> userTx = all.stream()
                    .filter(t -> t.getUsername().equals(currentUser))
                    .toList();

            double totalIncome = 0;
            double totalExpense = 0;

            tableModel.setRowCount(0);

            for (Transaction t : userTx) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getDate(),
                        t.getType(),
                        t.getCategory(),
                        t.getAmount(),
                        t.getDescription()
                });

                if (t.getType() == Transaction.Type.INCOME)
                    totalIncome += t.getAmount().doubleValue();
                else
                    totalExpense += t.getAmount().doubleValue();
            }

            double balance = totalIncome - totalExpense;

            totalIncomeLbl.setText("Total Income: " + totalIncome);
            totalExpenseLbl.setText("Total Expense: " + totalExpense);
            balanceLbl.setText("Balance: " + balance);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ==========================
    // LOGOUT
    // ==========================
    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginUI(userDAO, txDAO).setVisible(true);
        }
    }

    // ==========================
    // ERROR POPUP
    // ==========================
    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
