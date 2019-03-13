/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id.uuid;


import java.util.UUID;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CustomVersionOneStrategyTest extends BaseUnitTestCase {
    @Test
    public void testUniqueCounter() {
        CustomVersionOneStrategy strategy = new CustomVersionOneStrategy();
        long now = System.currentTimeMillis();
        UUID uuid1 = new UUID(strategy.getMostSignificantBits(), CustomVersionOneStrategy.generateLeastSignificantBits(now));
        Assert.assertEquals(2, uuid1.variant());
        Assert.assertEquals(1, uuid1.version());
        for (int i = 0; i < 100; i++) {
            UUID uuidX = new UUID(strategy.getMostSignificantBits(), CustomVersionOneStrategy.generateLeastSignificantBits(now));
            Assert.assertEquals(2, uuidX.variant());
            Assert.assertEquals(1, uuidX.version());
            Assert.assertFalse(uuid1.equals(uuidX));
            Assert.assertEquals(uuid1.getMostSignificantBits(), uuidX.getMostSignificantBits());
        }
    }

    @Test
    public void testRangeOfValues() {
        CustomVersionOneStrategy strategy = new CustomVersionOneStrategy();
        UUID uuid = new UUID(strategy.getMostSignificantBits(), CustomVersionOneStrategy.generateLeastSignificantBits(0));
        Assert.assertEquals(2, uuid.variant());
        Assert.assertEquals(1, uuid.version());
        uuid = new UUID(strategy.getMostSignificantBits(), CustomVersionOneStrategy.generateLeastSignificantBits(Long.MAX_VALUE));
        Assert.assertEquals(2, uuid.variant());
        Assert.assertEquals(1, uuid.version());
    }
}

