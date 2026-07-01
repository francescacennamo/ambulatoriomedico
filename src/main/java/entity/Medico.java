package entity;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Medico extends Utente {
    @ManyToOne
    @JoinColumn(name = "specializzazione_id")
    private Specializzazione specializzazione;

    @Enumerated(EnumType.STRING)
    private StatoAccount statoAccount;

    @OneToMany(mappedBy = "medico")
    private List<Disponibilita> disponibilita;

    @OneToMany(mappedBy = "medico")
    private List<Visita> listaVisite;

    public Medico() {
        super();
        this.statoAccount = StatoAccount.APPROVATO;
        this.disponibilita = new ArrayList<>();
        this.listaVisite = new ArrayList<>();
    }

    public Specializzazione getSpecializzazione() {
        return specializzazione;
    }

    public void setSpecializzazione(Specializzazione specializzazione) {
        this.specializzazione = specializzazione;
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