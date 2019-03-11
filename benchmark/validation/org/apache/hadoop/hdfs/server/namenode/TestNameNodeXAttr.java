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


import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests NameNode interaction for all XAttr APIs.
 * This test suite covers restarting NN, saving new checkpoint,
 * and also includes test of xattrs for symlinks.
 */
public class TestNameNodeXAttr extends FSXAttrBaseTest {
    private static final Path linkParent = new Path("/symdir1");

    private static final Path targetParent = new Path("/symdir2");

    private static final Path link = new Path(TestNameNodeXAttr.linkParent, "link");

    private static final Path target = new Path(TestNameNodeXAttr.targetParent, "target");

    @Test(timeout = 120000)
    public void testXAttrSymlinks() throws Exception {
        fs.mkdirs(TestNameNodeXAttr.linkParent);
        fs.mkdirs(TestNameNodeXAttr.targetParent);
        DFSTestUtil.createFile(fs, TestNameNodeXAttr.target, 1024, ((short) (3)), 48879L);
        fs.createSymlink(TestNameNodeXAttr.target, TestNameNodeXAttr.link, false);
        fs.setXAttr(TestNameNodeXAttr.target, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1);
        fs.setXAttr(TestNameNodeXAttr.target, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2);
        Map<String, byte[]> xattrs = fs.getXAttrs(TestNameNodeXAttr.link);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, xattrs.get(FSXAttrBaseTest.name2));
        fs.setXAttr(TestNameNodeXAttr.link, FSXAttrBaseTest.name3, null);
        xattrs = fs.getXAttrs(TestNameNodeXAttr.target);
        Assert.assertEquals(xattrs.size(), 3);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, xattrs.get(FSXAttrBaseTest.name2));
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name3));
        fs.removeXAttr(TestNameNodeXAttr.link, FSXAttrBaseTest.name1);
        xattrs = fs.getXAttrs(TestNameNodeXAttr.target);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, xattrs.get(FSXAttrBaseTest.name2));
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name3));
        fs.removeXAttr(TestNameNodeXAttr.target, FSXAttrBaseTest.name3);
        xattrs = fs.getXAttrs(TestNameNodeXAttr.link);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, xattrs.get(FSXAttrBaseTest.name2));
        fs.delete(TestNameNodeXAttr.linkParent, true);
        fs.delete(TestNameNodeXAttr.targetParent, true);
    }
}

