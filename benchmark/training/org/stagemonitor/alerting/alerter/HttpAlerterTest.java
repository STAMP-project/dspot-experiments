package org.stagemonitor.alerting.alerter;


import CheckResult.Status.ERROR;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.alerting.incident.Incident;


public class HttpAlerterTest extends AbstractElasticsearchTest {
    public AbstractAlerterTest abstractAlerterTest = new AbstractAlerterTest();

    private HttpAlerter httpAlerter;

    private AlertSender alertSender;

    @Test
    public void testAlert() throws Exception {
        Subscription subscription = abstractAlerterTest.createSubscription(httpAlerter);
        subscription.setTarget(((elasticsearchUrl) + "/stagemonitor/alerts"));
        Incident incident = alertSender.sendTestAlert(subscription, ERROR);
        refresh();
        Collection<Incident> allIncidents = elasticsearchClient.getAll("/stagemonitor/alerts", 10, Incident.class);
        Assert.assertEquals(1, allIncidents.size());
        Assert.assertEquals(incident, allIncidents.iterator().next());
    }
}

