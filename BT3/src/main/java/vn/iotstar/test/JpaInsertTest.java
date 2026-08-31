package vn.iotstar.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;

public class JpaInsertTest {
    public static void main(String[] args) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            Category cate = new Category();
            cate.setCategoryname("Iphone");
            cate.setImages("abc.jpg");
            cate.setStatus(1);

            Video video = new Video();
            video.setVideoId("v01-" + System.currentTimeMillis());
            video.setTitle("test");
            video.setCategory(cate);
            video.setActive(1);
            video.setViews(0);

            transaction.begin();
            em.persist(cate);
            em.persist(video);
            transaction.commit();
            System.out.println("Insert test thành công.");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
