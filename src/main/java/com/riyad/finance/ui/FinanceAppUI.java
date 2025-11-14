package com.riyad.finance.ui;

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
    private final TransactionDAO dao;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public FinanceAppUI(TransactionDAO dao) {
        super("Java Finance Tracker");
        this.dao = dao;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Date", "Type", "Category", "Amount", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Toolbar buttons
        JPanel toolbar = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export CSV");
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(delBtn); toolbar.add(refreshBtn); toolbar.add(exportBtn);
        add(toolbar, BorderLayout.NORTH);

        // Button actions
        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> refreshTable());
        exportBtn.addActionListener(e -> onExport());

        // Initial load
        refreshTable();
    }

    private void onAdd() {
        TransactionDialog dlg = new TransactionDialog(this);
        dlg.setFields(java.time.LocalDate.now().toString(), Transaction.Type.EXPENSE, "General", "0.00", "");
        dlg.setVisible(true);
        if (!dlg.isSaved()) return;

        Transaction tx = dlg.getTransaction(UUID.randomUUID().toString());
        try {
            dao.save(tx);
            refreshTable();
        } catch (IOException ex) {
            showError(ex);
        }
    }

    private void onEdit() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a row first"); return; }
        String id = (String) tableModel.getValueAt(r, 0);
        try {
            Transaction existing = dao.findAll().stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
            if (existing == null) return;

            TransactionDialog dlg = new TransactionDialog(this);
            dlg.setFields(existing.getDate().toString(), existing.getType(), existing.getCategory(),
                    existing.getAmount().toPlainString(), existing.getDescription());
            dlg.setVisible(true);
            if (!dlg.isSaved()) return;

            Transaction updated = dlg.getTransaction(id);
            dao.update(updated);
            refreshTable();
        } catch (Exception ex) { showError(ex); }
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a row first"); return; }
        String id = (String) tableModel.getValueAt(r, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Delete selected transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try { dao.delete(id); refreshTable(); } catch (Exception ex) { showError(ex); }
    }

    private void onExport() {
        try {
            List<Transaction> all = dao.findAll();
            Path out = Path.of("data/export.csv");
            CSVExporter.exportToCsv(all, out);
            JOptionPane.showMessageDialog(this, "Exported to " + out.toAbsolutePath());
        } catch (Exception ex) { showError(ex); }
    }

    private void refreshTable() {
        try {
            List<Transaction> all = dao.findAll();
            tableModel.setRowCount(0);
            for (Transaction t : all) {
                tableModel.addRow(new Object[]{
                        t.getId(), t.getDate().toString(), t.getType().name(),
                        t.getCategory(), t.getAmount().toPlainString(), t.getDescription()
                });
            }
        } catch (Exception e) { showError(e); }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
