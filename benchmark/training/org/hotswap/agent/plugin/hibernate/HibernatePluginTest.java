/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.plugin.hibernate;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hotswap.agent.plugin.hibernate.testEntities.TestEntity;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertNull;


/**
 * Basic test
 *
 * @author Jiri Bubnik
 */
public class HibernatePluginTest {
    static EntityManagerFactory entityManagerFactory;

    @Test
    public void testSetupOk() throws Exception {
        doInTransaction(new HibernatePluginTest.InTransaction() {
            @Override
            public void process(EntityManager entityManager) throws Exception {
                TestEntity entity = ((TestEntity) (entityManager.createQuery("from TestEntity where name='Test'").getSingleResult()));
                Assert.assertNotNull(entity);
                Assert.assertEquals("Test", entity.getName());
                Assert.assertEquals("descr", entity.getDescription());
            }
        });
        swapClasses();
        doInTransaction(new HibernatePluginTest.InTransaction() {
            @Override
            public void process(EntityManager entityManager) throws Exception {
                TestEntity entity = ((TestEntity) (entityManager.createQuery("from TestEntity where name='Test'").getSingleResult()));
                Assert.assertNotNull(entity);
                Assert.assertEquals("Test", entity.getName());
                assertNull(entity.getDescription());
            }
        });
    }

    private static interface InTransaction {
        public void process(EntityManager entityManager) throws Exception;
    }
}

