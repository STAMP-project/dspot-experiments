/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.tools;


import java.io.File;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Test;


public class TestFileChannelIntegrityTool {
    private static File baseDir;

    private static File origCheckpointDir;

    private static File origDataDir;

    private static Event event;

    private static Context ctx;

    private File checkpointDir;

    private File dataDir;

    private static int invalidEvent = 0;

    @Test
    public void testFixCorruptRecordsWithCheckpoint() throws Exception {
        doTestFixCorruptEvents(true);
    }

    @Test
    public void testFixCorruptRecords() throws Exception {
        doTestFixCorruptEvents(false);
    }

    @Test
    public void testFixInvalidRecords() throws Exception {
        doTestFixInvalidEvents(false, TestFileChannelIntegrityTool.DummyEventVerifier.Builder.class.getName());
    }

    @Test
    public void testFixInvalidRecordsWithCheckpoint() throws Exception {
        doTestFixInvalidEvents(true, TestFileChannelIntegrityTool.DummyEventVerifier.Builder.class.getName());
    }

    public static class DummyEventVerifier implements EventValidator {
        private int value = 0;

        private DummyEventVerifier(int val) {
            value = val;
        }

        @Override
        public boolean validateEvent(Event event) {
            return (event.getBody()[0]) != (value);
        }

        public static class Builder implements EventValidator.Builder {
            private int binaryValidator = 0;

            @Override
            public EventValidator build() {
                return new TestFileChannelIntegrityTool.DummyEventVerifier(binaryValidator);
            }

            @Override
            public void configure(Context context) {
                binaryValidator = context.getInteger("validatorValue");
            }
        }
    }
}

