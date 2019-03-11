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
package org.apache.hadoop.mapred;


import HttpServletResponse.SC_BAD_REQUEST;
import HttpServletResponse.SC_OK;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.fs.Path;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base class to test Job end notification in local and cluster mode.
 *
 * Starts up hadoop on Local or Cluster mode (by extending of the
 * HadoopTestCase class) and it starts a servlet engine that hosts
 * a servlet that will receive the notification of job finalization.
 *
 * The notification servlet returns a HTTP 400 the first time is called
 * and a HTTP 200 the second time, thus testing retry.
 *
 * In both cases local file system is used (this is irrelevant for
 * the tested functionality)
 */
public abstract class NotificationTestCase extends HadoopTestCase {
    protected NotificationTestCase(int mode) throws IOException {
        super(mode, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    private int port;

    private String contextPath = "/notification";

    private String servletPath = "/mapred";

    private Server webServer;

    public static class NotificationServlet extends HttpServlet {
        public static volatile int counter = 0;

        public static volatile int failureCounter = 0;

        private static final long serialVersionUID = 1L;

        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
            String queryString = req.getQueryString();
            switch (NotificationTestCase.NotificationServlet.counter) {
                case 0 :
                    verifyQuery(queryString, "SUCCEEDED");
                    break;
                case 2 :
                    verifyQuery(queryString, "KILLED");
                    break;
                case 4 :
                    verifyQuery(queryString, "FAILED");
                    break;
            }
            if (((NotificationTestCase.NotificationServlet.counter) % 2) == 0) {
                res.sendError(SC_BAD_REQUEST, "forcing error");
            } else {
                res.setStatus(SC_OK);
            }
            (NotificationTestCase.NotificationServlet.counter)++;
        }

        protected void verifyQuery(String query, String expected) throws IOException {
            if (query.contains(expected)) {
                return;
            }
            (NotificationTestCase.NotificationServlet.failureCounter)++;
            Assert.assertTrue(((("The request (" + query) + ") does not contain ") + expected), false);
        }
    }

    @Test
    public void testMR() throws Exception {
        System.out.println(launchWordCount(this.createJobConf(), "a b c d e f g h", 1, 1));
        boolean keepTrying = true;
        for (int tries = 0; (tries < 30) && keepTrying; tries++) {
            Thread.sleep(50);
            keepTrying = !((NotificationTestCase.NotificationServlet.counter) == 2);
        }
        Assert.assertEquals(2, NotificationTestCase.NotificationServlet.counter);
        Assert.assertEquals(0, NotificationTestCase.NotificationServlet.failureCounter);
        Path inDir = new Path("notificationjob/input");
        Path outDir = new Path("notificationjob/output");
        // Hack for local FS that does not have the concept of a 'mounting point'
        if (isLocalFS()) {
            String localPathRoot = System.getProperty("test.build.data", "/tmp").toString().replace(' ', '+');
            inDir = new Path(localPathRoot, inDir);
            outDir = new Path(localPathRoot, outDir);
        }
        // run a job with KILLED status
        System.out.println(UtilsForTests.runJobKill(this.createJobConf(), inDir, outDir).getID());
        keepTrying = true;
        for (int tries = 0; (tries < 30) && keepTrying; tries++) {
            Thread.sleep(50);
            keepTrying = !((NotificationTestCase.NotificationServlet.counter) == 4);
        }
        Assert.assertEquals(4, NotificationTestCase.NotificationServlet.counter);
        Assert.assertEquals(0, NotificationTestCase.NotificationServlet.failureCounter);
        // run a job with FAILED status
        System.out.println(UtilsForTests.runJobFail(this.createJobConf(), inDir, outDir).getID());
        keepTrying = true;
        for (int tries = 0; (tries < 30) && keepTrying; tries++) {
            Thread.sleep(50);
            keepTrying = !((NotificationTestCase.NotificationServlet.counter) == 6);
        }
        Assert.assertEquals(6, NotificationTestCase.NotificationServlet.counter);
        Assert.assertEquals(0, NotificationTestCase.NotificationServlet.failureCounter);
    }
}

