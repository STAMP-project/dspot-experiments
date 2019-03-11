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
package io.crate.metadata;


import DocSysColumns.DOC;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ColumnIdentTest {
    @Test
    public void testSqlFqn() throws Exception {
        ColumnIdent ident = new ColumnIdent("foo", Arrays.asList("x", "y", "z"));
        MatcherAssert.assertThat(ident.sqlFqn(), Matchers.is("foo['x']['y']['z']"));
        ident = new ColumnIdent("a");
        MatcherAssert.assertThat(ident.sqlFqn(), Matchers.is("a"));
        ident = new ColumnIdent("a", Collections.singletonList(""));
        MatcherAssert.assertThat(ident.sqlFqn(), Matchers.is("a['']"));
        ident = new ColumnIdent("a.b", Collections.singletonList("c"));
        MatcherAssert.assertThat(ident.sqlFqn(), Matchers.is("a.b['c']"));
    }

    @Test
    public void testShiftRight() throws Exception {
        MatcherAssert.assertThat(new ColumnIdent("foo", "bar").shiftRight(), Matchers.is(new ColumnIdent("bar")));
        MatcherAssert.assertThat(new ColumnIdent("foo", Arrays.asList("x", "y", "z")).shiftRight(), Matchers.is(new ColumnIdent("x", Arrays.asList("y", "z"))));
        MatcherAssert.assertThat(new ColumnIdent("foo").shiftRight(), Matchers.nullValue());
    }

    @Test
    public void testIsChildOf() throws Exception {
        ColumnIdent root = new ColumnIdent("root");
        ColumnIdent rootX = new ColumnIdent("root", "x");
        ColumnIdent rootXY = new ColumnIdent("root", Arrays.asList("x", "y"));
        ColumnIdent rootYX = new ColumnIdent("root", Arrays.asList("y", "x"));
        MatcherAssert.assertThat(root.isChildOf(root), Matchers.is(false));
        MatcherAssert.assertThat(rootX.isChildOf(root), Matchers.is(true));
        MatcherAssert.assertThat(rootXY.isChildOf(root), Matchers.is(true));
        MatcherAssert.assertThat(rootXY.isChildOf(rootX), Matchers.is(true));
        MatcherAssert.assertThat(rootYX.isChildOf(root), Matchers.is(true));
        MatcherAssert.assertThat(rootYX.isChildOf(rootX), Matchers.is(false));
    }

    @Test
    public void testPrepend() throws Exception {
        ColumnIdent foo = new ColumnIdent("foo");
        MatcherAssert.assertThat(foo.prepend(DOC.name()), Matchers.is(new ColumnIdent(DOC.name(), "foo")));
        ColumnIdent fooBar = new ColumnIdent("foo", "bar");
        MatcherAssert.assertThat(fooBar.prepend("x"), Matchers.is(new ColumnIdent("x", Arrays.asList("foo", "bar"))));
    }

    @Test
    public void testValidColumnNameValidation() throws Exception {
        // Allowed.
        ColumnIdent.validateColumnName("valid");
        ColumnIdent.validateColumnName("field_name_");
        ColumnIdent.validateColumnName("_Name");
        ColumnIdent.validateColumnName("_name_");
        ColumnIdent.validateColumnName("__name");
        ColumnIdent.validateColumnName("_name1");
        ColumnIdent.validateColumnName("['index']");
        ColumnIdent.validateColumnName("[0]");
        ColumnIdent.validateColumnName("[]i");
        ColumnIdent.validateColumnName("ident['index");
        ColumnIdent.validateColumnName("i][[");
        ColumnIdent.validateColumnName("1'");
    }

    @Test
    public void testIllegalColumnNameValidation() throws Exception {
        assertExceptionIsThrownOnValidation(".name", "contains a dot");
        assertExceptionIsThrownOnValidation("column.name", "contains a dot");
        assertExceptionIsThrownOnValidation(".", "contains a dot");
        assertExceptionIsThrownOnValidation("_a", "system column");
        assertExceptionIsThrownOnValidation("_name", "system column");
        assertExceptionIsThrownOnValidation("_field_name", "system column");
        assertExceptionIsThrownOnValidation("ident['index']", "subscript");
        assertExceptionIsThrownOnValidation("ident['index]", "subscript");
        assertExceptionIsThrownOnValidation("ident[0]", "subscript");
    }
}

