package test;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestHibernate {

        public static void main(String[] args) {
            Persistence.createEntityManagerFactory("ambulatorioPU");

            EntityManagerFactory emf =
                    Persistence.createEntityManagerFactory("ambulatorioPU");

            System.out.println("OK");

            emf.close();
        }
    }
