package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Signup3 extends JFrame implements ActionListener {
    JLabel heading, accountType , services, passwordLabel, confirmPasswordLabel ,recoveryquestionlabel,recoveryanswerlabel,pinLabel,confirmPinLabel  ;
    JCheckBox  internetCheckBox, mobileCheckBox, emailCheckBox, chequeCheckBox, eStatementCheckBox, atmCheckBox;
    JPasswordField passwordField, confirmPasswordField,pinField,confirmPinField  ;
    JRadioButton savingsRadioButton, fixedDepositRadioButton, currentRadioButton, recurringDepositRadioButton;
    JButton submitButton, cancelButton, clearButton;
    JComboBox Recoveryquestion;
    JTextField recoveryAnswerField;
    ButtonGroup accountTypeComboBox;

    long formno;
    Signup3(long formno) {
        this.formno = formno;
        setLayout(null);
        getContentPane().setBackground(java.awt.Color.WHITE);
        UIUtils.setTitleAndVisibility(this, "APPLICATION FORM PAGE 3", 850, 800, 350, 10);

        heading = UIUtils.createLabel("Page 3: Account Details", 0, 30, 850, 30, UIUtils.Font2, this);
        heading.setForeground(new java.awt.Color(31, 97, 66));
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        accountType = UIUtils.createLabel("Account Type:", 100, 80, 200, 30, UIUtils.Font3, this);
        savingsRadioButton = UIUtils.createRadioButton("Savings Account", 100, 120, 200, 30, this);
        currentRadioButton = UIUtils.createRadioButton("Current Account", 320, 120, 200, 30, this);
        fixedDepositRadioButton = UIUtils.createRadioButton("Fixed Deposit Account", 100, 160, 200, 30, this);
        recurringDepositRadioButton = UIUtils.createRadioButton("Recurring Deposit Account", 320, 160, 200, 30, this);

        accountTypeComboBox = new ButtonGroup();
        accountTypeComboBox.add(savingsRadioButton);
        accountTypeComboBox.add(currentRadioButton);
        accountTypeComboBox.add(fixedDepositRadioButton);
        accountTypeComboBox.add(recurringDepositRadioButton);

        services = UIUtils.createLabel("Services Required:", 100, 200, 200, 30, UIUtils.Font3, this);
        internetCheckBox = UIUtils.createCheckBox("Internet Banking", 100, 240, 200, 30, this);
        mobileCheckBox = UIUtils.createCheckBox("Mobile Banking", 320, 240, 200, 30, this);
        emailCheckBox = UIUtils.createCheckBox("Email Alerts", 100, 280, 200, 30, this);
        chequeCheckBox = UIUtils.createCheckBox("Cheque Book", 320, 280, 200, 30, this);
        atmCheckBox = UIUtils.createCheckBox("ATM Card", 100, 320, 200, 30, this);
        eStatementCheckBox = UIUtils.createCheckBox("E-Statement", 320, 320, 200, 30, this);

        passwordLabel = UIUtils.createLabel("Password:", 100, 360, 200, 30, UIUtils.Font3, this);
        passwordField = UIUtils.createPasswordField(this, 320, 360, 300, 30, UIUtils.Font3);

        pinLabel= UIUtils.createLabel("PIN:", 100, 400, 200, 30, UIUtils.Font3, this);
        pinField = UIUtils.createPasswordField(this, 320, 400, 300, 30, UIUtils.Font3);

        confirmPinLabel= UIUtils.createLabel("Confirm PIN:", 100, 440, 200, 30, UIUtils.Font3, this);
        confirmPinField = UIUtils.createPasswordField(this, 320, 440, 300, 30, UIUtils.Font3);

        confirmPasswordLabel = UIUtils.createLabel("Confirm Password:", 100, 480, 200, 30, UIUtils.Font3, this);
        confirmPasswordField = UIUtils.createPasswordField(this, 320, 480, 300, 30, UIUtils.Font3);

        recoveryquestionlabel = UIUtils.createLabel("Recovery Question:", 100, 520, 200, 30, UIUtils.Font3, this);
        String[] questions = {"Select a recovery question","What is your pet's name?", "What is your mother's maiden name?", "What was your first car?", "What elementary school did you attend?", "What is the name of the town where you were born?"};
        Recoveryquestion = UIUtils.createComboBox(questions, 320, 520, 300, 30, UIUtils.Font6, this);
        Recoveryquestion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent) {
                    ((JComponent)c).setToolTipText(value.toString());
                }
                return c;
            }
        });

        recoveryanswerlabel = UIUtils.createLabel("Recovery Answer:", 100, 560, 200, 30, UIUtils.Font3, this);
        recoveryAnswerField = UIUtils.createTextField(this, 320, 560, 300, 30, UIUtils.Font6);

        submitButton = UIUtils.createButton("Submit", 100, 620, 120, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        clearButton = UIUtils.createButton("Clear", 320, 620, 120, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        cancelButton = UIUtils.createButton("Cancel", 540, 620, 120, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        }


    private long generateAccountNumber() {
        long accountNumber;
        String query = "SELECT * FROM accounts WHERE account_no=?";
        try( DatabaseConnection c = new DatabaseConnection()) {
            while (true) {
                accountNumber = 1000000000L + (long) (Math.random() * 9000000000L);
                try (PreparedStatement ps = c.getConnection().prepareStatement(query)) {
                    ps.setLong(1, accountNumber);
                    try(ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            break;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return accountNumber;
    }

    private  boolean del(long formno){
        String Del_query = "delete from signup where formno = ?";
        try( DatabaseConnection c = new DatabaseConnection();
             PreparedStatement p1 = c.getConnection().prepareStatement(Del_query)) {
            c.getConnection().setAutoCommit(false);
            p1.setLong(1,formno);
            int rows_affected=p1.executeUpdate();
            if (rows_affected>0){
                c.getConnection().commit();
                return true;
            }else {
                c.getConnection().rollback();
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == clearButton) {
            accountTypeComboBox.clearSelection();
            internetCheckBox.setSelected(false);
            mobileCheckBox.setSelected(false);
            emailCheckBox.setSelected(false);
            chequeCheckBox.setSelected(false);
            atmCheckBox.setSelected(false);
            eStatementCheckBox.setSelected(false);
            passwordField.setText("");
            confirmPasswordField.setText("");
            pinField.setText("");
            confirmPinField.setText("");
            Recoveryquestion.setSelectedIndex(0);
            recoveryAnswerField.setText("");
        } else if(e.getSource() == cancelButton) {
            boolean deleted = del(formno);
            if(deleted){
                JOptionPane.showMessageDialog(null, "Your application is cancelled successfully","Success",JOptionPane.INFORMATION_MESSAGE);
                dispose();
                setVisible(false);
                new Login();

            } else {
                JOptionPane.showMessageDialog(null, "Error in cancelling your application","Error",JOptionPane.ERROR_MESSAGE);
            }
        } else if(e.getSource()==submitButton){

            long accountNumber = generateAccountNumber();
            if (accountNumber == -1) {
                JOptionPane.showMessageDialog(null,"Error in generating account no.","Error",JOptionPane.ERROR_MESSAGE);
            } else {
                String acc_type = null;
                if (savingsRadioButton.isSelected()) acc_type = "Savings Account";
                else if (currentRadioButton.isSelected()) acc_type = "Current Account";
                else if (fixedDepositRadioButton.isSelected()) acc_type = "Fixed Deposit  Account";
                else if (recurringDepositRadioButton.isSelected()) acc_type = "Recurring Deposit Account";
                String services = "";
                if (internetCheckBox.isSelected()) services += " Internet Banking ";
                if (mobileCheckBox.isSelected()) services += " Mobile Banking ";
                if (emailCheckBox.isSelected()) services += " Email Alerts ";
                if (chequeCheckBox.isSelected()) services += " Cheque Book ";
                if (atmCheckBox.isSelected()) services += " ATM Card ";
                if (eStatementCheckBox.isSelected()) services += " E-Statement ";
                String password = String.valueOf(passwordField.getPassword());
                String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
                String pin = String.valueOf(pinField.getPassword());
                String confirmPin = String.valueOf(confirmPinField.getPassword());
                String recoveryQuestion = (String) Recoveryquestion.getSelectedItem();
                String recoveryAnswer = recoveryAnswerField.getText();

                if (acc_type == null || password.isEmpty() || confirmPassword.isEmpty() || pin.isEmpty() || confirmPin.isEmpty() || Recoveryquestion.getSelectedIndex() == 0 || recoveryAnswer.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                if(!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;

                }

                if(!pin.equals(confirmPin)) {
                    JOptionPane.showMessageDialog(this, "PINs do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String query = "insert into accounts (accountType, services, password, pin, recoveryQuestion, recoveryAnswer ,account_no,formno,balance) values (?,?,?,?,?,?,?,?,?)";
                try(DatabaseConnection c = new DatabaseConnection();
                    PreparedStatement p = c.getConnection().prepareStatement(query)) {
                    c.getConnection().setAutoCommit(false);
                    p.setString(1, acc_type);
                    p.setString(2, services);
                    p.setString(3, password);
                    p.setString(4, pin);
                    p.setString(5, recoveryQuestion);
                    p.setString(6, recoveryAnswer);
                    p.setLong(7, accountNumber);
                    p.setLong(8, formno);
                    p.setInt(9,0);

                    int rows_affected = p.executeUpdate();
                    if (rows_affected > 0){
                        c.getConnection().commit();
                        JOptionPane.showMessageDialog(this,"Account generated successfully \n Your Account no. is "+accountNumber,"Success",JOptionPane.INFORMATION_MESSAGE);
                        new Login();
                        setVisible(false);
                    }
                    else{
                        c.getConnection().rollback();
                        JOptionPane.showMessageDialog(this,"Error in creating account","Error",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog((null), "Error in creating account", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }


        }

    }

}


