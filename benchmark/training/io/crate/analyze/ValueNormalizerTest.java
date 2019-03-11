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
package io.crate.analyze;


import DataTypes.DOUBLE;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.exceptions.ColumnValidationException;
import io.crate.exceptions.InvalidColumnNameException;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.Reference;
import io.crate.metadata.RelationName;
import io.crate.metadata.RowGranularity;
import io.crate.metadata.Schemas;
import io.crate.metadata.table.TableInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SymbolMatchers;
import io.crate.types.DataTypes;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class ValueNormalizerTest extends CrateDummyClusterServiceUnitTest {
    private static final RelationName TEST_TABLE_IDENT = new RelationName(Schemas.DOC_SCHEMA_NAME, "test1");

    private TableInfo userTableInfo;

    @Test
    public void testNormalizePrimitiveLiteral() throws Exception {
        Reference ref = new Reference(new io.crate.metadata.ReferenceIdent(ValueNormalizerTest.TEST_TABLE_IDENT, new ColumnIdent("bool")), RowGranularity.DOC, DataTypes.BOOLEAN);
        Literal<Boolean> trueLiteral = Literal.of(true);
        assertThat(normalizeInputForReference(trueLiteral, ref), Matchers.<Symbol>is(trueLiteral));
        assertThat(normalizeInputForReference(Literal.of("true"), ref), Matchers.<Symbol>is(trueLiteral));
        assertThat(normalizeInputForReference(Literal.of("false"), ref), Matchers.<Symbol>is(Literal.of(false)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNormalizeDynamicEmptyObjectLiteral() throws Exception {
        Reference objRef = userTableInfo.getReference(new ColumnIdent("dyn_empty"));
        Map<String, Object> map = new HashMap<>();
        map.put("time", "2014-02-16T00:00:01");
        map.put("false", true);
        Literal<Map<String, Object>> normalized = ((Literal) (normalizeInputForReference(Literal.of(map), objRef)));
        assertThat(((String) (normalized.value().get("time"))), Matchers.is("2014-02-16T00:00:01"));
        assertThat(((Boolean) (normalized.value().get("false"))), Matchers.is(true));
    }

    @Test(expected = ColumnValidationException.class)
    public void testNormalizeObjectLiteralInvalidNested() throws Exception {
        Reference objRef = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<>();
        map.put("d", "2014-02-16T00:00:01");
        normalizeInputForReference(Literal.of(map), objRef);
    }

    @Test
    public void testNormalizeObjectLiteralConvertFromString() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<>();
        map.put("d", "2.9");
        Symbol normalized = normalizeInputForReference(Literal.of(map), objInfo);
        assertThat(normalized, Matchers.instanceOf(Literal.class));
        assertThat(((Literal<Map<String, Object>>) (normalized)).value().get("d"), Matchers.<Object>is(2.9));
    }

    @Test
    public void testNormalizeObjectLiteral() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("d", 2.9);
                put("inner_strict", new HashMap<String, Object>() {
                    {
                        put("double", "-88.7");
                    }
                });
            }
        };
        normalizeInputForReference(Literal.of(map), objInfo);
        Symbol normalized = normalizeInputForReference(Literal.of(map), objInfo);
        assertThat(normalized, Matchers.instanceOf(Literal.class));
        assertThat(((Literal<Map<String, Object>>) (normalized)).value().get("d"), Matchers.<Object>is(2.9));
        assertThat(((Literal<Map<String, Object>>) (normalized)).value().get("inner_strict"), Matchers.<Object>is(new HashMap<String, Object>() {
            {
                put("double", (-88.7));
            }
        }));
    }

    @Test
    public void testNormalizeDynamicObjectLiteralWithAdditionalColumn() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<>();
        map.put("d", 2.9);
        map.put("half", "1.45");
        Symbol normalized = normalizeInputForReference(Literal.of(map), objInfo);
        assertThat(normalized, Matchers.instanceOf(Literal.class));
        assertThat(value(), Matchers.<Object>is(map));// stays the same

    }

    @Test
    public void testNormalizeDynamicObjectWithRestrictedAdditionalColumn() throws Exception {
        expectedException.expect(InvalidColumnNameException.class);
        expectedException.expectMessage("contains a dot");
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<>();
        map.put("_invalid.column_name", 0);
        normalizeInputForReference(Literal.of(map), objInfo);
    }

    @Test(expected = ColumnUnknownException.class)
    public void testNormalizeStrictObjectLiteralWithAdditionalColumn() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("strict"));
        Map<String, Object> map = new HashMap<>();
        map.put("inner_d", 2.9);
        map.put("half", "1.45");
        normalizeInputForReference(Literal.of(map), objInfo);
    }

    @Test(expected = ColumnUnknownException.class)
    public void testNormalizeStrictObjectLiteralWithAdditionalNestedColumn() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("strict"));
        Map<String, Object> map = new HashMap<>();
        map.put("inner_d", 2.9);
        map.put("inner_map", new HashMap<String, Object>() {
            {
                put("much_inner", "yaw");
            }
        });
        normalizeInputForReference(Literal.of(map), objInfo);
    }

    @Test(expected = ColumnUnknownException.class)
    public void testNormalizeNestedStrictObjectLiteralWithAdditionalColumn() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<>();
        map.put("inner_strict", new HashMap<String, Object>() {
            {
                put("double", 2.9);
                put("half", "1.45");
            }
        });
        map.put("half", "1.45");
        normalizeInputForReference(Literal.of(map), objInfo);
    }

    @Test
    public void testNormalizeDynamicNewColumnTimestamp() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("dyn"));
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("time", "1970-01-01T00:00:00");
            }
        };
        Literal<Map<String, Object>> literal = ((Literal) (normalizeInputForReference(Literal.of(map), objInfo)));
        assertThat(((String) (literal.value().get("time"))), Matchers.is("1970-01-01T00:00:00"));
    }

    @Test
    public void testNormalizeIgnoredNewColumnTimestamp() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("ignored"));
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("time", "1970-01-01T00:00:00");
            }
        };
        Literal<Map<String, Object>> literal = ((Literal) (normalizeInputForReference(Literal.of(map), objInfo)));
        assertThat(((String) (literal.value().get("time"))), Matchers.is("1970-01-01T00:00:00"));
    }

    @Test
    public void testNormalizeDynamicNewColumnNoTimestamp() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("ignored"));
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("no_time", "1970");
            }
        };
        Literal<Map<String, Object>> literal = ((Literal) (normalizeInputForReference(Literal.of(map), objInfo)));
        assertThat(((String) (literal.value().get("no_time"))), Matchers.is("1970"));
    }

    @Test
    public void testNormalizeStringToNumberColumn() throws Exception {
        Reference objInfo = userTableInfo.getReference(new ColumnIdent("d"));
        Literal<String> stringDoubleLiteral = Literal.of("298.444");
        Literal literal = ((Literal) (normalizeInputForReference(stringDoubleLiteral, objInfo)));
        assertThat(literal, SymbolMatchers.isLiteral(298.444, DOUBLE));
    }
}

