package com.db.chart.model;


import android.test.suitebuilder.annotation.SmallTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
@SmallTest
public class BarSetTest {
    BarSet set;

    Bar bar;

    /**
     * No null point argument.
     */
    @Test(expected = NullPointerException.class)
    public void pointNullException() {
        set.addBar(null);
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
     * Gradient colors can't be null.
     */
    @Test(expected = NullPointerException.class)
    public void barGradientNullException() {
        bar.setGradientColor(null, new float[]{ 1.0F, 2.0F });
    }
}

