/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.metadata.doc;


import com.google.common.collect.ImmutableList;
import io.crate.expression.udf.UserDefinedFunctionMetaData;
import io.crate.expression.udf.UserDefinedFunctionService;
import io.crate.expression.udf.UserDefinedFunctionsMetaData;
import io.crate.metadata.Functions;
import io.crate.metadata.SearchPath;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.types.DataTypes;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.index.Index;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DocSchemaInfoTest extends CrateDummyClusterServiceUnitTest {
    private DocSchemaInfo docSchemaInfo;

    private Functions functions;

    private UserDefinedFunctionService udfService;

    @Test
    public void testInvalidFunction() throws Exception {
        UserDefinedFunctionMetaData invalid = new UserDefinedFunctionMetaData("my_schema", "invalid", ImmutableList.of(), DataTypes.INTEGER, "burlesque", "this is not valid burlesque code");
        UserDefinedFunctionMetaData valid = new UserDefinedFunctionMetaData("my_schema", "valid", ImmutableList.of(), DataTypes.INTEGER, "burlesque", "\"Hello, World!\"Q");
        UserDefinedFunctionsMetaData metaData = UserDefinedFunctionsMetaData.of(invalid, valid);
        // if a functionImpl can't be created, it won't be registered
        udfService.updateImplementations("my_schema", metaData.functionsMetaData().stream());
        assertThat(functions.get("my_schema", "valid", ImmutableList.of(), SearchPath.pathWithPGCatalogAndDoc()), Matchers.notNullValue());
        expectedException.expectMessage("unknown function: my_schema.invalid()");
        functions.get("my_schema", "invalid", ImmutableList.of(), SearchPath.pathWithPGCatalogAndDoc());
    }

    @Test
    public void testNoNPEIfDeletedIndicesNotInPreviousClusterState() throws Exception {
        // sometimes on startup it occurs that a ClusterChangedEvent contains deleted indices
        // which are not in the previousState.
        MetaData metaData = new MetaData.Builder().build();
        docSchemaInfo.invalidateFromIndex(new Index("my_index", "asdf"), metaData);
    }
}

