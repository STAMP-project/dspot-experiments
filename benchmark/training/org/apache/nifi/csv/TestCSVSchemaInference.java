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


import CSVUtils.TRIM_FIELDS;
import RecordFieldType.DATE;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.context.PropertyContext;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.schema.inference.InferSchemaAccessStrategy;
import org.apache.nifi.schema.inference.TimeValueInference;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestCSVSchemaInference {
    private final TimeValueInference timestampInference = new TimeValueInference("yyyy-MM-dd", "HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Test
    public void testInferenceIncludesAllRecords() throws IOException {
        final File file = new File("src/test/resources/csv/prov-events.csv");
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        new CSVReader().getSupportedPropertyDescriptors().forEach(( prop) -> properties.put(prop, prop.getDefaultValue()));
        properties.put(TRIM_FIELDS, "true");
        final PropertyContext context = new org.apache.nifi.util.MockConfigurationContext(properties, null);
        final RecordSchema schema;
        try (final InputStream in = new FileInputStream(file);final InputStream bufferedIn = new BufferedInputStream(in)) {
            final InferSchemaAccessStrategy<?> accessStrategy = new InferSchemaAccessStrategy(( var, content) -> new CSVRecordSource(content, context), new CSVSchemaInference(timestampInference), Mockito.mock(ComponentLog.class));
            schema = accessStrategy.getSchema(null, bufferedIn, null);
        }
        Assert.assertSame(STRING, schema.getDataType("eventId").get().getFieldType());
        Assert.assertSame(INT, schema.getDataType("eventOrdinal").get().getFieldType());
        Assert.assertSame(STRING, schema.getDataType("eventType").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("timestampMillis").get().getFieldType());
        Assert.assertEquals(TIMESTAMP.getDataType("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), schema.getDataType("timestamp").get());
        Assert.assertEquals(TIME.getDataType("HH:mm:ss"), schema.getDataType("eventTime").get());
        Assert.assertEquals(DATE.getDataType("yyyy-MM-dd"), schema.getDataType("eventDate").get());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("maybeTime").get());
        Assert.assertEquals(DATE.getDataType("yyyy-MM-dd"), schema.getDataType("maybeDate").get());
        Assert.assertSame(INT, schema.getDataType("parentIds").get().getFieldType());
        Assert.assertSame(STRING, schema.getDataType("numeric string").get().getFieldType());
        final List<String> fieldNames = schema.getFieldNames();
        Assert.assertEquals(Arrays.asList("eventId", "eventOrdinal", "eventType", "timestampMillis", "timestamp", "eventDate", "eventTime", "maybeTime", "maybeDate", "durationMillis", "lineageStart", "componentId", "componentType", "componentName", "processGroupId", "processGroupName", "entityId", "entityType", "entitySize", "previousEntitySize", "updatedAttributes", "actorHostname", "contentURI", "previousContentURI", "parentIds", "childIds", "platform", "application", "extra field", "numeric string"), fieldNames);
    }
}

