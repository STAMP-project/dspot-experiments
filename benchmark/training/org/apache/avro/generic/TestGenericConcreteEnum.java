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
package org.apache.avro.generic;


import java.io.IOException;
import org.apache.avro.FooBarSpecificRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.Assert;
import org.junit.Test;


/**
 * See AVRO-1810: GenericDatumWriter broken with Enum
 */
public class TestGenericConcreteEnum {
    @Test
    public void testGenericWriteAndRead() throws IOException {
        FooBarSpecificRecord specificRecord = getRecord();
        byte[] bytes = TestGenericConcreteEnum.serializeRecord(specificRecord);
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        GenericDatumReader<IndexedRecord> genericDatumReader = new GenericDatumReader(FooBarSpecificRecord.SCHEMA$);
        IndexedRecord deserialized = new GenericData.Record(FooBarSpecificRecord.SCHEMA$);
        genericDatumReader.read(deserialized, decoder);
        Assert.assertEquals(0, GenericData.get().compare(specificRecord, deserialized, FooBarSpecificRecord.SCHEMA$));
    }

    @Test
    public void testGenericWriteSpecificRead() throws IOException {
        FooBarSpecificRecord specificRecord = getRecord();
        byte[] bytes = TestGenericConcreteEnum.serializeRecord(specificRecord);
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        SpecificDatumReader<FooBarSpecificRecord> specificDatumReader = new SpecificDatumReader(FooBarSpecificRecord.SCHEMA$);
        FooBarSpecificRecord deserialized = new FooBarSpecificRecord();
        specificDatumReader.read(deserialized, decoder);
        Assert.assertEquals(specificRecord, deserialized);
    }
}

