/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import TableDefinition.Type.VIEW;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ViewDefinitionTest {
    private static final String VIEW_QUERY = "VIEW QUERY";

    private static final List<UserDefinedFunction> USER_DEFINED_FUNCTIONS = ImmutableList.of(UserDefinedFunction.inline("Function"), UserDefinedFunction.fromUri("URI"));

    private static final ViewDefinition VIEW_DEFINITION = ViewDefinition.newBuilder(ViewDefinitionTest.VIEW_QUERY, ViewDefinitionTest.USER_DEFINED_FUNCTIONS).build();

    @Test
    public void testToBuilder() {
        compareViewDefinition(ViewDefinitionTest.VIEW_DEFINITION, ViewDefinitionTest.VIEW_DEFINITION.toBuilder().build());
        ViewDefinition viewDefinition = ViewDefinitionTest.VIEW_DEFINITION.toBuilder().setQuery("NEW QUERY").build();
        Assert.assertEquals("NEW QUERY", viewDefinition.getQuery());
        viewDefinition = viewDefinition.toBuilder().setQuery(ViewDefinitionTest.VIEW_QUERY).build();
        compareViewDefinition(ViewDefinitionTest.VIEW_DEFINITION, viewDefinition);
        viewDefinition = viewDefinition.toBuilder().setUseLegacySql(true).build();
        Assert.assertTrue(viewDefinition.useLegacySql());
    }

    @Test
    public void testToBuilderIncomplete() {
        TableDefinition viewDefinition = ViewDefinition.of(ViewDefinitionTest.VIEW_QUERY);
        Assert.assertEquals(viewDefinition, viewDefinition.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(ViewDefinitionTest.VIEW_QUERY, ViewDefinitionTest.VIEW_DEFINITION.getQuery());
        Assert.assertEquals(VIEW, ViewDefinitionTest.VIEW_DEFINITION.getType());
        Assert.assertEquals(ViewDefinitionTest.USER_DEFINED_FUNCTIONS, ViewDefinitionTest.VIEW_DEFINITION.getUserDefinedFunctions());
        ViewDefinition viewDefinition = ViewDefinition.newBuilder(ViewDefinitionTest.VIEW_QUERY).setUserDefinedFunctions(UserDefinedFunction.inline("Function"), UserDefinedFunction.fromUri("URI")).build();
        Assert.assertEquals(ViewDefinitionTest.VIEW_QUERY, viewDefinition.getQuery());
        Assert.assertEquals(VIEW, viewDefinition.getType());
        Assert.assertEquals(ViewDefinitionTest.USER_DEFINED_FUNCTIONS, viewDefinition.getUserDefinedFunctions());
        Assert.assertFalse(viewDefinition.useLegacySql());
        viewDefinition = ViewDefinition.newBuilder(ViewDefinitionTest.VIEW_QUERY, UserDefinedFunction.inline("Function"), UserDefinedFunction.fromUri("URI")).build();
        Assert.assertEquals(ViewDefinitionTest.VIEW_QUERY, viewDefinition.getQuery());
        Assert.assertEquals(VIEW, viewDefinition.getType());
        Assert.assertEquals(ViewDefinitionTest.USER_DEFINED_FUNCTIONS, viewDefinition.getUserDefinedFunctions());
        Assert.assertFalse(viewDefinition.useLegacySql());
        viewDefinition = ViewDefinition.newBuilder(ViewDefinitionTest.VIEW_QUERY).build();
        Assert.assertEquals(ViewDefinitionTest.VIEW_QUERY, viewDefinition.getQuery());
        Assert.assertEquals(VIEW, viewDefinition.getType());
        Assert.assertNull(viewDefinition.getUserDefinedFunctions());
        Assert.assertFalse(viewDefinition.useLegacySql());
        viewDefinition = ViewDefinition.newBuilder(ViewDefinitionTest.VIEW_QUERY).setUseLegacySql(true).build();
        Assert.assertEquals(ViewDefinitionTest.VIEW_QUERY, viewDefinition.getQuery());
        Assert.assertEquals(VIEW, viewDefinition.getType());
        Assert.assertNull(viewDefinition.getUserDefinedFunctions());
        Assert.assertTrue(viewDefinition.useLegacySql());
    }

    @Test
    public void testToAndFromPb() {
        ViewDefinition viewDefinition = ViewDefinitionTest.VIEW_DEFINITION.toBuilder().setUseLegacySql(false).build();
        Assert.assertTrue(((TableDefinition.fromPb(viewDefinition.toPb())) instanceof ViewDefinition));
        compareViewDefinition(viewDefinition, TableDefinition.<ViewDefinition>fromPb(viewDefinition.toPb()));
    }
}

