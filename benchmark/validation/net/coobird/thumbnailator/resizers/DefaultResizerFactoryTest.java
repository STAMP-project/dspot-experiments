package net.coobird.thumbnailator.resizers;


import java.awt.Dimension;
import org.junit.Assert;
import org.junit.Test;

import static Resizers.BICUBIC;
import static Resizers.BILINEAR;
import static Resizers.NULL;
import static Resizers.PROGRESSIVE;


public class DefaultResizerFactoryTest {
    @Test
    public void defaultResizer() {
        // given
        Resizer resizer = PROGRESSIVE;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        // when
        Resizer receivedResizer = factory.getResizer();
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceSmallerThanDestination() {
        // given
        Resizer resizer = BICUBIC;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(100, 100);
        Dimension targetDimension = new Dimension(200, 200);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceLargerThanDestination_LessThan2Times() {
        // given
        Resizer resizer = BILINEAR;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(200, 200);
        Dimension targetDimension = new Dimension(150, 150);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceLargerThanDestination_2Times() {
        // given
        Resizer resizer = BILINEAR;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(200, 200);
        Dimension targetDimension = new Dimension(100, 100);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceLargerThanDestination_MoreThan2Times() {
        // given
        Resizer resizer = PROGRESSIVE;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(200, 200);
        Dimension targetDimension = new Dimension(50, 50);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceSameSizeAsDestination() {
        // given
        Resizer resizer = NULL;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(100, 100);
        Dimension targetDimension = new Dimension(100, 100);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceHeightLargerThanDestination() {
        // given
        Resizer resizer = PROGRESSIVE;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(100, 200);
        Dimension targetDimension = new Dimension(100, 100);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceHeightSmallerThanDestination() {
        // given
        Resizer resizer = PROGRESSIVE;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(100, 50);
        Dimension targetDimension = new Dimension(100, 100);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceWidthLargerThanDestination() {
        // given
        Resizer resizer = PROGRESSIVE;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(200, 100);
        Dimension targetDimension = new Dimension(100, 100);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }

    @Test
    public void resizerWhereSourceWidthSmallerThanDestination() {
        // given
        Resizer resizer = PROGRESSIVE;
        ResizerFactory factory = DefaultResizerFactory.getInstance();
        Dimension sourceDimension = new Dimension(50, 100);
        Dimension targetDimension = new Dimension(100, 100);
        // when
        Resizer receivedResizer = factory.getResizer(sourceDimension, targetDimension);
        // then
        Assert.assertEquals(resizer, receivedResizer);
    }
}

