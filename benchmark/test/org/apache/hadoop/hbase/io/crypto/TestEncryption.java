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
package org.apache.hadoop.hbase.io.crypto;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MiscTests.class, SmallTests.class })
public class TestEncryption {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEncryption.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestEncryption.class);

    @Test
    public void testSmallBlocks() throws Exception {
        byte[] key = new byte[16];
        Bytes.random(key);
        byte[] iv = new byte[16];
        Bytes.random(iv);
        for (int size : new int[]{ 4, 8, 16, 32, 64, 128, 256, 512 }) {
            checkTransformSymmetry(key, iv, getRandomBlock(size));
        }
    }

    @Test
    public void testLargeBlocks() throws Exception {
        byte[] key = new byte[16];
        Bytes.random(key);
        byte[] iv = new byte[16];
        Bytes.random(iv);
        for (int size : new int[]{ 256 * 1024, 512 * 1024, 1024 * 1024 }) {
            checkTransformSymmetry(key, iv, getRandomBlock(size));
        }
    }

    @Test
    public void testOddSizedBlocks() throws Exception {
        byte[] key = new byte[16];
        Bytes.random(key);
        byte[] iv = new byte[16];
        Bytes.random(iv);
        for (int size : new int[]{ 3, 7, 11, 23, 47, 79, 119, 175 }) {
            checkTransformSymmetry(key, iv, getRandomBlock(size));
        }
    }

    @Test
    public void testTypicalHFileBlocks() throws Exception {
        byte[] key = new byte[16];
        Bytes.random(key);
        byte[] iv = new byte[16];
        Bytes.random(iv);
        for (int size : new int[]{ 4 * 1024, 8 * 1024, 64 * 1024, 128 * 1024 }) {
            checkTransformSymmetry(key, iv, getRandomBlock(size));
        }
    }
}

