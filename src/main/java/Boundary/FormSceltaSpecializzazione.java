package Boundary;

import Control.PrenotazioneController;
import Entity.Specializzazione;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FormSceltaSpecializzazione {
    // 1. Pannello principale da mappare nel file .form (field name = contentPane)
    private JPanel contentPane;
    private JComboBox<String> cmbSpecializzazioni;
    private JButton avantiButton;

    // Riferimenti per la logica applicativa
    private PrenotazioneController controller;
    private List<Specializzazione> listaSpecializzazioni;

    public FormSceltaSpecializzazione() {
        // 2. Inizializziamo il controller del caso d'uso (GRASP Controller)
        this.controller = new PrenotazioneController();

        // 3. Creiamo la finestra contenitrice (JFrame)
        JFrame frame = new JFrame("Effettua Prenotazione - Specializzazioni");
        frame.setContentPane(this.contentPane); // Associa il pannello disegnato

        // 4. Popoliamo la JComboBox chiedendo i dati al controller
        popolaComboSpecializzazioni();

        // 5. Aggiungiamo il listener al pulsante Avanti (Approccio consigliato dal prof.)
        avantiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gestisciClickAvanti(frame);
            }
        });

        // 6. Configurazione della finestra (Proprietà standard viste a lezione)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiude l'app alla chiusura della finestra
        frame.setResizable(false); // Impedisce il ridimensionamento manuale
        frame.pack(); // Adatta la finestra alla grandezza fissata nel designer (.form)
        frame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
        frame.setVisible(true); // Mostra la GUI
    }

    private void popolaComboSpecializzazioni() {
        // Recuperiamo la lista dal controller (che a sua volta interroga il database)
        listaSpecializzazioni = controller.ottieniSpecializzazioni();
        cmbSpecializzazioni.removeAllItems();

        if (listaSpecializzazioni.isEmpty()) {
            cmbSpecializzazioni.addItem("Nessuna specializzazione disponibile");
            avantiButton.setEnabled(false);
        } else {
            // Cicliamo sulle entità e aggiungiamo solo il nome testuale nella combo box
            for (Specializzazione s : listaSpecializzazioni) {
                cmbSpecializzazioni.addItem(s.getNome());
            }
        }
    }

    private void gestisciClickAvanti(JFrame frameAttuale) {
        int index = cmbSpecializzazioni.getSelectedIndex();

        // Verifichiamo che ci sia una selezione valida
        if (index >= 0 && listaSpecializzazioni != null && !listaSpecializzazioni.isEmpty()) {
            Specializzazione scelta = listaSpecializzazioni.get(index);

            // Messaggio di conferma temporaneo per verificare il flusso
            JOptionPane.showMessageDialog(frameAttuale, "Hai selezionato: " + scelta.getNome());

            // TODO: Qui inseriremo l'apertura della prossima interfaccia (es. Scelta Medico)
            // frameAttuale.dispose();
            // new FormSceltaMedico(scelta);
        }
    }
}