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
package org.apache.avro;


import SchemaIncompatibilityType.NAME_MISMATCH;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestSchemaCompatibilityNameMismatch {
    private static final Schema FIXED_4_ANOTHER_NAME = Schema.createFixed("AnotherName", null, null, 4);

    private static final Schema FIXED_4_NAMESPACE_V1 = Schema.createFixed("Fixed", null, "org.apache.avro.tests.v_1_0", 4);

    private static final Schema FIXED_4_NAMESPACE_V2 = Schema.createFixed("Fixed", null, "org.apache.avro.tests.v_2_0", 4);

    @Parameterized.Parameter(0)
    public Schema reader;

    @Parameterized.Parameter(1)
    public Schema writer;

    @Parameterized.Parameter(2)
    public String details;

    @Parameterized.Parameter(3)
    public String location;

    @Test
    public void testNameMismatchSchemas() throws Exception {
        TestSchemaCompatibility.validateIncompatibleSchemas(reader, writer, NAME_MISMATCH, details, location);
    }
}

