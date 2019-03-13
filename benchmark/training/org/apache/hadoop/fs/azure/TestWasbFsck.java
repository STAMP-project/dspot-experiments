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
package org.apache.hadoop.fs.azure;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests which look at fsck recovery.
 */
public class TestWasbFsck extends AbstractWasbTestWithTimeout {
    private AzureBlobStorageTestAccount testAccount;

    private FileSystem fs;

    private InMemoryBlockBlobStore backingStore;

    /**
     * Tests that we delete dangling files properly
     */
    @Test
    public void testDelete() throws Exception {
        Path danglingFile = new Path("/crashedInTheMiddle");
        // Create a file and leave it dangling and try to delete it.
        FSDataOutputStream stream = fs.create(danglingFile);
        stream.write(new byte[]{ 1, 2, 3 });
        stream.flush();
        // Now we should still only see a zero-byte file in this place
        FileStatus fileStatus = fs.getFileStatus(danglingFile);
        Assert.assertNotNull(fileStatus);
        Assert.assertEquals(0, fileStatus.getLen());
        Assert.assertEquals(1, getNumTempBlobs());
        // Run WasbFsck -delete to delete the file.
        runFsck("-delete");
        // Now we should see no trace of the file.
        Assert.assertEquals(0, getNumTempBlobs());
        Assert.assertFalse(fs.exists(danglingFile));
    }
}

