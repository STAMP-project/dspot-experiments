/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.provenance;


import ProvenanceEventType.RECEIVE;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class TestStandardRecordReaderWriter extends AbstractTestRecordReaderWriter {
    private AtomicLong idGenerator = new AtomicLong(0L);

    @Test
    public void testWriteUtfLargerThan64k() throws IOException, InterruptedException {
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "1.txt");
        attributes.put("uuid", UUID.randomUUID().toString());
        final ProvenanceEventBuilder builder = new StandardProvenanceEventRecord.Builder();
        builder.setEventTime(System.currentTimeMillis());
        builder.setEventType(RECEIVE);
        builder.setTransitUri("nifi://unit-test");
        builder.fromFlowFile(TestUtil.createFlowFile(3L, 3000L, attributes));
        builder.setComponentId("1234");
        builder.setComponentType("dummy processor");
        final String seventyK = StringUtils.repeat("X", 70000);
        Assert.assertTrue(((seventyK.length()) > 65535));
        Assert.assertTrue(((seventyK.getBytes("UTF-8").length) > 65535));
        builder.setDetails(seventyK);
        final ProvenanceEventRecord record = builder.build();
        try (final ByteArrayOutputStream headerOut = new ByteArrayOutputStream();final DataOutputStream out = new DataOutputStream(headerOut)) {
            out.writeUTF(PersistentProvenanceRepository.class.getName());
            out.writeInt(9);
        }
        try (final ByteArrayOutputStream recordOut = new ByteArrayOutputStream();final StandardRecordWriter writer = new StandardRecordWriter(recordOut, "devnull", idGenerator, null, false, 0)) {
            writer.writeHeader(1L);
            recordOut.reset();
            writer.writeRecord(record);
        }
    }
}

