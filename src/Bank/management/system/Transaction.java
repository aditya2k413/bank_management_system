package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaction extends JFrame implements ActionListener {
    private final UserContext ctx;

    JButton depositButton, withdrawButton, transferButton, miniStatementButton, SecurityOptions, balanceEnquiryButton, exitButton;

    public Transaction(UserContext ctx) {
        this.ctx = ctx;
        String name = ctx.getName();
        long accno = ctx.getAccountNo();
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        UIUtils.setTitleAndVisibility(this, "Transaction Page", 800, 600, 350, 100);

        UIUtils.createLabel("Welcome " + name, 200, 30, 400, 30, UIUtils.Font1, this);
        UIUtils.createLabel("Account No: " + accno, 250, 70, 400, 25, UIUtils.Font2, this);

        depositButton = UIUtils.createButton("Deposit", 160, 160, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);
        withdrawButton = UIUtils.createButton("Withdraw", 440, 160, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);

        transferButton = UIUtils.createButton("Transfer Money", 160, 250, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);
        miniStatementButton = UIUtils.createButton("Mini Statement", 440, 250, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);

        SecurityOptions = UIUtils.createButton("Security Options", 160, 340, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);
        balanceEnquiryButton = UIUtils.createButton("Balance Enquiry", 440, 340, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);

        exitButton = UIUtils.createButton("Exit", 300, 440, 200, 45, UIUtils.Font3, Color.BLACK, Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            setVisible(false);
            new Login().setVisible(true);
            dispose();
        } else if (e.getSource() == depositButton) {
            new Deposite(this, ctx).setVisible(true);
        } else if (e.getSource() == withdrawButton) {
            new Withdraw(this, ctx).setVisible(true);
        } else if (e.getSource()==transferButton) {
            new Transfer(this, ctx).setVisible(true);
        } else if (e.getSource()==miniStatementButton) {
            new MiniStatement(this, ctx).setVisible(true);
        } else if (e.getSource()==SecurityOptions) {
            new SecurityOptionsDialog(this, ctx).setVisible(true);
        } else if (e.getSource()==balanceEnquiryButton) {
            showBalance();
        }

    }
    private void showBalance() {
        String query = "SELECT balance FROM accounts WHERE account_no = ?";

        try (DatabaseConnection c = new DatabaseConnection();
             PreparedStatement ps = c.getConnection().prepareStatement(query)) {

            ps.setLong(1, ctx.getAccountNo());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int balance = rs.getInt("balance");
                JOptionPane.showMessageDialog(this,
                        "Your current balance is ₹ " + balance,
                        "Balance Enquiry",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Account not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error fetching balance: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Transaction(new UserContext(31, 1961516542, "Test User"));
    }
}
