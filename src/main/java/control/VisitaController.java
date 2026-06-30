package control;

import database.GestorePersistenza;
import entity.Visita;
import java.util.List;
import java.util.Map;

public class VisitaController {

    private final GestorePersistenza gestorePersistenza;

    public VisitaController() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public List<Visita> getVisitePerMedico(Long idMedico) {
        return gestorePersistenza.cercaPerCampi(
                Visita.class,
                Map.of("medico.id", idMedico)
        );
    }
}