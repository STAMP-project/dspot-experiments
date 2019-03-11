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
public class TestDataReaderIBM_J9_R27 {
    @Test
    public void testFullHeaderWithAfGcs() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R27_SR1_full_header.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(3));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(0.042303, 1.0E-7));
        Assert.assertThat("total before", event.getTotal(), Matchers.is(UnittestHelper.toKiloBytes(1073741824)));
        Assert.assertThat("free before", event.getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((1073741824 - 804158480))));
        Assert.assertThat("free after", event.getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((1073741824 - 912835672))));
        Assert.assertThat("total young before", event.getYoung().getTotal(), Matchers.is(UnittestHelper.toKiloBytes(268435456)));
        Assert.assertThat("young before", event.getYoung().getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes(268435456)));
        Assert.assertThat("young after", event.getYoung().getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((268435456 - 108677192))));
        Assert.assertThat("total tenured before", event.getTenured().getTotal(), Matchers.is(UnittestHelper.toKiloBytes(805306368)));
        Assert.assertThat("tenured before", event.getTenured().getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((805306368 - 804158480))));
        Assert.assertThat("tenured after", event.getTenured().getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((805306368 - 804158480))));
        Assert.assertThat("timestamp 1", event.getTimestamp(), closeTo(0.0, 1.0E-4));
        Assert.assertThat("timestamp 2", model.get(1).getTimestamp(), closeTo(1.927, 1.0E-4));
        Assert.assertThat("timestamp 3", model.get(2).getTimestamp(), closeTo(3.982, 1.0E-4));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(1));
    }

    @Test
    public void testSystemGc() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R27_SR1_global.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(0.075863, 1.0E-7));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }
}

