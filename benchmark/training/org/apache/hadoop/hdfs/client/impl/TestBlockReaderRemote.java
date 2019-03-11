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
package org.apache.hadoop.hdfs.client.impl;


import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.hdfs.BlockReader;
import org.junit.Assert;
import org.junit.Test;


/**
 * This tests BlockReaderRemote.
 */
public class TestBlockReaderRemote {
    private BlockReaderTestUtil util;

    private byte[] blockData;

    private BlockReader reader;

    @Test(timeout = 60000)
    public void testSkip() throws IOException {
        Random random = new Random();
        byte[] buf = new byte[1];
        for (int pos = 0; pos < (blockData.length);) {
            long skip = (random.nextInt(100)) + 1;
            long skipped = reader.skip(skip);
            if ((pos + skip) >= (blockData.length)) {
                Assert.assertEquals(blockData.length, (pos + skipped));
                break;
            } else {
                Assert.assertEquals(skip, skipped);
                pos += skipped;
                Assert.assertEquals(1, reader.read(buf, 0, 1));
                Assert.assertEquals(blockData[pos], buf[0]);
                pos += 1;
            }
        }
    }
}

