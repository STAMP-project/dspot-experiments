package org.opengrok.indexer.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.web.Statistics;
import org.opengrok.indexer.web.org.opengrok.indexer.web.Statistics;


public class StatisticsUtilsTest {
    @Test
    public void testLoadEmptyStatistics() throws IOException {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        String json = "{}";
        try (InputStream in = new StringInputStream(json)) {
            StatisticsUtils.loadStatistics(in);
        }
        Assert.assertEquals(new org.opengrok.indexer.web.Statistics().toJson(), env.getStatistics().toJson());
    }

    @Test
    public void testLoadStatistics() throws IOException {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        String json = "{" + ((((((((((((((((((((((((((((((((("\"requests_per_minute_max\":3," + "\"timing\":{") + "\"*\":2288,") + "\"xref\":53,") + "\"root\":2235") + "},") + "\"minutes\":756,") + "\"timing_min\":{") + "\"*\":2,") + "\"xref\":2,") + "\"root\":2235") + "},") + "\"timing_avg\":{") + "\"*\":572.0,") + "\"xref\":17.666666666666668,") + "\"root\":2235.0") + "},") + "\"request_categories\":{") + "\"*\":4,") + "\"xref\":3,") + "\"root\":1") + "},") + "\"day_histogram\":[0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,1],") + "\"requests\":4,") + "\"requests_per_minute_min\":1,") + "\"requests_per_minute\":3,") + "\"requests_per_minute_avg\":0.005291005291005291,") + "\"month_histogram\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,0],") + "\"timing_max\":{") + "\"*\":2235,") + "\"xref\":48,") + "\"root\":2235") + "}") + "}");
        try (InputStream in = new StringInputStream(json)) {
            StatisticsUtils.loadStatistics(in);
        }
        org.opengrok.indexer.web.Statistics stats = env.getStatistics();
        Assert.assertNotNull(stats);
        Assert.assertEquals(756, stats.getMinutes());
        Assert.assertEquals(4, stats.getRequests());
        Assert.assertEquals(3, stats.getRequestsPerMinute());
        Assert.assertEquals(1, stats.getRequestsPerMinuteMin());
        Assert.assertEquals(3, stats.getRequestsPerMinuteMax());
        Assert.assertEquals(0.005291005291005291, stats.getRequestsPerMinuteAvg(), 5.0E-5);
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, stats.getDayHistogram());
        Assert.assertArrayEquals(new long[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0 }, stats.getMonthHistogram());
        Assert.assertEquals(createMap(new Object[][]{ new Object[]{ "*", 4L }, new Object[]{ "xref", 3L }, new Object[]{ "root", 1L } }), stats.getRequestCategories());
        Assert.assertEquals(createMap(new Object[][]{ new Object[]{ "*", 2288L }, new Object[]{ "xref", 53L }, new Object[]{ "root", 2235L } }), stats.getTiming());
        Assert.assertEquals(createMap(new Object[][]{ new Object[]{ "*", 2L }, new Object[]{ "xref", 2L }, new Object[]{ "root", 2235L } }), stats.getTimingMin());
        Assert.assertEquals(createMap(new Object[][]{ new Object[]{ "*", 2235L }, new Object[]{ "xref", 48L }, new Object[]{ "root", 2235L } }), stats.getTimingMax());
    }

    @Test(expected = IOException.class)
    public void testLoadInvalidStatistics() throws IOException {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        String json = "{ malformed json with missing bracket";
        try (InputStream in = new StringInputStream(json)) {
            StatisticsUtils.loadStatistics(in);
        }
    }

    @Test
    public void testSaveStatistics() throws IOException {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        env.setStatistics(new Statistics());
        env.getStatistics().addRequest();
        env.getStatistics().addRequest("root");
        env.getStatistics().addRequestTime("root", 10L);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            StatisticsUtils.saveStatistics(out);
            Assert.assertNotEquals("{}", out.toString());
            Assert.assertEquals(env.getStatistics().toJson(), out.toString());
        }
    }

    @Test(expected = IOException.class)
    public void testSaveNullStatistics() throws IOException {
        RuntimeEnvironment.getInstance().setStatisticsFilePath(null);
        StatisticsUtils.saveStatistics();
    }

    @Test(expected = IOException.class)
    public void testSaveNullStatisticsFile() throws IOException {
        StatisticsUtils.saveStatistics(((File) (null)));
    }

    @Test(expected = IOException.class)
    public void testLoadNullStatistics() throws IOException {
        RuntimeEnvironment.getInstance().setStatisticsFilePath(null);
        StatisticsUtils.loadStatistics();
    }

    @Test(expected = IOException.class)
    public void testLoadNullStatisticsFile() throws IOException {
        StatisticsUtils.loadStatistics(((File) (null)));
    }
}

