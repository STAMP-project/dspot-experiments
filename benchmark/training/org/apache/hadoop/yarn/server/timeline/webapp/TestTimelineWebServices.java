/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timeline.webapp;


import AuthenticationFilter.AUTH_TYPE;
import AuthenticationFilter.CONFIG_PREFIX;
import DelegationTokenAuthenticationHandler.TOKEN_KIND;
import MediaType.APPLICATION_JSON;
import PseudoAuthenticationHandler.ANONYMOUS_ALLOWED;
import TimelineDataManager.DEFAULT_DOMAIN_ID;
import TimelineDelegationTokenIdentifier.KIND_NAME;
import TimelineEvents.EventsOfOneEntity;
import TimelinePutError.FORBIDDEN_RELATION;
import TimelinePutResponse.TimelinePutError.ACCESS_DENIED;
import TimelineStore.SystemFilter.ENTITY_OWNER;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.yarn.api.records.timeline.TimelineAbout;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomains;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvents;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.AdminACLsManager;
import org.apache.hadoop.yarn.server.timeline.TimelineDataManager;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.apache.hadoop.yarn.server.timeline.security.TimelineACLsManager;
import org.apache.hadoop.yarn.server.timeline.security.TimelineAuthenticationFilter;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.YarnJacksonJaxbJsonProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestTimelineWebServices extends JerseyTestBase {
    private static TimelineStore store;

    private static TimelineACLsManager timelineACLsManager;

    private static AdminACLsManager adminACLsManager;

    private static long beforeTime;

    private static class WebServletModule extends ServletModule {
        @SuppressWarnings("unchecked")
        @Override
        protected void configureServlets() {
            bind(YarnJacksonJaxbJsonProvider.class);
            bind(TimelineWebServices.class);
            bind(GenericExceptionHandler.class);
            try {
                TestTimelineWebServices.store = TestTimelineWebServices.mockTimelineStore();
            } catch (Exception e) {
                Assert.fail();
            }
            Configuration conf = new YarnConfiguration();
            conf.setBoolean(YARN_ACL_ENABLE, false);
            TestTimelineWebServices.timelineACLsManager = new TimelineACLsManager(conf);
            TestTimelineWebServices.timelineACLsManager.setTimelineStore(TestTimelineWebServices.store);
            conf.setBoolean(YARN_ACL_ENABLE, true);
            conf.set(YARN_ADMIN_ACL, "admin");
            TestTimelineWebServices.adminACLsManager = new AdminACLsManager(conf);
            TimelineDataManager timelineDataManager = new TimelineDataManager(TestTimelineWebServices.store, TestTimelineWebServices.timelineACLsManager);
            timelineDataManager.init(conf);
            timelineDataManager.start();
            bind(TimelineDataManager.class).toInstance(timelineDataManager);
            serve("/*").with(GuiceContainer.class);
            TimelineAuthenticationFilter taFilter = new TimelineAuthenticationFilter();
            FilterConfig filterConfig = Mockito.mock(FilterConfig.class);
            Mockito.when(filterConfig.getInitParameter(CONFIG_PREFIX)).thenReturn(null);
            Mockito.when(filterConfig.getInitParameter(AUTH_TYPE)).thenReturn("simple");
            Mockito.when(filterConfig.getInitParameter(ANONYMOUS_ALLOWED)).thenReturn("true");
            ServletContext context = Mockito.mock(ServletContext.class);
            Mockito.when(filterConfig.getServletContext()).thenReturn(context);
            Enumeration<String> names = Mockito.mock(Enumeration.class);
            Mockito.when(names.hasMoreElements()).thenReturn(true, true, true, false);
            Mockito.when(names.nextElement()).thenReturn(AUTH_TYPE, ANONYMOUS_ALLOWED, TOKEN_KIND);
            Mockito.when(filterConfig.getInitParameterNames()).thenReturn(names);
            Mockito.when(filterConfig.getInitParameter(TOKEN_KIND)).thenReturn(KIND_NAME.toString());
            try {
                taFilter.init(filterConfig);
            } catch (ServletException e) {
                Assert.fail(("Unable to initialize TimelineAuthenticationFilter: " + (e.getMessage())));
            }
            taFilter = Mockito.spy(taFilter);
            try {
                Mockito.doNothing().when(taFilter).init(ArgumentMatchers.any(FilterConfig.class));
            } catch (ServletException e) {
                Assert.fail(("Unable to initialize TimelineAuthenticationFilter: " + (e.getMessage())));
            }
            filter("/*").through(taFilter);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestTimelineWebServices.WebServletModule()));
    }

    public TestTimelineWebServices() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.yarn.server.applicationhistoryservice.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").clientConfig(new DefaultClientConfig(YarnJacksonJaxbJsonProvider.class)).build());
    }

    @Test
    public void testAbout() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineAbout actualAbout = response.getEntity(TimelineAbout.class);
        TimelineAbout expectedAbout = TimelineUtils.createTimelineAbout("Timeline API");
        Assert.assertNotNull("Timeline service about response is null", actualAbout);
        Assert.assertEquals(expectedAbout.getAbout(), actualAbout.getAbout());
        Assert.assertEquals(expectedAbout.getTimelineServiceVersion(), actualAbout.getTimelineServiceVersion());
        Assert.assertEquals(expectedAbout.getTimelineServiceBuildVersion(), actualAbout.getTimelineServiceBuildVersion());
        Assert.assertEquals(expectedAbout.getTimelineServiceVersionBuiltOn(), actualAbout.getTimelineServiceVersionBuiltOn());
        Assert.assertEquals(expectedAbout.getHadoopVersion(), actualAbout.getHadoopVersion());
        Assert.assertEquals(expectedAbout.getHadoopBuildVersion(), actualAbout.getHadoopBuildVersion());
        Assert.assertEquals(expectedAbout.getHadoopVersionBuiltOn(), actualAbout.getHadoopVersionBuiltOn());
    }

    @Test
    public void testGetEntities() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TestTimelineWebServices.verifyEntities(response.getEntity(TimelineEntities.class));
    }

    @Test
    public void testFromId() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("fromId", "id_2").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        Assert.assertEquals(2, response.getEntity(TimelineEntities.class).getEntities().size());
        response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("fromId", "id_1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        Assert.assertEquals(3, response.getEntity(TimelineEntities.class).getEntities().size());
    }

    @Test
    public void testFromTs() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("fromTs", Long.toString(TestTimelineWebServices.beforeTime)).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        Assert.assertEquals(0, response.getEntity(TimelineEntities.class).getEntities().size());
        response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("fromTs", Long.toString(System.currentTimeMillis())).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        Assert.assertEquals(3, response.getEntity(TimelineEntities.class).getEntities().size());
    }

    @Test
    public void testPrimaryFilterString() {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("primaryFilter", "user:username").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TestTimelineWebServices.verifyEntities(response.getEntity(TimelineEntities.class));
    }

    @Test
    public void testPrimaryFilterInteger() {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("primaryFilter", ("appname:" + (Integer.toString(Integer.MAX_VALUE)))).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TestTimelineWebServices.verifyEntities(response.getEntity(TimelineEntities.class));
    }

    @Test
    public void testPrimaryFilterLong() {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("primaryFilter", ("long:" + (Long.toString((((long) (Integer.MAX_VALUE)) + 1L))))).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TestTimelineWebServices.verifyEntities(response.getEntity(TimelineEntities.class));
    }

    @Test
    public void testSecondaryFilters() {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").queryParam("secondaryFilter", ("user:username,appname:" + (Integer.toString(Integer.MAX_VALUE)))).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TestTimelineWebServices.verifyEntities(response.getEntity(TimelineEntities.class));
    }

    @Test
    public void testGetEntity() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").path("id_1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineEntity entity = response.getEntity(TimelineEntity.class);
        Assert.assertNotNull(entity);
        Assert.assertEquals("id_1", entity.getEntityId());
        Assert.assertEquals("type_1", entity.getEntityType());
        Assert.assertEquals(123L, entity.getStartTime().longValue());
        Assert.assertEquals(2, entity.getEvents().size());
        Assert.assertEquals(4, entity.getPrimaryFilters().size());
        Assert.assertEquals(4, entity.getOtherInfo().size());
    }

    @Test
    public void testGetEntityFields1() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").path("id_1").queryParam("fields", "events,otherinfo").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineEntity entity = response.getEntity(TimelineEntity.class);
        Assert.assertNotNull(entity);
        Assert.assertEquals("id_1", entity.getEntityId());
        Assert.assertEquals("type_1", entity.getEntityType());
        Assert.assertEquals(123L, entity.getStartTime().longValue());
        Assert.assertEquals(2, entity.getEvents().size());
        Assert.assertEquals(0, entity.getPrimaryFilters().size());
        Assert.assertEquals(4, entity.getOtherInfo().size());
    }

    @Test
    public void testGetEntityFields2() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").path("id_1").queryParam("fields", ("lasteventonly," + "primaryfilters,relatedentities")).accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineEntity entity = response.getEntity(TimelineEntity.class);
        Assert.assertNotNull(entity);
        Assert.assertEquals("id_1", entity.getEntityId());
        Assert.assertEquals("type_1", entity.getEntityType());
        Assert.assertEquals(123L, entity.getStartTime().longValue());
        Assert.assertEquals(1, entity.getEvents().size());
        Assert.assertEquals(4, entity.getPrimaryFilters().size());
        Assert.assertEquals(0, entity.getOtherInfo().size());
    }

    @Test
    public void testGetEvents() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("type_1").path("events").queryParam("entityId", "id_1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineEvents events = response.getEntity(TimelineEvents.class);
        Assert.assertNotNull(events);
        Assert.assertEquals(1, events.getAllEvents().size());
        TimelineEvents.EventsOfOneEntity partEvents = events.getAllEvents().get(0);
        Assert.assertEquals(2, partEvents.getEvents().size());
        TimelineEvent event1 = partEvents.getEvents().get(0);
        Assert.assertEquals(456L, event1.getTimestamp());
        Assert.assertEquals("end_event", event1.getEventType());
        Assert.assertEquals(1, event1.getEventInfo().size());
        TimelineEvent event2 = partEvents.getEvents().get(1);
        Assert.assertEquals(123L, event2.getTimestamp());
        Assert.assertEquals("start_event", event2.getEventType());
        Assert.assertEquals(0, event2.getEventInfo().size());
    }

    @Test
    public void testPostEntitiesWithPrimaryFilter() throws Exception {
        TimelineEntities entities = new TimelineEntities();
        TimelineEntity entity = new TimelineEntity();
        Map<String, Set<Object>> filters = new HashMap<String, Set<Object>>();
        filters.put(ENTITY_OWNER.toString(), new HashSet<Object>());
        entity.setPrimaryFilters(filters);
        entity.setEntityId("test id 6");
        entity.setEntityType("test type 6");
        entity.setStartTime(System.currentTimeMillis());
        entities.addEntity(entity);
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "tester").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
        TimelinePutResponse putResposne = response.getEntity(TimelinePutResponse.class);
        Assert.assertEquals(0, putResposne.getErrors().size());
    }

    @Test
    public void testPostEntities() throws Exception {
        TimelineEntities entities = new TimelineEntities();
        TimelineEntity entity = new TimelineEntity();
        entity.setEntityId("test id 1");
        entity.setEntityType("test type 1");
        entity.setStartTime(System.currentTimeMillis());
        entity.setDomainId("domain_id_1");
        entities.addEntity(entity);
        WebResource r = resource();
        // No owner, will be rejected
        ClientResponse response = r.path("ws").path("v1").path("timeline").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
        response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "tester").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelinePutResponse putResposne = response.getEntity(TimelinePutResponse.class);
        Assert.assertNotNull(putResposne);
        Assert.assertEquals(0, putResposne.getErrors().size());
        // verify the entity exists in the store
        response = r.path("ws").path("v1").path("timeline").path("test type 1").path("test id 1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        entity = response.getEntity(TimelineEntity.class);
        Assert.assertNotNull(entity);
        Assert.assertEquals("test id 1", entity.getEntityId());
        Assert.assertEquals("test type 1", entity.getEntityType());
    }

    @Test
    public void testPostIncompleteEntities() throws Exception {
        TimelineEntities entities = new TimelineEntities();
        TimelineEntity entity1 = new TimelineEntity();
        entity1.setEntityId("test id 1");
        entity1.setEntityType("test type 1");
        entity1.setStartTime(System.currentTimeMillis());
        entity1.setDomainId("domain_id_1");
        entities.addEntity(entity1);
        // Add an entity with no id or type.
        entities.addEntity(new TimelineEntity());
        WebResource r = resource();
        // One of the entities has no id or type. HTTP 400 will be returned
        ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "tester").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        assertResponseStatusCode(Status.BAD_REQUEST, response.getStatusInfo());
    }

    @Test
    public void testPostEntitiesWithYarnACLsEnabled() throws Exception {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            TimelineEntities entities = new TimelineEntities();
            TimelineEntity entity = new TimelineEntity();
            entity.setEntityId("test id 2");
            entity.setEntityType("test type 2");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_1");
            entities.addEntity(entity);
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_1").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelinePutResponse putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertNotNull(putResponse);
            Assert.assertEquals(0, putResponse.getErrors().size());
            // override/append timeline data in the same entity with different user
            response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_3").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertNotNull(putResponse);
            Assert.assertEquals(1, putResponse.getErrors().size());
            Assert.assertEquals(ACCESS_DENIED, putResponse.getErrors().get(0).getErrorCode());
            // Cross domain relationship will be rejected
            entities = new TimelineEntities();
            entity = new TimelineEntity();
            entity.setEntityId("test id 3");
            entity.setEntityType("test type 2");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_2");
            entity.setRelatedEntities(Collections.singletonMap("test type 2", Collections.singleton("test id 2")));
            entities.addEntity(entity);
            r = resource();
            response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_3").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertNotNull(putResponse);
            Assert.assertEquals(1, putResponse.getErrors().size());
            Assert.assertEquals(FORBIDDEN_RELATION, putResponse.getErrors().get(0).getErrorCode());
            // Make sure the entity has been added anyway even though the
            // relationship is been excluded
            response = r.path("ws").path("v1").path("timeline").path("test type 2").path("test id 3").queryParam("user.name", "reader_user_3").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            entity = response.getEntity(TimelineEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals("test id 3", entity.getEntityId());
            Assert.assertEquals("test type 2", entity.getEntityType());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testPostEntitiesToDefaultDomain() throws Exception {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            TimelineEntities entities = new TimelineEntities();
            TimelineEntity entity = new TimelineEntity();
            entity.setEntityId("test id 7");
            entity.setEntityType("test type 7");
            entity.setStartTime(System.currentTimeMillis());
            entities.addEntity(entity);
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "anybody_1").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelinePutResponse putResposne = response.getEntity(TimelinePutResponse.class);
            Assert.assertNotNull(putResposne);
            Assert.assertEquals(0, putResposne.getErrors().size());
            // verify the entity exists in the store
            response = r.path("ws").path("v1").path("timeline").path("test type 7").path("test id 7").queryParam("user.name", "any_body_2").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            entity = response.getEntity(TimelineEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals("test id 7", entity.getEntityId());
            Assert.assertEquals("test type 7", entity.getEntityType());
            Assert.assertEquals(DEFAULT_DOMAIN_ID, entity.getDomainId());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testGetEntityWithYarnACLsEnabled() throws Exception {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            TimelineEntities entities = new TimelineEntities();
            TimelineEntity entity = new TimelineEntity();
            entity.setEntityId("test id 3");
            entity.setEntityType("test type 3");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_1");
            entities.addEntity(entity);
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_1").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelinePutResponse putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertEquals(0, putResponse.getErrors().size());
            // verify the system data will not be exposed
            // 1. No field specification
            response = r.path("ws").path("v1").path("timeline").path("test type 3").path("test id 3").queryParam("user.name", "reader_user_1").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            entity = response.getEntity(TimelineEntity.class);
            Assert.assertNull(entity.getPrimaryFilters().get(ENTITY_OWNER.toString()));
            // 2. other field
            response = r.path("ws").path("v1").path("timeline").path("test type 3").path("test id 3").queryParam("fields", "relatedentities").queryParam("user.name", "reader_user_1").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            entity = response.getEntity(TimelineEntity.class);
            Assert.assertNull(entity.getPrimaryFilters().get(ENTITY_OWNER.toString()));
            // 3. primaryfilters field
            response = r.path("ws").path("v1").path("timeline").path("test type 3").path("test id 3").queryParam("fields", "primaryfilters").queryParam("user.name", "reader_user_1").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            entity = response.getEntity(TimelineEntity.class);
            Assert.assertNull(entity.getPrimaryFilters().get(ENTITY_OWNER.toString()));
            // get entity with other user
            response = r.path("ws").path("v1").path("timeline").path("test type 3").path("test id 3").queryParam("user.name", "reader_user_2").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testGetEntitiesWithYarnACLsEnabled() {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            // Put entity [4, 4] in domain 1
            TimelineEntities entities = new TimelineEntities();
            TimelineEntity entity = new TimelineEntity();
            entity.setEntityId("test id 4");
            entity.setEntityType("test type 4");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_1");
            entities.addEntity(entity);
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_1").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelinePutResponse putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertEquals(0, putResponse.getErrors().size());
            // Put entity [4, 5] in domain 2
            entities = new TimelineEntities();
            entity = new TimelineEntity();
            entity.setEntityId("test id 5");
            entity.setEntityType("test type 4");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_2");
            entities.addEntity(entity);
            r = resource();
            response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_3").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertEquals(0, putResponse.getErrors().size());
            // Query entities of type 4
            response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "reader_user_1").path("test type 4").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            entities = response.getEntity(TimelineEntities.class);
            // Reader 1 should just have the access to entity [4, 4]
            Assert.assertEquals(1, entities.getEntities().size());
            Assert.assertEquals("test type 4", entities.getEntities().get(0).getEntityType());
            Assert.assertEquals("test id 4", entities.getEntities().get(0).getEntityId());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testGetEventsWithYarnACLsEnabled() {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            // Put entity [5, 5] in domain 1
            TimelineEntities entities = new TimelineEntities();
            TimelineEntity entity = new TimelineEntity();
            entity.setEntityId("test id 5");
            entity.setEntityType("test type 5");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_1");
            TimelineEvent event = new TimelineEvent();
            event.setEventType("event type 1");
            event.setTimestamp(System.currentTimeMillis());
            entity.addEvent(event);
            entities.addEntity(entity);
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_1").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelinePutResponse putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertEquals(0, putResponse.getErrors().size());
            // Put entity [5, 6] in domain 2
            entities = new TimelineEntities();
            entity = new TimelineEntity();
            entity.setEntityId("test id 6");
            entity.setEntityType("test type 5");
            entity.setStartTime(System.currentTimeMillis());
            entity.setDomainId("domain_id_2");
            event = new TimelineEvent();
            event.setEventType("event type 2");
            event.setTimestamp(System.currentTimeMillis());
            entity.addEvent(event);
            entities.addEntity(entity);
            r = resource();
            response = r.path("ws").path("v1").path("timeline").queryParam("user.name", "writer_user_3").accept(APPLICATION_JSON).type(APPLICATION_JSON).post(ClientResponse.class, entities);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            putResponse = response.getEntity(TimelinePutResponse.class);
            Assert.assertEquals(0, putResponse.getErrors().size());
            // Query events belonging to the entities of type 4
            response = r.path("ws").path("v1").path("timeline").path("test type 5").path("events").queryParam("user.name", "reader_user_1").queryParam("entityId", "test id 5,test id 6").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelineEvents events = response.getEntity(TimelineEvents.class);
            // Reader 1 should just have the access to the events of entity [5, 5]
            Assert.assertEquals(1, events.getAllEvents().size());
            Assert.assertEquals("test id 5", events.getAllEvents().get(0).getEntityId());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testGetDomain() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("domain").path("domain_id_1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineDomain domain = response.getEntity(TimelineDomain.class);
        TestTimelineWebServices.verifyDomain(domain, "domain_id_1");
    }

    @Test
    public void testGetDomainYarnACLsEnabled() {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").path("domain").path("domain_id_1").queryParam("user.name", "owner_1").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelineDomain domain = response.getEntity(TimelineDomain.class);
            TestTimelineWebServices.verifyDomain(domain, "domain_id_1");
            response = r.path("ws").path("v1").path("timeline").path("domain").path("domain_id_1").queryParam("user.name", "tester").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            assertResponseStatusCode(Status.NOT_FOUND, response.getStatusInfo());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testGetDomains() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("owner", "owner_1").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        TimelineDomains domains = response.getEntity(TimelineDomains.class);
        Assert.assertEquals(2, domains.getDomains().size());
        for (int i = 0; i < (domains.getDomains().size()); ++i) {
            TestTimelineWebServices.verifyDomain(domains.getDomains().get(i), (i == 0 ? "domain_id_4" : "domain_id_1"));
        }
    }

    @Test
    public void testGetDomainsYarnACLsEnabled() throws Exception {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("user.name", "owner_1").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            TimelineDomains domains = response.getEntity(TimelineDomains.class);
            Assert.assertEquals(2, domains.getDomains().size());
            for (int i = 0; i < (domains.getDomains().size()); ++i) {
                TestTimelineWebServices.verifyDomain(domains.getDomains().get(i), (i == 0 ? "domain_id_4" : "domain_id_1"));
            }
            response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("owner", "owner_1").queryParam("user.name", "tester").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            domains = response.getEntity(TimelineDomains.class);
            Assert.assertEquals(0, domains.getDomains().size());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }

    @Test
    public void testPutDomain() throws Exception {
        TimelineDomain domain = new TimelineDomain();
        domain.setId("test_domain_id");
        WebResource r = resource();
        // No owner, will be rejected
        ClientResponse response = r.path("ws").path("v1").path("timeline").path("domain").accept(APPLICATION_JSON).type(APPLICATION_JSON).put(ClientResponse.class, domain);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
        response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("user.name", "tester").accept(APPLICATION_JSON).type(APPLICATION_JSON).put(ClientResponse.class, domain);
        assertResponseStatusCode(Status.OK, response.getStatusInfo());
        // Verify the domain exists
        response = r.path("ws").path("v1").path("timeline").path("domain").path("test_domain_id").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        domain = response.getEntity(TimelineDomain.class);
        Assert.assertNotNull(domain);
        Assert.assertEquals("test_domain_id", domain.getId());
        Assert.assertEquals("tester", domain.getOwner());
        Assert.assertEquals(null, domain.getDescription());
        // Update the domain
        domain.setDescription("test_description");
        response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("user.name", "tester").accept(APPLICATION_JSON).type(APPLICATION_JSON).put(ClientResponse.class, domain);
        assertResponseStatusCode(Status.OK, response.getStatusInfo());
        // Verify the domain is updated
        response = r.path("ws").path("v1").path("timeline").path("domain").path("test_domain_id").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        domain = response.getEntity(TimelineDomain.class);
        Assert.assertNotNull(domain);
        Assert.assertEquals("test_domain_id", domain.getId());
        Assert.assertEquals("test_description", domain.getDescription());
    }

    @Test
    public void testPutDomainYarnACLsEnabled() throws Exception {
        AdminACLsManager oldAdminACLsManager = TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(TestTimelineWebServices.adminACLsManager);
        try {
            TimelineDomain domain = new TimelineDomain();
            domain.setId("test_domain_id_acl");
            WebResource r = resource();
            ClientResponse response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("user.name", "tester").accept(APPLICATION_JSON).type(APPLICATION_JSON).put(ClientResponse.class, domain);
            assertResponseStatusCode(Status.OK, response.getStatusInfo());
            // Update the domain by another user
            response = r.path("ws").path("v1").path("timeline").path("domain").queryParam("user.name", "other").accept(APPLICATION_JSON).type(APPLICATION_JSON).put(ClientResponse.class, domain);
            assertResponseStatusCode(Status.FORBIDDEN, response.getStatusInfo());
        } finally {
            TestTimelineWebServices.timelineACLsManager.setAdminACLsManager(oldAdminACLsManager);
        }
    }
}

