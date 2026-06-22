package Boundary;

import javax.swing.*;

public class PazienteForm {
    private JButton logoutButton;
    private JButton prenotaVisitaButton;
    private JButton leMieVisiteButton;

        public static void main(String[] args) {
            JFrame frame = new JFrame();
            frame.setTitle("Paziente Form");
            //frame.setContentPane(new Boundary.PazienteForm().contentPane); da errore
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

