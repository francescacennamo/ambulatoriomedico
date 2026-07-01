package control;

import database.GestorePersistenza;
import entity.Specializzazione;
import entity.Medico;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PrenotazioneController {

    private GestorePersistenza gp = new GestorePersistenza();
    public PrenotazioneController() {
        this.gp = new GestorePersistenza();
    }

    public Map<Long, String> ottieniMappaSpecializzazioni() {
        List<Specializzazione> lista = gp.cercaPerCampi(Specializzazione.class, Map.of());
        Map<Long, String> mappa = new HashMap<>();
        for (Specializzazione s : lista) {
            mappa.put(s.getId(), s.getNome());
        }
        return mappa;
    }

    public Map<Long, String> ottieniMediciPerSpecializzazione(Long idSpecializzazione) {
        // Cerchiamo i medici che hanno la specializzazione con l'id passato
        // Usiamo il tuo metodo cercaPerCampo(Classe, NomeCampo, Valore)
        List<Medico> listaMedici = gp.cercaPerCampo(Medico.class, "specializzazione.id", idSpecializzazione);

        Map<Long, String> mappaMedici = new HashMap<>();
        for (Medico m : listaMedici) {
            // Uniamo Nome e Cognome per mostrarli chiaramente nella ComboBox
            String nomeCompleto = m.getCognome() + " " + m.getNome();
            mappaMedici.put(m.getId(), nomeCompleto);
        }

        return mappaMedici;
    }
        }

