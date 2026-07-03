package control; // Convenzione Java: i pacchetti vanno scritti in minuscolo

import database.GestorePersistenza;
import entity.Utente;
import java.util.Map;
import java.util.HashMap;

public class LoginController {

    // Gestore della persistenza
    private final GestorePersistenza gestorePersistenza;

    // Mappa che associa il nome della classe al ruolo
    private final Map<String, String> ruoli;

    // Costruttore
    public LoginController() {

        this.gestorePersistenza = new GestorePersistenza();

        this.ruoli = new HashMap<>();

        ruoli.put("entity.Medico", "MEDICO");
        ruoli.put("entity.Paziente", "PAZIENTE");
        ruoli.put("entity.Amministratore", "AMMINISTRATORE");
    }

    /**
     * Esegue il login e restituisce il ruolo dell'utente autenticato.
     *
     * @param email email inserita
     * @param password password inserita
     * @return MEDICO, PAZIENTE, AMMINISTRATORE oppure null se il login fallisce
     */
    public String login(String email, String password) {

        Map<String, Object> criteriLogin = new HashMap<>();

        criteriLogin.put("email", email);
        criteriLogin.put("password", password);

        Utente utente = gestorePersistenza.cercaPrimoPerCampi(
                Utente.class,
                criteriLogin
        );

        if (utente == null) {
            return null;
        }

        String nomeClasse = utente.getClass().getName();

        return ruoli.get(nomeClasse);
    }

}