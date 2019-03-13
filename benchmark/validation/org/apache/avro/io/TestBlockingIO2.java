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
package org.apache.avro.io;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This class has more exhaustive tests for Blocking IO. The reason
 * we have both TestBlockingIO and TestBlockingIO2 is that with the
 * mnemonics used in TestBlockingIO2, it is hard to test skip() operations.
 * and with the test infrastructure of TestBlockingIO, it is hard to test
 * enums, unions etc.
 */
@RunWith(Parameterized.class)
public class TestBlockingIO2 {
    private final Decoder decoder;

    private final String calls;

    private Object[] values;

    public TestBlockingIO2(int bufferSize, int skipLevel, String calls) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        EncoderFactory factory = new EncoderFactory().configureBlockSize(bufferSize);
        Encoder encoder = factory.blockingBinaryEncoder(os, null);
        this.values = TestValidatingIO.randomValues(calls);
        TestValidatingIO.generate(encoder, calls, values);
        encoder.flush();
        byte[] bb = os.toByteArray();
        decoder = DecoderFactory.get().binaryDecoder(bb, null);
        this.calls = calls;
    }

    @Test
    public void testScan() throws IOException {
        TestValidatingIO.check(decoder, calls, values, (-1));
    }
}

