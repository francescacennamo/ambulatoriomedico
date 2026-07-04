package control; // Convenzione Java: i pacchetti vanno scritti in minuscolo

import database.GestorePersistenza;
import entity.Utente;
import entity.Paziente; // Inserito l'import per l'entità Paziente
import entity.Medico;
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

    /**
     * Recupera l'anagrafica completa del Paziente tramite la sua email.
     * Metodo sicuro per il passaggio dati tra Boundary e Database post-login.
     *
     * @param email email del paziente loggato
     * @return l'oggetto Paziente trovato nel DB, oppure null
     */
    /**
     * Recupera l'anagrafica del Paziente tramite la sua email e la mappa in una struttura generica.
     * In questo modo la Boundary riceve solo stringhe e non l'oggetto Entity.
     *
     * @param email email del paziente loggato
     * @return una mappa con nome, cognome ed email, oppure una mappa vuota/null
     */
    public Map<String, String> ottieniAnagraficaPaziente(String email) {
        Map<String, String> datiAnagrafici = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            return datiAnagrafici;
        }


        Paziente p = gestorePersistenza.cercaPrimoPerCampi(
                Paziente.class,
                Map.of("email", email.trim())
        );

        // Trasformiamo i dati dell'Entity in stringhe semplici dentro la mappa
        if (p != null) {
            datiAnagrafici.put("nome", p.getNome());
            datiAnagrafici.put("cognome", p.getCognome());
            datiAnagrafici.put("email", p.getEmail());
            datiAnagrafici.put("recapito", p.getRecapitoTelefonico());
        }

        return datiAnagrafici;
    }

    public Map<String,String> ottieniAnagraficaMedico(String email){

        Map<String,String> dati = new HashMap<>();

        if(email == null || email.isBlank()){
            return dati;
        }

        Medico medico =
                gestorePersistenza.cercaPrimoPerCampi(
                        Medico.class,
                        Map.of("email",email)
                );

        if(medico != null){

            dati.put("id", medico.getId().toString());
            dati.put("nome", medico.getNome());
            dati.put("cognome", medico.getCognome());
            dati.put("email", medico.getEmail());
            dati.put("recapito", medico.getRecapitoTelefonico());

        }

        return dati;
    }
}