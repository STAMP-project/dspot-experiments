package com.github.mikephil.charting.test;


import YAxis.AxisDependency.LEFT;
import YAxis.AxisDependency.RIGHT;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by philipp on 06/06/16.
 */
public class ChartDataTest {
    @Test
    public void testDynamicChartData() {
        List<Entry> entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(10, 10));
        entries1.add(new Entry(15, (-2)));
        entries1.add(new Entry(21, 50));
        ScatterDataSet set1 = new ScatterDataSet(entries1, "");
        List<Entry> entries2 = new ArrayList<Entry>();
        entries2.add(new Entry((-1), 10));
        entries2.add(new Entry(10, 2));
        entries2.add(new Entry(20, 5));
        ScatterDataSet set2 = new ScatterDataSet(entries2, "");
        ScatterData data = new ScatterData(set1, set2);
        Assert.assertEquals((-2), data.getYMin(LEFT), 0.01F);
        Assert.assertEquals(50.0F, data.getYMax(LEFT), 0.01F);
        Assert.assertEquals(6, data.getEntryCount());
        Assert.assertEquals((-1.0F), data.getXMin(), 0.01F);
        Assert.assertEquals(21.0F, data.getXMax(), 0.01F);
        Assert.assertEquals((-2.0F), data.getYMin(), 0.01F);
        Assert.assertEquals(50.0F, data.getYMax(), 0.01F);
        Assert.assertEquals(3, data.getMaxEntryCountSet().getEntryCount());
        // now add and remove values
        data.addEntry(new Entry((-10), (-10)), 0);
        Assert.assertEquals(set1, data.getMaxEntryCountSet());
        Assert.assertEquals(4, data.getMaxEntryCountSet().getEntryCount());
        Assert.assertEquals((-10.0F), data.getYMin(LEFT), 0.01F);
        Assert.assertEquals(50.0F, data.getYMax(LEFT), 0.01F);
        Assert.assertEquals((-10.0F), data.getXMin(), 0.01F);
        Assert.assertEquals(21.0F, data.getXMax(), 0.01F);
        Assert.assertEquals((-10.0F), data.getYMin(), 0.01F);
        Assert.assertEquals(50.0F, data.getYMax(), 0.01F);
        data.addEntry(new Entry((-100), 100), 0);
        data.addEntry(new Entry(0, (-100)), 0);
        Assert.assertEquals((-100.0F), data.getYMin(LEFT), 0.01F);
        Assert.assertEquals(100.0F, data.getYMax(LEFT), 0.01F);
        // right axis will adapt left axis values if no right axis values are present
        Assert.assertEquals((-100), data.getYMin(RIGHT), 0.01F);
        Assert.assertEquals(100.0F, data.getYMax(RIGHT), 0.01F);
        List<Entry> entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(0, 200));
        entries3.add(new Entry(0, (-50)));
        ScatterDataSet set3 = new ScatterDataSet(entries3, "");
        set3.setAxisDependency(RIGHT);
        data.addDataSet(set3);
        Assert.assertEquals(3, data.getDataSetCount());
        Assert.assertEquals((-100.0F), data.getYMin(LEFT), 0.01F);
        Assert.assertEquals(100.0F, data.getYMax(LEFT), 0.01F);
        Assert.assertEquals((-50.0F), data.getYMin(RIGHT), 0.01F);
        Assert.assertEquals(200.0F, data.getYMax(RIGHT), 0.01F);
        LineData lineData = new LineData();
        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getYMax(), 0.01F);
        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(LEFT), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getYMax(LEFT), 0.01F);
        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(RIGHT), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getYMax(RIGHT), 0.01F);
        Assert.assertEquals(0, lineData.getDataSetCount());
        List<Entry> lineEntries1 = new ArrayList<Entry>();
        lineEntries1.add(new Entry(10, 90));
        lineEntries1.add(new Entry(1000, 1000));
        LineDataSet lineSet1 = new LineDataSet(lineEntries1, "");
        lineData.addDataSet(lineSet1);
        Assert.assertEquals(1, lineData.getDataSetCount());
        Assert.assertEquals(2, lineSet1.getEntryCount());
        Assert.assertEquals(2, lineData.getEntryCount());
        Assert.assertEquals(10, lineData.getXMin(), 0.01F);
        Assert.assertEquals(1000.0F, lineData.getXMax(), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(), 0.01F);
        Assert.assertEquals(1000, lineData.getYMax(), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(LEFT), 0.01F);
        Assert.assertEquals(1000.0F, lineData.getYMax(LEFT), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(RIGHT), 0.01F);
        Assert.assertEquals(1000, lineData.getYMax(RIGHT), 0.01F);
        List<Entry> lineEntries2 = new ArrayList<Entry>();
        lineEntries2.add(new Entry((-1000), 2000));
        lineEntries2.add(new Entry(2000, (-3000)));
        Entry e = new Entry((-1000), 2500);
        lineEntries2.add(e);
        LineDataSet lineSet2 = new LineDataSet(lineEntries2, "");
        lineSet2.setAxisDependency(RIGHT);
        lineData.addDataSet(lineSet2);
        Assert.assertEquals(2, lineData.getDataSetCount());
        Assert.assertEquals(3, lineSet2.getEntryCount());
        Assert.assertEquals(5, lineData.getEntryCount());
        Assert.assertEquals((-1000), lineData.getXMin(), 0.01F);
        Assert.assertEquals(2000, lineData.getXMax(), 0.01F);
        Assert.assertEquals((-3000), lineData.getYMin(), 0.01F);
        Assert.assertEquals(2500, lineData.getYMax(), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(LEFT), 0.01F);
        Assert.assertEquals(1000.0F, lineData.getYMax(LEFT), 0.01F);
        Assert.assertEquals((-3000), lineData.getYMin(RIGHT), 0.01F);
        Assert.assertEquals(2500, lineData.getYMax(RIGHT), 0.01F);
        Assert.assertTrue(lineData.removeEntry(e, 1));
        Assert.assertEquals((-1000), lineData.getXMin(), 0.01F);
        Assert.assertEquals(2000, lineData.getXMax(), 0.01F);
        Assert.assertEquals((-3000), lineData.getYMin(), 0.01F);
        Assert.assertEquals(2000, lineData.getYMax(), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(LEFT), 0.01F);
        Assert.assertEquals(1000.0F, lineData.getYMax(LEFT), 0.01F);
        Assert.assertEquals((-3000), lineData.getYMin(RIGHT), 0.01F);
        Assert.assertEquals(2000, lineData.getYMax(RIGHT), 0.01F);
        Assert.assertEquals(2, lineData.getDataSetCount());
        Assert.assertTrue(lineData.removeDataSet(lineSet2));
        Assert.assertEquals(1, lineData.getDataSetCount());
        Assert.assertEquals(10, lineData.getXMin(), 0.01F);
        Assert.assertEquals(1000, lineData.getXMax(), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(), 0.01F);
        Assert.assertEquals(1000, lineData.getYMax(), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(LEFT), 0.01F);
        Assert.assertEquals(1000.0F, lineData.getYMax(LEFT), 0.01F);
        Assert.assertEquals(90, lineData.getYMin(RIGHT), 0.01F);
        Assert.assertEquals(1000, lineData.getYMax(RIGHT), 0.01F);
        Assert.assertTrue(lineData.removeDataSet(lineSet1));
        Assert.assertEquals(0, lineData.getDataSetCount());
        Assert.assertEquals(Float.MAX_VALUE, lineData.getXMin(), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getXMax(), 0.01F);
        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getYMax(), 0.01F);
        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(LEFT), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getYMax(LEFT), 0.01F);
        Assert.assertEquals(Float.MAX_VALUE, lineData.getYMin(RIGHT), 0.01F);
        Assert.assertEquals((-(Float.MAX_VALUE)), lineData.getYMax(RIGHT), 0.01F);
        Assert.assertFalse(lineData.removeDataSet(lineSet1));
        Assert.assertFalse(lineData.removeDataSet(lineSet2));
    }
}

