package com.tagtraum.perf.gcviewer;


import DataWriterType.CSV;
import DataWriterType.CSV_TS;
import DataWriterType.SUMMARY;
import com.tagtraum.perf.gcviewer.model.GCResource;
import com.tagtraum.perf.gcviewer.model.GcResourceFile;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the class {@link com.tagtraum.perf.gcviewer.GCViewerArgsParser}
 * - makes sure that argument inputs parse correctly
 *
 * @author <a href="mailto:smendenh@redhat.com">Samuel Mendenhall</a>
<p>created on: 06.09.2014</p>
 */
public class TestGCViewerArgsParser {
    @Test
    public void noArguments() throws Exception {
        String[] args = new String[]{  };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 0);
        Assert.assertEquals(gcViewerArgsParser.getType(), SUMMARY);
    }

    @Test
    public void onlyGCLog() throws Exception {
        String[] args = new String[]{ "some_gc.log" };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 1);
        Assert.assertEquals(gcViewerArgsParser.getGcResource(), new GcResourceFile("some_gc.log"));
        Assert.assertEquals(gcViewerArgsParser.getType(), SUMMARY);
    }

    @Test
    public void onlyGcLogSeries() throws Exception {
        String[] args = new String[]{ "some_gc.log.0;some_gc.log.1;some_gc.log.2" };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 1);
        List<GCResource> resources = Arrays.asList(new GcResourceFile("some_gc.log.0"), new GcResourceFile("some_gc.log.1"), new GcResourceFile("some_gc.log.2"));
        Assert.assertEquals(gcViewerArgsParser.getGcResource(), new com.tagtraum.perf.gcviewer.model.GcResourceSeries(resources));
        Assert.assertEquals(gcViewerArgsParser.getType(), SUMMARY);
    }

    @Test
    public void gcAndExportFile() throws Exception {
        String[] args = new String[]{ "some_gc.log", "export_to.csv" };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 2);
        Assert.assertEquals(gcViewerArgsParser.getGcResource(), new GcResourceFile("some_gc.log"));
        Assert.assertEquals(gcViewerArgsParser.getSummaryFilePath(), "export_to.csv");
        Assert.assertEquals(gcViewerArgsParser.getType(), SUMMARY);
    }

    @Test
    public void gcSeriesAndExportFile() throws Exception {
        String[] args = new String[]{ "some_gc.log.0;some_gc.log.1;some_gc.log.2", "export_to.csv" };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 2);
        List<GCResource> resources = Arrays.asList(new GcResourceFile("some_gc.log.0"), new GcResourceFile("some_gc.log.1"), new GcResourceFile("some_gc.log.2"));
        Assert.assertEquals(gcViewerArgsParser.getGcResource(), new com.tagtraum.perf.gcviewer.model.GcResourceSeries(resources));
        Assert.assertEquals(gcViewerArgsParser.getSummaryFilePath(), "export_to.csv");
        Assert.assertEquals(gcViewerArgsParser.getType(), SUMMARY);
    }

    @Test
    public void onlyType() throws Exception {
        String[] args = new String[]{ "-t", "CSV_TS" };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 0);
        Assert.assertEquals(gcViewerArgsParser.getType(), CSV_TS);
    }

    @Test
    public void allInitialArgsWithType() throws Exception {
        String[] args = new String[]{ "some_gc.log", "export_to.csv", "the_chart.png", "-t", "CSV" };
        GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
        gcViewerArgsParser.parseArguments(args);
        Assert.assertEquals(gcViewerArgsParser.getArgumentCount(), 3);
        Assert.assertEquals(gcViewerArgsParser.getGcResource(), new GcResourceFile("some_gc.log"));
        Assert.assertEquals(gcViewerArgsParser.getSummaryFilePath(), "export_to.csv");
        Assert.assertEquals(gcViewerArgsParser.getChartFilePath(), "the_chart.png");
        Assert.assertEquals(gcViewerArgsParser.getType(), CSV);
    }

    @Test
    public void illegalType() {
        String[] args = new String[]{ "some_gc.log", "export_to.csv", "the_chart.png", "-t", "ILLEGAL" };
        try {
            GCViewerArgsParser gcViewerArgsParser = new GCViewerArgsParser();
            gcViewerArgsParser.parseArguments(args);
            Assert.fail("GCVIewerArgsParserException expected");
        } catch (GCViewerArgsParserException e) {
            Assert.assertThat("exception message", e.getMessage(), Matchers.startsWith("Illegal type 'ILLEGAL'"));
        }
    }
}

