package Control;

import Database.UtenteDAO;
import Entity.Utente;

public class LoginController {

    public Utente login(String email, String password) {

        UtenteDAO dao = new UtenteDAO();

        return dao.login(email, password);
    }
}