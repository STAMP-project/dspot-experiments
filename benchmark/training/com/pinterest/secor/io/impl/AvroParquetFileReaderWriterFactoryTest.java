/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.pinterest.secor.io.impl;


import com.google.common.io.Files;
import com.pinterest.secor.common.LogFilePath;
import com.pinterest.secor.common.SecorConfig;
import com.pinterest.secor.common.SecorSchemaRegistryClient;
import com.pinterest.secor.io.FileReader;
import com.pinterest.secor.io.FileWriter;
import com.pinterest.secor.io.KeyValue;
import junit.framework.TestCase;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class AvroParquetFileReaderWriterFactoryTest extends TestCase {
    private AvroParquetFileReaderWriterFactory mFactory;

    private SpecificDatumWriter<GenericRecord> writer;

    private SecorConfig config;

    private SecorSchemaRegistryClient secorSchemaRegistryClient;

    private GenericRecord msg1;

    private GenericRecord msg2;

    @Test
    public void testAvroParquetReadWriteRoundTrip() throws Exception {
        Mockito.when(config.getFileReaderWriterFactory()).thenReturn(AvroParquetFileReaderWriterFactory.class.getName());
        LogFilePath tempLogFilePath = new LogFilePath(Files.createTempDir().toString(), "test-avro-topic", new String[]{ "part-1" }, 0, 1, 23232, ".avro");
        FileWriter fileWriter = mFactory.BuildFileWriter(tempLogFilePath, null);
        KeyValue kv1 = new KeyValue(23232, AvroParquetFileReaderWriterFactory.serializeAvroRecord(writer, msg1));
        KeyValue kv2 = new KeyValue(23233, AvroParquetFileReaderWriterFactory.serializeAvroRecord(writer, msg2));
        fileWriter.write(kv1);
        fileWriter.write(kv2);
        fileWriter.close();
        FileReader fileReader = mFactory.BuildFileReader(tempLogFilePath, null);
        KeyValue kvout = fileReader.next();
        TestCase.assertEquals(kv1.getOffset(), kvout.getOffset());
        Assert.assertArrayEquals(kv1.getValue(), kvout.getValue());
        TestCase.assertEquals(msg1, secorSchemaRegistryClient.decodeMessage("test-avro-topic", kvout.getValue()));
        kvout = fileReader.next();
        TestCase.assertEquals(kv2.getOffset(), kvout.getOffset());
        Assert.assertArrayEquals(kv2.getValue(), kvout.getValue());
        TestCase.assertEquals(msg2, secorSchemaRegistryClient.decodeMessage("test-avro-topic", kvout.getValue()));
    }
}

