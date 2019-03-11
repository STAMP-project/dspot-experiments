package org.jctools.maps.nbhs_test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.hamcrest.CoreMatchers;
import org.jctools.maps.NonBlockingHashSet;
import org.junit.Assert;
import org.junit.Test;


/* Written by Cliff Click and released to the public domain, as explained at
http://creativecommons.org/licenses/publicdomain
 */
// Test NonBlockingHashSet via JUnit
public class nbhs_tester {
    private static NonBlockingHashSet<String> _nbhs;

    // Test some basic stuff; add a few keys, remove a few keys
    @Test
    public void testBasic() {
        Assert.assertTrue(nbhs_tester._nbhs.isEmpty());
        Assert.assertTrue(nbhs_tester._nbhs.add("k1"));
        checkSizes(1);
        Assert.assertTrue(nbhs_tester._nbhs.add("k2"));
        checkSizes(2);
        Assert.assertFalse(nbhs_tester._nbhs.add("k1"));
        Assert.assertFalse(nbhs_tester._nbhs.add("k2"));
        checkSizes(2);
        Assert.assertThat(nbhs_tester._nbhs.remove("k1"), CoreMatchers.is(true));
        checkSizes(1);
        Assert.assertThat(nbhs_tester._nbhs.remove("k1"), CoreMatchers.is(false));
        Assert.assertTrue(nbhs_tester._nbhs.remove("k2"));
        checkSizes(0);
        Assert.assertFalse(nbhs_tester._nbhs.remove("k2"));
        Assert.assertFalse(nbhs_tester._nbhs.remove("k3"));
        Assert.assertTrue(nbhs_tester._nbhs.isEmpty());
    }

    @Test
    public void testIteration() {
        Assert.assertTrue(nbhs_tester._nbhs.isEmpty());
        Assert.assertTrue(nbhs_tester._nbhs.add("k1"));
        Assert.assertTrue(nbhs_tester._nbhs.add("k2"));
        StringBuilder buf = new StringBuilder();
        for (String val : nbhs_tester._nbhs) {
            buf.append(val);
        }
        Assert.assertThat("found all vals", buf.toString(), CoreMatchers.anyOf(CoreMatchers.is("k1k2"), CoreMatchers.is("k2k1")));
        Assert.assertThat("toString works", nbhs_tester._nbhs.toString(), CoreMatchers.anyOf(CoreMatchers.is("[k1, k2]"), CoreMatchers.is("[k2, k1]")));
        nbhs_tester._nbhs.clear();
    }

    @Test
    public void testIterationBig() {
        for (int i = 0; i < 100; i++) {
            nbhs_tester._nbhs.add(("a" + i));
        }
        Assert.assertThat(nbhs_tester._nbhs.size(), CoreMatchers.is(100));
        int sz = 0;
        int sum = 0;
        for (String s : nbhs_tester._nbhs) {
            sz++;
            Assert.assertThat("", s.charAt(0), CoreMatchers.is('a'));
            int x = Integer.parseInt(s.substring(1));
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= 99)));
        }
        Assert.assertThat("Found 100 ints", sz, CoreMatchers.is(100));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is(((100 * 99) / 2)));
        Assert.assertThat("can remove 3", nbhs_tester._nbhs.remove("a3"), CoreMatchers.is(true));
        Assert.assertThat("can remove 4", nbhs_tester._nbhs.remove("a4"), CoreMatchers.is(true));
        sz = 0;
        sum = 0;
        for (String s : nbhs_tester._nbhs) {
            sz++;
            Assert.assertThat("", s.charAt(0), CoreMatchers.is('a'));
            int x = Integer.parseInt(s.substring(1));
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= 99)));
        }
        Assert.assertThat("Found 98 ints", sz, CoreMatchers.is(98));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is((((100 * 99) / 2) - (3 + 4))));
        nbhs_tester._nbhs.clear();
    }

    @Test
    public void testSerial() {
        Assert.assertTrue(nbhs_tester._nbhs.isEmpty());
        Assert.assertTrue(nbhs_tester._nbhs.add("k1"));
        Assert.assertTrue(nbhs_tester._nbhs.add("k2"));
        // Serialize it out
        try {
            FileOutputStream fos = new FileOutputStream("NBHS_test.txt");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(nbhs_tester._nbhs);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Read it back
        try {
            File f = new File("NBHS_test.txt");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            NonBlockingHashSet nbhs = ((NonBlockingHashSet) (in.readObject()));
            in.close();
            Assert.assertEquals(nbhs_tester._nbhs.toString(), nbhs.toString());
            if (!(f.delete())) {
                throw new IOException("delete failed");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}

