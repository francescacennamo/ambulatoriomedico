package Entity;
import java.util.ArrayList;
import java.util.List;

public class Medico extends Utente {

    private Specializzazione specializzazione;
    private StatoAccount statoAccount;
    private List<Disponibilita> disponibilita;
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