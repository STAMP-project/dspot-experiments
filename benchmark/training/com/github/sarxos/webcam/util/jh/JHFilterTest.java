package com.github.sarxos.webcam.util.jh;


import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class JHFilterTest {
    public static class TestFilter extends JHFilter {
        @Override
        public BufferedImage filter(BufferedImage src, BufferedImage dest) {
            return null;
        }
    }

    @Test
    public void test_createCompatibleDestImage() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        final BufferedImage image1 = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
        final BufferedImage image2 = createCompatibleDestImage(image1, null);
        Assertions.assertThat(image2.getType()).isEqualTo(image1.getType());
    }

    @Test
    public void test_getBounds2D() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        final BufferedImage image = new BufferedImage(11, 22, BufferedImage.TYPE_BYTE_INDEXED);
        final Rectangle2D bounds = getBounds2D(image);
        Assertions.assertThat(bounds.getWidth()).isEqualTo(11);
        Assertions.assertThat(bounds.getHeight()).isEqualTo(22);
        Assertions.assertThat(bounds.getX()).isEqualTo(0);
        Assertions.assertThat(bounds.getY()).isEqualTo(0);
    }

    @Test
    public void test_getPoint2D() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        final Point2D src = new Point(34, 56);
        final Point2D dst = new Point(67, 78);
        final Point2D out = filter.getPoint2D(src, dst);
        Assertions.assertThat(out).isSameAs(dst);
        Assertions.assertThat(out.getX()).isEqualTo(34);
        Assertions.assertThat(out.getY()).isEqualTo(56);
    }

    @Test
    public void test_getPoint2DNull() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        final Point2D src = new Point(34, 56);
        final Point2D out = getPoint2D(src, null);
        Assertions.assertThat(out.getX()).isEqualTo(34);
        Assertions.assertThat(out.getY()).isEqualTo(56);
    }

    @Test
    public void test_getRenderingHints() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        Assertions.assertThat(getRenderingHints()).isNull();
    }

    @Test
    public void test_setGetRGB() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        final int[] p = new int[]{ 0 };
        setRGB(image, 0, 0, 1, 1, p);
        final int[] c = getRGB(image, 0, 0, 1, 1, p);
        Assertions.assertThat(c).hasSize(1);
        Assertions.assertThat(c[0]).isEqualTo(0);
    }

    @Test
    public void test_setGetRGBIndexed() {
        final JHFilterTest.TestFilter filter = new JHFilterTest.TestFilter();
        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        final int[] p = new int[]{ -16777216 };
        setRGB(image, 0, 0, 1, 1, p);
        final int[] c = getRGB(image, 0, 0, 1, 1, p);
        Assertions.assertThat(c).hasSize(1);
        Assertions.assertThat(c[0]).isEqualTo((-16777216));
    }
}

