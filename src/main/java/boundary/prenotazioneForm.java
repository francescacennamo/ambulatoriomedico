package boundary;
import control.PrenotazioneController;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Map;

import java.util.Calendar;
import java.util.Date;

public class prenotazioneForm {
    private JPanel contentPane;
    private JTextField textCognome;
    private JComboBox cmbSpecializzazioni;
    private JComboBox cmbMediciPerSpec;
    private JLabel labelNome;
    private JLabel labelCognome;
    private JLabel labelSpecializzazione;
    private JLabel labelTitolo;
    private JLabel labelMedico;
    private JTextField textNome;
    private JLabel labelPrenAltro;
    private JCheckBox siCheckBox;
    private JTextField textEmail;
    private JLabel labelEmail;
    private JLabel labelData;
    private JSpinner spinnerData;

    private PrenotazioneController controller;
    private Map<Long, String> mappaSpecializzazioni;
    private Map<Long, String> mappaMedici;

    public prenotazioneForm() {
        $$$setupUI$$$();
        this.controller = new PrenotazioneController();

        Date oggi = new Date();

// Definisci i limiti (opzionale): es. non puoi prenotare nel passato
        Calendar cal = Calendar.getInstance();
        Date inizio = cal.getTime(); // Oggi
        cal.add(Calendar.YEAR, 2);
        Date fine = cal.getTime();   // Tra due anni

// Crea il modello per la data (Valore iniziale, Minimo, Massimo, Campo da incrementare)
        SpinnerDateModel dateModel = new SpinnerDateModel(oggi, inizio, fine, Calendar.DAY_OF_MONTH);
        spinnerData.setModel(dateModel);

// Scegli il formato di visualizzazione (es. 25/12/2026)
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerData, "dd/MM/yyyy");
        spinnerData.setEditor(editor);

        popolaSpecializzazioni();

        cmbSpecializzazioni.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Recuperiamo il nome selezionato (es. "Cardiologia")
                String nomeSelezionato = (String) cmbSpecializzazioni.getSelectedItem();

