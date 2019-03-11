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
package org.apache.hadoop.hdfs.nfs.nfs3;


import java.net.URL;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestNfs3HttpServer {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestNfs3HttpServer.class.getSimpleName());

    private static NfsConfiguration conf = new NfsConfiguration();

    private static MiniDFSCluster cluster;

    private static String keystoresDir;

    private static String sslConfDir;

    @Test
    public void testHttpServer() throws Exception {
        Nfs3 nfs = new Nfs3(TestNfs3HttpServer.conf);
        nfs.startServiceInternal(false);
        RpcProgramNfs3 nfsd = ((RpcProgramNfs3) (nfs.getRpcProgram()));
        Nfs3HttpServer infoServer = nfsd.getInfoServer();
        String urlRoot = infoServer.getServerURI().toString();
        // Check default servlets.
        String pageContents = DFSTestUtil.urlGet(new URL((urlRoot + "/jmx")));
        Assert.assertTrue(("Bad contents: " + pageContents), pageContents.contains("java.lang:type="));
        System.out.println(("pc:" + pageContents));
        int port = infoServer.getSecurePort();
        Assert.assertTrue("Can't get https port", (port > 0));
    }
}

