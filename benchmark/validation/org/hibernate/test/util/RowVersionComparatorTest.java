/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.util;


import RowVersionComparator.INSTANCE;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class RowVersionComparatorTest extends BaseUnitTestCase {
    @Test
    public void testNull() {
        try {
            INSTANCE.compare(null, null);
            Assert.fail("should have thrown NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            INSTANCE.compare(null, new byte[]{ 1 });
            Assert.fail("should have thrown NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            INSTANCE.compare(new byte[]{ 1 }, null);
            Assert.fail("should have thrown NullPointerException");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    @Test
    public void testArraysSameLength() {
        Assert.assertEquals(0, INSTANCE.compare(new byte[]{  }, new byte[]{  }));
        Assert.assertEquals(0, INSTANCE.compare(new byte[]{ 1 }, new byte[]{ 1 }));
        Assert.assertEquals(0, INSTANCE.compare(new byte[]{ 1, 2 }, new byte[]{ 1, 2 }));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 0, 2 }, new byte[]{ 1, 2 })) < 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 1, 1 }, new byte[]{ 1, 2 })) < 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 2, 2 }, new byte[]{ 1, 2 })) > 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 2, 2 }, new byte[]{ 2, 1 })) > 0));
    }

    @Test
    public void testArraysDifferentLength() {
        Assert.assertTrue(((INSTANCE.compare(new byte[]{  }, new byte[]{ 1 })) < 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 1 }, new byte[]{  })) > 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 1 }, new byte[]{ 1, 2 })) < 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 1, 2 }, new byte[]{ 1 })) > 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 2 }, new byte[]{ 1, 2 })) > 0));
        Assert.assertTrue(((INSTANCE.compare(new byte[]{ 1, 2 }, new byte[]{ 2 })) < 0));
    }
}

