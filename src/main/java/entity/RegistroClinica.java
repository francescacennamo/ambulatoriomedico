package entity;

import database.GestorePersistenza;

public class RegistroClinica {

    private GestorePersistenza gestore;

    public RegistroClinica() {
        this.gestore = new GestorePersistenza();
    }

    public void registraMedico(String nome, String cognome, String email,
                               String password, String recapito,
                               Specializzazione specializzazione) {
        Medico medico = new Medico();
        medico.setNome(nome);
        medico.setCognome(cognome);
        medico.setEmail(email);
        medico.setPassword(password);
        medico.setRecapitoTelefonico(recapito);
        medico.setSpecializzazione(specializzazione);
        gestore.salva(medico);
    }

    public void registraPaziente(String nome, String cognome, String email,
                                 String password, String recapito) {
        Paziente paziente = new Paziente();
        paziente.setNome(nome);
        paziente.setCognome(cognome);
        paziente.setEmail(email);
        paziente.setPassword(password);
        paziente.setRecapitoTelefonico(recapito);
        gestore.salva(paziente);
    }
}