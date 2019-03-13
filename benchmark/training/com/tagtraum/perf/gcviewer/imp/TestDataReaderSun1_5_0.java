package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

import static GcLogType.SUN1_5;


/**
 * Tests some cases for java 1.5 (using DataReaderSun1_6_0).
 *
 * Date: Jan 30, 2002
 * Time: 5:53:55 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class TestDataReaderSun1_5_0 {
    /**
     * Test output for -XX:+PrintAdaptiveSizePolicy
     */
    @Test
    public void testAdaptiveSizePolicy() throws Exception {
        String fileName = "SampleSun1_5_0AdaptiveSizePolicy.txt";
        final InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("number of events", 6, model.getPause().getN());
        Assert.assertEquals("number of full gcs", 1, model.getFullGCPause().getN());
        Assert.assertEquals("number of gcs", 5, model.getGCPause().getN());
        Assert.assertEquals("total pause", 0.1024222, model.getPause().getSum(), 1.0E-6);
        Assert.assertEquals("full gc pause", 0.0583435, model.getFullGCPause().getSum(), 1.0E-6);
        Assert.assertEquals("gc pause", 0.0440787, model.getGCPause().getSum(), 1.0E-6);
    }

    @Test
    public void testCMSPrintGCDetails() throws Exception {
        String fileName = "SampleSun1_5_0CMS_PrintGCDetails.txt";
        final InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("size", 515, model.size());
        Assert.assertEquals("throughput", 88.2823289184, model.getThroughput(), 1.0E-8);
        Assert.assertEquals("sum of pauses", model.getPause().getSum(), ((model.getFullGCPause().getSum()) + (model.getGCPause().getSum())), 1.0E-7);
        Assert.assertEquals("total pause", 9.1337492, model.getPause().getSum(), 1.0E-7);
        Assert.assertEquals("full gc pause", 7.4672903, model.getFullGCPause().getSum(), 1.0E-8);
    }

    @Test
    public void testParallelOldGC() throws Exception {
        String fileName = "SampleSun1_5_0ParallelOldGC.txt";
        final InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("size", 1, model.size());
        Assert.assertEquals("gc pause", 27.0696262, model.getFullGCPause().getMax(), 1.0E-6);
    }

    @Test
    public void testCMSIncrementalPacing() throws Exception {
        String fileName = "SampleSun1_5_0CMS_IncrementalPacing.txt";
        final InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("size", 810, model.size());
        Assert.assertEquals("throughput", 94.181240109114, model.getThroughput(), 1.0E-8);
        Assert.assertEquals("total gc pause", 2.3410947, model.getPause().getSum(), 1.0E-9);
        Assert.assertEquals("gc pause", 2.3410947, model.getGCPause().getSum(), 1.0E-9);
        Assert.assertEquals("full gc paus", 0.0, model.getFullGCPause().getSum(), 0.01);
    }

    @Test
    public void testPromotionFailure() throws Exception {
        String fileName = "SampleSun1_5_0PromotionFailure.txt";
        final InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("size", 6, model.size());
        Assert.assertEquals("throughput", 98.0937624615, model.getThroughput(), 1.0E-8);
        Assert.assertEquals("gc pause", 8.413616, model.getPause().getSum(), 1.0E-6);
    }

    @Test
    public void testCMSConcurrentModeFailure() throws Exception {
        String fileName = "SampleSun1_5_0ConcurrentModeFailure.txt";
        final InputStream in = getInputStream(fileName);
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile(fileName), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("size", 3417, model.size());
        Assert.assertEquals("throughput", 78.558339113, model.getThroughput(), 1.0E-8);
        Assert.assertEquals("gc pause", 181.8116798, model.getPause().getSum(), 1.0E-9);
    }

    @Test
    public void testCmsScavengeBeforeRemark() throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(("0.871: [GC[YG occupancy: 16241 K (16320 K)]0.871: [Scavenge-Before-Remark0.871: [Full GC 0.871: [ParNew: 16241K->0K(16320K), 0.0311294 secs] 374465K->374455K(524224K), 0.0312110 secs]" + ("\n, 0.0312323 secs]" + "\n0.902: [Rescan (parallel) , 0.0310561 secs]0.933: [weak refs processing, 0.0000152 secs] [1 CMS-remark: 374455K(507904K)] 374455K(524224K), 0.0624207 secs]")).getBytes());
        final DataReader reader = new DataReaderSun1_6_0(new GcResourceFile("byteArray"), in, SUN1_5);
        GCModel model = reader.read();
        Assert.assertEquals("gc count", 2, model.size());
        Assert.assertEquals("full gc pause", 0.031211, model.getFullGCPause().getMax(), 1.0E-6);
        Assert.assertEquals("remark pause", (0.0624207 - 0.031211), model.getGCPause().getMax(), 1.0E-9);
    }
}

