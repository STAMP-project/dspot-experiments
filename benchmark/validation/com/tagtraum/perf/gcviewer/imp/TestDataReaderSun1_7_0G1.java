package com.tagtraum.perf.gcviewer.imp;


import Type.G1_CONCURRENT_MARK_START;
import Type.G1_YOUNG_INITIAL_MARK;
import Type.G1_YOUNG_INITIAL_MARK_TO_SPACE_EXHAUSTED;
import Type.G1_YOUNG_TO_SPACE_EXHAUSTED_INITIAL_MARK;
import com.tagtraum.perf.gcviewer.math.DoubleData;
import com.tagtraum.perf.gcviewer.model.ConcurrentGCEvent;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static GcLogType.SUN1_7G1;


public class TestDataReaderSun1_7_0G1 {
    @Test
    public void youngPause_u1() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0-01_G1_young.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause", 0.00631825, model.getPause().getMax(), 1.0E-9);
        Assert.assertEquals("heap", (64 * 1024), model.getHeapAllocatedSizes().getMax());
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    /**
     * Parse memory format of java 1.7.0_u2.
     */
    @Test
    public void youngPause_u2() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0-02_G1_young.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause", 0.144822, model.getPause().getMax(), 1.0E-9);
        GCEvent heap = ((GCEvent) (model.getEvents().next()));
        Assert.assertEquals("heap before", (1105 * 1024), heap.getPreUsed());
        Assert.assertEquals("heap after", (380 * 1024), heap.getPostUsed());
        Assert.assertEquals("heap", ((2 * 1024) * 1024), heap.getTotal());
        GCEvent young = model.getGCEvents().next().getYoung();
        Assert.assertNotNull("young", young);
        Assert.assertEquals("young before", (1024 * 1024), young.getPreUsed());
        Assert.assertEquals("young after", (128 * 1024), young.getPostUsed());
        Assert.assertEquals("young total", ((896 + 128) * 1024), young.getTotal());
        GCEvent tenured = model.getGCEvents().next().getTenured();
        Assert.assertNotNull("tenured", tenured);
        Assert.assertEquals("tenured before", ((1105 - 1024) * 1024), tenured.getPreUsed());
        Assert.assertEquals("tenured after", ((380 - 128) * 1024), tenured.getPostUsed());
        Assert.assertEquals("tenured total", (1024 * 1024), tenured.getTotal());
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void youngPauseDateStamp_u2() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_02_G1_young_datestamp.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause", 0.144822, model.getPause().getMax(), 1.0E-9);
        GCEvent heap = ((GCEvent) (model.getEvents().next()));
        Assert.assertEquals("heap", (1105 * 1024), heap.getPreUsed());
        Assert.assertEquals("heap", (380 * 1024), heap.getPostUsed());
        Assert.assertEquals("heap", (2048 * 1024), heap.getTotal());
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    /**
     * Test parsing GC logs that have PrintAdaptiveSizePolicy turned on
     */
    @Test
    public void printAdaptiveSizePolicy() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_12PrintAdaptiveSizePolicy.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause", 0.158757, model.getPause().getMax(), 1.0E-9);
        GCEvent heap = ((GCEvent) (model.getEvents().next()));
        Assert.assertEquals("heap", ((65 * 1024) * 1024), heap.getPreUsed());
        // test parsing of decimal values
        Assert.assertEquals("heap", ((64.3 * 1024) * 1024), heap.getPostUsed(), 100.0);
        Assert.assertEquals("heap", ((92.0 * 1024) * 1024), heap.getTotal(), 100.0);
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    /**
     * Test parsing GC logs that have PrintGCCause turned on
     */
    @Test
    public void printGCCause() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_40PrintGCCause.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("gc pause", 0.077938, model.getPause().getMax(), 1.0E-9);
        GCEvent heap = ((GCEvent) (model.getEvents().next()));
        Assert.assertEquals("heap", (32 * 1024), heap.getPreUsed());
        // test parsing of decimal values
        Assert.assertEquals("heap", 7136, ((double) (heap.getPostUsed())), 100.0);
        Assert.assertEquals("heap", ((88.0 * 1024) * 1024), ((double) (heap.getTotal())), 100.0);
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    /**
     * Test parsing GC logs that have PrintGCCause turned on
     */
    @Test
    public void printGCCause2() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_40PrintGCCause2.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("type name", "GC pause (G1 Evacuation Pause) (young) (to-space exhausted)", event.getTypeAsString());
        Assert.assertEquals("gc pause", 0.0398848, model.getPause().getMax(), 1.0E-9);
    }

    @Test
    public void testDetailedCollectionDatestampMixed1() throws Exception {
        // parse one detailed event with a mixed line (concurrent event starts in the middle of an stw collection)
        // 2012-02-24T03:49:09.100-0800: 312.402: [GC pause (young)2012-02-24T03:49:09.378-0800: 312.680: [GC concurrent-mark-start]
        // (initial-mark), 0.28645100 secs]
        final DataReader reader = getDataReader("SampleSun1_7_0G1_DateStamp_Detailed-mixedLine1.txt");
        GCModel model = reader.read();
        Assert.assertEquals("nummber of events", 2, model.size());
        Assert.assertEquals("number of pauses", 1, model.getPause().getN());
        Assert.assertEquals("gc pause sum", 0.286451, model.getPause().getSum(), 1.0E-9);
        Assert.assertEquals("gc memory", ((17786 * 1024) - (17147 * 1024)), model.getFreedMemoryByGC().getMax());
    }

    @Test
    public void testDetailedCollectionDatestampMixed2() throws Exception {
        // parse one detailed event with a mixed line (concurrent event starts in the middle of an stw collection)
        // 2012-02-24T03:50:08.274-0800: 371.576: [GC pause (young)2012-02-24T03:50:08.554-0800:  (initial-mark), 0.28031200 secs]
        // 371.856:    [Parallel Time: 268.0 ms]
        // [GC concurrent-mark-start]
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1_DateStamp_Detailed-mixedLine2.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 3, model.size());
        Assert.assertEquals("number of warnings", 0, handler.getCount());
        Assert.assertEquals("concurrent event type", G1_CONCURRENT_MARK_START.toString(), model.getConcurrentGCEvents().next().getTypeAsString());
        Assert.assertEquals("number of pauses", 2, model.getPause().getN());
        Assert.assertEquals("gc pause max", 0.280312, model.getPause().getMax(), 1.0E-9);
        Assert.assertEquals("gc memory", ((20701 * 1024) - (20017 * 1024)), model.getFreedMemoryByGC().getMax());
    }

    @Test
    public void testDetailedCollectionDatestampMixed3() throws Exception {
        // parse one detailed event with a mixed line
        // -> concurrent event occurs somewhere in the detail lines below the stw event
        DataReader reader = getDataReader("SampleSun1_7_0G1_DateStamp_Detailed-mixedLine3.txt");
        GCModel model = reader.read();
        Assert.assertEquals("nummber of events", 2, model.size());
        Assert.assertEquals("concurrent event type", G1_CONCURRENT_MARK_START.toString(), model.getConcurrentGCEvents().next().getTypeAsString());
        Assert.assertEquals("number of pauses", 1, model.getPause().getN());
        Assert.assertEquals("gc pause sum", 0.088949, model.getPause().getSum(), 1.0E-9);
        Assert.assertEquals("gc memory", ((29672 * 1024) - (28733 * 1024)), model.getFreedMemoryByGC().getMax());
    }

    @Test
    public void applicationStoppedMixedLine() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        InputStream in = new ByteArrayInputStream(("2012-07-26T14:58:54.045+0200: Total time for which application threads were stopped: 0.0078335 seconds" + "\n3.634: [GC concurrent-root-region-scan-start]").getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(gcResource, in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertThat("count", model.size(), Matchers.is(2));
        Assert.assertThat("gc type (0)", model.get(0).getTypeAsString(), Matchers.equalTo("Total time for which application threads were stopped"));
        Assert.assertThat("gc timestamp (0)", model.get(0).getTimestamp(), Matchers.closeTo(0.0, 0.01));
        Assert.assertThat("gc type (1)", model.get(1).getTypeAsString(), Matchers.equalTo("GC concurrent-root-region-scan-start"));
        Assert.assertThat("gc timestamp (1)", model.get(1).getTimestamp(), Matchers.closeTo(3.634, 0.01));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void applicationTimeMixed() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        InputStream in = new ByteArrayInputStream(("2012-07-26T15:24:21.845+0200: 3.100: [GC concurrent-root-region-scan-end, 0.0000680]" + ("\n2012-07-26T14:58:58.320+0200Application time: 0.0000221 seconds" + "\n: 7.907: [GC concurrent-mark-start]")).getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(gcResource, in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertEquals("count", 2, model.size());
        Assert.assertThat("gc type (0)", "GC concurrent-root-region-scan-end", Matchers.equalTo(model.get(0).getTypeAsString()));
        Assert.assertThat("gc timestamp (0)", model.get(0).getTimestamp(), Matchers.closeTo(3.1, 0.01));
        Assert.assertThat("gc type (1)", "GC concurrent-mark-start", Matchers.equalTo(model.get(1).getTypeAsString()));
        // should be 7.907, but line starts with ":", so timestamp of previous event is taken
        Assert.assertThat("gc timestamp (1)", model.get(1).getTimestamp(), Matchers.closeTo(3.1, 1.0E-4));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void eventNoMemory() throws Exception {
        // there are (rarely) events, where the memory information could not be parsed,
        // because the line with the memory information was mixed with another event
        // looks like this:    [251.448:  213M->174M(256M)[GC concurrent-mark-start]
        // (produced using -XX:+PrintGcDetails -XX:+PrintHeapAtGC)
        GCEvent event = new GCEvent();
        event.setType(G1_YOUNG_INITIAL_MARK);
        event.setTimestamp(0.5);
        event.setPause(0.2);
        // but no memory information -> all values zero there
        GCModel model = new GCModel();
        model.add(event);
        DoubleData initiatingOccupancyFraction = model.getCmsInitiatingOccupancyFraction();
        Assert.assertEquals("fraction", 0, initiatingOccupancyFraction.getSum(), 0.1);
    }

    @Test
    public void gcRemark() throws Exception {
        final InputStream in = new ByteArrayInputStream(("0.197: [GC remark 0.197: [GC ref-proc, 0.0000070 secs], 0.0005297 secs]" + "\n [Times: user=0.00 sys=0.00, real=0.00 secs]").getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(new GcResourceFile("byteArray"), in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        Assert.assertEquals("gc pause", 5.297E-4, model.getGCPause().getMax(), 1.0E-6);
    }

    @Test
    public void gcRemarkWithDateTimeStamp() throws Exception {
        final InputStream in = new ByteArrayInputStream(("2013-09-08T22:11:22.639+0000: 52131.385: [GC remark 2013-09-08T22:11:22.640+0000: 52131.386: [GC ref-proc, 0.0120750 secs], 0.0347170 secs]\n" + " [Times: user=0.43 sys=0.00, real=0.03 secs] \n").getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(new GcResourceFile("byteArray"), in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        Assert.assertEquals("gc pause", 0.034717, model.getGCPause().getMax(), 1.0E-6);
    }

    @Test
    public void printTenuringDistribution() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1TenuringDistribution.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 11, model.size());
        Assert.assertEquals("number of concurrent events", 4, model.getConcurrentEventPauses().size());
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void printApplicationTimePrintTenuringDistribution() throws Exception {
        // test parsing when the following options are set:
        // -XX:+PrintTenuringDistribution (output ignored)
        // -XX:+PrintGCApplicationStoppedTime (output ignored)
        // -XX:+PrintGCApplicationConcurrentTime (output ignored)
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_02PrintApplicationTimeTenuringDistribution.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 9, model.size());
        Assert.assertEquals("number of concurrent events", 2, model.getConcurrentEventPauses().size());
        GCEvent youngEvent = ((GCEvent) (model.get(0)));
        Assert.assertEquals("gc pause (young)", 0.00784501, youngEvent.getPause(), 1.0E-9);
        Assert.assertEquals("heap (young)", (20 * 1024), youngEvent.getTotal());
        GCEvent partialEvent = ((GCEvent) (model.get(7)));
        Assert.assertEquals("gc pause (partial)", 0.02648319, partialEvent.getPause(), 1.0E-9);
        Assert.assertEquals("heap (partial)", (128 * 1024), partialEvent.getTotal());
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    /**
     * Usually "cleanup" events have memory information; if it doesn't the parser should just continue
     */
    @Test
    public void simpleLogCleanUpNoMemory() throws Exception {
        final InputStream in = new ByteArrayInputStream(("2013-06-22T18:58:45.955+0200: 1.433: [Full GC 128M->63M(128M), 0.0385026 secs]" + (("\n2013-06-22T18:58:45.986+0200: 1.472: [GC cleanup, 0.0000004 secs]" + "\n2013-06-22T18:58:45.986+0200: 1.472: [GC concurrent-mark-abort]") + "\n2013-06-22T18:58:46.002+0200: 1.483: [GC pause (young) 91M->90M(128M), 0.0128787 secs]")).getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(new GcResourceFile("bytearray"), in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertEquals("count", 4, model.size());
        // the order of the events is in fact wrong; but for the sake of simplicity in the parser I accept it
        Assert.assertEquals("cleanup event", "GC cleanup", model.get(2).getTypeAsString());
        Assert.assertEquals("concurrent-mark-abort event", "GC concurrent-mark-abort", model.get(1).getTypeAsString());
    }

    @Test
    public void printHeapAtGcWithConcurrentEvents() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1_PrintHeapAtGC_withConcurrent.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 3, model.size());
        Assert.assertEquals("number of concurrent events", 2, model.getConcurrentEventPauses().size());
        ConcurrentGCEvent concurrentEvent = ((ConcurrentGCEvent) (model.get(0)));
        Assert.assertEquals("GC concurrent-root-region-scan-end expected", "GC concurrent-root-region-scan-end", concurrentEvent.getTypeAsString());
        concurrentEvent = ((ConcurrentGCEvent) (model.get(1)));
        Assert.assertEquals("GC concurrent-mark-start expected", "GC concurrent-mark-start", concurrentEvent.getTypeAsString());
        GCEvent fullGcEvent = ((GCEvent) (model.get(2)));
        Assert.assertEquals("full gc", "Full GC", fullGcEvent.getTypeAsString());
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }

    @Test
    public void printPrintHeapAtGC() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_40PrintHeapAtGC.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("type name", "GC pause (G1 Evacuation Pause) (young)", event.getTypeAsString());
        Assert.assertEquals("gc pause", 0.0147015, model.getPause().getMax(), 1.0E-9);
        Assert.assertEquals("error count", 0, handler.getCount());
    }

    @Test
    public void printAdaptiveSizePolicyPrintReferencePolicy() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1AdaptiveSize_Reference.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("count", model.size(), Matchers.is(3));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type name", event.getTypeAsString(), Matchers.equalTo("GC pause (G1 Evacuation Pause) (young)"));
        Assert.assertThat("gc pause", event.getPause(), Matchers.closeTo(0.0107924, 1.0E-8));
        GCEvent event2 = ((GCEvent) (model.get(1)));
        Assert.assertThat("type name 2", event2.getTypeAsString(), Matchers.equalTo("GC pause (young)"));
        Assert.assertThat("gc pause 2", event2.getPause(), Matchers.closeTo(0.0130642, 1.0E-8));
        GCEvent event3 = ((GCEvent) (model.get(2)));
        Assert.assertThat("type name 3", event3.getTypeAsString(), Matchers.equalTo("GC remark; GC ref-proc"));
        Assert.assertThat("gc pause 3", event3.getPause(), Matchers.closeTo(0.0013608, 1.0E-8));
        Assert.assertThat("error count", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void printReferencePolicy() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1PrintReferencePolicy.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("count", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type name", event.getTypeAsString(), Matchers.equalTo("GC pause (young)"));
        Assert.assertThat("gc pause", event.getPause(), Matchers.closeTo(0.0049738, 1.0E-8));
        Assert.assertThat("error count", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void printDetailsWithoutTimestamp() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1_DetailsWoTimestamp.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("count", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type name", event.getTypeAsString(), Matchers.equalTo("GC pause (young)"));
        Assert.assertThat("gc pause", event.getPause(), Matchers.closeTo(0.005531, 1.0E-8));
        Assert.assertThat("error count", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void printSimpleToSpaceOverflow() throws Exception {
        InputStream in = new ByteArrayInputStream("2013-12-21T15:48:44.062+0100: 0.441: [GC pause (G1 Evacuation Pause) (young)-- 90M->94M(128M), 0.0245257 secs]".getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(new GcResourceFile("bytearray"), in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertEquals("count", 1, model.size());
        Assert.assertEquals("gc pause", 0.0245257, model.getGCPause().getMax(), 1.0E-6);
    }

    @Test
    public void pauseWithComma() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1_PauseWithComma.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("count", model.size(), Matchers.is(1));
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertThat("type name", event.getTypeAsString(), Matchers.equalTo("GC pause (young)"));
        Assert.assertThat("gc pause", event.getPause(), Matchers.closeTo(0.066567, 1.0E-8));
        Assert.assertThat("error count", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void testYoungToSpaceOverflowInitialMarkOriginalOrder() throws Exception {
        verifyYoungToSpaceOverflowInitialMarkMixedOrder("SampleSun1_7_0_G1_young_initial_mark_to_space_exhausted_original_order.txt", G1_YOUNG_TO_SPACE_EXHAUSTED_INITIAL_MARK);
    }

    @Test
    public void testYoungToSpaceOverflowInitialMarkReverseOrder() throws Exception {
        verifyYoungToSpaceOverflowInitialMarkMixedOrder("SampleSun1_7_0_G1_young_initial_mark_to_space_exhausted_reverse_order.txt", G1_YOUNG_INITIAL_MARK_TO_SPACE_EXHAUSTED);
    }

    @Test
    public void printGCApplicationStoppedTimeTenuringDist() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0_51_G1_PrintApplicationTimeTenuringDistribution.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("GC count", model.size(), Matchers.is(3));
        // standard event
        Assert.assertThat("type name (0)", model.get(0).getTypeAsString(), Matchers.equalTo("GC pause (young)"));
        Assert.assertThat("GC pause (0)", model.get(0).getPause(), Matchers.closeTo(7.112E-4, 1.0E-8));
        // "application stopped" (as overhead added to previous event)
        Assert.assertThat("type name (1)", model.get(1).getTypeAsString(), Matchers.equalTo("Total time for which application threads were stopped"));
        Assert.assertThat("GC pause (1)", model.get(1).getPause(), Matchers.closeTo((8.648E-4 - 7.112E-4), 1.0E-8));
        // standalone "application stopped", without immediate GC event before
        Assert.assertThat("type name (2)", model.get(2).getTypeAsString(), Matchers.equalTo("Total time for which application threads were stopped"));
        Assert.assertThat("GC pause (2)", model.get(2).getPause(), Matchers.closeTo(6.94E-5, 1.0E-8));
        Assert.assertThat("total pause", model.getPause().getSum(), Matchers.closeTo(9.342E-4, 1.0E-8));
        Assert.assertThat("throughput", model.getThroughput(), Matchers.closeTo(99.88663576, 1.0E-6));
        Assert.assertThat("number of parse problems", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void printGCApplicationStoppedTimeTenuringDistErgonomicsComma() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1_AppStopped_TenuringDist_Ergonomics_comma.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("GC count", model.size(), Matchers.is(3));
        // standalone "application stopped"
        Assert.assertThat("type name (0)", model.get(0).getTypeAsString(), Matchers.equalTo("Total time for which application threads were stopped"));
        Assert.assertThat("GC pause (0)", model.get(0).getPause(), Matchers.closeTo(3.06E-4, 1.0E-8));
        // standard event
        Assert.assertThat("type name (0)", model.get(1).getTypeAsString(), Matchers.equalTo("GC pause (G1 Evacuation Pause) (young)"));
        Assert.assertThat("GC pause (0)", model.get(1).getPause(), Matchers.closeTo(0.02822, 1.0E-8));
        // standalone "application stopped", without immediate GC event before
        Assert.assertThat("type name (2)", model.get(2).getTypeAsString(), Matchers.equalTo("Total time for which application threads were stopped"));
        Assert.assertThat("GC pause (2)", model.get(2).getPause(), Matchers.closeTo((0.029212 - 0.02822), 1.0E-8));
        Assert.assertThat("number of parse problems", handler.getCount(), Matchers.is(0));
    }

    @Test
    public void printGCApplicationStoppedMixed() throws Exception {
        // TODO relax G1 parser: don't warn, when 2 timestamps are in the beginning of a line
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_7_0G1_MixedApplicationStopped.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader(gcResource);
        GCModel model = reader.read();
        Assert.assertThat("GC count", model.size(), Matchers.is(5));
        // last "application stopped"
        Assert.assertThat("type name", model.get(4).getTypeAsString(), Matchers.equalTo("Total time for which application threads were stopped"));
        Assert.assertThat("GC pause", model.get(4).getPause(), Matchers.closeTo(0.001546, 1.0E-8));
        Assert.assertThat("number of parse problems", handler.getCount(), Matchers.is(1));
    }

    @Test
    public void printAdaptiveSizePolicyFullGc() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        InputStream in = new ByteArrayInputStream(("2014-08-03T13:33:50.932+0200: 0.992: [Full GC0.995: [SoftReference, 34 refs, 0.0000090 secs]0.995: [WeakReference, 0 refs, 0.0000016 secs]0.996: [FinalReference, 4 refs, 0.0000020 secs]0.996: [PhantomReference, 0 refs, 0.0000012 secs]0.996: [JNI Weak Reference, 0.0000016 secs] 128M->63M(128M), 0.0434091 secs]" + "\n [Times: user=0.03 sys=0.00, real=0.03 secs] ").getBytes());
        DataReader reader = new DataReaderSun1_6_0G1(gcResource, in, SUN1_7G1);
        GCModel model = reader.read();
        Assert.assertThat("gc pause", model.getFullGCPause().getMax(), Matchers.closeTo(0.0434091, 1.0E-9));
        GCEvent heap = ((GCEvent) (model.getEvents().next()));
        Assert.assertThat("heap", heap.getTotal(), Matchers.is((128 * 1024)));
        Assert.assertThat("number of errors", handler.getCount(), Matchers.is(0));
    }
}

