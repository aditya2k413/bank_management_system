package Bank.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MiniStatement extends JDialog {
    private  final UserContext ctx;
    private JTable table;
    private DefaultTableModel model;

    public MiniStatement(JFrame parent,UserContext ctx) {
        super(parent, "Mini Statement", true);
        this.ctx = ctx;
        setLayout(new BorderLayout());
        setSize(700, 400);
        setLocationRelativeTo(parent);

        String[] columnName = {"Date & Time", "Transaction Type", "Debit (-)", "Credit (+)", "Balance (₹)"};
        model = new DefaultTableModel(columnName, 0);

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        loadTransactions();

    }
    private void loadTransactions() {
        String query = " SELECT timestamp, type, amount, balance_after, payment_type FROM transactions WHERE account_no = ? AND MONTH(timestamp) = MONTH(CURRENT_DATE()) AND YEAR(timestamp) = YEAR(CURRENT_DATE()) ORDER BY timestamp DESC";
        try (DatabaseConnection c = new DatabaseConnection();
             PreparedStatement ps = c.getConnection().prepareStatement(query)){
            ps.setLong(1, ctx.getAccountNo());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String dateTime = rs.getString("timestamp");
                String type = rs.getString("type");
                int amount = rs.getInt("amount");
                int balance = rs.getInt("balance_after");
                String paymentType = rs.getString("payment_type");

                // Combine "type" + "payment_type" for clearer label
                String transactionType = paymentType + " (" + type + ")";

                // Put amount in the correct column
                String debit = type.equalsIgnoreCase("Debit") ? String.valueOf(amount) : "";
                String credit = type.equalsIgnoreCase("Credit") ? String.valueOf(amount) : "";

                Object[] row = {dateTime, transactionType, debit, credit, balance};
                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
