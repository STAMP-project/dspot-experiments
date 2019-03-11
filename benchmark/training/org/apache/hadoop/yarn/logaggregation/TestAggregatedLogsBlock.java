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
package org.apache.hadoop.yarn.logaggregation;


import HtmlBlock.Block;
import YarnConfiguration.DEFAULT_NM_WEBAPP_ADDRESS;
import YarnConfiguration.NM_WEBAPP_ADDRESS;
import com.google.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.yarn.logaggregation.filecontroller.tfile.TFileAggregatedLogsBlock;
import org.apache.hadoop.yarn.webapp.View.ViewContext;
import org.apache.hadoop.yarn.webapp.log.AggregatedLogsBlockForTest;
import org.apache.hadoop.yarn.webapp.view.BlockForTest;
import org.apache.hadoop.yarn.webapp.view.HtmlBlock;
import org.apache.hadoop.yarn.webapp.view.HtmlBlockForTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test AggregatedLogsBlock. AggregatedLogsBlock should check user, aggregate a
 * logs into one file and show this logs or errors into html code
 */
public class TestAggregatedLogsBlock {
    /**
     * Bad user. User 'owner' is trying to read logs without access
     */
    @Test
    public void testAccessDenied() throws Exception {
        FileUtil.fullyDelete(new File("target/logs"));
        Configuration configuration = getConfiguration();
        writeLogs("target/logs/logs/application_0_0001/container_0_0001_01_000001");
        writeLog(configuration, "owner");
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(data);
        HtmlBlock html = new HtmlBlockForTest();
        HtmlBlock.Block block = new BlockForTest(html, printWriter, 10, false);
        TestAggregatedLogsBlock.TFileAggregatedLogsBlockForTest aggregatedBlock = getTFileAggregatedLogsBlockForTest(configuration, "owner", "container_0_0001_01_000001", "localhost:1234");
        aggregatedBlock.render(block);
        block.getWriter().flush();
        String out = data.toString();
        Assert.assertTrue(out.contains("User [owner] is not authorized to view the logs for entity"));
    }

    @Test
    public void testBlockContainsPortNumForUnavailableAppLog() {
        FileUtil.fullyDelete(new File("target/logs"));
        Configuration configuration = getConfiguration();
        String nodeName = configuration.get(NM_WEBAPP_ADDRESS, DEFAULT_NM_WEBAPP_ADDRESS);
        AggregatedLogsBlockForTest aggregatedBlock = getAggregatedLogsBlockForTest(configuration, "admin", "container_0_0001_01_000001", nodeName);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(data);
        HtmlBlock html = new HtmlBlockForTest();
        HtmlBlock.Block block = new BlockForTest(html, printWriter, 10, false);
        aggregatedBlock.render(block);
        block.getWriter().flush();
        String out = data.toString();
        Assert.assertTrue(out.contains(nodeName));
    }

