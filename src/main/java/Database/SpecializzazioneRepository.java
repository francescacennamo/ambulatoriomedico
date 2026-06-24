package Database;

import Entity.Specializzazione;
import jakarta.persistence.EntityManager;
import java.util.List;

public class SpecializzazioneRepository {

    public List<Specializzazione> findAll() {

        EntityManager em = JpaUtil.getEntityManager();

        try {

            return em.createQuery(
                            "SELECT s FROM Specializzazione s",
                            Specializzazione.class)
                    .getResultList();

        } finally {

            if (em != null) {
                em.close();
            }
        }
    }
}