package com.baeldung.hibernate.pessimisticlocking;


import LockModeType.PESSIMISTIC_FORCE_INCREMENT;
import LockModeType.PESSIMISTIC_READ;
import LockModeType.PESSIMISTIC_WRITE;
import com.vividsolutions.jts.util.Assert;
import java.io.IOException;
import org.junit.Test;


public class BasicPessimisticLockingIntegrationTest {
    @Test
    public void givenFoundRecordWithPessimisticRead_whenFindingNewOne_PessimisticLockExceptionThrown() {
        try {
            EntityManager entityManager = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            entityManager.find(PessimisticLockingStudent.class, 1L, PESSIMISTIC_READ);
            EntityManager entityManager2 = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            entityManager2.find(PessimisticLockingStudent.class, 1L, PESSIMISTIC_READ);
            entityManager.close();
            entityManager2.close();
        } catch (Exception e) {
            Assert.isTrue((e instanceof PessimisticLockException));
        }
    }

    @Test
    public void givenRecordWithPessimisticReadQuery_whenQueryingNewOne_PessimisticLockExceptionThrown() throws IOException {
        try {
            EntityManager entityManager = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            Query query = entityManager.createQuery("from Student where studentId = :studentId");
            query.setParameter("studentId", 1L);
            query.setLockMode(PESSIMISTIC_WRITE);
            query.getResultList();
            EntityManager entityManager2 = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            Query query2 = entityManager2.createQuery("from Student where studentId = :studentId");
            query2.setParameter("studentId", 1L);
            query2.setLockMode(PESSIMISTIC_READ);
            query2.getResultList();
            entityManager.close();
            entityManager2.close();
        } catch (Exception e) {
            Assert.isTrue((e instanceof PessimisticLockException));
        }
    }

    @Test
    public void givenRecordWithPessimisticReadLock_whenFindingNewOne_PessimisticLockExceptionThrown() {
        try {
            EntityManager entityManager = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            PessimisticLockingStudent resultStudent = entityManager.find(PessimisticLockingStudent.class, 1L);
            entityManager.lock(resultStudent, PESSIMISTIC_READ);
            EntityManager entityManager2 = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            entityManager2.find(PessimisticLockingStudent.class, 1L, PESSIMISTIC_FORCE_INCREMENT);
            entityManager.close();
            entityManager2.close();
        } catch (Exception e) {
            Assert.isTrue((e instanceof PessimisticLockException));
        }
    }

    @Test
    public void givenRecordAndRefreshWithPessimisticRead_whenFindingWithPessimisticWrite_PessimisticLockExceptionThrown() {
        try {
            EntityManager entityManager = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            PessimisticLockingStudent resultStudent = entityManager.find(PessimisticLockingStudent.class, 1L);
            entityManager.refresh(resultStudent, PESSIMISTIC_FORCE_INCREMENT);
            EntityManager entityManager2 = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            entityManager2.find(PessimisticLockingStudent.class, 1L, PESSIMISTIC_WRITE);
            entityManager.close();
            entityManager2.close();
        } catch (Exception e) {
            Assert.isTrue((e instanceof PessimisticLockException));
        }
    }

    @Test
    public void givenRecordWithPessimisticRead_whenUpdatingRecord_PessimisticLockExceptionThrown() {
        try {
            EntityManager entityManager = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            PessimisticLockingStudent resultStudent = entityManager.find(PessimisticLockingStudent.class, 1L);
            entityManager.refresh(resultStudent, PESSIMISTIC_READ);
            EntityManager entityManager2 = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            PessimisticLockingStudent resultStudent2 = entityManager2.find(PessimisticLockingStudent.class, 1L);
            resultStudent2.setName("Change");
            entityManager2.persist(resultStudent2);
            entityManager2.getTransaction().commit();
            entityManager.close();
            entityManager2.close();
        } catch (Exception e) {
            Assert.isTrue((e instanceof PessimisticLockException));
        }
    }

    @Test
    public void givenRecordWithPessimisticWrite_whenUpdatingRecord_PessimisticLockExceptionThrown() {
        try {
            EntityManager entityManager = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            PessimisticLockingStudent resultStudent = entityManager.find(PessimisticLockingStudent.class, 1L);
            entityManager.refresh(resultStudent, PESSIMISTIC_WRITE);
            EntityManager entityManager2 = BasicPessimisticLockingIntegrationTest.getEntityManagerWithOpenTransaction();
            PessimisticLockingStudent resultStudent2 = entityManager2.find(PessimisticLockingStudent.class, 1L);
            resultStudent2.setName("Change");
            entityManager2.persist(resultStudent2);
            entityManager2.getTransaction().commit();
            entityManager.close();
            entityManager2.close();
        } catch (Exception e) {
            Assert.isTrue((e instanceof PessimisticLockException));
        }
    }
}

