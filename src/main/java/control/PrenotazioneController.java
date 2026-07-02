package control;

import database.GestorePersistenza;
import entity.Specializzazione;
import entity.Medico;
import entity.FasciaOraria;
import entity.Disponibilita;
import entity.StatoFascia;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PrenotazioneController {

    private GestorePersistenza gp = new GestorePersistenza();
    public PrenotazioneController() {
        this.gp = new GestorePersistenza();
    }

    public Map<Long, String> ottieniMappaSpecializzazioni() {
        List<Specializzazione> lista = gp.cercaPerCampi(Specializzazione.class, Map.of());
        Map<Long, String> mappa = new HashMap<>();
        for (Specializzazione s : lista) {
            mappa.put(s.getId(), s.getNome());
        }
        return mappa;
    }

    public Map<Long, String> ottieniMediciPerSpecializzazione(Long idSpecializzazione) {
        // Cerchiamo i medici che hanno la specializzazione con l'id passato
        // Usiamo il tuo metodo cercaPerCampo(Classe, NomeCampo, Valore)
        List<Medico> listaMedici = gp.cercaPerCampo(Medico.class, "specializzazione.id", idSpecializzazione);

        Map<Long, String> mappaMedici = new HashMap<>();
        for (Medico m : listaMedici) {
            // Uniamo Nome e Cognome per mostrarli chiaramente nella ComboBox
            String nomeCompleto = m.getCognome() + " " + m.getNome();
            mappaMedici.put(m.getId(), nomeCompleto);
        }

        return mappaMedici;
    }

    public void generaFasceSeMancanti(Long idMedico, LocalDate dataSelezionata) {
        // 1. Controlla se esistono già record reali di FasciaOraria per questo medico in questa data
        Map<String, Object> filtriFasce = Map.of(
                "medico.id", idMedico,
                "data", dataSelezionata
        );
        List<FasciaOraria> fasceEsistenti = gp.cercaPerCampi(FasciaOraria.class, filtriFasce);

        // Se ci sono già, interrompiamo (evita duplicazioni)
        if (!fasceEsistenti.isEmpty()) {
            return;
        }

        // 2. Convertiamo il giorno della data in una stringa in italiano (iniziale maiuscola)
        // Es: THURSDAY -> "Giovedì"
        String nomeGiorno = convertiInGiornoItaliano(dataSelezionata.getDayOfWeek());

        // 3. Cerchiamo se il medico ha una Disponibilita teorica registrata per quel giorno
        Map<String, Object> filtriDisp = Map.of(
                "medico.id", idMedico,
                "giorno", nomeGiorno
        );
        List<Disponibilita> listaDisp = gp.cercaPerCampi(Disponibilita.class, filtriDisp);

        // 4. Se troviamo la disponibilità del medico, generiamo le fasce reali
        Medico medico = gp.trovaPerId(Medico.class, idMedico);
        if (medico != null && !listaDisp.isEmpty()) {
            for (Disponibilita disp : listaDisp) {

                // Caso A: Se la tua stringa "fasciaOraria" è un blocco macro (es. "09:00 - 11:00")
                // Possiamo dividerlo in sottomoduli da 30 minuti:
                String[] parti = disp.getFasciaOraria().split(" - ");
                if (parti.length == 2) {
                    java.time.LocalTime inizio = java.time.LocalTime.parse(parti[0]);
                    java.time.LocalTime fine = java.time.LocalTime.parse(parti[1]);

                    while (inizio.isBefore(fine)) {
                        java.time.LocalTime successivo = inizio.plusMinutes(30);
                        String orarioSlot = inizio + " - " + successivo;

                        FasciaOraria nuovaFascia = new FasciaOraria(orarioSlot, dataSelezionata, medico);
                        gp.salva(nuovaFascia);

                        inizio = successivo;
                    }
                }
            /* Caso B: Se nel database metti già singole stringhe (es: "09:00 - 09:30"), usa questo:
            FasciaOraria nuovaFascia = new FasciaOraria(disp.getFasciaOraria(), dataSelezionata, medico);
            gp.salva(nuovaFascia);
            */
            }
        }
    }

    // Metodo di supporto per mappare il DayOfWeek nelle stringhe usate nel database
    private String convertiInGiornoItaliano(java.time.DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Lunedì";
            case TUESDAY -> "Martedì";
            case WEDNESDAY -> "Mercoledì";
            case THURSDAY -> "Giovedì";
            case FRIDAY -> "Venerdì";
            case SATURDAY -> "Sabato";
            case SUNDAY -> "Domenica";
        };
    }
    public Map<Long, String> ottieniFasceDisponibili(Long idMedico, LocalDate dataSelezionata) {
        // 1. Recuperiamo prima l'oggetto Medico intero usando il suo ID
        Medico medico = gp.trovaPerId(Medico.class, idMedico);

        // Se il medico non esiste, restituiamo una mappa vuota
        if (medico == null) {
            return new HashMap<>();
        }

        // 2. Generiamo le fasce prendendole dalla tabella Disponibilita (se non esistono già)
        generaFasceSeMancanti(idMedico, dataSelezionata);

        // 3. Prepariamo i criteri usando l'oggetto Medico intero
        Map<String, Object> criteri = new HashMap<>();
        criteri.put("medico", medico);                      // 🌟 Sostituito "medico.id" con "medico"
        criteri.put("data", dataSelezionata);
        criteri.put("stato", StatoFascia.DISPONIBILE);

        List<FasciaOraria> fasceEntita = gp.cercaPerCampi(FasciaOraria.class, criteri);

        Map<Long, String> mappaRisultato = new HashMap<>();
        for (FasciaOraria f : fasceEntita) {
            mappaRisultato.put(f.getId(), f.getOrario());
        }

        return mappaRisultato;
    }
    }

