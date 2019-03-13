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
package org.apache.hadoop.hdfs.protocol.datatransfer;


import java.io.IOException;
import org.junit.Test;


public class TestPacketReceiver {
    private static final long OFFSET_IN_BLOCK = 12345L;

    private static final int SEQNO = 54321;

    @Test
    public void testReceiveAndMirror() throws IOException {
        PacketReceiver pr = new PacketReceiver(false);
        // Test three different lengths, to force reallocing
        // the buffer as it grows.
        doTestReceiveAndMirror(pr, 100, 10);
        doTestReceiveAndMirror(pr, 50, 10);
        doTestReceiveAndMirror(pr, 150, 10);
        pr.close();
    }
}

