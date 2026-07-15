package control;

import entity.GestoreUtente;
import entity.GestoreVisite;
import java.time.LocalDate;
import java.util.Map;

public class PrenotazioneController {

    public PrenotazioneController() {}

    public Map<String, String> ottieniAnagraficaPazientePerId(Long id) {
        return GestoreUtente.getInstance().ottieniAnagraficaPazientePerId(id);
    }

    public Map<Long, String> ottieniMappaSpecializzazioni() {
        return GestoreUtente.getInstance().ottieniMappaSpecializzazioni();
    }

    public Map<Long, String> ottieniMediciPerSpecializzazione(Long idSpecializzazione) {
        return GestoreUtente.getInstance().ottieniMediciPerSpecializzazione(idSpecializzazione);
    }

    public Map<Long, String> ottieniFasceDisponibili(Long idMedico, LocalDate dataSelezionata) {
        return GestoreVisite.getInstance().ottieniFasceDisponibili(idMedico, dataSelezionata);
    }

    public boolean confermaPrenotazione(Long idMedico, Long idFascia, Long idPaziente, String telefonoVisita, boolean prenotaPerAltro, String nomeAltro, String cognomeAltro) {
        return GestoreVisite.getInstance().prenotaVisita(idMedico, idFascia, idPaziente, telefonoVisita, prenotaPerAltro, nomeAltro, cognomeAltro);
    }
    public Map<String, String> ottieniDisponibilitaMedico(Long idMedico) {
        return GestoreVisite.getInstance().ottieniDisponibilitaMedico(idMedico);
    }
}

