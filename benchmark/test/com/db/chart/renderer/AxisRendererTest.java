package com.db.chart.renderer;


import ChartView.Style;
import android.test.suitebuilder.annotation.MediumTest;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.ChartSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@MediumTest
public class AxisRendererTest {
    @Mock
    Style mStyleMock;

    private XRenderer mXRndr;

    private ArrayList<ChartSet> mData;

    @Test
    public void init_HandleValues_NoBordersAndNoStep() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        mXRndr.setHandleValues(true);
        mXRndr.init(mData, mStyleMock);
        ArrayList<String> labels = mXRndr.labels;
        Assert.assertEquals("0", labels.get(0));
        Assert.assertEquals("0.333", labels.get(1));
        Assert.assertEquals(0.333, mXRndr.getStep(), 0.001);
    }

    @Test
    public void init_HandleValues_NoBordersAndStep() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        mXRndr.setHandleValues(true);
        mXRndr.setStep(9);
        mXRndr.init(mData, mStyleMock);
        ArrayList<String> labels = mXRndr.labels;
        Assert.assertEquals("0", labels.get(0));
        Assert.assertEquals("9", labels.get(1));
    }

    @Test
    public void init_HandleValues_BordersAndNoStep() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        mXRndr.setHandleValues(true);
        mXRndr.setBorderValues(0, 9);
        mXRndr.init(mData, mStyleMock);
        Assert.assertEquals(3, mXRndr.getStep(), 0);
    }

    @Test
    public void defineMandatoryBorderSpacing_Mandatory_Result() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        Mockito.when(mStyleMock.getAxisBorderSpacing()).thenReturn(1);
        mXRndr.init(mData, mStyleMock);
        mXRndr.setMandatoryBorderSpacing(true);
        mXRndr.defineMandatoryBorderSpacing(0, 4);
        Assert.assertEquals(0.5F, mXRndr.mandatoryBorderSpacing, 0.0F);
    }

    @Test
    public void defineMandatoryBorderSpacing_NotMandatory_Result() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        Mockito.when(mStyleMock.getAxisBorderSpacing()).thenReturn(1);
        mXRndr.init(mData, mStyleMock);
        mXRndr.setMandatoryBorderSpacing(false);
        mXRndr.defineMandatoryBorderSpacing(0, 4);
        Assert.assertEquals(0.0F, mXRndr.mandatoryBorderSpacing, 0.0F);
    }

    @Test
    public void defineLabelsPosition_Nominal_ScreenStep2() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        Mockito.when(mStyleMock.getAxisBorderSpacing()).thenReturn(1);
        mXRndr.init(mData, mStyleMock);
        mXRndr.defineLabelsPosition(0, 4);
        Assert.assertEquals(2.0F, mXRndr.screenStep, 0.0F);
    }

    @Test
    public void defineLabelsPosition_Nominal_LabelsPos13() {
        Mockito.when(mStyleMock.getLabelsFormat()).thenReturn(new DecimalFormat());
        Mockito.when(mStyleMock.getAxisBorderSpacing()).thenReturn(1);
        mXRndr.init(mData, mStyleMock);
        mXRndr.defineLabelsPosition(0, 4);
        ArrayList<Float> toAssert = new ArrayList<>();
        toAssert.add(1.0F);
        toAssert.add(3.0F);
        Assert.assertEquals(toAssert, mXRndr.labelsPos);
    }

    @Test
    public void convertToLabelsFormat_Integer_StringXXDB() {
        ArrayList<Float> values = new ArrayList<>();
        values.add(3.0F);
        DecimalFormat format = new DecimalFormat("#'DB'");
        Assert.assertEquals("3DB", mXRndr.convertToLabelsFormat(values, format).get(0));
    }

    @Test
    public void extractLabels_ChartSets_Result() {
        ArrayList<String> toAssert = new ArrayList<>();
        toAssert.add("0");
        toAssert.add("1");
        Assert.assertEquals(toAssert, mXRndr.extractLabels(mData));
    }

    @Test
    public void findBorders_NoStep() {
        Assert.assertArrayEquals(new float[]{ 0, 1 }, mXRndr.findBorders(mData), 0);
    }

    @Test
    public void findBorders_BiggerStep_Result() {
        Assert.assertArrayEquals(new float[]{ 0, 9 }, mXRndr.findBorders(mData, 9), 0);
    }

    @Test
    public void findBorders_MaxMinEqual_MaxPlusStep() {
        BarSet set = new BarSet();
        for (int i = 0; i < 2; i++)
            set.addBar(new Bar(Integer.toString(i), ((float) (0))));

        mData = new ArrayList();
        mData.add(set);
        Assert.assertArrayEquals(new float[]{ 0, 9 }, mXRndr.findBorders(mData, 9), 0);
    }

    @Test
    public void calculateValues_Nominal_Result() {
        ArrayList<Float> toAssert = new ArrayList<>();
        toAssert.add(0.0F);
        toAssert.add(5.0F);
        toAssert.add(10.0F);
        Assert.assertEquals(toAssert, mXRndr.calculateValues(0, 8, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBorderValues_MinGreaterMax_ThrowException() {
        mXRndr.setBorderValues(2, 1, 1);
    }

    @Test
    public void setBorderValues_Nominal_StepLargestDivisor() {
        XRenderer spyXRndr = Mockito.spy(mXRndr);
        Mockito.doReturn(false).when(spyXRndr).hasStep();
        spyXRndr.setBorderValues(3, 26);
        Assert.assertEquals(7.666, spyXRndr.getStep(), 0.001);
        spyXRndr.setBorderValues(3, 30);
        Assert.assertEquals(9, spyXRndr.getStep(), 0);
        spyXRndr.setBorderValues(3, 18);
        Assert.assertEquals(5, spyXRndr.getStep(), 0);
        spyXRndr.setBorderValues(3, 23);
        Assert.assertEquals(6.666, spyXRndr.getStep(), 0.001);
        spyXRndr.setBorderValues((-3), 17);
        Assert.assertEquals(6.666, spyXRndr.getStep(), 0.001);
    }
}

