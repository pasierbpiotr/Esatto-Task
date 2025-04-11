package repository;

import model.WeatherRecord;
import util.HibernateUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class WeatherRepository {
    public void save(WeatherRecord record) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(record);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<WeatherRecord> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT w FROM WeatherRecord w ORDER BY w.observationDateTime",
                            WeatherRecord.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM WeatherRecord").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}