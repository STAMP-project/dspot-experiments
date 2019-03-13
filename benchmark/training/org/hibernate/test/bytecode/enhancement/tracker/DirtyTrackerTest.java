/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.tracker;


import org.hibernate.bytecode.enhance.internal.tracker.DirtyTracker;
import org.hibernate.bytecode.enhance.internal.tracker.SimpleFieldTracker;
import org.hibernate.bytecode.enhance.internal.tracker.SortedFieldTracker;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">St?le W. Pedersen</a>
 */
public class DirtyTrackerTest {
    @Test
    public void testSimpleTracker() {
        DirtyTracker tracker = new SimpleFieldTracker();
        Assert.assertTrue(tracker.isEmpty());
        Assert.assertEquals(0, tracker.get().length);
        tracker.add("foo");
        Assert.assertFalse(tracker.isEmpty());
        Assert.assertArrayEquals(tracker.get(), new String[]{ "foo" });
        tracker.clear();
        Assert.assertTrue(tracker.isEmpty());
        Assert.assertEquals(0, tracker.get().length);
        tracker.add("foo");
        tracker.add("bar");
        tracker.add("another.bar");
        tracker.add("foo");
        tracker.add("another.foo");
        tracker.add("another.bar");
        Assert.assertEquals(4, tracker.get().length);
        tracker.suspend(true);
        tracker.add("one more");
        Assert.assertEquals(4, tracker.get().length);
    }

    @Test
    public void testSortedTracker() {
        DirtyTracker tracker = new SortedFieldTracker();
        Assert.assertTrue(tracker.isEmpty());
        Assert.assertEquals(0, tracker.get().length);
        tracker.add("foo");
        Assert.assertFalse(tracker.isEmpty());
        Assert.assertArrayEquals(tracker.get(), new String[]{ "foo" });
        tracker.clear();
        Assert.assertTrue(tracker.isEmpty());
        Assert.assertEquals(0, tracker.get().length);
        tracker.add("foo");
        tracker.add("bar");
        tracker.add("another.bar");
        tracker.add("foo");
        tracker.add("another.foo");
        tracker.add("another.bar");
        Assert.assertEquals(4, tracker.get().length);
        // the algorithm for this implementation relies on the fact that the array is kept sorted, so let's check it really is
        Assert.assertTrue(isSorted(tracker.get()));
        tracker.suspend(true);
        tracker.add("one more");
        Assert.assertEquals(4, tracker.get().length);
    }
}

