package entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Visita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Rimosso LocalDate data perché è già dentro FasciaOraria

    @OneToOne // Una visita occupa esattamente una fascia oraria
    @JoinColumn(name = "fasciaoraria_id")
    private FasciaOraria fasciaOraria;

    @Enumerated(EnumType.STRING)
    private StatoVisita stato;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne
    @JoinColumn(name = "paziente_id")
    private Paziente paziente;

    public Visita() {
        this.stato = StatoVisita.PRENOTATA;
    }

    public Visita(Paziente paziente, Medico medico, FasciaOraria fasciaOraria) {
        this.paziente = paziente;
        this.medico = medico;
        this.fasciaOraria = fasciaOraria;
        this.stato = StatoVisita.PRENOTATA;
    }

    // Mantieni i tuoi getter e setter standard modificando il tipo di FasciaOraria
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FasciaOraria getFasciaOraria() { return fasciaOraria; }
    public void setFasciaOraria(FasciaOraria fasciaOraria) { this.fasciaOraria = fasciaOraria; }
    public StatoVisita getStato() { return stato; }
    public void setStato(StatoVisita stato) { this.stato = stato; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
    public Paziente getPaziente() { return paziente; }
    public void setPaziente(Paziente paziente) { this.paziente = paziente; }
}