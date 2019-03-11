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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify XAttrs through ViewFileSystem functionality.
 */
public class TestViewFileSystemWithXAttrs {
    private static MiniDFSCluster cluster;

    private static Configuration clusterConf = new Configuration();

    private static FileSystem fHdfs;

    private static FileSystem fHdfs2;

    private FileSystem fsView;

    private Configuration fsViewConf;

    private FileSystem fsTarget;

    private FileSystem fsTarget2;

    private Path targetTestRoot;

    private Path targetTestRoot2;

    private Path mountOnNn1;

    private Path mountOnNn2;

    private FileSystemTestHelper fileSystemTestHelper = new FileSystemTestHelper("/tmp/TestViewFileSystemWithXAttrs");

    // XAttrs
    protected static final String name1 = "user.a1";

    protected static final byte[] value1 = new byte[]{ 49, 50, 51 };

    protected static final String name2 = "user.a2";

    protected static final byte[] value2 = new byte[]{ 55, 56, 57 };

    /**
     * Verify a ViewFileSystem wrapped over multiple federated NameNodes will
     * dispatch the XAttr operations to the correct NameNode.
     */
    @Test
    public void testXAttrOnMountEntry() throws Exception {
        // Set XAttrs on the first namespace and verify they are correct
        fsView.setXAttr(mountOnNn1, TestViewFileSystemWithXAttrs.name1, TestViewFileSystemWithXAttrs.value1);
        fsView.setXAttr(mountOnNn1, TestViewFileSystemWithXAttrs.name2, TestViewFileSystemWithXAttrs.value2);
        Assert.assertEquals(2, fsView.getXAttrs(mountOnNn1).size());
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value1, fsView.getXAttr(mountOnNn1, TestViewFileSystemWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value2, fsView.getXAttr(mountOnNn1, TestViewFileSystemWithXAttrs.name2));
        // Double-check by getting the XAttrs using FileSystem
        // instead of ViewFileSystem
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value1, TestViewFileSystemWithXAttrs.fHdfs.getXAttr(targetTestRoot, TestViewFileSystemWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value2, TestViewFileSystemWithXAttrs.fHdfs.getXAttr(targetTestRoot, TestViewFileSystemWithXAttrs.name2));
        // Paranoid check: verify the other namespace does not
        // have XAttrs set on the same path.
        Assert.assertEquals(0, fsView.getXAttrs(mountOnNn2).size());
        Assert.assertEquals(0, TestViewFileSystemWithXAttrs.fHdfs2.getXAttrs(targetTestRoot2).size());
        // Remove the XAttr entries on the first namespace
        fsView.removeXAttr(mountOnNn1, TestViewFileSystemWithXAttrs.name1);
        fsView.removeXAttr(mountOnNn1, TestViewFileSystemWithXAttrs.name2);
        Assert.assertEquals(0, fsView.getXAttrs(mountOnNn1).size());
        Assert.assertEquals(0, TestViewFileSystemWithXAttrs.fHdfs.getXAttrs(targetTestRoot).size());
        // Now set XAttrs on the second namespace
        fsView.setXAttr(mountOnNn2, TestViewFileSystemWithXAttrs.name1, TestViewFileSystemWithXAttrs.value1);
        fsView.setXAttr(mountOnNn2, TestViewFileSystemWithXAttrs.name2, TestViewFileSystemWithXAttrs.value2);
        Assert.assertEquals(2, fsView.getXAttrs(mountOnNn2).size());
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value1, fsView.getXAttr(mountOnNn2, TestViewFileSystemWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value2, fsView.getXAttr(mountOnNn2, TestViewFileSystemWithXAttrs.name2));
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value1, TestViewFileSystemWithXAttrs.fHdfs2.getXAttr(targetTestRoot2, TestViewFileSystemWithXAttrs.name1));
        Assert.assertArrayEquals(TestViewFileSystemWithXAttrs.value2, TestViewFileSystemWithXAttrs.fHdfs2.getXAttr(targetTestRoot2, TestViewFileSystemWithXAttrs.name2));
        fsView.removeXAttr(mountOnNn2, TestViewFileSystemWithXAttrs.name1);
        fsView.removeXAttr(mountOnNn2, TestViewFileSystemWithXAttrs.name2);
        Assert.assertEquals(0, fsView.getXAttrs(mountOnNn2).size());
        Assert.assertEquals(0, TestViewFileSystemWithXAttrs.fHdfs2.getXAttrs(targetTestRoot2).size());
    }
}

