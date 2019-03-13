package org.ethereum.db.prune;


import Chain.NULL;
import Segment.Tracker;
import org.ethereum.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 31.01.2018
 */
public class SegmentTest {
    @Test
    public void simpleTest() {
        Segment s = new Segment(1, ByteUtil.intToBytes(1), ByteUtil.intToBytes(0));
        Assert.assertEquals(NULL, s.main);
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(0, s.getMaxNumber());
        Assert.assertEquals(0, s.size());
        Segment.Tracker t = s.startTracking();
        t.addMain(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1));
        t.commit();
        Assert.assertTrue(s.isComplete());
        Assert.assertEquals(2, s.getMaxNumber());
        Assert.assertEquals(1, s.size());
        t = s.startTracking();
        t.addItem(2, ByteUtil.intToBytes(21), ByteUtil.intToBytes(1));
        t.addItem(2, ByteUtil.intToBytes(22), ByteUtil.intToBytes(1));
        t.addItem(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2));
        t.addMain(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2));// should process double adding

        t.commit();
        Assert.assertTrue(s.isComplete());
        Assert.assertEquals(3, s.getMaxNumber());
        Assert.assertEquals(2, s.size());
        Assert.assertEquals(2, s.forks.size());
        t = s.startTracking();
        t.addItem(3, ByteUtil.intToBytes(31), ByteUtil.intToBytes(21));
        t.commit();
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(3, s.getMaxNumber());
        Assert.assertEquals(2, s.size());
        Assert.assertEquals(2, s.forks.size());
    }

    // short forks
    @Test
    public void testFork1() {
        Segment s = new Segment(1, ByteUtil.intToBytes(1), ByteUtil.intToBytes(0));
        s.startTracking().addItem(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)).addMain(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)).commit();
        Assert.assertTrue(s.isComplete());
        s.startTracking().addItem(3, ByteUtil.intToBytes(31), ByteUtil.intToBytes(2)).addItem(3, ByteUtil.intToBytes(32), ByteUtil.intToBytes(2)).addItem(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)).addMain(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)).commit();
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(2, s.size());
        Assert.assertEquals(2, s.forks.size());
        s.startTracking().addItem(4, ByteUtil.intToBytes(41), ByteUtil.intToBytes(31)).addItem(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)).addMain(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)).commit();
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(3, s.size());
        Assert.assertEquals(2, s.forks.size());
        Assert.assertEquals(4, s.getMaxNumber());
        s.startTracking().addItem(5, ByteUtil.intToBytes(53), ByteUtil.intToBytes(4)).addItem(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)).addMain(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)).commit();
        s.startTracking().addItem(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)).addMain(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)).commit();
        Assert.assertTrue(s.isComplete());
        Assert.assertEquals(5, s.size());
        Assert.assertEquals(3, s.forks.size());
        Assert.assertEquals(6, s.getMaxNumber());
    }

    // long fork with short forks
    @Test
    public void testFork2() {
        Segment s = new Segment(1, ByteUtil.intToBytes(1), ByteUtil.intToBytes(0));
        s.startTracking().addItem(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)).addMain(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)).commit();
        Assert.assertTrue(s.isComplete());
        s.startTracking().addItem(3, ByteUtil.intToBytes(30), ByteUtil.intToBytes(2)).addItem(3, ByteUtil.intToBytes(31), ByteUtil.intToBytes(2)).addItem(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)).addMain(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)).commit();
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(2, s.size());
        Assert.assertEquals(2, s.forks.size());
        s.startTracking().addItem(4, ByteUtil.intToBytes(40), ByteUtil.intToBytes(30)).addItem(4, ByteUtil.intToBytes(41), ByteUtil.intToBytes(31)).addItem(4, ByteUtil.intToBytes(42), ByteUtil.intToBytes(3)).addItem(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)).addMain(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)).commit();
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(3, s.size());
        Assert.assertEquals(3, s.forks.size());
        Assert.assertEquals(4, s.getMaxNumber());
        s.startTracking().addItem(5, ByteUtil.intToBytes(50), ByteUtil.intToBytes(40)).addItem(5, ByteUtil.intToBytes(53), ByteUtil.intToBytes(4)).addItem(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)).addMain(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)).commit();
        s.startTracking().addItem(6, ByteUtil.intToBytes(60), ByteUtil.intToBytes(50)).addItem(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)).addMain(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)).commit();
        Assert.assertFalse(s.isComplete());
        Assert.assertEquals(5, s.size());
        Assert.assertEquals(4, s.forks.size());
        Assert.assertEquals(6, s.getMaxNumber());
        s.startTracking().addItem(7, ByteUtil.intToBytes(7), ByteUtil.intToBytes(6)).addMain(7, ByteUtil.intToBytes(7), ByteUtil.intToBytes(6)).commit();
        Assert.assertTrue(s.isComplete());
        Assert.assertEquals(6, s.size());
        Assert.assertEquals(4, s.forks.size());
        Assert.assertEquals(7, s.getMaxNumber());
    }

    // several branches started at fork block
    @Test
    public void testFork3() {
        /* 2: 2 -> 3: 3  -> 4: 4  -> 5: 5  -> 6: 6 <-- Main
        \
        -> 3: 31 -> 4: 41
        \          -> 5: 53
        \        /
        -> 4: 42 -> 5: 52
        \
        -> 5: 54
         */
        Segment s = new Segment(1, ByteUtil.intToBytes(1), ByteUtil.intToBytes(0));
        s.startTracking().addItem(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)).addMain(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)).commit();
        s.startTracking().addItem(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)).addItem(3, ByteUtil.intToBytes(31), ByteUtil.intToBytes(2)).addMain(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)).commit();
        s.startTracking().addItem(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)).addItem(4, ByteUtil.intToBytes(41), ByteUtil.intToBytes(31)).addItem(4, ByteUtil.intToBytes(42), ByteUtil.intToBytes(31)).addMain(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)).commit();
        s.startTracking().addItem(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)).addItem(5, ByteUtil.intToBytes(52), ByteUtil.intToBytes(42)).addItem(5, ByteUtil.intToBytes(53), ByteUtil.intToBytes(42)).addItem(5, ByteUtil.intToBytes(54), ByteUtil.intToBytes(42)).addMain(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)).commit();
        s.startTracking().addItem(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)).addMain(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)).commit();
        Chain main = Chain.fromItems(new ChainItem(2, ByteUtil.intToBytes(2), ByteUtil.intToBytes(1)), new ChainItem(3, ByteUtil.intToBytes(3), ByteUtil.intToBytes(2)), new ChainItem(4, ByteUtil.intToBytes(4), ByteUtil.intToBytes(3)), new ChainItem(5, ByteUtil.intToBytes(5), ByteUtil.intToBytes(4)), new ChainItem(6, ByteUtil.intToBytes(6), ByteUtil.intToBytes(5)));
        Chain fork1 = Chain.fromItems(new ChainItem(3, ByteUtil.intToBytes(31), ByteUtil.intToBytes(2)), new ChainItem(4, ByteUtil.intToBytes(41), ByteUtil.intToBytes(31)));
        Chain fork2 = Chain.fromItems(new ChainItem(4, ByteUtil.intToBytes(42), ByteUtil.intToBytes(31)), new ChainItem(5, ByteUtil.intToBytes(52), ByteUtil.intToBytes(42)));
        Chain fork3 = Chain.fromItems(new ChainItem(5, ByteUtil.intToBytes(53), ByteUtil.intToBytes(42)));
        Chain fork4 = Chain.fromItems(new ChainItem(5, ByteUtil.intToBytes(54), ByteUtil.intToBytes(42)));
        Assert.assertEquals(main, s.main);
        Assert.assertEquals(4, s.forks.size());
        Assert.assertEquals(fork1, s.forks.get(0));
        Assert.assertEquals(fork2, s.forks.get(1));
        Assert.assertEquals(fork3, s.forks.get(2));
        Assert.assertEquals(fork4, s.forks.get(3));
    }
}

