package Entity;
import java.time.LocalDate;
import jakarta.persistence.*;
@Entity

public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private FasciaOraria fasciaOraria;

    @Enumerated(EnumType.STRING)
    private StatoVisita stato;

    @ManyToOne
    private Medico medico;

    @ManyToOne
    private Paziente paziente;

    public Visita() {
        this.stato = StatoVisita.PRENOTATA;
    }

    public Long getId1() {
        return id;
    }

    public void setId1(Long id1) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public FasciaOraria getFasciaOraria() {
        return fasciaOraria;
    }

    public void setFasciaOraria(FasciaOraria fasciaOraria) {
        this.fasciaOraria = fasciaOraria;
    }
    public StatoVisita getStato() {
        return stato;
    }

    public void setStato(StatoVisita stato) {
        this.stato = stato;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Paziente getPaziente() {
        return paziente;
    }

    public void setPaziente(Paziente paziente) {
        this.paziente = paziente;
    }
}