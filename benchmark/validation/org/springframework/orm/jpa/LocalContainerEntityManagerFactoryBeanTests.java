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


import PersistenceUnitTransactionType.JTA;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Phillip Webb
 */
@SuppressWarnings("rawtypes")
public class LocalContainerEntityManagerFactoryBeanTests extends AbstractEntityManagerFactoryBeanTests {
    // Static fields set by inner class DummyPersistenceProvider
    private static Map actualProps;

    private static PersistenceUnitInfo actualPui;

    @Test
    public void testValidPersistenceUnit() throws Exception {
        parseValidPersistenceUnit();
    }

    @Test
    public void testExceptionTranslationWithNoDialect() throws Exception {
        LocalContainerEntityManagerFactoryBean cefb = parseValidPersistenceUnit();
        cefb.getObject();
        Assert.assertNull("No dialect set", cefb.getJpaDialect());
        RuntimeException in1 = new RuntimeException("in1");
        PersistenceException in2 = new PersistenceException();
        Assert.assertNull("No translation here", cefb.translateExceptionIfPossible(in1));
        DataAccessException dex = cefb.translateExceptionIfPossible(in2);
        Assert.assertNotNull(dex);
        Assert.assertSame(in2, dex.getCause());
    }

    @Test
    public void testEntityManagerFactoryIsProxied() throws Exception {
        LocalContainerEntityManagerFactoryBean cefb = parseValidPersistenceUnit();
        EntityManagerFactory emf = cefb.getObject();
        Assert.assertSame("EntityManagerFactory reference must be cached after init", emf, cefb.getObject());
        Assert.assertNotSame("EMF must be proxied", AbstractEntityManagerFactoryBeanTests.mockEmf, emf);
        Assert.assertTrue(emf.equals(emf));
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setSerializationId("emf-bf");
        bf.registerSingleton("emf", cefb);
        cefb.setBeanFactory(bf);
        cefb.setBeanName("emf");
        Assert.assertNotNull(SerializationTestUtils.serializeAndDeserialize(emf));
    }

    @Test
    public void testApplicationManagedEntityManagerWithoutTransaction() throws Exception {
        Object testEntity = new Object();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(mockEm);
        LocalContainerEntityManagerFactoryBean cefb = parseValidPersistenceUnit();
        EntityManagerFactory emf = cefb.getObject();
        Assert.assertSame("EntityManagerFactory reference must be cached after init", emf, cefb.getObject());
        Assert.assertNotSame("EMF must be proxied", AbstractEntityManagerFactoryBeanTests.mockEmf, emf);
        EntityManager em = emf.createEntityManager();
        Assert.assertFalse(em.contains(testEntity));
        cefb.destroy();
        Mockito.verify(AbstractEntityManagerFactoryBeanTests.mockEmf).close();
    }

    @Test
    public void testApplicationManagedEntityManagerWithTransaction() throws Exception {
        Object testEntity = new Object();
        EntityTransaction mockTx = Mockito.mock(EntityTransaction.class);
        // This one's for the tx (shared)
        EntityManager sharedEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(sharedEm.getTransaction()).willReturn(new LocalContainerEntityManagerFactoryBeanTests.NoOpEntityTransaction());
        // This is the application-specific one
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(mockEm.getTransaction()).willReturn(mockTx);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(sharedEm, mockEm);
        LocalContainerEntityManagerFactoryBean cefb = parseValidPersistenceUnit();
        JpaTransactionManager jpatm = new JpaTransactionManager();
        jpatm.setEntityManagerFactory(cefb.getObject());
        TransactionStatus txStatus = jpatm.getTransaction(new DefaultTransactionAttribute());
        EntityManagerFactory emf = cefb.getObject();
        Assert.assertSame("EntityManagerFactory reference must be cached after init", emf, cefb.getObject());
        Assert.assertNotSame("EMF must be proxied", AbstractEntityManagerFactoryBeanTests.mockEmf, emf);
        EntityManager em = emf.createEntityManager();
        em.joinTransaction();
        Assert.assertFalse(em.contains(testEntity));
        jpatm.commit(txStatus);
        cefb.destroy();
        Mockito.verify(mockTx).begin();
        Mockito.verify(mockTx).commit();
        Mockito.verify(mockEm).contains(testEntity);
        Mockito.verify(AbstractEntityManagerFactoryBeanTests.mockEmf).close();
    }

