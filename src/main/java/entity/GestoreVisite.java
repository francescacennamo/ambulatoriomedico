package entity;

import database.GestorePersistenza;
import java.time.LocalDate;
import java.util.*;

public class GestoreVisite {
    private static GestoreVisite instance;
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

    public List<Long> getIdVisitePerMedico(Long idMedico) {
        List<Long> ids = new ArrayList<>();
        List<Visita> visite = gestorePersistenza.cercaPerCampi(Visita.class, Map.of("medico.id", idMedico));
        for (Visita visita : visite) ids.add(visita.getId());
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
        dettaglio.put("recapitoFornito", visita.getRecapitoFornito()); // Aggiunto ai dettagli
        return dettaglio;
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
        Map<Long, String> mappaRisultato = new LinkedHashMap<>();
        for (FasciaOraria f : fasceEntita) mappaRisultato.put(f.getId(), f.getOrario());
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

    // MODIFICA: Aggiunto il parametro 'telefonoVisita'
    public boolean prenotaVisita(Long idMedico, Long idFascia, Long idPaziente, String telefonoVisita, boolean perAltro, String nomeAltro, String cognomeAltro) {
        Medico medico = gestorePersistenza.trovaPerId(Medico.class, idMedico);
        FasciaOraria fascia = gestorePersistenza.trovaPerId(FasciaOraria.class, idFascia);
        Paziente paziente = gestorePersistenza.trovaPerId(Paziente.class, idPaziente);

        if (medico == null || fascia == null || paziente == null || fascia.getStato() != StatoFascia.DISPONIBILE) {
            return false;
        }

        Visita visita = new Visita(paziente, medico, fascia);
        visita.setRecapitoFornito(telefonoVisita); // Salviamo il telefono nella Visita

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
}