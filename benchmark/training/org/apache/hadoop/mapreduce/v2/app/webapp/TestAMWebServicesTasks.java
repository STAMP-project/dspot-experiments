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
import Status.BAD_REQUEST;
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
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MockAppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
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
 * Test the app master web service Rest API for getting tasks, a specific task,
 * and task counters.
 *
 * /ws/v1/mapreduce/jobs/{jobid}/tasks
 * /ws/v1/mapreduce/jobs/{jobid}/tasks/{taskid}
 * /ws/v1/mapreduce/jobs/{jobid}/tasks/{taskid}/counters
 */
public class TestAMWebServicesTasks extends JerseyTestBase {
    private static Configuration conf = new Configuration();

    private static AppContext appContext;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            TestAMWebServicesTasks.appContext = new MockAppContext(0, 1, 2, 1);
            bind(JAXBContextResolver.class);
            bind(AMWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(AppContext.class).toInstance(TestAMWebServicesTasks.appContext);
            bind(Configuration.class).toInstance(TestAMWebServicesTasks.conf);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestAMWebServicesTasks.WebServletModule()));
    }

    public TestAMWebServicesTasks() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.mapreduce.v2.app.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testTasks() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject tasks = json.getJSONObject("tasks");
            JSONArray arr = tasks.getJSONArray("task");
            Assert.assertEquals("incorrect number of elements", 2, arr.length());
            verifyAMTask(arr, jobsMap.get(id), null);
        }
    }

    @Test
    public void testTasksDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject tasks = json.getJSONObject("tasks");
            JSONArray arr = tasks.getJSONArray("task");
            Assert.assertEquals("incorrect number of elements", 2, arr.length());
            verifyAMTask(arr, jobsMap.get(id), null);
        }
    }

    @Test
    public void testTasksSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks/").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject tasks = json.getJSONObject("tasks");
            JSONArray arr = tasks.getJSONArray("task");
            Assert.assertEquals("incorrect number of elements", 2, arr.length());
            verifyAMTask(arr, jobsMap.get(id), null);
        }
    }

    @Test
    public void testTasksXML() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document dom = db.parse(is);
            NodeList tasks = dom.getElementsByTagName("tasks");
            Assert.assertEquals("incorrect number of elements", 1, tasks.getLength());
            NodeList task = dom.getElementsByTagName("task");
            verifyAMTaskXML(task, jobsMap.get(id));
        }
    }

    @Test
    public void testTasksQueryMap() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String type = "m";
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").queryParam("type", type).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject tasks = json.getJSONObject("tasks");
            JSONArray arr = tasks.getJSONArray("task");
            Assert.assertEquals("incorrect number of elements", 1, arr.length());
            verifyAMTask(arr, jobsMap.get(id), type);
        }
    }

    @Test
    public void testTasksQueryReduce() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String type = "r";
            ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").queryParam("type", type).accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject tasks = json.getJSONObject("tasks");
            JSONArray arr = tasks.getJSONArray("task");
            Assert.assertEquals("incorrect number of elements", 1, arr.length());
            verifyAMTask(arr, jobsMap.get(id), type);
        }
    }

    @Test
    public void testTasksQueryInvalid() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            // tasktype must be exactly either "m" or "r"
            String tasktype = "reduce";
            try {
                r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").queryParam("type", tasktype).accept(APPLICATION_JSON).get(JSONObject.class);
                Assert.fail("should have thrown exception on invalid uri");
            } catch (UniformInterfaceException ue) {
                ClientResponse response = ue.getResponse();
                WebServicesTestUtils.assertResponseStatusCode(BAD_REQUEST, response.getStatusInfo());
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject msg = response.getEntity(JSONObject.class);
                JSONObject exception = msg.getJSONObject("RemoteException");
                Assert.assertEquals("incorrect number of elements", 3, exception.length());
                String message = exception.getString("message");
                String type = exception.getString("exception");
                String classname = exception.getString("javaClassName");
                WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: tasktype must be either m or r", message);
                WebServicesTestUtils.checkStringMatch("exception type", "BadRequestException", type);
                WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.BadRequestException", classname);
            }
        }
    }

    @Test
    public void testTaskId() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).accept(APPLICATION_JSON).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                Assert.assertEquals("incorrect number of elements", 1, json.length());
                JSONObject info = json.getJSONObject("task");
                verifyAMSingleTask(info, task);
            }
        }
    }

    @Test
    public void testTaskIdSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path((tid + "/")).accept(APPLICATION_JSON).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                Assert.assertEquals("incorrect number of elements", 1, json.length());
                JSONObject info = json.getJSONObject("task");
                verifyAMSingleTask(info, task);
            }
        }
    }

    @Test
    public void testTaskIdDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                Assert.assertEquals("incorrect number of elements", 1, json.length());
                JSONObject info = json.getJSONObject("task");
                verifyAMSingleTask(info, task);
            }
        }
    }

    @Test
    public void testTaskIdBogus() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String tid = "bogustaskid";
            try {
                r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).get(JSONObject.class);
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
                WebServicesTestUtils.checkStringEqual("exception message", ((("java.lang.Exception: TaskId string : " + ("bogustaskid is not properly formed" + "\nReason: java.util.regex.Matcher[pattern=")) + (TaskID.TASK_ID_REGEX)) + " region=0,11 lastmatch=]"), message);
                WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
                WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
            }
        }
    }

    @Test
    public void testTaskIdNonExist() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String tid = "task_0_0000_m_000000";
            try {
                r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).get(JSONObject.class);
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
                WebServicesTestUtils.checkStringMatch("exception message", "java.lang.Exception: task not found with id task_0_0000_m_000000", message);
                WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
                WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
            }
        }
    }

    @Test
    public void testTaskIdInvalid() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String tid = "task_0_0000_d_000000";
            try {
                r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).get(JSONObject.class);
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
                WebServicesTestUtils.checkStringEqual("exception message", ((("java.lang.Exception: TaskId string : " + ("task_0_0000_d_000000 is not properly formed" + "\nReason: java.util.regex.Matcher[pattern=")) + (TaskID.TASK_ID_REGEX)) + " region=0,20 lastmatch=]"), message);
                WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
                WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
            }
        }
    }

    @Test
    public void testTaskIdInvalid2() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String tid = "task_0_m_000000";
            try {
                r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).get(JSONObject.class);
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
                WebServicesTestUtils.checkStringEqual("exception message", ((("java.lang.Exception: TaskId string : " + ("task_0_m_000000 is not properly formed" + "\nReason: java.util.regex.Matcher[pattern=")) + (TaskID.TASK_ID_REGEX)) + " region=0,15 lastmatch=]"), message);
                WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
                WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
            }
        }
    }

    @Test
    public void testTaskIdInvalid3() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            String tid = "task_0_0000_m";
            try {
                r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).get(JSONObject.class);
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
                WebServicesTestUtils.checkStringEqual("exception message", ((("java.lang.Exception: TaskId string : " + ("task_0_0000_m is not properly formed" + "\nReason: java.util.regex.Matcher[pattern=")) + (TaskID.TASK_ID_REGEX)) + " region=0,13 lastmatch=]"), message);
                WebServicesTestUtils.checkStringMatch("exception type", "NotFoundException", type);
                WebServicesTestUtils.checkStringMatch("exception classname", "org.apache.hadoop.yarn.webapp.NotFoundException", classname);
            }
        }
    }

    @Test
    public void testTaskIdXML() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).accept(APPLICATION_XML).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                String xml = response.getEntity(String.class);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                Document dom = db.parse(is);
                NodeList nodes = dom.getElementsByTagName("task");
                for (int i = 0; i < (nodes.getLength()); i++) {
                    Element element = ((Element) (nodes.item(i)));
                    verifyAMSingleTaskXML(element, task);
                }
            }
        }
    }

    @Test
    public void testTaskIdCounters() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("counters").accept(APPLICATION_JSON).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                Assert.assertEquals("incorrect number of elements", 1, json.length());
                JSONObject info = json.getJSONObject("jobTaskCounters");
                verifyAMJobTaskCounters(info, task);
            }
        }
    }

    @Test
    public void testTaskIdCountersSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("counters/").accept(APPLICATION_JSON).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                Assert.assertEquals("incorrect number of elements", 1, json.length());
                JSONObject info = json.getJSONObject("jobTaskCounters");
                verifyAMJobTaskCounters(info, task);
            }
        }
    }

    @Test
    public void testTaskIdCountersDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("counters").get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                Assert.assertEquals("incorrect number of elements", 1, json.length());
                JSONObject info = json.getJSONObject("jobTaskCounters");
                verifyAMJobTaskCounters(info, task);
            }
        }
    }

    @Test
    public void testJobTaskCountersXML() throws Exception {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestAMWebServicesTasks.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("counters").accept(APPLICATION_XML).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                String xml = response.getEntity(String.class);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                Document dom = db.parse(is);
                NodeList info = dom.getElementsByTagName("jobTaskCounters");
                verifyAMTaskCountersXML(info, task);
            }
        }
    }
}

