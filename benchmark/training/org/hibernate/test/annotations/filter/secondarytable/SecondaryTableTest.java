/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.filter.secondarytable;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class SecondaryTableTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testFilter() {
        try (Session session = openSession()) {
            Assert.assertEquals(Long.valueOf(4), session.createQuery("select count(u) from User u").uniqueResult());
            session.enableFilter("ageFilter").setParameter("age", 24);
            Assert.assertEquals(Long.valueOf(2), session.createQuery("select count(u) from User u").uniqueResult());
        }
    }
}

