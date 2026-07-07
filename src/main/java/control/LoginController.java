package control;

import entity.GestoreUtente;
import java.util.Map;

public class LoginController {

    /**
     * Esegue il login delegando tutta la logica a GestoreUtente.
     * Restituisce il ruolo dell'utente ("MEDICO", "PAZIENTE", "AMMINISTRATORE")
     * oppure null se le credenziali non sono valide.
     */
    public String login(String email, String password) {
        return GestoreUtente.getInstance().login(email, password);
    }

    /**
     * Recupera l'anagrafica del Paziente tramite la sua email.
     */
    public Map<String, String> ottieniAnagraficaPaziente(String email) {
        return GestoreUtente.getInstance().ottieniAnagraficaPaziente(email);
    }

    /**
     * Recupera l'anagrafica del Medico tramite la sua email.
     */
    public Map<String, String> ottieniAnagraficaMedico(String email) {
        return GestoreUtente.getInstance().ottieniAnagraficaMedico(email);
    }
}