    @Test
    public void testApplicationManagedEntityManagerWithTransactionAndCommitException() throws Exception {
        Object testEntity = new Object();
        EntityTransaction mockTx = Mockito.mock(EntityTransaction.class);
        BDDMockito.willThrow(new OptimisticLockException()).given(mockTx).commit();
        // This one's for the tx (shared)
        EntityManager sharedEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(sharedEm.getTransaction()).willReturn(new LocalContainerEntityManagerFactoryBeanTests.NoOpEntityTransaction());
        // This is the application-specific one
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(mockEm.getTransaction()).willReturn(mockTx);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(sharedEm, mockEm);
        LocalContainerEntityManagerFactoryBean cefb = parseValidPersistenceUnit();
        JpaTransactionManager jpatm = new JpaTransactionManager();
        jpatm.setEntityManagerFactory(cefb.getObject());
        TransactionStatus txStatus = jpatm.getTransaction(new DefaultTransactionAttribute());
        EntityManagerFactory emf = cefb.getObject();
        Assert.assertSame("EntityManagerFactory reference must be cached after init", emf, cefb.getObject());
        Assert.assertNotSame("EMF must be proxied", AbstractEntityManagerFactoryBeanTests.mockEmf, emf);
        EntityManager em = emf.createEntityManager();
        em.joinTransaction();
        Assert.assertFalse(em.contains(testEntity));
        try {
            jpatm.commit(txStatus);
            Assert.fail("Should have thrown OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException ex) {
            // expected
        }
        cefb.destroy();
        Mockito.verify(mockTx).begin();
        Mockito.verify(mockEm).contains(testEntity);
        Mockito.verify(AbstractEntityManagerFactoryBeanTests.mockEmf).close();
    }

    @Test
    public void testApplicationManagedEntityManagerWithJtaTransaction() throws Exception {
        Object testEntity = new Object();
        // This one's for the tx (shared)
        EntityManager sharedEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(sharedEm.getTransaction()).willReturn(new LocalContainerEntityManagerFactoryBeanTests.NoOpEntityTransaction());
        // This is the application-specific one
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(AbstractEntityManagerFactoryBeanTests.mockEmf.createEntityManager()).willReturn(sharedEm, mockEm);
        LocalContainerEntityManagerFactoryBean cefb = parseValidPersistenceUnit();
        MutablePersistenceUnitInfo pui = ((MutablePersistenceUnitInfo) (cefb.getPersistenceUnitInfo()));
        pui.setTransactionType(JTA);
        JpaTransactionManager jpatm = new JpaTransactionManager();
        jpatm.setEntityManagerFactory(cefb.getObject());
        TransactionStatus txStatus = jpatm.getTransaction(new DefaultTransactionAttribute());
        EntityManagerFactory emf = cefb.getObject();
        Assert.assertSame("EntityManagerFactory reference must be cached after init", emf, cefb.getObject());
        Assert.assertNotSame("EMF must be proxied", AbstractEntityManagerFactoryBeanTests.mockEmf, emf);
        EntityManager em = emf.createEntityManager();
        em.joinTransaction();
        Assert.assertFalse(em.contains(testEntity));
        jpatm.commit(txStatus);
        cefb.destroy();
        Mockito.verify(mockEm).joinTransaction();
        Mockito.verify(mockEm).contains(testEntity);
        Mockito.verify(AbstractEntityManagerFactoryBeanTests.mockEmf).close();
    }

    @Test
    public void testInvalidPersistenceUnitName() throws Exception {
        try {
            createEntityManagerFactoryBean("org/springframework/orm/jpa/domain/persistence.xml", null, "call me Bob");
            Assert.fail("Should not create factory with this name");
        } catch (IllegalArgumentException ex) {
            // Ok
        }
    }

    @Test
    public void testRejectsMissingPersistenceUnitInfo() throws Exception {
        LocalContainerEntityManagerFactoryBean containerEmfb = new LocalContainerEntityManagerFactoryBean();
        String entityManagerName = "call me Bob";
        containerEmfb.setPersistenceUnitName(entityManagerName);
        containerEmfb.setPersistenceProviderClass(LocalContainerEntityManagerFactoryBeanTests.DummyContainerPersistenceProvider.class);
        try {
            containerEmfb.afterPropertiesSet();
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // Ok
        }
    }

    private static class DummyContainerPersistenceProvider implements PersistenceProvider {
        @Override
        public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo pui, Map map) {
            LocalContainerEntityManagerFactoryBeanTests.actualPui = pui;
            LocalContainerEntityManagerFactoryBeanTests.actualProps = map;
            return AbstractEntityManagerFactoryBeanTests.mockEmf;
        }

        @Override
        public EntityManagerFactory createEntityManagerFactory(String emfName, Map properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ProviderUtil getProviderUtil() {
            throw new UnsupportedOperationException();
        }

        // JPA 2.1 method
        public void generateSchema(PersistenceUnitInfo persistenceUnitInfo, Map map) {
            throw new UnsupportedOperationException();
        }

        // JPA 2.1 method
        public boolean generateSchema(String persistenceUnitName, Map map) {
            throw new UnsupportedOperationException();
        }
    }

    private static class NoOpEntityTransaction implements EntityTransaction {
        @Override
        public void begin() {
        }

        @Override
        public void commit() {
        }

        @Override
        public void rollback() {
        }

        @Override
        public void setRollbackOnly() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getRollbackOnly() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }
}

