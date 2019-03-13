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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_REFORMAT_DISABLED;
import NamenodeRole.NAMENODE;
import StartupOption.FORMAT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.ExitUtil.ExitException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestClusterId {
    private static final Logger LOG = LoggerFactory.getLogger(TestClusterId.class);

    File hdfsDir;

    Configuration config;

    @Test
    public void testFormatClusterIdOption() throws IOException {
        // 1. should format without cluster id
        // StartupOption.FORMAT.setClusterId("");
        NameNode.format(config);
        // see if cluster id not empty.
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
        // 2. successful format with given clusterid
        FORMAT.setClusterId("mycluster");
        NameNode.format(config);
        // see if cluster id matches with given clusterid.
        cid = getClusterId(config);
        Assert.assertTrue("ClusterId didn't match", cid.equals("mycluster"));
        // 3. format without any clusterid again. It should generate new
        // clusterid.
        FORMAT.setClusterId("");
        NameNode.format(config);
        String newCid = getClusterId(config);
        Assert.assertFalse("ClusterId should not be the same", newCid.equals(cid));
    }

    /**
     * Test namenode format with -format option. Format should succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormat() throws IOException {
        String[] argv = new String[]{ "-format" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
    }

    /**
     * Test namenode format with -format option when an empty name directory
     * exists. Format should succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithEmptyDir() throws IOException {
        if (!(hdfsDir.mkdirs())) {
            Assert.fail(("Failed to create dir " + (hdfsDir.getPath())));
        }
        String[] argv = new String[]{ "-format" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
    }

    /**
     * Test namenode format with -format -force options when name directory
     * exists. Format should succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithForce() throws IOException {
        if (!(hdfsDir.mkdirs())) {
            Assert.fail(("Failed to create dir " + (hdfsDir.getPath())));
        }
        String[] argv = new String[]{ "-format", "-force" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
    }

    /**
     * Test namenode format with -format -force -clusterid option when name
     * directory exists. Format should succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithForceAndClusterId() throws IOException {
        if (!(hdfsDir.mkdirs())) {
            Assert.fail(("Failed to create dir " + (hdfsDir.getPath())));
        }
        String myId = "testFormatWithForceAndClusterId";
        String[] argv = new String[]{ "-format", "-force", "-clusterid", myId };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        String cId = getClusterId(config);
        Assert.assertEquals("ClusterIds do not match", myId, cId);
    }

    /**
     * Test namenode format with -clusterid -force option. Format command should
     * fail as no cluster id was provided.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithInvalidClusterIdOption() throws IOException {
        String[] argv = new String[]{ "-format", "-clusterid", "-force" };
        PrintStream origErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stdErr = new PrintStream(baos);
        System.setErr(stdErr);
        try {
            NameNode.createNameNode(argv, config);
            // Check if usage is printed
            Assert.assertTrue(baos.toString("UTF-8").contains("Usage: hdfs namenode"));
        } finally {
            System.setErr(origErr);
        }
        // check if the version file does not exists.
        File version = new File(hdfsDir, "current/VERSION");
        Assert.assertFalse("Check version should not exist", version.exists());
    }

    /**
     * Test namenode format with -format -clusterid options. Format should fail
     * was no clusterid was sent.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithNoClusterIdOption() throws IOException {
        String[] argv = new String[]{ "-format", "-clusterid" };
        PrintStream origErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stdErr = new PrintStream(baos);
        System.setErr(stdErr);
        try {
            NameNode.createNameNode(argv, config);
            // Check if usage is printed
            Assert.assertTrue(baos.toString("UTF-8").contains("Usage: hdfs namenode"));
        } finally {
            System.setErr(origErr);
        }
        // check if the version file does not exists.
        File version = new File(hdfsDir, "current/VERSION");
        Assert.assertFalse("Check version should not exist", version.exists());
    }

    /**
     * Test namenode format with -format -clusterid and empty clusterid. Format
     * should fail as no valid if was provided.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithEmptyClusterIdOption() throws IOException {
        String[] argv = new String[]{ "-format", "-clusterid", "" };
        PrintStream origErr = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream stdErr = new PrintStream(baos);
        System.setErr(stdErr);
        try {
            NameNode.createNameNode(argv, config);
            // Check if usage is printed
            Assert.assertTrue(baos.toString("UTF-8").contains("Usage: hdfs namenode"));
        } finally {
            System.setErr(origErr);
        }
        // check if the version file does not exists.
        File version = new File(hdfsDir, "current/VERSION");
        Assert.assertFalse("Check version should not exist", version.exists());
    }

    /**
     * Test namenode format with -format -nonInteractive options when a non empty
     * name directory exists. Format should not succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithNonInteractive() throws IOException {
        // we check for a non empty dir, so create a child path
        File data = new File(hdfsDir, "file");
        if (!(data.mkdirs())) {
            Assert.fail(("Failed to create dir " + (data.getPath())));
        }
        String[] argv = new String[]{ "-format", "-nonInteractive" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have been aborted with exit code 1", 1, e.status);
        }
        // check if the version file does not exists.
        File version = new File(hdfsDir, "current/VERSION");
        Assert.assertFalse("Check version should not exist", version.exists());
    }

    /**
     * Test namenode format with -format -nonInteractive options when name
     * directory does not exist. Format should succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithNonInteractiveNameDirDoesNotExit() throws IOException {
        String[] argv = new String[]{ "-format", "-nonInteractive" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
    }

    /**
     * Test namenode format with -force -nonInteractive -force option. Format
     * should succeed.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testFormatWithNonInteractiveAndForce() throws IOException {
        if (!(hdfsDir.mkdirs())) {
            Assert.fail(("Failed to create dir " + (hdfsDir.getPath())));
        }
        String[] argv = new String[]{ "-format", "-nonInteractive", "-force" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
    }

    /**
     * Test namenode format with -format option when a non empty name directory
     * exists. Enter Y when prompted and the format should succeed.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testFormatWithoutForceEnterYes() throws IOException, InterruptedException {
        // we check for a non empty dir, so create a child path
        File data = new File(hdfsDir, "file");
        if (!(data.mkdirs())) {
            Assert.fail(("Failed to create dir " + (data.getPath())));
        }
        // capture the input stream
        InputStream origIn = System.in;
        ByteArrayInputStream bins = new ByteArrayInputStream("Y\n".getBytes());
        System.setIn(bins);
        String[] argv = new String[]{ "-format" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should have succeeded", 0, e.status);
        }
        System.setIn(origIn);
        String cid = getClusterId(config);
        Assert.assertTrue("Didn't get new ClusterId", ((cid != null) && (!(cid.equals("")))));
    }

    /**
     * Test namenode format with -format option when a non empty name directory
     * exists. Enter N when prompted and format should be aborted.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testFormatWithoutForceEnterNo() throws IOException, InterruptedException {
        // we check for a non empty dir, so create a child path
        File data = new File(hdfsDir, "file");
        if (!(data.mkdirs())) {
            Assert.fail(("Failed to create dir " + (data.getPath())));
        }
        // capture the input stream
        InputStream origIn = System.in;
        ByteArrayInputStream bins = new ByteArrayInputStream("N\n".getBytes());
        System.setIn(bins);
        String[] argv = new String[]{ "-format" };
        try {
            NameNode.createNameNode(argv, config);
            Assert.fail("createNameNode() did not call System.exit()");
        } catch (ExitException e) {
            Assert.assertEquals("Format should not have succeeded", 1, e.status);
        }
        System.setIn(origIn);
        // check if the version file does not exists.
        File version = new File(hdfsDir, "current/VERSION");
        Assert.assertFalse("Check version should not exist", version.exists());
    }

    /**
     * Test NameNode format failure when reformat is disabled and metadata
     * directories exist.
     */
    @Test
    public void testNNFormatFailure() throws Exception {
        NameNode.initMetrics(config, NAMENODE);
        DFSTestUtil.formatNameNode(config);
        config.setBoolean(DFS_REFORMAT_DISABLED, true);
        // Call to NameNode format will fail as name dir is not empty
        try {
            NameNode.format(config);
            Assert.fail("NN format should fail.");
        } catch (NameNodeFormatException e) {
            GenericTestUtils.assertExceptionContains(("NameNode format aborted as " + "reformat is disabled for this cluster"), e);
        }
    }

    /**
     * Test NameNode format when reformat is disabled and metadata directories do
     * not exist.
     */
    @Test
    public void testNNFormatSuccess() throws Exception {
        NameNode.initMetrics(config, NAMENODE);
        config.setBoolean(DFS_REFORMAT_DISABLED, true);
        DFSTestUtil.formatNameNode(config);
    }
}

