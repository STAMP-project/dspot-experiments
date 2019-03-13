package net.coobird.thumbnailator.builders;


import Resizers.BICUBIC;
import Resizers.PROGRESSIVE;
import ThumbnailParameter.DEFAULT_FORMAT_TYPE;
import ThumbnailParameter.DEFAULT_IMAGE_TYPE;
import ThumbnailParameter.ORIGINAL_FORMAT;
import java.awt.Dimension;
import java.util.Collections;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.resizers.Resizer;
import net.coobird.thumbnailator.resizers.ResizerFactory;
import org.junit.Assert;
import org.junit.Test;


public class ThumbnailParameterBuilderTest {
    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>No methods on the builder is called</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalStateException occurs</li>
     * </ol>
     */
    @Test(expected = IllegalStateException.class)
    public void build_NothingSet() {
        new ThumbnailParameterBuilder().build();
        Assert.fail();
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Only the size(int,int) is set</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A ThumbnailParameter with default values is built.</li>
     * </ol>
     */
    @Test
    public void build_OnlySize_IntInt() {
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(100, 100).build();
        Assert.assertEquals(new Dimension(100, 100), param.getSize());
        Assert.assertTrue(Double.isNaN(param.getHeightScalingFactor()));
        Assert.assertTrue(Double.isNaN(param.getWidthScalingFactor()));
        Assert.assertEquals(ORIGINAL_FORMAT, param.getOutputFormat());
        Assert.assertEquals(DEFAULT_FORMAT_TYPE, param.getOutputFormatType());
        Assert.assertTrue(Float.isNaN(param.getOutputQuality()));
        Assert.assertEquals(PROGRESSIVE, param.getResizer());
        Assert.assertEquals(DEFAULT_IMAGE_TYPE, param.getType());
        Assert.assertEquals(Collections.emptyList(), param.getImageFilters());
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Only the size(Dimension) is set</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A ThumbnailParameter with default values is built.</li>
     * </ol>
     */
    @Test
    public void build_OnlySize_Dimension() {
        ThumbnailParameter param = new ThumbnailParameterBuilder().size(new Dimension(100, 100)).build();
        Assert.assertEquals(new Dimension(100, 100), param.getSize());
        Assert.assertTrue(Double.isNaN(param.getWidthScalingFactor()));
        Assert.assertTrue(Double.isNaN(param.getHeightScalingFactor()));
        Assert.assertEquals(ORIGINAL_FORMAT, param.getOutputFormat());
        Assert.assertEquals(DEFAULT_FORMAT_TYPE, param.getOutputFormatType());
        Assert.assertTrue(Float.isNaN(param.getOutputQuality()));
        Assert.assertEquals(PROGRESSIVE, param.getResizer());
        Assert.assertEquals(DEFAULT_IMAGE_TYPE, param.getType());
        Assert.assertEquals(Collections.emptyList(), param.getImageFilters());
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Only the scale(double) is set</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A ThumbnailParameter with default values is built.</li>
     * </ol>
     */
    @Test
    public void build_OnlyScale() {
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.5).build();
        Assert.assertEquals(null, param.getSize());
        Assert.assertTrue(((Double.compare(0.5, param.getWidthScalingFactor())) == 0));
        Assert.assertTrue(((Double.compare(0.5, param.getHeightScalingFactor())) == 0));
        Assert.assertEquals(ORIGINAL_FORMAT, param.getOutputFormat());
        Assert.assertEquals(DEFAULT_FORMAT_TYPE, param.getOutputFormatType());
        Assert.assertTrue(Float.isNaN(param.getOutputQuality()));
        Assert.assertEquals(PROGRESSIVE, param.getResizer());
        Assert.assertEquals(DEFAULT_IMAGE_TYPE, param.getType());
        Assert.assertEquals(Collections.emptyList(), param.getImageFilters());
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Only the scale(double) is set with Double.NaN</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     */
    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScale_NaN() {
        new ThumbnailParameterBuilder().scale(Double.NaN).build();
        Assert.fail();
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Only the scale(double) is set with Double.POSITIVE_INFINITY</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     */
    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScale_PositiveInfinity() {
        new ThumbnailParameterBuilder().scale(Double.POSITIVE_INFINITY).build();
        Assert.fail();
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Only the scale(double) is set with Double.NEGATIVE_INFINITY</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>An IllegalArgumentException is thrown.</li>
     * </ol>
     */
    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScale_NegativeInfinity() {
        new ThumbnailParameterBuilder().scale(Double.NEGATIVE_INFINITY).build();
        Assert.fail();
    }

    @Test
    public void build_OnlyScaleTwoArg() {
        // given, when
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.6, 0.4).build();
        // then
        Assert.assertEquals(null, param.getSize());
        Assert.assertTrue(((Double.compare(0.6, param.getWidthScalingFactor())) == 0));
        Assert.assertTrue(((Double.compare(0.4, param.getHeightScalingFactor())) == 0));
        Assert.assertEquals(ORIGINAL_FORMAT, param.getOutputFormat());
        Assert.assertEquals(DEFAULT_FORMAT_TYPE, param.getOutputFormatType());
        Assert.assertTrue(Float.isNaN(param.getOutputQuality()));
        Assert.assertEquals(PROGRESSIVE, param.getResizer());
        Assert.assertEquals(DEFAULT_IMAGE_TYPE, param.getType());
        Assert.assertEquals(Collections.emptyList(), param.getImageFilters());
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_NaN_Valid() {
        new ThumbnailParameterBuilder().scale(Double.NaN, 0.4).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_Valid_NaN() {
        new ThumbnailParameterBuilder().scale(0.6, Double.NaN).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_NaN_NaN() {
        new ThumbnailParameterBuilder().scale(Double.NaN, Double.NaN).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_PositiveInfinity_Valid() {
        new ThumbnailParameterBuilder().scale(Double.POSITIVE_INFINITY, 0.4).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_Valid_PositiveInfinity() {
        new ThumbnailParameterBuilder().scale(0.6, Double.POSITIVE_INFINITY).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_PositiveInfinity_PositiveInfinity() {
        new ThumbnailParameterBuilder().scale(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_NegativeInfinity_Valid() {
        new ThumbnailParameterBuilder().scale(Double.NEGATIVE_INFINITY, 0.4).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_Valid_NegativeInfinity() {
        new ThumbnailParameterBuilder().scale(0.6, Double.NEGATIVE_INFINITY).build();
        Assert.fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_OnlyScaleTwoArg_NegativeInfinity_NegativeInfinity() {
        new ThumbnailParameterBuilder().scale(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY).build();
        Assert.fail();
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Where resizer is called with a specific Resizer</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A ThumbnailParameter will contain the specified Resizer.</li>
     * </ol>
     */
    @Test
    public void build_calledResizer_returnsGivenResizer() {
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.5).resizer(BICUBIC).build();
        Assert.assertEquals(BICUBIC, param.getResizer());
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Where resizer is called with a specific Resizer</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A ThumbnailParameter will contain a ResizerFactory which will return
     * the specified Resizer.</li>
     * </ol>
     */
    @Test
    public void build_calledResizer_returnedResizerFactoryReturnsResizer() {
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.5).resizer(BICUBIC).build();
        Assert.assertEquals(BICUBIC, param.getResizerFactory().getResizer());
    }

    /**
     * Test for the {@link ThumbnailParameterBuilder#build()} method, where
     * <ol>
     * <li>Where resizerFactory is called with a specific ResizerFactory</li>
     * </ol>
     * and the expected outcome is,
     * <ol>
     * <li>A ThumbnailParameter will contain the specified ResizerFactory.</li>
     * </ol>
     */
    @Test
    public void build_calledResizerFactory() {
        ResizerFactory rf = new ResizerFactory() {
            public Resizer getResizer(Dimension arg0, Dimension arg1) {
                return null;
            }

            public Resizer getResizer() {
                return null;
            }
        };
        ThumbnailParameter param = new ThumbnailParameterBuilder().scale(0.5).resizerFactory(rf).build();
        Assert.assertEquals(rf, param.getResizerFactory());
    }
}

