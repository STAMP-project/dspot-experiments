package com.github.mikephil.charting.test;


import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.AxisRenderer;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by philipp on 31/05/16.
 */
public class AxisRendererTest {
    @Test
    public void testComputeAxisValues() {
        YAxis yAxis = new YAxis();
        yAxis.setLabelCount(6);
        AxisRenderer renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis(0, 100, false);
        float[] entries = yAxis.mEntries;
        Assert.assertEquals(6, entries.length);
        Assert.assertEquals(20, ((entries[1]) - (entries[0])), 0.01);// interval 20

        Assert.assertEquals(0, entries[0], 0.01);
        Assert.assertEquals(100, entries[((entries.length) - 1)], 0.01);
        yAxis = new YAxis();
        yAxis.setLabelCount(6);
        yAxis.setGranularity(50.0F);
        renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis(0, 100, false);
        entries = yAxis.mEntries;
        Assert.assertEquals(3, entries.length);
        Assert.assertEquals(50, ((entries[1]) - (entries[0])), 0.01);// interval 50

        Assert.assertEquals(0, entries[0], 0.01);
        Assert.assertEquals(100, entries[((entries.length) - 1)], 0.01);
        yAxis = new YAxis();
        yAxis.setLabelCount(5, true);
        renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis(0, 100, false);
        entries = yAxis.mEntries;
        Assert.assertEquals(5, entries.length);
        Assert.assertEquals(25, ((entries[1]) - (entries[0])), 0.01);// interval 25

        Assert.assertEquals(0, entries[0], 0.01);
        Assert.assertEquals(100, entries[((entries.length) - 1)], 0.01);
        yAxis = new YAxis();
        yAxis.setLabelCount(5, true);
        renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis(0, 0.01F, false);
        entries = yAxis.mEntries;
        Assert.assertEquals(5, entries.length);
        Assert.assertEquals(0.0025, ((entries[1]) - (entries[0])), 1.0E-4);
        Assert.assertEquals(0, entries[0], 1.0E-4);
        Assert.assertEquals(0.01, entries[((entries.length) - 1)], 1.0E-4);
        yAxis = new YAxis();
        yAxis.setLabelCount(5, false);
        renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis(0, 0.01F, false);
        entries = yAxis.mEntries;
        Assert.assertEquals(5, entries.length);
        Assert.assertEquals(0.002, ((entries[1]) - (entries[0])), 1.0E-4);
        Assert.assertEquals(0, entries[0], 1.0E-4);
        Assert.assertEquals(0.008, entries[((entries.length) - 1)], 1.0E-4);
        yAxis = new YAxis();
        yAxis.setLabelCount(6);
        renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis((-50), 50, false);
        entries = yAxis.mEntries;
        Assert.assertEquals(5, entries.length);
        Assert.assertEquals((-40), entries[0], 1.0E-4);
        Assert.assertEquals(0, entries[2], 1.0E-4);
        Assert.assertEquals(40, entries[((entries.length) - 1)], 1.0E-4);
        yAxis = new YAxis();
        yAxis.setLabelCount(6);
        renderer = new com.github.mikephil.charting.renderer.YAxisRenderer(null, yAxis, null);
        renderer.computeAxis((-50), 100, false);
        entries = yAxis.mEntries;
        Assert.assertEquals(5, entries.length);
        Assert.assertEquals((-30), entries[0], 1.0E-4);
        Assert.assertEquals(30, entries[2], 1.0E-4);
        Assert.assertEquals(90, entries[((entries.length) - 1)], 1.0E-4);
    }
}

