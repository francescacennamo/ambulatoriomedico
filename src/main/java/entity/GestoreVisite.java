package entity;

import database.GestorePersistenza;
import java.time.LocalDate;
import java.util.*;

public class GestoreVisite {

    private static GestoreVisite instance; // singleton
    private GestorePersistenza gestorePersistenza;

    private GestoreVisite() {
        gestorePersistenza = new GestorePersistenza();
    }

    public static GestoreVisite getInstance() {
        if (instance == null) {
            instance = new GestoreVisite();
        }
        return instance;
    }

    // ==========================================
    // METODI PER IL MEDICO E LE FASCE ORARIE
    // ==========================================

    public List<Long> getIdVisitePerMedico(Long idMedico) {
        List<Long> ids = new ArrayList<>();
        List<Visita> visite = gestorePersistenza.cercaPerCampi(Visita.class, Map.of("medico.id", idMedico));
        for (Visita visita : visite) {
            ids.add(visita.getId());
        }
        return ids;
    }

    public Map<Long, String> ottieniFasceDisponibili(Long idMedico, LocalDate dataSelezionata) {
        Medico medico = gestorePersistenza.trovaPerId(Medico.class, idMedico);
        if (medico == null) return new HashMap<>();

        generaFasceSeMancanti(idMedico, dataSelezionata);

        Map<String, Object> criteri = new HashMap<>();
        criteri.put("medico", medico);
        criteri.put("data", dataSelezionata);
        criteri.put("stato", StatoFascia.DISPONIBILE);

        List<FasciaOraria> fasceEntita = gestorePersistenza.cercaPerCampi(FasciaOraria.class, criteri);

        // Ordiniamo le fasce cronologicamente per orario
        fasceEntita.sort(Comparator.comparing(FasciaOraria::getOrario));

        // Prepariamo data e ora attuali per il confronto
        LocalDate dataDiOggi = LocalDate.now();
        java.time.LocalTime oraAttuale = java.time.LocalTime.now();

        // Usiamo LinkedHashMap per mantenere l'ordine cronologico nell'interfaccia
        Map<Long, String> mappaRisultato = new LinkedHashMap<>();

        for (FasciaOraria f : fasceEntita) {
            boolean fasciaValida = true;

            // Se l'utente sta guardando la giornata di OGGI, dobbiamo controllare l'orario
            if (dataSelezionata.equals(dataDiOggi)) {
                // Estraggo l'orario di inizio (es. da "09:00 - 09:30" prendo "09:00")
                String orarioInizioStr = f.getOrario().split(" - ")[0];
                java.time.LocalTime orarioInizioFascia = java.time.LocalTime.parse(orarioInizioStr);

                // Se l'orario di inizio della fascia è prima di adesso, la salto
                if (orarioInizioFascia.isBefore(oraAttuale)) {
                    fasciaValida = false;
                }
            }
            // Controllo di sicurezza: se la data per qualche motivo è passata, la nascondiamo
            else if (dataSelezionata.isBefore(dataDiOggi)) {
                fasciaValida = false;
            }

            // Se la fascia ha passato i controlli temporali, la aggiungiamo alla lista visibile
            if (fasciaValida) {
                mappaRisultato.put(f.getId(), f.getOrario());
            }
        }

        return mappaRisultato;
    }

    private void generaFasceSeMancanti(Long idMedico, LocalDate data) {
        Map<String, Object> filtriFasce = Map.of("medico.id", idMedico, "data", data);
        if (!gestorePersistenza.cercaPerCampi(FasciaOraria.class, filtriFasce).isEmpty()) return;

        String nomeGiorno = switch (data.getDayOfWeek()) {
            case MONDAY -> "Lunedì"; case TUESDAY -> "Martedì"; case WEDNESDAY -> "Mercoledì";
            case THURSDAY -> "Giovedì"; case FRIDAY -> "Venerdì"; case SATURDAY -> "Sabato"; case SUNDAY -> "Domenica";
        };

        List<Disponibilita> listaDisp = gestorePersistenza.cercaPerCampi(Disponibilita.class, Map.of("medico.id", idMedico, "giorno", nomeGiorno));
        Medico medico = gestorePersistenza.trovaPerId(Medico.class, idMedico);

        if (medico != null) {
            for (Disponibilita disp : listaDisp) {
                String[] parti = disp.getFasciaOraria().split(" - ");
                if (parti.length == 2) {
                    java.time.LocalTime inizio = java.time.LocalTime.parse(parti[0]);
                    java.time.LocalTime fine = java.time.LocalTime.parse(parti[1]);
                    while (inizio.isBefore(fine)) {
                        java.time.LocalTime succ = inizio.plusMinutes(30);
                        gestorePersistenza.salva(new FasciaOraria(inizio + " - " + succ, data, medico));
                        inizio = succ;
                    }
                }
            }
        }
    }

