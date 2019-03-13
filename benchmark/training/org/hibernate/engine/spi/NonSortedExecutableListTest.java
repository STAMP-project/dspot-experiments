/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.spi;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.action.spi.Executable;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Anton Marsden
 * @author Gail Badner
 */
public class NonSortedExecutableListTest extends BaseUnitTestCase {
    // For testing, we need an Executable that is also Comparable and Serializable
    private static class AnExecutable implements Serializable , Comparable , Executable {
        private final int n;

        private Serializable[] spaces;

        private transient boolean afterDeserializeCalled;

        public AnExecutable(int n, String... spaces) {
            this.n = n;
            this.spaces = spaces;
        }

        @Override
        public int compareTo(Object o) {
            return new Integer(n).compareTo(new Integer(((NonSortedExecutableListTest.AnExecutable) (o)).n));
        }

        @Override
        public int hashCode() {
            return n;
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj)
                return true;

            if (obj == null)
                return false;

            NonSortedExecutableListTest.AnExecutable other = ((NonSortedExecutableListTest.AnExecutable) (obj));
            return (n) == (other.n);
        }

        @Override
        public Serializable[] getPropertySpaces() {
            return spaces;
        }

        @Override
        public void beforeExecutions() throws HibernateException {
        }

        @Override
        public void execute() throws HibernateException {
        }

        @Override
        public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
            return null;
        }

        @Override
        public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
            return null;
        }

        @Override
        public void afterDeserialize(SharedSessionContractImplementor session) {
            this.afterDeserializeCalled = true;
        }

        public String toString() {
            return String.valueOf(n);
        }
    }

    private ExecutableList<NonSortedExecutableListTest.AnExecutable> l;

    private NonSortedExecutableListTest.AnExecutable action1 = new NonSortedExecutableListTest.AnExecutable(0, "a");

    private NonSortedExecutableListTest.AnExecutable action2 = new NonSortedExecutableListTest.AnExecutable(1, "b", "c");

    private NonSortedExecutableListTest.AnExecutable action3 = new NonSortedExecutableListTest.AnExecutable(2, "b", "d");

    private NonSortedExecutableListTest.AnExecutable action4 = new NonSortedExecutableListTest.AnExecutable(3);

    @Test
    public void testAdd() {
        Assert.assertEquals(0, l.size());
        l.add(action1);
        Assert.assertEquals(action1, l.get(0));
        Assert.assertEquals(1, l.size());
        l.add(action2);
        Assert.assertEquals(action2, l.get(1));
        l.add(action3);
        Assert.assertEquals(action3, l.get(2));
        Assert.assertEquals(3, l.size());
    }

    @Test
    public void testClear() {
        Assert.assertTrue(l.isEmpty());
        l.add(action1);
        Assert.assertFalse(l.isEmpty());
        l.add(action2);
        l.clear();
        Assert.assertTrue(l.isEmpty());
        Assert.assertEquals(0, l.size());
    }

    @Test
    public void testIterator() {
        l.add(action1);
        l.add(action2);
        l.add(action3);
        Iterator<NonSortedExecutableListTest.AnExecutable> iterator = l.iterator();
        Assert.assertEquals(action1, iterator.next());
        Assert.assertEquals(action2, iterator.next());
        Assert.assertEquals(action3, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testRemoveLastN() {
        l.add(action1);
        l.add(action2);
        l.add(action3);
        l.removeLastN(0);
        Assert.assertEquals(3, l.size());
        l.removeLastN(2);
        Assert.assertEquals(1, l.size());
        Assert.assertEquals(action1, l.get(0));
    }

    @Test
    public void testGetSpaces() {
        l.add(action1);
        Set<Serializable> ss = l.getQuerySpaces();
        Assert.assertEquals(1, ss.size());
        Assert.assertTrue(ss.contains("a"));
        l.add(action2);
        l.add(action3);
        l.add(action4);
        Set<Serializable> ss2 = l.getQuerySpaces();
        Assert.assertEquals(4, ss2.size());
        Assert.assertTrue(ss2.contains("a"));
        Assert.assertTrue(ss2.contains("b"));
        Assert.assertTrue(ss2.contains("c"));
        Assert.assertTrue(ss2.contains("d"));
        Assert.assertTrue((ss == ss2));// same Set (cached)

        // now remove action4
        l.remove(3);
        ss2 = l.getQuerySpaces();
        Assert.assertTrue((ss == ss2));// same Set (action4 has no spaces)

        Assert.assertEquals(4, ss2.size());
        l.remove(2);
        ss2 = l.getQuerySpaces();
        Assert.assertTrue((ss != ss2));// Different Set because it has been rebuilt. This would be incorrect if

        // Set.clear() was used
    }

    @Test
    public void testSort() {
        l.add(action4);
        l.add(action3);
        l.add(action2);
        l.add(action1);
        // sort should have no affect
        l.sort();
        Assert.assertEquals(action4, l.get(0));
        Assert.assertEquals(action3, l.get(1));
        Assert.assertEquals(action2, l.get(2));
        Assert.assertEquals(action1, l.get(3));
    }

    @Test
    public void testSerializeDeserialize() throws IOException, ClassNotFoundException {
        l.add(action4);
        l.add(action3);
        l.add(action2);
        l.add(action1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        l.writeExternal(oos);
        // this OOS stream needs to be flushed...
        oos.flush();
        ByteArrayInputStream bin = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        l = new ExecutableList<NonSortedExecutableListTest.AnExecutable>(false);
        l.readExternal(ois);
        Assert.assertEquals(4, l.size());
        Assert.assertEquals(action4, l.get(0));
        Assert.assertEquals(action3, l.get(1));
        Assert.assertEquals(action2, l.get(2));
        Assert.assertEquals(action1, l.get(3));
        Assert.assertFalse(l.get(0).afterDeserializeCalled);
        Assert.assertFalse(l.get(1).afterDeserializeCalled);
        Assert.assertFalse(l.get(2).afterDeserializeCalled);
        Assert.assertFalse(l.get(3).afterDeserializeCalled);
        l.afterDeserialize(null);
        Assert.assertTrue(l.get(0).afterDeserializeCalled);
        Assert.assertTrue(l.get(1).afterDeserializeCalled);
        Assert.assertTrue(l.get(2).afterDeserializeCalled);
        Assert.assertTrue(l.get(3).afterDeserializeCalled);
        Assert.assertEquals(action4, l.get(0));
        Assert.assertEquals(action3, l.get(1));
        Assert.assertEquals(action2, l.get(2));
        Assert.assertEquals(action1, l.get(3));
        // sort after deserializing; it should still have no affect
        l.sort();
        Assert.assertEquals(action4, l.get(0));
        Assert.assertEquals(action3, l.get(1));
        Assert.assertEquals(action2, l.get(2));
        Assert.assertEquals(action1, l.get(3));
    }
}

