/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ordered;


import FetchMode.JOIN;
import java.util.Iterator;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class OrderByTest extends BaseCoreFunctionalTestCase {
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testOrderBy() {
        Search s = new Search("Hibernate");
        s.getSearchResults().add("jboss.com");
        s.getSearchResults().add("hibernate.org");
        s.getSearchResults().add("HiA");
        Session sess = openSession();
        Transaction tx = sess.beginTransaction();
        sess.persist(s);
        sess.flush();
        sess.clear();
        s = ((Search) (sess.createCriteria(Search.class).uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(s.getSearchResults()));
        Iterator iter = s.getSearchResults().iterator();
        Assert.assertEquals(iter.next(), "HiA");
        Assert.assertEquals(iter.next(), "hibernate.org");
        Assert.assertEquals(iter.next(), "jboss.com");
        Assert.assertFalse(iter.hasNext());
        sess.clear();
        s = ((Search) (sess.createCriteria(Search.class).setFetchMode("searchResults", JOIN).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(s.getSearchResults()));
        iter = s.getSearchResults().iterator();
        Assert.assertEquals(iter.next(), "HiA");
        Assert.assertEquals(iter.next(), "hibernate.org");
        Assert.assertEquals(iter.next(), "jboss.com");
        Assert.assertFalse(iter.hasNext());
        sess.clear();
        s = ((Search) (sess.createQuery("from Search s left join fetch s.searchResults").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(s.getSearchResults()));
        iter = s.getSearchResults().iterator();
        Assert.assertEquals(iter.next(), "HiA");
        Assert.assertEquals(iter.next(), "hibernate.org");
        Assert.assertEquals(iter.next(), "jboss.com");
        Assert.assertFalse(iter.hasNext());
        /* sess.clear();
        s = (Search) sess.createCriteria(Search.class).uniqueResult();
        assertFalse( Hibernate.isInitialized( s.getSearchResults() ) );
        iter = sess.createFilter( s.getSearchResults(), "").iterate();
        assertEquals( iter.next(), "HiA" );
        assertEquals( iter.next(), "hibernate.org" );
        assertEquals( iter.next(), "jboss.com" );
        assertFalse( iter.hasNext() );
         */
        sess.delete(s);
        tx.commit();
        sess.close();
    }
}

