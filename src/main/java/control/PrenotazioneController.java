package control;

import database.GestorePersistenza;
import entity.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PrenotazioneController {

    private final GestorePersistenza gestore;

    public PrenotazioneController() {
        this.gestore = new GestorePersistenza();
    }

    // Restituisce solo una lista di stringhe (i nomi delle specializzazioni) alla GUI
    public List<String> ottieniNomiSpecializzazioni() {
        List<Medico> tuttiIMedici = gestore.cercaPerCampi(Medico.class, Map.of());
        return tuttiIMedici.stream()
                .map(m -> m.getSpecializzazione().getNome())
                .distinct()
                .toList();
    }

    // Riceve il nome della specializzazione (String) e restituisce una lista di stringhe
    // formattate con i dati dei medici (es: "ID: 5 - Dott. Mario Rossi")
    public List<String> ottieniMediciPerSpecializzazione(String nomeSpecializzazione) {
        // Troviamo prima l'entità specializzazione dal nome
        Map<String, Object> filtroSpec = Map.of("nome", nomeSpecializzazione);
        Specializzazione spec = gestore.cercaPrimoPerCampi(Specializzazione.class, filtroSpec);

        if (spec == null) return List.of();

        // Cerchiamo i medici associati
        List<Medico> medici = gestore.cercaPerCampi(Medico.class, Map.of("specializzazione", spec));
        return medici.stream()
                .map(m -> "ID: " + m.getId() + " - Dott. " + m.getNome() + " " + m.getCognome())
                .toList();
    }

    // Riceve l'ID del medico (Long) e la data, e restituisce una lista di stringhe con le fasce orarie
    // (es: "ID_FASCIA: 12 - 09:00 - 09:30 (DISPONIBILE)")
    public List<String> ottieniFasceOrarieMedico(Long idMedico, LocalDate data) {
        Map<String, Object> filtri = Map.of(
                "medico.id", idMedico,
                "data", data
        );
        List<FasciaOraria> fasce = gestore.cercaPerCampi(FasciaOraria.class, filtri);
        return fasce.stream()
                .map(f -> "ID_FASCIA: " + f.getId() + " - " + f.getOrario() + " (" + f.getStato() + ")")
                .toList();
    }

    // Il metodo di prenotazione prende solo gli ID (Long) e i dati testuali dell'utente
    public boolean prenotaVisita(Long idSelezionatoPazienteLoggato, Long idMedico, Long idFascia,
                                 boolean perAltraPersona, String nomeAltro, String cognomeAltro) {

        // 1. Recuperiamo l'entità della fascia oraria tramite l'ID fornito dalla GUI
        FasciaOraria fasciaAggiornata = gestore.trovaPerId(FasciaOraria.class, idFascia);
        if (fasciaAggiornata == null || fasciaAggiornata.getStato() == StatoFascia.PRENOTATA) {
            return false;
        }

        // 2. Recuperiamo il Medico e il Paziente loggato dal DB tramite i loro ID
        Medico medico = gestore.trovaPerId(Medico.class, idMedico);
        Paziente pazienteLoggato = gestore.trovaPerId(Paziente.class, idSelezionatoPazienteLoggato);

        if (medico == null || pazienteLoggato == null) return false;

        Paziente pazienteEffettivo = pazienteLoggato;

        // 3. Gestione terzo paziente
        if (perAltraPersona) {
            Map<String, Object> filtriPaziente = Map.of(
                    "nome", nomeAltro,
                    "cognome", cognomeAltro
            );
            Paziente pazienteTrovato = gestore.cercaPrimoPerCampi(Paziente.class, filtriPaziente);

            if (pazienteTrovato != null) {
                pazienteEffettivo = pazienteTrovato;
            } else {
                Paziente nuovoPaziente = new Paziente();
                nuovoPaziente.setNome(nomeAltro);
                nuovoPaziente.setCognome(cognomeAltro);
                gestore.salva(nuovoPaziente);
                pazienteEffettivo = nuovoPaziente;
            }
        }

        // 4. Aggiornamento dello stato ed inserimento visita
        fasciaAggiornata.setStato(StatoFascia.PRENOTATA);
        Visita nuovaVisita = new Visita(pazienteEffettivo, medico, fasciaAggiornata);

        return gestore.aggiorna(fasciaAggiornata) != null && gestore.salva(nuovaVisita);
    }
}