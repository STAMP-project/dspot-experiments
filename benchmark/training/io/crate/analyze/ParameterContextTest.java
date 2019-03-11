/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
package io.crate.analyze;


import DataTypes.UNDEFINED;
import Row.EMPTY;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import io.crate.data.Row;
import io.crate.data.Rows;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SymbolMatchers;
import io.crate.types.ArrayType;
import io.crate.types.DataTypes;
import io.crate.types.ObjectType;
import java.util.Collections;
import java.util.HashMap;
import org.apache.lucene.util.BytesRef;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ParameterContextTest extends CrateUnitTest {
    @Test
    public void testEmpty() throws Exception {
        ParameterContext ctx = new ParameterContext(Row.EMPTY, Collections.<Row>emptyList());
        assertFalse(ctx.hasBulkParams());
        assertThat(ctx.parameters(), Matchers.is(EMPTY));
    }

    @Test
    public void testArgs() throws Exception {
        Row args = new io.crate.data.RowN(RandomizedTest.$(true, 1, null, "string"));
        ParameterContext ctx = new ParameterContext(args, Collections.<Row>emptyList());
        assertFalse(ctx.hasBulkParams());
        assertThat(ctx.parameters(), Matchers.is(args));
    }

    @Test
    public void testBulkArgs() throws Exception {
        Object[][] bulkArgs = new Object[][]{ new Object[]{ true, 1, "foo", null, new String[]{ null } }, new Object[]{ false, 2, "bar", new Object[0], new String[]{ "foo", "bar" } } };
        ParameterContext ctx = new ParameterContext(Row.EMPTY, Rows.of(bulkArgs));
        assertTrue(ctx.hasBulkParams());
        ctx.setBulkIdx(0);
        assertThat(ctx.getAsSymbol(0), SymbolMatchers.isLiteral(true));
        assertThat(ctx.getAsSymbol(1), SymbolMatchers.isLiteral(1));
        assertThat(ctx.getAsSymbol(2), SymbolMatchers.isLiteral("foo"));
        assertThat(ctx.getAsSymbol(3), SymbolMatchers.isLiteral(null, UNDEFINED));
        assertThat(ctx.getAsSymbol(4), SymbolMatchers.isLiteral(new BytesRef[]{ null }, new ArrayType(DataTypes.UNDEFINED)));
        ctx.setBulkIdx(1);
        assertThat(ctx.getAsSymbol(0), SymbolMatchers.isLiteral(false));
        assertThat(ctx.getAsSymbol(1), SymbolMatchers.isLiteral(2));
        assertThat(ctx.getAsSymbol(2), SymbolMatchers.isLiteral("bar"));
        assertThat(ctx.getAsSymbol(3), SymbolMatchers.isLiteral(new Object[0], new ArrayType(DataTypes.UNDEFINED)));
        assertThat(ctx.getAsSymbol(4), SymbolMatchers.isLiteral(new String[]{ "foo", "bar" }, new ArrayType(DataTypes.STRING)));
    }

    @Test
    public void testBulkArgsMixedNumberOfArguments() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("mixed number of arguments inside bulk arguments");
        Object[][] bulkArgs = new Object[][]{ new Object[]{ "foo" }, new Object[]{ false, 1 } };
        new ParameterContext(Row.EMPTY, Rows.of(bulkArgs));
    }

    @Test
    public void testBulkArgsNested() throws Exception {
        HashMap<String, Object[]> obj1 = new HashMap<>();
        obj1.put("a", new String[]{ null });
        obj1.put("b", new Integer[]{ 1 });
        HashMap<String, Object[]> obj2 = new HashMap<>();
        obj2.put("a", new String[]{ "foo" });
        obj2.put("b", new Float[]{ 0.5F });
        Object[][] bulkArgs = new Object[][]{ new Object[]{ obj1 }, new Object[]{ obj2 } };
        ParameterContext ctx = new ParameterContext(Row.EMPTY, Rows.of(bulkArgs));
        ctx.setBulkIdx(0);
        assertThat(ctx.getAsSymbol(0), SymbolMatchers.isLiteral(obj1, ObjectType.untyped()));
        ctx.setBulkIdx(1);
        assertThat(ctx.getAsSymbol(0), SymbolMatchers.isLiteral(obj2, ObjectType.untyped()));
    }

    @Test
    public void testBulkNestedNested() throws Exception {
        Object[][] bulkArgs = new Object[][]{ new Object[]{ new String[][]{ new String[]{ null } } }, new Object[]{ new String[][]{ new String[]{ "foo" } } } };
        ParameterContext ctx = new ParameterContext(Row.EMPTY, Rows.of(bulkArgs));
        assertThat(ctx.getAsSymbol(0), SymbolMatchers.isLiteral(bulkArgs[0][0], new ArrayType(new ArrayType(DataTypes.UNDEFINED))));
    }

    @Test
    public void testBulkNestedNestedEmpty() throws Exception {
        Object[][] bulkArgs = new Object[][]{ new Object[]{ new String[][]{ new String[0] } }, new Object[]{ new String[][]{ new String[0] } } };
        ParameterContext ctx = new ParameterContext(Row.EMPTY, Rows.of(bulkArgs));
        assertThat(ctx.getAsSymbol(0), SymbolMatchers.isLiteral(bulkArgs[0][0], new ArrayType(new ArrayType(DataTypes.UNDEFINED))));
    }
}

