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
package org.apache.beam.runners.dataflow.worker;


import TestUtils.INTS;
import TestUtils.NO_INTS;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for AvroByteSink.
 */
// TODO: sharded filenames
// TODO: writing to GCS
@RunWith(JUnit4.class)
public class AvroByteSinkTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testWriteFile() throws Exception {
        runTestWriteFile(INTS, BigEndianIntegerCoder.of());
    }

    @Test
    public void testWriteEmptyFile() throws Exception {
        runTestWriteFile(NO_INTS, BigEndianIntegerCoder.of());
    }
}

