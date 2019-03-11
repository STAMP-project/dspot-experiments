package com.db.chart.model;


import android.test.suitebuilder.annotation.SmallTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
@SmallTest
public class LineSetTest {
    LineSet set;

    Point point;

    /**
     * No null point argument.
     */
    @Test(expected = NullPointerException.class)
    public void pointNullException() {
        set.addPoint(null);
    }

    /**
     * Test values update.
     */
    @Test
    public void updateValues() {
        set.updateValues(new float[]{ 4.0F, 5.0F, 6.0F });
        Assert.assertEquals(4.0F, set.getValue(0), 0.0F);
        Assert.assertEquals(5.0F, set.getValue(1), 0.0F);
        Assert.assertEquals(6.0F, set.getValue(2), 0.0F);
    }

    /**
     * No different size when updating values.
     */
    @Test(expected = IllegalArgumentException.class)
    public void updateValuesSizeException() {
        set.updateValues(new float[]{ 1.0F, 2.0F });
    }

    /**
     * Test color customization.
     * Line's fill color assigned to line's color when not previously defined.
     */
    @Test
    public void colorCustomization() {
        set.setFill(1);
        Assert.assertTrue(set.hasFill());
        Assert.assertEquals(1, set.getColor());
        Assert.assertEquals(1, set.getFillColor());
        set.setColor(3);
        set.setFill(2);
        Assert.assertEquals(3, set.getColor());
        set.setGradientFill(new int[]{ 1, 2 }, new float[]{ 1.0F, 2.0F });
        Assert.assertTrue(set.hasGradientFill());
    }

    /**
     * Test gradient color customization.
     * Line's gradient fill color assigned to line's color when not previously defined.
     */
    @Test
    public void colorGradientCustomization() {
        set.setGradientFill(new int[]{ 1, 2 }, new float[]{ 1.0F, 2.0F });
        Assert.assertTrue(set.hasGradientFill());
        Assert.assertEquals(new int[]{ 1, 2 }[0], set.getColor());
        Assert.assertArrayEquals(new int[]{ 1, 2 }, set.getGradientColors());
        set.setColor(3);
        set.setGradientFill(new int[]{ 1, 2 }, new float[]{ 1.0F, 2.0F });
        Assert.assertEquals(3, set.getColor());
    }

    /**
     * No negative thickness.
     */
    @Test(expected = IllegalArgumentException.class)
    public void thicknessNegativeException() {
        set.setThickness((-1));
    }

    /**
     * No negative dots stroke thickness.
     */
    @Test(expected = IllegalArgumentException.class)
    public void thicknessDotsNegativeException() {
        set.setDotsStrokeThickness((-1));
    }

    /**
     * No negative dots radius.
     */
    @Test(expected = IllegalArgumentException.class)
    public void radiusDotsNegativeException() {
        set.setDotsRadius((-1));
    }

    /**
     * Non null argument.
     */
    @Test(expected = NullPointerException.class)
    public void dashedNullException() {
        set.setDashed(null);
    }

    /**
     * End point should be equal to set's size until a different value is defined.
     */
    @Test
    public void endBorder() {
        Assert.assertEquals(set.size(), set.getEnd());
        set.endAt(1);
        Assert.assertEquals(1, set.getEnd());
    }

    /**
     * No negative index assigned to begin.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void beginNegativeException() {
        set.beginAt((-1));
    }

    /**
     * No index greater than set's size assigned to begin.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void beginGreaterException() {
        set.beginAt(((set.size()) + 1));
    }

    /**
     * No negative index assigned to end.
     */
    @Test(expected = IllegalArgumentException.class)
    public void endNegativeException() {
        set.endAt((-1));
    }

    /**
     * No index greater than set's size assigned to end.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void endGreaterException() {
        set.endAt(((set.size()) + 1));
    }

    /**
     * No index lesser than begin assigned to end.
     */
    @Test(expected = IllegalArgumentException.class)
    public void endLesserException() {
        set.beginAt(2);
        set.endAt(1);
    }

    /**
     * No null dots drawable argument.
     */
    @Test(expected = NullPointerException.class)
    public void drawableNullException() {
        set.setDotsDrawable(null);
    }

    /**
     * No null drawable argument.
     */
    @Test(expected = IllegalArgumentException.class)
    public void drawableDotNullException() {
        point.setDrawable(null);
    }

    /**
     * No negative dot stroke thickness.
     */
    @Test(expected = IllegalArgumentException.class)
    public void thicknessDotNegativeException() {
        point.setStrokeThickness((-1));
    }

    /**
     * No negative dots radius.
     */
    @Test(expected = IllegalArgumentException.class)
    public void radiusDotNegativeException() {
        point.setRadius((-1));
    }
}

