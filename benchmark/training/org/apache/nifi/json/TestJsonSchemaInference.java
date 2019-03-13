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


import RecordFieldType.BIGINT;
import RecordFieldType.DATE;
import RecordFieldType.DOUBLE;
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
import java.util.List;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.schema.inference.InferSchemaAccessStrategy;
import org.apache.nifi.schema.inference.TimeValueInference;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJsonSchemaInference {
    private final TimeValueInference timestampInference = new TimeValueInference("yyyy-MM-dd", "HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Test
    public void testInferenceIncludesAllRecords() throws IOException {
        final File file = new File("src/test/resources/json/data-types.json");
        final RecordSchema schema;
        try (final InputStream in = new FileInputStream(file);final InputStream bufferedIn = new BufferedInputStream(in)) {
            final InferSchemaAccessStrategy<?> accessStrategy = new InferSchemaAccessStrategy(( var, content) -> new JsonRecordSource(content), new JsonSchemaInference(timestampInference), Mockito.mock(ComponentLog.class));
            schema = accessStrategy.getSchema(null, bufferedIn, null);
        }
        Assert.assertSame(STRING, schema.getDataType("varcharc").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("uuid").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("tinyintc").get().getFieldType());
        Assert.assertSame(STRING, schema.getDataType("textc").get().getFieldType());
        Assert.assertEquals(DATE.getDataType("yyyy-MM-dd"), schema.getDataType("datec").get());
        Assert.assertSame(LONG, schema.getDataType("smallintc").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("mediumintc").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("intc").get().getFieldType());
        Assert.assertSame(BIGINT, schema.getDataType("bigintc").get().getFieldType());
        Assert.assertSame(DOUBLE, schema.getDataType("floatc").get().getFieldType());
        Assert.assertSame(DOUBLE, schema.getDataType("doublec").get().getFieldType());
        Assert.assertSame(DOUBLE, schema.getDataType("decimalc").get().getFieldType());
        Assert.assertEquals(TIMESTAMP.getDataType("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), schema.getDataType("timestampc").get());
        Assert.assertEquals(TIME.getDataType("HH:mm:ss"), schema.getDataType("timec").get());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("charc").get());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("tinytextc").get());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("blobc").get());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("mediumtextc").get());
        Assert.assertSame(LONG, schema.getDataType("enumc").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("setc").get().getFieldType());
        Assert.assertSame(LONG, schema.getDataType("boolc").get().getFieldType());
        Assert.assertEquals(STRING.getDataType(), schema.getDataType("binaryc").get());
        final List<String> fieldNames = schema.getFieldNames();
        Assert.assertEquals(Arrays.asList("varcharc", "uuid", "tinyintc", "textc", "datec", "smallintc", "mediumintc", "intc", "bigintc", "floatc", "doublec", "decimalc", "timestampc", "timec", "charc", "tinytextc", "blobc", "mediumtextc", "enumc", "setc", "boolc", "binaryc"), fieldNames);
    }
}

