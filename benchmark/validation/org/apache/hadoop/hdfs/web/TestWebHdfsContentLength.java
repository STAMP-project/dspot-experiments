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


import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestWebHdfsContentLength {
    private static ServerSocket listenSocket;

    private static String bindAddr;

    private static Path p;

    private static FileSystem fs;

    private static final Pattern contentLengthPattern = Pattern.compile("^(Content-Length|Transfer-Encoding):\\s*(.*)", Pattern.MULTILINE);

    private static String errResponse = "HTTP/1.1 500 Boom\r\n" + ("Content-Length: 0\r\n" + "Connection: close\r\n\r\n");

    private static String redirectResponse;

    private static ExecutorService executor;

    @Rule
    public Timeout timeout = new Timeout(30000);

    @Test
    public void testGetOp() throws Exception {
        Future<String> future = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            TestWebHdfsContentLength.fs.getFileStatus(TestWebHdfsContentLength.p);
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals(null, getContentLength(future));
    }

    @Test
    public void testGetOpWithRedirect() {
        Future<String> future1 = contentLengthFuture(TestWebHdfsContentLength.redirectResponse);
        Future<String> future2 = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        Future<String> future3 = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            TestWebHdfsContentLength.fs.open(TestWebHdfsContentLength.p).read();
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals(null, getContentLength(future1));
        Assert.assertEquals(null, getContentLength(future2));
        Assert.assertEquals(null, getContentLength(future3));
    }

    @Test
    public void testPutOp() {
        Future<String> future = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            TestWebHdfsContentLength.fs.mkdirs(TestWebHdfsContentLength.p);
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals("0", getContentLength(future));
    }

    @Test
    public void testPutOpWithRedirect() {
        Future<String> future1 = contentLengthFuture(TestWebHdfsContentLength.redirectResponse);
        Future<String> future2 = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            FSDataOutputStream os = TestWebHdfsContentLength.fs.create(TestWebHdfsContentLength.p);
            os.write(new byte[]{ 0 });
            os.close();
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals("0", getContentLength(future1));
        Assert.assertEquals("chunked", getContentLength(future2));
    }

    @Test
    public void testPostOp() {
        Future<String> future = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            TestWebHdfsContentLength.fs.concat(TestWebHdfsContentLength.p, new Path[]{ TestWebHdfsContentLength.p });
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals("0", getContentLength(future));
    }

    @Test
    public void testPostOpWithRedirect() {
        // POST operation with redirect
        Future<String> future1 = contentLengthFuture(TestWebHdfsContentLength.redirectResponse);
        Future<String> future2 = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            FSDataOutputStream os = TestWebHdfsContentLength.fs.append(TestWebHdfsContentLength.p);
            os.write(new byte[]{ 0 });
            os.close();
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals("0", getContentLength(future1));
        Assert.assertEquals("chunked", getContentLength(future2));
    }

    @Test
    public void testDelete() {
        Future<String> future = contentLengthFuture(TestWebHdfsContentLength.errResponse);
        try {
            TestWebHdfsContentLength.fs.delete(TestWebHdfsContentLength.p, false);
            Assert.fail();
        } catch (IOException ioe) {
        }// expected

        Assert.assertEquals(null, getContentLength(future));
    }
}

