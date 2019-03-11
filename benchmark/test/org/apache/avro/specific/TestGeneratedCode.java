/**
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.specific;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.test.FullRecordV1;
import org.apache.avro.specific.test.FullRecordV2;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;


public class TestGeneratedCode {
    private static final SpecificData MODEL = new SpecificData();

    private static final Schema V1S = FullRecordV1.getClassSchema();

    private static final Schema V2S = FullRecordV2.getClassSchema();

    @Test
    public void withoutSchemaMigration() throws IOException {
        FullRecordV1 src = new FullRecordV1(true, 87231, 731L, 54.2832F, 38.321, "Hi there", null);
        Assert.assertTrue("Test schema must allow for custom coders.", hasCustomCoders());
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        Encoder e = EncoderFactory.get().directBinaryEncoder(out, null);
        DatumWriter<FullRecordV1> w = ((DatumWriter<FullRecordV1>) (TestGeneratedCode.MODEL.createDatumWriter(TestGeneratedCode.V1S)));
        w.write(src, e);
        e.flush();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Decoder d = DecoderFactory.get().directBinaryDecoder(in, null);
        DatumReader<FullRecordV1> r = ((DatumReader<FullRecordV1>) (TestGeneratedCode.MODEL.createDatumReader(TestGeneratedCode.V1S)));
        FullRecordV1 dst = r.read(null, d);
        Assert.assertEquals(src, dst);
    }

    @Test
    public void withSchemaMigration() throws IOException {
        FullRecordV2 src = new FullRecordV2(true, 731, 87231, 38L, 54.2832F, "Hi there", ByteBuffer.wrap(Utf8.getBytesFor("Hello, world!")));
        Assert.assertTrue("Test schema must allow for custom coders.", hasCustomCoders());
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        Encoder e = EncoderFactory.get().directBinaryEncoder(out, null);
        DatumWriter<FullRecordV2> w = ((DatumWriter<FullRecordV2>) (TestGeneratedCode.MODEL.createDatumWriter(TestGeneratedCode.V2S)));
        w.write(src, e);
        e.flush();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Decoder d = DecoderFactory.get().directBinaryDecoder(in, null);
        DatumReader<FullRecordV1> r = ((DatumReader<FullRecordV1>) (TestGeneratedCode.MODEL.createDatumReader(TestGeneratedCode.V2S, TestGeneratedCode.V1S)));
        FullRecordV1 dst = r.read(null, d);
        FullRecordV1 expected = new FullRecordV1(true, 87231, 731L, 54.2832F, 38.0, null, "Hello, world!");
        Assert.assertEquals(expected, dst);
    }
}

