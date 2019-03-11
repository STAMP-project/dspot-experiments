/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class GetTransactionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMultipleCallsReturnTheSameTransaction() {
        EntityManager em = createEntityManager();
        EntityTransaction t = em.getTransaction();
        Assert.assertSame(t, em.getTransaction());
        Assert.assertFalse(t.isActive());
        t.begin();
        Assert.assertSame(t, em.getTransaction());
        Assert.assertTrue(t.isActive());
        t.commit();
        Assert.assertSame(t, em.getTransaction());
        Assert.assertFalse(t.isActive());
        em.close();
        Assert.assertSame(t, em.getTransaction());
        Assert.assertFalse(t.isActive());
    }
}

