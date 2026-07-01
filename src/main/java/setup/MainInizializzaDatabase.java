package setup;

import database.JpaUtil;
import entity.RegistroClinica;
import javax.swing.SwingUtilities;
import boundary.LoginForm;

public class MainInizializzaDatabase {

    public static void main(String[] args) {

        // Inizializza l'istanza unica di JPA (Singleton)
        JpaUtil.getInstance();

        // Istanziamo il registro ed eseguiamo il popolamento statico dei dati (Stile Professore)
        RegistroClinica registro = new RegistroClinica();
        DatiTestClinica.popola(registro);

        // Apertura controllata della GUI tramite l'Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.apriLoginForm();
            }
        });

        System.out.println("Database inizializzato e GUI avviata.");
    }
}