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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.http.JettyUtils;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Test the history server Rest API for getting the job conf. This
 * requires created a temporary configuration file.
 *
 *   /ws/v1/history/mapreduce/jobs/{jobid}/conf
 */
public class TestHsWebServicesJobConf extends JerseyTestBase {
    private static Configuration conf = new Configuration();

    private static HistoryContext appContext;

    private static HsWebApp webApp;

    private static File testConfDir = new File("target", ((TestHsWebServicesJobConf.class.getSimpleName()) + "confDir"));

    private static class WebServletModule extends ServletModule {
        @Override
        protected void configureServlets() {
            Path confPath = new Path(TestHsWebServicesJobConf.testConfDir.toString(), MRJobConfig.JOB_CONF_FILE);
            Configuration config = new Configuration();
            FileSystem localFs;
            try {
                localFs = FileSystem.getLocal(config);
                confPath = localFs.makeQualified(confPath);
                OutputStream out = localFs.create(confPath);
                try {
                    TestHsWebServicesJobConf.conf.writeXml(out);
                } finally {
                    out.close();
                }
                if (!(localFs.exists(confPath))) {
                    Assert.fail(("error creating config file: " + confPath));
                }
            } catch (IOException e) {
                Assert.fail(("error creating config file: " + (e.getMessage())));
            }
            TestHsWebServicesJobConf.appContext = new MockHistoryContext(0, 2, 1, confPath);
            TestHsWebServicesJobConf.webApp = Mockito.mock(HsWebApp.class);
            Mockito.when(TestHsWebServicesJobConf.webApp.name()).thenReturn("hsmockwebapp");
            bind(JAXBContextResolver.class);
            bind(HsWebServices.class);
            bind(GenericExceptionHandler.class);
            bind(WebApp.class).toInstance(TestHsWebServicesJobConf.webApp);
            bind(AppContext.class).toInstance(TestHsWebServicesJobConf.appContext);
            bind(HistoryContext.class).toInstance(TestHsWebServicesJobConf.appContext);
            bind(Configuration.class).toInstance(TestHsWebServicesJobConf.conf);
            serve("/*").with(GuiceContainer.class);
        }
    }

    static {
        GuiceServletConfig.setInjector(Guice.createInjector(new TestHsWebServicesJobConf.WebServletModule()));
    }

    public TestHsWebServicesJobConf() {
        super(new WebAppDescriptor.Builder("org.apache.hadoop.mapreduce.v2.hs.webapp").contextListenerClass(GuiceServletConfig.class).filterClass(GuiceFilter.class).contextPath("jersey-guice-filter").servletPath("/").build());
    }

    @Test
    public void testJobConf() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesJobConf.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("conf").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("conf");
            verifyHsJobConf(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobConfSlash() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesJobConf.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("conf/").accept(APPLICATION_JSON).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("conf");
            verifyHsJobConf(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobConfDefault() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesJobConf.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("conf").get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_JSON_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            JSONObject json = response.getEntity(JSONObject.class);
            Assert.assertEquals("incorrect number of elements", 1, json.length());
            JSONObject info = json.getJSONObject("conf");
            verifyHsJobConf(info, jobsMap.get(id));
        }
    }

    @Test
    public void testJobConfXML() throws Exception, JSONException {
        WebResource r = resource();
        Map<JobId, Job> jobsMap = TestHsWebServicesJobConf.appContext.getAllJobs();
        for (JobId id : jobsMap.keySet()) {
            String jobId = MRApps.toString(id);
            ClientResponse response = r.path("ws").path("v1").path("history").path("mapreduce").path("jobs").path(jobId).path("conf").accept(APPLICATION_XML).get(ClientResponse.class);
            Assert.assertEquals((((MediaType.APPLICATION_XML_TYPE) + "; ") + (JettyUtils.UTF_8)), response.getType().toString());
            String xml = response.getEntity(String.class);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document dom = db.parse(is);
            NodeList info = dom.getElementsByTagName("conf");
            verifyHsJobConfXML(info, jobsMap.get(id));
        }
    }
}

