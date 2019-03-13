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
import ServiceNowConstants.ACTION_RETRIEVE;
import ServiceNowConstants.API_VERSION;
import ServiceNowConstants.REQUEST_MODEL;
import ServiceNowConstants.RESOURCE;
import ServiceNowConstants.RESOURCE_TABLE;
import ServiceNowConstants.RESPONSE_MODEL;
import ServiceNowParams.PARAM_TABLE_NAME;
import ServiceNowParams.SYSPARM_QUERY;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.servicenow.model.Incident;
import org.junit.Assert;
import org.junit.Test;


public class ServiceNowTest extends ServiceNowTestSupport {
    @Test
    public void testExceptions() throws Exception {
        // 404
        try {
            template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(SYSPARM_QUERY, ("number=" + (UUID.randomUUID().toString()))).put(PARAM_TABLE_NAME, "incident").build());
        } catch (CamelExecutionException e) {
            assertTrue(((e.getCause()) instanceof ServiceNowException));
            ServiceNowException sne = ((ServiceNowException) (e.getCause()));
            assertEquals("failure", sne.getStatus());
            assertTrue(sne.getMessage().contains("No Record found"));
            assertTrue(sne.getDetail().contains("Records matching query not found"));
        }
        // 400
        try {
            template().sendBodyAndHeaders("direct:servicenow", null, ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_RETRIEVE).put(SYSPARM_QUERY, ("number=" + (UUID.randomUUID().toString()))).put(PARAM_TABLE_NAME, "notExistingTable").build());
        } catch (CamelExecutionException e) {
            assertTrue(((e.getCause()) instanceof ServiceNowException));
            ServiceNowException sne = ((ServiceNowException) (e.getCause()));
            assertEquals("failure", sne.getStatus());
            assertTrue(sne.getMessage().contains("Invalid table notExistingTable"));
            assertNull(sne.getDetail());
        }
    }

    @Test
    public void testBodyMismatch() throws Exception {
        try {
            template().sendBodyAndHeaders("direct:servicenow", "NotAnIncidentObject", ServiceNowTestSupport.kvBuilder().put(RESOURCE, "table").put(ACTION, ACTION_CREATE).put(PARAM_TABLE_NAME, "incident").build());
            fail("Should fail as body is not compatible with model defined in route for table incident");
        } catch (CamelExecutionException e) {
            assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testRequestResponseModels() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:servicenow");
        mock.reset();
        mock.expectedMessageCount(1);
        Incident incident = new Incident();
        incident.setDescription("my incident");
        incident.setShortDescription("An incident");
        incident.setSeverity(1);
        incident.setImpact(1);
        template().sendBodyAndHeaders("direct:servicenow", incident, ServiceNowTestSupport.kvBuilder().put(RESOURCE, RESOURCE_TABLE).put(ACTION, ACTION_CREATE).put(REQUEST_MODEL, Incident.class).put(RESPONSE_MODEL, JsonNode.class).put(PARAM_TABLE_NAME, "incident").build());
        mock.assertIsSatisfied();
        Object body = mock.getExchanges().get(0).getIn().getBody();
        assertNotNull(body);
        assertTrue((body instanceof JsonNode));
    }

    @Test
    public void testRequestResponseAsString() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:servicenow");
        mock.reset();
        mock.expectedMessageCount(1);
        Incident incident = new Incident();
        incident.setDescription("my incident");
        incident.setShortDescription("An incident");
        incident.setSeverity(1);
        incident.setImpact(1);
        template().sendBodyAndHeaders("direct:servicenow", incident, ServiceNowTestSupport.kvBuilder().put(RESOURCE, RESOURCE_TABLE).put(ACTION, ACTION_CREATE).put(REQUEST_MODEL, Incident.class).put(RESPONSE_MODEL, String.class).put(PARAM_TABLE_NAME, "incident").build());
        mock.assertIsSatisfied();
        Object body = mock.getExchanges().get(0).getIn().getBody();
        assertNotNull(body);
        assertTrue((body instanceof String));
    }

    @Test
    public void testVersionedApiRequest() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:servicenow");
        mock.reset();
        mock.expectedMessageCount(1);
        Incident incident = new Incident();
        incident.setDescription("my incident");
        incident.setShortDescription("An incident");
        incident.setSeverity(1);
        incident.setImpact(1);
        template().sendBodyAndHeaders("direct:servicenow", incident, ServiceNowTestSupport.kvBuilder().put(RESOURCE, RESOURCE_TABLE).put(API_VERSION, "v1").put(ACTION, ACTION_CREATE).put(REQUEST_MODEL, Incident.class).put(RESPONSE_MODEL, JsonNode.class).put(PARAM_TABLE_NAME, "incident").build());
        mock.assertIsSatisfied();
        Object body = mock.getExchanges().get(0).getIn().getBody();
        assertNotNull(body);
        assertTrue((body instanceof JsonNode));
    }

    // *********************************
    // Date/Time
    // *********************************
    @Test
    public void testDateTimeWithDefaults() throws Exception {
        final ServiceNowConfiguration configuration = new ServiceNowConfiguration();
        ObjectMapper mapper = configuration.getOrCreateMapper();
        ServiceNowTest.DateTimeBean bean = new ServiceNowTest.DateTimeBean();
        String serialized = mapper.writeValueAsString(bean);
        ServiceNowTestSupport.LOGGER.debug(serialized);
        ServiceNowTest.DateTimeBean deserialized = mapper.readValue(serialized, ServiceNowTest.DateTimeBean.class);
        Assert.assertEquals(bean.dateTime, deserialized.dateTime);
        Assert.assertEquals(bean.date, deserialized.date);
        Assert.assertEquals(bean.time, deserialized.time);
    }

    @Test
    public void testDateTimeWithCustomFormats() throws Exception {
        final ServiceNowConfiguration configuration = new ServiceNowConfiguration();
        configuration.setDateFormat("yyyyMMdd");
        configuration.setTimeFormat("HHmmss");
        ObjectMapper mapper = configuration.getOrCreateMapper();
        ServiceNowTest.DateTimeBean bean = new ServiceNowTest.DateTimeBean();
        String serialized = mapper.writeValueAsString(bean);
        ServiceNowTestSupport.LOGGER.debug(serialized);
        ServiceNowTest.DateTimeBean deserialized = mapper.readValue(serialized, ServiceNowTest.DateTimeBean.class);
        Assert.assertEquals(bean.dateTime, deserialized.dateTime);
        Assert.assertEquals(bean.date, deserialized.date);
        Assert.assertEquals(bean.time, deserialized.time);
    }

    public static class DateTimeBean {
        LocalDateTime dateTime;

        LocalDate date;

        LocalTime time;

        public DateTimeBean() {
            dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            date = dateTime.toLocalDate();
            time = dateTime.toLocalTime();
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }
    }
}

