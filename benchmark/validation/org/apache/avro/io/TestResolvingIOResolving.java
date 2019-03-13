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
import org.apache.avro.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestResolvingIOResolving {
    protected TestValidatingIO.Encoding eEnc;

    protected final int iSkipL;

    protected final String sJsWrtSchm;

    protected final String sWrtCls;

    protected final String sJsRdrSchm;

    protected final String sRdrCls;

    protected final Object[] oaWrtVals;

    protected final Object[] oaRdrVals;

    public TestResolvingIOResolving(TestValidatingIO.Encoding encoding, int skipLevel, String jsonWriterSchema, String writerCalls, Object[] writerValues, String jsonReaderSchema, String readerCalls, Object[] readerValues) {
        this.eEnc = encoding;
        this.iSkipL = skipLevel;
        this.sJsWrtSchm = jsonWriterSchema;
        this.sWrtCls = writerCalls;
        this.oaWrtVals = writerValues;
        this.sJsRdrSchm = jsonReaderSchema;
        this.sRdrCls = readerCalls;
        this.oaRdrVals = readerValues;
    }

    @Test
    public void testResolving() throws IOException {
        Schema writerSchema = new Schema.Parser().parse(sJsWrtSchm);
        byte[] bytes = TestValidatingIO.make(writerSchema, sWrtCls, oaWrtVals, eEnc);
        Schema readerSchema = new Schema.Parser().parse(sJsRdrSchm);
        TestValidatingIO.print(eEnc, iSkipL, writerSchema, readerSchema, oaWrtVals, oaRdrVals);
        TestResolvingIO.check(writerSchema, readerSchema, bytes, sRdrCls, oaRdrVals, eEnc, iSkipL);
    }
}

