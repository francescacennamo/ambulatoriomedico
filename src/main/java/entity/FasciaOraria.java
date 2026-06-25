package entity;
//
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class FasciaOraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orario; // Es: "09:00 - 09:30"
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private StatoFascia stato;

    @ManyToOne
    @JoinColumn(name = "medico_id")
    private Medico medico;

    public FasciaOraria() {
        this.stato = StatoFascia.DISPONIBILE; // Di base è libera
    }

    // Costruttore utile
    public FasciaOraria(String orario, LocalDate data, Medico medico) {
        this.orario = orario;
        this.data = data;
        this.medico = medico;
        this.stato = StatoFascia.DISPONIBILE;
    }

    // GETTER E SETTER
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrario() { return orario; }
    public void setOrario(String orario) { this.orario = orario; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public StatoFascia getStato() { return stato; }
    public void setStato(StatoFascia stato) { this.stato = stato; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    @Override
    public String toString() {
        return orario + " (" + stato + ")";
    }
}