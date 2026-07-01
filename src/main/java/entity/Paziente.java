package entity;
import java.util.List;
import jakarta.persistence.*;
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Paziente extends Utente {
    @OneToMany(mappedBy = "paziente")
    private List<Visita> listaVisite;

    public Paziente() {
        super();
    }

    public List<Visita> getListaVisite() {
        return listaVisite;
    }

    public void setListaVisite(List<Visita> listaVisite) {
        this.listaVisite = listaVisite;
    }
}