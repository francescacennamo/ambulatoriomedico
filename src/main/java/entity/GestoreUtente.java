package entity;

import database.GestorePersistenza;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestoreUtente {

    private static GestoreUtente instance; // singleton
    private final GestorePersistenza gestorePersistenza;
    private final Map<String, String> ruoli;

    private GestoreUtente() {
        this.gestorePersistenza = new GestorePersistenza();
        this.ruoli = new HashMap<>();
        ruoli.put("entity.Medico", "MEDICO");
        ruoli.put("entity.Paziente", "PAZIENTE");
        ruoli.put("entity.Amministratore", "AMMINISTRATORE");
    }

    public static GestoreUtente getInstance() {
        if (instance == null) {
            instance = new GestoreUtente();
        }
        return instance;
    }

    /**
     * Esegue il login e restituisce il ruolo dell'utente autenticato,
     * oppure null se le credenziali non sono valide.
     */
    public String login(String email, String password) {

        Map<String, Object> campiLogin = new HashMap<>();
        campiLogin.put("email", email);
        campiLogin.put("password", password);

        Utente utente = gestorePersistenza.cercaPrimoPerCampi(Utente.class, campiLogin);

        if (utente == null) {
            return null;
        }

        String nomeClasse = utente.getClass().getName();
        return ruoli.get(nomeClasse);
    }

    /**
     * Recupera l'anagrafica del Paziente tramite la sua email,
     * mappata in stringhe semplici (mai l'entity nuda verso la boundary).
     */
    public Map<String, String> ottieniAnagraficaPaziente(String email) {
        Map<String, String> datiAnagrafici = new HashMap<>();

        if (email == null || email.trim().isEmpty()) {
            return datiAnagrafici;
        }

        Paziente p = gestorePersistenza.cercaPrimoPerCampi(
                Paziente.class, Map.of("email", email.trim()));

        if (p != null) {
            datiAnagrafici.put("nome", p.getNome());
            datiAnagrafici.put("cognome", p.getCognome());
            datiAnagrafici.put("email", p.getEmail());
            datiAnagrafici.put("recapito", p.getRecapitoTelefonico());
        }

        return datiAnagrafici;
    }

    /**
     * Recupera l'anagrafica del Medico tramite la sua email,
     * mappata in stringhe semplici.
     */
    public Map<String, String> ottieniAnagraficaMedico(String email) {
        Map<String, String> dati = new HashMap<>();

        if (email == null || email.isBlank()) {
            return dati;
        }

        Medico medico = gestorePersistenza.cercaPrimoPerCampi(
                Medico.class, Map.of("email", email));

        if (medico != null) {
            dati.put("id", medico.getId().toString());
            dati.put("nome", medico.getNome());
            dati.put("cognome", medico.getCognome());
            dati.put("email", medico.getEmail());
            dati.put("recapito", medico.getRecapitoTelefonico());
        }

        return dati;
    }
    public Map<Long, String> ottieniMappaSpecializzazioni() {
        List<Specializzazione> lista = gestorePersistenza.cercaPerCampi(Specializzazione.class, Map.of());
        Map<Long, String> mappa = new HashMap<>();
        for (Specializzazione s : lista) {
            mappa.put(s.getId(), s.getNome());
        }
        return mappa;
    }

    public Map<Long, String> ottieniMediciPerSpecializzazione(Long idSpecializzazione) {
        List<Medico> listaMedici = gestorePersistenza.cercaPerCampo(Medico.class, "specializzazione.id", idSpecializzazione);
        Map<Long, String> mappaMedici = new HashMap<>();
        for (Medico m : listaMedici) {
            mappaMedici.put(m.getId(), m.getCognome() + " " + m.getNome());
        }
        return mappaMedici;
    }
}
