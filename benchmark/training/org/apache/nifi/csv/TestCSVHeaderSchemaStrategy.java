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
package org.apache.nifi.csv;


import CSVUtils.COMMENT_MARKER;
import CSVUtils.CSV_FORMAT;
import CSVUtils.CUSTOM;
import CSVUtils.ESCAPE_CHAR;
import CSVUtils.QUOTE_CHAR;
import CSVUtils.TRIM_FIELDS;
import CSVUtils.VALUE_SEPARATOR;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;


public class TestCSVHeaderSchemaStrategy {
    @Test
    public void testSimple() throws IOException, SchemaNotFoundException {
        final String headerLine = "a, b, c, d, e\\,z, f";
        final byte[] headerBytes = headerLine.getBytes();
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(CSV_FORMAT, CUSTOM.getValue());
        properties.put(COMMENT_MARKER, "#");
        properties.put(VALUE_SEPARATOR, ",");
        properties.put(TRIM_FIELDS, "true");
        properties.put(QUOTE_CHAR, "\"");
        properties.put(ESCAPE_CHAR, "\\");
        final ConfigurationContext context = new org.apache.nifi.util.MockConfigurationContext(properties, null);
        final CSVHeaderSchemaStrategy strategy = new CSVHeaderSchemaStrategy(context);
        final RecordSchema schema;
        try (final InputStream bais = new ByteArrayInputStream(headerBytes)) {
            schema = strategy.getSchema(null, bais, null);
        }
        final List<String> expectedFieldNames = Arrays.asList("a", "b", "c", "d", "e,z", "f");
        Assert.assertEquals(expectedFieldNames, schema.getFieldNames());
        Assert.assertTrue(schema.getFields().stream().allMatch(( field) -> field.getDataType().equals(RecordFieldType.STRING.getDataType())));
    }
}

