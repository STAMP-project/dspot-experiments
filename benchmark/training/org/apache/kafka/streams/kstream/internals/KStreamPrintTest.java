/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;


import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.junit.Assert;
import org.junit.Test;


public class KStreamPrintTest {
    private ByteArrayOutputStream byteOutStream;

    private Processor<Integer, String> printProcessor;

    @Test
    @SuppressWarnings("unchecked")
    public void testPrintStreamWithProvidedKeyValueMapper() {
        final List<KeyValue<Integer, String>> inputRecords = Arrays.asList(new KeyValue(0, "zero"), new KeyValue(1, "one"), new KeyValue(2, "two"), new KeyValue(3, "three"));
        final String[] expectedResult = new String[]{ "[test-stream]: 0, zero", "[test-stream]: 1, one", "[test-stream]: 2, two", "[test-stream]: 3, three" };
        for (final KeyValue<Integer, String> record : inputRecords) {
            printProcessor.process(record.key, record.value);
        }
        printProcessor.close();
        final String[] flushOutDatas = new String(byteOutStream.toByteArray(), StandardCharsets.UTF_8).split("\\r*\\n");
        for (int i = 0; i < (flushOutDatas.length); i++) {
            Assert.assertEquals(expectedResult[i], flushOutDatas[i]);
        }
    }
}

