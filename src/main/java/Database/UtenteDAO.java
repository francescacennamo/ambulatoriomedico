package Database;

import Entity.Utente;

public class UtenteDAO {

    public Utente login(String email, String password) {

        // per ora simuliamo il database

        if(email.equals("admin@ambulatorio.it")
                && password.equals("1234")) {

            Utente utente = new Utente();

            utente.setEmail(email);

            return utente;
        }

        return null;
    }
}