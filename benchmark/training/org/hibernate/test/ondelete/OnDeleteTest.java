/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ondelete;


import DialectChecks.SupportsCircularCascadeDeleteCheck;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class OnDeleteTest extends BaseCoreFunctionalTestCase {
    @Test
    @RequiresDialectFeature(value = SupportsCircularCascadeDeleteCheck.class, comment = "db/dialect does not support circular cascade delete constraints")
    public void testJoinedSubclass() {
        Statistics statistics = sessionFactory().getStatistics();
        statistics.clear();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Salesperson mark = new Salesperson();
        mark.setName("Mark");
        mark.setTitle("internal sales");
        mark.setSex('M');
        mark.setAddress("buckhead");
        mark.setZip("30305");
        mark.setCountry("USA");
        Person joe = new Person();
        joe.setName("Joe");
        joe.setAddress("San Francisco");
        joe.setZip("XXXXX");
        joe.setCountry("USA");
        joe.setSex('M');
        joe.setSalesperson(mark);
        mark.getCustomers().add(joe);
        s.save(mark);
        t.commit();
        Assert.assertEquals(statistics.getEntityInsertCount(), 2);
        Assert.assertEquals(statistics.getPrepareStatementCount(), 5);
        statistics.clear();
        t = s.beginTransaction();
        s.delete(mark);
        t.commit();
        Assert.assertEquals(statistics.getEntityDeleteCount(), 2);
        if (getDialect().supportsCascadeDelete()) {
            Assert.assertEquals(statistics.getPrepareStatementCount(), 1);
        }
        t = s.beginTransaction();
        List names = s.createQuery("select name from Person").list();
        Assert.assertTrue(names.isEmpty());
        t.commit();
        s.close();
    }
}

