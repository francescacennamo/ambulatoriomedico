package Entity;

public class Disponibilita {

    private String giorno;
    private String fasciaOraria;
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

