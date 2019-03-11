package org.kairosdb.core.datapoints;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.util.KDataInputStream;


/**
 * Created with IntelliJ IDEA.
 * User: bhawkins
 * Date: 12/9/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataPointTestCommon {
    public static DataPointFactory factory;

    public static List<DataPoint> dataPointList = new ArrayList<DataPoint>();

    public static double sum = 0.0;

    @Test
    public void testBufferSerialization() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
        DataOutputStream dataOutputStream = new DataOutputStream(buffer);
        for (DataPoint dataPoint : DataPointTestCommon.dataPointList) {
            dataPoint.writeValueToBuffer(dataOutputStream);
        }
        double testSum = 0.0;
        KDataInputStream dataInputStream = new KDataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        for (int i = 0; i < (DataPointTestCommon.dataPointList.size()); i++) {
            DataPoint dp = DataPointTestCommon.factory.getDataPoint(DataPointTestCommon.dataPointList.get(i).getTimestamp(), dataInputStream);
            Assert.assertEquals(DataPointTestCommon.dataPointList.get(i), dp);
            testSum += dp.getDoubleValue();
        }
        Assert.assertEquals(DataPointTestCommon.sum, testSum, 1.0E-4);
    }

    @Test
    public void testEqualsHashCode() {
        final HashSet<DataPoint> dataPointsSet = new HashSet(DataPointTestCommon.dataPointList);
        Assert.assertEquals(DataPointTestCommon.dataPointList.size(), dataPointsSet.size());
    }
}

