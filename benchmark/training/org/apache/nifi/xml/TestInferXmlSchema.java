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
package org.apache.nifi.xml;


import RecordFieldType.DATE;
import RecordFieldType.INT;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.schema.inference.InferSchemaAccessStrategy;
import org.apache.nifi.schema.inference.RecordSourceFactory;
import org.apache.nifi.schema.inference.SchemaInferenceEngine;
import org.apache.nifi.schema.inference.TimeValueInference;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.xml.inference.XmlNode;
import org.apache.nifi.xml.inference.XmlRecordSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestInferXmlSchema {
    private final TimeValueInference timeValueInference = new TimeValueInference("MM/dd/yyyy", "HH:mm:ss", "MM/dd/yyyy HH:mm:ss.SSS");

    @Test
    public void testFlatXml() throws IOException {
        final File file = new File("src/test/resources/xml/person.xml");
        final RecordSourceFactory<XmlNode> xmlSourceFactory = ( var, in) -> new XmlRecordSource(in, false);
        final SchemaInferenceEngine<XmlNode> schemaInference = new org.apache.nifi.xml.inference.XmlSchemaInference(timeValueInference);
        final InferSchemaAccessStrategy<XmlNode> inferStrategy = new InferSchemaAccessStrategy(xmlSourceFactory, schemaInference, Mockito.mock(ComponentLog.class));
        final RecordSchema schema;
        try (final InputStream fis = new FileInputStream(file);final InputStream in = new BufferedInputStream(fis)) {
            schema = inferStrategy.getSchema(Collections.emptyMap(), in, null);
        }
        Assert.assertSame(STRING, schema.getDataType("NAME").get().getFieldType());
        Assert.assertSame(INT, schema.getDataType("AGE").get().getFieldType());
        Assert.assertSame(STRING, schema.getDataType("COUNTRY").get().getFieldType());
        Assert.assertEquals(DATE.getDataType(timeValueInference.getDateFormat()), schema.getDataType("DOB").get());
        Assert.assertEquals(TIME.getDataType(timeValueInference.getTimeFormat()), schema.getDataType("TOB").get());
        Assert.assertEquals(TIMESTAMP.getDataType(timeValueInference.getTimestampFormat()), schema.getDataType("TSOB").get());
    }

    @Test
    public void testFieldsFromAllRecordsIncluded() throws IOException {
        final File file = new File("src/test/resources/xml/people_nested.xml");
        final RecordSourceFactory<XmlNode> xmlSourceFactory = ( var, in) -> new XmlRecordSource(in, true);
        final SchemaInferenceEngine<XmlNode> schemaInference = new org.apache.nifi.xml.inference.XmlSchemaInference(timeValueInference);
        final InferSchemaAccessStrategy<XmlNode> inferStrategy = new InferSchemaAccessStrategy(xmlSourceFactory, schemaInference, Mockito.mock(ComponentLog.class));
        final RecordSchema schema;
        try (final InputStream fis = new FileInputStream(file);final InputStream in = new BufferedInputStream(fis)) {
            schema = inferStrategy.getSchema(Collections.emptyMap(), in, null);
        }
        Assert.assertSame(STRING, schema.getDataType("NAME").get().getFieldType());
        Assert.assertSame(INT, schema.getDataType("AGE").get().getFieldType());
        Assert.assertSame(STRING, schema.getDataType("COUNTRY").get().getFieldType());
        Assert.assertEquals(DATE.getDataType(timeValueInference.getDateFormat()), schema.getDataType("DOB").get());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("TOB").get());
        Assert.assertEquals(TIMESTAMP.getDataType(timeValueInference.getTimestampFormat()), schema.getDataType("TSOB").get());
        final DataType addressDataType = schema.getDataType("ADDRESS").get();
        final RecordSchema addressSchema = getChildSchema();
        Assert.assertSame(STRING, addressSchema.getDataType("STREET").get().getFieldType());
        Assert.assertSame(STRING, addressSchema.getDataType("CITY").get().getFieldType());
        Assert.assertSame(STRING, addressSchema.getDataType("STATE").get().getFieldType());
    }
}

