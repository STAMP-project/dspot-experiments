package org.jctools.maps.nbhs_test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.hamcrest.CoreMatchers;
import org.jctools.maps.NonBlockingSetInt;
import org.junit.Assert;
import org.junit.Test;


/* Written by Cliff Click and released to the public domain, as explained at
http://creativecommons.org/licenses/publicdomain
 */
// Test NonBlockingSetInt via JUnit
public class nbsi_tester {
    private static NonBlockingSetInt _nbsi;

    // Test some basic stuff; add a few keys, remove a few keys
    @Test
    public void testBasic() {
        Assert.assertTrue(nbsi_tester._nbsi.isEmpty());
        Assert.assertTrue(nbsi_tester._nbsi.add(1));
        checkSizes(1);
        Assert.assertTrue(nbsi_tester._nbsi.add(2));
        checkSizes(2);
        Assert.assertFalse(nbsi_tester._nbsi.add(1));
        Assert.assertFalse(nbsi_tester._nbsi.add(2));
        checkSizes(2);
        Assert.assertThat(nbsi_tester._nbsi.remove(1), CoreMatchers.is(true));
        checkSizes(1);
        Assert.assertThat(nbsi_tester._nbsi.remove(1), CoreMatchers.is(false));
        Assert.assertTrue(nbsi_tester._nbsi.remove(2));
        checkSizes(0);
        Assert.assertFalse(nbsi_tester._nbsi.remove(2));
        Assert.assertFalse(nbsi_tester._nbsi.remove(3));
        Assert.assertTrue(nbsi_tester._nbsi.isEmpty());
        Assert.assertTrue(nbsi_tester._nbsi.add(63));
        checkSizes(1);
        Assert.assertTrue(nbsi_tester._nbsi.remove(63));
        Assert.assertFalse(nbsi_tester._nbsi.remove(63));
        Assert.assertTrue(nbsi_tester._nbsi.isEmpty());
        Assert.assertTrue(nbsi_tester._nbsi.add(10000));
        checkSizes(1);
        Assert.assertTrue(nbsi_tester._nbsi.add(20000));
        checkSizes(2);
        Assert.assertFalse(nbsi_tester._nbsi.add(10000));
        Assert.assertFalse(nbsi_tester._nbsi.add(20000));
        checkSizes(2);
        Assert.assertThat(nbsi_tester._nbsi.remove(10000), CoreMatchers.is(true));
        checkSizes(1);
        Assert.assertThat(nbsi_tester._nbsi.remove(10000), CoreMatchers.is(false));
        Assert.assertTrue(nbsi_tester._nbsi.remove(20000));
        checkSizes(0);
        Assert.assertFalse(nbsi_tester._nbsi.remove(20000));
        nbsi_tester._nbsi.clear();
    }

    @Test
    public void testIteration() {
        Assert.assertTrue(nbsi_tester._nbsi.isEmpty());
        Assert.assertTrue(nbsi_tester._nbsi.add(1));
        Assert.assertTrue(nbsi_tester._nbsi.add(2));
        StringBuilder buf = new StringBuilder();
        for (Integer val : nbsi_tester._nbsi) {
            buf.append(val);
        }
        Assert.assertThat("found all vals", buf.toString(), CoreMatchers.anyOf(CoreMatchers.is("12"), CoreMatchers.is("21")));
        Assert.assertThat("toString works", nbsi_tester._nbsi.toString(), CoreMatchers.anyOf(CoreMatchers.is("[1, 2]"), CoreMatchers.is("[2, 1]")));
        nbsi_tester._nbsi.clear();
    }

    @Test
    public void testIterationBig() {
        for (int i = 0; i < 100; i++) {
            nbsi_tester._nbsi.add(i);
        }
        Assert.assertThat(nbsi_tester._nbsi.size(), CoreMatchers.is(100));
        int sz = 0;
        int sum = 0;
        for (Integer x : nbsi_tester._nbsi) {
            sz++;
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= 99)));
        }
        Assert.assertThat("Found 100 ints", sz, CoreMatchers.is(100));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is(((100 * 99) / 2)));
        Assert.assertThat("can remove 3", nbsi_tester._nbsi.remove(3), CoreMatchers.is(true));
        Assert.assertThat("can remove 4", nbsi_tester._nbsi.remove(4), CoreMatchers.is(true));
        sz = 0;
        sum = 0;
        for (Integer x : nbsi_tester._nbsi) {
            sz++;
            sum += x;
            Assert.assertTrue(((x >= 0) && (x <= 99)));
        }
        Assert.assertThat("Found 98 ints", sz, CoreMatchers.is(98));
        Assert.assertThat("Found all integers in list", sum, CoreMatchers.is((((100 * 99) / 2) - (3 + 4))));
        nbsi_tester._nbsi.clear();
    }

    @Test
    public void testSerial() {
        Assert.assertTrue(nbsi_tester._nbsi.isEmpty());
        Assert.assertTrue(nbsi_tester._nbsi.add(1));
        Assert.assertTrue(nbsi_tester._nbsi.add(2));
        // Serialize it out
        try {
            FileOutputStream fos = new FileOutputStream("NBSI_test.txt");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(nbsi_tester._nbsi);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // Read it back
        try {
            File f = new File("NBSI_test.txt");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream in = new ObjectInputStream(fis);
            NonBlockingSetInt nbsi = ((NonBlockingSetInt) (in.readObject()));
            in.close();
            Assert.assertEquals(nbsi_tester._nbsi.toString(), nbsi.toString());
            if (!(f.delete())) {
                throw new IOException("delete failed");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        nbsi_tester._nbsi.clear();
    }

    // Do some simple concurrent testing
    @Test
    public void testConcurrentSimple() throws InterruptedException {
        final NonBlockingSetInt nbsi = new NonBlockingSetInt();
        // In 2 threads, add & remove even & odd elements concurrently
        Thread t1 = new Thread() {
            public void run() {
                work_helper(nbsi, "T1", 1);
            }
        };
        t1.start();
        work_helper(nbsi, "T0", 1);
        t1.join();
        // In the end, all members should be removed
        StringBuffer buf = new StringBuffer();
        buf.append("Should be emptyset but has these elements: {");
        boolean found = false;
        for (Integer x : nbsi) {
            buf.append(" ").append(x);
            found = true;
        }
        if (found) {
            System.out.println(buf);
        }
        Assert.assertThat("concurrent size=0", nbsi.size(), CoreMatchers.is(0));
        for (Integer x : nbsi) {
            Assert.assertTrue("No elements so never get here", false);
        }
        nbsi_tester._nbsi.clear();
    }
}

