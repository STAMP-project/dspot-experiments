/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.tracker;


import org.hibernate.bytecode.enhance.internal.tracker.CompositeOwnerTracker;
import org.hibernate.engine.spi.CompositeOwner;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">St?le W. Pedersen</a>
 */
public class CompositeOwnerTrackerTest {
    private int counter = 0;

    @Test
    public void testCompositeOwnerTracker() {
        CompositeOwnerTracker tracker = new CompositeOwnerTracker();
        tracker.add("foo", new CompositeOwnerTrackerTest.TestCompositeOwner());
        tracker.callOwner(".street1");
        Assert.assertEquals(1, counter);
        tracker.add("bar", new CompositeOwnerTrackerTest.TestCompositeOwner());
        tracker.callOwner(".city");
        Assert.assertEquals(3, counter);
        tracker.removeOwner("foo");
        tracker.callOwner(".country");
        Assert.assertEquals(4, counter);
        tracker.removeOwner("bar");
        tracker.callOwner(".country");
        tracker.add("moo", new CompositeOwnerTrackerTest.TestCompositeOwner());
        tracker.callOwner(".country");
        Assert.assertEquals(5, counter);
    }

    class TestCompositeOwner implements CompositeOwner {
        @Override
        public void $$_hibernate_trackChange(String attributeName) {
            if ((counter) == 0) {
                Assert.assertEquals("foo.street1", attributeName);
            }
            if ((counter) == 1) {
                Assert.assertEquals("foo.city", attributeName);
            }
            if ((counter) == 2) {
                Assert.assertEquals("bar.city", attributeName);
            }
            if ((counter) == 3) {
                Assert.assertEquals("bar.country", attributeName);
            }
            if ((counter) == 4) {
                Assert.assertEquals("moo.country", attributeName);
            }
            (counter)++;
        }
    }
}

