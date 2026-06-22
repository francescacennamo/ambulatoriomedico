package Boundary;
import Controller.LoginController;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm {
    private JPanel contentPane;
    private JTextField textEmail;
    private JPasswordField passwordField;
    private JButton passwordDimenticataButton;
    private JButton accediButton;
    private JPanel lbllogo;
    private JLabel lblLogo;


    public LoginForm() {
        accediButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 String email = textEmail.getText();
                 String password = passwordField.getText();

                 LoginController controller = new LoginController();

                 boolean ok = controller.login(email, password);

                 if (ok) {
                     JOptionPane.showMessageDialog(null,
                             "Login Effettuato");
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "Credenziali non valide");
                        }
                    }
                });
            }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Login Form");
        frame.setContentPane(new LoginForm().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}