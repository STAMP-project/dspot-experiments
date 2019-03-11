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
package org.apache.flink.formats.csv;


import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.factories.DeserializationSchemaFactory;
import org.apache.flink.table.factories.SerializationSchemaFactory;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.types.Row;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CsvRowFormatFactory}.
 */
public class CsvRowFormatFactoryTest extends TestLogger {
    private static final TypeInformation<Row> SCHEMA = Types.ROW(new String[]{ "a", "b", "c" }, new TypeInformation[]{ Types.STRING(), Types.INT(), Types.ROW(new String[]{ "a", "b", "c" }, new TypeInformation[]{ Types.STRING(), Types.INT(), Types.BOOLEAN() }) });

    @Test
    public void testSchema() {
        final Map<String, String> properties = new Csv().schema(CsvRowFormatFactoryTest.SCHEMA).fieldDelimiter(';').lineDelimiter("\r\n").quoteCharacter('\'').allowComments().ignoreParseErrors().arrayElementDelimiter("|").escapeCharacter('\\').nullLiteral("n/a").toProperties();
        final CsvRowDeserializationSchema expectedDeser = setFieldDelimiter(';').setQuoteCharacter('\'').setAllowComments(true).setIgnoreParseErrors(true).setArrayElementDelimiter("|").setEscapeCharacter('\\').setNullLiteral("n/a").build();
        final DeserializationSchema<?> actualDeser = TableFactoryService.find(DeserializationSchemaFactory.class, properties).createDeserializationSchema(properties);
        Assert.assertEquals(expectedDeser, actualDeser);
        final CsvRowSerializationSchema expectedSer = build();
        final SerializationSchema<?> actualSer = TableFactoryService.find(SerializationSchemaFactory.class, properties).createSerializationSchema(properties);
        Assert.assertEquals(expectedSer, actualSer);
    }

    @Test
    public void testSchemaDerivation() {
        final Map<String, String> properties = new HashMap<>();
        properties.putAll(new Schema().schema(TableSchema.fromTypeInfo(CsvRowFormatFactoryTest.SCHEMA)).toProperties());
        properties.putAll(new Csv().deriveSchema().toProperties());
        final CsvRowSerializationSchema expectedSer = build();
        final CsvRowDeserializationSchema expectedDeser = new CsvRowDeserializationSchema.Builder(CsvRowFormatFactoryTest.SCHEMA).build();
        final SerializationSchema<?> actualSer = TableFactoryService.find(SerializationSchemaFactory.class, properties).createSerializationSchema(properties);
        Assert.assertEquals(expectedSer, actualSer);
        final DeserializationSchema<?> actualDeser = TableFactoryService.find(DeserializationSchemaFactory.class, properties).createDeserializationSchema(properties);
        Assert.assertEquals(expectedDeser, actualDeser);
    }
}

