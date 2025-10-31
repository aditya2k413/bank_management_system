package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Deposite extends JDialog implements ActionListener {
    private final UserContext ctx;
    JButton depositButton, backButton;
    JTextField amountField;
    JLabel amountLabel;

    public Deposite(JFrame parent, UserContext ctx) {
        super(parent, "Deposit", true);
        this.ctx = ctx;

        setLayout(null);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        UIUtils.createLabel("Deposit Amount", 150, 60, 300, 40, UIUtils.Font1, this);
        amountLabel = UIUtils.createLabel("Amount:", 100, 150, 150, 30, UIUtils.Font3, this);
        amountField = UIUtils.createTextField(this, 220, 150, 150, 30, UIUtils.Font4);

        depositButton = UIUtils.createButton("DEPOSIT", 100, 230, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
        backButton = UIUtils.createButton("CANCEL", 260, 230, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
        } else if (e.getSource() == depositButton) {
            String amountText = amountField.getText();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dep(amount);
        }
    }

    private void dep(int amount) {
        String query_1 = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
        String query_2 = "INSERT INTO transactions (account_no, type, amount, payment_type, balance_after) VALUES (?, 'Credit', ?, 'Cash', ?)";

        try (DatabaseConnection c = new DatabaseConnection();
             PreparedStatement ps1 = c.getConnection().prepareStatement(query_1);
             PreparedStatement ps2 = c.getConnection().prepareStatement(query_2)) {

            try {
                c.getConnection().setAutoCommit(false);

                ps1.setInt(1, amount);
                ps1.setLong(2, ctx.getAccountNo());
                ps1.executeUpdate();

                String balanceQuery = "SELECT balance FROM accounts WHERE account_no = ?";
                try (PreparedStatement psBal = c.getConnection().prepareStatement(balanceQuery)) {
                    psBal.setLong(1, ctx.getAccountNo());
                    ResultSet rs = psBal.executeQuery();
                    if (rs.next()) {
                        int newBalance = rs.getInt("balance");

                        ps2.setLong(1, ctx.getAccountNo());
                        ps2.setInt(2, amount);
                        ps2.setInt(3, newBalance);
                        ps2.executeUpdate();
                    }
                }

                c.getConnection().commit();

                JOptionPane.showMessageDialog(this, "Rs " + amount + " deposited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (SQLException e) {
                c.getConnection().rollback();
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Transaction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Transaction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
