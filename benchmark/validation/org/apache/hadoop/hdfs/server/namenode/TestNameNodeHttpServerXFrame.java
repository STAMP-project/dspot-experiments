/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import HttpServer2.XFrameOption.SAMEORIGIN;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * A class to test the XFrameoptions of Namenode HTTP Server. We are not reusing
 * the TestNameNodeHTTPServer since it is a parameterized class and these
 * following tests will run multiple times doing the same thing, if we had the
 * code in that classs.
 */
public class TestNameNodeHttpServerXFrame {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testNameNodeXFrameOptionsEnabled() throws Exception {
        HttpURLConnection conn = createServerwithXFrame(true, null);
        String xfoHeader = conn.getHeaderField("X-FRAME-OPTIONS");
        Assert.assertTrue("X-FRAME-OPTIONS is absent in the header", (xfoHeader != null));
        Assert.assertTrue(xfoHeader.endsWith(SAMEORIGIN.toString()));
    }

    @Test
    public void testNameNodeXFrameOptionsDisabled() throws Exception {
        HttpURLConnection conn = createServerwithXFrame(false, null);
        String xfoHeader = conn.getHeaderField("X-FRAME-OPTIONS");
        Assert.assertTrue("unexpected X-FRAME-OPTION in header", (xfoHeader == null));
    }

    @Test
    public void testNameNodeXFrameOptionsIllegalOption() throws Exception {
        exception.expect(IllegalArgumentException.class);
        createServerwithXFrame(true, "hadoop");
    }

    @Test
    public void testSecondaryNameNodeXFrame() throws IOException {
        Configuration conf = new HdfsConfiguration();
        FileSystem.setDefaultUri(conf, "hdfs://localhost:0");
        SecondaryNameNode sn = new SecondaryNameNode(conf);
        sn.startInfoServer();
        InetSocketAddress httpAddress = SecondaryNameNode.getHttpAddress(conf);
        URL url = URI.create(((("http://" + (httpAddress.getHostName())) + ":") + (httpAddress.getPort()))).toURL();
        HttpURLConnection conn = ((HttpURLConnection) (url.openConnection()));
        conn.connect();
        String xfoHeader = conn.getHeaderField("X-FRAME-OPTIONS");
        Assert.assertTrue("X-FRAME-OPTIONS is absent in the header", (xfoHeader != null));
        Assert.assertTrue(xfoHeader.endsWith(SAMEORIGIN.toString()));
    }
}

