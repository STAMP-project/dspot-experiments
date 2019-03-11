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
package org.apache.nifi.processors.flume;


import CoreAttributes.FILENAME;
import ExecuteFlumeSink.FLUME_CONFIG;
import ExecuteFlumeSink.SINK_TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.flume.sink.NullSink;
import org.apache.flume.source.AvroSource;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecuteFlumeSinkTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteFlumeSinkTest.class);

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testValidators() {
        TestRunner runner = TestRunners.newTestRunner(ExecuteFlumeSink.class);
        Collection<ValidationResult> results;
        ProcessContext pc;
        results = new HashSet();
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            ExecuteFlumeSinkTest.logger.debug(vr.toString());
            Assert.assertTrue(vr.toString().contains("is invalid because Sink Type is required"));
        }
        // non-existent class
        results = new HashSet();
        runner.setProperty(SINK_TYPE, "invalid.class.name");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            ExecuteFlumeSinkTest.logger.debug(vr.toString());
            Assert.assertTrue(vr.toString().contains("is invalid because unable to load sink"));
        }
        // class doesn't implement Sink
        results = new HashSet();
        runner.setProperty(SINK_TYPE, AvroSource.class.getName());
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        for (ValidationResult vr : results) {
            ExecuteFlumeSinkTest.logger.debug(vr.toString());
            Assert.assertTrue(vr.toString().contains("is invalid because unable to create sink"));
        }
        results = new HashSet();
        runner.setProperty(SINK_TYPE, NullSink.class.getName());
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testNullSink() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ExecuteFlumeSink.class);
        runner.setProperty(SINK_TYPE, NullSink.class.getName());
        try (InputStream inputStream = getClass().getResourceAsStream("/testdata/records.txt")) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put(FILENAME.key(), "records.txt");
            runner.enqueue(inputStream, attributes);
            runner.run();
        }
    }

    @Test
    public void testBatchSize() throws IOException {
        TestRunner runner = TestRunners.newTestRunner(ExecuteFlumeSink.class);
        runner.setProperty(SINK_TYPE, NullSink.class.getName());
        runner.setProperty(FLUME_CONFIG, "tier1.sinks.sink-1.batchSize = 1000\n");
        for (int i = 0; i < 100000; i++) {
            runner.enqueue(String.valueOf(i).getBytes());
        }
        runner.run(100);
    }
}

