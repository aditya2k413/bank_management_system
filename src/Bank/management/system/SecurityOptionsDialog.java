package Bank.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SecurityOptionsDialog extends JDialog implements ActionListener {
    private final UserContext ctx;
    JButton changePinBtn, changePassBtn, forgotPinBtn, forgotPassBtn;

    public SecurityOptionsDialog(JFrame parent, UserContext ctx) {
        super(parent, "Security Options", true);
        this.ctx = ctx;

        setLayout(null);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Color.WHITE);

        UIUtils.createLabel("Choose an Option", 120, 40, 200, 30, UIUtils.Font2, this);

         changePinBtn = UIUtils.createButton("Change PIN", 120, 100, 160, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
         changePassBtn = UIUtils.createButton("Change Password", 120, 150, 160, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
         forgotPinBtn = UIUtils.createButton("Forgot PIN", 120, 200, 160, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
         forgotPassBtn = UIUtils.createButton("Forgot Password", 120, 250, 160, 40, UIUtils.Font6, Color.BLACK, Color.LIGHT_GRAY, this, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(e.getSource()==changePinBtn) {
            new PinChange(this, ctx).setVisible(true);
        } else if(e.getSource()==changePassBtn) {
            new ChangePassword(this, ctx).setVisible(true);
        } else if(e.getSource()==forgotPinBtn) {
            new ForgotPin(this,ctx).setVisible(true);
        } else if(e.getSource()==forgotPassBtn) {
            new ForgotPassword(this,ctx).setVisible(true);
        }
    }
}
