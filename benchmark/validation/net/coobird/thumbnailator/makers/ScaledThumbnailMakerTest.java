package net.coobird.thumbnailator.makers;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import net.coobird.thumbnailator.resizers.ProgressiveBilinearResizer;
import net.coobird.thumbnailator.resizers.Resizer;
import net.coobird.thumbnailator.resizers.ResizerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * A class which tests the behavior of the
 * {@link ScaledThumbnailMaker} class.
 *
 * @author coobird
 */
public class ScaledThumbnailMakerTest {
    /**
     * Test for the {@link ScaledThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the no-args constructor</li>
     * <li>The scale method is not called</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException occurs.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void uninitializedWithNoArgConstructor() {
        // given
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        // when
        new ScaledThumbnailMaker().make(img);
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the one argument constructor</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created by reducing the size by the
     * specified scaling factor</li>
     * <li>The imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void makeWithOneArgConstructor() {
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.5).make(img);
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the one argument constructor</li>
     * <li>The scale method is called.</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown, due to the scaling factor
     * being specified twice.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void makeWithOneArgConstructorWithScaleOneArg() {
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        new ScaledThumbnailMaker(0.5).scale(0.5).make(img);
    }

    @Test(expected = IllegalStateException.class)
    public void makeWithOneArgConstructorWithScaleTwoArg() {
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        new ScaledThumbnailMaker(0.5).scale(0.5, 0.5).make(img);
    }

    @Test
    public void makeWithTwoArgConstructor() {
        // given
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.6, 0.4).make(img);
        // then
        Assert.assertEquals(120, thumbnail.getWidth());
        Assert.assertEquals(80, thumbnail.getHeight());
    }

    @Test(expected = IllegalStateException.class)
    public void makeWithTwoArgConstructorWithScaleOneArg() {
        // given
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        // when
        new ScaledThumbnailMaker(0.6, 0.4).scale(0.5).make(img);
        // then
        Assert.fail();
    }

    @Test(expected = IllegalStateException.class)
    public void makeWithTwoArgConstructorWithScaleTwoArg() {
        // given
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        // when
        new ScaledThumbnailMaker(0.6, 0.4).scale(0.5, 0.5).make(img);
        // then
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the no argument constructor</li>
     * <li>The scale method is called.</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created by reducing the size by the
     * specified scaling factor</li>
     * <li>The imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void makeWithNoArgConstructorAndScaleOneArg() {
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new ScaledThumbnailMaker().scale(0.5).make(img);
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void makeWithNoArgConstructorAndScaleTwoArg() {
        // given
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker().scale(0.6, 0.4).make(img);
        // then
        Assert.assertEquals(120, thumbnail.getWidth());
        Assert.assertEquals(80, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link ScaledThumbnailMaker} class where,
     * <ol>
     * <li>An thumbnail is to be made</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>The ResizerFactory is used to obtain a Resizer.</li>
     * </ol>
     */
    @Test
    public void verifyResizerFactoryBeingCalled() {
        // given
        BufferedImage img = ScaledThumbnailMakerTest.makeTestImage200x200();
        Resizer spyResizer = Mockito.spy(new ProgressiveBilinearResizer());
        ResizerFactory resizerFactory = Mockito.mock(ResizerFactory.class);
        Mockito.when(resizerFactory.getResizer(ArgumentMatchers.any(Dimension.class), ArgumentMatchers.any(Dimension.class))).thenReturn(spyResizer);
        // when
        new ScaledThumbnailMaker(0.5).resizerFactory(resizerFactory).make(img);
        // then
        Mockito.verify(resizerFactory, Mockito.atLeastOnce()).getResizer(new Dimension(200, 200), new Dimension(100, 100));
        Mockito.verify(spyResizer).resize(ArgumentMatchers.eq(img), ArgumentMatchers.any(BufferedImage.class));
    }

    @Test
    public void scaleIsZeroThroughOneArgConstructor() {
        // given
        try {
            // when
            new ScaledThumbnailMaker(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsZeroThroughOneArgScaleMethod() {
        // given
        try {
            // when
            new ScaledThumbnailMaker().scale(0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsZeroThroughTwoArgConstructor() {
        // given
        try {
            // when
            new ScaledThumbnailMaker(0, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsZeroThroughTwoArgScaleMethod() {
        // given
        try {
            // when
            new ScaledThumbnailMaker().scale(0, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsNegativeThroughOneArgConstructor() {
        // given
        try {
            // when
            new ScaledThumbnailMaker((-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsNegativeThroughOneArgScaleMethod() {
        // given
        try {
            // when
            new ScaledThumbnailMaker().scale((-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsNegativeThroughTwoArgConstructor() {
        // given
        try {
            // when
            new ScaledThumbnailMaker(1, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void scaleIsNegativeThroughTwoArgScaleMethod() {
        // given
        try {
            // when
            new ScaledThumbnailMaker().scale(1, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void isRoundingWidthRatherThanTruncate_scaleOneArg() {
        // given
        BufferedImage img = new BufferedImageBuilder(99, 100).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void isRoundingHeightRatherThanTruncate_scaleOneArg() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 99).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void isRoundingWidthRatherThanTruncate_scaleTwoArg() {
        // given
        BufferedImage img = new BufferedImageBuilder(99, 100).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1, 0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void isRoundingHeightRatherThanTruncate_scaleTwoArg() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 99).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1, 0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void widthBecomesZeroIfTruncated() {
        // given
        BufferedImage img = new BufferedImageBuilder(9, 100).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(1, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void widthBecomesZeroIfTruncatedButIsOneIfRounded() {
        // given
        BufferedImage img = new BufferedImageBuilder(10, 100).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(1, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void widthBecomesZeroIfTruncatedAndIsZeroIfRounded() {
        // given
        BufferedImage img = new BufferedImageBuilder(1, 100).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(1, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void heightBecomesZeroIfTruncated() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 9).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(1, thumbnail.getHeight());
    }

    @Test
    public void heightBecomesZeroIfTruncatedButIsOneIfRounded() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 10).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(1, thumbnail.getHeight());
    }

    @Test
    public void heightBecomesZeroIfTruncatedAndIsZeroIfRounded() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 1).build();
        // when
        BufferedImage thumbnail = new ScaledThumbnailMaker(0.1).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(1, thumbnail.getHeight());
    }
}

