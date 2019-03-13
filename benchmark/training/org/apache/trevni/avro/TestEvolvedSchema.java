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
package org.apache.trevni.avro;


import GenericData.Record;
import java.io.File;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.trevni.ColumnFileMetaData;
import org.apache.trevni.avro.AvroColumnReader.Params;
import org.junit.Assert;
import org.junit.Test;


public class TestEvolvedSchema {
    private static String writerSchema = "{" + ((((((("    \"namespace\": \"org.apache.avro\"," + "    \"name\": \"test_evolution\",") + "    \"type\": \"record\",") + "    \"fields\": [") + "        { \"name\": \"a\", \"type\":\"string\" },") + "        { \"name\": \"b\", \"type\":\"int\" }") + "     ]") + "}");

    private static String innerSchema = "{\"name\":\"c1\"," + (("          \"type\":\"record\"," + "          \"fields\":[{\"name\":\"c11\", \"type\":\"int\", \"default\": 2},") + "                      {\"name\":\"c12\", \"type\":\"string\", \"default\":\"goodbye\"}]}");

    private static String evolvedSchema2 = (((((("{" + (((((("    \"namespace\": \"org.apache.avro\"," + "    \"name\": \"test_evolution\",") + "    \"type\": \"record\",") + "    \"fields\": [") + "        { \"name\": \"a\", \"type\":\"string\" },") + "        { \"name\": \"b\", \"type\":\"int\" },") + "        { \"name\": \"c\", \"type\":")) + (TestEvolvedSchema.innerSchema)) + ",") + "          \"default\":{\"c11\": 1, \"c12\": \"hello\"}") + "        }") + "     ]") + "}";

    Record writtenRecord;

    Record evolvedRecord;

    Record innerRecord;

    private static final Schema writer = Schema.parse(TestEvolvedSchema.writerSchema);

    private static final Schema evolved = Schema.parse(TestEvolvedSchema.evolvedSchema2);

    private static final Schema inner = Schema.parse(TestEvolvedSchema.innerSchema);

    @Test
    public void testTrevniEvolvedRead() throws IOException {
        AvroColumnWriter<GenericRecord> acw = new AvroColumnWriter(TestEvolvedSchema.writer, new ColumnFileMetaData());
        acw.write(writtenRecord);
        File serializedTrevni = File.createTempFile("trevni", null);
        acw.writeTo(serializedTrevni);
        AvroColumnReader.Params params = new Params(serializedTrevni);
        params.setSchema(TestEvolvedSchema.evolved);
        AvroColumnReader<GenericRecord> acr = new AvroColumnReader(params);
        GenericRecord readRecord = acr.next();
        Assert.assertEquals(evolvedRecord, readRecord);
        Assert.assertFalse(acr.hasNext());
    }

    @Test
    public void testAvroEvolvedRead() throws IOException {
        File serializedAvro = File.createTempFile("avro", null);
        DatumWriter<GenericRecord> dw = new org.apache.avro.generic.GenericDatumWriter(TestEvolvedSchema.writer);
        DataFileWriter<GenericRecord> dfw = new DataFileWriter(dw);
        dfw.create(TestEvolvedSchema.writer, serializedAvro);
        dfw.append(writtenRecord);
        dfw.flush();
        dfw.close();
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader(TestEvolvedSchema.writer);
        reader.setExpected(TestEvolvedSchema.evolved);
        DataFileReader<GenericRecord> dfr = new DataFileReader(serializedAvro, reader);
        GenericRecord readRecord = dfr.next();
        Assert.assertEquals(evolvedRecord, readRecord);
        Assert.assertFalse(dfr.hasNext());
    }
}

