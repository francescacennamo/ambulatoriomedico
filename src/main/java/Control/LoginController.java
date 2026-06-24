package Control;

import Database.UtenteDAO;
import Entity.Utente;

public class LoginController {

    public Utente login(String email, String password) {

        UtenteDAO dao = new UtenteDAO();

        return dao.login(email, password);
    }
    //serve per controllare se esiste nel database
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
    //cerca l'utente, cambia la password, salva nel db
    public void aggiornaPassword(String email, String nuovaPassword) {

        UtenteDAO dao = new UtenteDAO();

        Utente utente = dao.findByEmail(email);

        if (utente != null) {
        //con setPassword cambia l'oggetto java
            utente.setPassword(nuovaPassword);
        //con update cambia anche nel db
            dao.update(utente);
        }
    }
}