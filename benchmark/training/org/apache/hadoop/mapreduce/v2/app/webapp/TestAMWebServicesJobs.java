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
import Status.NOT_FOUND;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.test.framework.WebAppDescriptor;
import java.io.StringReader;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MockAppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebServicesTestUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Test the app master web service Rest API for getting jobs, a specific job,
 * and job counters.
 *
 * /ws/v1/mapreduce/jobs
 * /ws/v1/mapreduce/jobs/{jobid}
 * /ws/v1/mapreduce/jobs/{jobid}/counters
 * /ws/v1/mapreduce/jobs/{jobid}/jobattempts
 */
public class TestAMWebServicesJobs extends JerseyTestBase {
    private static Configuration conf = new Configuration();

    private static AppContext appContext;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            TestAMWebServicesJobs.appContext = new MockAppContext(0, 1, 2, 1);
            bind(JAXBContextResolver.class);
            bind(AMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(AppContext.class).toInstance(TestAMWebServicesJobs.appContext);
            bind(Configuration.class).toInstance(TestAMWebServicesJobs.conf);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestAMWebServicesJobs.WebServletModule()));
    }

    public TestAMWebServicesJobs() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.mapreduce.v2.app.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testJobs() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject jobs = json.getJSONObject("jobs");
        JSONArray arr = jobs.getJSONArray("job");
        JSONObject info = arr.getJSONObject(0);
        Job job = TestAMWebServicesJobs.appContext.getJob(MRApps.toJobID(info.getString("id")));
        verifyAMJob(info, job);
    }

    @Test
    public void testJobsSlash() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs/").accept(APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject jobs = json.getJSONObject("jobs");
        JSONArray arr = jobs.getJSONArray("job");
        JSONObject info = arr.getJSONObject(0);
        Job job = TestAMWebServicesJobs.appContext.getJob(MRApps.toJobID(info.getString("id")));
        verifyAMJob(info, job);
    }

    @Test
    public void testJobsDefault() throws Exception, JSONException {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        JSONObject json = response.getEntity(JSONObject.class);
        Assert.assertEquals("incorrect number of elements", 1, json.length());
        JSONObject jobs = json.getJSONObject("jobs");
        JSONArray arr = jobs.getJSONArray("job");
        JSONObject info = arr.getJSONObject(0);
        Job job = TestAMWebServicesJobs.appContext.getJob(MRApps.toJobID(info.getString("id")));
        verifyAMJob(info, job);
    }

    @Test
    public void testJobsXML() throws Exception {
        WebResource r = resource();
        ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").accept(APPLICATION_XML).get(ClientResponse.class);
        Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
        String xml = response.getEntity(String.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document dom = db.parse(is);
        NodeList jobs = dom.getElementsByTagName("jobs");
        Assert.assertEquals("incorrect number of elements", 1, jobs.getLength());
        NodeList job = dom.getElementsByTagName("job");
        Assert.assertEquals("incorrect number of elements", 1, job.getLength());
        verifyAMJobXML(job, TestAMWebServicesJobs.appContext);
    }

    @Test
    public void testJobId() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("job");
            verifyAMJob(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobIdSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path((jobId + "/")).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("job");
            verifyAMJob(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobIdDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("job");
            verifyAMJob(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobIdNonExist() throws Exception, JSONException {
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("mapreduce").path("jobs").path("job_0_1234").get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: job, job_0_1234, is not found", message);
            WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
        }
    }

    @Test
    public void testJobIdInvalid() throws Exception, JSONException {
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("mapreduce").path("jobs").path("job_foo").accept(APPLICATION_JSON).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            verifyJobIdInvalid(message, type, classname);
        }
    }

    // verify the exception output default is JSON
    @Test
    public void testJobIdInvalidDefault() throws Exception, JSONException {
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("mapreduce").path("jobs").path("job_foo").get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            verifyJobIdInvalid(message, type, classname);
        }
    }

    // test that the exception output works in XML
    @Test
    public void testJobIdInvalidXML() throws Exception, JSONException {
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("mapreduce").path("jobs").path("job_foo").accept(APPLICATION_XML).get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String msg = response.getEntity(String.class);
            System.out.println(msg);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(msg));
            Document dom = db.parse(is);
            NodeList nodes = dom.getElementsByTagName("RemoteException");
            Element element = ((Element) (nodes.item(0)));
            String message = WebServicesTestUtils.getXmlString(element, "message");
            String type = WebServicesTestUtils.getXmlString(element, "exception");
            String classname = WebServicesTestUtils.getXmlString(element, "javaClassName");
            verifyJobIdInvalid(message, type, classname);
        }
    }

    @Test
    public void testJobIdInvalidBogus() throws Exception, JSONException {
        WebResource r = resource();
        try {
            r.path("ws").path("v1").path("mapreduce").path("jobs").path("bogusfoo").get(JSONObject.class);
            Assert.fail("should have thrown exception on invalid uri");
        } catch (UniformInterfaceException ue) {
            ClientResponse response = ue.getResponse();
            WebServicesTestUtils.assertResponseStatusCode(NOT_FOUND, response.getStatusInfo());
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject msg = response.getEntity(JSONObject.class);
            JSONObject exception = msg.getJSONObject("RemoteException");
            Assert.assertEquals("incorrect number of elements", 3, exception.length());
            String message = exception.getString("message");
            String type = exception.getString("exception");
            String classname = exception.getString("javaClassName");
            WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: JobId string : bogusfoo is not properly formed", message);
            WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
            WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
        }
    }

    @Test
    public void testJobIdXML() throws Exception {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document dom = db.parse(is);
            NodeList job = dom.getElementsByTagName("job");
            verifyAMJobXML(job, TestAMWebServicesJobs.appContext);
        }
    }

    @Test
    public void testJobCounters() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("counters").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("jobCounters");
            verifyAMJobCounters(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobCountersSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("counters/").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("jobCounters");
            verifyAMJobCounters(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobCountersDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("counters/").get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("jobCounters");
            verifyAMJobCounters(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobCountersXML() throws Exception {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("counters").accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document dom = db.parse(is);
            NodeList info = dom.getElementsByTagName("jobCounters");
            verifyAMJobCountersXML(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobAttempts() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("jobattempts").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("jobAttempts");
            verifyJobAttempts(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobAttemptsSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("jobattempts/").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("jobAttempts");
            verifyJobAttempts(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobAttemptsDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("jobattempts").get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("jobAttempts");
            verifyJobAttempts(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobAttemptsXML() throws Exception {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesJobs.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("jobattempts").accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document dom = db.parse(is);
            NodeList attempts = dom.getElementsByTagName("jobAttempts");
            Assert.assertEquals("incorrect number of elements", 1, attempts.getLength());
            NodeList info = dom.getElementsByTagName("jobAttempt");
            verifyJobAttemptsXML(info, jobsMap.get(id));
        }
    }
}

