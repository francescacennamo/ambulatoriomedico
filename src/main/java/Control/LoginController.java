package Control;

import Database.UtenteDAO;
import Entity.Utente;

public class LoginController {

    public Utente login(String email, String password) {

        UtenteDAO dao = new UtenteDAO();

        return dao.login(email, password);
    }
    public boolean esisteUtente(String email) {

        UtenteDAO dao = new UtenteDAO();

        return dao.findByEmail(email) != null;
    }
    public String generaCodice() {

        return String.valueOf(
                100000 +
                        (int)(Math.random() * 900000)
        );
    }
    public void aggiornaPassword(String email,
                                 String nuovaPassword) {

        UtenteDAO dao = new UtenteDAO();

        Utente utente = dao.findByEmail(email);

        if (utente != null) {

            utente.setPassword(nuovaPassword);

            dao.update(utente);
        }
    }
}