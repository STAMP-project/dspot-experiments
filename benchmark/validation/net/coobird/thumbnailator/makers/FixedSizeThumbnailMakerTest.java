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
 * {@link FixedSizeThumbnailMaker} class.
 *
 * @author coobird
 */
public class FixedSizeThumbnailMakerTest {
    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the no-args constructor</li>
     * <li>the keepAspectRatio method is not called</li>
     * <li>the fitWithinDimensions method is not called</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException occurs.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void uninitializedWithNoArgConstructor() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        new FixedSizeThumbnailMaker().make(img);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>the keepAspectRatio method is not called</li>
     * <li>the fitWithinDimensions method is not called</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException occurs.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void uninitializedWithTwoArgConstructor() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        new FixedSizeThumbnailMaker(100, 100).make(img);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the no argument constructor</li>
     * <li>The keepAspectRatio method is called with true</li>
     * <li>the fitWithinDimensions method is not called</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException occurs, due to not specifying the
     * dimensions of the thumbnail.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void unintializedNoArgConstructorAndAspectRatioSpecified() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        new FixedSizeThumbnailMaker().keepAspectRatio(true).make(img);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The keepAspectRatio method is called with true</li>
     * <li>the fitWithinDimensions method is not called</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException occurs, due to not specifying the
     * dimensions of the thumbnail.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void uninitializedTwoArgConstructorAndAspectRatioSpecified() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        new FixedSizeThumbnailMaker(100, 100).keepAspectRatio(true).make(img);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The keepAspectRatio method is called with true</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created with the specified size, and the
     * imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void twoArgConstructorAndAspectRatioAndFitWithinDimensionsSpecified() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 100).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void threeArgumentConstructorThenFitWithinDimenions() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 100, true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void fourArgumentConstructor() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 100, true, true).make(img);
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void keepAspectRatioFalseAndFitWithinDimensionsTrueAllowed() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100).keepAspectRatio(false).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void keepAspectRatioFalseAndFitWithinDimensionsFalseAllowed() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100).keepAspectRatio(false).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The keepAspectRatio method is called with true</li>
     * <li>The vertical dimension is smaller</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created with the size based on the
     * vertical dimensions, keeping the aspect ratio of the original
     * and the imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void keepAspectRatioWithOffRatioTargetSizeForVertical() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The keepAspectRatio method is called with true</li>
     * <li>The horizontal dimension is smaller</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created with the size based on the
     * horizontal dimensions, keeping the aspect ratio of the original
     * and the imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void keepAspectRatioWithOffRatioTargetSizeForHorizontal() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The keepAspectRatio method is called with false</li>
     * <li>The vertical dimension is smaller</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created with the size that matches the
     * specified dimensions, and the imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void noKeepAspectRatioWithOffRatioTargetSizeForVertical() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50).keepAspectRatio(false).fitWithinDimensions(true).make(img);
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The keepAspectRatio method is called with false</li>
     * <li>The horizontal dimension is smaller</li>
     * <li>And finally the make method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A thumbnail image is created with the size that matches the
     * specified dimensions, and the imageType is the default type.</li>
     * </ol>
     */
    @Test
    public void noKeepAspectRatioWithOffRatioTargetSizeForHorizontal() {
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100).keepAspectRatio(false).fitWithinDimensions(true).make(img);
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void keepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForVertical() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50).keepAspectRatio(true).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void keepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForHorizontal() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100).keepAspectRatio(true).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void noKeepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForVertical() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(100, 50).keepAspectRatio(false).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(100, thumbnail.getWidth());
        Assert.assertEquals(50, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    @Test
    public void noKeepAspectRatioAndNoFitWithinWithOffRatioTargetSizeForHorizontal() {
        // given
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(50, 100).keepAspectRatio(false).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(50, thumbnail.getWidth());
        Assert.assertEquals(100, thumbnail.getHeight());
        Assert.assertEquals(BufferedImage.TYPE_INT_ARGB, thumbnail.getType());
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the two argument constructor</li>
     * <li>The size method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown because the size has already
     * been set.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void twoArgConstructorThenSize() {
        new FixedSizeThumbnailMaker(50, 100).size(50, 100);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the three argument constructor</li>
     * <li>The aspectRatio method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown because the aspectRatio has
     * already been set.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void threeArgConstructorThenKeepAspectRatio() {
        new FixedSizeThumbnailMaker(50, 100, true).keepAspectRatio(true);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the three argument constructor</li>
     * <li>The size method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown because the size has
     * already been set.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void threeArgConstructorThenSize() {
        new FixedSizeThumbnailMaker(50, 100, true).size(100, 100);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the four argument constructor</li>
     * <li>The size method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown because the size has
     * already been set.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void fourArgConstructorThenSize() {
        new FixedSizeThumbnailMaker(50, 100, true, true).size(100, 100);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the four argument constructor</li>
     * <li>The size method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown because the keepAspectRatio has
     * already been set.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void fourArgConstructorThenKeepAspectRatio() {
        new FixedSizeThumbnailMaker(50, 100, true, true).keepAspectRatio(true);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
     * <ol>
     * <li>It is initialized with the four argument constructor</li>
     * <li>The size method is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException is thrown because the fitWithinDimensions
     * has already been set.</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void fourArgConstructorThenFitWithinDimensions() {
        new FixedSizeThumbnailMaker(50, 100, true, true).fitWithinDimensions(true);
        Assert.fail();
    }

    /**
     * Test for the {@link FixedSizeThumbnailMaker} class where,
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
        BufferedImage img = FixedSizeThumbnailMakerTest.makeTestImage200x200();
        Resizer spyResizer = Mockito.spy(new ProgressiveBilinearResizer());
        ResizerFactory resizerFactory = Mockito.mock(ResizerFactory.class);
        Mockito.when(resizerFactory.getResizer(ArgumentMatchers.any(Dimension.class), ArgumentMatchers.any(Dimension.class))).thenReturn(spyResizer);
        // when
        new FixedSizeThumbnailMaker(100, 100).keepAspectRatio(true).fitWithinDimensions(true).resizerFactory(resizerFactory).make(img);
        // then
        Mockito.verify(resizerFactory, Mockito.atLeastOnce()).getResizer(new Dimension(200, 200), new Dimension(100, 100));
        Mockito.verify(spyResizer).resize(ArgumentMatchers.eq(img), ArgumentMatchers.any(BufferedImage.class));
    }

    @Test
    public void heightZeroIfTruncatedButOneIfRounded_FitWithinTrue() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 6).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(1, thumbnail.getHeight());
    }

    @Test
    public void heightZeroIfTruncated_FitWithinTrue() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 4).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(1, thumbnail.getHeight());
    }

    @Test
    public void widthZeroIfTruncatedButOneIfRounded_FitWithinTrue() {
        // given
        BufferedImage img = new BufferedImageBuilder(6, 100).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(1, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void widthZeroIfTruncated_FitWithinTrue() {
        // given
        BufferedImage img = new BufferedImageBuilder(4, 100).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(1, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void sizeGivenZeroForWidthViaConstructor() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker(0, 10);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenZeroForWidthViaMethod() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker().size(0, 10);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenZeroForHeightViaConstructor() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker(10, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenZeroForHeightViaMethod() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker().size(10, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenZeroForWidthAndHeightViaConstructor() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker(0, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenZeroForWidthAndHeightViaMethod() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker().size(0, 0);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenNegativeForWidthViaConstructor() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker((-1), 10);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenNegativeForWidthViaMethod() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker().size((-1), 10);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenNegativeForHeightViaConstructor() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker(10, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenNegativeForHeightViaMethod() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker().size(10, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenNegativeForWidthAndHeightViaConstructor() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker((-1), (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void sizeGivenNegativeForWidthAndHeightViaMethod() {
        // given
        try {
            // when
            new FixedSizeThumbnailMaker().size((-1), (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // then
        }
    }

    @Test
    public void widthBeingRounded_FitWithinTrue() {
        // given
        BufferedImage img = new BufferedImageBuilder(99, 100).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void widthBeingRounded_FitWithinFalse() {
        // given
        BufferedImage img = new BufferedImageBuilder(99, 100).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void heightBeingRounded_FitWithinTrue() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 99).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(true).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }

    @Test
    public void heightBeingRounded_FitWithinFalse() {
        // given
        BufferedImage img = new BufferedImageBuilder(100, 99).build();
        // when
        BufferedImage thumbnail = new FixedSizeThumbnailMaker(10, 10).keepAspectRatio(true).fitWithinDimensions(false).make(img);
        // then
        Assert.assertEquals(10, thumbnail.getWidth());
        Assert.assertEquals(10, thumbnail.getHeight());
    }
}

