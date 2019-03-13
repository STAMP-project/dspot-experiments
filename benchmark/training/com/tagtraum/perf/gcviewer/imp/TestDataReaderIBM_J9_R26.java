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
 * Tests some J9_R26 sample files against the IBM J9 parser.
 */
public class TestDataReaderIBM_J9_R26 {
    @Test
    public void testFullHeaderWithAfGcs() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R26_GAFP1_full_header.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(0.00529, 1.0E-7));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(1));
    }

    @Test
    public void testSystemGc() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleIBMJ9_R26_GAFP1_global.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("model size", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("pause", event.getPause(), closeTo(0.036392, 1.0E-7));
        Assert.assertThat("total before", event.getTotal(), Matchers.is(UnittestHelper.toKiloBytes(514064384)));
        Assert.assertThat("free before", event.getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((514064384 - 428417552))));
        Assert.assertThat("free after", event.getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((514064384 - 479900360))));
        Assert.assertThat("total young before", event.getYoung().getTotal(), Matchers.is(UnittestHelper.toKiloBytes(111411200)));
        Assert.assertThat("young before", event.getYoung().getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((111411200 - 29431656))));
        Assert.assertThat("young after", event.getYoung().getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((111411200 - 80831520))));
        Assert.assertThat("total tenured before", event.getTenured().getTotal(), Matchers.is(UnittestHelper.toKiloBytes(402653184)));
        Assert.assertThat("tenured before", event.getTenured().getPreUsed(), Matchers.is(UnittestHelper.toKiloBytes((402653184 - 398985896))));
        Assert.assertThat("tenured after", event.getTenured().getPostUsed(), Matchers.is(UnittestHelper.toKiloBytes((402653184 - 399068840))));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }
}

