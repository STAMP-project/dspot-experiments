/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.inheritance.joined;


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
public class JoinedSubclassAndSecondaryTable extends BaseCoreFunctionalTestCase {
    @Test
    public void testSecondaryTableAndJoined() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        SwimmingPool sp = new SwimmingPool();
        s.persist(sp);
        s.flush();
        s.clear();
        long rowCount = getTableRowCount(s, "POOL_ADDRESS");
        Assert.assertEquals("The address table is marked as optional. For null values no database row should be created", 0, rowCount);
        SwimmingPool sp2 = ((SwimmingPool) (s.get(SwimmingPool.class, sp.getId())));
        Assert.assertNull(sp.getAddress());
        PoolAddress address = new PoolAddress();
        address.setAddress("Park Avenue");
        sp2.setAddress(address);
        s.flush();
        s.clear();
        sp2 = ((SwimmingPool) (s.get(SwimmingPool.class, sp.getId())));
        rowCount = getTableRowCount(s, "POOL_ADDRESS");
        Assert.assertEquals("Now we should have a row in the pool address table ", 1, rowCount);
        Assert.assertNotNull(sp2.getAddress());
        Assert.assertEquals(sp2.getAddress().getAddress(), "Park Avenue");
        tx.rollback();
        s.close();
    }

    @Test
    public void testSecondaryTableAndJoinedInverse() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        SwimmingPool sp = new SwimmingPool();
        s.persist(sp);
        s.flush();
        s.clear();
        long rowCount = getTableRowCount(s, "POOL_ADDRESS_2");
        Assert.assertEquals("The address table is marked as optional. For null values no database row should be created", 0, rowCount);
        SwimmingPool sp2 = ((SwimmingPool) (s.get(SwimmingPool.class, sp.getId())));
        Assert.assertNull(sp.getSecondaryAddress());
        PoolAddress address = new PoolAddress();
        address.setAddress("Park Avenue");
        sp2.setSecondaryAddress(address);
        s.flush();
        s.clear();
        sp2 = ((SwimmingPool) (s.get(SwimmingPool.class, sp.getId())));
        rowCount = getTableRowCount(s, "POOL_ADDRESS_2");
        Assert.assertEquals("Now we should have a row in the pool address table ", 0, rowCount);
        Assert.assertNull(sp2.getSecondaryAddress());
        tx.rollback();
        s.close();
    }
}

