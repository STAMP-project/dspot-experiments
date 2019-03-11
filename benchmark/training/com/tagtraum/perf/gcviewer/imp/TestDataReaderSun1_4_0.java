package com.tagtraum.perf.gcviewer.imp;


import AbstractGCEvent.Type;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;

import static GcLogType.SUN1_4;


/**
 * Tests some cases for java 1.4 (using DataReaderSun1_6_0).
 *
 * Date: Jan 30, 2002
 * Time: 5:53:55 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestDataReaderSun1_4_0 {
    /**
     * Test output for -XX:+PrintAdaptiveSizePolicy
     */
    @Test
    public void testAdaptiveSizePolicy() throws Exception {
        String fileName = "SampleSun1_4_0AdaptiveSizePolicy.txt";
        InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_4);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 9, model.getPause().getN());
        Assert.assertEquals("number of full gcs", 3, model.getFullGCPause().getN());
        Assert.assertEquals("number of gcs", 6, model.getGCPause().getN());
        Assert.assertEquals("total pause", 0.1978746, model.getPause().getSum(), 1.0E-6);
        Assert.assertEquals("full gc pause", 0.026889, model.getFullGCPause().getSum(), 1.0E-6);
        Assert.assertEquals("gc pause", 0.1709856, model.getGCPause().getSum(), 1.0E-6);
    }

    @Test
    public void testParse1() throws Exception {
        // original testcase was written with timestamp "2.23492e-006d" as first timestamp
        // I have never seen a timestamp writte in scientific format in the logfiles, so
        // I assume, that was some experiment here in the unittest
        AbstractGCEvent<GCEvent> event1 = new GCEvent(0, 8968, 8230, 10912, 0.0037192, Type.GC);
        event1.getGeneration();
        AbstractGCEvent<GCEvent> event2 = new GCEvent(1, 8968, 8230, 10912, 0.0037192, Type.GC);
        event2.getGeneration();
        AbstractGCEvent<GCEvent> event3 = new GCEvent(2, 8968, 8230, 10912, 0.0037192, Type.GC);
        event3.getGeneration();
        AbstractGCEvent<GCEvent> event4 = new GCEvent(3, 10753, 6046, 10912, 0.3146707, Type.FULL_GC);
        event4.getGeneration();
        AbstractGCEvent<GCEvent> event5 = new GCEvent(4, 10753, 6046, 10912, 0.3146707, Type.INC_GC);
        event5.getGeneration();
        AbstractGCEvent<GCEvent> event6 = new GCEvent(5, 52471, 22991, 75776, 1.0754938, Type.GC);
        event6.getGeneration();
        ByteArrayInputStream in = new ByteArrayInputStream("0.0: [GC 8968K->8230K(10912K), 0.0037192 secs]\r\n1.0: [GC 8968K->8230K(10912K), 0.0037192 secs]\r\n2.0: [GC 8968K->8230K(10912K), 0.0037192 secs]\r\n3.0: [Full GC 10753K->6046K(10912K), 0.3146707 secs]\r\n4.0: [Inc GC 10753K->6046K(10912K), 0.3146707 secs]\r\n5.0: [GC Desired survivor size 3342336 bytes, new threshold 1 (max 32) - age   1:  6684672 bytes,  6684672 total 52471K->22991K(75776K), 1.0754938 secs]".getBytes());
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile("byteArray"), in, SUN1_4);
        GCModel model = reader.read();
        Assert.assertEquals("model size", 6, model.size());
        Iterator<AbstractGCEvent<?>> i = model.getStopTheWorldEvents();
        AbstractGCEvent<?> event = i.next();
        Assert.assertEquals("event 1", event, event1);
        event = i.next();
        Assert.assertEquals("event 2", event, event2);
        event = i.next();
        Assert.assertEquals("event 3", event, event3);
        event = i.next();
        Assert.assertEquals("event 4", event, event4);
        event = i.next();
        Assert.assertEquals("event 5", event, event5);
        event = i.next();
        Assert.assertEquals("event 6", event, event6);
        Assert.assertEquals("running time", (5 + 1.0754938), model.getRunningTime(), 1.0E-4);
        Assert.assertEquals("throughput", 71.75550076275, model.getThroughput(), 1.0E-7);
    }

    @Test
    public void testNoFullGC() throws Exception {
        String fileName = "SampleSun1_4_2NoFullGC.txt";
        InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_4);
        GCModel model = reader.read();
        // we just look at the first six...
        /* 0.000: [GC 511K->180K(1984K), 0.0095672 secs]
        0.691: [GC 433K->233K(1984K), 0.0056869 secs]
        1.030: [GC 745K->242K(1984K), 0.0043429 secs]
        1.378: [GC 753K->452K(1984K), 0.0094429 secs]
        2.499: [GC 964K->690K(1984K), 0.0108058 secs]
        2.831: [GC 1202K->856K(1984K), 0.0122599 secs]
         */
        AbstractGCEvent<GCEvent> event1 = new GCEvent(0.0, 511, 180, 1984, 0.0095672, Type.GC);
        event1.getGeneration();
        AbstractGCEvent<GCEvent> event2 = new GCEvent(0.691, 433, 233, 1984, 0.0056869, Type.GC);
        event2.getGeneration();
        AbstractGCEvent<GCEvent> event3 = new GCEvent(1.03, 745, 242, 1984, 0.0043429, Type.GC);
        event3.getGeneration();
        AbstractGCEvent<GCEvent> event4 = new GCEvent(1.378, 753, 452, 1984, 0.0094429, Type.GC);
        event4.getGeneration();
        AbstractGCEvent<GCEvent> event5 = new GCEvent(2.499, 964, 690, 1984, 0.0108058, Type.GC);
        event5.getGeneration();
        AbstractGCEvent<GCEvent> event6 = new GCEvent(2.831, 1202, 856, 1984, 0.0122599, Type.GC);
        event6.getGeneration();
        Assert.assertEquals("model size", 12, model.size());
        Iterator<GCEvent> i = model.getGCEvents();
        AbstractGCEvent<GCEvent> event = i.next();
        Assert.assertEquals("event 1", event, event1);
        event = i.next();
        Assert.assertEquals("event 2", event, event2);
        event = i.next();
        Assert.assertEquals("event 3", event, event3);
        event = i.next();
        Assert.assertEquals("event 4", event, event4);
        event = i.next();
        Assert.assertEquals("event 5", event, event5);
        event = i.next();
        Assert.assertEquals("event 6", event, event6);
        Assert.assertEquals("throughput", 98.928592417159, model.getThroughput(), 1.0E-11);
    }

    @Test
    public void testPrintGCDetails() throws Exception {
        String fileName = "SampleSun1_4_2PrintGCDetails.txt";
        InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_4);
        GCModel model = reader.read();
        /* 0.000: [GC 0.000: [DefNew: 1534K->128K(1664K), 0.0082759 secs] 1534K->276K(16256K), 0.0084272 secs]
        11.653: [Full GC 11.653: [Tenured: 5634K->5492K(14592K), 0.2856516 secs] 6560K->5492K(16256K), 0.2858561 secs]
        22.879: [GC 22.879: [DefNew: 1855K->125K(1856K), 0.0099038 secs]22.889: [Tenured: 14638K->9916K(14720K), 0.8038262 secs] 16358K->9916K(16576K), 0.8142078 secs]
        31.788: [Full GC 31.788: [Tenured: 16141K->13914K(16528K), 0.8032950 secs] 17881K->13914K(18640K), 0.8036514 secs]
         */
        GCEvent event1 = new GCEvent(0.0, 1534, 276, 16256, 0.0084272, Type.GC);
        GCEvent childEvent1 = new GCEvent(0.0, 1534, 128, 1664, 0.0082759, Type.DEF_NEW);
        childEvent1.getGeneration();
        event1.add(childEvent1);
        event1.getGeneration();
        GCEvent event2 = new GCEvent(11.653, 6560, 5492, 16256, 0.2858561, Type.FULL_GC);
        GCEvent childEvent2 = new GCEvent(11.653, 5634, 5492, 14592, 0.2856516, Type.TENURED);
        event2.add(childEvent2);
        event2.getGeneration();
        GCEvent event3 = new GCEvent(22.879, 16358, 9916, 16576, 0.8142078, Type.GC);
        GCEvent childEvent3a = new GCEvent(22.879, 1855, 125, 1856, 0.0099038, Type.DEF_NEW);
        event3.add(childEvent3a);
        GCEvent childEvent3b = new GCEvent(22.889, 14638, 9916, 14720, 0.8038262, Type.TENURED);
        event3.add(childEvent3b);
        event3.getGeneration();
        GCEvent event4 = new GCEvent(31.788, 17881, 13914, 18640, 0.8036514, Type.FULL_GC);
        GCEvent childEvent4 = new GCEvent(31.788, 16141, 13914, 16528, 0.803295, Type.TENURED);
        event4.add(childEvent4);
        event4.getGeneration();
        Assert.assertEquals("model.size()", 4, model.size());
        Iterator<AbstractGCEvent<?>> i = model.getStopTheWorldEvents();
        AbstractGCEvent<?> event = i.next();
        Assert.assertEquals("event 1", event1, event);
        event = i.next();
        Assert.assertEquals("event 2", event2, event);
        event = i.next();
        Assert.assertEquals("event 3", event3, event);
        event = i.next();
        Assert.assertEquals("event 4", event4, event);
        Assert.assertEquals("throughput", 94.133029724, model.getThroughput(), 1.0E-6);
    }

    @Test
    public void testPrintHeapAtGC() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleSun1_4_0PSPrintHeapAtGC.txt");
        gcResource.getLogger().addHandler(handler);
        InputStream in = getInputStream(gcResource.getResourceName());
        final DataReader reader = new DataReaderSun1_6_0(gcResource, in, SUN1_4);
        GCModel model = reader.read();
        Assert.assertEquals("GC count", 2, model.size());
        Assert.assertEquals("GC pause", 0.0083579, model.getGCPause().getMax(), 1.0E-8);
        Assert.assertEquals("Full GC pause", 0.0299536, model.getFullGCPause().getMax(), 1.0E-8);
        Assert.assertEquals("number of errors", 0, handler.getCount());
    }
}

