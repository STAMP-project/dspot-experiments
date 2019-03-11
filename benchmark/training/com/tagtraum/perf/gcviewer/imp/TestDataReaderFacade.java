package com.tagtraum.perf.gcviewer.imp;


import com.tagtraum.perf.gcviewer.UnittestHelper;
import com.tagtraum.perf.gcviewer.model.GCModel;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import com.tagtraum.perf.gcviewer.model.GcResourceSeries;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static com.tagtraum.perf.gcviewer.UnittestHelper.FOLDER.OPENJDK;


/**
 * Tests the implementation of {@link DataReaderFacade}.
 *
 * @author <a href="mailto:gcviewer@gmx.ch">Joerg Wuethrich</a>
<p>created on: 28.11.2012</p>
 */
public class TestDataReaderFacade {
    private static final String SAMPLE_GCLOG_SUN1_6_0 = "SampleSun1_6_0PrintHeapAtGC.txt";

    private static final String PARENT_PATH = ("src/test/resources/" + (OPENJDK.getFolderName())) + "/";

    private DataReaderFacade dataReaderFacade;

    /**
     * Tests {@link DataReaderFacade#loadModel(GCResource)}
     * with filename that does exist.
     */
    @Test
    public void loadModelStringFileExistsNoWarnings() throws Exception {
        TestLogHandler handler = new TestLogHandler();
        handler.setLevel(Level.WARNING);
        GCResource gcResource = new GcResourceFile(((TestDataReaderFacade.PARENT_PATH) + (TestDataReaderFacade.SAMPLE_GCLOG_SUN1_6_0)));
        gcResource.getLogger().addHandler(handler);
        final GCModel model = dataReaderFacade.loadModel(gcResource);
        Assert.assertEquals("has no errors", 0, handler.getCount());
        Assert.assertNotNull("Model returned", model);
        Assert.assertNotNull("Model returned contains URL", model.getURL());
    }

    /**
     * Tests {@link DataReaderFacade#loadModel(GCResource)}
     * with a malformed url.
     */
    @Test
    public void loadModelMalformedUrl() throws Exception {
        try {
            dataReaderFacade.loadModel(new GcResourceFile("httpblabla"));
        } catch (DataReaderException e) {
            Assert.assertNotNull("cause", e.getCause());
            Assert.assertEquals("expected exception in cause", MalformedURLException.class.getName(), e.getCause().getClass().getName());
        }
    }

    /**
     * Tests {@link DataReaderFacade#loadModel(GCResource)}
     * with a malformed url.
     */
    @Test
    public void loadModelIllegalArgument() throws Exception {
        try {
            dataReaderFacade.loadModel(new GcResourceFile("http://"));
        } catch (DataReaderException e) {
            Assert.assertNotNull("cause", e.getCause());
            Assert.assertEquals("expected exception in cause", IllegalArgumentException.class.getName(), e.getCause().getClass().getName());
        }
    }

    /**
     * Tests {@link DataReaderFacade#loadModel(GCResource)}
     * with filename that does not exist.
     */
    @Test
    public void loadModelFileDoesntExists() throws Exception {
        try {
            dataReaderFacade.loadModel(new GcResourceFile("dummy.txt"));
            Assert.fail("DataReaderException expected");
        } catch (DataReaderException e) {
            Assert.assertNotNull("cause", e.getCause());
            Assert.assertEquals("expected exception in cause", FileNotFoundException.class.getName(), e.getCause().getClass().getName());
        }
    }

    @Test
    public void testLoadModel_forSeries() throws DataReaderException, IOException {
        GCResource file1 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part1.txt").getPath());
        GCResource file2 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part2.txt").getPath());
        GCResource file3 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part3.txt").getPath());
        GCResource file4 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part4.txt").getPath());
        GCResource file5 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part5.txt").getPath());
        GCResource file6 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part6.txt").getPath());
        GCResource file7 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part7.txt").getPath());
        GCResource expectedResult = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-ManuallyMerged.txt").getPath());
        GCModel expectedModel = dataReaderFacade.loadModel(expectedResult);
        List<GCResource> resources = new ArrayList<>();
        resources.add(file4);
        resources.add(file3);
        resources.add(file6);
        resources.add(file1);
        resources.add(file7);
        resources.add(file2);
        resources.add(file5);
        GcResourceSeries series = new GcResourceSeries(resources);
        GCModel result = dataReaderFacade.loadModelFromSeries(series);
        Assert.assertThat(result.toString(), CoreMatchers.is(expectedModel.toString()));
    }

    @Test
    public void testLoadModelFromSeries() throws DataReaderException, IOException {
        GCResource file1 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part1.txt").getPath());
        GCResource file2 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part2.txt").getPath());
        GCResource file3 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part3.txt").getPath());
        GCResource file4 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part4.txt").getPath());
        GCResource file5 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part5.txt").getPath());
        GCResource file6 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part6.txt").getPath());
        GCResource file7 = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-Part7.txt").getPath());
        GCResource expectedResult = new GcResourceFile(UnittestHelper.getResource(OPENJDK, "SampleSun1_8_0Series-ManuallyMerged.txt").getPath());
        GCModel expectedModel = dataReaderFacade.loadModel(expectedResult);
        List<GCResource> resources = new ArrayList<>();
        resources.add(file4);
        resources.add(file3);
        resources.add(file6);
        resources.add(file1);
        resources.add(file7);
        resources.add(file2);
        resources.add(file5);
        GcResourceSeries series = new GcResourceSeries(resources);
        GCModel result = dataReaderFacade.loadModel(series);
        Assert.assertThat(result.toString(), CoreMatchers.is(expectedModel.toString()));
    }
}

