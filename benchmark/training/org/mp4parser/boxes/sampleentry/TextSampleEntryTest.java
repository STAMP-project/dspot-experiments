package org.mp4parser.boxes.sampleentry;


import com.googlecode.mp4parser.boxes.BoxWriteReadBase;
import org.junit.Assert;
import org.junit.Test;


public class TextSampleEntryTest extends BoxWriteReadBase<TextSampleEntry> {
    @Test
    public void testBitSetters() {
        TextSampleEntry tx3g = new TextSampleEntry();
        tx3g.setContinuousKaraoke(true);
        Assert.assertTrue(tx3g.isContinuousKaraoke());
        tx3g.setContinuousKaraoke(false);
        Assert.assertFalse(tx3g.isContinuousKaraoke());
    }
}

