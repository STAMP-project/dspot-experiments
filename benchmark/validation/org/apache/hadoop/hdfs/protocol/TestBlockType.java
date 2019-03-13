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
package org.apache.hadoop.hdfs.protocol;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test the BlockType class.
 */
public class TestBlockType {
    @Test
    public void testGetBlockType() throws Exception {
        Assert.assertEquals(BlockType.fromBlockId(0L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(1152921504606846976L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(2305843009213693952L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(4611686018427387904L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(8070450532247928832L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(4294967295L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(1152921508901814271L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(2305843013508661247L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(4611686022722355199L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(8070450536542896127L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(8070450536542896127L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(1152921504606846975L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(2305843009213693951L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(3458764513820540927L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(5764607523034234879L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(9223372036854775807L), BlockType.CONTIGUOUS);
        Assert.assertEquals(BlockType.fromBlockId(-9223372036854775808L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-8070450532247928832L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-6917529027641081856L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-1152921504606846976L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-9223372032559808513L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-8070450527952961537L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-6917529023346114561L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-1152921500311879681L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-8070450532247928833L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-6917529027641081857L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-5764607523034234881L), BlockType.STRIPED);
        Assert.assertEquals(BlockType.fromBlockId(-1L), BlockType.STRIPED);
    }
}

