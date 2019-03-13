package org.jctools.sets;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class HashSetTest {
    static class Key {
        @Override
        public String toString() {
            return ("Key [hash=" + (hash)) + "]";
        }

        final int hash;

        Key(int i) {
            hash = i;
        }

        public int hashCode() {
            return hash;
        }

        public boolean equals(Object obj) {
            return (this) == obj;
        }
    }

    final Set<HashSetTest.Key> set;

    public HashSetTest(Set<HashSetTest.Key> set) {
        super();
        this.set = set;
    }

    @Test
    public void testAddRemove() {
        HashSetTest.Key e = new HashSetTest.Key(1024);
        HashSetTest.Key j = new HashSetTest.Key(2048);
        Assert.assertTrue(set.add(e));
        Assert.assertTrue(set.contains(e));
        Assert.assertFalse(set.contains(j));
        Assert.assertFalse(set.add(e));
        Assert.assertTrue(set.remove(e));
        Assert.assertFalse(set.contains(e));
        Assert.assertFalse(set.remove(e));
    }

    @Test
    public void testIterator() {
        int sum = 0;
        for (int i = 0; i < 1024; i += 63) {
            Assert.assertTrue(set.add(new HashSetTest.Key(i)));
            sum += i;
        }
        Iterator<HashSetTest.Key> iter = set.iterator();
        while (iter.hasNext()) {
            sum -= iter.next().hashCode();
            iter.remove();
        } 
        Assert.assertEquals(0, set.size());
        Assert.assertEquals(0, sum);
    }

    @Test
    public void testRandom() {
        Random r = new Random();
        final long seed = r.nextLong();
        r.setSeed(seed);
        HashSetTest.Key[] keys = new HashSetTest.Key[1024];
        for (int i = 0; i < (keys.length); i++) {
            final int hash = r.nextInt(keys.length);
            keys[i] = new HashSetTest.Key(hash);
        }
        HashSet<HashSetTest.Key> setRef = new HashSet<>();
        long until = (System.currentTimeMillis()) + 1000;
        while ((System.currentTimeMillis()) < until) {
            HashSetTest.Key e = keys[r.nextInt(keys.length)];
            Assert.assertEquals(setRef.add(e), set.add(e));
            e = keys[r.nextInt(keys.length)];
            Assert.assertEquals(setRef.remove(e), set.remove(e));
        } 
        Assert.assertEquals(setRef.size(), set.size());
        Assert.assertTrue(setRef.containsAll(set));
        Assert.assertTrue(set.containsAll(setRef));
    }
}

