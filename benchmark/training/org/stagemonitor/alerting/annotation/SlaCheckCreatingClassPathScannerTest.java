package org.stagemonitor.alerting.annotation;


import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.alerting.AlertingPlugin;
import org.stagemonitor.alerting.check.Check;
import org.stagemonitor.alerting.check.MetricValueType;
import org.stagemonitor.alerting.incident.Incident;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.tracing.Traced;


public class SlaCheckCreatingClassPathScannerTest {
    private final AlertingPlugin alertingPlugin = Stagemonitor.getPlugin(AlertingPlugin.class);

    private Map<String, Check> checks;

    private static class SlaTestClass {
        @SLAs({ @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = { 0, 0 }), @SLA(errorRateThreshold = 0) })
        @Traced
        void monitorSla() {
            throw null;
        }

        @SLA(errorRateThreshold = 0)
        void monitorRequestsAnnotationMissing() {
        }

        @Traced
        @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = 0)
        void tooFewThresholds() {
        }

        @Traced(resolveNameAtRuntime = true)
        @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = { 0, 0 })
        void slaMonitorRequestsResolveAtRuntime() {
        }

        @Traced(requestName = "monitor requests custom name")
        @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = { 0, 0 })
        void slaMonitorRequestsCustomName() {
        }

        @Timed(name = "timed custom name", absolute = true)
        @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = { 0, 0 })
        void slaTimedCustomName() {
        }

        @Timed
        @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = { 0, 0 })
        void slaOnTimed() {
        }

        @ExceptionMetered
        @SLA(errorRateThreshold = 0)
        void slaOnExceptionMetered() {
        }

        @Timed
        @SLA(errorRateThreshold = 0)
        void slaMissingExceptionMetered() {
        }

        static void makeSureClassIsLoaded() {
        }
    }

    @Test
    public void testSlaMonitorRequests() throws Exception {
        testErrorRateCheck("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.monitorSla().errors", name("error_rate").operationName("Monitor Sla").operationType("method_invocation").build());
        testResponseTimeCheck("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.monitorSla().responseTime", name("response_time").operationName("Monitor Sla").operationType("method_invocation").build());
    }

    @Test
    public void testSlaCustomName() throws Exception {
        testResponseTimeCheck("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.slaMonitorRequestsCustomName().responseTime", name("response_time").operationName("monitor requests custom name").operationType("method_invocation").build());
    }

    @Test
    public void testTimedCustomName() throws Exception {
        testResponseTimeCheck("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.slaTimedCustomName().responseTime", name("timer").tag("signature", "timed custom name").build());
    }

    @Test
    public void testSlaTimed() throws Exception {
        testResponseTimeCheck("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.slaOnTimed().responseTime", name("timer").tag("signature", "SlaCheckCreatingClassPathScannerTest$SlaTestClass#slaOnTimed").build());
    }

    @Test
    public void testSlaExceptionMetered() throws Exception {
        testErrorRateCheck("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.slaOnExceptionMetered().errors", name("exception_rate").tag("signature", "SlaCheckCreatingClassPathScannerTest$SlaTestClass#slaOnExceptionMetered").build());
    }

    @Test
    public void testSlaMissingExceptionMetered() throws Exception {
        Assert.assertNull(checks.get("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.slaMissingExceptionMetered().errors"));
    }

    @Test
    public void testSlaMonitorRequestsResolveAtRuntime() throws Exception {
        Assert.assertNull(checks.get("void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.slaMonitorRequestsResolveAtRuntime().responseTime"));
    }

    @Test
    public void testSlaMonitorRequestsClassLevel() throws Exception {
        testResponseTimeCheck("public void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$ClassLevelMonitorRequestsTestClass.slaMonitorRequestsClassLevel().responseTime", name("response_time").operationName("Sla Monitor Requests Class Level").operationType("method_invocation").build());
    }

    @Traced
    private static class ClassLevelMonitorRequestsTestClass {
        static void makeSureClassIsLoaded() {
        }

        @SLA(metric = { MetricValueType.P95, MetricValueType.MAX }, threshold = { 0, 0 })
        public void slaMonitorRequestsClassLevel() {
        }
    }

    @Test
    public void testTriggersResponseTimeIncident() throws Exception {
        final String checkId = "void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.monitorSla().responseTime";
        assertThat(checks).containsKey(checkId);
        try {
            new SlaCheckCreatingClassPathScannerTest.SlaTestClass().monitorSla();
        } catch (Exception e) {
            // ignore
        }
        assertThat(Stagemonitor.getMetric2Registry().getTimers()).containsKey(name("response_time").operationName("Monitor Sla").operationType("method_invocation").build());
        alertingPlugin.getThresholdMonitoringReporter().report();
        final Incident incident = alertingPlugin.getIncidentRepository().getIncidentByCheckId(checkId);
        assertThat(incident).isNotNull();
    }

    @Test
    public void testTriggersErrorRateIncident() throws Exception {
        final String checkId = "void org.stagemonitor.alerting.annotation.SlaCheckCreatingClassPathScannerTest$SlaTestClass.monitorSla().errors";
        assertThat(checks).containsKey(checkId);
        try {
            new SlaCheckCreatingClassPathScannerTest.SlaTestClass().monitorSla();
        } catch (Exception e) {
            // ignore
        }
        assertThat(Stagemonitor.getMetric2Registry().getMeters()).containsKey(name("error_rate").operationName("Monitor Sla").operationType("method_invocation").build());
        alertingPlugin.getThresholdMonitoringReporter().report();
        final Incident incident = alertingPlugin.getIncidentRepository().getIncidentByCheckId(checkId);
        assertThat(incident).isNotNull();
    }
}

