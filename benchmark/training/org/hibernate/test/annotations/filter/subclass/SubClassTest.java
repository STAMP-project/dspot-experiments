/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.filter.subclass;


import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


public abstract class SubClassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIqFilter() {
        openSession();
        session.beginTransaction();
        assertCount(3);
        session.enableFilter("iqRange").setParameter("min", 101).setParameter("max", 140);
        assertCount(1);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testPregnantFilter() {
        openSession();
        session.beginTransaction();
        assertCount(3);
        session.enableFilter("pregnantOnly");
        assertCount(1);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testNonHumanFilter() {
        openSession();
        session.beginTransaction();
        assertCount(3);
        session.enableFilter("ignoreSome").setParameter("name", "Homo Sapiens");
        assertCount(0);
        session.getTransaction().commit();
        session.close();
    }
}

