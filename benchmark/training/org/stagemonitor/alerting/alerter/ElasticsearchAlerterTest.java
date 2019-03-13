package org.stagemonitor.alerting.alerter;


import CheckResult.Status.ERROR;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.alerting.incident.Incident;


public class ElasticsearchAlerterTest extends AbstractElasticsearchTest {
    public AbstractAlerterTest abstractAlerterTest = new AbstractAlerterTest();

    private ElasticsearchAlerter elasticsearchAlerter;

    private AlertSender alertSender;

    @Test
    public void testAlert() throws Exception {
        Incident incident = alertSender.sendTestAlert(abstractAlerterTest.createSubscription(elasticsearchAlerter), ERROR);
        refresh();
        Collection<Incident> allIncidents = elasticsearchClient.getAll("/stagemonitor/alerts", 10, Incident.class);
        Assert.assertEquals(1, allIncidents.size());
        Assert.assertEquals(incident, allIncidents.iterator().next());
    }
}

