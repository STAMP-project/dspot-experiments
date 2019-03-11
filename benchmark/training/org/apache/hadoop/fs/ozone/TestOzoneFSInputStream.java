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
package org.apache.hadoop.fs.ozone;


import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test OzoneFSInputStream by reading through multiple interfaces.
 */
public class TestOzoneFSInputStream {
    private static MiniOzoneCluster cluster = null;

    private static FileSystem fs;

    private static StorageHandler storageHandler;

    private static Path filePath = null;

    private static byte[] data = null;

    @Test
    public void testO3FSSingleByteRead() throws IOException {
        FSDataInputStream inputStream = TestOzoneFSInputStream.fs.open(TestOzoneFSInputStream.filePath);
        byte[] value = new byte[TestOzoneFSInputStream.data.length];
        int i = 0;
        while (true) {
            int val = inputStream.read();
            if (val == (-1)) {
                break;
            }
            value[i] = ((byte) (val));
            Assert.assertEquals(("value mismatch at:" + i), value[i], TestOzoneFSInputStream.data[i]);
            i++;
        } 
        Assert.assertEquals(i, TestOzoneFSInputStream.data.length);
        Assert.assertTrue(Arrays.equals(value, TestOzoneFSInputStream.data));
        inputStream.close();
    }

    @Test
    public void testO3FSMultiByteRead() throws IOException {
        FSDataInputStream inputStream = TestOzoneFSInputStream.fs.open(TestOzoneFSInputStream.filePath);
        byte[] value = new byte[TestOzoneFSInputStream.data.length];
        byte[] tmp = new byte[(1 * 1024) * 1024];
        int i = 0;
        while (true) {
            int val = inputStream.read(tmp);
            if (val == (-1)) {
                break;
            }
            System.arraycopy(tmp, 0, value, (i * (tmp.length)), tmp.length);
            i++;
        } 
        Assert.assertEquals((i * (tmp.length)), TestOzoneFSInputStream.data.length);
        Assert.assertTrue(Arrays.equals(value, TestOzoneFSInputStream.data));
        inputStream.close();
    }
}

