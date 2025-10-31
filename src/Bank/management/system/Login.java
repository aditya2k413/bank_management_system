package Bank.management.system;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Login extends JFrame implements ActionListener {
    JButton signInButton, clearButton, signUpButton;
    JTextField tf1;
    JPasswordField pf2;
    JLabel welcomeLabel,user_name,password;

    Login(){
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // Load image safely
        java.net.URL imgUrl = getClass().getClassLoader().getResource("icon/frameimg.jpg");
        JLabel imgLabel;
        if (imgUrl != null) {//if true  image resource was found
            ImageIcon i1 = new ImageIcon(imgUrl);
            Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            // to scale image we need to create image class object
            // using image.scale_smooth instead of Image.SCALE_DEFAULT for better scaling quality.
            ImageIcon i3 = new ImageIcon(i2);
            //This is necessary because JLabel requires an ImageIcon to display images,
            // and you need to convert the scaled image back into an ImageIcon after resizing.
            imgLabel = new JLabel(i3);
        } else {
            imgLabel = new JLabel("Image not found");
        }
        imgLabel.setBounds(130, 20, 100, 100);
        add(imgLabel);

        welcomeLabel = UIUtils.createLabel("Floyd National Bank", 240, 45, 500, 50, UIUtils.Font1, this);
        welcomeLabel.setForeground(new Color(0, 204, 99));
        user_name = UIUtils.createLabel("User id", 120, 150, 150, 40, UIUtils.Font3, this);
        tf1 = UIUtils.createTextField(this, 300, 150, 250, 40, UIUtils.Font4);
        password = UIUtils.createLabel("Password", 120, 220, 150, 40, UIUtils.Font3, this);
        pf2 = UIUtils.createPasswordField(this, 300, 220, 250, 40, UIUtils.Font4);
        signInButton = UIUtils.createButton("SIGN IN", 300, 300, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        clearButton = UIUtils.createButton("CLEAR", 450, 300, 100, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        signUpButton = UIUtils.createButton("SIGN UP", 300, 350, 250, 30, UIUtils.Font6, Color.BLACK, Color.WHITE, this, this);
        UIUtils.setTitleAndVisibility(this, "Floyd National Bank", 800, 500, 400, 140);
    }

    public static void main(String[] args) {
        new Login();
    }

    @Override// this method tells which button is selected and what task to perform
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==clearButton){
            tf1.setText("");
            pf2.setText("");
        }
        else if(e.getSource()==signUpButton){
            setVisible(false);// to close login page
            new Signup().setVisible(true);
        }
        else if(e.getSource() == signInButton){
            String user = String.valueOf(tf1.getText());
            String pass = String.valueOf(pf2.getPassword());
            if (user.isEmpty()||pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Please enter valid details","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            verify(user,pass);


        }
    }
    private void  verify(String gm, String pass){
        String q = " SELECT signup.name, accounts.account_no, signup.formno FROM signup  JOIN accounts  ON signup.formno = accounts.formno WHERE signup.email = ? AND accounts.password = ? ";
        try(DatabaseConnection c = new DatabaseConnection();
        PreparedStatement ps = c.getConnection().prepareStatement(q)) {
            ps.setString(1, gm);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                long accno = rs.getLong("account_no");
                long formno = rs.getLong("formno");
                UserContext ctx = new UserContext(formno, accno, name);
                setVisible(false);
                new Transaction(ctx);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}