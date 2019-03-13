/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.erasurecode.rawcoder;


import org.apache.hadoop.io.erasurecode.ErasureCodeNative;
import org.junit.Assume;
import org.junit.Test;

import static org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderBenchmark.CODER.DUMMY_CODER;
import static org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderBenchmark.CODER.ISAL_CODER;
import static org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderBenchmark.CODER.LEGACY_RS_CODER;
import static org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderBenchmark.CODER.RS_CODER;


/**
 * Tests for the raw erasure coder benchmark tool.
 */
public class TestRawErasureCoderBenchmark {
    @Test
    public void testDummyCoder() throws Exception {
        // Dummy coder
        RawErasureCoderBenchmark.performBench("encode", DUMMY_CODER, 2, 100, 1024);
        RawErasureCoderBenchmark.performBench("decode", DUMMY_CODER, 5, 150, 100);
    }

    @Test
    public void testLegacyRSCoder() throws Exception {
        // Legacy RS Java coder
        RawErasureCoderBenchmark.performBench("encode", LEGACY_RS_CODER, 2, 80, 200);
        RawErasureCoderBenchmark.performBench("decode", LEGACY_RS_CODER, 5, 300, 350);
    }

    @Test
    public void testRSCoder() throws Exception {
        // RS Java coder
        RawErasureCoderBenchmark.performBench("encode", RS_CODER, 3, 200, 200);
        RawErasureCoderBenchmark.performBench("decode", RS_CODER, 4, 135, 20);
    }

    @Test
    public void testISALCoder() throws Exception {
        Assume.assumeTrue(ErasureCodeNative.isNativeCodeLoaded());
        // ISA-L coder
        RawErasureCoderBenchmark.performBench("encode", ISAL_CODER, 5, 300, 64);
        RawErasureCoderBenchmark.performBench("decode", ISAL_CODER, 6, 200, 128);
    }
}

