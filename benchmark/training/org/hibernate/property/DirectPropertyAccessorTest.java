/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michael Rudolf
 */
public class DirectPropertyAccessorTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-3718")
    public void testDirectIdPropertyAccess() throws Exception {
        Session s = openSession();
        final Transaction transaction = s.beginTransaction();
        Item i = new Item();
        s.persist(i);
        Order o = new Order();
        o.setOrderNumber(1);
        o.getItems().add(i);
        s.persist(o);
        transaction.commit();
        s.clear();
        o = ((Order) (s.load(Order.class, 1)));
        Assert.assertFalse(Hibernate.isInitialized(o));
        o.getOrderNumber();
        // If you mapped with field access, any method, except id, call initializes the proxy
        Assert.assertFalse(Hibernate.isInitialized(o));
        o.getName();
        Assert.assertTrue(Hibernate.isInitialized(o));
        s.close();
    }
}

