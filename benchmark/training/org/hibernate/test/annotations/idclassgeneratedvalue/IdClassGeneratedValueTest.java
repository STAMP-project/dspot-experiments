/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations.idclassgeneratedvalue;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test.
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Stale W. Pedersen</a>
 */
public class IdClassGeneratedValueTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testBaseLine() {
        Session s = openSession();
        s.beginTransaction();
        Simple s1 = new Simple(1L, 2L, 10);
        s.persist(s1);
        Simple s2 = new Simple(2L, 3L, 20);
        s.persist(s2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List<Simple> simpleList = s.createQuery("select s from Simple s").list();
        Assert.assertEquals(simpleList.size(), 2);
        s1 = ((Simple) (s.load(Simple.class, new SimplePK(1L, 2L))));
        Assert.assertEquals(s1.getQuantity(), 10);
        s.clear();
        s.createQuery("delete Simple").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testSingleGeneratedValue() {
        Session s = openSession();
        s.beginTransaction();
        Simple2 s1 = new Simple2(200L, 10);
        s.persist(s1);
        Long s1Id1 = s1.getId1();
        Simple2 s2 = new Simple2(300L, 20);
        s.persist(s2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List<Simple2> simpleList = s.createQuery("select s from Simple2 s").list();
        Assert.assertEquals(simpleList.size(), 2);
        s1 = ((Simple2) (s.load(Simple2.class, new SimplePK(s1Id1, 200L))));
        Assert.assertEquals(s1.getQuantity(), 10);
        s.clear();
        s.createQuery("delete Simple2").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testMultipleGeneratedValue() {
        Session s = openSession();
        s.beginTransaction();
        Multiple m1 = new Multiple(1000L, 10);
        s.persist(m1);
        Long m1Id1 = m1.getId1();
        Long m1Id2 = m1.getId2();
        Multiple m2 = new Multiple(2000L, 20);
        s.persist(m2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List<Multiple> simpleList = s.createQuery("select m from Multiple m").list();
        Assert.assertEquals(simpleList.size(), 2);
        m1 = ((Multiple) (s.load(Multiple.class, new MultiplePK(m1Id1, m1Id2, 1000L))));
        Assert.assertEquals(m1.getQuantity(), 10);
        s.clear();
        s.createQuery("delete Multiple").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }
}

