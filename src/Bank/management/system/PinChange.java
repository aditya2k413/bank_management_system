package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PinChange extends JDialog implements ActionListener {

    private final UserContext ctx;
    private final JDialog parentDialog;
    private final JPasswordField oldPinField, newPinField, confirmPinField;
    private final JButton changeButton, cancelButton;

    public PinChange(JDialog parentDialog, UserContext ctx) {
        super(parentDialog, "Change PIN", true);
        this.ctx = ctx;
        this.parentDialog = parentDialog;

        setLayout(null);
        setSize(500, 400);
        setLocationRelativeTo(parentDialog);
        getContentPane().setBackground(Color.WHITE);

        UIUtils.createLabel("Change PIN", 120, 40, 250, 40, UIUtils.Font1, this);

        UIUtils.createLabel("Current PIN:", 100, 120, 150, 30, UIUtils.Font3, this);
        oldPinField = UIUtils.createPasswordField(this, 250, 120, 150, 30, UIUtils.Font4);

        UIUtils.createLabel("New PIN:", 100, 170, 150, 30, UIUtils.Font3, this);
        newPinField = UIUtils.createPasswordField(this, 250, 170, 150, 30, UIUtils.Font4);

        UIUtils.createLabel("Confirm PIN:", 100, 220, 150, 30, UIUtils.Font3, this);
        confirmPinField = UIUtils.createPasswordField(this, 250, 220, 150, 30, UIUtils.Font4);

        changeButton = UIUtils.createButton("CHANGE", 120, 290, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
        cancelButton = UIUtils.createButton("CANCEL", 270, 290, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            dispose();
            return;
        }

        if (e.getSource() == changeButton) {
            String oldPin = new String(oldPinField.getPassword());
            String newPin = new String(newPinField.getPassword());
            String confirmPin = new String(confirmPinField.getPassword());

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(this, "New PIN and Confirm PIN do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPin.equals(oldPin)) {
                JOptionPane.showMessageDialog(this, "Error: Try another pin", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            changePin(oldPin, newPin);
        }
    }

    private void changePin(String oldPin, String newPin) {
        String queryCheck = "SELECT pin FROM accounts WHERE account_no = ?";
        String queryUpdate = "UPDATE accounts SET pin = ? WHERE account_no = ?";

        try (DatabaseConnection c = new DatabaseConnection()) {

            try (PreparedStatement psCheck = c.getConnection().prepareStatement(queryCheck)) {
                psCheck.setLong(1, ctx.getAccountNo());
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    String currentPin = rs.getString("pin");
                    if (!currentPin.equals(oldPin)) {
                        JOptionPane.showMessageDialog(this, "Incorrect current PIN", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (PreparedStatement psUpdate = c.getConnection().prepareStatement(queryUpdate)) {
                psUpdate.setString(1, newPin);
                psUpdate.setLong(2, ctx.getAccountNo());
                psUpdate.executeUpdate();

                JOptionPane.showMessageDialog(this, "PIN changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error changing PIN: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
