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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;


/**
 * Test WebHDFS with multiple NameNodes
 */
public class TestWebHdfsWithMultipleNameNodes {
    static final Logger LOG = WebHdfsTestUtil.LOG;

    private static final Configuration conf = new HdfsConfiguration();

    private static MiniDFSCluster cluster;

    private static WebHdfsFileSystem[] webhdfs;

    @Test
    public void testRedirect() throws Exception {
        final String dir = "/testRedirect/";
        final String filename = "file";
        final Path p = new Path(dir, filename);
        final String[] writeStrings = TestWebHdfsWithMultipleNameNodes.createStrings("write to webhdfs ", "write");
        final String[] appendStrings = TestWebHdfsWithMultipleNameNodes.createStrings("append to webhdfs ", "append");
        // test create: create a file for each namenode
        for (int i = 0; i < (TestWebHdfsWithMultipleNameNodes.webhdfs.length); i++) {
            final FSDataOutputStream out = TestWebHdfsWithMultipleNameNodes.webhdfs[i].create(p);
            out.write(writeStrings[i].getBytes());
            out.close();
        }
        for (int i = 0; i < (TestWebHdfsWithMultipleNameNodes.webhdfs.length); i++) {
            // check file length
            final long expected = writeStrings[i].length();
            Assert.assertEquals(expected, TestWebHdfsWithMultipleNameNodes.webhdfs[i].getFileStatus(p).getLen());
        }
        // test read: check file content for each namenode
        for (int i = 0; i < (TestWebHdfsWithMultipleNameNodes.webhdfs.length); i++) {
            final FSDataInputStream in = TestWebHdfsWithMultipleNameNodes.webhdfs[i].open(p);
            for (int c, j = 0; (c = in.read()) != (-1); j++) {
                Assert.assertEquals(writeStrings[i].charAt(j), c);
            }
            in.close();
        }
        // test append: append to the file for each namenode
        for (int i = 0; i < (TestWebHdfsWithMultipleNameNodes.webhdfs.length); i++) {
            final FSDataOutputStream out = TestWebHdfsWithMultipleNameNodes.webhdfs[i].append(p);
            out.write(appendStrings[i].getBytes());
            out.close();
        }
        for (int i = 0; i < (TestWebHdfsWithMultipleNameNodes.webhdfs.length); i++) {
            // check file length
            final long expected = (writeStrings[i].length()) + (appendStrings[i].length());
            Assert.assertEquals(expected, TestWebHdfsWithMultipleNameNodes.webhdfs[i].getFileStatus(p).getLen());
        }
        // test read: check file content for each namenode
        for (int i = 0; i < (TestWebHdfsWithMultipleNameNodes.webhdfs.length); i++) {
            final StringBuilder b = new StringBuilder();
            final FSDataInputStream in = TestWebHdfsWithMultipleNameNodes.webhdfs[i].open(p);
            for (int c; (c = in.read()) != (-1);) {
                b.append(((char) (c)));
            }
            final int wlen = writeStrings[i].length();
            Assert.assertEquals(writeStrings[i], b.substring(0, wlen));
            Assert.assertEquals(appendStrings[i], b.substring(wlen));
            in.close();
        }
    }
}

