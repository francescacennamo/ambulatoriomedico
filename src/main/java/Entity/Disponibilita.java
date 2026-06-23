package Entity;
import jakarta.persistence.*;
@Entity

public class Disponibilita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String giorno;
    private String fasciaOraria;
    @ManyToOne
    private Medico medico;

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Disponibilita() {
    }

    public Disponibilita(String giorno, String fasciaOraria) {
        this.giorno = giorno;
        this.fasciaOraria = fasciaOraria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGiorno() {
        return giorno;
    }

    public void setGiorno(String giorno) {
        this.giorno = giorno;
    }

    public String getFasciaOraria() {
        return fasciaOraria;
    }

    public void setFasciaOraria(String fasciaOraria) {
        this.fasciaOraria = fasciaOraria;
    }
}

