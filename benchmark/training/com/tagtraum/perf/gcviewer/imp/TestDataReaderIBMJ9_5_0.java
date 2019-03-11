package com.tagtraum.perf.gcviewer.imp;


import Type.FULL_GC;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the implementation of {@link TestDataReaderIBMJ9_5_0} and {@link IBMJ9SAXHandler}.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 18.02.2013</p>
 */
public class TestDataReaderIBMJ9_5_0 {
    @Test
    public void afTenuredGlobal() throws Exception {
        final DataReader reader = getDataReader("SampleIBMJ9_5_0af-global-200811_07.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("pause", 0.035912, event.getPause(), 1.0E-7);
        Assert.assertEquals("timestamp", 0, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", FULL_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", ((52428800 - 2621440) / 1024), event.getPreUsed());
        Assert.assertEquals("after", ((52428800 - 40481192) / 1024), event.getPostUsed());
        Assert.assertEquals("total", (52428800 / 1024), event.getTotal());
    }

    @Test
    public void afTenuredGlobal_20090417_AA() throws Exception {
        final DataReader reader = getDataReader("SampleIBMJ9_5_0af-global-20090417_AA.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("pause", 0.837024, event.getPause(), 1.0E-7);
        Assert.assertEquals("timestamp", 0, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", FULL_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", ((12884901888L - 4626919608L) / 1024), event.getPreUsed());
        Assert.assertEquals("after", ((12884901888L - 10933557088L) / 1024), event.getPostUsed());
        Assert.assertEquals("total", (12884901888L / 1024), event.getTotal());
    }
}

