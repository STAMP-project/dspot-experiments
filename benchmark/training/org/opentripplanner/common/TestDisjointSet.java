package org.opentripplanner.common;


import java.util.HashMap;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Test;


public class TestDisjointSet extends TestCase {
    @Test
    public void testSimple() {
        DisjointSet<String> set = new DisjointSet<String>();
        set.union("cats", "dogs");
        TestCase.assertEquals(set.size(set.find("cats")), 2);
        TestCase.assertEquals(set.find("cats"), set.find("dogs"));
        TestCase.assertTrue(((set.find("cats")) != (set.find("sparrows"))));
        TestCase.assertEquals(set.size(set.find("sparrows")), 1);
        set.union("sparrows", "robins");
        TestCase.assertEquals(set.size(set.find("robins")), 2);
        TestCase.assertTrue(((set.sets().size()) == 2));
        TestCase.assertTrue(((set.find("dogs")) != (set.find("robins"))));
        TestCase.assertEquals(set.find("sparrows"), set.find("robins"));
        set.union("sparrows", "dogs");
        TestCase.assertEquals(set.find("dogs"), set.find("robins"));
        TestCase.assertEquals(set.size(set.find("cats")), 4);
        TestCase.assertTrue(((set.sets().size()) == 1));
    }

    public void testRandom() {
        DisjointSet<Integer> set = new DisjointSet<Integer>();
        Random random = new Random(1);
        for (int i = 0; i < 150; ++i) {
            set.union(Math.abs(((random.nextInt()) % 700)), Math.abs(((random.nextInt()) % 700)));
        }
        HashMap<Integer, Integer> seen = new HashMap<Integer, Integer>();
        int sizeSum = 0;
        for (int i = 0; i < 700; ++i) {
            int key = set.find(i);
            int size = set.size(key);
            Integer lastSize = seen.get(key);
            TestCase.assertTrue(((lastSize == null) || (size == lastSize)));
            if (lastSize == null) {
                seen.put(key, size);
                sizeSum += size;
            }
            TestCase.assertTrue(((size >= 1) && (size <= 150)));
        }
        TestCase.assertEquals(700, sizeSum);
    }
}

