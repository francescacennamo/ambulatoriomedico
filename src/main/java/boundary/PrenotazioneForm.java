package boundary;
import control.PrenotazioneController;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import java.util.Calendar;
import java.util.Date;

public class PrenotazioneForm {
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
    private JComboBox cmbFasciaOraria;
    private JLabel labelFasciaOraria;
    private JLabel labelNomeAltro;
    private JTextField textNomeAltro;
    private JTextField textCognomeAltro;
    private JLabel cognomeAltroLabel;
    private JLabel logoLabel;
    private JButton confermaButton;
    private JButton annullaButton;

    private PrenotazioneController controller;
    private Map<Long, String> mappaSpecializzazioni;
    private Map<Long, String> mappaMedici;
    private Map<Long, String> mappaFasceOrarie; // ID Fascia -> Orario (es: "09:00 - 09:30")

    private String nomePazienteLoggato;
    private String cognomePazienteLoggato;
    private String emailPazienteLoggato;

    public PrenotazioneForm() {
        this("", "", "");
    }
    public PrenotazioneForm(String nome, String cognome, String email) {
        $$$setupUI$$$();
        this.controller = new PrenotazioneController();
        this.nomePazienteLoggato = nome;
        this.cognomePazienteLoggato = cognome;
        this.emailPazienteLoggato = email;

        // Autocompiliamo i campi di testo grafici della form
        textNome.setText(nome);
        textCognome.setText(cognome);
        textEmail.setText(email);

        // Disabilitiamo la modifica manuale per non alterare l'identità del richiedente
        textNome.setEditable(false);
        textCognome.setEditable(false);
        textEmail.setEditable(false);
        Date oggi = new Date();

        Calendar cal = Calendar.getInstance();
        Date inizio = cal.getTime(); // Oggi
        cal.add(Calendar.YEAR, 2);
        Date fine = cal.getTime();   // Tra due anni

        SpinnerDateModel dateModel = new SpinnerDateModel(oggi, inizio, fine, Calendar.DAY_OF_MONTH);
        spinnerData.setModel(dateModel);

        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerData, "dd/MM/yyyy");
        spinnerData.setEditor(editor);

        popolaSpecializzazioni();

