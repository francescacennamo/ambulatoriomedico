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
}