/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.file;


import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;


public class TestSeekableByteArrayInput {
    @Test
    public void testSerialization() throws Exception {
        Schema testSchema = getTestSchema();
        GenericRecord message = new org.apache.avro.generic.GenericData.Record(testSchema);
        message.put("name", "testValue");
        byte[] data = getSerializedMessage(message, testSchema);
        GenericDatumReader<IndexedRecord> reader = new GenericDatumReader(testSchema);
        SeekableInput in = new SeekableByteArrayInput(data);
        FileReader<IndexedRecord> dfr = null;
        IndexedRecord result = null;
        try {
            dfr = DataFileReader.openReader(in, reader);
            result = dfr.next();
        } finally {
            if (dfr != null) {
                dfr.close();
            }
        }
        Assert.assertNotNull(result);
        Assert.assertTrue((result instanceof GenericRecord));
        Assert.assertEquals(new Utf8("testValue"), get("name"));
    }
}