    public boolean prenotaVisita(Long idMedico, Long idFascia, Long idPaziente, String telefonoVisita, boolean perAltro, String nomeAltro, String cognomeAltro) {
        Medico medico = gestorePersistenza.trovaPerId(Medico.class, idMedico);
        FasciaOraria fascia = gestorePersistenza.trovaPerId(FasciaOraria.class, idFascia);
        Paziente paziente = gestorePersistenza.trovaPerId(Paziente.class, idPaziente);

        if (medico == null || fascia == null || paziente == null || fascia.getStato() != StatoFascia.DISPONIBILE) {
            return false;
        }

        Visita visita = new Visita(paziente, medico, fascia);
        visita.setRecapitoFornito(telefonoVisita); // Salviamo il recapito specifico della visita

        if (perAltro) {
            visita.setBeneficiarioNome(nomeAltro);
            visita.setBeneficiarioCognome(cognomeAltro);
        }

        fascia.setStato(StatoFascia.PRENOTATA);

        if (gestorePersistenza.salva(visita)) {
            gestorePersistenza.aggiorna(fascia);
            return true;
        }
        return false;
    }

    // ==========================================
    // METODI AGGIUNTI PER IL PAZIENTE E ANNULLAMENTO
    // ==========================================

    public List<Long> getIdVisitePerPaziente(Long idPaziente) {
        List<Long> ids = new ArrayList<>();
        // Cerca le visite usando l'id del paziente
        List<Visita> visite = gestorePersistenza.cercaPerCampi(Visita.class, Map.of("paziente.id", idPaziente));
        for (Visita visita : visite) {
            ids.add(visita.getId());
        }
        return ids;
    }

    public Map<String, Object> getDettaglioVisita(Long idVisita) {
        Visita visita = gestorePersistenza.trovaPerId(Visita.class, idVisita);
        Map<String, Object> dettaglio = new HashMap<>();

        if (visita == null) return dettaglio;

        dettaglio.put("paziente", visita.getPaziente().getNome() + " " + visita.getPaziente().getCognome());
        dettaglio.put("data", visita.getFasciaOraria().getData());
        dettaglio.put("orario", visita.getFasciaOraria().getOrario());
        dettaglio.put("stato", visita.getStato());
        dettaglio.put("beneficiarioNome", visita.getBeneficiarioNome());
        dettaglio.put("beneficiarioCognome", visita.getBeneficiarioCognome());
        dettaglio.put("recapitoFornito", visita.getRecapitoFornito());

        // AGGIUNTE FONDAMENTALI PER VISITA PAZIENTE FORM:
        dettaglio.put("medico", visita.getMedico().getNome() + " " + visita.getMedico().getCognome());

        if (visita.getMedico().getSpecializzazione() != null) {
            dettaglio.put("specializzazione", visita.getMedico().getSpecializzazione().getNome());
        } else {
            dettaglio.put("specializzazione", "Specializzazione N/D");
        }

        return dettaglio;
    }

    public boolean annullaVisita(Long idVisita) {
        Visita visita = gestorePersistenza.trovaPerId(Visita.class, idVisita);

        if (visita != null && visita.getFasciaOraria() != null) {

            // Controllo di sicurezza lato Backend: la visita è passata o entro le 24 ore?
            LocalDate dataVisita = visita.getFasciaOraria().getData();
            String orarioInizioStr = visita.getFasciaOraria().getOrario().split(" - ")[0];

            java.time.LocalTime orarioVisita = java.time.LocalTime.parse(orarioInizioStr);
            java.time.LocalDateTime dataOraVisita = java.time.LocalDateTime.of(dataVisita, orarioVisita);

            // Se "adesso" è oltre la scadenza (cioè dataOraVisita meno 24 ore), blocchiamo tutto
            if (java.time.LocalDateTime.now().isAfter(dataOraVisita.minusHours(24))) {
                return false;
            }

            // Aggiorna lo stato della visita
            visita.setStato(StatoVisita.ANNULLATA);

            // Libera la fascia oraria per renderla di nuovo prenotabile per altri
            visita.getFasciaOraria().setStato(StatoFascia.DISPONIBILE);
            gestorePersistenza.aggiorna(visita.getFasciaOraria());

            // Salva le modifiche
            gestorePersistenza.aggiorna(visita);
            return true;
        }
        return false;
    }
}