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
package org.apache.hadoop.fs.azurebfs;


import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test read, write and seek.
 * Uses package-private methods in AbfsConfiguration, which is why it is in
 * this package.
 */
@RunWith(Parameterized.class)
public class ITestAbfsReadWriteAndSeek extends AbstractAbfsScaleTest {
    private static final Path TEST_PATH = new Path("/testfile");

    private final int size;

    public ITestAbfsReadWriteAndSeek(final int size) throws Exception {
        this.size = size;
    }

    @Test
    public void testReadAndWriteWithDifferentBufferSizesAndSeek() throws Exception {
        testReadWriteAndSeek(size);
    }
}

