package net.coobird.thumbnailator.filters;


import Flip.HORIZONTAL;
import Flip.VERTICAL;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.test.BufferedImageAssert;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import net.coobird.thumbnailator.util.BufferedImages;
import org.junit.Assert;
import org.junit.Test;

import static Flip.HORIZONTAL;
import static Flip.VERTICAL;


public class FlipTest {
    @Test
    public void flipHorizontal() throws Exception {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Exif/original.png"));
        // when
        BufferedImage result = HORIZONTAL.apply(img);
        // then
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 1, 1, 1, 1, 1, 0, 0, 1 });
    }

    @Test
    public void flipVertical() throws Exception {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Exif/original.png"));
        // when
        BufferedImage result = VERTICAL.apply(img);
        // then
        BufferedImageAssert.assertMatches(result, new float[]{ 1, 0, 0, 1, 1, 1, 1, 1, 1 });
    }

    /**
     * Checks that the input image contents are not altered, when using the
     * {@link Flip#HORIZONTAL}.
     */
    @Test
    public void inputContentsAreNotAltered_UsingFlipHorizontal() {
        // given
        BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        BufferedImage copyImage = BufferedImages.copy(originalImage);
        ImageFilter filter = HORIZONTAL;
        // when
        filter.apply(originalImage);
        // then
        Assert.assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
    }

    /**
     * Checks that the input image contents are not altered, when using the
     * {@link Flip#VERTICAL}.
     */
    @Test
    public void inputContentsAreNotAltered_UsingFlipVertical() {
        // given
        BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        BufferedImage copyImage = BufferedImages.copy(originalImage);
        ImageFilter filter = VERTICAL;
        // when
        filter.apply(originalImage);
        // then
        Assert.assertTrue(BufferedImageComparer.isSame(originalImage, copyImage));
    }
}

