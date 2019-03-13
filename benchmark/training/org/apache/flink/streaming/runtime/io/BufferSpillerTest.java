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
package org.apache.flink.streaming.runtime.io;


import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.network.partition.consumer.BufferOrEvent;
import org.junit.Assert;
import org.junit.Test;

import static BufferSpiller.HEADER_SIZE;


/**
 * Tests for {@link BufferSpiller}.
 */
public class BufferSpillerTest extends BufferBlockerTestBase {
    private static IOManager ioManager;

    private BufferSpiller spiller;

    /**
     * Tests that the static HEADER_SIZE field has valid header size.
     */
    @Test
    public void testHeaderSizeStaticField() throws Exception {
        int size = 13;
        BufferOrEvent boe = BufferBlockerTestBase.generateRandomBuffer(size, 0);
        spiller.add(boe);
        Assert.assertEquals("Changed the header format, but did not adjust the HEADER_SIZE field", ((HEADER_SIZE) + size), spiller.getBytesBlocked());
    }
}

