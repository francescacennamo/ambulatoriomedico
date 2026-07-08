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
import javax.swing.ImageIcon;
import java.net.URL;

import com.toedter.calendar.JDateChooser;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("unchecked")
public class PrenotazioneForm {
    private JPanel contentPane;
    private JTextField textCognome;
    private JComboBox<String> cmbSpecializzazioni;
    private JComboBox<String> cmbMediciPerSpec;
    private JLabel labelNome, labelCognome, labelSpecializzazione, labelTitolo, labelMedico;
    private JTextField textNome;
    private JLabel labelPrenAltro;
    private JCheckBox siCheckBox;
    private JTextField textEmail;
    private JLabel labelEmail, labelData;
    private JComboBox<String> cmbFasciaOraria;
    private JLabel labelFasciaOraria, labelNomeAltro;
    private JTextField textNomeAltro, textCognomeAltro;
    private JLabel cognomeAltroLabel, logoLabel;
    private JButton confermaButton, annullaButton;
    private JTextField textRecapito;
    private JLabel labelTel;
    private JPanel panelData;
    private JDateChooser dateChooser; // Questo è il nostro nuovo calendario!

    private PrenotazioneController controller;
    private Map<Long, String> mappaSpecializzazioni;
    private Map<Long, String> mappaMedici;
    private Map<Long, String> mappaFasceOrarie;

    private Long idPazienteLoggato;

    public PrenotazioneForm(Long id) {
        $$$setupUI$$$();
        this.controller = new PrenotazioneController();
        this.idPazienteLoggato = id;

        Map<String, String> anagrafica = controller.ottieniAnagraficaPazientePerId(id);

        textNome.setText(anagrafica.getOrDefault("nome", ""));
        textCognome.setText(anagrafica.getOrDefault("cognome", ""));
        textEmail.setText(anagrafica.getOrDefault("email", ""));
        textRecapito.setText(anagrafica.getOrDefault("recapito", ""));

        // MODIFICA: 'textRecapito' NON è più bloccato. Lasciamo l'utente libero di scriverci!
        textNome.setEditable(false);
        textCognome.setEditable(false);
        textEmail.setEditable(false);


        // INIZIALIZZAZIONE CALENDARIO (JDateChooser)
        dateChooser = new JDateChooser();
        dateChooser.setDate(new Date()); // Imposta la data di oggi come default
        dateChooser.setMinSelectableDate(new Date()); // BONUS UX: Impedisce di selezionare giorni passati!

        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.YEAR, 2);
        dateChooser.setMaxSelectableDate(cal1.getTime()); // Massimo 2 anni nel futuro

        // Inseriamo il calendario dentro il pannello grafico creato nel Designer
        panelData.setLayout(new BorderLayout());
        panelData.add(dateChooser, BorderLayout.CENTER);


        URL imgURL = getClass().getResource("/logo.png");
        if (imgURL != null) {
            Image scaledImage = new ImageIcon(imgURL).getImage().getScaledInstance(220, -1, Image.SCALE_SMOOTH);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setText("");
            logoLabel.setIcon(new ImageIcon(scaledImage));
        }

        popolaSpecializzazioni();

        cmbSpecializzazioni.addActionListener(e -> {
            String nome = (String) cmbSpecializzazioni.getSelectedItem();
            if (nome != null) popolaMedici(trovaIdSpecDaNome(nome));
        });

        cmbMediciPerSpec.addActionListener(e -> {
            String medico = (String) cmbMediciPerSpec.getSelectedItem();
            if (medico != null) popolaFasceOrarie(trovaIdMedicoDaNome(medico));
            else cmbFasciaOraria.removeAllItems();
        });

        siCheckBox.addActionListener(e -> {
            boolean spuntata = siCheckBox.isSelected();
            labelNomeAltro.setVisible(spuntata);
            textNomeAltro.setVisible(spuntata);
            cognomeAltroLabel.setVisible(spuntata);
            textCognomeAltro.setVisible(spuntata);
            contentPane.revalidate();
            contentPane.repaint();
        });

