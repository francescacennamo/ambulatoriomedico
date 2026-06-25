package entity;
//
import jakarta.persistence.*;

@Entity
public class Specializzazione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descrizione;

    public Specializzazione() {}

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    @Override
    public String toString() {
        return this.nome; // Sostituisci "nome" con l'attributo che contiene il testo (es. "Cardiologia")
    }
}
