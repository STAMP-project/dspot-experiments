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
package org.apache.hadoop.hbase.io.hfile;


import Algorithm.NONE;
import ChecksumType.NULL;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.RedundantKVGenerator;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ IOTests.class, SmallTests.class })
public class TestHFileDataBlockEncoder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileDataBlockEncoder.class);

    private HFileDataBlockEncoder blockEncoder;

    private RedundantKVGenerator generator = new RedundantKVGenerator();

    private boolean includesMemstoreTS;

    /**
     * Create test for given data block encoding configuration.
     *
     * @param blockEncoder
     * 		What kind of encoding policy will be used.
     */
    public TestHFileDataBlockEncoder(HFileDataBlockEncoder blockEncoder, boolean includesMemstoreTS) {
        this.blockEncoder = blockEncoder;
        this.includesMemstoreTS = includesMemstoreTS;
        System.err.println(((("Encoding: " + (blockEncoder.getDataBlockEncoding())) + ", includesMemstoreTS: ") + includesMemstoreTS));
    }

    /**
     * Test putting and taking out blocks into cache with different
     * encoding options.
     */
    @Test
    public void testEncodingWithCache() throws IOException {
        testEncodingWithCacheInternals(false);
        testEncodingWithCacheInternals(true);
    }

    /**
     * Test for HBASE-5746.
     */
    @Test
    public void testHeaderSizeInCacheWithoutChecksum() throws Exception {
        testHeaderSizeInCacheWithoutChecksumInternals(false);
        testHeaderSizeInCacheWithoutChecksumInternals(true);
    }

    /**
     * Test encoding.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testEncoding() throws IOException {
        testEncodingInternals(false);
        testEncodingInternals(true);
    }

    /**
     * Test encoding with offheap keyvalue. This test just verifies if the encoders
     * work with DBB and does not use the getXXXArray() API
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testEncodingWithOffheapKeyValue() throws IOException {
        // usually we have just block without headers, but don't complicate that
        try {
            List<Cell> kvs = generator.generateTestExtendedOffheapKeyValues(60, true);
            HFileContext meta = new HFileContextBuilder().withIncludesMvcc(includesMemstoreTS).withIncludesTags(true).withHBaseCheckSum(true).withCompression(NONE).withBlockSize(0).withChecksumType(NULL).build();
            writeBlock(kvs, meta, true);
        } catch (IllegalArgumentException e) {
            Assert.fail("No exception should have been thrown");
        }
    }
}

