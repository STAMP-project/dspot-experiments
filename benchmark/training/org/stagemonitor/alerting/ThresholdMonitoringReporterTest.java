package org.stagemonitor.alerting;


import Alerter.AlertArguments;
import CheckResult.Status;
import CheckResult.Status.CRITICAL;
import CheckResult.Status.OK;
import CheckResult.Status.WARN;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.stagemonitor.alerting.alerter.Alerter;
import org.stagemonitor.alerting.check.Check;
import org.stagemonitor.alerting.check.CheckResult;
import org.stagemonitor.alerting.incident.CheckResults;
import org.stagemonitor.alerting.incident.Incident;
import org.stagemonitor.alerting.incident.IncidentRepository;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.util.JsonUtils;


public class ThresholdMonitoringReporterTest {
    private final MeasurementSession measurementSession = new MeasurementSession("testApp", "testHost", "testInstance");

    private ThresholdMonitoringReporter thresholdMonitoringReporter;

    private Alerter alerter;

    private IncidentRepository incidentRepository;

    private AlertingPlugin alertingPlugin;

    @Test
    public void testAlerting() throws Exception {
        Check check = ThresholdMonitoringReporterTest.createCheckCheckingMean(1, 5);
        Mockito.when(alertingPlugin.getChecks()).thenReturn(Collections.singletonMap(check.getId(), check));
        checkMetrics();
        ArgumentCaptor<Alerter.AlertArguments> alertArguments = ArgumentCaptor.forClass(AlertArguments.class);
        Mockito.verify(alerter).alert(alertArguments.capture());
        final Incident incident = alertArguments.getValue().getIncident();
        Assert.assertEquals(check.getId(), incident.getCheckId());
        Assert.assertEquals("Test Timer", incident.getCheckName());
        Assert.assertEquals(OK, incident.getOldStatus());
        Assert.assertEquals(WARN, incident.getNewStatus());
        Assert.assertEquals(1, incident.getCheckResults().size());
        List<CheckResult> checkResults = incident.getCheckResults().iterator().next().getResults();
        Assert.assertEquals(2, checkResults.size());
        CheckResult result = checkResults.get(0);
        Assert.assertEquals("test_timer,signature=timer3 mean < 5.0 is false", result.getFailingExpression());
        Assert.assertEquals(6.0, result.getCurrentValue(), 0);
        Assert.assertEquals(WARN, result.getStatus());
    }

    @Test
    public void testAlertAfter2Failures() throws Exception {
        Check check = ThresholdMonitoringReporterTest.createCheckCheckingMean(2, 6);
        Mockito.when(alertingPlugin.getChecks()).thenReturn(Collections.singletonMap(check.getId(), check));
        checkMetrics();
        Mockito.verify(alerter, Mockito.times(0)).alert(ArgumentMatchers.any(AlertArguments.class));
        Mockito.verify(incidentRepository).createIncident(ArgumentMatchers.any(Incident.class));
        checkMetrics();
        Mockito.verify(alerter).alert(ArgumentMatchers.any(AlertArguments.class));
        Mockito.verify(incidentRepository).updateIncident(ArgumentMatchers.any(Incident.class));
    }

    @Test
    public void testNoAlertWhenFailureRecovers() throws Exception {
        Check check = ThresholdMonitoringReporterTest.createCheckCheckingMean(2, 6);
        Mockito.when(alertingPlugin.getChecks()).thenReturn(Collections.singletonMap(check.getId(), check));
        // violation
        checkMetrics(7, 0, 0);
        Mockito.verify(alerter, Mockito.times(0)).alert(ArgumentMatchers.any(AlertArguments.class));
        final Incident incident = incidentRepository.getIncidentByCheckId(check.getId());
        Assert.assertNotNull(incident);
        Assert.assertEquals(OK, incident.getOldStatus());
        Assert.assertEquals(WARN, incident.getNewStatus());
        Assert.assertNotNull(incident.getFirstFailureAt());
        Assert.assertNull(incident.getResolvedAt());
        Assert.assertEquals(1, incident.getConsecutiveFailures());
        System.out.println(incident);
        // back to ok
        checkMetrics(1, 0, 0);
        Mockito.verify(alerter, Mockito.times(0)).alert(ArgumentMatchers.any(AlertArguments.class));
        Assert.assertNull(incidentRepository.getIncidentByCheckId(check.getId()));
    }

