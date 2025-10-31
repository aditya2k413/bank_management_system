package Bank.management.system;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class UIUtils  {
    public static final Font Font1 = new Font("Arial", Font.BOLD, 38);
    public static final Font Font2 = new Font("Arial", Font.BOLD, 22);
    public static final Font Font3 = new Font("Arial", Font.BOLD, 20);
    public static final Font Font4 = new Font("Arial", Font.BOLD, 18);
    public static final Font Font6 = new Font("Arial", Font.PLAIN, 14);

    public static void setTitleAndVisibility(JFrame frame, String title, int width, int height, int locX, int locY){
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setLocation(locX, locY);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static JCheckBox createCheckBox(String text, int x, int y, int width, int height, Container container) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setBounds(x, y, width, height);
        checkBox.setBackground(Color.WHITE);
        container.add(checkBox);
        return checkBox;
    }


    public static JComboBox createComboBox(String[] items, int x, int y, int width, int height, Font font, Container container) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(font);
        comboBox.setBounds(x, y, width, height);
        container.add(comboBox);
        return comboBox;
    }

    public static JTextField createTextField(Container container, int x, int y, int width, int height, Font font) {
        JTextField tf = new JTextField();
        tf.setFont(font);
        tf.setBounds(x, y, width, height);
        container.add(tf);
        return tf;
    }

    public static JLabel createLabel(String text, int x, int y, int width, int height, Font font, Container container) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setBounds(x, y, width, height);
        container.add(lbl);
        return lbl;
    }

    public static JRadioButton createRadioButton(String text, int x, int y, int width, int height, Container container) {
        JRadioButton rb = new JRadioButton(text);
        rb.setBounds(x, y, width, height);
        rb.setBackground(Color.WHITE);
        container.add(rb);
        return rb;
    }

    public static JPasswordField createPasswordField(Container container, int x, int y, int width, int height, Font font) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(font);
        pf.setBounds(x, y, width, height);
        container.add(pf);
        return pf;
    }



    public static JButton createButton(String text, int x, int y, int width, int height, Font font, Color bgColor, Color fgColor, ActionListener listener, Container container) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setBounds(x, y, width, height);
        btn.addActionListener(listener);
        container.add(btn);
        return btn;
    }

}

