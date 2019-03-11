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
package org.apache.hadoop.fs.viewfs;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileContextTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify XAttrs through ViewFs functionality.
 */
public class TestViewFsWithXAttrs {
    private static MiniDFSCluster cluster;

    private static Configuration clusterConf = new Configuration();

    private static FileContext fc;

    private static FileContext fc2;

    private FileContext fcView;

    private FileContext fcTarget;

    private FileContext fcTarget2;

    private Configuration fsViewConf;

    private Path targetTestRoot;

    private Path targetTestRoot2;

    private Path mountOnNn1;

    private Path mountOnNn2;

    private FileContextTestHelper fileContextTestHelper = new FileContextTestHelper("/tmp/TestViewFsWithXAttrs");

    // XAttrs
    protected static final String name1 = "user.a1";

    protected static final byte[] value1 = new byte[]{ 49, 50, 51 };

    protected static final String name2 = "user.a2";

    protected static final byte[] value2 = new byte[]{ 55, 56, 57 };

    /**
     * Verify a ViewFs wrapped over multiple federated NameNodes will
     * dispatch the XAttr operations to the correct NameNode.
     */
    @Test
    public void testXAttrOnMountEntry() throws Exception {
        // Set XAttrs on the first namespace and verify they are correct
        fcView.setXAttr(mountOnNn1, TestViewFsWithXAttrs.name1, TestViewFsWithXAttrs.value1);
        fcView.setXAttr(mountOnNn1, TestViewFsWithXAttrs.name2, TestViewFsWithXAttrs.value2);
        Assert.assertEquals(2, fcView.getXAttrs(mountOnNn1).size());
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value1, fcView.getXAttr(mountOnNn1, TestViewFsWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value2, fcView.getXAttr(mountOnNn1, TestViewFsWithXAttrs.name2));
        // Double-check by getting the XAttrs using FileSystem
        // instead of ViewFs
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value1, TestViewFsWithXAttrs.fc.getXAttr(targetTestRoot, TestViewFsWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value2, TestViewFsWithXAttrs.fc.getXAttr(targetTestRoot, TestViewFsWithXAttrs.name2));
        // Paranoid check: verify the other namespace does not
        // have XAttrs set on the same path.
        Assert.assertEquals(0, fcView.getXAttrs(mountOnNn2).size());
        Assert.assertEquals(0, TestViewFsWithXAttrs.fc2.getXAttrs(targetTestRoot2).size());
        // Remove the XAttr entries on the first namespace
        fcView.removeXAttr(mountOnNn1, TestViewFsWithXAttrs.name1);
        fcView.removeXAttr(mountOnNn1, TestViewFsWithXAttrs.name2);
        Assert.assertEquals(0, fcView.getXAttrs(mountOnNn1).size());
        Assert.assertEquals(0, TestViewFsWithXAttrs.fc.getXAttrs(targetTestRoot).size());
        // Now set XAttrs on the second namespace
        fcView.setXAttr(mountOnNn2, TestViewFsWithXAttrs.name1, TestViewFsWithXAttrs.value1);
        fcView.setXAttr(mountOnNn2, TestViewFsWithXAttrs.name2, TestViewFsWithXAttrs.value2);
        Assert.assertEquals(2, fcView.getXAttrs(mountOnNn2).size());
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value1, fcView.getXAttr(mountOnNn2, TestViewFsWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value2, fcView.getXAttr(mountOnNn2, TestViewFsWithXAttrs.name2));
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value1, TestViewFsWithXAttrs.fc2.getXAttr(targetTestRoot2, TestViewFsWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFsWithXAttrs.value2, TestViewFsWithXAttrs.fc2.getXAttr(targetTestRoot2, TestViewFsWithXAttrs.name2));
        fcView.removeXAttr(mountOnNn2, TestViewFsWithXAttrs.name1);
        fcView.removeXAttr(mountOnNn2, TestViewFsWithXAttrs.name2);
        Assert.assertEquals(0, fcView.getXAttrs(mountOnNn2).size());
        Assert.assertEquals(0, TestViewFsWithXAttrs.fc2.getXAttrs(targetTestRoot2).size());
    }
}

