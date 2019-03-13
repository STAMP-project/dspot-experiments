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
package org.apache.hadoop.yarn.server.webproxy;


import CommonConfigurationKeys.DEFAULT_HADOOP_HTTP_STATIC_USER;
import HttpServletResponse.SC_OK;
import MimeType.HTML;
import ProxyUriUtils.PROXY_PATH_SPEC;
import ProxyUriUtils.PROXY_SERVLET_NAME;
import ProxyUtils.LOCATION;
import YarnApplicationState.FINISHED;
import YarnApplicationState.KILLED;
import YarnApplicationState.RUNNING;
import YarnConfiguration.APPLICATION_HISTORY_ENABLED;
import YarnConfiguration.DEFAULT_YARN_ADMIN_ACL;
import YarnConfiguration.PROXY_ADDRESS;
import YarnConfiguration.RM_APPLICATION_HTTPS_POLICY;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.service.CompositeService;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static AppReportSource.AHS;
import static AppReportSource.RM;


/**
 * Test the WebAppProxyServlet and WebAppProxy. For back end use simple web
 * server.
 */
public class TestWebAppProxyServlet {
    private static final Logger LOG = LoggerFactory.getLogger(TestWebAppProxyServlet.class);

    private static Server server;

    private static int originalPort = 0;

    private static int numberOfHeaders = 0;

    private static final String UNKNOWN_HEADER = "Unknown-Header";

    private static boolean hasUnknownHeader = false;

    Configuration configuration = new Configuration();

