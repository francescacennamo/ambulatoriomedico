package entity;

import database.GestorePersistenza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestoreVisite {

    private static GestoreVisite instance;

    private GestorePersistenza gestorePersistenza;

    private GestoreVisite() {
        gestorePersistenza = new GestorePersistenza();
    }

    public static GestoreVisite getInstance() {

        if (instance == null) {
            instance = new GestoreVisite();
        }

        return instance;
    }

    public List<Long> getIdVisitePerMedico(Long idMedico) {

        List<Long> ids = new ArrayList<>();

        List<Visita> visite = gestorePersistenza.cercaPerCampi(
                Visita.class,
                Map.of("medico.id", idMedico)
        );

        for (Visita visita : visite) {
            ids.add(visita.getId());
        }

        return ids;
    }

    /**
     * Restituisce il dettaglio di una visita.
     */
    public Map<String, Object> getDettaglioVisita(Long idVisita) {

        Visita visita = gestorePersistenza.trovaPerId(Visita.class, idVisita);

        Map<String, Object> dettaglio = new HashMap<>();

        if (visita == null) {
            return dettaglio;
        }

        dettaglio.put("paziente",
                visita.getPaziente().getNome() + " " +
                        visita.getPaziente().getCognome());

        dettaglio.put("data",
                visita.getFasciaOraria().getData());

        dettaglio.put("orario",
                visita.getFasciaOraria().getOrario());

        dettaglio.put("stato",
                visita.getStato());

        dettaglio.put("beneficiarioNome",
                visita.getBeneficiarioNome());

        dettaglio.put("beneficiarioCognome",
                visita.getBeneficiarioCognome());

        return dettaglio;
    }
}