package Bank.management.system;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Transfer extends JDialog implements ActionListener {
    private final UserContext ctx;
    JButton transferButton, backButton;
    JTextField amountField, toAccountField;
    JPasswordField pinField;

    public Transfer(JFrame parent, UserContext ctx) {
        super(parent, "Transfer Money", true);
        this.ctx = ctx;

        setLayout(null);
        setSize(550, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(java.awt.Color.WHITE);

        UIUtils.createLabel("Transfer Money", 180, 40, 300, 40, UIUtils.Font1, this);
        UIUtils.createLabel("Amount:", 100, 120, 150, 30, UIUtils.Font3, this);
        amountField = UIUtils.createTextField(this, 250, 120, 200, 30, UIUtils.Font4);

        UIUtils.createLabel("To Account No:", 100, 170, 150, 30, UIUtils.Font3, this);
        toAccountField = UIUtils.createTextField(this, 250, 170, 200, 30, UIUtils.Font4);

        UIUtils.createLabel("PIN:", 100, 220, 150, 30, UIUtils.Font3, this);
        pinField = UIUtils.createPasswordField(this, 250, 220, 200, 30, UIUtils.Font4);

        transferButton = UIUtils.createButton("TRANSFER", 130, 300, 130, 40, UIUtils.Font6, java.awt.Color.BLACK, java.awt.Color.LIGHT_GRAY, this, this);
        backButton = UIUtils.createButton("CANCEL", 290, 300, 130, 40, UIUtils.Font6, java.awt.Color.BLACK, java.awt.Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
        } else if (e.getSource() == transferButton) {
            String amountText = amountField.getText();
            String toAccountText = toAccountField.getText();
            String pinText = new String(pinField.getPassword());

            if (amountText.isEmpty() || toAccountText.isEmpty() || pinText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all the fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int amount;
            long toAccountNo;

            try {
                amount = Integer.parseInt(amountText);
                toAccountNo = Long.parseLong(toAccountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            transfer(amount, toAccountNo, pinText);
        }
    }

    private void transfer(int amount, long toAccountNo, String pinText) {
        String checkReceiver = "SELECT account_no FROM accounts WHERE account_no = ?";
        String checkSender = "SELECT balance, pin FROM accounts WHERE account_no = ? FOR UPDATE";
        String updateSender = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
        String updateReceiver = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
        String insertTransaction = "INSERT INTO transactions (account_no, type, amount, payment_type, balance_after) VALUES (?, ?, ?, 'Transfer', ?)";

        try (DatabaseConnection c = new DatabaseConnection()) {
            c.getConnection().setAutoCommit(false);

            try (PreparedStatement psCheckReceiver = c.getConnection().prepareStatement(checkReceiver)) {
                psCheckReceiver.setLong(1, toAccountNo);
                try (ResultSet rs = psCheckReceiver.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Receiver account does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement psCheckSender = c.getConnection().prepareStatement(checkSender)) {
                psCheckSender.setLong(1, ctx.getAccountNo());
                try (ResultSet rs = psCheckSender.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "Unexpected error: account not found", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }

                    String storedPin = rs.getString("pin");
                    int currentBalance = rs.getInt("balance");

                    if (!storedPin.equals(pinText)) {
                        JOptionPane.showMessageDialog(this, "Invalid PIN", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }

                    if (currentBalance < amount) {
                        JOptionPane.showMessageDialog(this, "Insufficient balance", "Error", JOptionPane.ERROR_MESSAGE);
                        c.getConnection().rollback();
                        return;
                    }
                }
            }

            try (PreparedStatement psUpdateSender = c.getConnection().prepareStatement(updateSender);
                 PreparedStatement psUpdateReceiver = c.getConnection().prepareStatement(updateReceiver);
                 PreparedStatement psInsertTransaction = c.getConnection().prepareStatement(insertTransaction)) {

                psUpdateSender.setInt(1, amount);
                psUpdateSender.setLong(2, ctx.getAccountNo());
                psUpdateSender.executeUpdate();

                psUpdateReceiver.setInt(1, amount);
                psUpdateReceiver.setLong(2, toAccountNo);
                psUpdateReceiver.executeUpdate();

                String balanceQuery = "SELECT balance FROM accounts WHERE account_no = ?";
                try (PreparedStatement psBal = c.getConnection().prepareStatement(balanceQuery)) {


                    psBal.setLong(1, ctx.getAccountNo());
                    ResultSet rsSender = psBal.executeQuery();
                    if (rsSender.next()) {
                        int senderBalance = rsSender.getInt("balance");
                        psInsertTransaction.setLong(1, ctx.getAccountNo());
                        psInsertTransaction.setString(2, "Debit");
                        psInsertTransaction.setInt(3, amount);
                        psInsertTransaction.setInt(4, senderBalance);
                        psInsertTransaction.executeUpdate();
                    }

                    rsSender.close();
                    psInsertTransaction.clearParameters();

                    psBal.setLong(1, toAccountNo);
                    ResultSet rsReceiver = psBal.executeQuery();
                    if (rsReceiver.next()) {
                        int receiverBalance = rsReceiver.getInt("balance");
                        psInsertTransaction.setLong(1, toAccountNo);
                        psInsertTransaction.setString(2, "Credit");
                        psInsertTransaction.setInt(3, amount);
                        psInsertTransaction.setInt(4, receiverBalance);
                        psInsertTransaction.executeUpdate();
                    }
                }


                c.getConnection().commit();

                JOptionPane.showMessageDialog(this,
                        "Rs " + amount + " transferred successfully to account " + toAccountNo,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dispose();

            } catch (Exception ex) {
                c.getConnection().rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Transaction failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
