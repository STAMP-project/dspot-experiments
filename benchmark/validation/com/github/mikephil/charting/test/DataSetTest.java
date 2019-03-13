package com.github.mikephil.charting.test;


import DataSet.Rounding.CLOSEST;
import DataSet.Rounding.DOWN;
import DataSet.Rounding.UP;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by philipp on 31/05/16.
 */
public class DataSetTest {
    @Test
    public void testCalcMinMax() {
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 10));
        entries.add(new Entry(15, 2));
        entries.add(new Entry(21, 5));
        ScatterDataSet set = new ScatterDataSet(entries, "");
        Assert.assertEquals(10.0F, set.getXMin(), 0.01F);
        Assert.assertEquals(21.0F, set.getXMax(), 0.01F);
        Assert.assertEquals(2.0F, set.getYMin(), 0.01F);
        Assert.assertEquals(10.0F, set.getYMax(), 0.01F);
        Assert.assertEquals(3, set.getEntryCount());
        set.addEntry(new Entry(25, 1));
        Assert.assertEquals(10.0F, set.getXMin(), 0.01F);
        Assert.assertEquals(25.0F, set.getXMax(), 0.01F);
        Assert.assertEquals(1.0F, set.getYMin(), 0.01F);
        Assert.assertEquals(10.0F, set.getYMax(), 0.01F);
        Assert.assertEquals(4, set.getEntryCount());
        set.removeEntry(3);
        Assert.assertEquals(10.0F, set.getXMin(), 0.01F);
        Assert.assertEquals(21, set.getXMax(), 0.01F);
        Assert.assertEquals(2.0F, set.getYMin(), 0.01F);
        Assert.assertEquals(10.0F, set.getYMax(), 0.01F);
    }

    @Test
    public void testAddRemoveEntry() {
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 10));
        entries.add(new Entry(15, 2));
        entries.add(new Entry(21, 5));
        ScatterDataSet set = new ScatterDataSet(entries, "");
        Assert.assertEquals(3, set.getEntryCount());
        set.addEntryOrdered(new Entry(5, 1));
        Assert.assertEquals(4, set.getEntryCount());
        Assert.assertEquals(5, set.getXMin(), 0.01F);
        Assert.assertEquals(21, set.getXMax(), 0.01F);
        Assert.assertEquals(1.0F, set.getYMin(), 0.01F);
        Assert.assertEquals(10.0F, set.getYMax(), 0.01F);
        Assert.assertEquals(5, set.getEntryForIndex(0).getX(), 0.01F);
        Assert.assertEquals(1, set.getEntryForIndex(0).getY(), 0.01F);
        set.addEntryOrdered(new Entry(20, 50));
        Assert.assertEquals(5, set.getEntryCount());
        Assert.assertEquals(20, set.getEntryForIndex(3).getX(), 0.01F);
        Assert.assertEquals(50, set.getEntryForIndex(3).getY(), 0.01F);
        Assert.assertTrue(set.removeEntry(3));
        Assert.assertEquals(4, set.getEntryCount());
        Assert.assertEquals(21, set.getEntryForIndex(3).getX(), 0.01F);
        Assert.assertEquals(5, set.getEntryForIndex(3).getY(), 0.01F);
        Assert.assertEquals(5, set.getEntryForIndex(0).getX(), 0.01F);
        Assert.assertEquals(1, set.getEntryForIndex(0).getY(), 0.01F);
        Assert.assertTrue(set.removeFirst());
        Assert.assertEquals(3, set.getEntryCount());
        Assert.assertEquals(10, set.getEntryForIndex(0).getX(), 0.01F);
        Assert.assertEquals(10, set.getEntryForIndex(0).getY(), 0.01F);
        set.addEntryOrdered(new Entry(15, 3));
        Assert.assertEquals(4, set.getEntryCount());
        Assert.assertEquals(15, set.getEntryForIndex(1).getX(), 0.01F);
        Assert.assertEquals(3, set.getEntryForIndex(1).getY(), 0.01F);
        Assert.assertEquals(21, set.getEntryForIndex(3).getX(), 0.01F);
        Assert.assertEquals(5, set.getEntryForIndex(3).getY(), 0.01F);
        Assert.assertTrue(set.removeLast());
        Assert.assertEquals(3, set.getEntryCount());
        Assert.assertEquals(15, set.getEntryForIndex(2).getX(), 0.01F);
        Assert.assertEquals(2, set.getEntryForIndex(2).getY(), 0.01F);
        Assert.assertTrue(set.removeLast());
        Assert.assertEquals(2, set.getEntryCount());
        Assert.assertTrue(set.removeLast());
        Assert.assertEquals(1, set.getEntryCount());
        Assert.assertEquals(10, set.getEntryForIndex(0).getX(), 0.01F);
        Assert.assertEquals(10, set.getEntryForIndex(0).getY(), 0.01F);
        Assert.assertTrue(set.removeLast());
        Assert.assertEquals(0, set.getEntryCount());
        Assert.assertFalse(set.removeLast());
        Assert.assertFalse(set.removeFirst());
    }

    @Test
    public void testGetEntryForXValue() {
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(10, 10));
        entries.add(new Entry(15, 5));
        entries.add(new Entry(21, 5));
        ScatterDataSet set = new ScatterDataSet(entries, "");
        Entry closest = set.getEntryForXValue(17, Float.NaN, CLOSEST);
        Assert.assertEquals(15, closest.getX(), 0.01F);
        Assert.assertEquals(5, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(17, Float.NaN, DOWN);
        Assert.assertEquals(15, closest.getX(), 0.01F);
        Assert.assertEquals(5, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(15, Float.NaN, DOWN);
        Assert.assertEquals(15, closest.getX(), 0.01F);
        Assert.assertEquals(5, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(14, Float.NaN, DOWN);
        Assert.assertEquals(10, closest.getX(), 0.01F);
        Assert.assertEquals(10, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(17, Float.NaN, UP);
        Assert.assertEquals(21, closest.getX(), 0.01F);
        Assert.assertEquals(5, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(21, Float.NaN, UP);
        Assert.assertEquals(21, closest.getX(), 0.01F);
        Assert.assertEquals(5, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(21, Float.NaN, CLOSEST);
        Assert.assertEquals(21, closest.getX(), 0.01F);
        Assert.assertEquals(5, closest.getY(), 0.01F);
    }

    @Test
    public void testGetEntryForXValueWithDuplicates() {
        // sorted list of values (by x position)
        List<Entry> values = new ArrayList<Entry>();
        values.add(new Entry(0, 10));
        values.add(new Entry(1, 20));
        values.add(new Entry(2, 30));
        values.add(new Entry(3, 40));
        values.add(new Entry(3, 50));// duplicate

        values.add(new Entry(4, 60));
        values.add(new Entry(4, 70));// duplicate

        values.add(new Entry(5, 80));
        values.add(new Entry(6, 90));
        values.add(new Entry(7, 100));
        values.add(new Entry(8, 110));
        values.add(new Entry(8, 120));// duplicate

        ScatterDataSet set = new ScatterDataSet(values, "");
        Entry closest = set.getEntryForXValue(0, Float.NaN, CLOSEST);
        Assert.assertEquals(0, closest.getX(), 0.01F);
        Assert.assertEquals(10, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(5, Float.NaN, CLOSEST);
        Assert.assertEquals(5, closest.getX(), 0.01F);
        Assert.assertEquals(80, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(5.4F, Float.NaN, CLOSEST);
        Assert.assertEquals(5, closest.getX(), 0.01F);
        Assert.assertEquals(80, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(4.6F, Float.NaN, CLOSEST);
        Assert.assertEquals(5, closest.getX(), 0.01F);
        Assert.assertEquals(80, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(7, Float.NaN, CLOSEST);
        Assert.assertEquals(7, closest.getX(), 0.01F);
        Assert.assertEquals(100, closest.getY(), 0.01F);
        closest = set.getEntryForXValue(4.0F, Float.NaN, CLOSEST);
        Assert.assertEquals(4, closest.getX(), 0.01F);
        Assert.assertEquals(60, closest.getY(), 0.01F);
        List<Entry> entries = set.getEntriesForXValue(4.0F);
        Assert.assertEquals(2, entries.size());
        Assert.assertEquals(60, entries.get(0).getY(), 0.01F);
        Assert.assertEquals(70, entries.get(1).getY(), 0.01F);
        entries = set.getEntriesForXValue(3.5F);
        Assert.assertEquals(0, entries.size());
        entries = set.getEntriesForXValue(2.0F);
        Assert.assertEquals(1, entries.size());
        Assert.assertEquals(30, entries.get(0).getY(), 0.01F);
    }
}

