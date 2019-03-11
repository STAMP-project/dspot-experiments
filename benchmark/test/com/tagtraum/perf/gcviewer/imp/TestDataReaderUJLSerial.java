package com.tagtraum.perf.gcviewer.imp;


import Type.UJL_PAUSE_FULL;
import Type.UJL_PAUSE_YOUNG;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests unified jvm logging parser for serial gc events.
 */
public class TestDataReaderUJLSerial {
    @Test
    public void parseGcDefaults() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-serial-gc-defaults.txt");
        Assert.assertThat("size", model.size(), Matchers.is(11));
        Assert.assertThat("amount of gc event types", model.getGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of gc events", model.getGCPause().getN(), Matchers.is(8));
        Assert.assertThat("amount of full gc events", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of full gc events", model.getFullGCPause().getN(), Matchers.is(3));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
        AbstractGCEvent<?> event1 = model.get(0);
        Assert.assertThat("event1 type", event1.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_YOUNG.getName()));
        Assert.assertThat("event1 pause", event1.getPause(), Matchers.closeTo(0.011421, 1.0E-5));
        Assert.assertThat("event1 heap before", event1.getPreUsed(), Matchers.is((1024 * 25)));
        Assert.assertThat("event1 heap after", event1.getPostUsed(), Matchers.is((1024 * 23)));
        Assert.assertThat("event1 total heap", event1.getTotal(), Matchers.is((1024 * 92)));
        Assert.assertThat("event1 not isFull", event1.isFull(), Matchers.is(false));
        AbstractGCEvent<?> event2 = model.get(2);
        Assert.assertThat("event2 type", event2.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_FULL.getName()));
        Assert.assertThat("event2 pause", event2.getPause(), Matchers.closeTo(0.007097, 1.0E-5));
        Assert.assertThat("event2 heap before", event2.getPreUsed(), Matchers.is((1024 * 74)));
        Assert.assertThat("event2 heap after", event2.getPostUsed(), Matchers.is((1024 * 10)));
        Assert.assertThat("event2 total heap", event2.getTotal(), Matchers.is((1024 * 92)));
        Assert.assertThat("event2 isFull", event2.isFull(), Matchers.is(true));
        // gc log says GC(10) for this event
        AbstractGCEvent<?> event3 = model.get(9);
        Assert.assertThat("event3 type", event3.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_FULL.getName()));
        // gc log says GC(9) for this event, but comes after GC(10)
        AbstractGCEvent<?> event4 = model.get(10);
        Assert.assertThat("event4 type", event4.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_YOUNG.getName()));
    }

    @Test
    public void parseGcAllSafepointOsCpu() throws Exception {
        GCModel model = getGCModelFromLogFile("sample-ujl-serial-gc-all,safepoint,os+cpu.txt");
        Assert.assertThat("size", model.size(), Matchers.is(4));
        Assert.assertThat("amount of STW GC pause types", model.getGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of STW GC pauses", model.getGCPause().getN(), Matchers.is(3));
        Assert.assertThat("amount of STW Full GC pause types", model.getFullGcEventPauses().size(), Matchers.is(1));
        Assert.assertThat("amount of STW Full GC pauses", model.getFullGCPause().getN(), Matchers.is(1));
        Assert.assertThat("amount of concurrent pause types", model.getConcurrentEventPauses().size(), Matchers.is(0));
        AbstractGCEvent<?> event1 = model.get(0);
        Assert.assertThat("event1 type", event1.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_YOUNG.getName()));
        Assert.assertThat("event1 pause", event1.getPause(), Matchers.closeTo(0.009814, 1.0E-5));
        Assert.assertThat("event1 heap before", event1.getPreUsed(), Matchers.is((1024 * 25)));
        Assert.assertThat("event1 heap after", event1.getPostUsed(), Matchers.is((1024 * 23)));
        Assert.assertThat("event1 total heap", event1.getTotal(), Matchers.is((1024 * 92)));
        // TODO fix timestamps or renderers (seeing the ujl logs, I realise, that the timestamps usually are the end of the event, not the beginning, as I kept thinking)
        // GC(3) Pause Full (Allocation Failure)
        AbstractGCEvent<?> event2 = model.get(2);
        Assert.assertThat("event2 type", event2.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_FULL.getName()));
        Assert.assertThat("event2 pause", event2.getPause(), Matchers.closeTo(0.006987, 1.0E-5));
        Assert.assertThat("event2 time", event2.getTimestamp(), Matchers.closeTo(0.29, 1.0E-4));
        // GC(2) Pause Young (Allocation Failure)
        AbstractGCEvent<?> event3 = model.get(3);
        Assert.assertThat("event3 type", event3.getTypeAsString(), Matchers.startsWith(UJL_PAUSE_YOUNG.getName()));
        Assert.assertThat("event3 pause", event3.getPause(), Matchers.closeTo(0.007118, 1.0E-5));
        Assert.assertThat("event3 time", event3.getTimestamp(), Matchers.closeTo(0.29, 1.0E-4));
    }

    @Test
    public void testParseUnknownLineFormat() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        InputStream in = new ByteArrayInputStream("[0.317s][info][gc            ] GC(11) Pause Young (G1 Evacuation Pause) 122M->113M(128M) unexpected 0.774ms".getBytes());
        DataReader reader = new DataReaderUnifiedJvmLogging(gcResource, in);
        GCModel model = reader.read();
        Assert.assertThat("number of warnings", handler.getCount(), Matchers.is(1));
        Assert.assertThat("warning message", handler.getLogRecords().get(0).getMessage(), Matchers.startsWith("Expected memory and pause in the end of line number"));
    }

    @Test
    public void testParseUnknownGcType() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        InputStream in = new ByteArrayInputStream("[0.317s][info][gc            ] GC(11) Pause Young unknown event".getBytes());
        DataReader reader = new DataReaderUnifiedJvmLogging(gcResource, in);
        GCModel model = reader.read();
        Assert.assertThat("number of warnings", handler.getCount(), Matchers.is(1));
        Assert.assertThat("warning message", handler.getLogRecords().get(0).getMessage(), Matchers.startsWith("Failed to parse gc event ("));
    }
}

