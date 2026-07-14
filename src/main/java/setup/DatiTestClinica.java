package setup;

import entity.*;
import database.GestorePersistenza;
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

        // NUOVI MEDICI AGGIUNTI
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "luigi.verdi@clinica.it")) == null) {
            registro.registraMedico("Luigi", "Verdi", "luigi.verdi@clinica.it", "pass123", "3331112222", "Dermatologia");
        }
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "elena.bruni@clinica.it")) == null) {
            registro.registraMedico("Elena", "Bruni", "elena.bruni@clinica.it", "pass123", "3333334444", "Oculistica");
        }
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "roberto.gialli@clinica.it")) == null) {
            registro.registraMedico("Roberto", "Gialli", "roberto.gialli@clinica.it", "pass123", "3335556666", "Neurologia");
        }
        if (gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "giulia.viola@clinica.it")) == null) {
            registro.registraMedico("Giulia", "Viola", "giulia.viola@clinica.it", "pass123", "3337778888", "Pediatria");
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
        Medico dario = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "dario.bianchi@clinica.it"));
        Medico luigi = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "luigi.verdi@clinica.it"));
        Medico elena = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "elena.bruni@clinica.it"));
        Medico roberto = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "roberto.gialli@clinica.it"));
        Medico giulia = gestore.cercaPrimoPerCampi(Medico.class, Map.of("email", "giulia.viola@clinica.it"));

        // 4. Inserimento delle Disponibilità generali (il piano orario settimanale dei medici)
        List<Disponibilita> dispCaricate = gestore.cercaPerCampi(Disponibilita.class, Map.of());
        if (dispCaricate.isEmpty()) {

            // Marco Neri (Cardiologia)
            if (marco != null) {
                Disponibilita d1 = new Disponibilita("Lunedì", "09:00 - 12:00"); d1.setMedico(marco);
                Disponibilita d2 = new Disponibilita("Giovedì", "15:00 - 18:00"); d2.setMedico(marco);
                gestore.salvaTutti(d1, d2);
            }

            // Sara Greco (Ortopedia)
            if (sara != null) {
                Disponibilita d3 = new Disponibilita("Martedì", "09:00 - 12:00"); d3.setMedico(sara);
                Disponibilita d4 = new Disponibilita("Giovedì", "09:00 - 12:00"); d4.setMedico(sara);
                gestore.salvaTutti(d3, d4);
            }

            // Dario Bianchi (Cardiologia)
            if (dario != null) {
                Disponibilita d5 = new Disponibilita("Mercoledì", "10:00 - 13:00"); d5.setMedico(dario);
                Disponibilita d6 = new Disponibilita("Venerdì", "14:00 - 18:00"); d6.setMedico(dario);
                gestore.salvaTutti(d5, d6);
            }

            // Luigi Verdi (Dermatologia)
            if (luigi != null) {
                Disponibilita d7 = new Disponibilita("Martedì", "15:00 - 18:00"); d7.setMedico(luigi);
                Disponibilita d8 = new Disponibilita("Mercoledì", "09:00 - 12:00"); d8.setMedico(luigi);
                gestore.salvaTutti(d7, d8);
            }

            // Elena Bruni (Oculistica)
            if (elena != null) {
                Disponibilita d9 = new Disponibilita("Lunedì", "14:00 - 18:00"); d9.setMedico(elena);
                Disponibilita d10 = new Disponibilita("Venerdì", "09:00 - 13:00"); d10.setMedico(elena);
                gestore.salvaTutti(d9, d10);
            }

            // Roberto Gialli (Neurologia)
            if (roberto != null) {
                Disponibilita d11 = new Disponibilita("Giovedì", "10:00 - 15:00"); d11.setMedico(roberto);
                gestore.salvaTutti(d11);
            }

            // Giulia Viola (Pediatria)
            if (giulia != null) {
                Disponibilita d12 = new Disponibilita("Lunedì", "09:00 - 13:00"); d12.setMedico(giulia);
                Disponibilita d13 = new Disponibilita("Mercoledì", "14:00 - 18:00"); d13.setMedico(giulia);
                gestore.salvaTutti(d12, d13);
            }
        }
    }
}