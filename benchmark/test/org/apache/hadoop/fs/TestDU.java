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
package org.apache.hadoop.fs;


import CommonConfigurationKeys.FS_DU_INTERVAL_KEY;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test makes sure that "DU" does not get to run on each call to getUsed
 */
public class TestDU {
    private static final File DU_DIR = GenericTestUtils.getTestDir("dutmp");

    /**
     * Verify that du returns expected used space for a file.
     * We assume here that if a file system crates a file of size
     * that is a multiple of the block size in this file system,
     * then the used size for the file will be exactly that size.
     * This is true for most file systems.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testDU() throws IOException, InterruptedException {
        final int writtenSize = 32 * 1024;// writing 32K

        // Allow for extra 4K on-disk slack for local file systems
        // that may store additional file metadata (eg ext attrs).
        final int slack = 4 * 1024;
        File file = new File(TestDU.DU_DIR, "data");
        createFile(file, writtenSize);
        Thread.sleep(5000);// let the metadata updater catch up

        DU du = new DU(file, 10000, 0, (-1));
        du.init();
        long duSize = du.getUsed();
        du.close();
        Assert.assertTrue("Invalid on-disk size", ((duSize >= writtenSize) && (writtenSize <= (duSize + slack))));
        // test with 0 interval, will not launch thread
        du = new DU(file, 0, 1, (-1));
        du.init();
        duSize = du.getUsed();
        du.close();
        Assert.assertTrue("Invalid on-disk size", ((duSize >= writtenSize) && (writtenSize <= (duSize + slack))));
        // test without launching thread
        du = new DU(file, 10000, 0, (-1));
        du.init();
        duSize = du.getUsed();
        Assert.assertTrue("Invalid on-disk size", ((duSize >= writtenSize) && (writtenSize <= (duSize + slack))));
    }

    @Test
    public void testDUGetUsedWillNotReturnNegative() throws IOException {
        File file = new File(TestDU.DU_DIR, "data");
        Assert.assertTrue(file.createNewFile());
        Configuration conf = new Configuration();
        conf.setLong(FS_DU_INTERVAL_KEY, 10000L);
        DU du = new DU(file, 10000L, 0, (-1));
        du.incDfsUsed((-(Long.MAX_VALUE)));
        long duSize = du.getUsed();
        Assert.assertTrue(String.valueOf(duSize), (duSize >= 0L));
    }

    @Test
    public void testDUSetInitialValue() throws IOException {
        File file = new File(TestDU.DU_DIR, "dataX");
        createFile(file, 8192);
        DU du = new DU(file, 3000, 0, 1024);
        du.init();
        Assert.assertTrue("Initial usage setting not honored", ((du.getUsed()) == 1024));
        // wait until the first du runs.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
        }
        Assert.assertTrue("Usage didn't get updated", ((du.getUsed()) == 8192));
    }
}