        // Ascolta quando l'utente clicca su un giorno nel calendario
        dateChooser.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String medico = (String) cmbMediciPerSpec.getSelectedItem();
                if (medico != null) popolaFasceOrarie(trovaIdMedicoDaNome(medico));
            }
        });


        confermaButton.addActionListener(e -> {
            String medicoSelezionato = (String) cmbMediciPerSpec.getSelectedItem();
            String orarioSelezionato = (String) cmbFasciaOraria.getSelectedItem();
            String telefonoVisita = textRecapito.getText().trim();

            // Errore 1: Campi obbligatori mancanti
            if (medicoSelezionato == null || orarioSelezionato == null || telefonoVisita.isEmpty()) {
                mostraMessaggioPersonalizzato(
                        "Compilare i campi obbligatori (Medico, Fascia, Recapito Telefonico).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE,
                        false // Resta sulla form
                );
                return;
            }

            Long idMedico = trovaIdMedicoDaNome(medicoSelezionato);
            Long idFascia = trovaIdFasciaDaOrario(orarioSelezionato);
            boolean prenotaPerAltro = siCheckBox.isSelected();
            String nomeAltro = textNomeAltro.getText().trim();
            String cognomeAltro = textCognomeAltro.getText().trim();

            // Errore 2: Dati beneficiario mancanti
            if (prenotaPerAltro && (nomeAltro.isEmpty() || cognomeAltro.isEmpty())) {
                mostraMessaggioPersonalizzato(
                        "Inserisci Nome e Cognome della persona per cui stai prenotando.",
                        "Dati Mancanti",
                        JOptionPane.WARNING_MESSAGE,
                        false // Resta sulla form
                );
                return;
            }

            boolean successo = controller.confermaPrenotazione(idMedico, idFascia, idPazienteLoggato, telefonoVisita, prenotaPerAltro, nomeAltro, cognomeAltro);

            if (successo) {
                // Caso Successo: Mostra il messaggio verde/informativo e torna al PazienteForm
                mostraMessaggioPersonalizzato(
                        "Visita prenotata con successo!",
                        "Conferma",
                        JOptionPane.INFORMATION_MESSAGE,
                        true // TORNA AL PAZIENTE
                );
            } else {
                // Errore 3: Fallimento database/controller
                mostraMessaggioPersonalizzato(
                        "Impossibile completare la prenotazione. Controllare i dati.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE,
                        false // Resta sulla form
                );
            }
        });

        annullaButton.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(contentPane);
            if (win != null) win.dispose();
            SwingUtilities.invokeLater(() -> new PazienteForm(idPazienteLoggato).apriForm());
        });
    }

    public JPanel getContentPane() {
        return this.contentPane;
    }

    private void popolaSpecializzazioni() {
        mappaSpecializzazioni = controller.ottieniMappaSpecializzazioni();
        cmbSpecializzazioni.removeAllItems();
        for (String nome : mappaSpecializzazioni.values()) cmbSpecializzazioni.addItem(nome);
        cmbSpecializzazioni.setSelectedIndex(-1);
    }

    private Long trovaIdSpecDaNome(String nomeCercato) {
        if (mappaSpecializzazioni == null) return null;
        for (Map.Entry<Long, String> entry : mappaSpecializzazioni.entrySet()) {
            if (entry.getValue().equals(nomeCercato)) return entry.getKey();
        }
        return null;
    }

    private void popolaMedici(Long idSpec) {
        cmbMediciPerSpec.removeAllItems();
        mappaMedici = controller.ottieniMediciPerSpecializzazione(idSpec);
        for (String nome : mappaMedici.values()) cmbMediciPerSpec.addItem(nome);
    }

    private Long trovaIdMedicoDaNome(String nomeCercato) {
        if (mappaMedici == null) return null;
        for (Map.Entry<Long, String> entry : mappaMedici.entrySet()) {
            if (entry.getValue().equals(nomeCercato)) return entry.getKey();
        }
        return null;
    }

    private void popolaFasceOrarie(Long idMedico) {
        cmbFasciaOraria.removeAllItems();
        if (idMedico == null) return;

        // Molto più pulito e coerente così:
        Date dataSelezionata = dateChooser.getDate();
        if (dataSelezionata == null) return;

        LocalDate localDate = dataSelezionata.toInstant()
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
            if (entry.getValue().equals(orarioCercato)) return entry.getKey();
        }
        return null;
    }

    public JFrame apriPrenotazioneForm() {
        JFrame frame = new JFrame("Prenotazione Visita");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
    // METODO HELPER: Crea un popup personalizzato con il bottone del colore dello stile della clinica
    private void mostraMessaggioPersonalizzato(String messaggio, String titolo, int tipoMessaggio, boolean tornaAlPaziente) {
        JButton btnOk = new JButton("OK");
        btnOk.setBackground(new Color(-10828087)); // Sfondo colorato
        btnOk.setForeground(Color.WHITE);          // Testo bianco
        btnOk.setFont(new Font("Arial", Font.BOLD, 14));
        btnOk.setFocusPainted(false);

        JOptionPane optionPane = new JOptionPane(
                messaggio,
                tipoMessaggio,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{btnOk},
                btnOk
        );

        JDialog dialog = optionPane.createDialog(contentPane, titolo);

        btnOk.addActionListener(actionEvent -> {
            dialog.dispose(); // Chiude sempre il popup

            // Se l'operazione è andata a buon fine (true), chiude la form e torna indietro
            if (tornaAlPaziente) {
                Window win = SwingUtilities.getWindowAncestor(contentPane);
                if (win != null) win.dispose();
                SwingUtilities.invokeLater(() -> new PazienteForm(idPazienteLoggato).apriForm());
            }
        });

        dialog.setVisible(true);
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
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(new Color(-14793370));
        contentPane.setFocusable(true);
        Font contentPaneFont = this.$$$getFont$$$(null, -1, -1, contentPane.getFont());
        if (contentPaneFont != null) contentPane.setFont(contentPaneFont);
        contentPane.setForeground(new Color(-14793370));
        contentPane.setInheritsPopupMenu(false);
        contentPane.setMaximumSize(new Dimension(900, 660));
        contentPane.setMinimumSize(new Dimension(700, 660));
        contentPane.setOpaque(true);
        contentPane.setPreferredSize(new Dimension(800, 600));
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
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelTitolo, gbc);
        labelNome = new JLabel();
        labelNome.setBackground(new Color(-1));
        Font labelNomeFont = this.$$$getFont$$$("Arial", -1, 16, labelNome.getFont());
        if (labelNomeFont != null) labelNome.setFont(labelNomeFont);
        labelNome.setForeground(new Color(-1));
        labelNome.setOpaque(false);
        labelNome.setText("Nome");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelNome, gbc);
        labelCognome = new JLabel();
        labelCognome.setBackground(new Color(-1));
        Font labelCognomeFont = this.$$$getFont$$$("Arial", -1, 16, labelCognome.getFont());
        if (labelCognomeFont != null) labelCognome.setFont(labelCognomeFont);
        labelCognome.setForeground(new Color(-1));
        labelCognome.setText("Cognome");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelCognome, gbc);
        textCognome = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(textCognome, gbc);
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
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(cmbSpecializzazioni, gbc);
        labelMedico = new JLabel();
        labelMedico.setBackground(new Color(-1));
        Font labelMedicoFont = this.$$$getFont$$$("Arial", -1, 16, labelMedico.getFont());
        if (labelMedicoFont != null) labelMedico.setFont(labelMedicoFont);
        labelMedico.setForeground(new Color(-1));
        labelMedico.setText("Medico");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelMedico, gbc);
        cmbMediciPerSpec = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        cmbMediciPerSpec.setModel(defaultComboBoxModel2);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(cmbMediciPerSpec, gbc);
        labelSpecializzazione = new JLabel();
        labelSpecializzazione.setBackground(new Color(-1));
        Font labelSpecializzazioneFont = this.$$$getFont$$$("Arial", -1, 16, labelSpecializzazione.getFont());
        if (labelSpecializzazioneFont != null) labelSpecializzazione.setFont(labelSpecializzazioneFont);
        labelSpecializzazione.setForeground(new Color(-1));
        labelSpecializzazione.setText("Specializzazione");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelSpecializzazione, gbc);
        textNome = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(textNome, gbc);
        labelPrenAltro = new JLabel();
        labelPrenAltro.setBackground(new Color(-1));
        Font labelPrenAltroFont = this.$$$getFont$$$("Arial", -1, 16, labelPrenAltro.getFont());
        if (labelPrenAltroFont != null) labelPrenAltro.setFont(labelPrenAltroFont);
        labelPrenAltro.setForeground(new Color(-1));
        labelPrenAltro.setText("Prenoto per un'altra persona");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelPrenAltro, gbc);
        siCheckBox = new JCheckBox();
        siCheckBox.setBackground(new Color(-14793370));
        Font siCheckBoxFont = this.$$$getFont$$$("Arial", -1, 12, siCheckBox.getFont());
        if (siCheckBoxFont != null) siCheckBox.setFont(siCheckBoxFont);
        siCheckBox.setForeground(new Color(-1));
        siCheckBox.setText("Sì");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 11;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        contentPane.add(siCheckBox, gbc);
        labelEmail = new JLabel();
        labelEmail.setBackground(new Color(-14793370));
        Font labelEmailFont = this.$$$getFont$$$("Arial", -1, 16, labelEmail.getFont());
        if (labelEmailFont != null) labelEmail.setFont(labelEmailFont);
        labelEmail.setForeground(new Color(-1));
        labelEmail.setText("Email");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelEmail, gbc);
        textEmail = new JTextField();
        textEmail.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(textEmail, gbc);
        labelData = new JLabel();
        Font labelDataFont = this.$$$getFont$$$("Arial", -1, 16, labelData.getFont());
        if (labelDataFont != null) labelData.setFont(labelDataFont);
        labelData.setForeground(new Color(-1));
        labelData.setText("Data");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelData, gbc);
        cmbFasciaOraria = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 10;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(cmbFasciaOraria, gbc);
        labelFasciaOraria = new JLabel();
        Font labelFasciaOrariaFont = this.$$$getFont$$$("Arial", -1, 16, labelFasciaOraria.getFont());
        if (labelFasciaOrariaFont != null) labelFasciaOraria.setFont(labelFasciaOrariaFont);
        labelFasciaOraria.setForeground(new Color(-1));
        labelFasciaOraria.setText("Fascia Oraria");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelFasciaOraria, gbc);
        labelNomeAltro = new JLabel();
        Font labelNomeAltroFont = this.$$$getFont$$$("Arial", -1, 16, labelNomeAltro.getFont());
        if (labelNomeAltroFont != null) labelNomeAltro.setFont(labelNomeAltroFont);
        labelNomeAltro.setForeground(new Color(-1));
        labelNomeAltro.setText("Nome del paziente");
        labelNomeAltro.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelNomeAltro, gbc);
        textNomeAltro = new JTextField();
        textNomeAltro.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 12;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(textNomeAltro, gbc);
        cognomeAltroLabel = new JLabel();
        Font cognomeAltroLabelFont = this.$$$getFont$$$("Arial", -1, 16, cognomeAltroLabel.getFont());
        if (cognomeAltroLabelFont != null) cognomeAltroLabel.setFont(cognomeAltroLabelFont);
        cognomeAltroLabel.setForeground(new Color(-1));
        cognomeAltroLabel.setText("Cognome del paziente");
        cognomeAltroLabel.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(cognomeAltroLabel, gbc);
        textCognomeAltro = new JTextField();
        textCognomeAltro.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 13;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(textCognomeAltro, gbc);
        confermaButton = new JButton();
        confermaButton.setBackground(new Color(-10828087));
        Font confermaButtonFont = this.$$$getFont$$$("Arial", Font.BOLD, 14, confermaButton.getFont());
        if (confermaButtonFont != null) confermaButton.setFont(confermaButtonFont);
        confermaButton.setForeground(new Color(-1));
        confermaButton.setText("Conferma");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 50, 10, 0);
        contentPane.add(confermaButton, gbc);
        annullaButton = new JButton();
        annullaButton.setBackground(new Color(-10828087));
        Font annullaButtonFont = this.$$$getFont$$$("Arial", Font.BOLD, 14, annullaButton.getFont());
        if (annullaButtonFont != null) annullaButton.setFont(annullaButtonFont);
        annullaButton.setForeground(new Color(-1));
        annullaButton.setText("Annulla");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 50, 10, 0);
        contentPane.add(annullaButton, gbc);
        logoLabel = new JLabel();
        Font logoLabelFont = this.$$$getFont$$$("Felix Titling", -1, 28, logoLabel.getFont());
        if (logoLabelFont != null) logoLabel.setFont(logoLabelFont);
        logoLabel.setForeground(new Color(-1));
        logoLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 40, 35, 0);
        contentPane.add(logoLabel, gbc);
        labelTel = new JLabel();
        Font labelTelFont = this.$$$getFont$$$("Arial", -1, 16, labelTel.getFont());
        if (labelTelFont != null) labelTel.setFont(labelTelFont);
        labelTel.setForeground(new Color(-1));
        labelTel.setText("Recapito Telefonico");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 10, 10, 0);
        contentPane.add(labelTel, gbc);
        textRecapito = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(textRecapito, gbc);
        panelData = new JPanel();
        panelData.setLayout(new GridBagLayout());
        panelData.setBackground(new Color(-14793370));
        Font panelDataFont = this.$$$getFont$$$("Arial", Font.PLAIN, 12, panelData.getFont());
        if (panelDataFont != null) panelData.setFont(panelDataFont);
        panelData.setForeground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 9;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 10);
        contentPane.add(panelData, gbc);
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