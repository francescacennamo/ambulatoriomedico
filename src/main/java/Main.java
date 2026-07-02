import boundary.PrenotazioneForm;
import database.JpaUtil;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // 1. Il Main inizializza l'infrastruttura di persistenza (Livello Database)
        JpaUtil.getInstance();

        // 2. Avvia la Boundary nel thread corretto (Livello Boundary)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Istanziamo la Boundary.
                    // Sarà LEI, al suo interno, a istanziare il proprio Controller,
                    // rispettando il flusso: Boundary -> Controller -> Entity/Database.
                    PrenotazioneForm form = new PrenotazioneForm();

                    // Creazione del contenitore grafico standard
                    JFrame frame = new JFrame("Test Prenotazione Form");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setContentPane(form.getContentPane());

                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}