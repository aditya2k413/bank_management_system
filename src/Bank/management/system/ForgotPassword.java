package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPassword extends JDialog implements ActionListener {
    private final UserContext ctx;
    private final JDialog parentDialog;

    private final JComboBox<String> recoveryQuestionBox;
    private final JTextField answerField;
    private final JPasswordField newPassField, confirmPassField;
    private final JButton resetButton, cancelButton;

    public ForgotPassword(JDialog parentDialog, UserContext ctx) {
        super(parentDialog, "Forgot Password", true);
        this.ctx = ctx;
        this.parentDialog = parentDialog;

        setLayout(null);
        setSize(550, 450);
        setLocationRelativeTo(parentDialog);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = UIUtils.createLabel("Reset Your Password", 0, 40, getWidth(), 40, UIUtils.Font1, this);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        UIUtils.createLabel("Recovery Question:", 80, 120, 200, 30, UIUtils.Font3, this);
        String[] questions = {
                "Select a recovery question",
                "What is your pet's name?",
                "What is your mother's maiden name?",
                "What was your first car?",
                "What elementary school did you attend?",
                "What is the name of the town where you were born?"
        };
        recoveryQuestionBox = UIUtils.createComboBox(questions, 260, 120, 250, 30, UIUtils.Font4, this);
        recoveryQuestionBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {
                    ((JComponent) c).setToolTipText(value.toString());
                }
                return c;
            }
        });

        UIUtils.createLabel("Your Answer:", 80, 170, 200, 30, UIUtils.Font3, this);
        answerField = UIUtils.createTextField(this, 260, 170, 250, 30, UIUtils.Font4);

        UIUtils.createLabel("New Password:", 80, 220, 200, 30, UIUtils.Font3, this);
        newPassField = UIUtils.createPasswordField(this, 260, 220, 250, 30, UIUtils.Font4);

        UIUtils.createLabel("Confirm Password:", 80, 270, 200, 30, UIUtils.Font3, this);
        confirmPassField = UIUtils.createPasswordField(this, 260, 270, 250, 30, UIUtils.Font4);

        resetButton = UIUtils.createButton("RESET", 150, 340, 100, 35, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
        cancelButton = UIUtils.createButton("CANCEL", 300, 340, 100, 35, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            dispose();
            new SecurityOptionsDialog(null, ctx).setVisible(true);
            return;
        }

        if (e.getSource() == resetButton) {
            String selectedQuestion = (String) recoveryQuestionBox.getSelectedItem();
            String answer = answerField.getText().trim();
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (recoveryQuestionBox.getSelectedIndex() == 0 || answer.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            resetPassword(selectedQuestion, answer, newPass);
        }
    }

    private void resetPassword(String selectedQuestion, String answer, String newPass) {
        String checkQuery = "SELECT recoveryQuestion, recoveryAnswer FROM accounts WHERE account_no = ?";
        String updateQuery = "UPDATE accounts SET password = ? WHERE account_no = ?";

        try (DatabaseConnection c = new DatabaseConnection();
             PreparedStatement psCheck = c.getConnection().prepareStatement(checkQuery)) {

            psCheck.setLong(1, ctx.getAccountNo());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    String correctQuestion = rs.getString("recoveryQuestion");
                    String correctAnswer = rs.getString("recoveryAnswer");

                    if (!correctQuestion.equalsIgnoreCase(selectedQuestion)) {
                        JOptionPane.showMessageDialog(this, "Incorrect recovery question", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!correctAnswer.equalsIgnoreCase(answer)) {
                        JOptionPane.showMessageDialog(this, "Incorrect recovery answer", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Account not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (PreparedStatement psUpdate = c.getConnection().prepareStatement(updateQuery)) {
                psUpdate.setString(1, newPass);
                psUpdate.setLong(2, ctx.getAccountNo());
                psUpdate.executeUpdate();

                JOptionPane.showMessageDialog(this, "Password reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
