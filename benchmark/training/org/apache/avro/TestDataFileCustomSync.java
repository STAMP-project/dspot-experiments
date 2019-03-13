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
package org.apache.avro;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestDataFileCustomSync {
    @Test(expected = IOException.class)
    public void testInvalidSync() throws IOException {
        // Invalid size (must be 16):
        byte[] sync = new byte[8];
        createDataFile(sync);
    }

    @Test
    public void testRandomSync() throws IOException {
        byte[] sync = TestDataFileCustomSync.generateSync();
        byte[] randSyncFile = createDataFile(null);
        byte[] customSyncFile = createDataFile(sync);
        Assert.assertFalse(Arrays.equals(randSyncFile, customSyncFile));
    }

    @Test
    public void testCustomSync() throws IOException {
        byte[] sync = TestDataFileCustomSync.generateSync();
        byte[] customSyncFile = createDataFile(sync);
        byte[] sameCustomSyncFile = createDataFile(sync);
        Assert.assertTrue(Arrays.equals(customSyncFile, sameCustomSyncFile));
    }
}

