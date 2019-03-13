package org.mp4parser;


import org.junit.Assert;
import org.junit.Test;
import org.mp4parser.boxes.iso14496.part12.MovieBox;
import org.mp4parser.boxes.iso14496.part12.TrackHeaderBox;
import org.mp4parser.tools.Path;


public class SkippingBoxTest {
    private IsoFile isoFile;

    @Test
    public void testBoxesHaveBeenSkipped() {
        MovieBox movieBox = isoFile.getMovieBox();
        Assert.assertNotNull(movieBox);
        Assert.assertEquals(4, movieBox.getBoxes().size());
        Assert.assertEquals("mvhd", movieBox.getBoxes().get(0).getType());
        Assert.assertEquals("iods", movieBox.getBoxes().get(1).getType());
        Assert.assertEquals("trak", movieBox.getBoxes().get(2).getType());
        Assert.assertEquals("udta", movieBox.getBoxes().get(3).getType());
        Box box = Path.getPath(isoFile, "moov/trak/tkhd");
        Assert.assertTrue((box instanceof TrackHeaderBox));
        TrackHeaderBox thb = ((TrackHeaderBox) (box));
        Assert.assertTrue(((thb.getDuration()) == 102595));
        box = Path.getPath(isoFile, "mdat");
        Assert.assertTrue((box instanceof SkipBox));
        box = Path.getPath(isoFile, "moov/mvhd");
        Assert.assertTrue((box instanceof SkipBox));
    }
}

