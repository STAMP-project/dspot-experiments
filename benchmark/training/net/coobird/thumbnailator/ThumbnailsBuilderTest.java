package net.coobird.thumbnailator;


import AlphaInterpolation.SPEED;
import Dithering.DEFAULT;
import Positions.CENTER;
import Rename.PREFIX_DOT_THUMBNAIL;
import Resizers.NULL;
import Resizers.PROGRESSIVE;
import ScalingMode.PROGRESSIVE_BILINEAR;
import ThumbnailParameter.DEFAULT_FORMAT_TYPE;
import ThumbnailParameter.ORIGINAL_FORMAT;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import net.coobird.thumbnailator.geometry.AbsoluteSize;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Region;
import net.coobird.thumbnailator.name.Rename;
import net.coobird.thumbnailator.resizers.DefaultResizerFactory;
import net.coobird.thumbnailator.resizers.Resizer;
import net.coobird.thumbnailator.resizers.ResizerFactory;
import net.coobird.thumbnailator.test.BufferedImageComparer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * A class which tests the behavior of the builder interface of the
 * {@link Thumbnails} class.
 *
 * @author coobird
 */
public class ThumbnailsBuilderTest {
    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The width is 0.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     */
    @Test
    public void sizeWithZeroWidth() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            // when
            Thumbnails.of(img).size(0, 50).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The height is 0.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     */
    @Test
    public void sizeWithZeroHeight() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            // when
            Thumbnails.of(img).size(50, 0).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The height is Integer.MAX_VALUE.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The image is created</li>
     * <li>The thumbnail has the size based on the width</li>
     * </ol>
     */
    @Test
    public void sizeWithHeightAsALargeNumber() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(50, Integer.MAX_VALUE).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The width is Integer.MAX_VALUE.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The image is created</li>
     * <li>The thumbnail has the size based on the height</li>
     * </ol>
     */
    @Test
    public void sizeWithWidthAsALargeNumber() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(Integer.MAX_VALUE, 50).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The width and height is 0.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     */
    @Test
    public void sizeWithZeroWidthAndHeight() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            // when
            Thumbnails.of(img).size(0, 0).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully created.</li>
     * <li>The thumbnail dimensions are that which is specified by the
     * size method.</li>
     * </ol>
     */
    @Test
    public void sizeOnly() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage thumbnail = Thumbnails.of(img).size(50, 50).asBufferedImage();
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called twice.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void sizeTwice() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(50, 50).size(50, 50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called twice.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void widthTwice() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).width(50).width(50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called.</li>
     * <li>Then, the size method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void widthThenSize() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).width(50).size(50, 50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>Then, the width method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void sizeThenWidth() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(50, 50).width(50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called twice.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void heightTwice() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).height(50).height(50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called.</li>
     * <li>Then the size method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void heightThenSize() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).height(50).size(50, 50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>Then, the height method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void sizeThenHeight() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(50, 50).height(50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The keepAspectRatio method is true.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully created.</li>
     * <li>The thumbnail dimensions are not the same as what is specified
     * by the size method, but one that keeps the aspect ratio of the
     * original.</li>
     * </ol>
     */
    @Test
    public void sizeWithAspectRatioTrue() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage thumbnail = Thumbnails.of(img).size(120, 50).keepAspectRatio(true).asBufferedImage();
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The size method is called.</li>
     * <li>The keepAspectRatio method is false.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully created.</li>
     * <li>The thumbnail dimensions are that which is specified by the
     * size method.</li>
     * </ol>
     */
    @Test
    public void sizeWithAspectRatioFalse() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage thumbnail = Thumbnails.of(img).size(120, 50).keepAspectRatio(false).asBufferedImage();
        Assert.assertEquals(120, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void sizeWithScale() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).size(100, 100).scale(0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scale cannot be set"));
        }
    }

    @Test
    public void sizeWithScaleTwoArg() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).size(100, 100).scale(0.5, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scale cannot be set"));
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The forceSize method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully created.</li>
     * <li>The thumbnail dimensions are that which is specified by the
     * size method.</li>
     * </ol>
     */
    @Test
    public void forceSizeOnly() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage thumbnail = Thumbnails.of(img).forceSize(50, 50).asBufferedImage();
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The forceSize method is called twice.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void forceSizeTwice() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).forceSize(50, 50).forceSize(50, 50).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The forceSize method is called.</li>
     * <li>The keepAspectRatio method is called with true.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void forceSizeWithAspectRatioTrue() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).forceSize(50, 50).keepAspectRatio(true).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The forceSize method is called.</li>
     * <li>The keepAspectRatio method is called with false.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void forceSizeWithAspectRatioFalse() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).forceSize(50, 50).keepAspectRatio(false).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The allowOverwrite method is called once.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>No exceptions are thrown.</li>
     * </ol>
     */
    @Test
    public void allowOverwriteCalledOnce() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).allowOverwrite(true).asBufferedImage();
        // then
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The allowOverwrite method is called twice.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void allowOverwriteCalledTwice() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).allowOverwrite(true).allowOverwrite(true).asBufferedImage();
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scale method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully created.</li>
     * <li>The thumbnail dimensions are determined by the value passed into the
     * scale method.</li>
     * </ol>
     */
    @Test
    public void scaleOnly() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage thumbnail = Thumbnails.of(img).scale(0.25F).asBufferedImage();
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scale method is called.</li>
     * <li>The keepAspectRatio method is true.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test
    public void scaleWithAspectRatioTrue() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            Thumbnails.of(img).scale(0.5F).keepAspectRatio(true).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("scaling factor has already been specified"));
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scale method is called.</li>
     * <li>The keepAspectRatio method is false.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test
    public void scaleWithAspectRatioFalse() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            Thumbnails.of(img).scale(0.5F).keepAspectRatio(false).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("scaling factor has already been specified"));
        }
    }

    @Test
    public void scaleWithScaleTwoArg() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.5F).scale(0.5, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scale is already set"));
        }
    }

    @Test
    public void scaleWithSize() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.5F).size(100, 100).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("size cannot be set"));
        }
    }

    @Test
    public void scaleTwoArgOnly() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).scale(0.6, 0.4).asBufferedImage();
        // then
        Assert.assertEquals(120, thumbnail.getWidth());
        Assert.assertEquals(80, thumbnail.getHeight());
    }

    @Test
    public void scaleTwoArgWithAspectRatioTrue() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.6, 0.4).keepAspectRatio(true).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor has already been specified"));
        }
    }

    @Test
    public void scaleTwoArgWithAspectRatioFalse() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.6, 0.4).keepAspectRatio(false).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor has already been specified"));
        }
    }

    @Test
    public void scaleTwoArgWithScaleOneArg() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.6, 0.4).scale(0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scale is already set"));
        }
    }

    @Test
    public void scaleTwoArgWithScaleTwoArg() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.6, 0.4).scale(0.5, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scale is already set"));
        }
    }

    @Test
    public void scaleTwoArgWithSize() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.6, 0.4).size(100, 100).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("size cannot be set"));
        }
    }

    @Test
    public void scaleWithZeroFactor() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.0).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleTwoArgWithValidAndZeroFactor() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.5, 0.0).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleTwoArgWithZeroFactorAndValid() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.0, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleTwoArgWithZeroFactorAndZeroFactor() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.0, 0.0).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleWithNaN() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.NaN).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is not a number"));
        }
    }

    @Test
    public void scaleTwoArgWithValidAndNaN() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.5, Double.NaN).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is not a number"));
        }
    }

    @Test
    public void scaleTwoArgWithNaNAndValid() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.NaN, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is not a number"));
        }
    }

    @Test
    public void scaleTwoArgWithNaNAndNaN() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.NaN, Double.NaN).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is not a number"));
        }
    }

    @Test
    public void scaleWithPositiveInfinity() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.POSITIVE_INFINITY).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor cannot be infinity"));
        }
    }

    @Test
    public void scaleTwoArgWithValidAndPositiveInfinity() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.5, Double.POSITIVE_INFINITY).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor cannot be infinity"));
        }
    }

    @Test
    public void scaleTwoArgWithPositiveInfinityAndValid() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.POSITIVE_INFINITY, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor cannot be infinity"));
        }
    }

    @Test
    public void scaleTwoArgWithPositiveInfinityAndPositiveInfinity() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor cannot be infinity"));
        }
    }

    @Test
    public void scaleWithNegativeInfinity() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.NEGATIVE_INFINITY).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleTwoArgWithValidAndNegativeInfinity() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(0.5, Double.NEGATIVE_INFINITY).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleTwoArgWithNegativeInfinityAndValid() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.NEGATIVE_INFINITY, 0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    @Test
    public void scaleTwoArgWithNegativeInfinityAndNegativeInfinity() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        try {
            Thumbnails.of(img).scale(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY).asBufferedImage();
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
            Assert.assertTrue(e.getMessage().contains("scaling factor is equal to or less than 0"));
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The keepAspectRatio method is called before the size method.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test
    public void keepAspectRatioBeforeSize() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            Thumbnails.of(img).keepAspectRatio(false);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("unless the size parameter has already been specified."));
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The keepAspectRatio method is called after the scale method.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test
    public void keepAspectRatioAfterScale() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            Thumbnails.of(img).scale(0.5F).keepAspectRatio(false).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("scaling factor has already been specified"));
        }
    }

    @Test
    public void keepAspectRatioAfterScaleTwoArg() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            Thumbnails.of(img).scale(0.6, 0.4).keepAspectRatio(true).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("scaling factor has already been specified"));
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The specified Resizer is called for resizing.</li>
     * </ol>
     */
    @Test
    public void resizerOnly() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Resizer resizer = Mockito.mock(Resizer.class);
        BufferedImage thumbnail = Thumbnails.of(img).size(200, 200).resizer(resizer).asBufferedImage();
        Mockito.verify(resizer).resize(img, thumbnail);
        Mockito.verifyNoMoreInteractions(resizer);
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called twice</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerTwice() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).resizer(PROGRESSIVE).resizer(PROGRESSIVE).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scalingMode method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully created.</li>
     * </ol>
     */
    @Test
    public void scalingModeOnly() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).scalingMode(PROGRESSIVE_BILINEAR).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scalingMode method is called twice</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void scalingModeTwice() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).scalingMode(PROGRESSIVE_BILINEAR).scalingMode(PROGRESSIVE_BILINEAR).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called, then</li>
     * <li>The scalingMode method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerThenScalingMode() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).resizer(PROGRESSIVE).scalingMode(PROGRESSIVE_BILINEAR).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizerFactory method is called, then</li>
     * <li>The scalingMode method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerFactoryThenScalingMode() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).resizerFactory(DefaultResizerFactory.getInstance()).scalingMode(PROGRESSIVE_BILINEAR).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scalingMode method is called, then</li>
     * <li>The resizer method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void scalingModeThenResizer() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).scalingMode(PROGRESSIVE_BILINEAR).resizer(PROGRESSIVE).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scalingMode method is called, then</li>
     * <li>The resizerFactory method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An {@link IllegalStateException} is thrown</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void scalingModeThenResizerFactory() throws IOException {
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        Thumbnails.of(img).size(200, 200).scalingMode(PROGRESSIVE_BILINEAR).resizerFactory(DefaultResizerFactory.getInstance()).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The imageType method is not called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The image type of the resulting image is the same as the original
     * image</li>
     * </ol>
     */
    @Test
    public void imageTypeNotCalled() throws IOException {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_INDEXED);
        BufferedImage thumbnail = Thumbnails.of(img).size(200, 200).asBufferedImage();
        Assert.assertEquals(BufferedImage.TYPE_BYTE_INDEXED, thumbnail.getType());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The imageType method is called with the same type as the original
     * image</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The image type of the resulting image is the same as the original
     * image</li>
     * </ol>
     */
    @Test
    public void imageTypeCalledSameType() throws IOException {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage thumbnail = Thumbnails.of(img).size(200, 200).imageType(BufferedImage.TYPE_BYTE_GRAY).asBufferedImage();
        Assert.assertEquals(BufferedImage.TYPE_BYTE_GRAY, thumbnail.getType());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The imageType method is called with the different type as the
     * original image</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The image type of the resulting image is the the specified by the
     * imageType method</li>
     * </ol>
     */
    @Test
    public void imageTypeCalledDifferentType() throws IOException {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage thumbnail = Thumbnails.of(img).size(200, 200).imageType(BufferedImage.TYPE_BYTE_GRAY).asBufferedImage();
        Assert.assertEquals(BufferedImage.TYPE_BYTE_GRAY, thumbnail.getType());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The imageType method is called twice</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void imageTypeCalledTwice() throws IOException {
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_BYTE_GRAY);
        Thumbnails.of(img).size(200, 200).imageType(BufferedImage.TYPE_BYTE_GRAY).imageType(BufferedImage.TYPE_BYTE_GRAY).asBufferedImage();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Region) is called with valid parameters</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully produced</li>
     * <li>The specified range is used as the source</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_Region() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        Thumbnails.of(img).sourceRegion(new Region(new Coordinate(0, 0), new AbsoluteSize(50, 50))).size(50, 50).toFile(outFile);
        // then
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(thumbnail, img.getSubimage(0, 0, 50, 50)));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Region) is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An NullPointerException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_Region_Null() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(((Region) (null))).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Rectangle) is called with valid parameters</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully produced</li>
     * <li>The specified range is used as the source</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_Rectangle() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        Thumbnails.of(img).sourceRegion(new Rectangle(0, 0, 50, 50)).size(50, 50).toFile(outFile);
        // then
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(thumbnail, img.getSubimage(0, 0, 50, 50)));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Region) is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An NullPointerException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_Rectangle_Null() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(((Rectangle) (null))).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, Size) is called with valid parameters</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully produced</li>
     * <li>The specified range is used as the source</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionSize() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        Thumbnails.of(img).sourceRegion(new Coordinate(0, 0), new AbsoluteSize(50, 50)).size(50, 50).toFile(outFile);
        // then
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(thumbnail, img.getSubimage(0, 0, 50, 50)));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, Size) is called with Position as null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An NullPointerException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionSize_PositionNull() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(null, new AbsoluteSize(50, 50)).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, Size) is called with Size as null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An NullPointerException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionSize_SizeNull() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(new Coordinate(0, 0), null).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, int, int) is called with valid parameters</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully produced</li>
     * <li>The specified range is used as the source</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionIntInt() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        Thumbnails.of(img).sourceRegion(new Coordinate(0, 0), 50, 50).size(50, 50).toFile(outFile);
        // then
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(thumbnail, img.getSubimage(0, 0, 50, 50)));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, int, int) is called with Position as null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An NullPointerException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionIntInt_PositionNull() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(null, 50, 50).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (NullPointerException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, int, int) is called with width as non-positive</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionIntInt_WidthNonPositive() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(new Coordinate(0, 0), (-1), 50).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(Position, int, int) is called with height as non-positive</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_PositionIntInt_HeightNonPositive() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(new Coordinate(0, 0), 50, (-1)).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(int, int, int, int) is called with valid parameters</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail is successfully produced</li>
     * <li>The specified range is used as the source</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_IntIntIntInt() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        Thumbnails.of(img).sourceRegion(0, 0, 50, 50).size(50, 50).toFile(outFile);
        // then
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertTrue(BufferedImageComparer.isRGBSimilar(thumbnail, img.getSubimage(0, 0, 50, 50)));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(int, int, int, int) is called with width as non-positive</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_IntIntIntInt_WidthNonPositive() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(0, 0, (-1), 50).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>sourceRegion(int, int, int, int) is called with height as non-positive</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void sourceRegion_IntIntIntInt_HeightNonPositive() throws IOException {
        // given
        BufferedImage img = ImageIO.read(new File("src/test/resources/Thumbnailator/grid.png"));
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        // when
        try {
            Thumbnails.of(img).sourceRegion(0, 0, 50, (-1)).size(50, 50).toFile(outFile);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(float) is 0.0f</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The outputQuality is allowed</li>
     * <li>The thumbnail is successfully produced</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputQuality_float_ValidArg_ZeroZero() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(0.0F).toFile(outFile);
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(float) is 0.5f</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The outputQuality is allowed</li>
     * <li>The thumbnail is successfully produced</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputQuality_float_ValidArg_ZeroFive() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(0.5F).toFile(outFile);
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(float) is 1.0f</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The outputQuality is allowed</li>
     * <li>The thumbnail is successfully produced</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputQuality_float_ValidArg_OneZero() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(1.0F).toFile(outFile);
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(float) is negative</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputQuality_float_InvalidArg_Negative() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality((-0.01F));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(float) is greater than 1.0d</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputQuality_float_InvalidArg_OverOne() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(1.01F);
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(double) is 0.0d</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The outputQuality is allowed</li>
     * <li>The thumbnail is successfully produced</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputQuality_double_ValidArg_ZeroZero() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(0.0).toFile(outFile);
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(double) is 0.5d</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The outputQuality is allowed</li>
     * <li>The thumbnail is successfully produced</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputQuality_double_ValidArg_ZeroFive() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(0.5).toFile(outFile);
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(double) is 1.0d</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The outputQuality is allowed</li>
     * <li>The thumbnail is successfully produced</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputQuality_double_ValidArg_OneZero() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(1.0).toFile(outFile);
        BufferedImage thumbnail = ImageIO.read(outFile);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(double) is negative</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputQuality_double_InvalidArg_Negative() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality((-0.01));
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputQuality(double) is greater than 1.0d</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputQuality_double_InvalidArg_OverOne() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.jpg");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").outputQuality(1.01);
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name matches the file extension</li>
     * <li>format is same as the original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_SameAsOriginal_SameAsFileExtension() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("png").toFile(outFile);
        BufferedImage fromFileImage = ImageIO.read(outFile);
        String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile)).next().getFormatName();
        Assert.assertEquals("png", formatName);
        Assert.assertEquals(50, fromFileImage.getWidth());
        Assert.assertEquals(50, fromFileImage.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name matches the file extension</li>
     * <li>format is different from the original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_DiffersFromOriginal_SameAsFileExtension() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").toFile(outFile);
        BufferedImage fromFileImage = ImageIO.read(outFile);
        String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile)).next().getFormatName();
        Assert.assertEquals("JPEG", formatName);
        Assert.assertEquals(50, fromFileImage.getWidth());
        Assert.assertEquals(50, fromFileImage.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name matches the file extension</li>
     * <li>format is different from the original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_DiffersFromOriginal_SameAsFileExtension_Jpeg() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        outFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpeg").toFile(outFile);
        BufferedImage fromFileImage = ImageIO.read(outFile);
        String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile)).next().getFormatName();
        Assert.assertEquals("JPEG", formatName);
        Assert.assertEquals(50, fromFileImage.getWidth());
        Assert.assertEquals(50, fromFileImage.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name differs from the file extension</li>
     * <li>format is same as the original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * <li>The format extension is appended to the output filename.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_SameAsOriginal_DiffersFromFileExtension() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg");
        File actualOutFile = new File("src/test/resources/Thumbnailator/grid.tmp.jpg.png");
        actualOutFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("png").toFile(outFile);
        BufferedImage fromFileImage = ImageIO.read(actualOutFile);
        String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(actualOutFile)).next().getFormatName();
        Assert.assertEquals("png", formatName);
        Assert.assertEquals(50, fromFileImage.getWidth());
        Assert.assertEquals(50, fromFileImage.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name differs from the file extension</li>
     * <li>format differs from the original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * <li>The format extension is appended to the output filename.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_DiffersFromOriginal_DiffersFromFileExtension() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        File actualOutFile = new File("src/test/resources/Thumbnailator/grid.tmp.png.jpg");
        actualOutFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpg").toFile(outFile);
        BufferedImage fromFileImage = ImageIO.read(actualOutFile);
        String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(actualOutFile)).next().getFormatName();
        Assert.assertEquals("JPEG", formatName);
        Assert.assertEquals(50, fromFileImage.getWidth());
        Assert.assertEquals(50, fromFileImage.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name differs from the file extension</li>
     * <li>format differs from the original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * <li>The format extension is appended to the output filename.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_DiffersFromOriginal_DiffersFromFileExtension_Jpeg() throws IOException {
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        File outFile = new File("src/test/resources/Thumbnailator/grid.tmp.png");
        File actualOutFile = new File("src/test/resources/Thumbnailator/grid.tmp.png.jpeg");
        actualOutFile.deleteOnExit();
        Thumbnails.of(f).size(50, 50).outputFormat("jpeg").toFile(outFile);
        BufferedImage fromFileImage = ImageIO.read(actualOutFile);
        String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(actualOutFile)).next().getFormatName();
        Assert.assertEquals("JPEG", formatName);
        Assert.assertEquals(50, fromFileImage.getWidth());
        Assert.assertEquals(50, fromFileImage.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>multiple files</li>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>format name is same as file extension</li>
     * <li>format is same as original format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Multiple_SameAsOriginal_SameAsExtension_Both() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.png");
        File f2 = new File("src/test/resources/Thumbnailator/igrid.png");
        // when
        Thumbnails.of(f1, f2).size(50, 50).outputFormat("png").toFiles(PREFIX_DOT_THUMBNAIL);
        // then
        File outFile1 = new File("src/test/resources/Thumbnailator/thumbnail.grid.png");
        File outFile2 = new File("src/test/resources/Thumbnailator/thumbnail.igrid.png");
        outFile1.deleteOnExit();
        outFile2.deleteOnExit();
        BufferedImage fromFileImage1 = ImageIO.read(outFile1);
        BufferedImage fromFileImage2 = ImageIO.read(outFile2);
        String formatName1 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile1)).next().getFormatName();
        Assert.assertEquals("png", formatName1);
        String formatName2 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile2)).next().getFormatName();
        Assert.assertEquals("png", formatName2);
        Assert.assertEquals(50, fromFileImage1.getWidth());
        Assert.assertEquals(50, fromFileImage1.getHeight());
        Assert.assertEquals(50, fromFileImage2.getWidth());
        Assert.assertEquals(50, fromFileImage2.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>multiple files</li>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>1st file: file extension same as format specified</li>
     * <li>2nd file: file extension differs from format specified</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * <li>For the file with the different format and extension, the extension
     * of the specified format will be added to the file name.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Multiple_FirstExtensionSame_SecondExtensionDifferent() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.png");
        File f2 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1, f2).size(50, 50).outputFormat("png").toFiles(PREFIX_DOT_THUMBNAIL);
        // then
        File outFile1 = new File("src/test/resources/Thumbnailator/thumbnail.grid.png");
        File outFile2 = new File("src/test/resources/Thumbnailator/thumbnail.grid.jpg.png");
        outFile1.deleteOnExit();
        outFile2.deleteOnExit();
        BufferedImage fromFileImage1 = ImageIO.read(outFile1);
        BufferedImage fromFileImage2 = ImageIO.read(outFile2);
        String formatName1 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile1)).next().getFormatName();
        Assert.assertEquals("png", formatName1);
        String formatName2 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile2)).next().getFormatName();
        Assert.assertEquals("png", formatName2);
        Assert.assertEquals(50, fromFileImage1.getWidth());
        Assert.assertEquals(50, fromFileImage1.getHeight());
        Assert.assertEquals(50, fromFileImage2.getWidth());
        Assert.assertEquals(50, fromFileImage2.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>multiple files</li>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>1st file: file extension differs from format specified</li>
     * <li>2nd file: file extension same as format specified</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * <li>For the file with the different format and extension, the extension
     * of the specified format will be added to the file name.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Multiple_FirstExtensionDifferent_SecondExtensionSame() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        File f2 = new File("src/test/resources/Thumbnailator/grid.png");
        // when
        Thumbnails.of(f1, f2).size(50, 50).outputFormat("png").toFiles(PREFIX_DOT_THUMBNAIL);
        // then
        File outFile1 = new File("src/test/resources/Thumbnailator/thumbnail.grid.jpg.png");
        File outFile2 = new File("src/test/resources/Thumbnailator/thumbnail.grid.png");
        outFile1.deleteOnExit();
        outFile2.deleteOnExit();
        BufferedImage fromFileImage1 = ImageIO.read(outFile1);
        BufferedImage fromFileImage2 = ImageIO.read(outFile2);
        String formatName1 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile1)).next().getFormatName();
        Assert.assertEquals("png", formatName1);
        String formatName2 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile2)).next().getFormatName();
        Assert.assertEquals("png", formatName2);
        Assert.assertEquals(50, fromFileImage1.getWidth());
        Assert.assertEquals(50, fromFileImage1.getHeight());
        Assert.assertEquals(50, fromFileImage2.getWidth());
        Assert.assertEquals(50, fromFileImage2.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>multiple files</li>
     * <li>outputFormat</li>
     * <li>toFile(File)</li>
     * <li>1st file: file extension differs from format specified</li>
     * <li>2nd file: file extension differs from format specified</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The output format of the image is one that is specified.</li>
     * <li>For the file with the different format and extension, the extension
     * of the specified format will be added to the file name.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Multiple_FirstExtensionDifferent_SecondExtensionDifferent() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        File f2 = new File("src/test/resources/Thumbnailator/grid.bmp");
        // when
        Thumbnails.of(f1, f2).size(50, 50).outputFormat("png").toFiles(PREFIX_DOT_THUMBNAIL);
        // then
        File outFile1 = new File("src/test/resources/Thumbnailator/thumbnail.grid.jpg.png");
        File outFile2 = new File("src/test/resources/Thumbnailator/thumbnail.grid.bmp.png");
        outFile1.deleteOnExit();
        outFile2.deleteOnExit();
        BufferedImage fromFileImage1 = ImageIO.read(outFile1);
        BufferedImage fromFileImage2 = ImageIO.read(outFile2);
        String formatName1 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile1)).next().getFormatName();
        Assert.assertEquals("png", formatName1);
        String formatName2 = ImageIO.getImageReaders(ImageIO.createImageInputStream(outFile2)).next().getFormatName();
        Assert.assertEquals("png", formatName2);
        Assert.assertEquals(50, fromFileImage1.getWidth());
        Assert.assertEquals(50, fromFileImage1.getHeight());
        Assert.assertEquals(50, fromFileImage2.getWidth());
        Assert.assertEquals(50, fromFileImage2.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat with a supported format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>No exception is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_SupportedFormat() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat("png");
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat with a supported format</li>
     * <li>toFile(File)</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputFormat_UnsupportedFormat() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat("unsupported");
        // then
        // expect an IllagelArgumentException.
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat specified</li>
     * <li>outputFormatType is supported</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>No exception is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Checks_FormatSpecified_TypeSupported() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat("JPEG").outputFormatType("JPEG");
        // then
        // no exception occurs
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat specified</li>
     * <li>outputFormatType is specified, but the outputFormat does not
     * support compression.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputFormat_Checks_FormatSpecified_FormatDoesNotSupportCompression() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat("PNG").outputFormatType("JPEG");
        // then
        // expect an IllagelArgumentException.
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat specified</li>
     * <li>outputFormatType is unsupported</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputFormat_Checks_FormatSpecified_TypeUnsupported() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat("JPEG").outputFormatType("foo");
        // then
        // expect an IllagelArgumentException.
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat specified</li>
     * <li>outputFormatType is default for the output format</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>No exception is thrown</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Checks_FormatSpecified_TypeDefault() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat("JPEG").outputFormatType(DEFAULT_FORMAT_TYPE);
        // then
        // no exception occurs
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat is the format of the original image</li>
     * <li>outputFormatType is specified</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test(expected = IllegalArgumentException.class)
    public void outputFormat_Checks_FormatSpecifiedAsOriginal_TypeSpecified() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat(ORIGINAL_FORMAT).outputFormatType("JPEG");
        // then
        // expect an IllagelArgumentException.
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>outputFormat is the format of the original image</li>
     * <li>outputFormatType is specified</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>No exception is thrown.</li>
     * </ol>
     *
     * @throws IOException
     * 		
     */
    @Test
    public void outputFormat_Checks_FormatSpecifiedAsOriginal_TypeIsDefaultForFormat() throws IOException {
        // given
        File f1 = new File("src/test/resources/Thumbnailator/grid.jpg");
        // when
        Thumbnails.of(f1).size(50, 50).outputFormat(ORIGINAL_FORMAT).outputFormatType(DEFAULT_FORMAT_TYPE);
        // then
        // no exception is thrown.
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A NullPointerException is thrown</li>
     * </ol>
     */
    @Test(expected = NullPointerException.class)
    public void resizer_Null() {
        try {
            // given
            // when
            Thumbnails.of("non-existent-file").size(200, 200).resizer(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Resizer is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The alphaInterpolation method is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A NullPointerException is thrown</li>
     * </ol>
     */
    @Test(expected = NullPointerException.class)
    public void alphaInterpolation_Null() {
        try {
            // given
            // when
            Thumbnails.of("non-existent-file").size(200, 200).alphaInterpolation(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Alpha interpolation is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The dithering method is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A NullPointerException is thrown</li>
     * </ol>
     */
    @Test(expected = NullPointerException.class)
    public void dithering_Null() {
        try {
            // given
            // when
            Thumbnails.of("non-existent-file").size(200, 200).dithering(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Dithering is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The antialiasing method is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A NullPointerException is thrown</li>
     * </ol>
     */
    @Test(expected = NullPointerException.class)
    public void antialiasing_Null() {
        try {
            // given
            // when
            Thumbnails.of("non-existent-file").size(200, 200).antialiasing(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Antialiasing is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The rendering method is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A NullPointerException is thrown</li>
     * </ol>
     */
    @Test(expected = NullPointerException.class)
    public void rendering_Null() {
        try {
            // given
            // when
            Thumbnails.of("non-existent-file").size(200, 200).rendering(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Rendering is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The scalingMode method is called with null</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A NullPointerException is thrown</li>
     * </ol>
     */
    @Test(expected = NullPointerException.class)
    public void scalingMode_Null() {
        try {
            // given
            // when
            Thumbnails.of("non-existent-file").size(200, 200).scalingMode(null);
        } catch (NullPointerException e) {
            // then
            Assert.assertEquals("Scaling mode is null.", e.getMessage());
            throw e;
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the resizerFactory method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerThenResizerFactory() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).resizer(PROGRESSIVE).resizerFactory(DefaultResizerFactory.getInstance()).asBufferedImage();
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizerFactory method is called.</li>
     * <li>Then, the resizer method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerFactoryThenResizer() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).resizerFactory(DefaultResizerFactory.getInstance()).resizer(PROGRESSIVE).asBufferedImage();
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the alphaInterpolation method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test
    public void resizerThenAlphainterpolation() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(50, 50).resizer(PROGRESSIVE).alphaInterpolation(SPEED).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the dithering method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test
    public void resizerThenDithering() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(50, 50).resizer(PROGRESSIVE).dithering(DEFAULT).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the antialiasing method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test
    public void resizerThenAntialiasing() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(50, 50).resizer(PROGRESSIVE).antialiasing(Antialiasing.DEFAULT).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the rendering method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test
    public void resizerThenRendering() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(50, 50).resizer(PROGRESSIVE).rendering(Rendering.DEFAULT).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the alphaInterpolation method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerFactoryThenAlphainterpolation() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).resizerFactory(DefaultResizerFactory.getInstance()).alphaInterpolation(SPEED).asBufferedImage();
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the dithering method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerFactoryThenDithering() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).resizerFactory(DefaultResizerFactory.getInstance()).dithering(DEFAULT).asBufferedImage();
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the antialiasing method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerFactoryThenAntialiasing() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).resizerFactory(DefaultResizerFactory.getInstance()).antialiasing(Antialiasing.DEFAULT).asBufferedImage();
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizer method is called.</li>
     * <li>Then, the rendering method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail is created successfully.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void resizerFactoryThenRendering() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        Thumbnails.of(img).size(50, 50).resizerFactory(DefaultResizerFactory.getInstance()).rendering(Rendering.DEFAULT).asBufferedImage();
        // then
        Assert.fail();
    }

    @Test
    public void renameGivenThumbnailParameter() throws IOException {
        // given
        Rename rename = Mockito.mock(Rename.class);
        Mockito.when(rename.apply(ArgumentMatchers.anyString(), ArgumentMatchers.any(ThumbnailParameter.class))).thenReturn("thumbnail.grid.png");
        File f = new File("src/test/resources/Thumbnailator/grid.png");
        // when
        Thumbnails.of(f).size(50, 50).asFiles(rename);
        // then
        ArgumentCaptor<ThumbnailParameter> ac = ArgumentCaptor.forClass(ThumbnailParameter.class);
        Mockito.verify(rename).apply(ArgumentMatchers.eq(f.getName()), ac.capture());
        Assert.assertEquals(new Dimension(50, 50), ac.getValue().getSize());
        // clean up
        new File("src/test/resources/Thumbnailator/thumbnail.grid.png").deleteOnExit();
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void width() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).width(50).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(25, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void height() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).height(50).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * <li>The height method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size is constrained by the height</li>
     * <li>The image is constrained to the smallest dimension</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void widthAndHeight() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).width(50).height(50).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(25, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called</li>
     * <li>The width method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size is constrained by the height</li>
     * <li>The image is constrained to the smallest dimension</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void heightAndWidth() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).height(50).width(50).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(25, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * <li>The keepAspectRatio is called with false</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void widthNotPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).keepAspectRatio(false).width(50).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called</li>
     * <li>The keepAspectRatio method is called with false</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void heightNotPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).height(50).keepAspectRatio(false).asBufferedImage();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * <li>The height method is called</li>
     * <li>The keepAspectRatio is called with false</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void widthAndHeightNotPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).width(50).height(50).keepAspectRatio(false).asBufferedImage();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * <li>The height method is called</li>
     * <li>The keepAspectRatio is called with false</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void heightAndWidthNotPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).height(50).width(50).keepAspectRatio(false).asBufferedImage();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The keepAspectRatio is called with false, first</li>
     * <li>The width method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void widthNotPreservingTheAspectRatioFirst() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).keepAspectRatio(false).width(50).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The keepAspectRatio is called with false, first</li>
     * <li>The height method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void heightNotPreservingTheAspectRatioFirst() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).keepAspectRatio(false).height(50).asBufferedImage();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The keepAspectRatio is called with false, first</li>
     * <li>The width method is called</li>
     * <li>The height method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void widthAndHeightNotPreservingTheAspectRatioFirst() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).keepAspectRatio(false).width(50).height(50).asBufferedImage();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The keepAspectRatio is called with false, first</li>
     * <li>The width method is called</li>
     * <li>The height method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown.</li>
     * </ol>
     */
    @Test
    public void heightAndWidthNotPreservingTheAspectRatioFirst() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        try {
            // when
            Thumbnails.of(img).keepAspectRatio(false).height(50).width(50).asBufferedImage();
        } catch (IllegalStateException e) {
            // then
        }
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * <li>The keepAspectRatio is called with true</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void widthAndPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).width(50).keepAspectRatio(true).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(25, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called</li>
     * <li>The keepAspectRatio is called with true</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void heightAndPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).height(50).keepAspectRatio(true).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The width method is called</li>
     * <li>The height method is called</li>
     * <li>The keepAspectRatio is called with true</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size is constrained by the height</li>
     * <li>The image is constrained to the smallest dimension</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void widthAndHeightAndPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).width(50).height(50).keepAspectRatio(true).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(25, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The height method is called</li>
     * <li>The width method is called</li>
     * <li>The keepAspectRatio is called with true</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The thumbnail size is constrained by the width</li>
     * <li>The thumbnail size is constrained by the height</li>
     * <li>The image is constrained to the smallest dimension</li>
     * <li>The thumbnail size maintains the aspect ratio of the original</li>
     * </ol>
     */
    @Test
    public void heightAndWidthAndPreservingTheAspectRatio() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 100).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).height(50).width(50).keepAspectRatio(true).asBufferedImage();
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(25, thumbnail.getHeight());
    }

    /**
     * Test for the {@link Thumbnails.Builder} class where,
     * <ol>
     * <li>The resizerFactory method is called.</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The specified {@link ResizerFactory} is called when resizing.</li>
     * </ol>
     */
    @Test
    public void resizerFactoryCalledOnResize() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        ResizerFactory resizerFactory = Mockito.mock(ResizerFactory.class);
        Mockito.when(resizerFactory.getResizer(ArgumentMatchers.any(Dimension.class), ArgumentMatchers.any(Dimension.class))).thenReturn(NULL);
        // when
        Thumbnails.of(img).resizerFactory(resizerFactory).size(50, 50).asBufferedImage();
        // then
        Mockito.verify(resizerFactory).getResizer(new Dimension(200, 200), new Dimension(50, 50));
    }

    @Test
    public void cropWithSizeBefore() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 50).crop(CENTER).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void cropWithSizeAfter() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).crop(CENTER).size(100, 50).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void cropWithAspectRatioTrueBefore() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 50).keepAspectRatio(true).crop(CENTER).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void cropWithAspectRatioTrueAfter() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).crop(CENTER).size(100, 50).keepAspectRatio(true).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void cropWithAspectRatioFalseBefore() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 50).keepAspectRatio(false).crop(CENTER).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void cropWithAspectRatioFalseAfter() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).crop(CENTER).size(100, 50).keepAspectRatio(false).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
    }

    @Test
    public void cropWithScaleBeforeShouldFail() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            // when
            Thumbnails.of(img).scale(0.5).crop(CENTER).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
        }
    }

    @Test
    public void cropWithScaleAfterShouldFail() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            // when
            Thumbnails.of(img).crop(CENTER).scale(0.5).asBufferedImage();
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
        }
    }

    @Test
    public void cropTwice() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        try {
            // when
            Thumbnails.of(img).crop(CENTER).crop(CENTER);
            Assert.fail();
        } catch (IllegalStateException e) {
            // then
        }
    }

    @Test
    public void cropCenterThenWatermarkCenter() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkTopLeft() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.TOP_LEFT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkTopRight() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.TOP_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkBottomLeft() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.BOTTOM_LEFT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkBottomRight() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkCenterNonSquareOriginalWide() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(300, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 300, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkTopLeftNonSquareOriginalWide() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(300, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 300, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.TOP_LEFT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkTopRightNonSquareOriginalWide() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(300, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 300, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.TOP_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkBottomLeftNonSquareOriginalWide() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(300, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 300, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.BOTTOM_LEFT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkBottomRightNonSquareOriginalWide() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(300, 200).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 300, 200);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkCenterNonSquareOriginalTall() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 300).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 300);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkTopLeftNonSquareOriginalTall() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 300).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 300);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.TOP_LEFT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkTopRightNonSquareOriginalTall() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 300).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 300);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.TOP_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkBottomLeftNonSquareOriginalTall() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 300).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 300);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.BOTTOM_LEFT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void cropCenterThenWatermarkBottomRightNonSquareOriginalTall() throws IOException {
        // given
        BufferedImage img = new BufferedImageBuilder(200, 300).build();
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g;
        g = img.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, 200, 300);
        g.dispose();
        g = watermark.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 25, 25);
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(img).size(100, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 0));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(99, 50));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(0, 99));
        Assert.assertEquals(Color.black.getRGB(), thumbnail.getRGB(50, 99));
        Assert.assertEquals(Color.white.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation1File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_1.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation2File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_2.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation3File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_3.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation4File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_4.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation5File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_5.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation6File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_6.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation7File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_7.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation8File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_8.jpg";
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation1InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_1.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation2InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_2.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation3InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_3.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation4InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_4.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation5InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_5.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation6InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_6.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation7InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_7.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkExifOrientation8InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_8.jpg");
        BufferedImage watermark = new BufferedImageBuilder(25, 25).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(100, 100).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(99, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation1File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_1.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation2File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_2.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation3File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_3.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation4File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_4.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation5File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_5.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation6File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_6.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation7File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_7.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation8File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_8.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation1InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_1.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation2InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_2.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation3InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_3.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation4InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_4.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation5InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_5.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation6InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_6.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation7InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_7.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropExifOrientation8InputStream() throws IOException {
        // given
        InputStream imgIS = new FileInputStream("src/test/resources/Exif/source_8.jpg");
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgIS).size(50, 100).crop(CENTER).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation1File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_1.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation2File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_2.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation3File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_3.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation4File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_4.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation5File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_5.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation6File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_6.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation7File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_7.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkAndCropNonCenterExifOrientation8File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_8.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(Positions.BOTTOM_RIGHT, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(49, 99));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation1File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_1.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation2File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_2.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation3File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_3.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation4File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_4.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation5File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_5.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation6File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_6.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation7File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_7.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }

    @Test
    public void watermarkCenterAndCropNonCenterExifOrientation8File() throws IOException {
        // given
        String imgPath = "src/test/resources/Exif/source_8.jpg";
        BufferedImage watermark = new BufferedImageBuilder(10, 10).build();
        Graphics g = watermark.getGraphics();
        g.setColor(Color.blue);
        g.fillRect(0, 0, watermark.getWidth(), watermark.getHeight());
        g.dispose();
        // when
        BufferedImage thumbnail = Thumbnails.of(imgPath).size(50, 100).crop(Positions.TOP_LEFT).watermark(CENTER, watermark, 1.0F).asBufferedImage();
        // then
        Assert.assertEquals(Color.blue.getRGB(), thumbnail.getRGB(25, 50));
    }
}

