package test;

import Entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DataLoader {

    public static void main(String[] args) {

        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ambulatorio_PU");

        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Specializzazione
        Specializzazione cardiologia = new Specializzazione();
        cardiologia.setNome("Cardiologia");
        cardiologia.setDescrizione("Visite cardiologiche");

        em.persist(cardiologia);

        // Medico
        Medico medico = new Medico();
        medico.setNome("Mario");
        medico.setCognome("Rossi");
        medico.setEmail("mario.rossi@ambulatorio.it");
        medico.setPassword("password");
        medico.setSpecializzazione(cardiologia);

    //rendo l'oggetto persistente
        em.persist(medico);

        // Paziente
        Paziente paziente = new Paziente();
        paziente.setNome("Luca");
        paziente.setCognome("Bianchi");
        paziente.setEmail("luca.bianchi@gmail.com");
        paziente.setPassword("password");

        em.persist(paziente);

        em.getTransaction().commit();

        em.close();
        emf.close();

        System.out.println("Dati caricati correttamente");
    }
}