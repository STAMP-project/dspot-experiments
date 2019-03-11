package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.util.logging.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on 08.10.2014</p>
 */
public class TestDataReaderIBM_J9_R28 {
    @Test
    public void testAfScavenge() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R28_af_scavenge_full_header.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(2));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(0.025388, 1.0E-7));
        Assert.assertThat("total before", event.getTotal(), Matchers.is(UnittestHelper.toKiloBytes(536870912)));
        Assert.assertThat("free before", event.getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((536870912 - 401882552))));
        Assert.assertThat("free after", event.getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((536870912 - 457545744))));
        Assert.assertThat("total young before", event.getYoung().getTotal(), Matchers.is(UnittestHelper.toKiloBytes(134217728)));
        Assert.assertThat("young before", event.getYoung().getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes(134217728)));
        Assert.assertThat("young after", event.getYoung().getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((134217728 - 55663192))));
        Assert.assertThat("total tenured before", event.getTenured().getTotal(), Matchers.is(UnittestHelper.toKiloBytes(402653184)));
        Assert.assertThat("tenured before", event.getTenured().getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((402653184 - 401882552))));
        Assert.assertThat("tenured after", event.getTenured().getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((402653184 - 401882552))));
        Assert.assertThat("timestamp 1", event.getTimestamp(), closeTo(0.0, 1.0E-4));
        Assert.assertThat("timestamp 2", model.get(1).getTimestamp(), closeTo(1.272, 1.0E-4));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.equalTo("af scavenge; nursery; tenure"));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void testAfGlobal() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R28_af_global.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(1.255648, 1.0E-7));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.equalTo("af global; tenure"));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void testSysGlobal() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R28_sys_global.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(0.097756, 1.0E-7));
        Assert.assertThat("type", event.getTypeAsString(), Matchers.equalTo("sys explicit global; nursery; tenure"));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void testConcurrentMinimal() throws Exception {
        // there are minimal concurrent blocks, that don't contain any information, that the parser can use (at least, at the moment)
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R28_concurrentMinimal.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(0));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }
}

