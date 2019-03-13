/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.xml;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class XmlAttributeOverrideTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testAttributeOverriding() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Employee e = new Employee();
        e.setId(Long.valueOf(100));
        e.setName("Bubba");
        e.setHomeAddress(new Address("123 Main St", "New York", "NY", "11111"));
        e.setMailAddress(new Address("P.O. Box 123", "New York", "NY", "11111"));
        em.persist(e);
        em.flush();
        em.getTransaction().rollback();
        em.close();
    }

    @Test
    public void testDefaultEventListener() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CounterListener.reset();
        Employee e = new Employee();
        e.setId(Long.valueOf(100));
        e.setName("Bubba");
        e.setHomeAddress(new Address("123 Main St", "New York", "NY", "11111"));
        e.setMailAddress(new Address("P.O. Box 123", "New York", "NY", "11111"));
        em.persist(e);
        em.flush();
        em.clear();
        em.find(Employee.class, e.getId()).setName("Bibo");
        em.flush();
        em.clear();
        em.remove(em.find(Employee.class, e.getId()));
        em.flush();
        em.getTransaction().rollback();
        em.close();
        Assert.assertEquals(1, CounterListener.insert);
        Assert.assertEquals(1, CounterListener.update);
        Assert.assertEquals(1, CounterListener.delete);
    }
}

