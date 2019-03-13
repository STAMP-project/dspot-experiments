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
package org.apache.nifi.processors.kite;


import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kitesdk.data.DatasetDescriptor;
import org.kitesdk.data.spi.DefaultConfiguration;


public class TestGetSchema {
    public static final Schema SCHEMA = SchemaBuilder.record("Test").fields().requiredLong("id").requiredString("color").optionalDouble("price").endRecord();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testSchemaFromResourceURI() throws IOException {
        DatasetDescriptor descriptor = // in kite-data-core test-jar
        new DatasetDescriptor.Builder().schemaUri("resource:schema/user.avsc").build();
        Schema expected = descriptor.getSchema();
        Schema schema = AbstractKiteProcessor.getSchema("resource:schema/user.avsc", DefaultConfiguration.get());
        Assert.assertEquals("Schema from resource URI should match", expected, schema);
    }
}

