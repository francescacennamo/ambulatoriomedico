package Control;

import Database.SpecializzazioneRepository;
import Entity.Specializzazione;
import java.util.List;

public class PrenotazioneController {
    private SpecializzazioneRepository specializzazioneRepo;

    public PrenotazioneController() {
        // Il controller istanzia il repository per accedere ai dati (GRASP Creator/Expert)
        this.specializzazioneRepo = new SpecializzazioneRepository();
    }

    /**
     * Recupera l'elenco di tutte le specializzazioni disponibili nel database
     */
    public List<Specializzazione> ottieniSpecializzazioni() {
        return specializzazioneRepo.findAll();
    }
}