package net.coobird.thumbnailator.filters;


import java.awt.image.BufferedImage;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import net.coobird.thumbnailator.util.BufferedImages;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link Watermark} filter.
 *
 * @author coobird
 */
public class WatermarkTest {
    /**
     * Checks that the input image contents are not altered.
     */
    @Test
    public void inputContentsAreNotAltered() {
        // given
        BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        BufferedImage copyImage = BufferedImages.copy(originalImage);
        BufferedImage watermarkImg = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        ImageFilter filter = new Watermark(Positions.BOTTOM_CENTER, watermarkImg, 0.5F);
        // when
        filter.apply(originalImage);
        // then
        Assert.assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
    }
}

