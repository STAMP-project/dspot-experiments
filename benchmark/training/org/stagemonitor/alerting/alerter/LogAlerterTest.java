package org.stagemonitor.alerting.alerter;


import CheckResult.Status.ERROR;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.stagemonitor.alerting.incident.Incident;


public class LogAlerterTest extends AbstractAlerterTest {
    private LogAlerter logAlerter;

    private Logger logger;

    private AlertSender alertSender;

    public LogAlerterTest() {
        super();
        logger = Mockito.mock(Logger.class);
        logAlerter = new LogAlerter(alertingPlugin, logger);
        this.alertSender = createAlertSender(logAlerter);
    }

    @Test
    public void testLogAlert() throws Exception {
        Incident incident = alertSender.sendTestAlert(createSubscription(logAlerter), ERROR);
        Mockito.verify(logger).error(ArgumentMatchers.eq(String.format(("Incident for check \'Test Check\':\n" + ((((((((((((("First failure: %s\n" + "Old status: OK\n") + "New status: ERROR\n") + "Failing check: 1\n") + "Hosts: testHost\n") + "Instances: testInstance\n") + "\n") + "Details:\n") + "Host:\t\t\ttestHost\n") + "Instance:\t\ttestInstance\n") + "Status: \t\tERROR\n") + "Description:\ttest\n") + "Current value:\t10\n") + "\n")), toFreemarkerIsoLocal(incident.getFirstFailureAt()))));
    }
}

