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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class TestLazyPersistPolicy extends LazyPersistTestCase {
    @Test
    public void testPolicyNotSetByDefault() throws IOException {
        getClusterBuilder().build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, 0, false);
        // Stat the file and check that the LAZY_PERSIST policy is not
        // returned back.
        HdfsFileStatus status = client.getFileInfo(path.toString());
        Assert.assertThat(status.getStoragePolicy(), IsNot.not(LazyPersistTestCase.LAZY_PERSIST_POLICY_ID));
    }

    @Test
    public void testPolicyPropagation() throws IOException {
        getClusterBuilder().build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, 0, true);
        // Stat the file and check that the lazyPersist flag is returned back.
        HdfsFileStatus status = client.getFileInfo(path.toString());
        Assert.assertThat(status.getStoragePolicy(), Is.is(LazyPersistTestCase.LAZY_PERSIST_POLICY_ID));
    }

    @Test
    public void testPolicyPersistenceInEditLog() throws IOException {
        getClusterBuilder().build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, 0, true);
        cluster.restartNameNode(true);
        // Stat the file and check that the lazyPersist flag is returned back.
        HdfsFileStatus status = client.getFileInfo(path.toString());
        Assert.assertThat(status.getStoragePolicy(), Is.is(LazyPersistTestCase.LAZY_PERSIST_POLICY_ID));
    }

    @Test
    public void testPolicyPersistenceInFsImage() throws IOException {
        getClusterBuilder().build();
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        Path path = new Path((("/" + METHOD_NAME) + ".dat"));
        makeTestFile(path, 0, true);
        // checkpoint
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        cluster.restartNameNode(true);
        // Stat the file and check that the lazyPersist flag is returned back.
        HdfsFileStatus status = client.getFileInfo(path.toString());
        Assert.assertThat(status.getStoragePolicy(), Is.is(LazyPersistTestCase.LAZY_PERSIST_POLICY_ID));
    }
}

