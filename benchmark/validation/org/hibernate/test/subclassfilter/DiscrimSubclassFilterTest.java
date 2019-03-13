/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.subclassfilter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DiscrimSubclassFilterTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testFiltersWithSubclass() {
        Session s = openSession();
        s.enableFilter("region").setParameter("userRegion", "US");
        Transaction t = s.beginTransaction();
        prepareTestData(s);
        s.clear();
        List results;
        Iterator itr;
        results = s.createQuery("from Person").list();
        Assert.assertEquals("Incorrect qry result count", 4, results.size());
        s.clear();
        results = s.createQuery("from Employee").list();
        Assert.assertEquals("Incorrect qry result count", 2, results.size());
        s.clear();
        results = new ArrayList(new HashSet(s.createQuery("from Person as p left join fetch p.minions").list()));
        Assert.assertEquals("Incorrect qry result count", 4, results.size());
        itr = results.iterator();
        while (itr.hasNext()) {
            // find john
            final Person p = ((Person) (itr.next()));
            if (p.getName().equals("John Doe")) {
                Employee john = ((Employee) (p));
                Assert.assertEquals("Incorrect fecthed minions count", 1, john.getMinions().size());
                break;
            }
        } 
        s.clear();
        results = new ArrayList(new HashSet(s.createQuery("from Employee as p left join fetch p.minions").list()));
        Assert.assertEquals("Incorrect qry result count", 2, results.size());
        itr = results.iterator();
        while (itr.hasNext()) {
            // find john
            final Person p = ((Person) (itr.next()));
            if (p.getName().equals("John Doe")) {
                Employee john = ((Employee) (p));
                Assert.assertEquals("Incorrect fecthed minions count", 1, john.getMinions().size());
                break;
            }
        } 
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete Customer where contactOwner is not null").executeUpdate();
        s.createQuery("delete Employee where manager is not null").executeUpdate();
        s.createQuery("delete Person").executeUpdate();
        t.commit();
        s.close();
    }
}

