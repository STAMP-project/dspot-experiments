package net.coobird.thumbnailator.resizers;


import java.awt.image.BufferedImage;
import org.junit.Assert;
import org.junit.Test;


public class ProgressiveBilinearResizerTest {
    /**
     * Test for
     * {@link ProgressiveBilinearResizer#resize(BufferedImage, BufferedImage)}
     * where,
     *
     * 1) source image is null.
     * 2) destination image is null.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void resizeNullAndNull() {
        BufferedImage srcImage = null;
        BufferedImage destImage = null;
        new ProgressiveBilinearResizer().resize(srcImage, destImage);
        Assert.fail();
    }

    /**
     * Test for
     * {@link ProgressiveBilinearResizer#resize(BufferedImage, BufferedImage)}
     * where,
     *
     * 1) source image is specified.
     * 2) destination image is null.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void resizeSpecifiedAndNull() {
        BufferedImage srcImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        BufferedImage destImage = null;
        new ProgressiveBilinearResizer().resize(srcImage, destImage);
        Assert.fail();
    }

    /**
     * Test for
     * {@link ProgressiveBilinearResizer#resize(BufferedImage, BufferedImage)}
     * where,
     *
     * 1) source image is null.
     * 2) destination image is specified.
     *
     * Expected outcome is,
     *
     * 1) Processing will stop with an NullPointerException.
     *
     * @throws IOException
     * 		
     */
    @Test(expected = NullPointerException.class)
    public void resizeNullAndSpecified() {
        BufferedImage srcImage = null;
        BufferedImage destImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        new ProgressiveBilinearResizer().resize(srcImage, destImage);
        Assert.fail();
    }
}