                if (nomeSelezionato != null) {
                    // Troviamo l'ID corrispondente a quel nome cercando nella mappa
                    Long idSelezionato = trovaIdSpecDaNome(nomeSelezionato);

                    if (idSelezionato != null) {
                        // Passiamo l'ID al metodo che caricherà i medici (molto più sicuro del nome!)
                        popolaMedici(idSelezionato);
                    }
                }
            }
        });
        cmbMediciPerSpec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String medicoSelezionato = (String) cmbMediciPerSpec.getSelectedItem();
                if (medicoSelezionato != null) {
                    // Qui trovi l'ID del medico selezionato se ti servirà per salvare la prenotazione
                    Long idMedico = trovaIdMedicoDaNome(medicoSelezionato);
                    System.out.println("ID Medico selezionato: " + idMedico);
                }
            }
        });
        siCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getContentPane() {
        return this.contentPane;
    }

    private void popolaSpecializzazioni() {
        // Chiediamo la mappa al controller (contiene ID -> Nome)
        this.mappaSpecializzazioni = controller.ottieniMappaSpecializzazioni();

        cmbSpecializzazioni.removeAllItems();

        // Prendiamo solo i VALORI (cioè i nomi delle specializzazioni) e li mettiamo nella tendina
        for (String nome : mappaSpecializzazioni.values()) {
            cmbSpecializzazioni.addItem(nome);
        }
        cmbSpecializzazioni.setSelectedIndex(-1);
    }

    private Long trovaIdSpecDaNome(String nomeCercato) {
        for (Map.Entry<Long, String> entry : mappaSpecializzazioni.entrySet()) {
            if (entry.getValue().equals(nomeCercato)) {
                return entry.getKey(); // Restituisce l'ID (es. 3)
            }
        }
        return null;
    }

    private void popolaMedici(Long idSpecializzazione) {
        cmbMediciPerSpec.removeAllItems();
        // Chiediamo al controller i medici filtrati per l'ID della specializzazione
        this.mappaMedici = controller.ottieniMediciPerSpecializzazione(idSpecializzazione);

        // Svuotiamo la combobox dei medici (cancella i medici della vecchia selezione)
        cmbMediciPerSpec.removeAllItems();

        // Inseriamo i nomi dei medici nella seconda ComboBox
        for (String nomeMedico : mappaMedici.values()) {
            cmbMediciPerSpec.addItem(nomeMedico);
        }
    }

    private Long trovaIdMedicoDaNome(String nomeCercato) {
        for (Map.Entry<Long, String> entry : mappaMedici.entrySet()) {
            if (entry.getValue().equals(nomeCercato)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(8, 5, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setBackground(new Color(-14793370));
        Font contentPaneFont = this.$$$getFont$$$(null, -1, -1, contentPane.getFont());
        if (contentPaneFont != null) contentPane.setFont(contentPaneFont);
        contentPane.setForeground(new Color(-14793370));
        contentPane.setInheritsPopupMenu(false);
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(450, 350));
        contentPane.setVisible(true);
        labelTitolo = new JLabel();
        labelTitolo.setAutoscrolls(true);
        labelTitolo.setBackground(new Color(-1));
        Font labelTitoloFont = this.$$$getFont$$$("Arial Black", -1, 20, labelTitolo.getFont());
        if (labelTitoloFont != null) labelTitolo.setFont(labelTitoloFont);
        labelTitolo.setForeground(new Color(-1));
        labelTitolo.setOpaque(false);
        labelTitolo.setText("Prenota la tua visita");
        labelTitolo.setVisible(true);
        contentPane.add(labelTitolo, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelNome = new JLabel();
        labelNome.setBackground(new Color(-1));
        Font labelNomeFont = this.$$$getFont$$$("Arial", -1, 16, labelNome.getFont());
        if (labelNomeFont != null) labelNome.setFont(labelNomeFont);
        labelNome.setForeground(new Color(-1));
        labelNome.setOpaque(false);
        labelNome.setText("Nome");
        contentPane.add(labelNome, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelCognome = new JLabel();
        labelCognome.setBackground(new Color(-1));
        Font labelCognomeFont = this.$$$getFont$$$("Arial", -1, 16, labelCognome.getFont());
        if (labelCognomeFont != null) labelCognome.setFont(labelCognomeFont);
        labelCognome.setForeground(new Color(-1));
        labelCognome.setText("Cognome");
        contentPane.add(labelCognome, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textCognome = new JTextField();
        contentPane.add(textCognome, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cmbSpecializzazioni = new JComboBox();
        cmbSpecializzazioni.setEnabled(true);
        cmbSpecializzazioni.setFocusable(true);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("");
        defaultComboBoxModel1.addElement("Allergologia");
        defaultComboBoxModel1.addElement("Cardiologia");
        defaultComboBoxModel1.addElement("Dermatologia");
        defaultComboBoxModel1.addElement("Endocrinologia");
        defaultComboBoxModel1.addElement("Gastroenterologia");
        defaultComboBoxModel1.addElement("Geriatria");
        defaultComboBoxModel1.addElement("Ginecologia e Ostetricia");
        defaultComboBoxModel1.addElement("Neurologia");
        defaultComboBoxModel1.addElement("Oculistica");
        defaultComboBoxModel1.addElement("Ortopedia");
        defaultComboBoxModel1.addElement("Pediatria");
        defaultComboBoxModel1.addElement("Pneumologia");
        cmbSpecializzazioni.setModel(defaultComboBoxModel1);
        cmbSpecializzazioni.setOpaque(true);
        cmbSpecializzazioni.setRequestFocusEnabled(true);
        contentPane.add(cmbSpecializzazioni, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        labelMedico = new JLabel();
        labelMedico.setBackground(new Color(-1));
        Font labelMedicoFont = this.$$$getFont$$$("Arial", -1, 16, labelMedico.getFont());
        if (labelMedicoFont != null) labelMedico.setFont(labelMedicoFont);
        labelMedico.setForeground(new Color(-1));
        labelMedico.setText("Medico");
        contentPane.add(labelMedico, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbMediciPerSpec = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        cmbMediciPerSpec.setModel(defaultComboBoxModel2);
        contentPane.add(cmbMediciPerSpec, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelSpecializzazione = new JLabel();
        labelSpecializzazione.setBackground(new Color(-1));
        Font labelSpecializzazioneFont = this.$$$getFont$$$("Arial", -1, 16, labelSpecializzazione.getFont());
        if (labelSpecializzazioneFont != null) labelSpecializzazione.setFont(labelSpecializzazioneFont);
        labelSpecializzazione.setForeground(new Color(-1));
        labelSpecializzazione.setText("Specializzazione");
        contentPane.add(labelSpecializzazione, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textNome = new JTextField();
        contentPane.add(textNome, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        labelPrenAltro = new JLabel();
        labelPrenAltro.setBackground(new Color(-1));
        Font labelPrenAltroFont = this.$$$getFont$$$("Arial", -1, 16, labelPrenAltro.getFont());
        if (labelPrenAltroFont != null) labelPrenAltro.setFont(labelPrenAltroFont);
        labelPrenAltro.setForeground(new Color(-1));
        labelPrenAltro.setText("Prenoto per un'altra persona");
        contentPane.add(labelPrenAltro, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        siCheckBox = new JCheckBox();
        siCheckBox.setBackground(new Color(-14793370));
        Font siCheckBoxFont = this.$$$getFont$$$("Arial", -1, 12, siCheckBox.getFont());
        if (siCheckBoxFont != null) siCheckBox.setFont(siCheckBoxFont);
        siCheckBox.setForeground(new Color(-1));
        siCheckBox.setText("Sì");
        contentPane.add(siCheckBox, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelEmail = new JLabel();
        labelEmail.setBackground(new Color(-14793370));
        Font labelEmailFont = this.$$$getFont$$$("Arial", -1, 16, labelEmail.getFont());
        if (labelEmailFont != null) labelEmail.setFont(labelEmailFont);
        labelEmail.setForeground(new Color(-1));
        labelEmail.setText("Email");
        contentPane.add(labelEmail, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textEmail = new JTextField();
        textEmail.setText("");
        contentPane.add(textEmail, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        labelData = new JLabel();
        Font labelDataFont = this.$$$getFont$$$("Arial", -1, 16, labelData.getFont());
        if (labelDataFont != null) labelData.setFont(labelDataFont);
        labelData.setForeground(new Color(-1));
        labelData.setText("Data");
        contentPane.add(labelData, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spinnerData = new JSpinner();
        contentPane.add(spinnerData, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
