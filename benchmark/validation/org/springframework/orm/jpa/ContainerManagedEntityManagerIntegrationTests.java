/**
 * Copyright 2002-2016 the original author or authors.
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


import java.lang.reflect.Proxy;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.domain.Person;


/**
 * Integration tests using in-memory database for container-managed JPA
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 */
public class ContainerManagedEntityManagerIntegrationTests extends AbstractEntityManagerFactoryIntegrationTests {
    @Autowired
    private AbstractEntityManagerFactoryBean entityManagerFactoryBean;

    @Test
    public void testExceptionTranslationWithDialectFoundOnIntroducedEntityManagerInfo() throws Exception {
        doTestExceptionTranslationWithDialectFound(getJpaDialect());
    }

    @Test
    public void testExceptionTranslationWithDialectFoundOnEntityManagerFactoryBean() throws Exception {
        Assert.assertNotNull("Dialect must have been set", entityManagerFactoryBean.getJpaDialect());
        doTestExceptionTranslationWithDialectFound(entityManagerFactoryBean);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEntityManagerProxyIsProxy() {
        EntityManager em = createContainerManagedEntityManager();
        Assert.assertTrue(Proxy.isProxyClass(em.getClass()));
        Query q = em.createQuery("select p from Person as p");
        List<Person> people = q.getResultList();
        Assert.assertTrue(people.isEmpty());
        Assert.assertTrue("Should be open to start with", em.isOpen());
        try {
            em.close();
            Assert.fail("Close should not work on container managed EM");
        } catch (IllegalStateException ex) {
            // OK
        }
        Assert.assertTrue(em.isOpen());
    }

    // This would be legal, at least if not actually _starting_ a tx
    @Test
    public void testEntityManagerProxyRejectsProgrammaticTxManagement() {
        try {
            createContainerManagedEntityManager().getTransaction();
            Assert.fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    /* See comments in spec on EntityManager.joinTransaction().
    We take the view that this is a valid no op.
     */
    @Test
    public void testContainerEntityManagerProxyAllowsJoinTransactionInTransaction() {
        createContainerManagedEntityManager().joinTransaction();
    }

    @Test
    public void testContainerEntityManagerProxyRejectsJoinTransactionWithoutTransaction() {
        endTransaction();
        try {
            createContainerManagedEntityManager().joinTransaction();
            Assert.fail("Should have thrown a TransactionRequiredException");
        } catch (TransactionRequiredException ex) {
            // expected
        }
    }

    @Test
    public void testInstantiateAndSave() {
        EntityManager em = createContainerManagedEntityManager();
        doInstantiateAndSave(em);
    }

    @Test
    public void testReuseInNewTransaction() {
        EntityManager em = createContainerManagedEntityManager();
        doInstantiateAndSave(em);
        endTransaction();
        // assertFalse(em.getTransaction().isActive());
        startNewTransaction();
        // Call any method: should cause automatic tx invocation
        Assert.assertFalse(em.contains(new Person()));
        // assertTrue(em.getTransaction().isActive());
        doInstantiateAndSave(em);
        setComplete();
        endTransaction();// Should rollback

        Assert.assertEquals("Tx must have committed back", 1, countRowsInTable(em, "person"));
        // Now clean up the database
        deleteFromTables("person");
    }

    @Test
    public void testRollbackOccurs() {
        EntityManager em = createContainerManagedEntityManager();
        doInstantiateAndSave(em);
        endTransaction();// Should rollback

        Assert.assertEquals("Tx must have been rolled back", 0, countRowsInTable(em, "person"));
    }

    @Test
    public void testCommitOccurs() {
        EntityManager em = createContainerManagedEntityManager();
        doInstantiateAndSave(em);
        setComplete();
        endTransaction();// Should rollback

        Assert.assertEquals("Tx must have committed back", 1, countRowsInTable(em, "person"));
        // Now clean up the database
        deleteFromTables("person");
    }
}

