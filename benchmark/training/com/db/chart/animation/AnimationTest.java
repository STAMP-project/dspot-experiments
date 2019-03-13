package com.db.chart.animation;


import android.graphics.Rect;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class AnimationTest {
    private Animation mAnimation;

    @Test
    public void startingPoint_BottomLeft() {
        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{ new float[]{ 0.0F, 1.0F } });
        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues = mAnimation.applyStartingPosition(values, area, 0.0F, 0.0F);
        Assert.assertEquals(0, newValues.get(0)[0][0], 0.0F);
        Assert.assertEquals(10, newValues.get(0)[0][1], 0.0F);
    }

    @Test
    public void startingPoint_TopLeft() {
        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{ new float[]{ 0.0F, 1.0F } });
        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues = mAnimation.applyStartingPosition(values, area, 0.0F, 1.0F);
        Assert.assertEquals(0, newValues.get(0)[0][0], 0.0F);
        Assert.assertEquals(0, newValues.get(0)[0][1], 0.0F);
    }

    @Test
    public void startingPoint_BottomRight() {
        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{ new float[]{ 0.0F, 1.0F } });
        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues = mAnimation.applyStartingPosition(values, area, 1.0F, 0.0F);
        Assert.assertEquals(10, newValues.get(0)[0][0], 0.0F);
        Assert.assertEquals(10, newValues.get(0)[0][1], 0.0F);
    }

    @Test
    public void startingPoint_TopRight() {
        ArrayList<float[][]> values = new ArrayList<>(1);
        values.add(new float[][]{ new float[]{ 0.0F, 1.0F } });
        Rect area = new Rect();
        area.left = 0;
        area.top = 0;
        area.right = 10;
        area.bottom = 10;
        ArrayList<float[][]> newValues = mAnimation.applyStartingPosition(values, area, 1.0F, 1.0F);
        Assert.assertEquals(10, newValues.get(0)[0][0], 0.0F);
        Assert.assertEquals(0, newValues.get(0)[0][1], 0.0F);
    }

    @Test
    public void calculateEntriesInitTime_NoOrder() {
        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, null);
        Assert.assertTrue(((initTimes[0]) < (initTimes[1])));
        Assert.assertTrue(((initTimes[0]) < (initTimes[2])));
        Assert.assertTrue(((initTimes[1]) < (initTimes[2])));
    }

    @Test
    public void calculateEntriesInitTime_BackwardsOrder() {
        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, new int[]{ 2, 1, 0 });
        Assert.assertTrue(((initTimes[2]) < (initTimes[0])));
        Assert.assertTrue(((initTimes[2]) < (initTimes[1])));
        Assert.assertTrue(((initTimes[1]) < (initTimes[0])));
    }

    @Test
    public void calculateEntriesInitTime_MiddleOrder() {
        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, new int[]{ 1, 0, 2 });
        Assert.assertTrue(((initTimes[1]) < (initTimes[0])));
        Assert.assertTrue(((initTimes[1]) < (initTimes[2])));
        Assert.assertTrue(((initTimes[0]) < (initTimes[2])));
    }

    @Test
    public void calculateEntriesInitTime_NoOverlap() {
        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0, null);
        Assert.assertEquals(0, initTimes[0]);
        Assert.assertEquals(30, initTimes[1]);
        Assert.assertEquals(60, initTimes[2]);
    }

    @Test
    public void calculateEntriesInitTime_FullOverlap() {
        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 1, null);
        Assert.assertEquals(0, initTimes[0]);
        Assert.assertTrue(((initTimes[1]) == (initTimes[0])));
        Assert.assertTrue(((initTimes[1]) == (initTimes[2])));
    }

    @Test
    public void calculateEntriesInitTime_HalfOverlap() {
        long[] initTimes = mAnimation.calculateEntriesInitTime(3, 90, 0.5F, null);
        Assert.assertEquals(0, initTimes[0]);
        Assert.assertEquals(23, initTimes[1]);
        Assert.assertEquals(45, initTimes[2]);
    }

    @Test
    public void calculateEntriesDuration_NoOverlap() {
        Assert.assertEquals(30, mAnimation.calculateEntriesDuration(3, 90, 0));
    }

    @Test
    public void calculateEntriesDuration_FullOverlap() {
        Assert.assertEquals(90, mAnimation.calculateEntriesDuration(3, 90, 1));
    }

    @Test
    public void calculateEntriesDuration_HalfOverlap() {
        Assert.assertEquals(60, mAnimation.calculateEntriesDuration(3, 90, 0.5F));
    }
}

