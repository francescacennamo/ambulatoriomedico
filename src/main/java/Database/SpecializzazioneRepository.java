package Database;

import Entity.Specializzazione;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SpecializzazioneRepository {
    public List<Specializzazione> findAll() {
        // Usiamo il riferimento esplicito a JpaUtil dello stesso package
        EntityManager em = Database.JpaUtil.getInstance().getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Specializzazione s", Specializzazione.class)
                    .getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}