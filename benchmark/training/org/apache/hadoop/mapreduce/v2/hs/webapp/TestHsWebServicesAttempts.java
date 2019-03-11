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
package org.apache.hadoop.mapreduce.v2.hs.webapp;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_XML;
import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.client.ClientResponse;
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
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.hs.HistoryContext;
import org.apache.hadoop.mapreduce.v2.hs.MockHistoryContext;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.yarn.webapp.GenericExceptionHandler;
import org.apache.hadoop.yarn.webapp.GuiceServletConfig;
import org.apache.hadoop.yarn.webapp.JerseyTestBase;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Test the history server Rest API for getting task attempts, a
 * specific task attempt, and task attempt counters
 *
 * /ws/v1/history/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts
 * /ws/v1/history/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts/{attemptid}
 * /ws/v1/history/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts/{attemptid}/
 * counters
 */
public class TestHsWebServicesAttempts extends JerseyTestBase {
    private static Configuration conf = new Configuration();

    private static HistoryContext appContext;

    private static HsWebApp webApp;

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            TestHsWebServicesAttempts.appContext = new MockHistoryContext(0, 1, 2, 1);
            TestHsWebServicesAttempts.webApp = Mockito.mock(HsWebApp.class);
            Mockito.when(TestHsWebServicesAttempts.webApp.name()).thenReturn("hsmockwebapp");
            bind(JAXBContextResolver.class);
            bind(HsWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(WebApp.class).toInstance(TestHsWebServicesAttempts.webApp);
            bind(AppContext.class).toInstance(TestHsWebServicesAttempts.appContext);
            bind(HistoryContext.class).toInstance(TestHsWebServicesAttempts.appContext);
            bind(Configuration.class).toInstance(TestHsWebServicesAttempts.conf);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestHsWebServicesAttempts.WebServletModule()));
    }

    public TestHsWebServicesAttempts() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.mapreduce.v2.hs.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testTaskAttempts() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").accept(APPLICATION_JSON).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                verifyHsTaskAttempts(json, task);
            }
        }
    }

    @Test
    public void testTaskAttemptsSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts/").accept(APPLICATION_JSON).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                verifyHsTaskAttempts(json, task);
            }
        }
    }

    @Test
    public void testTaskAttemptsDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                JSONObject json = response.getEntity(JSONObject.class);
                verifyHsTaskAttempts(json, task);
            }
        }
    }

    @Test
    public void testTaskAttemptsXML() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").accept(APPLICATION_XML).get(ClientResponse.class);
                Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                String xml = response.getEntity(String.class);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                Document dom = db.parse(is);
                NodeList attempts = dom.getElementsByTagName("taskAttempts");
                Assert.assertEquals("incorrect number of elements", 1, attempts.getLength());
                NodeList nodes = dom.getElementsByTagName("taskAttempt");
                verifyHsTaskAttemptsXML(nodes, task);
            }
        }
    }

    @Test
    public void testTaskAttemptId() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                for (TaskAttempt att : task.getAttempts().values()) {
                    TaskAttemptId attemptid = att.getID();
                    String attid = MRApps.toString(attemptid);
                    ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").path(attid).accept(APPLICATION_JSON).get(ClientResponse.class);
                    Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    JSONObject json = response.getEntity(JSONObject.class);
                    Assert.assertEquals("incorrect number of elements", 1, json.length());
                    JSONObject info = json.getJSONObject("taskAttempt");
                    verifyHsTaskAttempt(info, att, task.getType());
                }
            }
        }
    }

    @Test
    public void testTaskAttemptIdSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                for (TaskAttempt att : task.getAttempts().values()) {
                    TaskAttemptId attemptid = att.getID();
                    String attid = MRApps.toString(attemptid);
                    ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").path((attid + "/")).accept(APPLICATION_JSON).get(ClientResponse.class);
                    Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    JSONObject json = response.getEntity(JSONObject.class);
                    Assert.assertEquals("incorrect number of elements", 1, json.length());
                    JSONObject info = json.getJSONObject("taskAttempt");
                    verifyHsTaskAttempt(info, att, task.getType());
                }
            }
        }
    }

    @Test
    public void testTaskAttemptIdDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                for (TaskAttempt att : task.getAttempts().values()) {
                    TaskAttemptId attemptid = att.getID();
                    String attid = MRApps.toString(attemptid);
                    ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").path(attid).get(ClientResponse.class);
                    Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    JSONObject json = response.getEntity(JSONObject.class);
                    Assert.assertEquals("incorrect number of elements", 1, json.length());
                    JSONObject info = json.getJSONObject("taskAttempt");
                    verifyHsTaskAttempt(info, att, task.getType());
                }
            }
        }
    }

    @Test
    public void testTaskAttemptIdXML() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                for (TaskAttempt att : task.getAttempts().values()) {
                    TaskAttemptId attemptid = att.getID();
                    String attid = MRApps.toString(attemptid);
                    ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").path(attid).accept(APPLICATION_XML).get(ClientResponse.class);
                    Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    String xml = response.getEntity(String.class);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(xml));
                    Document dom = db.parse(is);
                    NodeList nodes = dom.getElementsByTagName("taskAttempt");
                    for (int i = 0; i < (nodes.getLength()); i++) {
                        Element element = ((Element) (nodes.item(i)));
                        verifyHsTaskAttemptXML(element, att, task.getType());
                    }
                }
            }
        }
    }

    @Test
    public void testTaskAttemptIdBogus() throws Exception, JSONException {
        testTaskAttemptIdErrorGeneric("bogusid", ("java.lang.Exception: TaskAttemptId string : " + "bogusid is not properly formed"));
    }

    @Test
    public void testTaskAttemptIdNonExist() throws Exception, JSONException {
        testTaskAttemptIdErrorGeneric("attempt_0_1234_m_000000_0", "java.lang.Exception: Error getting info on task attempt id attempt_0_1234_m_000000_0");
    }

    @Test
    public void testTaskAttemptIdInvalid() throws Exception, JSONException {
        testTaskAttemptIdErrorGeneric("attempt_0_1234_d_000000_0", ("java.lang.Exception: Bad TaskType identifier. TaskAttemptId string : " + "attempt_0_1234_d_000000_0 is not properly formed."));
    }

    @Test
    public void testTaskAttemptIdInvalid2() throws Exception, JSONException {
        testTaskAttemptIdErrorGeneric("attempt_1234_m_000000_0", ("java.lang.Exception: TaskAttemptId string : " + "attempt_1234_m_000000_0 is not properly formed"));
    }

    @Test
    public void testTaskAttemptIdInvalid3() throws Exception, JSONException {
        testTaskAttemptIdErrorGeneric("attempt_0_1234_m_000000", ("java.lang.Exception: TaskAttemptId string : " + "attempt_0_1234_m_000000 is not properly formed"));
    }

    @Test
    public void testTaskAttemptIdCounters() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                for (TaskAttempt att : task.getAttempts().values()) {
                    TaskAttemptId attemptid = att.getID();
                    String attid = MRApps.toString(attemptid);
                    ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").path(attid).path("counters").accept(APPLICATION_JSON).get(ClientResponse.class);
                    Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    JSONObject json = response.getEntity(JSONObject.class);
                    Assert.assertEquals("incorrect number of elements", 1, json.length());
                    JSONObject info = json.getJSONObject("jobTaskAttemptCounters");
                    verifyHsJobTaskAttemptCounters(info, att);
                }
            }
        }
    }

    @Test
    public void testTaskAttemptIdXMLCounters() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesAttempts.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            for (Task task : jobsMap.get(id).getTasks().values()) {
                String tid = MRApps.toString(task.getID());
                for (TaskAttempt att : task.getAttempts().values()) {
                    TaskAttemptId attemptid = att.getID();
                    String attid = MRApps.toString(attemptid);
                    ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("tasks").path(tid).path("attempts").path(attid).path("counters").accept(APPLICATION_XML).get(ClientResponse.class);
                    Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
                    String xml = response.getEntity(String.class);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(xml));
                    Document dom = db.parse(is);
                    NodeList nodes = dom.getElementsByTagName("jobTaskAttemptCounters");
                    verifyHsTaskCountersXML(nodes, att);
                }
            }
        }
    }
}

