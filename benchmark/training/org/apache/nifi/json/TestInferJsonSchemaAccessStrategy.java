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
package org.apache.nifi.json;


import RecordFieldType.ARRAY;
import RecordFieldType.CHOICE;
import RecordFieldType.DATE;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.nifi.schema.inference.SchemaInferenceEngine;
import org.apache.nifi.schema.inference.TimeValueInference;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.type.ChoiceDataType;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Test;


public class TestInferJsonSchemaAccessStrategy {
    private final String dateFormat = DATE.getDefaultFormat();

    private final String timeFormat = TIME.getDefaultFormat();

    private final String timestampFormat = "yyyy-MM-DD'T'HH:mm:ss.SSS'Z'";

    private final SchemaInferenceEngine<JsonNode> timestampInference = new JsonSchemaInference(new TimeValueInference("yyyy-MM-dd", "HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

    private final SchemaInferenceEngine<JsonNode> noTimestampInference = new JsonSchemaInference(new TimeValueInference("yyyy-MM-dd", "HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

    @Test
    public void testInferenceIncludesAllRecords() throws IOException {
        final File file = new File("src/test/resources/json/prov-events.json");
        final RecordSchema schema = inferSchema(file);
        final RecordField extraField1 = schema.getField("extra field 1").get();
        Assert.assertSame(STRING, extraField1.getDataType().getFieldType());
        final RecordField extraField2 = schema.getField("extra field 2").get();
        Assert.assertSame(STRING, extraField2.getDataType().getFieldType());
        final RecordField updatedAttributesField = schema.getField("updatedAttributes").get();
        final DataType updatedAttributesDataType = updatedAttributesField.getDataType();
        Assert.assertSame(RECORD, updatedAttributesDataType.getFieldType());
        final List<String> expectedAttributeNames = Arrays.asList("path", "filename", "drop reason", "uuid", "reporting.task.type", "s2s.address", "schema.cache.identifier", "reporting.task.uuid", "record.count", "s2s.host", "reporting.task.transaction.id", "reporting.task.name", "mime.type");
        final RecordSchema updatedAttributesSchema = getChildSchema();
        Assert.assertEquals(expectedAttributeNames.size(), updatedAttributesSchema.getFieldCount());
        for (final String attributeName : expectedAttributeNames) {
            Assert.assertSame(STRING, updatedAttributesSchema.getDataType(attributeName).get().getFieldType());
        }
    }

    @Test
    public void testDateAndTimestampsInferred() throws IOException {
        final File file = new File("src/test/resources/json/prov-events.json");
        final RecordSchema schema = inferSchema(file);
        final RecordField timestampField = schema.getField("timestamp").get();
        Assert.assertEquals(TIMESTAMP.getDataType("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), timestampField.getDataType());
        final RecordField dateField = schema.getField("eventDate").get();
        Assert.assertEquals(DATE.getDataType("yyyy-MM-dd"), dateField.getDataType());
        final RecordField timeField = schema.getField("eventTime").get();
        Assert.assertEquals(TIME.getDataType("HH:mm:ss"), timeField.getDataType());
        // TIME value and a STRING should be inferred as a STRING field
        final RecordField maybeTimeField = schema.getField("maybeTime").get();
        Assert.assertEquals(STRING, maybeTimeField.getDataType().getFieldType());
        // DATE value and a null value should be inferred as a DATE field
        final RecordField maybeDateField = schema.getField("maybeDate").get();
        Assert.assertEquals(DATE.getDataType("yyyy-MM-dd"), maybeDateField.getDataType());
    }

    /**
     * Test is intended to ensure that all inference rules that are explained in the readers' additionalDetails.html are correct
     */
    @Test
    public void testDocsExample() throws IOException {
        final File file = new File("src/test/resources/json/docs-example.json");
        final RecordSchema schema = inferSchema(file);
        Assert.assertSame(STRING, schema.getDataType("name").get().getFieldType());
        Assert.assertSame(STRING, schema.getDataType("age").get().getFieldType());
        final DataType valuesDataType = schema.getDataType("values").get();
        Assert.assertSame(CHOICE, valuesDataType.getFieldType());
        final ChoiceDataType valuesChoiceType = ((ChoiceDataType) (valuesDataType));
        final List<DataType> possibleTypes = valuesChoiceType.getPossibleSubTypes();
        Assert.assertEquals(2, possibleTypes.size());
        Assert.assertTrue(possibleTypes.contains(STRING.getDataType()));
        Assert.assertTrue(possibleTypes.contains(ARRAY.getArrayDataType(STRING.getDataType())));
        Assert.assertSame(STRING, schema.getDataType("nullValue").get().getFieldType());
    }
}

