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
package org.apache.flink.table.runtime.compression;


import org.junit.Test;


/**
 * Tests for block compression.
 */
public class BlockCompressionTest {
    @Test
    public void testLz4() {
        BlockCompressionFactory factory = new Lz4BlockCompressionFactory();
        runArrayTest(factory, 32768);
        runArrayTest(factory, 16);
        runByteBufferTest(factory, false, 32768);
        runByteBufferTest(factory, false, 16);
        runByteBufferTest(factory, true, 32768);
        runByteBufferTest(factory, true, 16);
    }
}

