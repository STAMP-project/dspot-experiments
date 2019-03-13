/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class MathHelperTest {
    @Test
    public void ceilingPowerOfTwo() {
        Assert.assertEquals(1, MathHelper.ceilingPowerOfTwo(1));
        Assert.assertEquals(2, MathHelper.ceilingPowerOfTwo(2));
        Assert.assertEquals(4, MathHelper.ceilingPowerOfTwo(3));
        Assert.assertEquals(4, MathHelper.ceilingPowerOfTwo(4));
        Assert.assertEquals(8, MathHelper.ceilingPowerOfTwo(5));
        Assert.assertEquals(8, MathHelper.ceilingPowerOfTwo(6));
        Assert.assertEquals(8, MathHelper.ceilingPowerOfTwo(7));
        Assert.assertEquals(8, MathHelper.ceilingPowerOfTwo(8));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(9));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(10));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(11));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(12));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(13));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(16));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(14));
        Assert.assertEquals(16, MathHelper.ceilingPowerOfTwo(15));
    }
}

