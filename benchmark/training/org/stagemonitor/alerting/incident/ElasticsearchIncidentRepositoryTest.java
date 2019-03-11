package org.stagemonitor.alerting.incident;


import CheckResult.Status;
import java.util.Arrays;
import org.junit.Test;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.alerting.ThresholdMonitoringReporterTest;
import org.stagemonitor.alerting.check.Check;
import org.stagemonitor.core.MeasurementSession;


public class ElasticsearchIncidentRepositoryTest extends AbstractElasticsearchTest {
    private ElasticsearchIncidentRepository elasticsearchIncidentRepository;

    @Test
    public void testGetNonExistingIncindent() throws Exception {
        assertThat(elasticsearchIncidentRepository.getIncidentByCheckId("test 1")).isNull();
    }

    @Test
    public void testCreateAndGetIncindent() throws Exception {
        final Check check = ThresholdMonitoringReporterTest.createCheckCheckingMean(1, 5);
        check.setId("test 1");
        final Incident incident = new Incident(check, new MeasurementSession("testApp", "testHost2", "testInstance"), Arrays.asList(new org.stagemonitor.alerting.check.CheckResult("test", 10, Status.CRITICAL), new org.stagemonitor.alerting.check.CheckResult("test", 10, Status.ERROR)));
        elasticsearchIncidentRepository.createIncident(incident);
        refresh();
        assertThat(elasticsearchIncidentRepository.getIncidentByCheckId("test 1")).isEqualTo(incident);
    }
}

