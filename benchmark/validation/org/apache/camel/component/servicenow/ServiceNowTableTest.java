/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.servicenow;


import ServiceNowConstants.ACTION;
import ServiceNowConstants.ACTION_CREATE;
import ServiceNowConstants.ACTION_DELETE;
import ServiceNowConstants.ACTION_MODIFY;
import ServiceNowConstants.ACTION_RETRIEVE;
import ServiceNowConstants.MODEL;
import ServiceNowConstants.OFFSET_FIRST;
import ServiceNowConstants.OFFSET_LAST;
import ServiceNowConstants.OFFSET_NEXT;
import ServiceNowConstants.RESOURCE;
import ServiceNowConstants.RESPONSE_TYPE;
import ServiceNowParams.PARAM_SYS_ID;
import ServiceNowParams.PARAM_TABLE_NAME;
import ServiceNowParams.SYSPARM_EXCLUDE_REFERENCE_LINK;
import ServiceNowParams.SYSPARM_LIMIT;
import ServiceNowParams.SYSPARM_QUERY;
import java.util.List;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.servicenow.model.Incident;
import org.apache.camel.component.servicenow.model.IncidentWithParms;
import org.junit.Test;


public class ServiceNowTableTest extends ServiceNowTestSupport {
    @Test
    public void testRetrieveSome() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:servicenow");
        mock.expectedMessageCount(1);
        template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(SYSPARM_LIMIT, 10).put(PARAM_TABLE_NAME, "incident").build());
        mock.assertIsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        List<Incident> items = exchange.getIn().getBody(List.class);
        assertNotNull(items);
        assertTrue(((items.size()) <= 10));
        assertNotNull(exchange.getIn().getHeader(RESPONSE_TYPE));
        assertNotNull(exchange.getIn().getHeader(OFFSET_FIRST));
        assertNotNull(exchange.getIn().getHeader(OFFSET_NEXT));
        assertNotNull(exchange.getIn().getHeader(OFFSET_LAST));
    }

    @Test
    public void testRetrieveSomeWithParams() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:servicenow");
        mock.expectedMessageCount(1);
        template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(SYSPARM_LIMIT, 10).put(SYSPARM_EXCLUDE_REFERENCE_LINK, false).put(PARAM_TABLE_NAME, "incident").put(MODEL, IncidentWithParms.class).build());
        mock.assertIsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        List<Incident> items = exchange.getIn().getBody(List.class);
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertTrue(((items.size()) <= 10));
        assertNotNull(exchange.getIn().getHeader(RESPONSE_TYPE));
        assertNotNull(exchange.getIn().getHeader(OFFSET_FIRST));
        assertNotNull(exchange.getIn().getHeader(OFFSET_NEXT));
        assertNotNull(exchange.getIn().getHeader(OFFSET_LAST));
    }

    @Test
    public void testRetrieveSomeWithDefaults() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:servicenow-defaults");
        mock.expectedMessageCount(1);
        template().sendBodyAndHeaders("direct:servicenow-defaults", null, ServiceNowTestSupport.kvBuilder().put(ACTION, ACTION_RETRIEVE).put(SYSPARM_LIMIT, 10).build());
        mock.assertIsSatisfied();
        Exchange exchange = mock.getExchanges().get(0);
        List<Incident> items = exchange.getIn().getBody(List.class);
        assertNotNull(items);
        assertTrue(((items.size()) <= 10));
    }

    @Test
    public void testIncidentWorkflow() throws Exception {
        Incident incident = null;
        String sysId;
        String number;
        MockEndpoint mock = getMockEndpoint("mock:servicenow");
        // ************************
        // Create incident
        // ************************
        {
            mock.reset();
            mock.expectedMessageCount(1);
            incident = new Incident();
            incident.setDescription("my incident");
            incident.setShortDescription("An incident");
            incident.setSeverity(1);
            incident.setImpact(1);
            template().sendBodyAndHeaders("direct:servicenow", incident, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_CREATE).put(PARAM_TABLE_NAME, "incident").build());
            mock.assertIsSatisfied();
            incident = mock.getExchanges().get(0).getIn().getBody(Incident.class);
            sysId = incident.getId();
            number = incident.getNumber();
            ServiceNowTestSupport.LOGGER.info("****************************************************");
            ServiceNowTestSupport.LOGGER.info(" Incident created");
            ServiceNowTestSupport.LOGGER.info("  sysid  = {}", sysId);
            ServiceNowTestSupport.LOGGER.info("  number = {}", number);
            ServiceNowTestSupport.LOGGER.info("****************************************************");
        }
        // ************************
        // Search for the incident
        // ************************
        {
            ServiceNowTestSupport.LOGGER.info("Search the record {}", sysId);
            mock.reset();
            mock.expectedMessageCount(1);
            template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(PARAM_TABLE_NAME, "incident").put(SYSPARM_QUERY, ("number=" + number)).build());
            mock.assertIsSatisfied();
            List<Incident> incidents = mock.getExchanges().get(0).getIn().getBody(List.class);
            assertEquals(1, incidents.size());
            assertEquals(number, incidents.get(0).getNumber());
            assertEquals(sysId, incidents.get(0).getId());
        }
        // ************************
        // Modify the incident
        // ************************
        {
            ServiceNowTestSupport.LOGGER.info("Update the record {}", sysId);
            mock.reset();
            mock.expectedMessageCount(1);
            incident = new Incident();
            incident.setDescription("my incident");
            incident.setShortDescription("The incident");
            incident.setSeverity(2);
            incident.setImpact(3);
            template().sendBodyAndHeaders("direct:servicenow", incident, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_MODIFY).put(PARAM_TABLE_NAME, "incident").put(PARAM_SYS_ID, sysId).build());
            mock.assertIsSatisfied();
            incident = mock.getExchanges().get(0).getIn().getBody(Incident.class);
            assertEquals(number, incident.getNumber());
            assertEquals(2, incident.getSeverity());
            assertEquals(3, incident.getImpact());
            assertEquals("The incident", incident.getShortDescription());
        }
        // ************************
        // Retrieve it via query
        // ************************
        {
            ServiceNowTestSupport.LOGGER.info("Retrieve the record {}", sysId);
            mock.reset();
            mock.expectedMessageCount(1);
            template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(PARAM_TABLE_NAME, "incident").put(SYSPARM_QUERY, ("number=" + number)).build());
            mock.assertIsSatisfied();
            List<Incident> incidents = mock.getExchanges().get(0).getIn().getBody(List.class);
            assertEquals(1, incidents.size());
            assertEquals(number, incidents.get(0).getNumber());
            assertEquals(sysId, incidents.get(0).getId());
            assertEquals(2, incidents.get(0).getSeverity());
            assertEquals(3, incidents.get(0).getImpact());
            assertEquals("The incident", incidents.get(0).getShortDescription());
        }
        // ************************
        // Retrieve by sys id
        // ************************
        {
            ServiceNowTestSupport.LOGGER.info("Search the record {}", sysId);
            mock.reset();
            mock.expectedMessageCount(1);
            template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(PARAM_TABLE_NAME, "incident").put(PARAM_SYS_ID, sysId).build());
            mock.assertIsSatisfied();
            incident = mock.getExchanges().get(0).getIn().getBody(Incident.class);
            assertEquals(2, incident.getSeverity());
            assertEquals(3, incident.getImpact());
            assertEquals("The incident", incident.getShortDescription());
            assertEquals(number, incident.getNumber());
        }
        // ************************
        // Delete it
        // ************************
        {
            ServiceNowTestSupport.LOGGER.info("Delete the record {}", sysId);
            mock.reset();
            mock.expectedMessageCount(1);
            template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_DELETE).put(PARAM_TABLE_NAME, "incident").put(PARAM_SYS_ID, sysId).build());
            mock.assertIsSatisfied();
        }
        // ************************
        // Retrieve by id, should fail
        // ************************
        {
            ServiceNowTestSupport.LOGGER.info("Find the record {}, should fail", sysId);
            try {
                template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(PARAM_SYS_ID, sysId).put(PARAM_TABLE_NAME, "incident").build());
                fail((("Record " + number) + " should have been deleted"));
            } catch (CamelExecutionException e) {
                assertTrue(((e.getCause()) instanceof ServiceNowException));
                // we are good
            }
        }
    }
}

