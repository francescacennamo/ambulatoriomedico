package control;

import entity.GestoreVisite;

import java.util.List;
import java.util.Map;

public class VisitaController {
    public List<Long> getIdVisitePerMedico(Long idMedico) {
        return GestoreVisite.getInstance().getIdVisitePerMedico(idMedico);
    }

    public Map<String, Object> getDettaglioVisita(Long idVisita) {
        return GestoreVisite.getInstance().getDettaglioVisita(idVisita);
    }
    // Trova tutte le visite del paziente
    public List<Long> getIdVisitePerPaziente(Long idPaziente) {
        return GestoreVisite.getInstance().getIdVisitePerPaziente(idPaziente);
    }

    // Annulla la visita
    public boolean annullaVisita(Long idVisita) {
        return GestoreVisite.getInstance().annullaVisita(idVisita);
    }


}