    @SuppressWarnings("serial")
    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            int numHeaders = 0;
            TestWebAppProxyServlet.hasUnknownHeader = false;
            @SuppressWarnings("unchecked")
            Enumeration<String> names = req.getHeaderNames();
            while (names.hasMoreElements()) {
                String headerName = names.nextElement();
                if (headerName.equals(TestWebAppProxyServlet.UNKNOWN_HEADER)) {
                    TestWebAppProxyServlet.hasUnknownHeader = true;
                }
                ++numHeaders;
            } 
            TestWebAppProxyServlet.numberOfHeaders = numHeaders;
            resp.setStatus(SC_OK);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            InputStream is = req.getInputStream();
            OutputStream os = resp.getOutputStream();
            int c = is.read();
            while (c > (-1)) {
                os.write(c);
                c = is.read();
            } 
            is.close();
            os.close();
            resp.setStatus(SC_OK);
        }
    }

    @Test(timeout = 5000)
    public void testWebAppProxyServlet() throws Exception {
        configuration.set(PROXY_ADDRESS, "localhost:9090");
        // overriding num of web server threads, see HttpServer.HTTP_MAXTHREADS
        configuration.setInt("hadoop.http.max.threads", 10);
        TestWebAppProxyServlet.WebAppProxyServerForTest proxy = new TestWebAppProxyServlet.WebAppProxyServerForTest();
        proxy.init(configuration);
        start();
        int proxyPort = proxy.proxy.proxyServer.getConnectorAddress(0).getPort();
        TestWebAppProxyServlet.AppReportFetcherForTest appReportFetcher = proxy.proxy.appReportFetcher;
        // wrong url
        try {
            // wrong url without app ID
            URL emptyUrl = new URL((("http://localhost:" + proxyPort) + "/proxy"));
            HttpURLConnection emptyProxyConn = ((HttpURLConnection) (emptyUrl.openConnection()));
            emptyProxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, emptyProxyConn.getResponseCode());
            // wrong url. Set wrong app ID
            URL wrongUrl = new URL((("http://localhost:" + proxyPort) + "/proxy/app"));
            HttpURLConnection proxyConn = ((HttpURLConnection) (wrongUrl.openConnection()));
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, proxyConn.getResponseCode());
            // set true Application ID in url
            URL url = new URL((("http://localhost:" + proxyPort) + "/proxy/application_00_0"));
            proxyConn = ((HttpURLConnection) (url.openConnection()));
            // set cookie
            proxyConn.setRequestProperty("Cookie", "checked_application_0_0000=true");
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, proxyConn.getResponseCode());
            Assert.assertTrue(isResponseCookiePresent(proxyConn, "checked_application_0_0000", "true"));
            // test that redirection is squashed correctly
            URL redirectUrl = new URL((("http://localhost:" + proxyPort) + "/proxy/redirect/application_00_0"));
            proxyConn = ((HttpURLConnection) (redirectUrl.openConnection()));
            proxyConn.setInstanceFollowRedirects(false);
            proxyConn.connect();
            Assert.assertEquals(("The proxy returned an unexpected status code rather than" + "redirecting the connection (302)"), HttpURLConnection.HTTP_MOVED_TEMP, proxyConn.getResponseCode());
            String expected = (WebAppUtils.getResolvedRMWebAppURLWithScheme(configuration)) + "/cluster/failure/application_00_0";
            String redirect = proxyConn.getHeaderField(LOCATION);
            Assert.assertEquals(("The proxy did not redirect the connection to the failure " + "page of the RM"), expected, redirect);
            // cannot found application 1: null
            appReportFetcher.answer = 1;
            proxyConn = ((HttpURLConnection) (url.openConnection()));
            proxyConn.setRequestProperty("Cookie", "checked_application_0_0000=true");
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, proxyConn.getResponseCode());
            Assert.assertFalse(isResponseCookiePresent(proxyConn, "checked_application_0_0000", "true"));
            // cannot found application 2: ApplicationNotFoundException
            appReportFetcher.answer = 4;
            proxyConn = ((HttpURLConnection) (url.openConnection()));
            proxyConn.setRequestProperty("Cookie", "checked_application_0_0000=true");
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, proxyConn.getResponseCode());
            Assert.assertFalse(isResponseCookiePresent(proxyConn, "checked_application_0_0000", "true"));
            // wrong user
            appReportFetcher.answer = 2;
            proxyConn = ((HttpURLConnection) (url.openConnection()));
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, proxyConn.getResponseCode());
            String s = readInputStream(proxyConn.getInputStream());
            Assert.assertTrue(s.contains("to continue to an Application Master web interface owned by"));
            Assert.assertTrue(s.contains("WARNING: The following page may not be safe!"));
            // case if task has a not running status
            appReportFetcher.answer = 3;
            proxyConn = ((HttpURLConnection) (url.openConnection()));
            proxyConn.setRequestProperty("Cookie", "checked_application_0_0000=true");
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, proxyConn.getResponseCode());
            // test user-provided path and query parameter can be appended to the
            // original tracking url
            appReportFetcher.answer = 5;
            URL clientUrl = new URL((("http://localhost:" + proxyPort) + "/proxy/application_00_0/test/tez?x=y&h=p"));
            proxyConn = ((HttpURLConnection) (clientUrl.openConnection()));
            proxyConn.connect();
            TestWebAppProxyServlet.LOG.info(("" + (proxyConn.getURL())));
            TestWebAppProxyServlet.LOG.info(("ProxyConn.getHeaderField(): " + (proxyConn.getHeaderField(LOCATION))));
            Assert.assertEquals((("http://localhost:" + (TestWebAppProxyServlet.originalPort)) + "/foo/bar/test/tez?a=b&x=y&h=p#main"), proxyConn.getURL().toString());
        } finally {
            close();
        }
    }

    @Test(timeout = 5000)
    public void testAppReportForEmptyTrackingUrl() throws Exception {
        configuration.set(PROXY_ADDRESS, "localhost:9090");
        // overriding num of web server threads, see HttpServer.HTTP_MAXTHREADS
        configuration.setInt("hadoop.http.max.threads", 10);
        TestWebAppProxyServlet.WebAppProxyServerForTest proxy = new TestWebAppProxyServlet.WebAppProxyServerForTest();
        proxy.init(configuration);
        start();
        int proxyPort = proxy.proxy.proxyServer.getConnectorAddress(0).getPort();
        TestWebAppProxyServlet.AppReportFetcherForTest appReportFetcher = proxy.proxy.appReportFetcher;
        try {
            // set AHS_ENBALED = false to simulate getting the app report from RM
            configuration.setBoolean(APPLICATION_HISTORY_ENABLED, false);
            ApplicationId app = ApplicationId.newInstance(0, 0);
            appReportFetcher.answer = 6;
            URL url = new URL(((("http://localhost:" + proxyPort) + "/proxy/") + (app.toString())));
            HttpURLConnection proxyConn = ((HttpURLConnection) (url.openConnection()));
            proxyConn.connect();
            try {
                proxyConn.getResponseCode();
            } catch (ConnectException e) {
                // Connection Exception is expected as we have set
                // appReportFetcher.answer = 6, which does not set anything for
                // original tracking url field in the app report.
            }
            String appAddressInRm = (((WebAppUtils.getResolvedRMWebAppURLWithScheme(configuration)) + "/cluster") + "/app/") + (app.toString());
            Assert.assertTrue("Webapp proxy servlet should have redirected to RM", proxyConn.getURL().toString().equals(appAddressInRm));
            // set AHS_ENBALED = true to simulate getting the app report from AHS
            configuration.setBoolean(APPLICATION_HISTORY_ENABLED, true);
            proxyConn = ((HttpURLConnection) (url.openConnection()));
            proxyConn.connect();
            try {
                proxyConn.getResponseCode();
            } catch (ConnectException e) {
                // Connection Exception is expected as we have set
                // appReportFetcher.answer = 6, which does not set anything for
                // original tracking url field in the app report.
            }
            String appAddressInAhs = ((((WebAppUtils.getHttpSchemePrefix(configuration)) + (WebAppUtils.getAHSWebAppURLWithoutScheme(configuration))) + "/applicationhistory") + "/app/") + (app.toString());
            Assert.assertTrue("Webapp proxy servlet should have redirected to AHS", proxyConn.getURL().toString().equals(appAddressInAhs));
        } finally {
            close();
        }
    }

    @Test(timeout = 5000)
    public void testWebAppProxyPassThroughHeaders() throws Exception {
        Configuration configuration = new Configuration();
        configuration.set(PROXY_ADDRESS, "localhost:9091");
        configuration.setInt("hadoop.http.max.threads", 10);
        TestWebAppProxyServlet.WebAppProxyServerForTest proxy = new TestWebAppProxyServlet.WebAppProxyServerForTest();
        proxy.init(configuration);
        start();
        int proxyPort = proxy.proxy.proxyServer.getConnectorAddress(0).getPort();
        try {
            URL url = new URL((("http://localhost:" + proxyPort) + "/proxy/application_00_1"));
            HttpURLConnection proxyConn = ((HttpURLConnection) (url.openConnection()));
            // set headers
            proxyConn.addRequestProperty("Origin", "http://www.someurl.com");
            proxyConn.addRequestProperty("Access-Control-Request-Method", "GET");
            proxyConn.addRequestProperty("Access-Control-Request-Headers", "Authorization");
            proxyConn.addRequestProperty(TestWebAppProxyServlet.UNKNOWN_HEADER, "unknown");
            // Verify if four headers mentioned above have been added
            Assert.assertEquals(proxyConn.getRequestProperties().size(), 4);
            proxyConn.connect();
            Assert.assertEquals(HttpURLConnection.HTTP_OK, proxyConn.getResponseCode());
            // Verify if number of headers received by end server is 9.
            // This should match WebAppProxyServlet#PASS_THROUGH_HEADERS.
            // Nine headers include Accept, Host, Connection, User-Agent, Cookie,
            // Origin, Access-Control-Request-Method, Accept-Encoding, and
            // Access-Control-Request-Headers. Pls note that Unknown-Header is dropped
            // by proxy as it is not in the list of allowed headers.
            Assert.assertEquals(TestWebAppProxyServlet.numberOfHeaders, 9);
            Assert.assertFalse(TestWebAppProxyServlet.hasUnknownHeader);
        } finally {
            close();
        }
    }

    /**
     * Test main method of WebAppProxyServer
     */
    @Test(timeout = 5000)
    public void testWebAppProxyServerMainMethod() throws Exception {
        WebAppProxyServer mainServer = null;
        Configuration conf = new YarnConfiguration();
        conf.set(PROXY_ADDRESS, "localhost:9099");
        try {
            mainServer = WebAppProxyServer.startServer(conf);
            int counter = 20;
            URL wrongUrl = new URL("http://localhost:9099/proxy/app");
            HttpURLConnection proxyConn = null;
            while (counter > 0) {
                counter--;
                try {
                    proxyConn = ((HttpURLConnection) (wrongUrl.openConnection()));
                    proxyConn.connect();
                    proxyConn.getResponseCode();
                    // server started ok
                    counter = 0;
                } catch (Exception e) {
                    Thread.sleep(100);
                }
            } 
            Assert.assertNotNull(proxyConn);
            // wrong application Id
            Assert.assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, proxyConn.getResponseCode());
        } finally {
            if (mainServer != null) {
                mainServer.stop();
            }
        }
    }

    @Test(timeout = 5000)
    public void testCheckHttpsStrictAndNotProvided() throws Exception {
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        Mockito.when(resp.getWriter()).thenReturn(new PrintWriter(sw));
        YarnConfiguration conf = new YarnConfiguration();
        final URI httpLink = new URI("http://foo.com");
        final URI httpsLink = new URI("https://foo.com");
        // NONE policy
        conf.set(RM_APPLICATION_HTTPS_POLICY, "NONE");
        Assert.assertFalse(WebAppProxyServlet.checkHttpsStrictAndNotProvided(resp, httpsLink, conf));
        Assert.assertEquals("", sw.toString());
        Mockito.verify(resp, Mockito.times(0)).setContentType(Mockito.any());
        Assert.assertFalse(WebAppProxyServlet.checkHttpsStrictAndNotProvided(resp, httpLink, conf));
        Assert.assertEquals("", sw.toString());
        Mockito.verify(resp, Mockito.times(0)).setContentType(Mockito.any());
        // LENIENT policy
        conf.set(RM_APPLICATION_HTTPS_POLICY, "LENIENT");
        Assert.assertFalse(WebAppProxyServlet.checkHttpsStrictAndNotProvided(resp, httpsLink, conf));
        Assert.assertEquals("", sw.toString());
        Mockito.verify(resp, Mockito.times(0)).setContentType(Mockito.any());
        Assert.assertFalse(WebAppProxyServlet.checkHttpsStrictAndNotProvided(resp, httpLink, conf));
        Assert.assertEquals("", sw.toString());
        Mockito.verify(resp, Mockito.times(0)).setContentType(Mockito.any());
        // STRICT policy
        conf.set(RM_APPLICATION_HTTPS_POLICY, "STRICT");
        Assert.assertFalse(WebAppProxyServlet.checkHttpsStrictAndNotProvided(resp, httpsLink, conf));
        Assert.assertEquals("", sw.toString());
        Mockito.verify(resp, Mockito.times(0)).setContentType(Mockito.any());
        Assert.assertTrue(WebAppProxyServlet.checkHttpsStrictAndNotProvided(resp, httpLink, conf));
        String s = sw.toString();
        Assert.assertTrue((("Was expecting an HTML page explaining that an HTTPS tracking" + " url must be used but found ") + s), s.contains("HTTPS must be used"));
        Mockito.verify(resp, Mockito.times(1)).setContentType(HTML);
    }

    private class WebAppProxyServerForTest extends CompositeService {
        private TestWebAppProxyServlet.WebAppProxyForTest proxy = null;

        public WebAppProxyServerForTest() {
            super(WebAppProxyServer.class.getName());
        }

        @Override
        public synchronized void serviceInit(Configuration conf) throws Exception {
            proxy = new TestWebAppProxyServlet.WebAppProxyForTest();
            addService(proxy);
            super.serviceInit(conf);
        }
    }

    private class WebAppProxyForTest extends WebAppProxy {
        HttpServer2 proxyServer;

        TestWebAppProxyServlet.AppReportFetcherForTest appReportFetcher;

        @Override
        protected void serviceStart() throws Exception {
            Configuration conf = getConfig();
            String bindAddress = conf.get(PROXY_ADDRESS);
            bindAddress = StringUtils.split(bindAddress, ':')[0];
            AccessControlList acl = new AccessControlList(conf.get(YARN_ADMIN_ACL, DEFAULT_YARN_ADMIN_ACL));
            proxyServer = new HttpServer2.Builder().setName("proxy").addEndpoint(URI.create((((WebAppUtils.getHttpSchemePrefix(conf)) + bindAddress) + ":0"))).setFindPort(true).setConf(conf).setACL(acl).build();
            proxyServer.addServlet(PROXY_SERVLET_NAME, PROXY_PATH_SPEC, WebAppProxyServlet.class);
            appReportFetcher = new TestWebAppProxyServlet.AppReportFetcherForTest(conf);
            proxyServer.setAttribute(FETCHER_ATTRIBUTE, appReportFetcher);
            proxyServer.setAttribute(IS_SECURITY_ENABLED_ATTRIBUTE, Boolean.TRUE);
            String proxy = WebAppUtils.getProxyHostAndPort(conf);
            String[] proxyParts = proxy.split(":");
            String proxyHost = proxyParts[0];
            proxyServer.setAttribute(PROXY_HOST_ATTRIBUTE, proxyHost);
            proxyServer.start();
            TestWebAppProxyServlet.LOG.info("Proxy server is started at port {}", proxyServer.getConnectorAddress(0).getPort());
        }
    }

    private class AppReportFetcherForTest extends AppReportFetcher {
        int answer = 0;

        public AppReportFetcherForTest(Configuration conf) {
            super(conf);
        }

        public FetchedAppReport getApplicationReport(ApplicationId appId) throws YarnException {
            if ((answer) == 0) {
                return getDefaultApplicationReport(appId);
            } else
                if ((answer) == 1) {
                    return null;
                } else
                    if ((answer) == 2) {
                        FetchedAppReport result = getDefaultApplicationReport(appId);
                        result.getApplicationReport().setUser("user");
                        return result;
                    } else
                        if ((answer) == 3) {
                            FetchedAppReport result = getDefaultApplicationReport(appId);
                            result.getApplicationReport().setYarnApplicationState(KILLED);
                            return result;
                        } else
                            if ((answer) == 4) {
                                throw new ApplicationNotFoundException("Application is not found");
                            } else
                                if ((answer) == 5) {
                                    // test user-provided path and query parameter can be appended to the
                                    // original tracking url
                                    FetchedAppReport result = getDefaultApplicationReport(appId);
                                    result.getApplicationReport().setOriginalTrackingUrl((("localhost:" + (TestWebAppProxyServlet.originalPort)) + "/foo/bar?a=b#main"));
                                    result.getApplicationReport().setYarnApplicationState(FINISHED);
                                    return result;
                                } else
                                    if ((answer) == 6) {
                                        return getDefaultApplicationReport(appId, false);
                                    }






            return null;
        }

        /* If this method is called with isTrackingUrl=false, no tracking url
        will set in the app report. Hence, there will be a connection exception
        when the prxyCon tries to connect.
         */
        private FetchedAppReport getDefaultApplicationReport(ApplicationId appId, boolean isTrackingUrl) {
            FetchedAppReport fetchedReport;
            ApplicationReport result = new ApplicationReportPBImpl();
            result.setApplicationId(appId);
            result.setYarnApplicationState(RUNNING);
            result.setUser(DEFAULT_HADOOP_HTTP_STATIC_USER);
            if (isTrackingUrl) {
                result.setOriginalTrackingUrl((("localhost:" + (TestWebAppProxyServlet.originalPort)) + "/foo/bar"));
            }
            if (configuration.getBoolean(APPLICATION_HISTORY_ENABLED, false)) {
                fetchedReport = new FetchedAppReport(result, AHS);
            } else {
                fetchedReport = new FetchedAppReport(result, RM);
            }
            return fetchedReport;
        }

        private FetchedAppReport getDefaultApplicationReport(ApplicationId appId) {
            return getDefaultApplicationReport(appId, true);
        }
    }
}

