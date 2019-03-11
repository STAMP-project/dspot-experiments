/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops;


import DialectChecks.SupportsNoColumnInsert;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class GetLoadTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testGetLoad() {
        clearCounts();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Employer emp = new Employer();
        s.persist(emp);
        Node node = new Node("foo");
        Node parent = new Node("bar");
        parent.addChild(node);
        s.persist(parent);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        emp = ((Employer) (s.get(Employer.class, emp.getId())));
        Assert.assertTrue(Hibernate.isInitialized(emp));
        Assert.assertFalse(Hibernate.isInitialized(emp.getEmployees()));
        node = ((Node) (s.get(Node.class, node.getName())));
        Assert.assertTrue(Hibernate.isInitialized(node));
        Assert.assertFalse(Hibernate.isInitialized(node.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(node.getParent()));
        Assert.assertNull(s.get(Node.class, "xyz"));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        emp = ((Employer) (s.load(Employer.class, emp.getId())));
        emp.getId();
        Assert.assertFalse(Hibernate.isInitialized(emp));
        node = ((Node) (s.load(Node.class, node.getName())));
        Assert.assertEquals(node.getName(), "foo");
        Assert.assertFalse(Hibernate.isInitialized(node));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        emp = ((Employer) (s.get("org.hibernate.test.ops.Employer", emp.getId())));
        Assert.assertTrue(Hibernate.isInitialized(emp));
        node = ((Node) (s.get("org.hibernate.test.ops.Node", node.getName())));
        Assert.assertTrue(Hibernate.isInitialized(node));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        emp = ((Employer) (s.load("org.hibernate.test.ops.Employer", emp.getId())));
        emp.getId();
        Assert.assertFalse(Hibernate.isInitialized(emp));
        node = ((Node) (s.load("org.hibernate.test.ops.Node", node.getName())));
        Assert.assertEquals(node.getName(), "foo");
        Assert.assertFalse(Hibernate.isInitialized(node));
        tx.commit();
        s.close();
        assertFetchCount(0);
    }

    @Test
    public void testGetAfterDelete() {
        clearCounts();
        Session s = openSession();
        s.beginTransaction();
        Employer emp = new Employer();
        s.persist(emp);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(emp);
        emp = s.get(Employer.class, emp.getId());
        s.getTransaction().commit();
        s.close();
        Assert.assertNull("get did not return null after delete", emp);
    }
}

