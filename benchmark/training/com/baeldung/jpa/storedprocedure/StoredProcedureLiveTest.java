package com.baeldung.jpa.storedprocedure;


import ParameterMode.IN;
import com.baeldung.jpa.model.Car;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.StoredProcedureQuery;
import org.junit.Assert;
import org.junit.Test;


public class StoredProcedureLiveTest {
    private static EntityManagerFactory factory = null;

    private static EntityManager entityManager = null;

    @Test
    public void createCarTest() {
        final EntityTransaction transaction = StoredProcedureLiveTest.entityManager.getTransaction();
        try {
            transaction.begin();
            final Car car = new Car("Fiat Marea", 2015);
            StoredProcedureLiveTest.entityManager.persist(car);
            transaction.commit();
        } catch (final Exception e) {
            System.out.println(e.getCause());
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Test
    public void findCarsByYearNamedProcedure() {
        final StoredProcedureQuery findByYearProcedure = StoredProcedureLiveTest.entityManager.createNamedStoredProcedureQuery("findByYearProcedure");
        final StoredProcedureQuery storedProcedure = findByYearProcedure.setParameter("p_year", 2015);
        storedProcedure.getResultList().forEach(( c) -> Assert.assertEquals(new Integer(2015), ((Car) (c)).getYear()));
    }

    @Test
    public void findCarsByYearNoNamed() {
        final StoredProcedureQuery storedProcedure = StoredProcedureLiveTest.entityManager.createStoredProcedureQuery("FIND_CAR_BY_YEAR", Car.class).registerStoredProcedureParameter(1, Integer.class, IN).setParameter(1, 2015);
        storedProcedure.getResultList().forEach(( c) -> Assert.assertEquals(new Integer(2015), ((Car) (c)).getYear()));
    }
}

