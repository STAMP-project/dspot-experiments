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
package org.apache.nifi.schema.access;


import java.io.IOException;
import java.util.Collections;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.SchemaIdentifier;
import org.apache.nifi.util.MockPropertyValue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestSchemaNamePropertyStrategy extends AbstractSchemaAccessStrategyTest {
    @Test
    public void testNameOnly() throws IOException, SchemaNotFoundException {
        final PropertyValue nameValue = new MockPropertyValue("person");
        final PropertyValue branchValue = new MockPropertyValue(null);
        final PropertyValue versionValue = new MockPropertyValue(null);
        final SchemaNamePropertyStrategy schemaNamePropertyStrategy = new SchemaNamePropertyStrategy(schemaRegistry, nameValue, branchValue, versionValue);
        final SchemaIdentifier expectedSchemaIdentifier = SchemaIdentifier.builder().name(nameValue.getValue()).build();
        Mockito.when(schemaRegistry.retrieveSchema(ArgumentMatchers.argThat(new SchemaIdentifierMatcher(expectedSchemaIdentifier)))).thenReturn(recordSchema);
        final RecordSchema retrievedSchema = schemaNamePropertyStrategy.getSchema(Collections.emptyMap(), null, recordSchema);
        Assert.assertNotNull(retrievedSchema);
    }

    @Test
    public void testNameAndVersion() throws IOException, SchemaNotFoundException {
        final PropertyValue nameValue = new MockPropertyValue("person");
        final PropertyValue branchValue = new MockPropertyValue(null);
        final PropertyValue versionValue = new MockPropertyValue("1");
        final SchemaNamePropertyStrategy schemaNamePropertyStrategy = new SchemaNamePropertyStrategy(schemaRegistry, nameValue, branchValue, versionValue);
        final SchemaIdentifier expectedSchemaIdentifier = SchemaIdentifier.builder().name(nameValue.getValue()).version(versionValue.asInteger()).build();
        Mockito.when(schemaRegistry.retrieveSchema(ArgumentMatchers.argThat(new SchemaIdentifierMatcher(expectedSchemaIdentifier)))).thenReturn(recordSchema);
        final RecordSchema retrievedSchema = schemaNamePropertyStrategy.getSchema(Collections.emptyMap(), null, recordSchema);
        Assert.assertNotNull(retrievedSchema);
    }

    @Test
    public void testNameAndBlankVersion() throws IOException, SchemaNotFoundException {
        final PropertyValue nameValue = new MockPropertyValue("person");
        final PropertyValue branchValue = new MockPropertyValue(null);
        final PropertyValue versionValue = new MockPropertyValue("   ");
        final SchemaNamePropertyStrategy schemaNamePropertyStrategy = new SchemaNamePropertyStrategy(schemaRegistry, nameValue, branchValue, versionValue);
        final SchemaIdentifier expectedSchemaIdentifier = SchemaIdentifier.builder().name(nameValue.getValue()).build();
        Mockito.when(schemaRegistry.retrieveSchema(ArgumentMatchers.argThat(new SchemaIdentifierMatcher(expectedSchemaIdentifier)))).thenReturn(recordSchema);
        final RecordSchema retrievedSchema = schemaNamePropertyStrategy.getSchema(Collections.emptyMap(), null, recordSchema);
        Assert.assertNotNull(retrievedSchema);
    }

    @Test(expected = SchemaNotFoundException.class)
    public void testNameAndNonNumericVersion() throws IOException, SchemaNotFoundException {
        final PropertyValue nameValue = new MockPropertyValue("person");
        final PropertyValue branchValue = new MockPropertyValue(null);
        final PropertyValue versionValue = new MockPropertyValue("XYZ");
        final SchemaNamePropertyStrategy schemaNamePropertyStrategy = new SchemaNamePropertyStrategy(schemaRegistry, nameValue, branchValue, versionValue);
        schemaNamePropertyStrategy.getSchema(Collections.emptyMap(), null, recordSchema);
    }

    @Test
    public void testNameAndBranch() throws IOException, SchemaNotFoundException {
        final PropertyValue nameValue = new MockPropertyValue("person");
        final PropertyValue branchValue = new MockPropertyValue("test");
        final PropertyValue versionValue = new MockPropertyValue(null);
        final SchemaNamePropertyStrategy schemaNamePropertyStrategy = new SchemaNamePropertyStrategy(schemaRegistry, nameValue, branchValue, versionValue);
        final SchemaIdentifier expectedSchemaIdentifier = SchemaIdentifier.builder().name(nameValue.getValue()).branch(branchValue.getValue()).build();
        Mockito.when(schemaRegistry.retrieveSchema(ArgumentMatchers.argThat(new SchemaIdentifierMatcher(expectedSchemaIdentifier)))).thenReturn(recordSchema);
        final RecordSchema retrievedSchema = schemaNamePropertyStrategy.getSchema(Collections.emptyMap(), null, recordSchema);
        Assert.assertNotNull(retrievedSchema);
    }

    @Test
    public void testNameAndBlankBranch() throws IOException, SchemaNotFoundException {
        final PropertyValue nameValue = new MockPropertyValue("person");
        final PropertyValue branchValue = new MockPropertyValue("  ");
        final PropertyValue versionValue = new MockPropertyValue(null);
        final SchemaNamePropertyStrategy schemaNamePropertyStrategy = new SchemaNamePropertyStrategy(schemaRegistry, nameValue, branchValue, versionValue);
        final SchemaIdentifier expectedSchemaIdentifier = SchemaIdentifier.builder().name(nameValue.getValue()).build();
        Mockito.when(schemaRegistry.retrieveSchema(ArgumentMatchers.argThat(new SchemaIdentifierMatcher(expectedSchemaIdentifier)))).thenReturn(recordSchema);
        final RecordSchema retrievedSchema = schemaNamePropertyStrategy.getSchema(Collections.emptyMap(), null, recordSchema);
        Assert.assertNotNull(retrievedSchema);
    }
}

