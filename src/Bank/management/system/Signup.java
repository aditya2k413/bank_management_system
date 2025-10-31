package Bank.management.system;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class Signup extends JFrame implements ActionListener {

    //I have initialized them out of constructor so that they can be used out of constructor
    JLabel l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12;
    JRadioButton male, female, married, otherGender, unmarried, divorced, widowed;
    ButtonGroup genderGroup, maritalGroup;
    JButton b, cb, cancle;
    JTextField t1, t2, t3, t4, t5, t6, t7;
    JDateChooser DOB;
    long formno = 0;

    Signup() {
        this(0);
    }

    Signup(long formno) {
        this.formno = formno;

        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        UIUtils.setTitleAndVisibility(this, " APPLICATION FORM PAGE 1", 850, 800, 350, 10);


        l1 = UIUtils.createLabel("Application Form", 0, 20, 850, 40, UIUtils.Font1, this);
        l1.setForeground(new Color(0, 204, 99));
        l1.setHorizontalAlignment(SwingConstants.CENTER);

        l2 = UIUtils.createLabel("Page 1: Personal Details", 0, 80, 850, 30, UIUtils.Font2, this);
        l2.setForeground(new Color(31, 97, 66));
        l2.setHorizontalAlignment(SwingConstants.CENTER);

        l3 = UIUtils.createLabel("Name:", 100, 140, 200, 30, UIUtils.Font3, this);
        l4 = UIUtils.createLabel("Father's Name:", 100, 190, 200, 30, UIUtils.Font3, this);
        l5 = UIUtils.createLabel("Date of Birth:", 100, 240, 200, 30, UIUtils.Font3, this);
        l6 = UIUtils.createLabel("Gender:", 100, 290, 200, 30, UIUtils.Font3, this);
        l7 = UIUtils.createLabel("Email Address:", 100, 340, 200, 30, UIUtils.Font3, this);
        l8 = UIUtils.createLabel("Marital Status:", 100, 390, 200, 30, UIUtils.Font3, this);
        l9 = UIUtils.createLabel("Address:", 100, 440, 200, 30, UIUtils.Font3, this);
        l10 = UIUtils.createLabel("City:", 100, 490, 200, 30, UIUtils.Font3, this);
        l11 = UIUtils.createLabel("Pin Code:", 100, 540, 200, 30, UIUtils.Font3, this);
        l12 = UIUtils.createLabel("State:", 100, 590, 200, 30, UIUtils.Font3, this);


        t1 = UIUtils.createTextField(this, 300, 140, 400, 30, UIUtils.Font4);
        t2 = UIUtils.createTextField(this, 300, 190, 400, 30, UIUtils.Font4);
        t3 = UIUtils.createTextField(this, 300, 340, 400, 30, UIUtils.Font4);
        t4 = UIUtils.createTextField(this, 300, 440, 400, 30, UIUtils.Font4);
        t5 = UIUtils.createTextField(this, 300, 490, 400, 30, UIUtils.Font4);
        t6 = UIUtils.createTextField(this, 300, 540, 400, 30, UIUtils.Font4);
        t7 = UIUtils.createTextField(this, 300, 590, 400, 30, UIUtils.Font4);

        DOB = new JDateChooser();
        DOB.setBounds(300, 240, 400, 30);
        add(DOB);

        male = UIUtils.createRadioButton("Male", 300, 290, 100, 30, this);
        female = UIUtils.createRadioButton("Female", 450, 290, 100, 30, this);
        otherGender = UIUtils.createRadioButton("Other", 600, 290, 100, 30, this);

        married = UIUtils.createRadioButton("Married", 300, 390, 100, 30, this);
        unmarried = UIUtils.createRadioButton("Unmarried", 410, 390, 100, 30, this);
        divorced = UIUtils.createRadioButton("Divorced", 520, 390, 100, 30, this);
        widowed = UIUtils.createRadioButton("Widowed", 630, 390, 100, 30, this);

        genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genderGroup.add(otherGender);

        maritalGroup = new ButtonGroup();
        maritalGroup.add(married);
        maritalGroup.add(unmarried);
        maritalGroup.add(divorced);
        maritalGroup.add(widowed);

        b = UIUtils.createButton("Next", 620, 660, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        cb = UIUtils.createButton("Clear", 100, 660, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        cancle = UIUtils.createButton("cancle", 360, 660, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        loadDataIfExists();

    }

    private void loadDataIfExists() {
        if (formno == 0) return;
        String query = "SELECT * FROM signup WHERE formno = ?";
        try (DatabaseConnection c = new DatabaseConnection();
             PreparedStatement ps = c.getConnection().prepareStatement(query)) {
            ps.setLong(1, formno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t1.setText(rs.getString("name"));
                    t2.setText(rs.getString("father_name"));
                    Date dob = rs.getDate("dob");
                    if (dob != null) {
                        DOB.setDate(new java.util.Date(dob.getTime()));
                    }
                    String gender = rs.getString("gender");
                    if ("Male".equalsIgnoreCase(gender)) male.setSelected(true);
                    else if ("Female".equalsIgnoreCase(gender)) female.setSelected(true);
                    else if ("Other".equalsIgnoreCase(gender)) otherGender.setSelected(true);
                    t3.setText(rs.getString("email"));
                    String maritalStatus = rs.getString("marital_status");
                    if ("Married".equalsIgnoreCase(maritalStatus)) married.setSelected(true);
                    else if ("Unmarried".equalsIgnoreCase(maritalStatus)) unmarried.setSelected(true);
                    else if ("Divorced".equalsIgnoreCase(maritalStatus)) divorced.setSelected(true);
                    else if ("Widowed".equalsIgnoreCase(maritalStatus)) widowed.setSelected(true);
                    t4.setText(rs.getString("address"));
                    t5.setText(rs.getString("city"));
                    t6.setText(String.valueOf(rs.getInt("pincode")));
                    t7.setText(rs.getString("state"));
                }
            }
        } catch(SQLException e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource()==b){
            handelsave();
        } else if (ae.getSource()==cb) {
            t1.setText("");
            t2.setText("");
            t3.setText("");
            t4.setText("");
            t5.setText("");
            t6.setText("");
            t7.setText("");
            // Clear date chooser
            DOB.setDate(null);
            // Clear radio button selections
            genderGroup.clearSelection();
            maritalGroup.clearSelection();

        } else if (ae.getSource()==cancle) {
            if (formno != 0) {
                del(formno);
            } else {
                new Login().setVisible(true);
                dispose();
            }

        }
    }

    private void del(long formno){
        String Del_query = "delete from signup where formno = ?";
        try( DatabaseConnection c = new DatabaseConnection();
             PreparedStatement p1 = c.getConnection().prepareStatement(Del_query)) {
            c.getConnection().setAutoCommit(false);
            p1.setLong(1,formno);
            int rows_affected=p1.executeUpdate();
            if (rows_affected>0){
                c.getConnection().commit();
                new Login().setVisible(true);
                dispose();
            }else {
                c.getConnection().rollback();
                JOptionPane.showMessageDialog(null, "Failed to delete data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handelsave() {
            String name = t1.getText();
            String father_name = t2.getText();
            String email = t3.getText();
            String address = t4.getText();
            String city = t5.getText();
            String pincodeText = t6.getText();
            String state = t7.getText();
            java.util.Date date = DOB.getDate();

            String gender = null;
            if(male.isSelected())gender = "Male";
            else if(female.isSelected()) gender = "Female";
            else if(otherGender.isSelected())gender = "Other";

            String marital_status = null;
            if(married.isSelected()) marital_status = "Married";
            else if(unmarried.isSelected()) marital_status = "Unmarried";
            else if(divorced.isSelected()) marital_status = "Divorced";
            else if(widowed.isSelected()) marital_status = "Widowed";

            if (name.isEmpty() || father_name.isEmpty() ||  gender == null || email.isEmpty() || marital_status == null || address.isEmpty() || city.isEmpty() || pincodeText.isEmpty() || date==null) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            int pincode;
            try{
                pincode = Integer.parseInt(pincodeText);
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(this, "Pin Code must be a number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String insertQuery = "INSERT INTO signup (name, father_name, dob, gender, email, marital_status, address, city, pincode, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String updateQuery = "UPDATE signup SET name=?, father_name=?, dob=?, gender=?, email=?, marital_status=?, address=?, city=?, pincode=?, state=? WHERE formno=?";

            try(DatabaseConnection c= new DatabaseConnection()) {
                c.getConnection().setAutoCommit(false);
                if(formno==0){
                    try(PreparedStatement pstmt =c.getConnection().prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS)) {
                        pstmt.setString(1, name);
                        pstmt.setString(2, father_name);
                        pstmt.setDate(3, sqlDate);
                        pstmt.setString(4, gender);
                        pstmt.setString(5, email);
                        pstmt.setString(6, marital_status);
                        pstmt.setString(7, address);
                        pstmt.setString(8, city);
                        pstmt.setInt(9, pincode);
                        pstmt.setString(10, state);

                        int rows= pstmt.executeUpdate();
                        if (rows>0){
                            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    formno = generatedKeys.getLong(1);
                                }
                                c.getConnection().commit();
                                JOptionPane.showMessageDialog(this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                dispose();
                                new Signup2(formno).setVisible(true);
                            }
                        }else {
                            c.getConnection().rollback();
                            JOptionPane.showMessageDialog(this, "Failed to save data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    try(PreparedStatement pstmt = c.getConnection().prepareStatement(updateQuery,Statement.RETURN_GENERATED_KEYS)) {
                        pstmt.setString(1, name);
                        pstmt.setString(2, father_name);
                        pstmt.setDate(3, sqlDate);
                        pstmt.setString(4, gender);
                        pstmt.setString(5, email);
                        pstmt.setString(6, marital_status);
                        pstmt.setString(7, address);
                        pstmt.setString(8, city);
                        pstmt.setInt(9, pincode);
                        pstmt.setString(10, state);
                        pstmt.setLong(11, formno);
                        int row = pstmt.executeUpdate();
                        if(row>0) {
                            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    formno = generatedKeys.getLong(1);
                                }
                                c.getConnection().commit();
                                JOptionPane.showMessageDialog(this, "Data updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                dispose();
                                new Signup2(formno).setVisible(true);
                            }
                        }
                        else {
                            c.getConnection().rollback();
                            JOptionPane.showMessageDialog(this, "Failed to save data. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
}