        cmbSpecializzazioni.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeSelezionato = (String) cmbSpecializzazioni.getSelectedItem();
                if (nomeSelezionato != null) {
                    Long idSelezionato = trovaIdSpecDaNome(nomeSelezionato);
                    if (idSelezionato != null) {
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
                    Long idMedico = trovaIdMedicoDaNome(medicoSelezionato);
                    System.out.println("ID Medico selezionato: " + idMedico);
                    popolaFasceOrarie(idMedico);
                } else {
                    cmbFasciaOraria.removeAllItems();
                }
            }
        });

        siCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean spuntata = siCheckBox.isSelected();

                // Mostriamo o nascondiamo i campi di conseguenza
                labelNomeAltro.setVisible(spuntata);
                textNomeAltro.setVisible(spuntata);
                cognomeAltroLabel.setVisible(spuntata);
                textCognomeAltro.setVisible(spuntata);

                // Chiediamo al pannello principale di ricalcolare il layout grafico
                contentPane.revalidate();
                contentPane.repaint();

                // MODIFICA: Ridimensiona dinamicamente la finestra fissa (anche se setResizable è false)
                Window win = SwingUtilities.getWindowAncestor(contentPane);
                if (win != null) {
                    win.pack();
                }
            }
        });

        spinnerData.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String medicoSelezionato = (String) cmbMediciPerSpec.getSelectedItem();
                if (medicoSelezionato != null) {
                    Long idMedico = trovaIdMedicoDaNome(medicoSelezionato);
                    popolaFasceOrarie(idMedico);
                }
            }
        });

        confermaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String medicoSelezionato = (String) cmbMediciPerSpec.getSelectedItem();
                String orarioSelezionato = (String) cmbFasciaOraria.getSelectedItem();
                String emailLoggato = textEmail.getText().trim();

                if (medicoSelezionato == null || orarioSelezionato == null || emailLoggato.isEmpty()) {
                    JOptionPane.showMessageDialog(contentPane, "Compilare i campi obbligatori (Medico, Fascia, tua Email).", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Long idMedico = trovaIdMedicoDaNome(medicoSelezionato);
                Long idFascia = trovaIdFasciaDaOrario(orarioSelezionato);

                boolean prenotaPerAltro = siCheckBox.isSelected();
                String nomeAltro = textNomeAltro.getText().trim();
                String cognomeAltro = textCognomeAltro.getText().trim();

                if (prenotaPerAltro && (nomeAltro.isEmpty() || cognomeAltro.isEmpty())) {
                    JOptionPane.showMessageDialog(contentPane, "Inserisci Nome e Cognome della persona per cui stai prenotando.", "Dati Mancanti", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean successo = controller.confermaPrenotazione(idMedico, idFascia, emailLoggato, prenotaPerAltro, nomeAltro, cognomeAltro);

                if (successo) {
                    JOptionPane.showMessageDialog(contentPane, "Visita prenotata con successo!", "Conferma", JOptionPane.INFORMATION_MESSAGE);
                    if (idMedico != null) {
                        popolaFasceOrarie(idMedico);
                    }
                } else {
                    JOptionPane.showMessageDialog(contentPane, "Impossibile completare la prenotazione. Controllare i dati.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window win = SwingUtilities.getWindowAncestor(contentPane);
                if (win != null) {
                    win.dispose();
                }
                SwingUtilities.invokeLater(() -> {
                    new PazienteForm(nomePazienteLoggato, cognomePazienteLoggato, emailPazienteLoggato).apriForm();
                });
            }
        });
    }

    public JPanel getContentPane() {
        return this.contentPane;
    }

    private void popolaSpecializzazioni() {
        this.mappaSpecializzazioni = controller.ottieniMappaSpecializzazioni();
        cmbSpecializzazioni.removeAllItems();
        for (String nome : mappaSpecializzazioni.values()) {
            cmbSpecializzazioni.addItem(nome);
        }
        cmbSpecializzazioni.setSelectedIndex(-1);
    }

    private Long trovaIdSpecDaNome(String nomeCercato) {
        for (Map.Entry<Long, String> entry : mappaSpecializzazioni.entrySet()) {
            if (entry.getValue().equals(nomeCercato)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void popolaMedici(Long idSpecializzazione) {
        cmbMediciPerSpec.removeAllItems();
        this.mappaMedici = controller.ottieniMediciPerSpecializzazione(idSpecializzazione);
        cmbMediciPerSpec.removeAllItems();
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

    private void popolaFasceOrarie(Long idMedico) {
        cmbFasciaOraria.removeAllItems();
        if (idMedico == null) return;

        Date dateDaSpinner = (Date) spinnerData.getValue();
        LocalDate localDate = dateDaSpinner.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        this.mappaFasceOrarie = controller.ottieniFasceDisponibili(idMedico, localDate);
        for (String orario : mappaFasceOrarie.values()) {
            cmbFasciaOraria.addItem(orario);
        }
    }

    private Long trovaIdFasciaDaOrario(String orarioCercato) {
        if (mappaFasceOrarie == null) return null;
        for (Map.Entry<Long, String> entry : mappaFasceOrarie.entrySet()) {
            if (entry.getValue().equals(orarioCercato)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public JFrame apriPrenotazioneForm() {
        JFrame frame = new JFrame("Prenotazione Visita");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // MODIFICA: Impedisce all'utente di ridimensionare la finestra trascinando i bordi
        frame.setResizable(false);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
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
        contentPane.setLayout(new GridLayoutManager(15, 5, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setBackground(new Color(-14793370));
        contentPane.setFocusable(true);
        Font contentPaneFont = this.$$$getFont$$$(null, -1, -1, contentPane.getFont());
        if (contentPaneFont != null) contentPane.setFont(contentPaneFont);
        contentPane.setForeground(new Color(-14793370));
        contentPane.setInheritsPopupMenu(false);
        contentPane.setMaximumSize(new Dimension(650, 450));
        contentPane.setMinimumSize(new Dimension(650, 450));
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(650, 450));
        contentPane.setRequestFocusEnabled(true);
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
        contentPane.add(labelTitolo, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelNome = new JLabel();
        labelNome.setBackground(new Color(-1));
        Font labelNomeFont = this.$$$getFont$$$("Arial", -1, 16, labelNome.getFont());
        if (labelNomeFont != null) labelNome.setFont(labelNomeFont);
        labelNome.setForeground(new Color(-1));
        labelNome.setOpaque(false);
        labelNome.setText("Nome");
        contentPane.add(labelNome, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelCognome = new JLabel();
        labelCognome.setBackground(new Color(-1));
        Font labelCognomeFont = this.$$$getFont$$$("Arial", -1, 16, labelCognome.getFont());
        if (labelCognomeFont != null) labelCognome.setFont(labelCognomeFont);
        labelCognome.setForeground(new Color(-1));
        labelCognome.setText("Cognome");
        contentPane.add(labelCognome, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textCognome = new JTextField();
        contentPane.add(textCognome, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
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
        contentPane.add(cmbSpecializzazioni, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        labelMedico = new JLabel();
        labelMedico.setBackground(new Color(-1));
        Font labelMedicoFont = this.$$$getFont$$$("Arial", -1, 16, labelMedico.getFont());
        if (labelMedicoFont != null) labelMedico.setFont(labelMedicoFont);
        labelMedico.setForeground(new Color(-1));
        labelMedico.setText("Medico");
        contentPane.add(labelMedico, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbMediciPerSpec = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        cmbMediciPerSpec.setModel(defaultComboBoxModel2);
        contentPane.add(cmbMediciPerSpec, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelSpecializzazione = new JLabel();
        labelSpecializzazione.setBackground(new Color(-1));
        Font labelSpecializzazioneFont = this.$$$getFont$$$("Arial", -1, 16, labelSpecializzazione.getFont());
        if (labelSpecializzazioneFont != null) labelSpecializzazione.setFont(labelSpecializzazioneFont);
        labelSpecializzazione.setForeground(new Color(-1));
        labelSpecializzazione.setText("Specializzazione");
        contentPane.add(labelSpecializzazione, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textNome = new JTextField();
        contentPane.add(textNome, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        labelPrenAltro = new JLabel();
        labelPrenAltro.setBackground(new Color(-1));
        Font labelPrenAltroFont = this.$$$getFont$$$("Arial", -1, 16, labelPrenAltro.getFont());
        if (labelPrenAltroFont != null) labelPrenAltro.setFont(labelPrenAltroFont);
        labelPrenAltro.setForeground(new Color(-1));
        labelPrenAltro.setText("Prenoto per un'altra persona");
        contentPane.add(labelPrenAltro, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        siCheckBox = new JCheckBox();
        siCheckBox.setBackground(new Color(-14793370));
        Font siCheckBoxFont = this.$$$getFont$$$("Arial", -1, 12, siCheckBox.getFont());
        if (siCheckBoxFont != null) siCheckBox.setFont(siCheckBoxFont);
        siCheckBox.setForeground(new Color(-1));
        siCheckBox.setText("Sì");
        contentPane.add(siCheckBox, new GridConstraints(10, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelEmail = new JLabel();
        labelEmail.setBackground(new Color(-14793370));
        Font labelEmailFont = this.$$$getFont$$$("Arial", -1, 16, labelEmail.getFont());
        if (labelEmailFont != null) labelEmail.setFont(labelEmailFont);
        labelEmail.setForeground(new Color(-1));
        labelEmail.setText("Email");
        contentPane.add(labelEmail, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textEmail = new JTextField();
        textEmail.setText("");
        contentPane.add(textEmail, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        labelData = new JLabel();
        Font labelDataFont = this.$$$getFont$$$("Arial", -1, 16, labelData.getFont());
        if (labelDataFont != null) labelData.setFont(labelDataFont);
        labelData.setForeground(new Color(-1));
        labelData.setText("Data");
        contentPane.add(labelData, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        spinnerData = new JSpinner();
        spinnerData.setVisible(true);
        contentPane.add(spinnerData, new GridConstraints(8, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbFasciaOraria = new JComboBox();
        contentPane.add(cmbFasciaOraria, new GridConstraints(9, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelFasciaOraria = new JLabel();
        Font labelFasciaOrariaFont = this.$$$getFont$$$("Arial", -1, 16, labelFasciaOraria.getFont());
        if (labelFasciaOrariaFont != null) labelFasciaOraria.setFont(labelFasciaOrariaFont);
        labelFasciaOraria.setForeground(new Color(-1));
        labelFasciaOraria.setText("Fascia Oraria");
        contentPane.add(labelFasciaOraria, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelNomeAltro = new JLabel();
        Font labelNomeAltroFont = this.$$$getFont$$$("Arial", -1, 16, labelNomeAltro.getFont());
        if (labelNomeAltroFont != null) labelNomeAltro.setFont(labelNomeAltroFont);
        labelNomeAltro.setForeground(new Color(-1));
        labelNomeAltro.setText("Nome del paziente");
        labelNomeAltro.setVisible(false);
        contentPane.add(labelNomeAltro, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textNomeAltro = new JTextField();
        textNomeAltro.setVisible(false);
        contentPane.add(textNomeAltro, new GridConstraints(11, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        cognomeAltroLabel = new JLabel();
        Font cognomeAltroLabelFont = this.$$$getFont$$$("Arial", -1, 16, cognomeAltroLabel.getFont());
        if (cognomeAltroLabelFont != null) cognomeAltroLabel.setFont(cognomeAltroLabelFont);
        cognomeAltroLabel.setForeground(new Color(-1));
        cognomeAltroLabel.setText("Cognome del paziente");
        cognomeAltroLabel.setVisible(false);
        contentPane.add(cognomeAltroLabel, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textCognomeAltro = new JTextField();
        textCognomeAltro.setVisible(false);
        contentPane.add(textCognomeAltro, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        confermaButton = new JButton();
        confermaButton.setText("Conferma");
        contentPane.add(confermaButton, new GridConstraints(13, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        annullaButton = new JButton();
        annullaButton.setText("Annulla");
        contentPane.add(annullaButton, new GridConstraints(14, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoLabel = new JLabel();
        Font logoLabelFont = this.$$$getFont$$$("Felix Titling", -1, 28, logoLabel.getFont());
        if (logoLabelFont != null) logoLabel.setFont(logoLabelFont);
        logoLabel.setForeground(new Color(-1));
        logoLabel.setText("Salus");
        contentPane.add(logoLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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