package org.roaringbitmap.buffer;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.PeekableShortIterator;


public class TestMappeableBitmapContainerShortIterator {
    @Test
    public void testClone() {
        MappeableBitmapContainer mappeableBitmapContainer = new MappeableBitmapContainer();
        for (int k = 0; k < (2 * (MappeableArrayContainer.DEFAULT_MAX_SIZE)); ++k) {
            mappeableBitmapContainer.add(((short) (k * 10)));
        }
        MappeableBitmapContainerShortIterator tmbc = new MappeableBitmapContainerShortIterator(mappeableBitmapContainer);
        PeekableShortIterator tmbcClone = tmbc.clone();
        final List<Integer> tmbcList = TestMappeableBitmapContainerShortIterator.asList(tmbc);
        final List<Integer> tmbcCloneList = TestMappeableBitmapContainerShortIterator.asList(tmbcClone);
        Assert.assertEquals(tmbcList, tmbcCloneList);
    }
}

