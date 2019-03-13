/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests eager materialization and mutation of long byte arrays.
 *
 * @author Steve Ebersole
 */
public abstract class LongByteArrayTest extends BaseCoreFunctionalTestCase {
    private static final int ARRAY_SIZE = 10000;

    @Test
    public void testBoundedLongByteArrayAccess() {
        byte[] original = buildRecursively(LongByteArrayTest.ARRAY_SIZE, true);
        byte[] changed = buildRecursively(LongByteArrayTest.ARRAY_SIZE, false);
        byte[] empty = new byte[]{  };
        Session s = openSession();
        s.beginTransaction();
        LongByteArrayHolder entity = new LongByteArrayHolder();
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LongByteArrayHolder.class, entity.getId());
        Assert.assertNull(entity.getLongByteArray());
        entity.setLongByteArray(original);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LongByteArrayHolder.class, entity.getId());
        Assert.assertEquals(LongByteArrayTest.ARRAY_SIZE, entity.getLongByteArray().length);
        LongByteArrayTest.assertEquals(original, entity.getLongByteArray());
        entity.setLongByteArray(changed);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LongByteArrayHolder.class, entity.getId());
        Assert.assertEquals(LongByteArrayTest.ARRAY_SIZE, entity.getLongByteArray().length);
        LongByteArrayTest.assertEquals(changed, entity.getLongByteArray());
        entity.setLongByteArray(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LongByteArrayHolder.class, entity.getId());
        Assert.assertNull(entity.getLongByteArray());
        entity.setLongByteArray(empty);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = s.get(LongByteArrayHolder.class, entity.getId());
        if ((entity.getLongByteArray()) != null) {
            Assert.assertEquals(empty.length, entity.getLongByteArray().length);
            LongByteArrayTest.assertEquals(empty, entity.getLongByteArray());
        }
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSaving() {
        byte[] value = buildRecursively(LongByteArrayTest.ARRAY_SIZE, true);
        Session s = openSession();
        s.beginTransaction();
        LongByteArrayHolder entity = new LongByteArrayHolder();
        entity.setLongByteArray(value);
        s.persist(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongByteArrayHolder) (s.get(LongByteArrayHolder.class, entity.getId())));
        Assert.assertEquals(LongByteArrayTest.ARRAY_SIZE, entity.getLongByteArray().length);
        LongByteArrayTest.assertEquals(value, entity.getLongByteArray());
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

