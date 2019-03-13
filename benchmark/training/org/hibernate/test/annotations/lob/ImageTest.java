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
 * Tests eager materialization and mutation of data mapped by
 * {@link org.hibernate.type.ImageType}.
 *
 * @author Gail Badner
 */
@RequiresDialect({ SybaseASE15Dialect.class, SQLServerDialect.class, SybaseDialect.class, Sybase11Dialect.class })
public class ImageTest extends BaseCoreFunctionalTestCase {
    private static final int ARRAY_SIZE = 10000;

    @Test
    public void testBoundedLongByteArrayAccess() {
        byte[] original = buildRecursively(ImageTest.ARRAY_SIZE, true);
        byte[] changed = buildRecursively(ImageTest.ARRAY_SIZE, false);
        Session s = openSession();
        s.beginTransaction();
        ImageHolder entity = new ImageHolder();
        s.save(entity);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((ImageHolder) (s.get(ImageHolder.class, entity.getId())));
        Assert.assertNull(entity.getLongByteArray());
        Assert.assertNull(entity.getDog());
        Assert.assertNull(entity.getPicByteArray());
        entity.setLongByteArray(original);
        Dog dog = new Dog();
        dog.setName("rabbit");
        entity.setDog(dog);
        entity.setPicByteArray(wrapPrimitive(original));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((ImageHolder) (s.get(ImageHolder.class, entity.getId())));
        Assert.assertEquals(ImageTest.ARRAY_SIZE, entity.getLongByteArray().length);
        ImageTest.assertEquals(original, entity.getLongByteArray());
        Assert.assertEquals(ImageTest.ARRAY_SIZE, entity.getPicByteArray().length);
        ImageTest.assertEquals(original, unwrapNonPrimitive(entity.getPicByteArray()));
        Assert.assertNotNull(entity.getDog());
        Assert.assertEquals(dog.getName(), entity.getDog().getName());
        entity.setLongByteArray(changed);
        entity.setPicByteArray(wrapPrimitive(changed));
        dog.setName("papa");
        entity.setDog(dog);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((ImageHolder) (s.get(ImageHolder.class, entity.getId())));
        Assert.assertEquals(ImageTest.ARRAY_SIZE, entity.getLongByteArray().length);
        ImageTest.assertEquals(changed, entity.getLongByteArray());
        Assert.assertEquals(ImageTest.ARRAY_SIZE, entity.getPicByteArray().length);
        ImageTest.assertEquals(changed, unwrapNonPrimitive(entity.getPicByteArray()));
        Assert.assertNotNull(entity.getDog());
        Assert.assertEquals(dog.getName(), entity.getDog().getName());
        entity.setLongByteArray(null);
        entity.setPicByteArray(null);
        entity.setDog(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        entity = ((ImageHolder) (s.get(ImageHolder.class, entity.getId())));
        Assert.assertNull(entity.getLongByteArray());
        Assert.assertNull(entity.getDog());
        Assert.assertNull(entity.getPicByteArray());
        s.delete(entity);
        s.getTransaction().commit();
        s.close();
    }
}

