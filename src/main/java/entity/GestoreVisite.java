package entity;

import database.GestorePersistenza;
import java.time.*;
import java.util.*;

public class GestoreVisite {

    private static GestoreVisite instance;
    private final GestorePersistenza gestorePersistenza;

    private GestoreVisite() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public static GestoreVisite getInstance() {
        if (instance == null) instance = new GestoreVisite();
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

    public Map<Long, String> ottieniFasceDisponibili(Long idMedico, LocalDate data) {
        generaFasceSeMancanti(idMedico, data);

        List<FasciaOraria> fasce = gestorePersistenza.cercaPerCampi(FasciaOraria.class,
                Map.of("medico.id", idMedico, "data", data, "stato", StatoFascia.DISPONIBILE));

        fasce.sort(Comparator.comparing(FasciaOraria::getOrario));

        Map<Long, String> mappaRisultato = new LinkedHashMap<>();
        LocalDateTime oraAttuale = LocalDateTime.now();

        for (FasciaOraria f : fasce) {
            if (isFasciaPrenotabile(f, data, oraAttuale)) {
                mappaRisultato.put(f.getId(), f.getOrario());
            }
        }
        return mappaRisultato;
    }

    private boolean isFasciaPrenotabile(FasciaOraria f, LocalDate data, LocalDateTime oraAttuale) {
        if (data.isBefore(LocalDate.now())) return false;
        if (data.isEqual(LocalDate.now())) {
            String orarioStr = f.getOrario();
            if (orarioStr == null || !orarioStr.contains(" - ")) return false;
            LocalTime inizio = LocalTime.parse(orarioStr.split(" - ")[0]);
            return LocalDateTime.of(data, inizio).isAfter(oraAttuale);
        }
        return true;
    }

    private void generaFasceSeMancanti(Long idMedico, LocalDate data) {
        Medico medico = gestorePersistenza.trovaPerId(Medico.class, idMedico);
        if (medico == null) return;

        // 1. Recuperiamo tutte le fasce GIÀ ESISTENTI per quel medico in quella data
        List<FasciaOraria> fasceEsistenti = gestorePersistenza.cercaPerCampi(
                FasciaOraria.class,
                Map.of("medico.id", idMedico, "data", data)
        );

        // 2. Creiamo una lista (Set) con le stringhe degli orari già presenti per fare un controllo veloce
        Set<String> orariGiaPresenti = new HashSet<>();
        for (FasciaOraria f : fasceEsistenti) {
            orariGiaPresenti.add(f.getOrario());
        }

        // 3. Recuperiamo la disponibilità teorica aggiornata del medico per quel giorno della settimana
        String giornoSettimana = getGiornoItaliano(data);
        List<Disponibilita> dispList = gestorePersistenza.cercaPerCampi(
                Disponibilita.class,
                Map.of("medico.id", idMedico, "giorno", giornoSettimana)
        );

        // 4. Generiamo le fasce e salviamo SOLO quelle che non sono già presenti nel database
        for (Disponibilita disp : dispList) {
            String[] orari = disp.getFasciaOraria().split(" - ");
            LocalTime inizio = LocalTime.parse(orari[0]);
            LocalTime fine = LocalTime.parse(orari[1]);

            while (inizio.isBefore(fine)) {
                LocalTime succ = inizio.plusMinutes(30);
                String orarioFascia = inizio + " - " + succ;

                // Il cuore della modifica: controlliamo se l'orario specifico manca
                if (!orariGiaPresenti.contains(orarioFascia)) {
                    gestorePersistenza.salva(new FasciaOraria(orarioFascia, data, medico));
                }

                inizio = succ;
            }
        }
    }
    public Map<String, String> ottieniDisponibilitaMedico(Long idMedico) {

        List<Disponibilita> lista = gestorePersistenza.cercaPerCampi(
                Disponibilita.class,
                Map.of("medico.id", idMedico));

        Map<String, String> disponibilita = new LinkedHashMap<>();

        for (Disponibilita d : lista) {
            disponibilita.put(d.getGiorno(), d.getFasciaOraria());
        }

        return disponibilita;
    }
    // ==========================================
    // GESTIONE VISITE (Prenotazione/Annullamento)
    // ==========================================

    public boolean prenotaVisita(Long idMedico, Long idFascia, Long idPaziente, String telefonoVisita, boolean perAltro, String nomeAltro, String cognomeAltro) {
        Medico medico = gestorePersistenza.trovaPerId(Medico.class, idMedico);
        FasciaOraria fascia = gestorePersistenza.trovaPerId(FasciaOraria.class, idFascia);
        Paziente paziente = gestorePersistenza.trovaPerId(Paziente.class, idPaziente);

        if (medico == null || fascia == null || paziente == null || fascia.getStato() != StatoFascia.DISPONIBILE) return false;

        Visita visita = new Visita(paziente, medico, fascia);
        visita.setRecapitoFornito(telefonoVisita);
        if (perAltro) {
            visita.setBeneficiarioNome(nomeAltro);
            visita.setBeneficiarioCognome(cognomeAltro);
        }

        fascia.setStato(StatoFascia.PRENOTATA);
        gestorePersistenza.aggiorna(fascia);
        return gestorePersistenza.salva(visita);
    }

    public boolean annullaVisita(Long idVisita) {
        Visita visita = gestorePersistenza.trovaPerId(Visita.class, idVisita);
        if (visita == null || visita.getFasciaOraria() == null) return false;

        LocalDateTime dataOraVisita = LocalDateTime.of(visita.getFasciaOraria().getData(),
                LocalTime.parse(visita.getFasciaOraria().getOrario().split(" - ")[0]));

        if (LocalDateTime.now().isAfter(dataOraVisita.minusHours(24))) return false;

        visita.setStato(StatoVisita.ANNULLATA);
        visita.getFasciaOraria().setStato(StatoFascia.DISPONIBILE);
        gestorePersistenza.aggiorna(visita.getFasciaOraria());
        gestorePersistenza.aggiorna(visita);
        return true;
    }

    public List<Long> getIdVisitePerPaziente(Long idPaziente) {
        List<Long> ids = new ArrayList<>();
        List<Visita> visite = gestorePersistenza.cercaPerCampi(Visita.class, Map.of("paziente.id", idPaziente));
        for (Visita v : visite) ids.add(v.getId());
        return ids;
    }

    public Map<String, Object> getDettaglioVisita(Long idVisita) {
        Visita v = gestorePersistenza.trovaPerId(Visita.class, idVisita);
        Map<String, Object> d = new HashMap<>();
        if (v == null) return d;

        d.put("data", v.getFasciaOraria().getData());
        d.put("orario", v.getFasciaOraria().getOrario());
        d.put("stato", v.getStato());
        d.put("medico", v.getMedico().getNome() + " " + v.getMedico().getCognome());
        d.put("specializzazione", v.getMedico().getSpecializzazione() != null ? v.getMedico().getSpecializzazione().getNome() : "N/D");

        // DATI DEL PAZIENTE E DEL BENEFICIARIO:
        d.put("paziente", v.getPaziente().getNome() + " " + v.getPaziente().getCognome());
        d.put("beneficiarioNome", v.getBeneficiarioNome());
        d.put("beneficiarioCognome", v.getBeneficiarioCognome());
        d.put("recapitoFornito", v.getRecapitoFornito());

        return d;
    }

    private String getGiornoItaliano(LocalDate data) {
        return switch (data.getDayOfWeek()) {
            case MONDAY -> "Lunedì"; case TUESDAY -> "Martedì"; case WEDNESDAY -> "Mercoledì";
            case THURSDAY -> "Giovedì"; case FRIDAY -> "Venerdì"; case SATURDAY -> "Sabato"; case SUNDAY -> "Domenica";
        };
    }
}