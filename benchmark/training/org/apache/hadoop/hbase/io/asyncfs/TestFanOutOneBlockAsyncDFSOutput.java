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
package org.apache.hadoop.hbase.io.asyncfs;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster.DataNodeProperties;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hbase.thirdparty.io.netty.channel.Channel;
import org.apache.hbase.thirdparty.io.netty.channel.EventLoop;
import org.apache.hbase.thirdparty.io.netty.channel.EventLoopGroup;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, MediumTests.class })
public class TestFanOutOneBlockAsyncDFSOutput {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFanOutOneBlockAsyncDFSOutput.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFanOutOneBlockAsyncDFSOutput.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static DistributedFileSystem FS;

    private static EventLoopGroup EVENT_LOOP_GROUP;

    private static Class<? extends Channel> CHANNEL_CLASS;

    private static int READ_TIMEOUT_MS = 2000;

    @Rule
    public TestName name = new TestName();

    @Test
    public void test() throws IOException, InterruptedException, ExecutionException {
        Path f = new Path(("/" + (name.getMethodName())));
        EventLoop eventLoop = TestFanOutOneBlockAsyncDFSOutput.EVENT_LOOP_GROUP.next();
        FanOutOneBlockAsyncDFSOutput out = FanOutOneBlockAsyncDFSOutputHelper.createOutput(TestFanOutOneBlockAsyncDFSOutput.FS, f, true, false, ((short) (3)), TestFanOutOneBlockAsyncDFSOutput.FS.getDefaultBlockSize(), eventLoop, TestFanOutOneBlockAsyncDFSOutput.CHANNEL_CLASS);
        TestFanOutOneBlockAsyncDFSOutput.writeAndVerify(TestFanOutOneBlockAsyncDFSOutput.FS, f, out);
    }

    @Test
    public void testRecover() throws IOException, InterruptedException, ExecutionException {
        Path f = new Path(("/" + (name.getMethodName())));
        EventLoop eventLoop = TestFanOutOneBlockAsyncDFSOutput.EVENT_LOOP_GROUP.next();
        FanOutOneBlockAsyncDFSOutput out = FanOutOneBlockAsyncDFSOutputHelper.createOutput(TestFanOutOneBlockAsyncDFSOutput.FS, f, true, false, ((short) (3)), TestFanOutOneBlockAsyncDFSOutput.FS.getDefaultBlockSize(), eventLoop, TestFanOutOneBlockAsyncDFSOutput.CHANNEL_CLASS);
        byte[] b = new byte[10];
        ThreadLocalRandom.current().nextBytes(b);
        out.write(b, 0, b.length);
        out.flush(false).get();
        // restart one datanode which causes one connection broken
        TestFanOutOneBlockAsyncDFSOutput.TEST_UTIL.getDFSCluster().restartDataNode(0);
        out.write(b, 0, b.length);
        try {
            out.flush(false).get();
            Assert.fail("flush should fail");
        } catch (ExecutionException e) {
            // we restarted one datanode so the flush should fail
            TestFanOutOneBlockAsyncDFSOutput.LOG.info("expected exception caught", e);
        }
        out.recoverAndClose(null);
        Assert.assertEquals(b.length, TestFanOutOneBlockAsyncDFSOutput.FS.getFileStatus(f).getLen());
        byte[] actual = new byte[b.length];
        try (FSDataInputStream in = TestFanOutOneBlockAsyncDFSOutput.FS.open(f)) {
            in.readFully(actual);
        }
        Assert.assertArrayEquals(b, actual);
    }

    @Test
    public void testHeartbeat() throws IOException, InterruptedException, ExecutionException {
        Path f = new Path(("/" + (name.getMethodName())));
        EventLoop eventLoop = TestFanOutOneBlockAsyncDFSOutput.EVENT_LOOP_GROUP.next();
        FanOutOneBlockAsyncDFSOutput out = FanOutOneBlockAsyncDFSOutputHelper.createOutput(TestFanOutOneBlockAsyncDFSOutput.FS, f, true, false, ((short) (3)), TestFanOutOneBlockAsyncDFSOutput.FS.getDefaultBlockSize(), eventLoop, TestFanOutOneBlockAsyncDFSOutput.CHANNEL_CLASS);
        Thread.sleep(((TestFanOutOneBlockAsyncDFSOutput.READ_TIMEOUT_MS) * 2));
        // the connection to datanode should still alive.
        TestFanOutOneBlockAsyncDFSOutput.writeAndVerify(TestFanOutOneBlockAsyncDFSOutput.FS, f, out);
    }

