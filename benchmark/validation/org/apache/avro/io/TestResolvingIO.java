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


import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.avro.io.TestValidatingIO.Encoding.BINARY;
import static org.apache.avro.io.TestValidatingIO.Encoding.BLOCKING_BINARY;
import static org.apache.avro.io.TestValidatingIO.Encoding.JSON;


@RunWith(Parameterized.class)
public class TestResolvingIO {
    protected final TestValidatingIO.Encoding eEnc;

    protected final int iSkipL;

    protected final String sJsWrtSchm;

    protected final String sWrtCls;

    protected final String sJsRdrSchm;

    protected final String sRdrCls;

    public TestResolvingIO(TestValidatingIO.Encoding encoding, int skipLevel, String jsonWriterSchema, String writerCalls, String jsonReaderSchema, String readerCalls) {
        this.eEnc = encoding;
        this.iSkipL = skipLevel;
        this.sJsWrtSchm = jsonWriterSchema;
        this.sWrtCls = writerCalls;
        this.sJsRdrSchm = jsonReaderSchema;
        this.sRdrCls = readerCalls;
    }

    @Test
    public void testIdentical() throws IOException {
        performTest(eEnc, iSkipL, sJsWrtSchm, sWrtCls, sJsWrtSchm, sWrtCls);
    }

    private static final int COUNT = 10;

    @Test
    public void testCompatible() throws IOException {
        performTest(eEnc, iSkipL, sJsWrtSchm, sWrtCls, sJsRdrSchm, sRdrCls);
    }

    static Object[][] encodings = new Object[][]{ new Object[]{ BINARY }, new Object[]{ BLOCKING_BINARY }, new Object[]{ JSON } };

    static Object[][] skipLevels = new Object[][]{ new Object[]{ -1 }, new Object[]{ 0 }, new Object[]{ 1 }, new Object[]{ 2 } };
}

