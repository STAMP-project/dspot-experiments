package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.GO;


public class TestDataReaderGo {
    @Test
    public void test() throws IOException {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        String gcLog = "" + (((("gc starting...\n"// Such a line is not produced by the Go GC; it is just for testing
         + "gc 1 @0.058s 0%: 0+1.9+0 ms clock, 0+0.94/1.9/2.9+0 ms cpu, 4->5->1 MB, 5 MB goal, 4 P\n") + "a line unrelated to GC logging\n") + "gc 2 @0.073s 3%: 68+0.36+0.51 ms clock, 205+0/16/89+1.5 ms cpu, 11111111111111111111111111111111111->84->42 MB, 86 MB goal, 3 P\n") + "gc 58 @17.837s 0%: 0.48+17+0 ms clock, 1.9+9.3/7.9/15+0 ms cpu, 30->30->15 MB, 31 MB goal, 4 P\n");
        ByteArrayInputStream in = new ByteArrayInputStream(gcLog.getBytes("US-ASCII"));
        DataReader reader = new DataReaderGo(gcResource, in);
        GCModel model = reader.read();
        Assert.assertThat("gc 2 -> warning", handler.getCount(), Matchers.is(1));
        Assert.assertThat("size", model.size(), Matchers.is(2));
        AbstractGCEvent<?> event1 = model.get(0);
        Assert.assertThat("timestamp", event1.getTimestamp(), Matchers.closeTo(0.058, 1.0E-4));
        Assert.assertThat("pause", event1.getPause(), Matchers.closeTo((0 + 0), 0.1));
        Assert.assertThat("preused", event1.getPreUsed(), Matchers.is(4096));
        Assert.assertThat("postused", event1.getPostUsed(), Matchers.is(1024));
        Assert.assertThat("heap", event1.getTotal(), Matchers.is(5120));
    }

    @Test
    public void exampleLog() throws IOException {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("go1.9.txt");
        gcResource.getLogger().addHandler(handler);
        InputStream in = UnittestHelper.getResourceAsStream(GO, gcResource.getResourceName());
        DataReader reader = new DataReaderGo(gcResource, in);
        GCModel model = reader.read();
        Assert.assertThat("warnings", handler.getCount(), Matchers.is(0));
        Assert.assertThat("size", model.size(), Matchers.is(635));
    }
}

