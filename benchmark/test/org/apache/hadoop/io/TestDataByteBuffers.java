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
package org.apache.hadoop.io;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Test;


public class TestDataByteBuffers {
    private static final Random RAND = new Random(31L);

    @Test
    public void testBaseBuffers() throws IOException {
        DataOutputBuffer dob = new DataOutputBuffer();
        TestDataByteBuffers.writeJunk(dob, 1000);
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(dob.getData(), 0, dob.getLength());
        TestDataByteBuffers.readJunk(dib, 1000);
        dob.reset();
        TestDataByteBuffers.writeJunk(dob, 1000);
        dib.reset(dob.getData(), 0, dob.getLength());
        TestDataByteBuffers.readJunk(dib, 1000);
    }

    @Test
    public void testDataInputByteBufferCompatibility() throws IOException {
        DataOutputBuffer dob = new DataOutputBuffer();
        TestDataByteBuffers.writeJunk(dob, 1000);
        ByteBuffer buf = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        DataInputByteBuffer dib = new DataInputByteBuffer();
        dib.reset(buf);
        TestDataByteBuffers.readJunk(dib, 1000);
    }
}

