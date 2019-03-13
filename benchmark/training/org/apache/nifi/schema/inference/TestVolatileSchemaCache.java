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
package org.apache.nifi.schema.inference;


import RecordFieldType.INT;
import RecordFieldType.STRING;
import VolatileSchemaCache.MAX_SIZE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;


public class TestVolatileSchemaCache {
    @Test
    public void testEqualSchemasSameIdentifier() throws InitializationException {
        final List<RecordField> fields = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            fields.add(new RecordField(String.valueOf(i), STRING.getDataType()));
        }
        final ConfigurationContext configContext = new org.apache.nifi.util.MockConfigurationContext(Collections.singletonMap(MAX_SIZE, "100"), null);
        final VolatileSchemaCache cache = new VolatileSchemaCache();
        cache.initialize(new org.apache.nifi.util.MockControllerServiceInitializationContext(cache, "id"));
        cache.setup(configContext);
        final String firstId = cache.cacheSchema(new org.apache.nifi.serialization.SimpleRecordSchema(fields));
        final String secondId = cache.cacheSchema(new org.apache.nifi.serialization.SimpleRecordSchema(fields));
        Assert.assertEquals(firstId, secondId);
    }

    @Test
    public void testDifferentSchemasDifferentIdentifier() throws InitializationException {
        final List<RecordField> stringFields = new ArrayList<>();
        final List<RecordField> intFields = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stringFields.add(new RecordField(String.valueOf(i), STRING.getDataType()));
            intFields.add(new RecordField(String.valueOf(i), INT.getDataType()));
        }
        final ConfigurationContext configContext = new org.apache.nifi.util.MockConfigurationContext(Collections.singletonMap(MAX_SIZE, "100"), null);
        final VolatileSchemaCache cache = new VolatileSchemaCache();
        cache.initialize(new org.apache.nifi.util.MockControllerServiceInitializationContext(cache, "id"));
        cache.setup(configContext);
        final String firstId = cache.cacheSchema(new org.apache.nifi.serialization.SimpleRecordSchema(stringFields));
        final String secondId = cache.cacheSchema(new org.apache.nifi.serialization.SimpleRecordSchema(intFields));
        Assert.assertNotEquals(firstId, secondId);
    }

    @Test
    public void testIdentifierCollission() throws InitializationException {
        final List<RecordField> stringFields = new ArrayList<>();
        final List<RecordField> intFields = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            stringFields.add(new RecordField(String.valueOf(i), STRING.getDataType()));
            intFields.add(new RecordField(String.valueOf(i), INT.getDataType()));
        }
        final ConfigurationContext configContext = new org.apache.nifi.util.MockConfigurationContext(Collections.singletonMap(MAX_SIZE, "100"), null);
        final VolatileSchemaCache cache = new VolatileSchemaCache() {
            @Override
            protected String createIdentifier(final RecordSchema schema) {
                return "identifier";
            }
        };
        cache.initialize(new org.apache.nifi.util.MockControllerServiceInitializationContext(cache, "id"));
        cache.setup(configContext);
        final String firstId = cache.cacheSchema(new org.apache.nifi.serialization.SimpleRecordSchema(stringFields));
        final String secondId = cache.cacheSchema(new org.apache.nifi.serialization.SimpleRecordSchema(intFields));
        Assert.assertNotEquals(firstId, secondId);
        Assert.assertEquals(new org.apache.nifi.serialization.SimpleRecordSchema(stringFields), cache.getSchema(firstId).get());
        Assert.assertEquals(new org.apache.nifi.serialization.SimpleRecordSchema(intFields), cache.getSchema(secondId).get());
    }
}

