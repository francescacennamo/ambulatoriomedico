package entity;

import database.GestorePersistenza;
import java.util.Map;

public class RegistroClinica {

    private GestorePersistenza gestorePersistenza;

    public RegistroClinica() {
        // Il registro usa il GestorePersistenza per rendere persistenti gli oggetti.
        // La logica di business resta qui, l'infrastruttura nel database.
        this.gestorePersistenza = new GestorePersistenza();
    }

    public boolean registraMedico(String nome, String cognome, String email,
                                  String password, String recapito,
                                  String nomeSpecializzazione) {

        // 1. Cerchiamo se esiste già la specializzazione richiesta nel DB
        Specializzazione specializzazione = gestorePersistenza.cercaPrimoPerCampi(
                Specializzazione.class,
                Map.of("nome", nomeSpecializzazione)
        );

        Medico medico = new Medico();
        medico.setNome(nome);
        medico.setCognome(cognome);
        medico.setEmail(email);
        medico.setPassword(password);
        medico.setRecapitoTelefonico(recapito);

        // 2. Se non esiste, la creiamo al volo e salviamo tutto insieme (Stile Professore)
        if (specializzazione == null) {
            specializzazione = new Specializzazione();
            specializzazione.setNome(nomeSpecializzazione);
            specializzazione.setDescrizione("Reparto di " + nomeSpecializzazione);

            medico.setSpecializzazione(specializzazione);
            return gestorePersistenza.salvaTutti(specializzazione, medico);
        } else {
            // 3. Se esiste, agganciamo l'istanza persistente e salviamo solo il medico
            medico.setSpecializzazione(specializzazione);
            return gestorePersistenza.salva(medico);
        }
    }

    public boolean registraPaziente(String nome, String cognome, String email,
                                    String password, String recapito) {
        Paziente paziente = new Paziente();
        paziente.setNome(nome);
        paziente.setCognome(cognome);
        paziente.setEmail(email);
        paziente.setPassword(password);
        paziente.setRecapitoTelefonico(recapito);
        return gestorePersistenza.salva(paziente);
    }
}