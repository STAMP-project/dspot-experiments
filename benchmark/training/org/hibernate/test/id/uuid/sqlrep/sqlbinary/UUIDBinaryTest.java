/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.uuid.sqlrep.sqlbinary;


import org.hibernate.Session;
import org.hibernate.test.id.uuid.sqlrep.Node;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class UUIDBinaryTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUsage() {
        Session session = openSession();
        session.beginTransaction();
        Node root = new Node("root");
        session.save(root);
        Assert.assertNotNull(root.getId());
        Node child = new Node("child", root);
        session.save(child);
        Assert.assertNotNull(child.getId());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        Node node = ((Node) (session.get(Node.class, root.getId())));
        Assert.assertNotNull(node);
        node = ((Node) (session.get(Node.class, child.getId())));
        Assert.assertNotNull(node);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        // test joining
        node = ((Node) (session.createQuery("from Node n join fetch n.parent where n.parent is not null").uniqueResult()));
        Assert.assertNotNull(node);
        Assert.assertNotNull(node.getParent());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(child);
        session.delete(root);
        session.getTransaction().commit();
        session.close();
    }
}

