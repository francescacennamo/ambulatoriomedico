package Entity;
import java.util.List;

public class Paziente extends Utente {

    private List<Visita> listaVisite;

    public List<Visita> getListaVisite() {
        return listaVisite;
    }

    public void setListaVisite(List<Visita> listaVisite) {
        this.listaVisite = listaVisite;
    }
}