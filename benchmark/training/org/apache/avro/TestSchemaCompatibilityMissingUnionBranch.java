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


import SchemaIncompatibilityType.MISSING_UNION_BRANCH;
import java.util.Collections;
import java.util.List;
import org.apache.avro.SchemaCompatibility.SchemaIncompatibilityType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestSchemaCompatibilityMissingUnionBranch {
    private static final Schema RECORD1_WITH_INT = // 
    // 
    SchemaBuilder.record("Record1").fields().name("field1").type(TestSchemas.INT_SCHEMA).noDefault().endRecord();

    private static final Schema RECORD2_WITH_INT = // 
    // 
    SchemaBuilder.record("Record2").fields().name("field1").type(TestSchemas.INT_SCHEMA).noDefault().endRecord();

    private static final Schema UNION_INT_RECORD1 = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemaCompatibilityMissingUnionBranch.RECORD1_WITH_INT));

    private static final Schema UNION_INT_RECORD2 = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemaCompatibilityMissingUnionBranch.RECORD2_WITH_INT));

    private static final Schema UNION_INT_ENUM1_AB = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.ENUM1_AB_SCHEMA));

    private static final Schema UNION_INT_FIXED_4_BYTES = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.FIXED_4_BYTES));

    private static final Schema UNION_INT_BOOLEAN = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.BOOLEAN_SCHEMA));

    private static final Schema UNION_INT_ARRAY_INT = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.INT_ARRAY_SCHEMA));

    private static final Schema UNION_INT_MAP_INT = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.INT_MAP_SCHEMA));

    private static final Schema UNION_INT_NULL = Schema.createUnion(TestSchemas.list(TestSchemas.INT_SCHEMA, TestSchemas.NULL_SCHEMA));

    @Parameterized.Parameter(0)
    public Schema reader;

    @Parameterized.Parameter(1)
    public Schema writer;

    @Parameterized.Parameter(2)
    public List<String> details;

    @Parameterized.Parameter(3)
    public List<String> location;

    @Test
    public void testMissingUnionBranch() throws Exception {
        List<SchemaIncompatibilityType> types = Collections.nCopies(details.size(), MISSING_UNION_BRANCH);
        TestSchemaCompatibility.validateIncompatibleSchemas(reader, writer, types, details, location);
    }
}

