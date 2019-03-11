package org.roaringbitmap.buffer;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.ShortIterator;


public class TestReverseMappeableRunContainer {
    @Test
    public void testClone() {
        MappeableRunContainer mappeableRunContainer = new MappeableRunContainer();
        for (short i = 10; i < 20; ++i) {
            mappeableRunContainer.add(i);
        }
        ReverseMappeableRunContainerShortIterator rmr = new ReverseMappeableRunContainerShortIterator(mappeableRunContainer);
        ShortIterator rmrClone = rmr.clone();
        final List<Integer> rmrList = TestReverseMappeableRunContainer.asList(rmr);
        final List<Integer> rmrCloneList = TestReverseMappeableRunContainer.asList(rmrClone);
        Assert.assertTrue(rmrList.equals(rmrCloneList));
    }

    @Test
    public void testNextAsInt() {
        MappeableRunContainer mappeableRunContainer = new MappeableRunContainer();
        for (short i = 10; i < 15; ++i) {
            mappeableRunContainer.add(i);
        }
        ReverseMappeableRunContainerShortIterator rmr = new ReverseMappeableRunContainerShortIterator(mappeableRunContainer);
        Assert.assertEquals(14, rmr.nextAsInt());
        rmr.next();
        rmr.next();
        rmr.next();
        rmr.next();
        rmr.next();
        rmr.nextAsInt();
        rmr.nextAsInt();
        rmr.nextAsInt();
        rmr.nextAsInt();
        rmr.nextAsInt();
        Assert.assertEquals(13, rmr.nextAsInt());
    }
}

