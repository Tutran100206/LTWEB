package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.iotstar.config.JpaConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;

import java.util.List;

public class CategoryDao implements ICategoryDao {
    @Override
    public void insert(Category category) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Category category) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int cateid) throws Exception {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = em.find(Category.class, cateid);
            if (category == null) {
                throw new Exception("Category không tồn tại với id = " + cateid);
            }
            em.remove(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Category findById(int cateid) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.find(Category.class, cateid);
        } finally {
            em.close();
        }
    }

    @Override
    public Category findByCategoryname(String name) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Category c WHERE c.categoryname = :catename",
                            Category.class
                    )
                    .setParameter("catename", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createNamedQuery("Category.findAll", Category.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> searchByName(String catname) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Category c WHERE LOWER(c.categoryname) LIKE LOWER(:catname)",
                            Category.class
                    )
                    .setParameter("catname", "%" + catname + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createNamedQuery("Category.findAll", Category.class)
                    .setFirstResult((page - 1) * pagesize)
                    .setMaxResults(pagesize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int count() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Long count = em.createQuery("SELECT count(c) FROM Category c", Long.class).getSingleResult();
            return count.intValue();
        } finally {
            em.close();
        }
    }
}
