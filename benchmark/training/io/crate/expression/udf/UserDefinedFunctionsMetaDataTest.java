/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.expression.udf;


import DataTypes.BOOLEAN;
import DataTypes.DOUBLE;
import DataTypes.DOUBLE_ARRAY;
import DataTypes.FLOAT;
import DeprecationHandler.THROW_UNSUPPORTED_OPERATION;
import JsonXContent.jsonXContent;
import ToXContent.EMPTY_PARAMS;
import UserDefinedFunctionMetaData.DataTypeXContent;
import com.google.common.collect.ImmutableList;
import io.crate.analyze.FunctionArgumentDefinition;
import io.crate.test.integration.CrateUnitTest;
import io.crate.types.ArrayType;
import io.crate.types.DataTypes;
import java.io.IOException;
import java.util.List;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.hamcrest.core.Is;
import org.junit.Test;


public class UserDefinedFunctionsMetaDataTest extends CrateUnitTest {
    private static String definition = "function(a, b) {return a - b;}";

    private static List<FunctionArgumentDefinition> args = ImmutableList.of(FunctionArgumentDefinition.of(DOUBLE_ARRAY), FunctionArgumentDefinition.of("my_named_arg", DOUBLE));

    private static final UserDefinedFunctionMetaData FUNCTION_META_DATA = new UserDefinedFunctionMetaData("my_schema", "my_add", UserDefinedFunctionsMetaDataTest.args, DataTypes.FLOAT, "dummy_lang", UserDefinedFunctionsMetaDataTest.definition);

    public static final UserDefinedFunctionsMetaData DUMMY_UDF_META_DATA = UserDefinedFunctionsMetaData.of(UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA);

    @Test
    public void testUserDefinedFunctionStreaming() throws IOException {
        BytesStreamOutput out = new BytesStreamOutput();
        UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        UserDefinedFunctionMetaData udfMeta2 = UserDefinedFunctionMetaData.fromStream(in);
        assertThat(UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA, Is.is(udfMeta2));
        assertThat(udfMeta2.schema(), Is.is("my_schema"));
        assertThat(udfMeta2.name(), Is.is("my_add"));
        assertThat(udfMeta2.arguments().size(), Is.is(2));
        assertThat(udfMeta2.arguments().get(1), Is.is(FunctionArgumentDefinition.of("my_named_arg", DOUBLE)));
        assertThat(udfMeta2.argumentTypes().size(), Is.is(2));
        assertThat(udfMeta2.argumentTypes().get(1), Is.is(DOUBLE));
        assertThat(udfMeta2.returnType(), Is.is(FLOAT));
        assertThat(udfMeta2.language(), Is.is("dummy_lang"));
        assertThat(udfMeta2.definition(), Is.is(UserDefinedFunctionsMetaDataTest.definition));
    }

    @Test
    public void testUserDefinedFunctionToXContent() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // reflects the logic used to process custom metadata in the cluster state
        builder.startObject();
        UserDefinedFunctionsMetaDataTest.DUMMY_UDF_META_DATA.toXContent(builder, EMPTY_PARAMS);
        builder.endObject();
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, BytesReference.toBytes(BytesReference.bytes(builder)));
        parser.nextToken();// start object

        UserDefinedFunctionsMetaData functions = UserDefinedFunctionsMetaData.fromXContent(parser);
        assertEquals(UserDefinedFunctionsMetaDataTest.DUMMY_UDF_META_DATA, functions);
    }

    @Test
    public void testUserDefinedFunctionToXContentWithEmptyMetadata() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // reflects the logic used to process custom metadata in the cluster state
        builder.startObject();
        UserDefinedFunctionsMetaData functions = UserDefinedFunctionsMetaData.of();
        functions.toXContent(builder, EMPTY_PARAMS);
        builder.endObject();
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, BytesReference.toBytes(BytesReference.bytes(builder)));
        parser.nextToken();// enter START_OBJECT

        UserDefinedFunctionsMetaData functions2 = UserDefinedFunctionsMetaData.fromXContent(parser);
        assertEquals(functions, functions2);
    }

    @Test
    public void testDataTypeStreaming() throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        ArrayType type = new ArrayType(new ArrayType(DataTypes.STRING));
        DataTypeXContent.toXContent(type, builder, EMPTY_PARAMS);
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, BytesReference.toBytes(BytesReference.bytes(builder)));
        parser.nextToken();// enter START_OBJECT

        ArrayType type2 = ((ArrayType) (DataTypeXContent.fromXContent(parser)));
        assertTrue(type.equals(type2));
    }

    @Test
    public void testSameSignature() throws Exception {
        assertThat(UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA.sameSignature("my_schema", "my_add", UserDefinedFunctionMetaData.argumentTypesFrom(UserDefinedFunctionsMetaDataTest.args)), Is.is(true));
        assertThat(UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA.sameSignature("different_schema", "my_add", UserDefinedFunctionMetaData.argumentTypesFrom(UserDefinedFunctionsMetaDataTest.args)), Is.is(false));
        assertThat(UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA.sameSignature("my_schema", "different_name", UserDefinedFunctionMetaData.argumentTypesFrom(UserDefinedFunctionsMetaDataTest.args)), Is.is(false));
        assertThat(UserDefinedFunctionsMetaDataTest.FUNCTION_META_DATA.sameSignature("my_schema", "my_add", ImmutableList.of()), Is.is(false));
    }

    @Test
    public void testSpecificName() throws Exception {
        assertThat(UserDefinedFunctionMetaData.specificName("my_func", ImmutableList.of()), Is.is("my_func()"));
        assertThat(UserDefinedFunctionMetaData.specificName("my_func", ImmutableList.of(BOOLEAN, new ArrayType(DataTypes.BOOLEAN))), Is.is("my_func(boolean, boolean_array)"));
    }
}

