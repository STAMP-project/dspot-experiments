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
package org.apache.hadoop.hdfs.server.datanode.web;


import HttpServer2.XFrameOption.SAMEORIGIN;
import java.net.HttpURLConnection;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test that X-Frame-Options works correctly with DatanodeHTTPServer.
 */
public class TestDatanodeHttpXFrame {
    private MiniDFSCluster cluster = null;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testDataNodeXFrameOptionsEnabled() throws Exception {
        boolean xFrameEnabled = true;
        cluster = TestDatanodeHttpXFrame.createCluster(xFrameEnabled, null);
        HttpURLConnection conn = TestDatanodeHttpXFrame.getConn(cluster);
        String xfoHeader = conn.getHeaderField("X-FRAME-OPTIONS");
        Assert.assertTrue("X-FRAME-OPTIONS is absent in the header", (xfoHeader != null));
        Assert.assertTrue(xfoHeader.endsWith(SAMEORIGIN.toString()));
    }

    @Test
    public void testNameNodeXFrameOptionsDisabled() throws Exception {
        boolean xFrameEnabled = false;
        cluster = TestDatanodeHttpXFrame.createCluster(xFrameEnabled, null);
        HttpURLConnection conn = TestDatanodeHttpXFrame.getConn(cluster);
        String xfoHeader = conn.getHeaderField("X-FRAME-OPTIONS");
        Assert.assertTrue("unexpected X-FRAME-OPTION in header", (xfoHeader == null));
    }

    @Test
    public void testDataNodeXFramewithInvalidOptions() throws Exception {
        exception.expect(IllegalArgumentException.class);
        cluster = TestDatanodeHttpXFrame.createCluster(false, "Hadoop");
    }
}

