package org.stagemonitor.core.elasticsearch;


import java.util.GregorianCalendar;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.AbstractElasticsearchTest;


public class IndexSelectorIntegrationTest extends AbstractElasticsearchTest {
    private IndexSelector indexSelector;

    @Test
    public void testDeleteOldIndices() throws Exception {
        setTime(0L);
        AbstractElasticsearchTest.elasticsearchClient.sendAsJson("POST", "/stagemonitor-metrics-1969.12.30/metrics", "{\"foo\":\"bar\"}");
        AbstractElasticsearchTest.elasticsearchClient.sendAsJson("POST", "/stagemonitor-metrics-1969.12.31/metrics", "{\"foo\":\"bar\"}");
        AbstractElasticsearchTest.elasticsearchClient.sendAsJson("POST", "/stagemonitor-metrics-1970.01.01/metrics", "{\"foo\":\"bar\"}");
        AbstractElasticsearchTest.refresh();
        Assert.assertEquals(getIndices("stagemonitor-metrics*"), asSet("stagemonitor-metrics-1969.12.30", "stagemonitor-metrics-1969.12.31", "stagemonitor-metrics-1970.01.01"));
        AbstractElasticsearchTest.elasticsearchClient.deleteIndices(indexSelector.getIndexPatternOlderThanDays("stagemonitor-metrics-", 1));
        AbstractElasticsearchTest.refresh();
        Assert.assertEquals(getIndices("stagemonitor-metrics*"), asSet("stagemonitor-metrics-1969.12.31", "stagemonitor-metrics-1970.01.01"));
    }

    @Test
    public void testDeleteOldIndicesUnavailable() throws Exception {
        setTime(new GregorianCalendar(1970, 0, 10, 1, 0, 0).getTimeInMillis());
        AbstractElasticsearchTest.elasticsearchClient.sendAsJson("POST", "/stagemonitor-metrics-1970.01.07/metrics", "{\"foo\":\"bar\"}");
        AbstractElasticsearchTest.elasticsearchClient.sendAsJson("POST", "/stagemonitor-metrics-1970.01.09/metrics", "{\"foo\":\"bar\"}");
        AbstractElasticsearchTest.elasticsearchClient.sendAsJson("POST", "/stagemonitor-metrics-1970.01.10/metrics", "{\"foo\":\"bar\"}");
        AbstractElasticsearchTest.refresh();
        Assert.assertEquals(asSet("stagemonitor-metrics-1970.01.07", "stagemonitor-metrics-1970.01.09", "stagemonitor-metrics-1970.01.10"), getIndices("stagemonitor-metrics*"));
        AbstractElasticsearchTest.elasticsearchClient.deleteIndices(indexSelector.getIndexPatternOlderThanDays("stagemonitor-metrics-", 1));
        AbstractElasticsearchTest.refresh();
        Assert.assertEquals(asSet("stagemonitor-metrics-1970.01.09", "stagemonitor-metrics-1970.01.10"), getIndices("stagemonitor-metrics*"));
    }
}

