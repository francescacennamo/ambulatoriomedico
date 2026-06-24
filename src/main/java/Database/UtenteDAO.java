package Database;

import Entity.Utente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UtenteDAO {

    public Utente login(String email, String password) {

        EntityManager em = JpaUtil.getEntityManager();

        try {

            TypedQuery<Utente> query =
                    em.createQuery(
                            "SELECT u FROM Utente u " +
                                    "WHERE u.email = :email " +
                                    "AND u.password = :password",
                            Utente.class);

            query.setParameter("email", email);
            query.setParameter("password", password);

            return query.getSingleResult();

        } catch (Exception e) {

            return null;

        } finally {

            em.close();
        }
    }
    //questo metodo serve per trovare l'utente a partire dalla mail, quindi c'è
    //una query che restituisce l'utente dalla ricerca della mail
    public Utente findByEmail(String email) {

        EntityManager em = JpaUtil.getEntityManager();

        try {

            TypedQuery<Utente> query =
                    em.createQuery(
                            "FROM Utente u WHERE u.email = :email",
                            Utente.class);

            query.setParameter("email", email);

            return query.getSingleResult();

        } catch (Exception e) {

            return null;

        } finally {

            em.close();
        }
    }
    //quando cambio password devo aggiornare il record nel database
    public void update(Utente utente) {

        EntityManager em = JpaUtil.getEntityManager();

        em.getTransaction().begin();

        em.merge(utente); //come hibernate aggiorna il record, prende l'utente
        // e sincronizza con il db

        em.getTransaction().commit();

        em.close();
    }
}