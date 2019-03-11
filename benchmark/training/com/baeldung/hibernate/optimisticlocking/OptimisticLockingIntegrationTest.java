package com.baeldung.hibernate.optimisticlocking;


import LockModeType.OPTIMISTIC;
import LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import org.junit.Test;


public class OptimisticLockingIntegrationTest {
    @Test(expected = OptimisticLockException.class)
    public void givenVersionedEntities_whenConcurrentUpdate_thenOptimisticLockException() throws IOException {
        EntityManager em = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student = em.find(OptimisticLockingStudent.class, 1L);
        EntityManager em2 = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student2 = em2.find(OptimisticLockingStudent.class, 1L);
        student2.setName("RICHARD");
        em2.persist(student2);
        em2.getTransaction().commit();
        em2.close();
        student.setName("JOHN");
        em.persist(student);
        em.getTransaction().commit();
        em.close();
    }

    @Test(expected = OptimisticLockException.class)
    public void givenVersionedEntitiesWithLockByFindMethod_whenConcurrentUpdate_thenOptimisticLockException() throws IOException {
        EntityManager em = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student = em.find(OptimisticLockingStudent.class, 1L, OPTIMISTIC);
        EntityManager em2 = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student2 = em2.find(OptimisticLockingStudent.class, 1L, OPTIMISTIC_FORCE_INCREMENT);
        student2.setName("RICHARD");
        em2.persist(student2);
        em2.getTransaction().commit();
        em2.close();
        student.setName("JOHN");
        em.persist(student);
        em.getTransaction().commit();
        em.close();
    }

    @Test(expected = OptimisticLockException.class)
    public void givenVersionedEntitiesWithLockByRefreshMethod_whenConcurrentUpdate_thenOptimisticLockException() throws IOException {
        EntityManager em = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student = em.find(OptimisticLockingStudent.class, 1L);
        em.refresh(student, OPTIMISTIC);
        EntityManager em2 = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student2 = em2.find(OptimisticLockingStudent.class, 1L);
        em.refresh(student, OPTIMISTIC_FORCE_INCREMENT);
        student2.setName("RICHARD");
        em2.persist(student2);
        em2.getTransaction().commit();
        em2.close();
        student.setName("JOHN");
        em.persist(student);
        em.getTransaction().commit();
        em.close();
    }

    @Test(expected = OptimisticLockException.class)
    public void givenVersionedEntitiesWithLockByLockMethod_whenConcurrentUpdate_thenOptimisticLockException() throws IOException {
        EntityManager em = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student = em.find(OptimisticLockingStudent.class, 1L);
        em.lock(student, OPTIMISTIC);
        EntityManager em2 = OptimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
        OptimisticLockingStudent student2 = em2.find(OptimisticLockingStudent.class, 1L);
        em.lock(student, OPTIMISTIC_FORCE_INCREMENT);
        student2.setName("RICHARD");
        em2.persist(student2);
        em2.getTransaction().commit();
        em2.close();
        student.setName("JOHN");
        em.persist(student);
        em.getTransaction().commit();
        em.close();
    }
}

