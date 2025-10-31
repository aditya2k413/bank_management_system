package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Signup2 extends JFrame implements ActionListener {
    private long formno;
    JLabel heading,religion,category,income,education,occupation,pan,aadhar,seniorcitizen,existingaccount,disclaimer;
    JRadioButton yes, no, yes1, no1;
    ButtonGroup seniorGroup, existingGroup;
    JButton next,clear,back,cancle;
    JTextField panText,aadharText;
    JComboBox religionBox,categoryBox,incomeBox,educationBox,occupationBox;

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
    public Signup2(long formno) {
        this.formno = formno;
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        UIUtils.setTitleAndVisibility(this, " APPLICATION FORM PAGE 2", 850, 800, 350, 10);

        disclaimer =UIUtils.createLabel("Disclaimer: Please ensure all information is correct. Once submitted, data cannot be changed.", 50, 720, 750, 30, UIUtils.Font6, this);
        disclaimer.setForeground(Color.RED);
        disclaimer.setHorizontalAlignment(SwingConstants.CENTER);

        heading = UIUtils.createLabel("Page 2: Additional Details", 0, 80, 850, 30, UIUtils.Font2, this);
        heading.setForeground(new java.awt.Color(31, 97, 66));
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        religion = UIUtils.createLabel("Religion:", 100, 140, 200, 30, UIUtils.Font3, this);
        String[] valReligion = {"Select Religion","Hindu", "Muslim", "Sikh", "Christian", "Other"};
        religionBox = UIUtils.createComboBox(valReligion, 300, 140, 400, 30, UIUtils.Font6, this);

        category = UIUtils.createLabel("Category:", 100, 190, 200, 30, UIUtils.Font3, this);
        String[] valCategory = {"Select Category","General", "OBC", "SC", "ST", "Other"};
        categoryBox = UIUtils.createComboBox(valCategory, 300, 190, 400, 30, UIUtils.Font6, this);

        income = UIUtils.createLabel("Income:", 100, 240, 200, 30, UIUtils.Font3, this);
        String[] valIncome = {"Select Income","Null", "<1,50,000", "<2,50,000", "<5,00,000", "Upto 10,00,000"};
        incomeBox = UIUtils.createComboBox(valIncome, 300, 240, 400, 30, UIUtils.Font6, this);

        education = UIUtils.createLabel("<html>Educational<br>Qualification:</html>", 100, 290, 200, 40, UIUtils.Font3, this);
        String[] valEducation = {"Select Educational Qualification","Non-Graduate", "Graduate", "Post-Graduate", "Doctrate", "Other"};
        educationBox = UIUtils.createComboBox(valEducation, 300, 290, 400, 30, UIUtils.Font6, this);

        occupation = UIUtils.createLabel("Occupation:", 100, 350, 200, 30, UIUtils.Font3, this);
        String[] valOccupation = {"Select Occupation","Salaried", "Self-Employed", "Business", "Student", "Retired", "Unemployed", "Other"};
        occupationBox = UIUtils.createComboBox(valOccupation, 300, 350, 400, 30, UIUtils.Font6, this);

        pan = UIUtils.createLabel("PAN Number:", 100, 400, 200, 30, UIUtils.Font3, this);
        panText = UIUtils.createTextField(this, 300, 400, 400, 30, UIUtils.Font4);

        aadhar = UIUtils.createLabel("Aadhar Number:", 100, 450, 200, 30, UIUtils.Font3, this);
        aadharText = UIUtils.createTextField(this, 300, 450, 400, 30, UIUtils.Font4);

        seniorcitizen = UIUtils.createLabel("Senior Citizen:", 100, 500, 200, 30, UIUtils.Font3, this);
        yes=UIUtils.createRadioButton("Yes", 300, 500, 100, 30, this);
        no=UIUtils.createRadioButton("No", 450, 500, 100, 30, this);
        seniorGroup = new ButtonGroup();
        seniorGroup.add(yes);
        seniorGroup.add(no);

        existingaccount = UIUtils.createLabel("Existing Account:", 100, 550, 200, 30, UIUtils.Font3, this);
        yes1=UIUtils.createRadioButton("Yes", 300, 550, 100, 30, this);
        no1=UIUtils.createRadioButton("No", 450, 550, 100, 30, this);
        existingGroup = new ButtonGroup();
        existingGroup.add(yes1);
        existingGroup.add(no1);

        next = UIUtils.createButton("NEXT", 600, 650, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        clear= UIUtils.createButton("CLEAR", 120, 650, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        back= UIUtils.createButton("BACK", 350, 650, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        cancle = UIUtils.createButton("CANCEL", 600, 700, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        loadDataIfExists();
    }

    private boolean isSaveData() {
        String panno = panText.getText();
        String adharno = aadharText.getText();
        String senior_citizen = yes.isSelected() ? "Yes" : no.isSelected() ? "No" : null;
        String existing_account = yes1.isSelected() ? "Yes" : no1.isSelected() ? "No" : null;

        String religion = (String) religionBox.getSelectedItem();
        String category = (String) categoryBox.getSelectedItem();
        String income = (String) incomeBox.getSelectedItem();
        String education = (String) educationBox.getSelectedItem();
        String occupation = (String) occupationBox.getSelectedItem();

        if (panno.isEmpty() || adharno.isEmpty() || senior_citizen == null || existing_account == null
                || religion.equals("Select Religion") || category.equals("Select Category")
                || income.equals("Select Income") || education.equals("Select Educational Qualification")
                || occupation.equals("Select Occupation")) {
            return false;
        }
        String query;
        boolean isNew;
        try(DatabaseConnection c = new DatabaseConnection()) {
            c.getConnection().setAutoCommit(false);
            String checkQuery = "SELECT * FROM signup2 WHERE formno=?";
            try(PreparedStatement checkPs = c.getConnection().prepareStatement(checkQuery)) {
                checkPs.setLong(1, formno);
                try (ResultSet rs = checkPs.executeQuery()) {
                    isNew = !rs.next();
                }
            }
            if (isNew) query = "INSERT INTO signup2 (religion, category, income, education, occupation, panno, adharno, seniorcitizen, existingaccount, formno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            else query = "UPDATE signup2 SET religion=?, category=?, income=?, education=?, occupation=?, panno=?, adharno=?, seniorcitizen=?, existingaccount=? WHERE formno=?";
            try(PreparedStatement ps = c.getConnection().prepareStatement(query)) {
                    ps.setString(1, religion);
                    ps.setString(2, category);
                    ps.setString(3, income);
                    ps.setString(4, education);
                    ps.setString(5, occupation);
                    ps.setString(6, panno);
                    ps.setString(7, adharno);
                    ps.setString(8, senior_citizen);
                    ps.setString(9, existing_account);
                    ps.setLong(10, formno);
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        c.getConnection().commit();
                        return true;
                    } else {
                        c.getConnection().rollback();
                        return false;
                    }
                }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void loadDataIfExists() {
        String query = "SELECT * FROM signup2 WHERE formno=?";
        try (DatabaseConnection c = new DatabaseConnection();
             PreparedStatement ps = c.getConnection().prepareStatement(query)) {
            ps.setLong(1, formno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    religionBox.setSelectedItem(rs.getString("religion"));
                    categoryBox.setSelectedItem(rs.getString("category"));
                    incomeBox.setSelectedItem(rs.getString("income"));
                    educationBox.setSelectedItem(rs.getString("education"));
                    occupationBox.setSelectedItem(rs.getString("occupation"));
                    panText.setText(rs.getString("panno"));
                    aadharText.setText(rs.getString("adharno"));

                    String senior = rs.getString("seniorcitizen");
                    if ("Yes".equals(senior)) yes.setSelected(true);
                    else if ("No".equals(senior)) no.setSelected(true);

                    String existing = rs.getString("existingaccount");
                    if ("Yes".equals(existing)) yes1.setSelected(true);
                    else if ("No".equals(existing)) no1.setSelected(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clear) {
            panText.setText("");
            aadharText.setText("");
            existingGroup.clearSelection();
            seniorGroup.clearSelection();
            religionBox.setSelectedIndex(0);
            categoryBox.setSelectedIndex(0);
            incomeBox.setSelectedIndex(0);
            educationBox.setSelectedIndex(0);
            occupationBox.setSelectedIndex(0);
        } else if (e.getSource() == next) {
            if (isSaveData()) {
                dispose();
                new Signup3(formno).setVisible(true);;
            } else {
                JOptionPane.showMessageDialog(this, "Please Try Again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else if (e.getSource()==back) {
            if(isSaveData()) {
                dispose();
                new Signup(formno).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please Try Again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource()==cancle) {
            if (del(formno)) {
                dispose();
                new Login().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error occurred while cancelling the application.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
}
