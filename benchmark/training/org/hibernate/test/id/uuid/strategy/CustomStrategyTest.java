/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.uuid.strategy;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CustomStrategyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUsage() {
        Session session = openSession();
        session.beginTransaction();
        Node node = new Node();
        session.save(node);
        Assert.assertNotNull(node.getId());
        Assert.assertEquals(2, node.getId().variant());
        Assert.assertEquals(1, node.getId().version());
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(node);
        session.getTransaction().commit();
        session.close();
    }
}

