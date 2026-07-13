package boundary;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import control.VisitaController;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VisitaPazienteForm {
    private JPanel contentPane;
    private JPanel JPanel1;
    private JScrollPane scorrimento;
    private JPanel PanelCentrale;
    private JLabel logoLabel;
    private JButton indietroButton; // Questo in realtà è il pulsante "Torna alla home"

    private Long idPaziente;
    private JFrame previousFrame;
    private JFrame currentFrame;

    public VisitaPazienteForm(Long idPaziente, JFrame previousFrame) {
        this.idPaziente = idPaziente;
        this.previousFrame = previousFrame;

        // Imposta la velocità di scorrimento della scrollbar
        scorrimento.getVerticalScrollBar().setUnitIncrement(20);

        URL imgURL = getClass().getResource("/logo.png");
        if (imgURL != null) {
            ImageIcon originalIcon = new ImageIcon(imgURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(scaledImage);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setText("");
            logoLabel.setIcon(resizedIcon);
        } else {
            System.err.println("Errore: Impossibile trovare il file del logo.");
        }

        // Tasto "Torna alla Home"
        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFrame.dispose();      // Chiude questa finestra
                previousFrame.setVisible(true); // Riapre la dashboard del paziente
            }
        });
    }

    public JFrame apriForm() {
        JFrame frame = new JFrame("Le mie visite prenotate");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null);

        this.currentFrame = frame;

        caricaVisite(); // Carica le schede dinamicamente

        previousFrame.setVisible(false);
        frame.setVisible(true);

        return frame;
    }

    private void caricaVisite() {
        // Svuota il pannello centrale per evitare duplicati in caso di refresh
        PanelCentrale.removeAll();
        PanelCentrale.setLayout(new BoxLayout(PanelCentrale, BoxLayout.Y_AXIS));

        VisitaController controller = new VisitaController();
        List<Long> ids = controller.getIdVisitePerPaziente(idPaziente);

        for (Long id : ids) {
            Map<String, Object> visita = controller.getDettaglioVisita(id);
            JPanel card = creaSchedaVisita(id, visita);
            PanelCentrale.add(card);
            // Spazio tra una card e l'altra
            PanelCentrale.add(Box.createVerticalStrut(15));
        }

        if (ids.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna visita presente nello storico.");
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Arial", Font.BOLD, 18));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            PanelCentrale.add(lbl);
        }

        PanelCentrale.revalidate();
        PanelCentrale.repaint();
    }

    private JPanel creaSchedaVisita(Long idVisita, Map<String, Object> visita) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Nome del Medico e Specializzazione (il paziente vede da chi deve andare)
        String nomeMedico = (String) visita.getOrDefault("medico", "Medico N/D");
        String specializzazione = (String) visita.getOrDefault("specializzazione", "");
        JLabel medicoLabel = new JLabel("Dott. " + nomeMedico + " - " + specializzazione);
        medicoLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel data = new JLabel(visita.get("data") + "   " + visita.get("orario"));
        data.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel stato = new JLabel("Stato: " + visita.get("stato"));
        stato.setForeground(new Color(33, 150, 243));
        stato.setFont(new Font("Arial", Font.BOLD, 13));

        String nomeBeneficiario = (String) visita.get("beneficiarioNome");
        String cognomeBeneficiario = (String) visita.get("beneficiarioCognome");
        String testoBeneficiario;

        if (nomeBeneficiario == null || nomeBeneficiario.isBlank()) {
            testoBeneficiario = "Visita per te stesso";
        } else {
            testoBeneficiario = "Visita per: " + nomeBeneficiario + " " + cognomeBeneficiario;
        }
        JLabel beneficiario = new JLabel(testoBeneficiario);

        card.add(medicoLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(data);
        card.add(Box.createVerticalStrut(5));
        card.add(stato);
        card.add(Box.createVerticalStrut(5));
        card.add(beneficiario);

        // ==========================
        // Pulsante Annulla Visita
        // ==========================
        if (String.valueOf(visita.get("stato")).equalsIgnoreCase("PRENOTATA")) {
            card.add(Box.createVerticalStrut(10));

            JButton btnAnnulla = new JButton("Annulla visita");
            btnAnnulla.setBackground(new Color(220, 53, 69)); // Rosso per l'annullamento
            btnAnnulla.setForeground(Color.WHITE);
            btnAnnulla.setFocusPainted(false);

            btnAnnulla.addActionListener(e -> {

                try {
                    // 1. Estraiamo la data e l'orario della visita
                    String dataStr = visita.get("data").toString();
                    String orarioInizioStr = visita.get("orario").toString().split(" - ")[0]; // Prende l'orario di inizio

                    java.time.LocalDate dataVisita = java.time.LocalDate.parse(dataStr);
                    java.time.LocalTime orarioVisita = java.time.LocalTime.parse(orarioInizioStr);
                    java.time.LocalDateTime dataOraVisita = java.time.LocalDateTime.of(dataVisita, orarioVisita);

                    // 2. Calcoliamo la scadenza (24 ore prima della visita)
                    java.time.LocalDateTime limiteAnnullamento = dataOraVisita.minusHours(24);

                    // 3. Controlliamo se il momento attuale ha superato il limite
                    if (java.time.LocalDateTime.now().isAfter(limiteAnnullamento)) {
                        JOptionPane.showMessageDialog(currentFrame,
                                "Non è possibile annullare una visita passata o che si terrà entro le prossime 24 ore.",
                                "Impossibile annullare",
                                JOptionPane.WARNING_MESSAGE);
                        return; // Blocca l'esecuzione, non mostra il popup di conferma
                    }
                } catch (Exception ex) {
                    System.err.println("Impossibile fare il parsing della data per la verifica: " + ex.getMessage());
                }

                int scelta = JOptionPane.showConfirmDialog(
                        currentFrame,
                        "Sei sicuro di voler annullare questa prenotazione?",
                        "Conferma Annullamento",
                        JOptionPane.YES_NO_OPTION
                );

                if (scelta == JOptionPane.YES_OPTION) {
                    VisitaController controller = new VisitaController();
                    boolean successo = controller.annullaVisita(idVisita);

                    if (successo) {
                        JOptionPane.showMessageDialog(currentFrame, "Visita annullata con successo.", "Fatto", JOptionPane.INFORMATION_MESSAGE);
                        // Ricarica la vista automaticamente!
                        caricaVisite();
                    } else {
                        JOptionPane.showMessageDialog(currentFrame, "Impossibile annullare la visita. Potrebbe essere già passata.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            card.add(btnAnnulla);
        }

        return card;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        contentPane.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.setBackground(new Color(-16240818));
        JPanel1 = new JPanel();
        JPanel1.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        JPanel1.setBackground(new Color(-14793370));
        contentPane.add(JPanel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Arial", -1, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setForeground(new Color(-5329488));
        label1.setText("Visualizza tutte le visite prenotate");
        JPanel1.add(label1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Arial", Font.BOLD, 18, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setForeground(new Color(-1));
        label2.setText("Le mie visite");
        JPanel1.add(label2, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scorrimento = new JScrollPane();
        JPanel1.add(scorrimento, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        PanelCentrale = new JPanel();
        PanelCentrale.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        PanelCentrale.setBackground(new Color(-14793370));
        scorrimento.setViewportView(PanelCentrale);
        final Spacer spacer1 = new Spacer();
        PanelCentrale.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$("Arial", Font.BOLD, 28, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setForeground(new Color(-1));
        label3.setText("AREA RISERVATA PAZIENTE");
        JPanel1.add(label3, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoLabel = new JLabel();
        logoLabel.setText("Label");
        contentPane.add(logoLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        indietroButton = new JButton();
        indietroButton.setBackground(new Color(-2310814));
        Font indietroButtonFont = this.$$$getFont$$$("Arial", Font.BOLD, 14, indietroButton.getFont());
        if (indietroButtonFont != null) indietroButton.setFont(indietroButtonFont);
        indietroButton.setForeground(new Color(-1));
        indietroButton.setText("<Torna alla home");
        contentPane.add(indietroButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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

}