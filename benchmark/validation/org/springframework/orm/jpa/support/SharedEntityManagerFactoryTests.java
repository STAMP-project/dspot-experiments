/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.orm.jpa.support;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Phillip Webb
 */
public class SharedEntityManagerFactoryTests {
    @Test
    public void testValidUsage() {
        Object o = new Object();
        EntityManager mockEm = Mockito.mock(EntityManager.class);
        BDDMockito.given(mockEm.isOpen()).willReturn(true);
        EntityManagerFactory mockEmf = Mockito.mock(EntityManagerFactory.class);
        BDDMockito.given(mockEmf.createEntityManager()).willReturn(mockEm);
        SharedEntityManagerBean proxyFactoryBean = new SharedEntityManagerBean();
        proxyFactoryBean.setEntityManagerFactory(mockEmf);
        proxyFactoryBean.afterPropertiesSet();
        Assert.assertTrue(EntityManager.class.isAssignableFrom(proxyFactoryBean.getObjectType()));
        Assert.assertTrue(proxyFactoryBean.isSingleton());
        EntityManager proxy = proxyFactoryBean.getObject();
        Assert.assertSame(proxy, proxyFactoryBean.getObject());
        Assert.assertFalse(proxy.contains(o));
        Assert.assertTrue((proxy instanceof EntityManagerProxy));
        EntityManagerProxy emProxy = ((EntityManagerProxy) (proxy));
        try {
            emProxy.getTargetEntityManager();
            Assert.fail("Should have thrown IllegalStateException outside of transaction");
        } catch (IllegalStateException ex) {
            // expected
        }
        TransactionSynchronizationManager.bindResource(mockEmf, new org.springframework.orm.jpa.EntityManagerHolder(mockEm));
        try {
            Assert.assertSame(mockEm, emProxy.getTargetEntityManager());
        } finally {
            TransactionSynchronizationManager.unbindResource(mockEmf);
        }
        Assert.assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty());
        Mockito.verify(mockEm).contains(o);
        Mockito.verify(mockEm).close();
    }
}

