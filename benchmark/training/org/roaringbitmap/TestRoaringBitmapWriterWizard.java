package org.roaringbitmap;


import org.junit.Assert;
import org.junit.Test;


public class TestRoaringBitmapWriterWizard {
    @Test
    public void whenConstantMemoryIsSelectedWizardCreatesConstantMemoryWriter() {
        Assert.assertTrue(((RoaringBitmapWriter.writer().constantMemory().get()) instanceof ConstantMemoryContainerAppender));
    }

    @Test
    public void whenFastRankIsSelectedWizardCreatesFastRankRoaringBitmap() {
        Assert.assertNotNull(RoaringBitmapWriter.writer().fastRank().get().getUnderlying());
    }

    @Test(expected = IllegalStateException.class)
    public void whenFastRankIsSelectedBufferWizardThrows() {
        RoaringBitmapWriter.bufferWriter().fastRank().get().getUnderlying();
    }

    @Test
    public void shouldRespectProvidedStorageSizeHint() {
        Assert.assertEquals(20, RoaringBitmapWriter.writer().initialCapacity(20).get().getUnderlying().highLowContainer.keys.length);
    }
}

