package repository;

import jakarta.persistence.EntityManager;
import model.SavedCity;
import util.HibernateUtil;

import java.util.List;
import java.util.UUID;

public class CityRepository {

    public void save(SavedCity city) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if(city.getId() == null) {
                em.persist(city);
            } else {
                em.merge(city);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<SavedCity> findAll() {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM SavedCity c", SavedCity.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<SavedCity> findPage(int page, int size) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM SavedCity c ORDER BY c.lastViewed DESC", SavedCity.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public int getTotalPages(int pageSize) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(c) FROM SavedCity c", Long.class)
                    .getSingleResult();
            return (int) Math.ceil((double) count / pageSize);
        } finally {
            em.close();
        }
    }

    public boolean existsByName(String name) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM SavedCity c WHERE LOWER(c.name) = LOWER(:name)",
                            Long.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public List<SavedCity> searchCities(String query, int page, int size) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM SavedCity c WHERE LOWER(c.name) LIKE LOWER(:query) " +
                                    "ORDER BY c.lastViewed DESC",
                            SavedCity.class)
                    .setParameter("query", "%" + query + "%")
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(UUID id) {
        EntityManager em = HibernateUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.find(SavedCity.class, id));
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}