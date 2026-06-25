package setup;

import entity.*;
import database.GestorePersistenza;
import java.time.LocalDate;

public class DatiTestClinica {

    private DatiTestClinica() {}

    public static void popola(RegistroClinica registro) {

        GestorePersistenza gestore = new GestorePersistenza();

        // 1. Specializzazioni
        Specializzazione cardiologia = new Specializzazione();
        cardiologia.setNome("Cardiologia");
        cardiologia.setDescrizione("Malattie del cuore");
        gestore.salva(cardiologia);

        Specializzazione ortopedia = new Specializzazione();
        ortopedia.setNome("Ortopedia");
        ortopedia.setDescrizione("Malattie dell'apparato locomotore");
        gestore.salva(ortopedia);

        // 2. Medici
        Medico marco = new Medico();
        marco.setNome("Marco");
        marco.setCognome("Neri");
        marco.setEmail("marco.neri@clinica.it");
        marco.setPassword("pass123");
        marco.setRecapitoTelefonico("3331234567");
        marco.setSpecializzazione(cardiologia);
        gestore.salva(marco);

        Medico sara = new Medico();
        sara.setNome("Sara");
        sara.setCognome("Greco");
        sara.setEmail("sara.greco@clinica.it");
        sara.setPassword("pass456");
        sara.setRecapitoTelefonico("3339876543");
        sara.setSpecializzazione(ortopedia);
        gestore.salva(sara);

        // 3. Pazienti
        Paziente mario = new Paziente();
        mario.setNome("Mario");
        mario.setCognome("Rossi");
        mario.setEmail("mario.rossi@email.it");
        mario.setPassword("pass789");
        mario.setRecapitoTelefonico("3201234567");
        gestore.salva(mario);

        Paziente anna = new Paziente();
        anna.setNome("Anna");
        anna.setCognome("Bianchi");
        anna.setEmail("anna.bianchi@email.it");
        anna.setPassword("pass000");
        anna.setRecapitoTelefonico("3207654321");
        gestore.salva(anna);

        // 4. Fasce orarie
        FasciaOraria fascia1 = new FasciaOraria("09:00 - 09:30", LocalDate.of(2026, 7, 1), marco);
        gestore.salva(fascia1);

        FasciaOraria fascia2 = new FasciaOraria("10:00 - 10:30", LocalDate.of(2026, 7, 1), marco);
        gestore.salva(fascia2);

        FasciaOraria fascia3 = new FasciaOraria("09:00 - 09:30", LocalDate.of(2026, 7, 2), sara);
        gestore.salva(fascia3);

        FasciaOraria fascia4 = new FasciaOraria("11:00 - 11:30", LocalDate.of(2026, 7, 2), sara);
        gestore.salva(fascia4);

        // 5. Visite
        Visita visita1 = new Visita(mario, marco, fascia1);
        fascia1.setStato(StatoFascia.PRENOTATA);
        gestore.salvaTutti(visita1, fascia1);

        Visita visita2 = new Visita(anna, sara, fascia3);
        fascia3.setStato(StatoFascia.PRENOTATA);
        gestore.salvaTutti(visita2, fascia3);
    }
}