    /**
     * This is important for fencing when recover from RS crash.
     */
    @Test
    public void testCreateParentFailed() throws IOException {
        Path f = new Path((("/" + (name.getMethodName())) + "/test"));
        EventLoop eventLoop = TestFanOutOneBlockAsyncDFSOutput.EVENT_LOOP_GROUP.next();
        try {
            FanOutOneBlockAsyncDFSOutputHelper.createOutput(TestFanOutOneBlockAsyncDFSOutput.FS, f, true, false, ((short) (3)), TestFanOutOneBlockAsyncDFSOutput.FS.getDefaultBlockSize(), eventLoop, TestFanOutOneBlockAsyncDFSOutput.CHANNEL_CLASS);
            Assert.fail("should fail with parent does not exist");
        } catch (RemoteException e) {
            TestFanOutOneBlockAsyncDFSOutput.LOG.info("expected exception caught", e);
            Assert.assertThat(e.unwrapRemoteException(), CoreMatchers.instanceOf(FileNotFoundException.class));
        }
    }

    @Test
    public void testConnectToDatanodeFailed() throws IOException, ClassNotFoundException, IllegalAccessException, InterruptedException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        Field xceiverServerDaemonField = DataNode.class.getDeclaredField("dataXceiverServer");
        xceiverServerDaemonField.setAccessible(true);
        Class<?> xceiverServerClass = Class.forName("org.apache.hadoop.hdfs.server.datanode.DataXceiverServer");
        Method numPeersMethod = xceiverServerClass.getDeclaredMethod("getNumPeers");
        numPeersMethod.setAccessible(true);
        // make one datanode broken
        DataNodeProperties dnProp = TestFanOutOneBlockAsyncDFSOutput.TEST_UTIL.getDFSCluster().stopDataNode(0);
        Path f = new Path("/test");
        EventLoop eventLoop = TestFanOutOneBlockAsyncDFSOutput.EVENT_LOOP_GROUP.next();
        try (FanOutOneBlockAsyncDFSOutput output = FanOutOneBlockAsyncDFSOutputHelper.createOutput(TestFanOutOneBlockAsyncDFSOutput.FS, f, true, false, ((short) (3)), TestFanOutOneBlockAsyncDFSOutput.FS.getDefaultBlockSize(), eventLoop, TestFanOutOneBlockAsyncDFSOutput.CHANNEL_CLASS)) {
            // should exclude the dead dn when retry so here we only have 2 DNs in pipeline
            Assert.assertEquals(2, output.getPipeline().length);
        } finally {
            TestFanOutOneBlockAsyncDFSOutput.TEST_UTIL.getDFSCluster().restartDataNode(dnProp);
        }
    }

    @Test
    public void testWriteLargeChunk() throws IOException, InterruptedException, ExecutionException {
        Path f = new Path(("/" + (name.getMethodName())));
        EventLoop eventLoop = TestFanOutOneBlockAsyncDFSOutput.EVENT_LOOP_GROUP.next();
        FanOutOneBlockAsyncDFSOutput out = FanOutOneBlockAsyncDFSOutputHelper.createOutput(TestFanOutOneBlockAsyncDFSOutput.FS, f, true, false, ((short) (3)), ((1024 * 1024) * 1024), eventLoop, TestFanOutOneBlockAsyncDFSOutput.CHANNEL_CLASS);
        byte[] b = new byte[(50 * 1024) * 1024];
        ThreadLocalRandom.current().nextBytes(b);
        out.write(b);
        out.flush(false);
        Assert.assertEquals(b.length, out.flush(false).get().longValue());
        out.close();
        Assert.assertEquals(b.length, TestFanOutOneBlockAsyncDFSOutput.FS.getFileStatus(f).getLen());
        byte[] actual = new byte[b.length];
        try (FSDataInputStream in = TestFanOutOneBlockAsyncDFSOutput.FS.open(f)) {
            in.readFully(actual);
        }
        Assert.assertArrayEquals(b, actual);
    }
}