    @Test
    public void testAlertWhenBackToOk() throws Exception {
        Check check = ThresholdMonitoringReporterTest.createCheckCheckingMean(1, 6);
        Mockito.when(alertingPlugin.getChecks()).thenReturn(Collections.singletonMap(check.getId(), check));
        // violation
        checkMetrics(7, 0, 0);
        Mockito.verify(alerter, Mockito.times(1)).alert(ArgumentMatchers.any(AlertArguments.class));
        Incident incident = incidentRepository.getIncidentByCheckId(check.getId());
        Assert.assertNotNull(incident);
        Assert.assertEquals(OK, incident.getOldStatus());
        Assert.assertEquals(WARN, incident.getNewStatus());
        Assert.assertNotNull(incident.getFirstFailureAt());
        Assert.assertNull(incident.getResolvedAt());
        Assert.assertEquals(1, incident.getConsecutiveFailures());
        System.out.println(incident);
        // back to ok
        checkMetrics(1, 0, 0);
        ArgumentCaptor<Alerter.AlertArguments> alertArguments = ArgumentCaptor.forClass(AlertArguments.class);
        Mockito.verify(alerter, Mockito.times(2)).alert(alertArguments.capture());
        incident = alertArguments.getValue().getIncident();
        Assert.assertNotNull(incident);
        Assert.assertEquals(WARN, incident.getOldStatus());
        Assert.assertEquals(OK, incident.getNewStatus());
        Assert.assertNotNull(incident.getFirstFailureAt());
        Assert.assertNotNull(incident.getResolvedAt());
    }

    @Test
    public void testDontDeleteIncidentIfThereAreNonOkResultsFromOtherInstances() {
        Check check = ThresholdMonitoringReporterTest.createCheckCheckingMean(2, 6);
        Mockito.when(alertingPlugin.getChecks()).thenReturn(Collections.singletonMap(check.getId(), check));
        incidentRepository.createIncident(new Incident(check, new MeasurementSession("testApp", "testHost2", "testInstance"), Arrays.asList(new CheckResult("test", 10, Status.CRITICAL))));
        checkMetrics(7, 0, 0);
        Mockito.verify(alerter, Mockito.times(0)).alert(ArgumentMatchers.any(AlertArguments.class));
        Mockito.verify(incidentRepository).updateIncident(ArgumentMatchers.any(Incident.class));
        Incident storedIncident = incidentRepository.getIncidentByCheckId(check.getId());
        Assert.assertEquals(CRITICAL, storedIncident.getOldStatus());
        Assert.assertEquals(CRITICAL, storedIncident.getNewStatus());
        Assert.assertEquals(2, storedIncident.getCheckResults().size());
        Assert.assertEquals(2, storedIncident.getVersion());
        boolean containsTestHost = false;
        boolean containsTestHost2 = false;
        for (CheckResults checkResults : storedIncident.getCheckResults()) {
            if (checkResults.getMeasurementSession().getHostName().equals("testHost2")) {
                containsTestHost2 = true;
            } else
                if (checkResults.getMeasurementSession().getHostName().equals("testHost")) {
                    containsTestHost = true;
                }

        }
        Assert.assertTrue(containsTestHost);
        Assert.assertTrue(containsTestHost2);
        System.out.println(storedIncident);
        System.out.println(JsonUtils.toJson(storedIncident));
        checkMetrics(1, 0, 0);
        Mockito.verify(alerter, Mockito.times(0)).alert(ArgumentMatchers.any(AlertArguments.class));
        Mockito.verify(incidentRepository, Mockito.times(0)).deleteIncident(ArgumentMatchers.any(Incident.class));
        Mockito.verify(incidentRepository, Mockito.times(2)).updateIncident(ArgumentMatchers.any(Incident.class));
        storedIncident = incidentRepository.getIncidentByCheckId(check.getId());
        Assert.assertEquals(CRITICAL, storedIncident.getOldStatus());
        Assert.assertEquals(CRITICAL, storedIncident.getNewStatus());
        Assert.assertEquals(1, storedIncident.getCheckResults().size());
        Assert.assertEquals(3, storedIncident.getVersion());
    }
}

