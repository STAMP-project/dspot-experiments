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
package org.apache.hadoop.hbase.regionserver;


import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.io.HFileLink;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test HStoreFile
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestStoreFileInfo {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStoreFileInfo.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    /**
     * Validate that we can handle valid tables with '.', '_', and '-' chars.
     */
    @Test
    public void testStoreFileNames() {
        String[] legalHFileLink = new String[]{ "MyTable_02=abc012-def345", "MyTable_02.300=abc012-def345", "MyTable_02-400=abc012-def345", "MyTable_02-400.200=abc012-def345", "MyTable_02=abc012-def345_SeqId_1_", "MyTable_02=abc012-def345_SeqId_20_" };
        for (String name : legalHFileLink) {
            Assert.assertTrue(("should be a valid link: " + name), HFileLink.isHFileLink(name));
            Assert.assertTrue(("should be a valid StoreFile" + name), StoreFileInfo.validateStoreFileName(name));
            Assert.assertFalse(("should not be a valid reference: " + name), StoreFileInfo.isReference(name));
            String refName = name + ".6789";
            Assert.assertTrue(("should be a valid link reference: " + refName), StoreFileInfo.isReference(refName));
            Assert.assertTrue(("should be a valid StoreFile" + refName), StoreFileInfo.validateStoreFileName(refName));
        }
        String[] illegalHFileLink = new String[]{ ".MyTable_02=abc012-def345", "-MyTable_02.300=abc012-def345", "MyTable_02-400=abc0_12-def345", "MyTable_02-400.200=abc012-def345...." };
        for (String name : illegalHFileLink) {
            Assert.assertFalse(("should not be a valid link: " + name), HFileLink.isHFileLink(name));
        }
    }

    @Test
    public void testEqualsWithLink() throws IOException {
        Path origin = new Path("/origin");
        Path tmp = getDataTestDir();
        Path mob = new Path("/mob");
        Path archive = new Path("/archive");
        HFileLink link1 = new HFileLink(new Path(origin, "f1"), new Path(tmp, "f1"), new Path(mob, "f1"), new Path(archive, "f1"));
        HFileLink link2 = new HFileLink(new Path(origin, "f1"), new Path(tmp, "f1"), new Path(mob, "f1"), new Path(archive, "f1"));
        StoreFileInfo info1 = new StoreFileInfo(TestStoreFileInfo.TEST_UTIL.getConfiguration(), TestStoreFileInfo.TEST_UTIL.getTestFileSystem(), null, link1);
        StoreFileInfo info2 = new StoreFileInfo(TestStoreFileInfo.TEST_UTIL.getConfiguration(), TestStoreFileInfo.TEST_UTIL.getTestFileSystem(), null, link2);
        Assert.assertEquals(info1, info2);
        Assert.assertEquals(info1.hashCode(), info2.hashCode());
    }
}

