package com.riyad.finance.ui;

import com.riyad.finance.model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDialog extends JDialog {
    private boolean saved = false;
    private final JTextField dateField = new JTextField(10);
    private final JComboBox<Transaction.Type> typeBox = new JComboBox<>(Transaction.Type.values());
    private final JTextField categoryField = new JTextField(15);
    private final JTextField amountField = new JTextField(10);
    private final JTextField descField = new JTextField(20);
    private String username;
    public TransactionDialog(Frame owner, String username)  {
        super(owner, "Add / Edit Transaction", true);
        this.username = username;
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        form.add(new JLabel("Date (yyyy-MM-dd):")); form.add(dateField);
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Category:")); form.add(categoryField);
        form.add(new JLabel("Amount:")); form.add(amountField);
        form.add(new JLabel("Description:")); form.add(descField);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        buttons.add(save); buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        save.addActionListener(e -> {
            if (dateField.getText().trim().isEmpty() || amountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Date and amount required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            saved = true;
            setVisible(false);
        });

        cancel.addActionListener(e -> {
            saved = false;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() { return saved; }

    public void setFields(String date, Transaction.Type type, String category, String amount, String desc) {
        dateField.setText(date);
        typeBox.setSelectedItem(type);
        categoryField.setText(category);
        amountField.setText(amount);
        descField.setText(desc);
    }

    public Transaction getTransaction(String id) {
        LocalDate date = LocalDate.parse(dateField.getText());
        Transaction.Type type = (Transaction.Type) typeBox.getSelectedItem();
        BigDecimal amt = new BigDecimal(amountField.getText());
        return new Transaction(id, date, type, categoryField.getText(), amt, descField.getText(), username);
    }
}
