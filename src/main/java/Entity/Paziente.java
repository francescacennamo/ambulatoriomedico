package Entity;
import java.util.List;
import jakarta.persistence.*;
@Entity

public class Paziente extends Utente {

    @OneToMany(mappedBy = "paziente")
    private List<Visita> listaVisite;

    public List<Visita> getListaVisite() {
        return listaVisite;
    }

    public void setListaVisite(List<Visita> listaVisite) {
        this.listaVisite = listaVisite;
    }
}