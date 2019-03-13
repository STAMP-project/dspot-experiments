/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.data.input.impl;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.prefetch.JsonIterator;
import org.junit.Assert;
import org.junit.Test;


public class SqlFirehoseTest {
    private List<Map<String, Object>> inputs;

    private List<FileInputStream> fileList;

    private MapInputRowParser parser = null;

    private ObjectMapper objectMapper;

    private static File TEST_DIR;

    @Test
    public void testFirehose() throws Exception {
        final SqlFirehoseTest.TestCloseable closeable = new SqlFirehoseTest.TestCloseable();
        List<Object> expectedResults = new ArrayList<>();
        for (Map<String, Object> map : inputs) {
            expectedResults.add(map.get("x"));
        }
        final List<JsonIterator> lineIterators = fileList.stream().map(( s) -> new JsonIterator(new TypeReference<Map<String, Object>>() {}, s, closeable, objectMapper)).collect(Collectors.toList());
        try (final SqlFirehose firehose = new SqlFirehose(lineIterators.iterator(), parser, closeable)) {
            final List<Object> results = new ArrayList<>();
            while (firehose.hasMore()) {
                final InputRow inputRow = firehose.nextRow();
                if (inputRow == null) {
                    results.add(null);
                } else {
                    results.add(inputRow.getDimension("x").get(0));
                }
            } 
            Assert.assertEquals(expectedResults, results);
        }
    }

    @Test
    public void testClose() throws IOException {
        File file = File.createTempFile("test", "", SqlFirehoseTest.TEST_DIR);
        final SqlFirehoseTest.TestCloseable closeable = new SqlFirehoseTest.TestCloseable();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            final JsonGenerator jg = objectMapper.getFactory().createGenerator(fos);
            jg.writeStartArray();
            jg.writeEndArray();
            jg.close();
        }
        final JsonIterator<Map<String, Object>> jsonIterator = new JsonIterator(new TypeReference<Map<String, Object>>() {}, new FileInputStream(file), closeable, objectMapper);
        final SqlFirehose firehose = new SqlFirehose(ImmutableList.of(jsonIterator).iterator(), parser, closeable);
        firehose.hasMore();// initialize lineIterator

        firehose.close();
        Assert.assertTrue(closeable.closed);
    }

    private static final class TestCloseable implements Closeable {
        private boolean closed;

        @Override
        public void close() {
            closed = true;
        }
    }
}

