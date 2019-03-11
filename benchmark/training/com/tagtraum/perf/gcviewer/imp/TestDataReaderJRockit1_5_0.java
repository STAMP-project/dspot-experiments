/**
 * =================================================
 * Copyright 2006 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.perf.gcviewer.imp;


import Type.JROCKIT_GC;
import com.tagtraum.perf.gcviewer.math.DoubleData;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 * <p>Test DataReaderJRockit1_5_0 implementation.</p>
 *
 * Date: Jan 30, 2002
 * Time: 5:53:55 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestDataReaderJRockit1_5_0 {
    @Test
    public void testGcPrioPausetime() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile("SampleJRockit1_5_12_gcpriopausetime.txt");
        gcResource.getLogger().addHandler(handler);
        DataReader reader = getDataReader1_5(gcResource);
        GCModel model = reader.read();
        Assert.assertEquals("count", 10, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 6.29, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 3128161, event.getPreUsed());
        Assert.assertEquals("after", 296406, event.getPostUsed());
        Assert.assertEquals("total", 3145728, event.getTotal());
        Assert.assertEquals("pause", 0.059084, event.getPause(), 1.0E-7);
        Assert.assertEquals("number of warnings", 6, handler.getCount());
    }

    @Test
    public void testGcPrioThroughput() throws Exception {
        DataReader reader = getDataReader1_5(new GcResourceFile("SampleJRockit1_5_12_gcpriothroughput.txt"));
        GCModel model = reader.read();
        Assert.assertEquals("count", 8, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 4.817, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 1641728, event.getPreUsed());
        Assert.assertEquals("after", 148365, event.getPostUsed());
        Assert.assertEquals("total", 3145728, event.getTotal());
        Assert.assertEquals("pause", 0.039959, event.getPause(), 1.0E-7);
    }

    @Test
    public void testGenCon() throws Exception {
        DataReader reader = getDataReader1_5(new GcResourceFile("SampleJRockit1_5_12_gencon.txt"));
        GCModel model = reader.read();
        Assert.assertEquals("count", 8, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 6.038, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 3089328, event.getPreUsed());
        Assert.assertEquals("after", 352551, event.getPostUsed());
        Assert.assertEquals("total", 3145728, event.getTotal());
        Assert.assertEquals("pause", 0.1186, event.getPause(), 1.0E-7);
    }

    /**
     * This log file sample contains much more information about concurrent events
     * than is currently parsed. Still the parser must be able to extract the information
     * it can parse.
     */
    @Test
    public void testGenConMemstats() throws Exception {
        // allthough this log file was written with JRockit 1.5 VM, it has the same structure
        // as a JRockit 1.6 gc log file.
        // TODO refactor JRockit DataReader
        DataReader reader = getDataReader1_6(new GcResourceFile("SampleJRockit1_5_20_memstats2.txt"));
        GCModel model = reader.read();
        Assert.assertEquals("count", 11, model.size());
    }

    @Test
    public void testGenPar() throws Exception {
        DataReader reader = getDataReader1_5(new GcResourceFile("SampleJRockit1_5_12_genpar.txt"));
        GCModel model = reader.read();
        Assert.assertEquals("count", 17, model.size());
        // 2 types of events excpected: "GC" and "parallel nursery GC"
        Map<String, DoubleData> gcEventPauses = model.getGcEventPauses();
        Assert.assertEquals("2 types of events found", 2, gcEventPauses.entrySet().size());
    }

    /**
     * Test parsing of a malformed type. The test just expects an INFO to be logged - nothing else.
     */
    @Test
    public void testMalformedType() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.INFO);
        GCResource gcResource = new GcResourceFile("byteArray");
        gcResource.getLogger().addHandler(handler);
        ByteArrayInputStream in = new ByteArrayInputStream("[memory ][Thu Feb 21 15:06:38 2013][11844] 6.290-6.424: GC-malformed 3128161K->296406K (3145728K), sum of pauses 59.084 ms".getBytes());
        DataReader reader = new DataReaderJRockit1_5_0(gcResource, in);
        reader.read();
        // 3 INFO events:
        // Reading JRockit ... format
        // Failed to determine type ...
        // Reading done.
        Assert.assertEquals("number of infos", 3, handler.getCount());
        List<LogRecord> logRecords = handler.getLogRecords();
        Assert.assertEquals("should start with 'Failed to determine type'", 0, logRecords.get(1).getMessage().indexOf("Failed to determine type"));
    }

    @Test
    public void testSimpleOpts() throws Exception {
        DataReader reader = getDataReader1_5(new GcResourceFile("SampleJRockit1_5_12-gcreport-simpleopts-singlecon.txt"));
        GCModel model = reader.read();
        Assert.assertEquals("count", 5, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 6.771, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 3145728, event.getPreUsed());
        Assert.assertEquals("after", 296406, event.getPostUsed());
        Assert.assertEquals("total", 3145728, event.getTotal());
        Assert.assertEquals("pause", 0.066, event.getPause(), 1.0E-7);
    }
}

