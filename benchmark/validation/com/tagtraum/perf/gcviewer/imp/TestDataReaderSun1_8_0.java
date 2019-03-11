package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static GcLogType.SUN1_8;


/**
 * Test logs generated specifically by java 1.8.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 25.09.2013</p>
 */
public class TestDataReaderSun1_8_0 {
    @Test
    public void parallelPrintHeapAtGC() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0ParallelPrintHeapAtGC.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc pause sum", model.getPause().getSum(), Matchers.closeTo(0.0103603, 1.0E-9));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void scavengeBeforeRemarkPrintHeapAtGC_YGOccupancy() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0CMS_ScavengeBeforeRemark_HeapAtGc.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(2));
        Assert.assertThat("scavenge before remark event", model.get(0).getPause(), Matchers.closeTo(7.78E-5, 1.0E-9));
        Assert.assertThat("remark event", model.get(1).getPause(), Matchers.closeTo((0.001997 - 7.78E-5), 1.0E-9));
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void scavengeBeforeRemark_HeapAtGC_PrintTenuringDistribution_PrintFLSStats() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0CMS_ScavengeBR_HeapAtGC_TenuringDist_PrintFLS.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(2));
        Assert.assertThat("scavenge before remark event", model.get(0).getPause(), Matchers.closeTo(0.1306264, 1.0E-9));
        Assert.assertThat("remark event", model.get(1).getPause(), Matchers.closeTo((0.1787717 - 0.1306264), 1.0E-9));
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void parallelPrintTenuringGcCause() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0Parallel_Tenuring_PrintGCCause.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(5));
        Assert.assertThat("gc name", model.get(0).getTypeAsString(), Matchers.equalTo("GC (Allocation Failure); PSYoungGen"));
        Assert.assertThat("pause", model.get(0).getPause(), Matchers.closeTo(0.0199218, 1.0E-9));
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void parallelApple() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0Parallel_Apple.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(6));
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void cmsPrintHeapBeforeFullGc() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0CMS_HeadDumpBeforeFullGc.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(2));
        Assert.assertThat("gc name concurrent", model.get(0).getTypeAsString(), Matchers.equalTo("CMS-concurrent-mark"));
        Assert.assertThat("gc name full gc", model.get(1).getTypeAsString(), Matchers.equalTo("Full GC (GCLocker Initiated GC); CMS (concurrent mode failure); Metaspace"));
        Assert.assertThat("pause", model.get(1).getPause(), Matchers.closeTo(218.692881, 1.0E-9));
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void shenandoahPauseMark() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("13.976: [Pause Init Mark, 3.587 ms]" + "\n13.992: [Pause Final Mark 1447M->684M(2048M), 2.279 ms]").getBytes());
        DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(2));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
        AbstractGCEvent<?> initMarkEvent = model.get(0);
        Assert.assertThat("Pause init mark: name", initMarkEvent.getTypeAsString(), Matchers.equalTo("Pause Init Mark"));
        Assert.assertThat("Pause init mark: duration", initMarkEvent.getPause(), Matchers.closeTo(0.003587, 1.0E-5));
        AbstractGCEvent<?> finalMarkEvent = model.get(1);
        Assert.assertThat("Pause final mark: name", finalMarkEvent.getTypeAsString(), Matchers.equalTo("Pause Final Mark"));
        Assert.assertThat("Pause final mark: duration", finalMarkEvent.getPause(), Matchers.closeTo(0.002279, 1.0E-5));
        Assert.assertThat("Pause final mark: before", finalMarkEvent.getPreUsed(), Matchers.is((1447 * 1024)));
    }

    @Test
    public void shenandoahPauseUpdateRefs() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("14.001: [Pause Init Update Refs, 0.073 ms]" + "\n14.016: [Pause Final Update Refs 726M->60M(2048M), 0.899 ms]").getBytes());
        DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(2));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
        AbstractGCEvent<?> initUpdateRefsEvent = model.get(0);
        Assert.assertThat("Pause init update refs: name", initUpdateRefsEvent.getTypeAsString(), Matchers.equalTo("Pause Init Update Refs"));
        Assert.assertThat("Pause init update refs: duration", initUpdateRefsEvent.getPause(), Matchers.closeTo(7.3E-5, 1.0E-7));
        AbstractGCEvent<?> finalUpdateRefsEvent = model.get(1);
        Assert.assertThat("Pause Final Update Refs: name", finalUpdateRefsEvent.getTypeAsString(), Matchers.equalTo("Pause Final Update Refs"));
        Assert.assertThat("Pause Final Update Refs: duration", finalUpdateRefsEvent.getPause(), Matchers.closeTo(8.99E-4, 1.0E-5));
        Assert.assertThat("Pause Final Update Refs: before", finalUpdateRefsEvent.getPreUsed(), Matchers.is((726 * 1024)));
    }

    @Test
    public void shehandoahConcurrentEvents() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("13.979: [Concurrent marking 1435M->1447M(2048M), 12.576 ms]" + ((("\n13.994: [Concurrent evacuation 684M->712M(2048M), 6.041 ms]" + "\n14.001: [Concurrent update references  713M->726M(2048M), 14.718 ms]") + "\n14.017: [Concurrent reset bitmaps 60M->62M(2048M), 0.294 ms]") + "\n626.259: [Cancel concurrent mark, 0.056 ms]\n")).getBytes());
        DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(5));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
        AbstractGCEvent<?> concurrentMarking = model.get(0);
        Assert.assertThat("Concurrent Marking: name", concurrentMarking.getTypeAsString(), Matchers.equalTo("Concurrent marking"));
        Assert.assertThat("Concurrent Marking: duration", concurrentMarking.getPause(), Matchers.closeTo(0.012576, 1.0E-7));
        Assert.assertThat("Concurrent Marking: before", concurrentMarking.getPreUsed(), Matchers.is((1435 * 1024)));
        Assert.assertThat("Concurrent Marking: after", concurrentMarking.getPostUsed(), Matchers.is((1447 * 1024)));
        Assert.assertThat("Concurrent Marking: total", concurrentMarking.getTotal(), Matchers.is((2048 * 1024)));
    }

    @Test
    public void shenandoahIgnoredLines() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("Uncommitted 87M. Heap: 2048M reserved, 1961M committed, 992M used" + "\nCancelling concurrent GC: Allocation Failure").getBytes());
        DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(0));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void shenandoaPauseInitMarkDetails() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("Capacity: 262144M, Peak Occupancy: 222063M, Lowest Free: 40080M, Free Threshold: 7864M\n" + (("Uncommitted 1184M. Heap: 262144M reserved, 223616M committed, 213270M used\n" + "Periodic GC triggered. Time since last GC: 300004 ms, Guaranteed Interval: 300000 ms\n") + "347584.988: [Pause Init Mark, 3.942 ms]\n")).getBytes());
        DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(1));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
        AbstractGCEvent<?> initMarkEvent = model.get(0);
        Assert.assertThat("Pause init mark: duration", initMarkEvent.getPause(), Matchers.closeTo(0.003942, 1.0E-5));
    }

    @Test
    public void shenandoahPauseFinalMarkDetails() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream(("346363.391: [Pause Final MarkTotal Garbage: 54870M" + (((("\nImmediate Garbage: 0M, 0 regions (0% of total)" + "\nGarbage to be collected: 8900M (16% of total), 281 regions") + "\nLive objects to be evacuated: 85M") + "\nLive/garbage ratio in collected regions: 0%") + "\n 216G->216G(256G), 14.095 ms]")).getBytes());
        DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_8);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(1));
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
        AbstractGCEvent<?> finalMarkEvent = model.get(0);
        Assert.assertThat("name", finalMarkEvent.getTypeAsString(), Matchers.equalTo("Pause Final Mark"));
        Assert.assertThat("duration", finalMarkEvent.getPause(), Matchers.closeTo(0.014095, 1.0E-9));
        Assert.assertThat("before", finalMarkEvent.getPreUsed(), Matchers.is(((216 * 1024) * 1024)));
    }

    @Test
    public void shenandoahDetailsShutdown() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.INFO);
        GCResource gcResource = new GcResourceFile("SampleSun1_8_0ShenandoahDetailsShutdown.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("gc count", model.size(), Matchers.is(0));
        Assert.assertThat("number of errors", handler.getLogRecords().stream().filter(( logRecord) -> !(logRecord.getLevel().equals(Level.INFO))).count(), Matchers.is(0L));
        Assert.assertThat("contains GC STATISTICS", handler.getLogRecords().stream().filter(( logRecord) -> logRecord.getMessage().startsWith("GC STATISTICS")).count(), Matchers.is(1L));
    }
}

