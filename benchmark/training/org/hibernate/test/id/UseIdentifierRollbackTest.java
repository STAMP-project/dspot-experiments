/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class UseIdentifierRollbackTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSimpleRollback() {
        Session session = openSession();
        Transaction t = session.beginTransaction();
        Product prod = new Product();
        Assert.assertNull(prod.getName());
        session.persist(prod);
        session.flush();
        Assert.assertNotNull(prod.getName());
        t.rollback();
        session.close();
    }
}

