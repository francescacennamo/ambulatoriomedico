import boundary.prenotazioneForm;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Swing richiede che l'interfaccia grafica venga avviata nel thread corretto (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 1. Istanziamo la Boundary (che al suo interno chiama il Controller)
                    prenotazioneForm form = new prenotazioneForm();

                    // 2. Creiamo la finestra (il contenitore esterno)
                    JFrame frame = new JFrame("Test Prenotazione Form");

                    // Diciamo al programma di chiudersi definitivamente quando premiamo la "X"
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    // 3. Inseriamo il pannello della Form dentro la finestra
                    frame.setContentPane(form.getContentPane());

                    // 4. Adattiamo le dimensioni della finestra al contenuto e rendiamola visibile
                    frame.pack();
                    frame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
                    frame.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
