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
package org.apache.harmony.nio.tests.java.nio.channels;


import java.io.IOException;
import java.nio.channels.Pipe;
import junit.framework.TestCase;


/* Tests for Pipe and its default implementation */
public class PipeTest extends TestCase {
    /**
     *
     *
     * @unknown java.nio.channels.Pipe#open()
     */
    public void test_open() throws IOException {
        Pipe pipe = Pipe.open();
        TestCase.assertNotNull(pipe);
    }

    /**
     *
     *
     * @unknown java.nio.channels.Pipe#sink()
     */
    public void test_sink() throws IOException {
        Pipe pipe = Pipe.open();
        Pipe.SinkChannel sink = pipe.sink();
        TestCase.assertTrue(sink.isBlocking());
    }

    /**
     *
     *
     * @unknown java.nio.channels.Pipe#source()
     */
    public void test_source() throws IOException {
        Pipe pipe = Pipe.open();
        Pipe.SourceChannel source = pipe.source();
        TestCase.assertTrue(source.isBlocking());
    }
}

