package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChangePassword extends JDialog implements ActionListener {

    private final UserContext ctx;
    private final JPasswordField oldPasswordField, newPasswordField, confirmPasswordField;
    private final JButton changeButton, cancelButton;

    public ChangePassword(JDialog parent, UserContext ctx) {
        super(parent, "Change Password", true);
        this.ctx = ctx;

        setLayout(null);
        setSize(600, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = UIUtils.createLabel("Change Password", 0, 40, getWidth(), 50, UIUtils.Font1, this);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        UIUtils.createLabel("Current Password:", 80, 120, 200, 30, UIUtils.Font3, this);
        oldPasswordField = UIUtils.createPasswordField(this, 300, 120, 200, 30, UIUtils.Font4);

        UIUtils.createLabel("New Password:", 80, 170, 200, 30, UIUtils.Font3, this);
        newPasswordField = UIUtils.createPasswordField(this, 300, 170, 200, 30, UIUtils.Font4);

        UIUtils.createLabel("Confirm Password:", 80, 220, 200, 30, UIUtils.Font3, this);
        confirmPasswordField = UIUtils.createPasswordField(this, 300, 220, 200, 30, UIUtils.Font4);

        changeButton = UIUtils.createButton("CHANGE", 170, 300, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
        cancelButton = UIUtils.createButton("CANCEL", 320, 300, 120, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            dispose();
            return;
        }

        if (e.getSource() == changeButton) {
            String oldPass = new String(oldPasswordField.getPassword());
            String newPass = new String(newPasswordField.getPassword());
            String confirmPass = new String(confirmPasswordField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "New and Confirm passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPass.equals(oldPass)) {
                JOptionPane.showMessageDialog(this, "New password cannot be the same as old password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            changePassword(oldPass, newPass);
        }
    }

    private void changePassword(String oldPass, String newPass) {
        String queryCheck = "SELECT password FROM accounts WHERE account_no = ?";
        String queryUpdate = "UPDATE accounts SET password = ? WHERE account_no = ?";

        try (DatabaseConnection c = new DatabaseConnection()) {

            // Check if old password matches
            try (PreparedStatement psCheck = c.getConnection().prepareStatement(queryCheck)) {
                psCheck.setLong(1, ctx.getAccountNo());
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    String currentPassword = rs.getString("password");
                    if (!currentPassword.equals(oldPass)) {
                        JOptionPane.showMessageDialog(this, "Incorrect current password", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update password
            try (PreparedStatement psUpdate = c.getConnection().prepareStatement(queryUpdate)) {
                psUpdate.setString(1, newPass);
                psUpdate.setLong(2, ctx.getAccountNo());
                psUpdate.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
