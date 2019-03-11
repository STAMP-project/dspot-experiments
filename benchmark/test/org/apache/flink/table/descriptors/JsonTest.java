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
package org.apache.flink.table.descriptors;


import org.apache.flink.table.api.ValidationException;
import org.junit.Test;


/**
 * Tests for the {@link Json} descriptor.
 */
public class JsonTest extends DescriptorTestBase {
    private static final String JSON_SCHEMA = "{" + (((((((((((((((("    'title': 'Person'," + "    'type': 'object',") + "    'properties': {") + "        'firstName': {") + "            'type': 'string'") + "        },") + "        'lastName': {") + "            'type': 'string'") + "        },") + "        'age': {") + "            'description': 'Age in years',") + "            'type': 'integer',") + "            'minimum': 0") + "        }") + "    },") + "    'required': ['firstName', 'lastName']") + "}");

    @Test(expected = ValidationException.class)
    public void testInvalidMissingField() {
        addPropertyAndVerify(descriptors().get(0), "format.fail-on-missing-field", "DDD");
    }

    @Test(expected = ValidationException.class)
    public void testMissingSchema() {
        removePropertyAndVerify(descriptors().get(0), "format.json-schema");
    }

    @Test(expected = ValidationException.class)
    public void testDuplicateSchema() {
        // we add an additional non-json schema
        addPropertyAndVerify(descriptors().get(0), "format.schema", "DDD");
    }
}

