package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Withdraw extends JDialog implements ActionListener {

    private final UserContext ctx;
    private final JTextField amountField;
    private final JPasswordField pinField;
    private final JButton withdrawButton, cancelButton;

    public Withdraw(JFrame parent, UserContext ctx) {
        super(parent, "Withdraw Money", true); // true = modal
        this.ctx = ctx;

        setLayout(null);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        UIUtils.createLabel("Withdraw Money", 150, 40, 300, 30, UIUtils.Font1, this);
        UIUtils.createLabel("Enter Amount:", 100, 120, 200, 30, UIUtils.Font3, this);
        amountField = UIUtils.createTextField(this, 250, 120, 150, 30, UIUtils.Font4);

        UIUtils.createLabel("Enter PIN:", 100, 170, 200, 30, UIUtils.Font3, this);
        pinField = UIUtils.createPasswordField(this, 250, 170, 150, 30, UIUtils.Font4);

        withdrawButton = UIUtils.createButton("Withdraw", 120, 250, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
        cancelButton = UIUtils.createButton("Cancel", 280, 250, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            dispose(); // close only this dialog
            return;
        }

        if (e.getSource() == withdrawButton) {
            String amountText = amountField.getText().trim();
            String pinText = new String(pinField.getPassword()).trim();

            if (amountText.isEmpty() || pinText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter amount and PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountText);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid positive amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            withdrawAmount(amount, pinText);
        }
    }

    private void withdrawAmount(int amount, String pinText) {
        String query_1 = "SELECT pin, balance FROM accounts WHERE account_no = ? FOR UPDATE";
        String query_2 = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
        String query_3 = "INSERT INTO transactions (account_no, type, amount, payment_type, balance_after) VALUES (?, 'Debit', ?, 'Cash', ?)";

        try (DatabaseConnection c = new DatabaseConnection()) {
            c.getConnection().setAutoCommit(false);

            try (PreparedStatement ps1 = c.getConnection().prepareStatement(query_1)) {
                ps1.setLong(1, ctx.getAccountNo());

                try (ResultSet rs = ps1.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }

                    String storedPin = rs.getString("pin");
                    int currentBalance = rs.getInt("balance");

                    if (!storedPin.equals(pinText)) {
                        JOptionPane.showMessageDialog(this, "Invalid PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }

                    if (currentBalance < amount) {
                        JOptionPane.showMessageDialog(this, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement ps2 = c.getConnection().prepareStatement(query_2);
                 PreparedStatement ps3 = c.getConnection().prepareStatement(query_3)) {

                ps2.setInt(1, amount);
                ps2.setLong(2, ctx.getAccountNo());
                ps2.executeUpdate();

                String balanceQuery = "SELECT balance FROM accounts WHERE account_no = ?";
                try (PreparedStatement psBal = c.getConnection().prepareStatement(balanceQuery)) {
                    psBal.setLong(1, ctx.getAccountNo());
                    ResultSet rs = psBal.executeQuery();
                    if (rs.next()) {
                        int newBalance = rs.getInt("balance");
                        ps3.setLong(1, ctx.getAccountNo());
                        ps3.setInt(2, amount);
                        ps3.setInt(3, newBalance);
                        ps3.executeUpdate();
                    }
                }

                c.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Rs " + amount + " withdrawn successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (Exception ex) {
                c.getConnection().rollback();
                JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
