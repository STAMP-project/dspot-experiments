package com.github.mikephil.charting.test;


import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by philipp on 06/06/16.
 */
public class BarDataTest {
    @Test
    public void testGroupBars() {
        float groupSpace = 5.0F;
        float barSpace = 1.0F;
        List<BarEntry> values1 = new ArrayList<>();
        List<BarEntry> values2 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            values1.add(new BarEntry(i, 50));
            values2.add(new BarEntry(i, 60));
        }
        BarDataSet barDataSet1 = new BarDataSet(values1, "Set1");
        BarDataSet barDataSet2 = new BarDataSet(values2, "Set2");
        BarData data = new BarData(barDataSet1, barDataSet2);
        data.setBarWidth(10.0F);
        float groupWidth = data.getGroupWidth(groupSpace, barSpace);
        Assert.assertEquals(27.0F, groupWidth, 0.01F);
        Assert.assertEquals(0.0F, values1.get(0).getX(), 0.01F);
        Assert.assertEquals(1.0F, values1.get(1).getX(), 0.01F);
        data.groupBars(1000, groupSpace, barSpace);
        // 1000 + 2.5 + 0.5 + 5
        Assert.assertEquals(1008.0F, values1.get(0).getX(), 0.01F);
        Assert.assertEquals(1019.0F, values2.get(0).getX(), 0.01F);
        Assert.assertEquals(1035.0F, values1.get(1).getX(), 0.01F);
        Assert.assertEquals(1046.0F, values2.get(1).getX(), 0.01F);
        data.groupBars((-1000), groupSpace, barSpace);
        Assert.assertEquals((-992.0F), values1.get(0).getX(), 0.01F);
        Assert.assertEquals((-981.0F), values2.get(0).getX(), 0.01F);
        Assert.assertEquals((-965.0F), values1.get(1).getX(), 0.01F);
        Assert.assertEquals((-954.0F), values2.get(1).getX(), 0.01F);
        data.setBarWidth(20.0F);
        groupWidth = data.getGroupWidth(groupSpace, barSpace);
        Assert.assertEquals(47.0F, groupWidth, 0.01F);
        data.setBarWidth(10.0F);
        data.groupBars((-20), groupSpace, barSpace);
        Assert.assertEquals((-12.0F), values1.get(0).getX(), 0.01F);
        Assert.assertEquals((-1.0F), values2.get(0).getX(), 0.01F);
        Assert.assertEquals(15.0F, values1.get(1).getX(), 0.01F);
        Assert.assertEquals(26.0F, values2.get(1).getX(), 0.01F);
    }
}

