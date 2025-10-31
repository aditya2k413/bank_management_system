package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ForgotPin extends JDialog implements ActionListener {
    private final UserContext ctx;
    private final JDialog parentDialog;

    private final JComboBox<String> recoveryQuestionBox;
    private final JTextField answerField;
    private final JPasswordField newPinField, confirmPinField;
    private final JButton resetButton, cancelButton;

    public ForgotPin(JDialog parentDialog, UserContext ctx) {
        super(parentDialog, "Forgot PIN", true);
        this.ctx = ctx;
        this.parentDialog = parentDialog;

        setLayout(null);
        setSize(550, 450);
        setLocationRelativeTo(parentDialog);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = UIUtils.createLabel("Reset Your PIN", 0, 40, getWidth(), 40, UIUtils.Font1, this);
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

        UIUtils.createLabel("New PIN:", 80, 220, 200, 30, UIUtils.Font3, this);
        newPinField = UIUtils.createPasswordField(this, 260, 220, 250, 30, UIUtils.Font4);

        UIUtils.createLabel("Confirm PIN:", 80, 270, 200, 30, UIUtils.Font3, this);
        confirmPinField = UIUtils.createPasswordField(this, 260, 270, 250, 30, UIUtils.Font4);

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
            String newPin = new String(newPinField.getPassword());
            String confirmPin = new String(confirmPinField.getPassword());

            if (recoveryQuestionBox.getSelectedIndex() == 0 || answer.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(this, "PINs do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resetPin(selectedQuestion, answer, newPin);
        }
    }

    private void resetPin(String selectedQuestion, String answer, String newPin) {
        String checkQuery = "SELECT recoveryQuestion, recoveryAnswer FROM accounts WHERE account_no = ?";
        String updateQuery = "UPDATE accounts SET pin = ? WHERE account_no = ?";

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
                psUpdate.setString(1, newPin);
                psUpdate.setLong(2, ctx.getAccountNo());
                psUpdate.executeUpdate();

                JOptionPane.showMessageDialog(this, "PIN reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error resetting PIN: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
