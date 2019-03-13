package com.baeldung.hibernate.aggregatefunctions;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;


public class AggregateFunctionsIntegrationTest {
    private static Session session;

    private static Transaction transaction;

    @Test
    public void whenMaxAge_ThenReturnValue() {
        int maxAge = ((int) (AggregateFunctionsIntegrationTest.session.createQuery("SELECT MAX(age) from Student").getSingleResult()));
        assertThat(maxAge).isEqualTo(25);
    }

    @Test
    public void whenMinAge_ThenReturnValue() {
        int minAge = ((int) (AggregateFunctionsIntegrationTest.session.createQuery("SELECT MIN(age) from Student").getSingleResult()));
        assertThat(minAge).isEqualTo(20);
    }

    @Test
    public void whenAverageAge_ThenReturnValue() {
        Double avgAge = ((Double) (AggregateFunctionsIntegrationTest.session.createQuery("SELECT AVG(age) from Student").getSingleResult()));
        assertThat(avgAge).isEqualTo(22.2);
    }

    @Test
    public void whenCountAll_ThenReturnValue() {
        Long totalStudents = ((Long) (AggregateFunctionsIntegrationTest.session.createQuery("SELECT COUNT(*) from Student").getSingleResult()));
        assertThat(totalStudents).isEqualTo(5);
    }

    @Test
    public void whenSumOfAllAges_ThenReturnValue() {
        Long sumOfAllAges = ((Long) (AggregateFunctionsIntegrationTest.session.createQuery("SELECT SUM(age) from Student").getSingleResult()));
        assertThat(sumOfAllAges).isEqualTo(111);
    }
}

