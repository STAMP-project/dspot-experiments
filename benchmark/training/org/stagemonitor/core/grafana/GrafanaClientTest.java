package org.stagemonitor.core.grafana;


import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;


public class GrafanaClientTest {
    private GrafanaClient grafanaClient;

    @Test
    public void sendGrafanaDashboardAsync() throws Exception {
        final JsonNode dashboard = grafanaClient.getGrafanaDashboard("grafana/ElasticsearchCustomMetricsDashboard.json");
        boolean intervalFound = false;
        for (JsonNode template : dashboard.get("templating").get("list")) {
            if ("Interval".equals(template.get("name").textValue())) {
                intervalFound = true;
                Assert.assertEquals("60s", template.get("auto_min").textValue());
            }
        }
        Assert.assertTrue(intervalFound);
    }
}

