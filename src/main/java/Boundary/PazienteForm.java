package Boundary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PazienteForm {
    private JPanel contentPane;
    private JButton logoutButton;
    private JButton prenotaVisitaButton;
    private JButton leMieVisiteButton;

    // 1. Modifichiamo il costruttore per ricevere il frame principale
    public PazienteForm(JFrame frameAttuale) {
        prenotaVisitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 2. Chiudiamo la finestra attuale del menu Paziente
                frameAttuale.dispose();

                // 3. Apriamo la nuova interfaccia per la scelta della specializzazione
                new FormSceltaSpecializzazione();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Paziente Form");

        // 4. Cambiamo l'istanziazione passando il frame al costruttore
        PazienteForm form = new PazienteForm(frame);

        // 5. Usiamo l'oggetto "form" appena creato per impostare il contentPane
        frame.setContentPane(form.contentPane);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}