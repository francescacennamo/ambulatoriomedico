package Entity;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
@Entity

public class Medico extends Utente {

    @OneToOne
    private Specializzazione specializzazione;

    @Enumerated(EnumType.STRING)
    private StatoAccount statoAccount;

    @OneToMany(mappedBy = "medico")
    private List<Disponibilita> disponibilita;

    @OneToMany(mappedBy = "medico")
    private List<Visita> listaVisite;

    public Medico() {
        this.statoAccount = StatoAccount.IN_ATTESA;
        this.disponibilita = new ArrayList<>();
        this.listaVisite = new ArrayList<>();
    }


    public StatoAccount getStatoAccount() {
        return statoAccount;
    }

    public void setStatoAccount(StatoAccount statoAccount) {
        this.statoAccount = statoAccount;
    }

    public List<Disponibilita> getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(List<Disponibilita> disponibilita) {
        this.disponibilita = disponibilita;
    }

    public List<Visita> getListaVisite() {
        return listaVisite;
    }

    public void setListaVisite(List<Visita> listaVisite) {
        this.listaVisite = listaVisite;
    }
}