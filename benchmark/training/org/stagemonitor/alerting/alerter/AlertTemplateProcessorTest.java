package org.stagemonitor.alerting.alerter;


import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.alerting.incident.Incident;
import org.stagemonitor.alerting.incident.IncidentRepositoryTest;


public class AlertTemplateProcessorTest extends AbstractAlerterTest {
    private AlertTemplateProcessor alertTemplateProcessor;

    private Incident testIncident;

    @Test
    public void testProcessShortDescriptionTemplate() throws Exception {
        Assert.assertEquals("[OK -> CRITICAL] Test Timer has 2 failing checks", alertTemplateProcessor.processShortDescriptionTemplate(IncidentRepositoryTest.createIncidentWithVersion("1", 1)));
        // modify template
        configurationSource.add("stagemonitor.alerts.template.shortDescription", "foo");
        configuration.reloadDynamicConfigurationOptions();
        Assert.assertEquals("foo", alertTemplateProcessor.processShortDescriptionTemplate(testIncident));
    }

    @Test
    public void testProcessPlainTextTemplate() throws Exception {
        Assert.assertEquals(String.format(("Incident for check \'Test Timer\':\n" + ((((((((((((((((((("First failure: %s\n" + "Old status: OK\n") + "New status: CRITICAL\n") + "Failing checks: 2\n") + "Hosts: testHost2\n") + "Instances: testInstance\n") + "\n") + "Details:") + "\n") + "Host:\t\t\ttestHost2\n") + "Instance:\t\ttestInstance\n") + "Status: \t\tCRITICAL\n") + "Description:\ttest\n") + "Current value:\t10\n") + "\n") + "Host:\t\t\ttestHost2\n") + "Instance:\t\ttestInstance\n") + "Status: \t\tERROR\n") + "Description:\ttest\n") + "Current value:\t10\n\n")), toFreemarkerIsoLocal(testIncident.getFirstFailureAt())), alertTemplateProcessor.processPlainTextTemplate(testIncident));
    }

    @Test
    public void testProcessHtmlTemplate() throws Exception {
        Assert.assertEquals(String.format(("<h3>Incident for check Test Timer</h3>\n" + ((((((((((((((((((((((((((((((((("First failure: %s<br>\n" + "Old status: OK<br>\n") + "New status: CRITICAL<br>\n") + "Failing checks: 2<br>\n") + "Hosts: testHost2<br>\n") + "Instances: testInstance<br><br>\n") + "\n") + "<table>\n") + "\t<thead>\n") + "\t<tr>\n") + "\t\t<th>Host</th>\n") + "\t\t<th>Instance</th>\n") + "\t\t<th>Status</th>\n") + "\t\t<th>Description</th>\n") + "\t\t<th>Current Value</th>\n") + "\t</tr>\n") + "\t</thead>\n") + "\t<tbody>\n") + "\t\t\t<tr>\n") + "\t\t\t\t<td>testHost2</td>\n") + "\t\t\t\t<td>testInstance</td>\n") + "\t\t\t\t<td>CRITICAL</td>\n") + "\t\t\t\t<td>test</td>\n") + "\t\t\t\t<td>10</td>\n") + "\t\t\t</tr>\n") + "\t\t\t<tr>\n") + "\t\t\t\t<td>testHost2</td>\n") + "\t\t\t\t<td>testInstance</td>\n") + "\t\t\t\t<td>ERROR</td>\n") + "\t\t\t\t<td>test</td>\n") + "\t\t\t\t<td>10</td>\n") + "\t\t\t</tr>\n") + "\t</tbody>\n") + "</table>\n")), toFreemarkerIsoLocal(testIncident.getFirstFailureAt())), alertTemplateProcessor.processHtmlTemplate(testIncident));
    }
}