    /**
     * try to read bad logs
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBadLogs() throws Exception {
        FileUtil.fullyDelete(new File("target/logs"));
        Configuration configuration = getConfiguration();
        writeLogs("target/logs/logs/application_0_0001/container_0_0001_01_000001");
        writeLog(configuration, "owner");
        AggregatedLogsBlockForTest aggregatedBlock = getAggregatedLogsBlockForTest(configuration, "admin", "container_0_0001_01_000001");
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(data);
        HtmlBlock html = new HtmlBlockForTest();
        HtmlBlock.Block block = new BlockForTest(html, printWriter, 10, false);
        aggregatedBlock.render(block);
        block.getWriter().flush();
        String out = data.toString();
        Assert.assertTrue(out.contains("Logs not available for entity. Aggregation may not be complete, Check back later or try the nodemanager at localhost:1234"));
    }

    /**
     * Reading from logs should succeed and they should be shown in the
     * AggregatedLogsBlock html.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAggregatedLogsBlock() throws Exception {
        FileUtil.fullyDelete(new File("target/logs"));
        Configuration configuration = getConfiguration();
        writeLogs("target/logs/logs/application_0_0001/container_0_0001_01_000001");
        writeLog(configuration, "admin");
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(data);
        HtmlBlock html = new HtmlBlockForTest();
        HtmlBlock.Block block = new BlockForTest(html, printWriter, 10, false);
        TestAggregatedLogsBlock.TFileAggregatedLogsBlockForTest aggregatedBlock = getTFileAggregatedLogsBlockForTest(configuration, "admin", "container_0_0001_01_000001", "localhost:1234");
        aggregatedBlock.render(block);
        block.getWriter().flush();
        String out = data.toString();
        Assert.assertTrue(out.contains("test log1"));
        Assert.assertTrue(out.contains("test log2"));
        Assert.assertTrue(out.contains("test log3"));
    }

    /**
     * Reading from logs should succeed (from a HAR archive) and they should be
     * shown in the AggregatedLogsBlock html.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAggregatedLogsBlockHar() throws Exception {
        FileUtil.fullyDelete(new File("target/logs"));
        Configuration configuration = getConfiguration();
        URL harUrl = ClassLoader.getSystemClassLoader().getResource("application_1440536969523_0001.har");
        Assert.assertNotNull(harUrl);
        String path = "target/logs/admin/logs/application_1440536969523_0001" + "/application_1440536969523_0001.har";
        FileUtils.copyDirectory(new File(harUrl.getPath()), new File(path));
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(data);
        HtmlBlock html = new HtmlBlockForTest();
        HtmlBlock.Block block = new BlockForTest(html, printWriter, 10, false);
        TestAggregatedLogsBlock.TFileAggregatedLogsBlockForTest aggregatedBlock = getTFileAggregatedLogsBlockForTest(configuration, "admin", "container_1440536969523_0001_01_000001", "host1:1111");
        aggregatedBlock.render(block);
        block.getWriter().flush();
        String out = data.toString();
        Assert.assertTrue(out.contains("Hello stderr"));
        Assert.assertTrue(out.contains("Hello stdout"));
        Assert.assertTrue(out.contains("Hello syslog"));
        aggregatedBlock = getTFileAggregatedLogsBlockForTest(configuration, "admin", "container_1440536969523_0001_01_000002", "host2:2222");
        data = new ByteArrayOutputStream();
        printWriter = new PrintWriter(data);
        html = new HtmlBlockForTest();
        block = new BlockForTest(html, printWriter, 10, false);
        aggregatedBlock.render(block);
        block.getWriter().flush();
        out = data.toString();
        Assert.assertTrue(out.contains("Goodbye stderr"));
        Assert.assertTrue(out.contains("Goodbye stdout"));
        Assert.assertTrue(out.contains("Goodbye syslog"));
    }

    /**
     * Log files was deleted.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoLogs() throws Exception {
        FileUtil.fullyDelete(new File("target/logs"));
        Configuration configuration = getConfiguration();
        File f = new File("target/logs/logs/application_0_0001/container_0_0001_01_000001");
        if (!(f.exists())) {
            Assert.assertTrue(f.mkdirs());
        }
        writeLog(configuration, "admin");
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(data);
        HtmlBlock html = new HtmlBlockForTest();
        HtmlBlock.Block block = new BlockForTest(html, printWriter, 10, false);
        TestAggregatedLogsBlock.TFileAggregatedLogsBlockForTest aggregatedBlock = getTFileAggregatedLogsBlockForTest(configuration, "admin", "container_0_0001_01_000001", "localhost:1234");
        aggregatedBlock.render(block);
        block.getWriter().flush();
        String out = data.toString();
        Assert.assertTrue(out.contains("No logs available for container container_0_0001_01_000001"));
    }

    private static class TFileAggregatedLogsBlockForTest extends TFileAggregatedLogsBlock {
        private Map<String, String> params = new HashMap<String, String>();

        private HttpServletRequest request;

        @Inject
        TFileAggregatedLogsBlockForTest(ViewContext ctx, Configuration conf) {
            super(ctx, conf);
        }

        public void render(Block html) {
            super.render(html);
        }

        @Override
        public Map<String, String> moreParams() {
            return params;
        }

        public HttpServletRequest request() {
            return request;
        }

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }
    }
}

