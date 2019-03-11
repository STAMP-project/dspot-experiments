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
package org.apache.nifi.hbase.io;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.nifi.hbase.scan.ResultCell;
import org.junit.Assert;
import org.junit.Test;


public class TestJsonFullRowSerializer {
    static final String ROW = "row1";

    static final String FAM1 = "colFam1";

    static final String QUAL1 = "colQual1";

    static final String VAL1 = "val1";

    static final long TS1 = 1111111111;

    static final String FAM2 = "colFam2";

    static final String QUAL2 = "colQual2";

    static final String VAL2 = "val2";

    static final long TS2 = 222222222;

    private final byte[] rowKey = TestJsonFullRowSerializer.ROW.getBytes(StandardCharsets.UTF_8);

    private ResultCell[] cells;

    @Test
    public void testSerializeRegular() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final RowSerializer rowSerializer = new JsonFullRowSerializer(StandardCharsets.UTF_8, StandardCharsets.UTF_8);
        rowSerializer.serialize(rowKey, cells, out);
        final String json = out.toString(StandardCharsets.UTF_8.name());
        Assert.assertEquals((((((((((((((((((("{\"row\":\"row1\", \"cells\": [" + "{\"fam\":\"") + (TestJsonFullRowSerializer.FAM1)) + "\",\"qual\":\"") + (TestJsonFullRowSerializer.QUAL1)) + "\",\"val\":\"") + (TestJsonFullRowSerializer.VAL1)) + "\",\"ts\":") + (TestJsonFullRowSerializer.TS1)) + "}, ") + "{\"fam\":\"") + (TestJsonFullRowSerializer.FAM2)) + "\",\"qual\":\"") + (TestJsonFullRowSerializer.QUAL2)) + "\",\"val\":\"") + (TestJsonFullRowSerializer.VAL2)) + "\",\"ts\":") + (TestJsonFullRowSerializer.TS2)) + "}]}"), json);
    }

    @Test
    public void testSerializeWithBase64() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final RowSerializer rowSerializer = new JsonFullRowSerializer(StandardCharsets.UTF_8, StandardCharsets.UTF_8, true);
        rowSerializer.serialize(rowKey, cells, out);
        final String rowBase64 = Base64.encodeBase64String(TestJsonFullRowSerializer.ROW.getBytes(StandardCharsets.UTF_8));
        final String fam1Base64 = Base64.encodeBase64String(TestJsonFullRowSerializer.FAM1.getBytes(StandardCharsets.UTF_8));
        final String qual1Base64 = Base64.encodeBase64String(TestJsonFullRowSerializer.QUAL1.getBytes(StandardCharsets.UTF_8));
        final String val1Base64 = Base64.encodeBase64String(TestJsonFullRowSerializer.VAL1.getBytes(StandardCharsets.UTF_8));
        final String fam2Base64 = Base64.encodeBase64String(TestJsonFullRowSerializer.FAM2.getBytes(StandardCharsets.UTF_8));
        final String qual2Base64 = Base64.encodeBase64String(TestJsonFullRowSerializer.QUAL2.getBytes(StandardCharsets.UTF_8));
        final String val2Base64 = Base64.encodeBase64String(TestJsonFullRowSerializer.VAL2.getBytes(StandardCharsets.UTF_8));
        final String json = out.toString(StandardCharsets.UTF_8.name());
        Assert.assertEquals((((((((((((((((((((("{\"row\":\"" + rowBase64) + "\", \"cells\": [") + "{\"fam\":\"") + fam1Base64) + "\",\"qual\":\"") + qual1Base64) + "\",\"val\":\"") + val1Base64) + "\",\"ts\":") + (TestJsonFullRowSerializer.TS1)) + "}, ") + "{\"fam\":\"") + fam2Base64) + "\",\"qual\":\"") + qual2Base64) + "\",\"val\":\"") + val2Base64) + "\",\"ts\":") + (TestJsonFullRowSerializer.TS2)) + "}]}"), json);
    }
}

