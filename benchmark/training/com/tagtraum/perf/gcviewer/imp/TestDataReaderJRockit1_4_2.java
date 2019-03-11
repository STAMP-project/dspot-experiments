package com.tagtraum.perf.gcviewer.imp;


import Type.JROCKIT_GC;
import Type.JROCKIT_NURSERY_GC;
import com.tagtraum.perf.gcviewer.model.GCEvent;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Date: Jan 30, 2002
 * Time: 5:53:55 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestDataReaderJRockit1_4_2 {
    @Test
    public void testParseGenCon() throws Exception {
        // TODO refactor JRockit DataReader
        DataReader reader = getDataReader1_5("SampleJRockit1_4_2gencon.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 123, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 77.737, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_NURSERY_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 630435, event.getPreUsed());
        Assert.assertEquals("after", 183741, event.getPostUsed());
        Assert.assertEquals("total", 1048576, event.getTotal());
        Assert.assertEquals("pause", 0.566158, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseGenConBig() throws Exception {
        // TODO refactor JRockit DataReader
        DataReader reader = getDataReader1_5("SampleJRockit1_4_2gencon-big.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 32420, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 9.385, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_NURSERY_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 123930, event.getPreUsed());
        Assert.assertEquals("after", 27087, event.getPostUsed());
        Assert.assertEquals("total", 1048576, event.getTotal());
        Assert.assertEquals("pause", 0.053417, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseParallel() throws Exception {
        // TODO refactor JRockit DataReader
        DataReader reader = getDataReader1_5("SampleJRockit1_4_2parallel.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 92, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 226.002, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 1048576, event.getPreUsed());
        Assert.assertEquals("after", 133204, event.getPostUsed());
        Assert.assertEquals("total", 1048576, event.getTotal());
        Assert.assertEquals("pause", 0.544511, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParsePrioPauseTime() throws Exception {
        // TODO refactor JRockit DataReader
        DataReader reader = getDataReader1_5("SampleJRockit1_4_2priopausetime.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 1867, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 12.622, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 320728, event.getPreUsed());
        Assert.assertEquals("after", 130908, event.getPostUsed());
        Assert.assertEquals("total", 358400, event.getTotal());
        Assert.assertEquals("pause", 0.025921, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseTsGCReportGencon() throws Exception {
        DataReader reader = getDataReader1_4("SampleJRockit1_4_2ts-gcreport-gencon.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 63, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 13.594, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_NURSERY_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 13824, event.getPreUsed());
        Assert.assertEquals("after", 4553, event.getPostUsed());
        Assert.assertEquals("total", 32768, event.getTotal());
        Assert.assertEquals("pause", 0.028308, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseTsGCReportParallel() throws Exception {
        DataReader reader = getDataReader1_4("SampleJRockit1_4_2ts-gcreport-parallel.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 31, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 20.547, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 32768, event.getPreUsed());
        Assert.assertEquals("after", 5552, event.getPostUsed());
        Assert.assertEquals("total", 32768, event.getTotal());
        Assert.assertEquals("pause", 0.072, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseTsGCReportPrioPauseTime() throws Exception {
        String fileName = "SampleJRockit1_4_2ts-gcreport-gcpriopausetime.txt";
        InputStream in = getInputStream(fileName);
        DataReader reader = new DataReaderFactory().getDataReader(new GcResourceFile(fileName), in);
        Assert.assertTrue((("should be DataReaderJRockit1_4_2 (but was " + (reader.toString())) + ")"), (reader instanceof DataReaderJRockit1_4_2));
        GCModel model = reader.read();
        Assert.assertEquals("count", 64, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 18.785, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 32260, event.getPreUsed());
        Assert.assertEquals("after", 4028, event.getPostUsed());
        Assert.assertEquals("total", 32768, event.getTotal());
        Assert.assertEquals("pause", 0.024491, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseTsGCReportPrioThroughput() throws Exception {
        DataReader reader = getDataReader1_4("SampleJRockit1_4_2ts-gcreport-gcpriothroughput.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 70, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 20.021, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 32768, event.getPreUsed());
        Assert.assertEquals("after", 5561, event.getPostUsed());
        Assert.assertEquals("total", 32768, event.getTotal());
        Assert.assertEquals("pause", 0.061, event.getPause(), 1.0E-7);
    }

    @Test
    public void testParseTsGCReportSinglecon() throws Exception {
        DataReader reader = getDataReader1_4("SampleJRockit1_4_2ts-gcreport-singlecon.txt");
        GCModel model = reader.read();
        Assert.assertEquals("count", 41, model.size());
        GCEvent event = ((GCEvent) (model.get(0)));
        Assert.assertEquals("timestamp", 18.906, event.getTimestamp(), 1.0E-6);
        Assert.assertEquals("name", JROCKIT_GC.getName(), event.getExtendedType().getName());
        Assert.assertEquals("before", 32260, event.getPreUsed());
        Assert.assertEquals("after", 3997, event.getPostUsed());
        Assert.assertEquals("total", 32768, event.getTotal());
        Assert.assertEquals("pause", 0.020149, event.getPause(), 1.0E-7);
    }
}

