/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.orm.jpa;


import FlushModeType.AUTO;
import java.lang.reflect.Proxy;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.orm.jpa.domain.DriversLicense;
import org.springframework.orm.jpa.domain.Person;
import org.springframework.util.SerializationTestUtils;


/**
 * Integration tests for LocalContainerEntityManagerFactoryBean.
 * Uses an in-memory database.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class AbstractContainerEntityManagerFactoryIntegrationTests extends AbstractEntityManagerFactoryIntegrationTests {
    @Test
    public void testEntityManagerFactoryImplementsEntityManagerFactoryInfo() {
        Assert.assertTrue("Must have introduced config interface", ((entityManagerFactory) instanceof EntityManagerFactoryInfo));
        EntityManagerFactoryInfo emfi = ((EntityManagerFactoryInfo) (entityManagerFactory));
        Assert.assertEquals("Person", emfi.getPersistenceUnitName());
        Assert.assertNotNull("PersistenceUnitInfo must be available", emfi.getPersistenceUnitInfo());
        Assert.assertNotNull("Raw EntityManagerFactory must be available", emfi.getNativeEntityManagerFactory());
    }

    @Test
    public void testStateClean() {
        Assert.assertEquals("Should be no people from previous transactions", 0, countRowsInTable("person"));
    }

    @Test
    public void testJdbcTx1_1() {
        testJdbcTx2();
    }

    @Test
    public void testJdbcTx1_2() {
        testJdbcTx2();
    }

    @Test
    public void testJdbcTx1_3() {
        testJdbcTx2();
    }

    @Test
    public void testJdbcTx2() {
        Assert.assertEquals("Any previous tx must have been rolled back", 0, countRowsInTable("person"));
        executeSqlScript("/org/springframework/orm/jpa/insertPerson.sql");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEntityManagerProxyIsProxy() {
        Assert.assertTrue(Proxy.isProxyClass(sharedEntityManager.getClass()));
        Query q = sharedEntityManager.createQuery("select p from Person as p");
        q.getResultList();
        Assert.assertTrue("Should be open to start with", sharedEntityManager.isOpen());
        sharedEntityManager.close();
        Assert.assertTrue("Close should have been silently ignored", sharedEntityManager.isOpen());
    }

    @Test
    public void testBogusQuery() {
        try {
            Query query = sharedEntityManager.createQuery("It's raining toads");
            // required in OpenJPA case
            query.executeUpdate();
            Assert.fail("Should have thrown a RuntimeException");
        } catch (RuntimeException ex) {
            // expected
        }
    }

    @Test
    public void testGetReferenceWhenNoRow() {
        try {
            Person notThere = sharedEntityManager.getReference(Person.class, 666);
            // We may get here (as with Hibernate). Either behaviour is valid:
            // throw exception on first access or on getReference itself.
            notThere.getFirstName();
            Assert.fail("Should have thrown an EntityNotFoundException or ObjectNotFoundException");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getClass().getName().endsWith("NotFoundException"));
        }
    }

    @Test
    public void testLazyLoading() {
        try {
            Person tony = new Person();
            tony.setFirstName("Tony");
            tony.setLastName("Blair");
            tony.setDriversLicense(new DriversLicense("8439DK"));
            sharedEntityManager.persist(tony);
            setComplete();
            endTransaction();
            startNewTransaction();
            sharedEntityManager.clear();
            Person newTony = entityManagerFactory.createEntityManager().getReference(Person.class, tony.getId());
            Assert.assertNotSame(newTony, tony);
            endTransaction();
            Assert.assertNotNull(newTony.getDriversLicense());
            newTony.getDriversLicense().getSerialNumber();
        } finally {
            deleteFromTables("person", "drivers_license");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleResults() {
        // Add with JDBC
        String firstName = "Tony";
        insertPerson(firstName);
        Assert.assertTrue(Proxy.isProxyClass(sharedEntityManager.getClass()));
        Query q = sharedEntityManager.createQuery("select p from Person as p");
        List<Person> people = q.getResultList();
        Assert.assertEquals(1, people.size());
        Assert.assertEquals(firstName, people.get(0).getFirstName());
    }

    @Test
    public void testEntityManagerProxyRejectsProgrammaticTxManagement() {
        try {
            sharedEntityManager.getTransaction();
            Assert.fail("Should not be able to create transactions on container managed EntityManager");
        } catch (IllegalStateException ex) {
        }
    }

    @Test
    public void testInstantiateAndSaveWithSharedEmProxy() {
        testInstantiateAndSave(sharedEntityManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryNoPersons() {
        EntityManager em = entityManagerFactory.createEntityManager();
        Query q = em.createQuery("select p from Person as p");
        List<Person> people = q.getResultList();
        Assert.assertEquals(0, people.size());
        try {
            Assert.assertNull(q.getSingleResult());
            Assert.fail("Should have thrown NoResultException");
        } catch (NoResultException ex) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryNoPersonsNotTransactional() {
        endTransaction();
        EntityManager em = entityManagerFactory.createEntityManager();
        Query q = em.createQuery("select p from Person as p");
        List<Person> people = q.getResultList();
        Assert.assertEquals(0, people.size());
        try {
            Assert.assertNull(q.getSingleResult());
            Assert.fail("Should have thrown NoResultException");
        } catch (NoResultException ex) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryNoPersonsShared() {
        Query q = this.sharedEntityManager.createQuery("select p from Person as p");
        q.setFlushMode(AUTO);
        List<Person> people = q.getResultList();
        Assert.assertEquals(0, people.size());
        try {
            Assert.assertNull(q.getSingleResult());
            Assert.fail("Should have thrown NoResultException");
        } catch (NoResultException ex) {
            // expected
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testQueryNoPersonsSharedNotTransactional() {
        endTransaction();
        EntityManager em = this.sharedEntityManager;
        Query q = em.createQuery("select p from Person as p");
        q.setFlushMode(AUTO);
        List<Person> people = q.getResultList();
        Assert.assertEquals(0, people.size());
        try {
            Assert.assertNull(q.getSingleResult());
            Assert.fail("Should have thrown IllegalStateException");
        } catch (Exception ex) {
            // We would typically expect an IllegalStateException, but Hibernate throws a
            // PersistenceException. So we assert the contents of the exception message instead.
            Assert.assertTrue(ex.getMessage().contains("closed"));
        }
        q = em.createQuery("select p from Person as p");
        q.setFlushMode(AUTO);
        try {
            Assert.assertNull(q.getSingleResult());
            Assert.fail("Should have thrown NoResultException");
        } catch (NoResultException ex) {
            // expected
        }
    }

    @Test
    public void testCanSerializeProxies() throws Exception {
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(entityManagerFactory));
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(sharedEntityManager));
    }
}

