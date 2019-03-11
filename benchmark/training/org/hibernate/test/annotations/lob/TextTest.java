/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.lob;


import org.hibernate.Session;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.Sybase11Dialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests eager materialization and mutation of long strings.
 *
 * @author Steve Ebersole
 */
@RequiresDialect({ SybaseASE15Dialect.class, SQLServerDialect.class, SybaseDialect.class, Sybase11Dialect.class })
public class TextTest extends BaseCoreFunctionalTestCase {
    private static final int LONG_STRING_SIZE = 10000;

    @Test
    public void testBoundedLongStringAccess() {
        String original = buildRecursively(TextTest.LONG_STRING_SIZE, 'x');
        String changed = buildRecursively(TextTest.LONG_STRING_SIZE, 'y');
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
        Assert.assertNull(entity.getName());
        Assert.assertNull(entity.getWhatEver());
        entity.setLongString(original);
        entity.setName(original.toCharArray());
        entity.setWhatEver(wrapPrimitive(original.toCharArray()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertEquals(TextTest.LONG_STRING_SIZE, entity.getLongString().length());
        Assert.assertEquals(original, entity.getLongString());
        Assert.assertNotNull(entity.getName());
        Assert.assertEquals(TextTest.LONG_STRING_SIZE, entity.getName().length);
        TextTest.assertEquals(original.toCharArray(), entity.getName());
        Assert.assertNotNull(entity.getWhatEver());
        Assert.assertEquals(TextTest.LONG_STRING_SIZE, entity.getWhatEver().length);
        TextTest.assertEquals(original.toCharArray(), unwrapNonPrimitive(entity.getWhatEver()));
        entity.setLongString(changed);
        entity.setName(changed.toCharArray());
        entity.setWhatEver(wrapPrimitive(changed.toCharArray()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertEquals(TextTest.LONG_STRING_SIZE, entity.getLongString().length());
        Assert.assertEquals(changed, entity.getLongString());
        Assert.assertNotNull(entity.getName());
        Assert.assertEquals(TextTest.LONG_STRING_SIZE, entity.getName().length);
        TextTest.assertEquals(changed.toCharArray(), entity.getName());
        Assert.assertNotNull(entity.getWhatEver());
        Assert.assertEquals(TextTest.LONG_STRING_SIZE, entity.getWhatEver().length);
        TextTest.assertEquals(changed.toCharArray(), unwrapNonPrimitive(entity.getWhatEver()));
        entity.setLongString(null);
        entity.setName(null);
        entity.setWhatEver(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((LongStringHolder) (s.get(LongStringHolder.class, entity.getId())));
        Assert.assertNull(entity.getLongString());
        Assert.assertNull(entity.getName());
        Assert.assertNull(entity.getWhatEver());
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

