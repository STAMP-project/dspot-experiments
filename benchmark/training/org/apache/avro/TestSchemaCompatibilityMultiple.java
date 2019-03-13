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


import SchemaIncompatibilityType.FIXED_SIZE_MISMATCH;
import SchemaIncompatibilityType.MISSING_ENUM_SYMBOLS;
import SchemaIncompatibilityType.MISSING_UNION_BRANCH;
import SchemaIncompatibilityType.NAME_MISMATCH;
import SchemaIncompatibilityType.READER_FIELD_MISSING_DEFAULT_VALUE;
import SchemaIncompatibilityType.TYPE_MISMATCH;
import java.util.Arrays;
import java.util.List;
import org.apache.avro.SchemaCompatibility.SchemaIncompatibilityType;
import org.junit.Test;


public class TestSchemaCompatibilityMultiple {
    @Test
    public void testMultipleIncompatibilities() throws Exception {
        Schema reader = // EOR
        // EOR
        // EOR
        // 3.5.1
        // 3.5.0
        // 3.5
        // 3.4
        // 3.3
        // 3.2
        // 3.1
        // 3.0
        // 3
        // 2
        // 1
        // 0
        SchemaBuilder.record("base").fields().name("check_enum_symbols_field").type().enumeration("check_enum_symbols_type").symbols("A", "C").noDefault().name("check_enum_name_field").type().enumeration("check_enum_name_type").symbols("A", "B", "C", "D").noDefault().name("type_mismatch_field").type().stringType().noDefault().name("sub_record").type().record("sub_record_type").fields().name("identical_1_field").type().longType().longDefault(42L).name("extra_no_default_field").type().longType().noDefault().name("fixed_length_mismatch_field").type().fixed("fixed_length_mismatch_type").size(4).noDefault().name("union_missing_branches_field").type().unionOf().booleanType().endUnion().noDefault().name("reader_union_does_not_support_type_field").type().unionOf().booleanType().endUnion().noDefault().name("record_fqn_mismatch_field").type().record("recordA").namespace("not_nsA").fields().name("A_field_0").type().booleanType().booleanDefault(true).name("array_type_mismatch_field").type().array().items().stringType().noDefault().endRecord().noDefault().endRecord().noDefault().endRecord();
        Schema writer = // EOR
        // EOR
        // EOR
        // 3.5.1
        // 3.5.0
        // 3.5
        // 3.4
        // 3.3
        // 3.1
        // MISSING FIELD
        // 3.2
        // 3.0
        // 3
        // 2
        // 1
        // 0
        SchemaBuilder.record("base").fields().name("check_enum_symbols_field").type().enumeration("check_enum_symbols_type").symbols("A", "B", "C", "D").noDefault().name("check_enum_name_field").type().enumeration("check_enum_name_type_ERR").symbols("A", "B", "C", "D").noDefault().name("type_mismatch_field").type().longType().noDefault().name("sub_record").type().record("sub_record_type").fields().name("identical_1_field").type().longType().longDefault(42L).name("fixed_length_mismatch_field").type().fixed("fixed_length_mismatch_type").size(8).noDefault().name("union_missing_branches_field").type().unionOf().booleanType().and().doubleType().and().stringType().endUnion().noDefault().name("reader_union_does_not_support_type_field").type().longType().noDefault().name("record_fqn_mismatch_field").type().record("recordA").namespace("nsA").fields().name("A_field_0").type().booleanType().booleanDefault(true).name("array_type_mismatch_field").type().array().items().booleanType().noDefault().endRecord().noDefault().endRecord().noDefault().endRecord();
        List<SchemaIncompatibilityType> types = Arrays.asList(MISSING_ENUM_SYMBOLS, NAME_MISMATCH, TYPE_MISMATCH, READER_FIELD_MISSING_DEFAULT_VALUE, FIXED_SIZE_MISMATCH, MISSING_UNION_BRANCH, MISSING_UNION_BRANCH, MISSING_UNION_BRANCH, NAME_MISMATCH, TYPE_MISMATCH);
        List<String> details = Arrays.asList("[B, D]", "expected: check_enum_name_type_ERR", "reader type: STRING not compatible with writer type: LONG", "extra_no_default_field", "expected: 8, found: 4", "reader union lacking writer type: DOUBLE", "reader union lacking writer type: STRING", "reader union lacking writer type: LONG", "expected: nsA.recordA", "reader type: STRING not compatible with writer type: BOOLEAN");
        List<String> location = Arrays.asList("/fields/0/type/symbols", "/fields/1/type/name", "/fields/2/type", "/fields/3/type/fields/1", "/fields/3/type/fields/2/type/size", "/fields/3/type/fields/3/type/1", "/fields/3/type/fields/3/type/2", "/fields/3/type/fields/4/type", "/fields/3/type/fields/5/type/name", "/fields/3/type/fields/5/type/fields/1/type/items");
        TestSchemaCompatibility.validateIncompatibleSchemas(reader, writer, types, details, location);
    }
}

