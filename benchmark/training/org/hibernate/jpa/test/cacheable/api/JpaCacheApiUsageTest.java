/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cacheable.api;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class JpaCacheApiUsageTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testEviction() {
        // first create an Order
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(new Order(1, 500));
        em.getTransaction().commit();
        em.close();
        Assert.assertTrue(entityManagerFactory().getCache().contains(Order.class, 1));
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Assert.assertTrue(entityManagerFactory().getCache().contains(Order.class, 1));
        em.createQuery("delete Order").executeUpdate();
        em.getTransaction().commit();
        em.close();
        Assert.assertFalse(entityManagerFactory().getCache().contains(Order.class, 1));
    }
}

