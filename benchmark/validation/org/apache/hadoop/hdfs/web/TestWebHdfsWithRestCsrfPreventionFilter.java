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
package org.apache.hadoop.hdfs.web;


import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests use of the cross-site-request forgery (CSRF) prevention filter with
 * WebHDFS.  This is a parameterized test that covers various combinations of
 * CSRF protection enabled or disabled at the NameNode, the DataNode and the
 * WebHDFS client.  If the server is configured with CSRF prevention, but the
 * client is not, then protected operations are expected to fail.
 */
@RunWith(Parameterized.class)
public class TestWebHdfsWithRestCsrfPreventionFilter {
    private static final Path FILE = new Path("/file");

    private final boolean nnRestCsrf;

    private final boolean dnRestCsrf;

    private final boolean clientRestCsrf;

    private MiniDFSCluster cluster;

    private FileSystem fs;

    private FileSystem webhdfs;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public TestWebHdfsWithRestCsrfPreventionFilter(boolean nnRestCsrf, boolean dnRestCsrf, boolean clientRestCsrf) {
        this.nnRestCsrf = nnRestCsrf;
        this.dnRestCsrf = dnRestCsrf;
        this.clientRestCsrf = clientRestCsrf;
    }

    @Test
    public void testCreate() throws Exception {
        // create is a HTTP PUT that redirects from NameNode to DataNode, so we
        // expect CSRF prevention on either server to block an unconfigured client.
        if (((nnRestCsrf) || (dnRestCsrf)) && (!(clientRestCsrf))) {
            expectException();
        }
        Assert.assertTrue(webhdfs.createNewFile(TestWebHdfsWithRestCsrfPreventionFilter.FILE));
    }

    @Test
    public void testDelete() throws Exception {
        DFSTestUtil.createFile(fs, TestWebHdfsWithRestCsrfPreventionFilter.FILE, 1024, ((short) (1)), 0L);
        // delete is an HTTP DELETE that executes solely within the NameNode as a
        // metadata operation, so we expect CSRF prevention configured on the
        // NameNode to block an unconfigured client.
        if ((nnRestCsrf) && (!(clientRestCsrf))) {
            expectException();
        }
        Assert.assertTrue(webhdfs.delete(TestWebHdfsWithRestCsrfPreventionFilter.FILE, false));
    }

    @Test
    public void testGetFileStatus() throws Exception {
        // getFileStatus is an HTTP GET, not subject to CSRF prevention, so we
        // expect it to succeed always, regardless of CSRF configuration.
        Assert.assertNotNull(webhdfs.getFileStatus(new Path("/")));
    }

    @Test
    public void testTruncate() throws Exception {
        DFSTestUtil.createFile(fs, TestWebHdfsWithRestCsrfPreventionFilter.FILE, 1024, ((short) (1)), 0L);
        // truncate is an HTTP POST that executes solely within the NameNode as a
        // metadata operation, so we expect CSRF prevention configured on the
        // NameNode to block an unconfigured client.
        if ((nnRestCsrf) && (!(clientRestCsrf))) {
            expectException();
        }
        Assert.assertTrue(webhdfs.truncate(TestWebHdfsWithRestCsrfPreventionFilter.FILE, 0L));
    }
}

