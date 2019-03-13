package org.stagemonitor.alerting.alerter;


import CheckResult.Status.CRITICAL;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.alerting.AlertingPlugin;
import org.stagemonitor.alerting.incident.Incident;
import org.stagemonitor.core.util.JsonUtils;


public class IncidentServletTest {
    private IncidentServlet incidentServlet;

    private Incident incident;

    private AlertingPlugin alertingPlugin;

    @Test
    public void testGetIncidents() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        incidentServlet.service(new MockHttpServletRequest("GET", "/stagemonitor/incidents"), response);
        String expected = JsonUtils.toJson(new HashMap<String, Object>() {
            {
                put("status", CRITICAL);
                put("incidents", Arrays.asList(incident));
            }
        });
        Assert.assertEquals(expected, response.getContentAsString());
    }

    @Test
    public void testGetIncidentsIncidentRepositoryNull() throws Exception {
        Mockito.when(alertingPlugin.getIncidentRepository()).thenReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        incidentServlet.service(new MockHttpServletRequest("GET", "/stagemonitor/incidents"), response);
        Assert.assertEquals("{}", response.getContentAsString());
    }
}

