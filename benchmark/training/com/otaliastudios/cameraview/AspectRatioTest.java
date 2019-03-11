package com.otaliastudios.cameraview;


import AspectRatio.sCache;
import org.junit.Assert;
import org.junit.Test;


public class AspectRatioTest {
    @Test
    public void testConstructor() {
        AspectRatio ratio = AspectRatio.of(50, 10);
        Assert.assertEquals(ratio.getX(), 5.0F, 0);
        Assert.assertEquals(ratio.getY(), 1.0F, 0);
        Assert.assertEquals(ratio.toString(), "5:1");
    }

    @Test
    public void testEquals() {
        AspectRatio ratio = AspectRatio.of(50, 10);
        Assert.assertNotNull(ratio);
        Assert.assertEquals(ratio, ratio);
        AspectRatio ratio1 = AspectRatio.of(5, 1);
        Assert.assertEquals(ratio, ratio1);
        sCache.clear();
        AspectRatio ratio2 = AspectRatio.of(500, 100);
        Assert.assertEquals(ratio, ratio2);
        Size size = new Size(500, 100);
        Assert.assertTrue(ratio.matches(size));
    }

    @Test
    public void testCompare() {
        AspectRatio ratio1 = AspectRatio.of(10, 10);
        AspectRatio ratio2 = AspectRatio.of(10, 2);
        AspectRatio ratio3 = AspectRatio.of(2, 10);
        Assert.assertTrue(((ratio1.compareTo(ratio2)) < 0));
        Assert.assertTrue(((ratio1.compareTo(ratio3)) > 0));
        // noinspection EqualsWithItself,SimplifiableJUnitAssertion
        Assert.assertTrue(((ratio1.compareTo(ratio1)) == 0));
        Assert.assertNotEquals(ratio1.hashCode(), ratio2.hashCode());
    }

    @Test
    public void testInverse() {
        AspectRatio ratio = AspectRatio.of(50, 10);
        AspectRatio inverse = ratio.inverse();
        Assert.assertEquals(inverse.getX(), 1.0F, 0);
        Assert.assertEquals(inverse.getY(), 5.0F, 0);
    }

    @Test(expected = NumberFormatException.class)
    public void testParse_notNumbers() {
        AspectRatio.parse("a:b");
    }

    @Test(expected = NumberFormatException.class)
    public void testParse_noColon() {
        AspectRatio.parse("24");
    }

    @Test
    public void testParse() {
        AspectRatio ratio = AspectRatio.parse("16:9");
        Assert.assertEquals(ratio.getX(), 16);
        Assert.assertEquals(ratio.getY(), 9);
    }
}

