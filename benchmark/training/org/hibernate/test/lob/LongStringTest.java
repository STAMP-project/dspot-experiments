/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lob;


import org.hibernate.Session;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests eager materialization and mutation of long strings.
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class LongStringTest extends BaseCoreFunctionalTestCase {
    private static final int LONG_STRING_SIZE = 10000;

    @Test
    public void testBoundedLongStringAccess() {
        String original = buildRecursively(LongStringTest.LONG_STRING_SIZE, 'x');
        String changed = buildRecursively(LongStringTest.LONG_STRING_SIZE, 'y');
        String empty = "";
        Session s = openSession();
        s.beginTransaction();
        LongStringHolder entity = new LongStringHolder();
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertNull(entity.getLongString());
        entity.setLongString(original);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertEquals(LongStringTest.LONG_STRING_SIZE, entity.getLongString().length());
        Assert.assertEquals(original, entity.getLongString());
        entity.setLongString(changed);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertEquals(LongStringTest.LONG_STRING_SIZE, entity.getLongString().length());
        Assert.assertEquals(changed, entity.getLongString());
        entity.setLongString(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertNull(entity.getLongString());
        entity.setLongString(empty);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        if ((entity.getLongString()) != null) {
            if ((getDialect()) instanceof SybaseASE15Dialect) {
                // Sybase uses a single blank to denote an empty string (this is by design). So, when inserting an empty string '', it is interpreted as single blank ' '.
                Assert.assertEquals(empty.length(), entity.getLongString().trim().length());
                Assert.assertEquals(empty, entity.getLongString().trim());
            } else {
                Assert.assertEquals(empty.length(), entity.getLongString().length());
                Assert.assertEquals(empty, entity.getLongString());
            }
        }
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

