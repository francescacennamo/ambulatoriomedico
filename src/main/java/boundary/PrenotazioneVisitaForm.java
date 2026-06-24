package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.toedter.calendar.JDateChooser;
import control.PrenotazioneController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class PrenotazioneVisitaForm {

    // Componenti grafici (Tutti con tipo <String> per non importare Entity)
    private JComboBox<String> cmbSpecializzazione;
    private JComboBox<String> cmbMedico;
    private JPanel panelCalendario;
    private JButton confermaButton;
    private JList<String> listFascia;
    private JPanel pannelloPrenotazione;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtEmail;
    private JTextField txtRecapito;
    private JCheckBox ckPrenPerAltri;
    private JTextField txtNomePaziente;
    private JTextField txtCognomePaziente;

    // Attributi di controllo e dati primitivi
    private JDateChooser dateChooser;
    private final PrenotazioneController controller;
    private final Long idPazienteLoggato;

    // Il costruttore riceve l'ID del paziente e le stringhe di anagrafica da mostrare
    public PrenotazioneVisitaForm(JFrame frameAttuale, Long idPaziente, String nomeLoggato, String cognomeLoggato) {
        this.controller = new PrenotazioneController();
        this.idPazienteLoggato = idPaziente;

        $$$setupUI$$$();
        popolaSpecializzazioni();

        // Popoliamo i campi con le stringhe
        txtNome.setText(nomeLoggato);
        txtCognome.setText(cognomeLoggato);

        ckPrenPerAltri.setEnabled(true);

        ckPrenPerAltri.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selezionale = ckPrenPerAltri.isSelected();
                txtNomePaziente.setEnabled(selezionale);
                txtCognomePaziente.setEnabled(selezionale);

                if (!selezionale) {
                    txtNomePaziente.setText("");
                    txtCognomePaziente.setText("");
                }
            }
        });

        // EVENTO 1: Quando l'utente seleziona una specializzazione
        cmbSpecializzazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String specScelta = (String) cmbSpecializzazione.getSelectedItem();
                if (specScelta != null && !specScelta.equals("Seleziona...")) {
                    cmbMedico.removeAllItems();

                    // Riceve stringhe dal controller
                    List<String> medici = controller.ottieniMediciPerSpecializzazione(specScelta);
                    for (String m : medici) {
                        cmbMedico.addItem(m);
                    }
                    cmbMedico.setEnabled(true);
                }
            }
        });

        cmbMedico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aggiornaFasceOrarie();
            }
        });

        dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                aggiornaFasceOrarie();
            }
        });

        // EVENTO 3: Click sul pulsante di conferma finale
        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fasciaSelezionataTesto = listFascia.getSelectedValue();
                String medicoSelezionatoTesto = (String) cmbMedico.getSelectedItem();

                if (fasciaSelezionataTesto == null || medicoSelezionatoTesto == null) {
                    JOptionPane.showMessageDialog(null, "Selezionare una fascia oraria", "Avviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (fasciaSelezionataTesto.contains("(PRENOTATA)")) {
                    JOptionPane.showMessageDialog(null, "Orario già occupato!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // ESTRAZIONE DEGLI ID TRAMITE SPLIT SULLE STRINGHE DI TESTO
                Long idMedico = Long.parseLong(medicoSelezionatoTesto.split(" - ")[0].replace("ID: ", ""));
                Long idFascia = Long.parseLong(fasciaSelezionataTesto.split(" - ")[0].replace("ID_FASCIA: ", ""));

                boolean perAltraPersona = ckPrenPerAltri.isSelected();
                String nomeAltro = txtNomePaziente.getText().trim();
                String cognomeAltro = txtCognomePaziente.getText().trim();

                if (perAltraPersona && (nomeAltro.isEmpty() || cognomeAltro.isEmpty())) {
                    JOptionPane.showMessageDialog(null, "Inserire nome e cognome del paziente effettivo", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Passiamo solo tipi primitivi ed ID numerici al controller
                boolean successo = controller.prenotaVisita(
                        idPazienteLoggato,
                        idMedico,
                        idFascia,
                        perAltraPersona,
                        nomeAltro,
                        cognomeAltro
                );

                if (successo) {
                    JOptionPane.showMessageDialog(null, "Visita prenotata con successo!");
                    frameAttuale.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Impossibile completare la prenotazione", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void popolaSpecializzazioni() {
        cmbSpecializzazione.addItem("Seleziona...");
        List<String> specs = controller.ottieniNomiSpecializzazioni();
        for (String s : specs) {
            cmbSpecializzazione.addItem(s);
        }
    }

    private void aggiornaFasceOrarie() {
        String medicoSelezionatoTesto = (String) cmbMedico.getSelectedItem();
        Date dataSelezionata = dateChooser.getDate();

        if (medicoSelezionatoTesto != null && dataSelezionata != null) {
            Long idMedico = Long.parseLong(medicoSelezionatoTesto.split(" - ")[0].replace("ID: ", ""));

            LocalDate dataScelta = dataSelezionata.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            List<String> fasce = controller.ottieniFasceOrarieMedico(idMedico, dataScelta);

            DefaultListModel<String> model = new DefaultListModel<>();
            for (String f : fasce) {
                model.addElement(f);
            }
            listFascia.setModel(model);
            confermaButton.setEnabled(true);
        }
    }

    private void createUIComponents() {
        panelCalendario = new JPanel();
        panelCalendario.setLayout(new BorderLayout());

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date());

        panelCalendario.add(dateChooser, BorderLayout.CENTER);
    }

    public JPanel getRootComponent() {
        return pannelloPrenotazione;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        pannelloPrenotazione = new JPanel();
        pannelloPrenotazione.setLayout(new GridLayoutManager(17, 10, new Insets(0, 0, 0, 0), -1, -1));
        pannelloPrenotazione.setEnabled(false);
        final JLabel label1 = new JLabel();
        label1.setText("Prenota la tua visita");
        pannelloPrenotazione.add(label1, new GridConstraints(0, 2, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Scegli la specializzazione");
        pannelloPrenotazione.add(label2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbSpecializzazione = new JComboBox();
        pannelloPrenotazione.add(cmbSpecializzazione, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setEnabled(false);
        label3.setText("Scegli il medico");
        pannelloPrenotazione.add(label3, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbMedico = new JComboBox();
        cmbMedico.setEnabled(false);
        pannelloPrenotazione.add(cmbMedico, new GridConstraints(4, 2, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setEnabled(false);
        label4.setText("Scegli la data");
        pannelloPrenotazione.add(label4, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelCalendario.setEnabled(false);
        pannelloPrenotazione.add(panelCalendario, new GridConstraints(6, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setEnabled(false);
        label5.setText("Seleziona la fascia oraria");
        pannelloPrenotazione.add(label5, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        pannelloPrenotazione.add(spacer1, new GridConstraints(16, 4, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        pannelloPrenotazione.add(scrollPane1, new GridConstraints(8, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listFascia = new JList();
        scrollPane1.setViewportView(listFascia);
        final JLabel label6 = new JLabel();
        label6.setText("Nome");
        pannelloPrenotazione.add(label6, new GridConstraints(9, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNome = new JTextField();
        txtNome.setEnabled(false);
        pannelloPrenotazione.add(txtNome, new GridConstraints(9, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        confermaButton = new JButton();
        confermaButton.setEnabled(false);
        confermaButton.setText("Conferma");
        pannelloPrenotazione.add(confermaButton, new GridConstraints(16, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Cognome");
        pannelloPrenotazione.add(label7, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtCognome = new JTextField();
        txtCognome.setEnabled(false);
        pannelloPrenotazione.add(txtCognome, new GridConstraints(10, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Email");
        pannelloPrenotazione.add(label8, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Recapito Telefonico");
        pannelloPrenotazione.add(label9, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtEmail = new JTextField();
        txtEmail.setEnabled(false);
        pannelloPrenotazione.add(txtEmail, new GridConstraints(11, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtRecapito = new JTextField();
        txtRecapito.setEnabled(false);
        pannelloPrenotazione.add(txtRecapito, new GridConstraints(12, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        pannelloPrenotazione.add(toolBar$Separator1, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        pannelloPrenotazione.add(spacer2, new GridConstraints(16, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Prenoto per un'altra persona");
        pannelloPrenotazione.add(label10, new GridConstraints(13, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ckPrenPerAltri = new JCheckBox();
        ckPrenPerAltri.setEnabled(false);
        ckPrenPerAltri.setText("");
        pannelloPrenotazione.add(ckPrenPerAltri, new GridConstraints(13, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Nome del paziente");
        pannelloPrenotazione.add(label11, new GridConstraints(14, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtNomePaziente = new JTextField();
        txtNomePaziente.setEnabled(false);
        pannelloPrenotazione.add(txtNomePaziente, new GridConstraints(14, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Cognome del paziente");
        pannelloPrenotazione.add(label12, new GridConstraints(15, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtCognomePaziente = new JTextField();
        txtCognomePaziente.setEnabled(false);
        pannelloPrenotazione.add(txtCognomePaziente, new GridConstraints(15, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pannelloPrenotazione;
    }


}
