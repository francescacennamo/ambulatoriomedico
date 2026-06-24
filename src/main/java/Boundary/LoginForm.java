package Boundary;

import Control.LoginController;
import Entity.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm {

    private JPanel contentPane;
    private JTextField textEmail;
    private JPasswordField passwordField;
    private JButton accediButton;
    private JButton passwordDimenticataButton;
    private JLabel PasswordLabel;
    private JPanel mainPanel;

    public LoginForm() {

        accediButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

    }

    private void login() {

        String email = textEmail.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Compilare tutti i campi",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        LoginController controller = new LoginController();

        Utente utente = controller.login(email, password);

        if (utente != null) {

            JOptionPane.showMessageDialog(
                    null,
                    "Login effettuato correttamente",
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);

            /*
             * Qui successivamente apriremo:
             *
             * FormAmministratore
             * FormMedico
             * FormPaziente
             */

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Email o password non valide",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Ambulatorio San Giorgio");

        frame.setContentPane(new LoginForm().contentPane);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setResizable(false);

        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}