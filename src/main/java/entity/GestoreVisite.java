package entity;

import database.GestorePersistenza; //serve per poter interrogare il database

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestoreVisite {
    private static GestoreVisite instance; //singleton
    private GestorePersistenza gestorePersistenza;

    private GestoreVisite() { //private per evitare di poter fare new
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
        //Dice al gestore persistenza di cercare tutte le entity visita
        // in cui il campo idmedico è uguale all'id ricevto
        List<Visita> visite = gestorePersistenza.cercaPerCampi(Visita.class, Map.of("medico.id", idMedico));

        for (Visita visita : visite) {
            ids.add(visita.getId());
        }

        return ids;
    }

    //Restituisce il dettaglio di una visita.
    public Map<String, Object> getDettaglioVisita(Long idVisita) {

        Visita visita = gestorePersistenza.trovaPerId(Visita.class, idVisita);

        Map<String, Object> dettaglio = new HashMap<>();

        if (visita == null) {
            return dettaglio; //quidi restituisce una mappa vuota
        }

        dettaglio.put("paziente", visita.getPaziente().getNome() + " " + visita.getPaziente().getCognome());

        dettaglio.put("data", visita.getFasciaOraria().getData());

        dettaglio.put("orario", visita.getFasciaOraria().getOrario());

        dettaglio.put("stato", visita.getStato());

        dettaglio.put("beneficiarioNome", visita.getBeneficiarioNome());

        dettaglio.put("beneficiarioCognome", visita.getBeneficiarioCognome());

        return dettaglio;
    }
}