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
package org.springframework.orm.jpa.hibernate;


import FlushMode.AUTO;
import FlushMode.MANUAL;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.orm.jpa.AbstractContainerEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;


/**
 * Hibernate-specific JPA tests.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 */
@SuppressWarnings("deprecation")
public class HibernateEntityManagerFactoryIntegrationTests extends AbstractContainerEntityManagerFactoryIntegrationTests {
    @Test
    public void testCanCastNativeEntityManagerFactoryToHibernateEntityManagerFactoryImpl() {
        EntityManagerFactoryInfo emfi = ((EntityManagerFactoryInfo) (entityManagerFactory));
        Assert.assertTrue(((emfi.getNativeEntityManagerFactory()) instanceof HibernateEntityManagerFactory));
        Assert.assertTrue(((emfi.getNativeEntityManagerFactory()) instanceof SessionFactory));// as of Hibernate 5.2

    }

    @Test
    public void testCanCastSharedEntityManagerProxyToHibernateEntityManager() {
        Assert.assertTrue(((sharedEntityManager) instanceof HibernateEntityManager));
        Assert.assertTrue(((getTargetEntityManager()) instanceof Session));// as of Hibernate 5.2

    }

    @Test
    public void testCanUnwrapAopProxy() {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityManager proxy = ProxyFactory.getProxy(EntityManager.class, new org.springframework.aop.target.SingletonTargetSource(em));
        Assert.assertTrue((em instanceof HibernateEntityManager));
        Assert.assertFalse((proxy instanceof HibernateEntityManager));
        Assert.assertTrue(((proxy.unwrap(HibernateEntityManager.class)) != null));
        Assert.assertSame(em, proxy.unwrap(HibernateEntityManager.class));
        Assert.assertSame(em.getDelegate(), proxy.getDelegate());
    }

    // SPR-16956
    @Test
    public void testReadOnly() {
        Assert.assertSame(AUTO, sharedEntityManager.unwrap(Session.class).getHibernateFlushMode());
        Assert.assertFalse(sharedEntityManager.unwrap(Session.class).isDefaultReadOnly());
        endTransaction();
        this.transactionDefinition.setReadOnly(true);
        startNewTransaction();
        Assert.assertSame(MANUAL, sharedEntityManager.unwrap(Session.class).getHibernateFlushMode());
        Assert.assertTrue(sharedEntityManager.unwrap(Session.class).isDefaultReadOnly());
    }
}

