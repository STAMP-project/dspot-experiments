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
package org.apache.hadoop.mapreduce.v2.app.webapp;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import MediaType.TEXT_PLAIN;
import Status.INTERNAL_SERVER_ERROR;
import Status.NOT_FOUND;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import javax.ws.rs.core.MediaType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MockAppContext;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the MapReduce Application master info web services api's. Also test
 * non-existent urls.
 *
 *  /ws/v1/mapreduce
 *  /ws/v1/mapreduce/info
 */
public class TestAMWebServices extends JerseyTestBase {
    private static Configuration conf = new Configuration();

    private static MockAppContext appContext;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            TestAMWebServices.appContext = new MockAppContext(0, 1, 1, 1);
            TestAMWebServices.appContext.setBlacklistedNodes(Sets.newHashSet("badnode1", "badnode2"));
            bind(JAXBContextResolver.class);
            bind(AMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(AppContext.class).toInstance(TestAMWebServices.appContext);
            bind(Configuration.class).toInstance(TestAMWebServices.conf);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestAMWebServices.WebServletModule()));
    }

    public TestAMWebServices() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.mapreduce.v2.app.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testAM() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyAMInfo(json.getJSONObject("info"), TestAMWebServices.appContext);
    }

    @Test
    public void testAMSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyAMInfo(json.getJSONObject("info"), TestAMWebServices.appContext);
    }

    @Test
    public void testAMDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce/").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyAMInfo(json.getJSONObject("info"), TestAMWebServices.appContext);
    }

    @Test
    public void testAMXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyAMInfoXML(xml, TestAMWebServices.appContext);
    }

    @Test
    public void testInfo() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("info").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyAMInfo(json.getJSONObject("info"), TestAMWebServices.appContext);
    }

    @Test
    public void testInfoSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("info/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyAMInfo(json.getJSONObject("info"), TestAMWebServices.appContext);
    }

    @Test
    public void testInfoDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("info/").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyAMInfo(json.getJSONObject("info"), TestAMWebServices.appContext);
    }

    @Test
    public void testInfoXML() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("info/").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyAMInfoXML(xml, TestAMWebServices.appContext);
    }

    @Test
    public void testInvalidUri() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("mapreduce").path("bogus").accept(APPLICATION_JSON).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testInvalidUri2() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("invalid").accept(APPLICATION_JSON).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testInvalidAccept() throws Exception, JSONException {
        WebResource r = resource();
        String responseStr = "";
        try {
            responseStr = r.path("ws").path("v1").path("mapreduce").accept(TEXT_PLAIN).get(String.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(INTERNAL_SERVER_ERROR, response.getStatusInfo());
            WebServicesTestUtils.checkStringMatch("error string exists and shouldn't", "", responseStr);
        }
    }

    @Test
    public void testBlacklistedNodes() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("blacklistednodes").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        verifyBlacklistedNodesInfo(json, TestAMWebServices.appContext);
    }

    @Test
    public void testBlacklistedNodesXML() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("blacklistednodes").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        verifyBlacklistedNodesInfoXML(xml, TestAMWebServices.appContext);
    }
}

