package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static GcLogType.SUN1_8;


/**
 * Test logs generated specifically by JDK 1.8 G1 algorithm.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 22.07.2014</p>
 */
public class TestDataReaderSun1_8_0G1 {
    @Test
    public void fullConcurrentCycle() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1_ConcurrentCycle.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("size", model.size(), Matchers.is(10));
        Assert.assertThat("tenured size after concurrent cycle", model.getPostConcurrentCycleTenuredUsedSizes().getMax(), Matchers.is(((31949 - (10 * 1024)) - 3072)));
        Assert.assertThat("heap size after concurrent cycle", model.getPostConcurrentCycleHeapUsedSizes().getMax(), Matchers.is(31949));
        Assert.assertThat("initiatingOccupancyFraction", model.getCmsInitiatingOccupancyFraction().getMax(), Matchers.closeTo(0.69, 0.001));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    /**
     * In java 8, suddenly the full gc events in G1 got detailed information about the generation
     * sizes again. Test, that they are parsed correctly.
     */
    @Test
    public void fullGcWithDetailedSizes() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("2014-07-24T13:49:45.090+0400: 92457.841: [Full GC (Allocation Failure)  5811M->3097M(12G), 8.9862292 secs]" + ("\n  [Eden: 4096.0K(532.0M)->0.0B(612.0M) Survivors: 80.0M->0.0B Heap: 5811.9M(12.0G)->3097.8M(12.0G)], [Metaspace: 95902K->95450K(1140736K)]" + "\n [Times: user=12.34 sys=0.22, real=8.99 secs]")).getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("footprint", event.getTotal(), Matchers.is(((12 * 1024) * 1024)));
        Assert.assertThat("yound before", event.getYoung().getPreUsed(), Matchers.is((4096 + (80 * 1024))));
        Assert.assertThat("tenured", event.getTenured().getTotal(), Matchers.is((((12 * 1024) * 1024) - (612 * 1024))));
        Assert.assertThat("metaspace", event.getPerm().getTotal(), Matchers.is(1140736));
        Assert.assertThat("perm", model.getPermAllocatedSizes().getN(), Matchers.is(1));
        Assert.assertThat("warning count", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void printGCCauseTenuringDistribution() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1PrintGCCausePrintTenuringDistribution.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause sum", 16.7578613, model.getPause().getSum(), 1.0E-9);
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void printHeapAtGC() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1PrintHeapAtGc.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause sum", 0.0055924, model.getPause().getSum(), 1.0E-9);
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void humongousMixed() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1HumongousMixed.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("number of events", model.size(), Matchers.is(1));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(2));
    }

    @Test
    public void extendedRemark() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1extended-remark.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("number of events", model.size(), Matchers.is(1));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
        Assert.assertThat("pause duration", model.get(0).getPause(), Matchers.closeTo(0.100522, 1.0E-8));
    }

    @Test
    public void youngInitialMarkSystemGc() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1SystemGcYoung.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("number of events", model.size(), Matchers.is(1));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
        Assert.assertThat("pause duration", model.get(0).getPause(), Matchers.closeTo(0.2124664, 1.0E-8));
        Assert.assertThat("is system gc", model.get(0).isSystem(), Matchers.is(true));
    }

    @Test
    public void youngInitialMarkMetadataThreshold() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0G1YoungMetadataGcThreshold.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("number of events", model.size(), Matchers.is(1));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
        Assert.assertThat("pause duration", model.get(0).getPause(), Matchers.closeTo(0.0229931, 1.0E-8));
    }

    @Test
    public void doubleTimeStamp() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream("176020.306: 176020.306: [GC concurrent-root-region-scan-start]".getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("size", model.size(), Matchers.is(0));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(1));
        Assert.assertThat("log message contains line number", handler.getLogRecords().get(0).getMessage(), Matchers.containsString("Line"));
        Assert.assertThat("log message contains log line", handler.getLogRecords().get(0).getMessage(), Matchers.containsString("176020.306: 176020.306"));
    }
}

