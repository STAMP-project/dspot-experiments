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


import NameNodeDirType.IMAGE_AND_EDITS;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.FSImageStorageInspector.FSImageFile;
import org.junit.Assert;
import org.junit.Test;


public class TestFSImageStorageInspector {
    /**
     * Simple test with image, edits, and inprogress edits
     */
    @Test
    public void testCurrentStorageInspector() throws IOException {
        FSImageTransactionalStorageInspector inspector = new FSImageTransactionalStorageInspector();
        StorageDirectory mockDir = FSImageTestUtil.mockStorageDirectory(IMAGE_AND_EDITS, false, ("/foo/current/" + (NNStorage.getImageFileName(123))), ("/foo/current/" + (NNStorage.getFinalizedEditsFileName(123, 456))), ("/foo/current/" + (NNStorage.getImageFileName(456))), ("/foo/current/" + (NNStorage.getInProgressEditsFileName(457))));
        inspector.inspectDirectory(mockDir);
        Assert.assertEquals(2, inspector.foundImages.size());
        FSImageFile latestImage = inspector.getLatestImages().get(0);
        Assert.assertEquals(456, latestImage.txId);
        Assert.assertSame(mockDir, latestImage.sd);
        Assert.assertTrue(inspector.isUpgradeFinalized());
        Assert.assertEquals(new File(("/foo/current/" + (NNStorage.getImageFileName(456)))), latestImage.getFile());
    }
}

