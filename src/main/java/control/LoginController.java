package control; // Convenzione Java: i pacchetti vanno scritti in minuscolo

import database.GestorePersistenza;
import entity.Utente;
import java.util.Map;

public class LoginController {

    // MOTIVAZIONE BCED: Il controller possiede un riferimento al GestorePersistenza.
    // In questo modo il livello di Control coordina l'accesso ai dati senza che il
    // livello Database debba conoscere l'esistenza della classe Utente.
    private final GestorePersistenza gestorePersistenza;

    // Costruttore
    public LoginController() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public Utente login(String email, String password) {

        // 1. Creiamo la mappa con i filtri per la clausola WHERE.
        // Le chiavi ("email", "password") devono corrispondere esattamente
        // ai nomi degli attributi privati definiti nella tua classe Entity Utente.
        Map<String, Object> criteriLogin = Map.of(
                "email", email,
                "password", password
        );

        // 2. Sfruttiamo il metodo generico del gestore.
        // Gli passiamo Utente.class in modo che sappia su quale tabella cercare,
        // e la mappa con i criteri. Internamente farà la query e restituirà
        // l'utente se trovato, oppure null se le credenziali sono errate.
        return gestorePersistenza.cercaPrimoPerCampi(Utente.class, criteriLogin);
    }
}