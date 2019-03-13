package org.roaringbitmap;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class UnorderedRoaringBitmapWriterRandomisedTest {
    private final int[] data;

    public UnorderedRoaringBitmapWriterRandomisedTest(int[] data) {
        this.data = data;
    }

    @Test
    public void bitmapOfUnorderedShouldBuildSameBitmapAsBitmapOf() {
        RoaringBitmap baseline = RoaringBitmap.bitmapOf(data);
        RoaringBitmap test = RoaringBitmap.bitmapOfUnordered(data);
        RoaringArray baselineHLC = baseline.highLowContainer;
        RoaringArray testHLC = test.highLowContainer;
        Assert.assertEquals(baselineHLC.size, testHLC.size);
        for (int i = 0; i < (baselineHLC.size); ++i) {
            Container baselineContainer = baselineHLC.getContainerAtIndex(i);
            Container rbContainer = testHLC.getContainerAtIndex(i);
            Assert.assertEquals(baselineContainer, rbContainer);
        }
        Assert.assertEquals(baseline, test);
    }
}

