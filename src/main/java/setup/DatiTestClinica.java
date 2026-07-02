package setup;

import entity.*;
import database.GestorePersistenza;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DatiTestClinica {

    private DatiTestClinica() {
        // Classe di utilità
    }

    public static void popola(RegistroClinica registro) {

        GestorePersistenza gestore = new GestorePersistenza();

        // 1. Registrazione Medici tramite Registro
        // (Controlliamo se non ci sono già per non violare i vincoli UNIQUE)
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "marco.neri@clinica.it")) == null) {
            registro.registraMedico("Marco", "Neri", "marco.neri@clinica.it", "pass123", "3331234567", "Cardiologia");
        }
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "dario.bianchi@clinica.it")) == null) {
            registro.registraMedico("Dario", "Bianchi", "dario.bianchi@clinica.it", "p123", "3934956131", "Cardiologia");
        }
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "sara.greco@clinica.it")) == null) {
            registro.registraMedico("Sara", "Greco", "sara.greco@clinica.it", "pass456", "3339876543", "Ortopedia");
        }

        // 2. Registrazione Pazienti tramite Registro
        if (gestore.cercaPrimoPerCampi(Paziente.class, Map.of("email", "mario.rossi@email.it")) == null) {
            registro.registraPaziente("Mario", "Rossi", "mario.rossi@email.it", "pass789", "3201234567");
        }
        if (gestore.cercaPrimoPerCampi(Paziente.class, Map.of("email", "anna.bianchi@email.it")) == null) {
            registro.registraPaziente("Anna", "Bianchi", "anna.bianchi@email.it", "pass000", "3207654321");
        }

        // 3. Recupero delle istanze salvate per impostare le relazioni successive
        Medico marco = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "marco.neri@clinica.it"));
        Medico sara = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "sara.greco@clinica.it"));
        Paziente mario = gestore.cercaPrimoPerCampi(Paziente.class, Map.of("email", "mario.rossi@email.it"));
        Paziente anna = gestore.cercaPrimoPerCampi(Paziente.class, Map.of("email", "anna.bianchi@email.it"));

        // NEW: 3b. Inserimento delle Disponibilità generali (il piano orario settimanale dei medici)
        List<Disponibilita> dispCaricate = gestore.cercaPerCampi(Disponibilita.class, Map.of());
        if (dispCaricate.isEmpty() && marco != null && sara != null) {

            // Definiamo i giorni lavorativi teorici di Marco Neri
            Disponibilita d1 = new Disponibilita("Lunedì", "09:00 - 12:00");
            d1.setMedico(marco);
            Disponibilita d2 = new Disponibilita("Giovedì", "15:00 - 18:00");
            d2.setMedico(marco);

            // Definiamo i giorni lavorativi teorici di Sara Greco
            Disponibilita d3 = new Disponibilita("Martedì", "09:00 - 12:00");
            d3.setMedico(sara);
            Disponibilita d4 = new Disponibilita("Giovedì", "09:00 - 12:00"); // Oggi è Giovedì!
            d4.setMedico(sara);

            gestore.salvaTutti(d1, d2, d3, d4);
        }

        // 4. Inserimento Fasce Orarie fisse di test (solo se la tabella è attualmente vuota)
        List<FasciaOraria> caricate = gestore.cercaPerCampi(FasciaOraria.class, Map.of());
        if (caricate.isEmpty() && marco != null && sara != null) {

            FasciaOraria fascia1 = new FasciaOraria("09:00 - 09:30", LocalDate.of(2026, 7, 1), marco);
            FasciaOraria fascia2 = new FasciaOraria("10:00 - 10:30", LocalDate.of(2026, 7, 1), marco);
            FasciaOraria fascia3 = new FasciaOraria("09:00 - 09:30", LocalDate.of(2026, 7, 2), sara);
            FasciaOraria fascia4 = new FasciaOraria("11:00 - 11:30", LocalDate.of(2026, 7, 2), sara);

            gestore.salvaTutti(fascia1, fascia2, fascia3, fascia4);

            // 5. Creazione Visite legate alle fasce fisse
            if (mario != null) {
                Visita visita1 = new Visita(mario, marco, fascia1);
                fascia1.setStato(StatoFascia.PRENOTATA);

                gestore.salva(visita1);
                gestore.aggiorna(fascia1);
            }

            if (anna != null) {
                Visita visita2 = new Visita(anna, sara, fascia3);
                fascia3.setStato(StatoFascia.PRENOTATA);

                gestore.salva(visita2);
                gestore.aggiorna(fascia3);
            }
        }
    }
}