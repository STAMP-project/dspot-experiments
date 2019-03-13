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
import org.hibernate.dialect.CUBRIDDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@SkipForDialect(value = CUBRIDDialect.class, comment = "As of verion 8.4.1 CUBRID doesn't support temporary tables. This test fails with" + "HibernateException: cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables")
public class JoinedSubclassFilterTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testFiltersWithJoinedSubclass() {
        Session s = openSession();
        s.enableFilter("region").setParameter("userRegion", "US");
        Transaction t = s.beginTransaction();
        prepareTestData(s);
        s.clear();
        List results = s.createQuery("from Person").list();
        Assert.assertEquals("Incorrect qry result count", 4, results.size());
        s.clear();
        results = s.createQuery("from Employee").list();
        Assert.assertEquals("Incorrect qry result count", 2, results.size());
        Iterator itr = results.iterator();
        while (itr.hasNext()) {
            // find john
            final Person p = ((Person) (itr.next()));
            if (p.getName().equals("John Doe")) {
                Employee john = ((Employee) (p));
                Assert.assertEquals("Incorrect fecthed minions count", 2, john.getMinions().size());
                break;
            }
        } 
        s.clear();
        // TODO : currently impossible to define a collection-level filter w/ joined-subclass elements that will filter based on a superclass column and function correctly in (theta only?) outer joins;
        // this is consistent with the behaviour of a collection-level where.
        // this might be one argument for "pulling" the attached class-level filters into collection assocations,
        // although we'd need some way to apply the appropriate alias in that scenario.
        results = new ArrayList(new HashSet(s.createQuery("from Person as p left join fetch p.minions").list()));
        Assert.assertEquals("Incorrect qry result count", 4, results.size());
        itr = results.iterator();
        while (itr.hasNext()) {
            // find john
            final Person p = ((Person) (itr.next()));
            if (p.getName().equals("John Doe")) {
                Employee john = ((Employee) (p));
                Assert.assertEquals("Incorrect fecthed minions count", 2, john.getMinions().size());
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
                Assert.assertEquals("Incorrect fecthed minions count", 2, john.getMinions().size());
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

