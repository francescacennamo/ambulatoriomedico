package setup;

import database.JpaUtil;
import entity.RegistroClinica;
import javax.swing.SwingUtilities;
import boundary.LoginForm; // sostituisci con il nome esatto della tua classe GUI

public class MainInizializzaDatabase {

    public static void main(String[] args) {

        JpaUtil.getInstance();

        RegistroClinica registro = new RegistroClinica();
        DatiTestClinica.popola(registro);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm(); // sostituisci con il nome esatto
                loginForm.setVisible(true);
            }
        });

        System.out.println("Database inizializzato e GUI avviata.");
    }
}