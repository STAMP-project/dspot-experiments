/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops;


import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class SimpleOpsTest extends AbstractOperationTestCase {
    @Test
    public void testBasicOperations() {
        clearCounts();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        SimpleEntity entity = new SimpleEntity();
        entity.setId(1L);
        entity.setName("name");
        s.save(entity);
        tx.commit();
        s.close();
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        tx = s.beginTransaction();
        entity = ((SimpleEntity) (s.get(SimpleEntity.class, entity.getId())));
        Assert.assertEquals(Long.valueOf(1L), entity.getId());
        Assert.assertEquals("name", entity.getName());
        entity.setName("new name");
        tx.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        tx = s.beginTransaction();
        entity = ((SimpleEntity) (s.load(SimpleEntity.class, entity.getId())));
        Assert.assertFalse(Hibernate.isInitialized(entity));
        Assert.assertEquals(Long.valueOf(1L), entity.getId());
        Assert.assertEquals("new name", entity.getName());
        Assert.assertTrue(Hibernate.isInitialized(entity));
        tx.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
        entity.setName("another new name");
        s = openSession();
        tx = s.beginTransaction();
        s.merge(entity);
        tx.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
        clearCounts();
        s = openSession();
        tx = s.beginTransaction();
        entity = ((SimpleEntity) (s.get(SimpleEntity.class, entity.getId())));
        Assert.assertEquals(Long.valueOf(1L), entity.getId());
        Assert.assertEquals("another new name", entity.getName());
        s.delete(entity);
        tx.commit();
        s.close();
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }
}

