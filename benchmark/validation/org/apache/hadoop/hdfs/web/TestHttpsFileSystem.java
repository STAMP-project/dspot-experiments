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


import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestHttpsFileSystem {
    private static final String BASEDIR = GenericTestUtils.getTempPath(TestHttpsFileSystem.class.getSimpleName());

    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static String keystoresDir;

    private static String sslConfDir;

    private static String nnAddr;

    @Test
    public void testSWebHdfsFileSystem() throws Exception {
        FileSystem fs = WebHdfsTestUtil.getWebHdfsFileSystem(TestHttpsFileSystem.conf, "swebhdfs");
        final Path f = new Path("/testswebhdfs");
        FSDataOutputStream os = fs.create(f);
        os.write(23);
        os.close();
        Assert.assertTrue(fs.exists(f));
        InputStream is = fs.open(f);
        Assert.assertEquals(23, is.read());
        is.close();
        fs.close();
    }
}

