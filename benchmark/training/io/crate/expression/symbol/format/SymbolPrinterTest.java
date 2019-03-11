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
package io.crate.expression.symbol.format;


import DataTypes.GEO_SHAPE;
import DataTypes.INTEGER;
import DataTypes.UNDEFINED;
import FunctionInfo.Type;
import SymbolPrinter.INSTANCE;
import com.google.common.collect.ImmutableMap;
import io.crate.analyze.relations.AbstractTableRelation;
import io.crate.expression.symbol.Aggregation;
import io.crate.expression.symbol.Field;
import io.crate.expression.symbol.InputColumn;
import io.crate.expression.symbol.Literal;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.FunctionInfo;
import io.crate.metadata.Reference;
import io.crate.metadata.RelationName;
import io.crate.metadata.RowGranularity;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SqlExpressions;
import io.crate.types.DataTypes;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SymbolPrinterTest extends CrateUnitTest {
    private SqlExpressions sqlExpressions;

    private SymbolPrinter printer;

    private static final String TABLE_NAME = "formatter";

    @Test
    public void testFormatFunctionFullQualifiedInputName() throws Exception {
        Symbol f = sqlExpressions.asSymbol("concat('foo', foo)");
        assertPrint(f, "concat('foo', doc.formatter.foo)");
    }

    @Test
    public void testFormatFunctionsWithoutBrackets() throws Exception {
        Symbol f = sqlExpressions.asSymbol("current_schema");
        assertPrint(f, "current_schema");
    }

    @Test
    public void testSubstrFunction() throws Exception {
        Symbol f = sqlExpressions.asSymbol("substr('foobar', 4)");
        assertPrint(f, "'bar'");
    }

    @Test
    public void testSubstrFunctionWithLength() throws Exception {
        Symbol f = sqlExpressions.asSymbol("substr('foobar', 4, 1)");
        assertPrint(f, "'b'");
    }

    @Test
    public void testWindowFunction() throws Exception {
        Symbol f = sqlExpressions.asSymbol("avg(idx) over (partition by idx order by foo)");
        assertPrint(f, "avg(doc.formatter.idx)");
    }

    @Test
    public void testFormatAggregation() throws Exception {
        FunctionInfo functionInfo = new FunctionInfo(new io.crate.metadata.FunctionIdent("agg", Collections.singletonList(INTEGER)), DataTypes.LONG, Type.AGGREGATE);
        Aggregation a = new Aggregation(functionInfo, DataTypes.LONG, Collections.singletonList(Literal.of((-127))));
        assertPrint(a, "agg(-127)");
    }

    @Test
    public void testReference() throws Exception {
        Reference r = new Reference(new io.crate.metadata.ReferenceIdent(new RelationName("sys", "table"), new ColumnIdent("column", Arrays.asList("path", "nested"))), RowGranularity.DOC, DataTypes.STRING);
        assertPrint(r, "sys.\"table\".\"column\"[\'path\'][\'nested\']");
    }

    @Test
    public void testDocReference() throws Exception {
        Reference r = new Reference(new io.crate.metadata.ReferenceIdent(new RelationName("doc", "table"), new ColumnIdent("column", Arrays.asList("path", "nested"))), RowGranularity.DOC, DataTypes.STRING);
        assertPrint(r, "doc.\"table\".\"column\"[\'path\'][\'nested\']");
    }

    @Test
    public void testDynamicReference() throws Exception {
        Reference r = new io.crate.expression.symbol.DynamicReference(new io.crate.metadata.ReferenceIdent(new RelationName("schema", "table"), new ColumnIdent("column", Arrays.asList("path", "nested"))), RowGranularity.DOC);
        assertPrint(r, "schema.\"table\".\"column\"[\'path\'][\'nested\']");
    }

    @Test
    public void testReferenceEscaped() throws Exception {
        Reference r = new Reference(new io.crate.metadata.ReferenceIdent(new RelationName("doc", "table"), new ColumnIdent("colum\"n")), RowGranularity.DOC, DataTypes.STRING);
        assertPrint(r, "doc.\"table\".\"colum\"\"n\"");
    }

    @Test
    public void testLiteralEscaped() throws Exception {
        assertPrint(Literal.of("bla'bla"), "'bla''bla'");
    }

    @Test
    public void testObjectLiteral() throws Exception {
        Literal<Map<String, Object>> l = Literal.of(new HashMap<String, Object>() {
            {
                put("field", "value");
                put("array", new Integer[]{ 1, 2, 3 });
                put("nestedMap", new HashMap<String, Object>() {
                    {
                        put("inner", (-5.0E-5));
                    }
                });
            }
        });
        assertPrint(l, "{\"array\"=[1, 2, 3], \"field\"=\'value\', \"nestedMap\"={\"inner\"=-5.0E-5}}");
    }

    @Test
    public void testBooleanLiteral() throws Exception {
        Literal<Boolean> f = Literal.of(false);
        assertPrint(f, "false");
        Literal<Boolean> t = Literal.of(true);
        assertPrint(t, "true");
    }

    @Test
    public void visitStringLiteral() throws Exception {
        Literal<String> l = Literal.of("fooBar");
        assertPrint(l, "'fooBar'");
    }

    @Test
    public void visitDoubleLiteral() throws Exception {
        Literal<Double> d = Literal.of((-500.88765));
        assertPrint(d, "-500.88765");
    }

    @Test
    public void visitFloatLiteral() throws Exception {
        Literal<Float> f = Literal.of(500.887F);
        assertPrint(f, "500.887");
    }

    @Test
    public void testExtract() {
        assertPrintIsParseable("to_long(extract(century from '1970-01-01'))");
        assertPrintIsParseable("to_long(extract(day_of_week from current_timestamp))");
    }

    @Test
    public void testIsNull() throws Exception {
        assertPrintIsParseable("null IS NULL");
        assertPrintIsParseable("formatter.foo IS NULL");
        assertPrintIsParseable("'123' IS NULL");
    }

    @Test
    public void testQueries() throws Exception {
        assertPrintIsParseable("(1 + formatter.bar) * 4 = 14");
    }

    @Test
    public void testCast() throws Exception {
        assertPrintIsParseable("CAST (formatter.bar AS TIMESTAMP)");
        assertPrintIsParseable("CAST (TRUE AS string)");
        assertPrintIsParseable("CAST (1+2 AS string)");
    }

    @Test
    public void testNull() throws Exception {
        assertPrint(Literal.of(UNDEFINED, null), "NULL");
    }

    @Test
    public void testNullKey() throws Exception {
        assertPrint(Literal.of(new HashMap<String, Object>() {
            {
                put("null", null);
            }
        }), "{\"null\"=NULL}");
    }

    @Test
    public void testNativeArray() throws Exception {
        assertPrint(Literal.of(GEO_SHAPE, ImmutableMap.of("type", "Point", "coordinates", new double[]{ 1.0, 2.0 })), "{\"coordinates\"=[1.0, 2.0], \"type\"=\'Point\'}");
    }

    @Test
    public void testFormatQualified() throws Exception {
        Symbol ref = sqlExpressions.asSymbol("formatter.\"CraZy\"");
        assertThat(printer.printQualified(ref), Matchers.is("doc.formatter.\"CraZy\""));
        assertThat(printer.printUnqualified(ref), Matchers.is("\"CraZy\""));
    }

    @Test
    public void testMaxDepthEllipsis() throws Exception {
        Symbol nestedFn = sqlExpressions.asSymbol("abs(sqrt(ln(1+1+1+1+1+1+1+1)))");
        assertThat(printer.printUnqualified(nestedFn), Matchers.is("1.442026886600883"));
    }

    @Test
    public void testStyles() throws Exception {
        Symbol nestedFn = sqlExpressions.asSymbol("abs(sqrt(ln(bar+cast(\"select\" as long)+1+1+1+1+1+1)))");
        assertThat(printer.printQualified(nestedFn), Matchers.is("abs(sqrt(ln((((((((doc.formatter.bar + cast(doc.formatter.\"select\" AS long)) + 1) + 1) + 1) + 1) + 1) + 1))))"));
        assertThat(printer.printUnqualified(nestedFn), Matchers.is("abs(sqrt(ln((((((((bar + cast(\"select\" AS long)) + 1) + 1) + 1) + 1) + 1) + 1))))"));
    }

    @Test
    public void testFormatOperatorWithStaticInstance() throws Exception {
        Symbol comparisonOperator = sqlExpressions.asSymbol("bar = 1 and foo = 2");
        String printed = INSTANCE.printQualified(comparisonOperator);
        assertThat(printed, Matchers.is("((doc.formatter.bar = 1) AND (doc.formatter.foo = '2'))"));
    }

    @Test
    public void testPrintFetchRefs() throws Exception {
        Field field = ((Field) (sqlExpressions.asSymbol("bar")));
        Reference reference = ((AbstractTableRelation) (field.relation())).resolveField(field);
        Symbol fetchRef = new io.crate.expression.symbol.FetchReference(new InputColumn(1, reference.valueType()), reference);
        assertPrint(fetchRef, "FETCH(INPUT(1), doc.formatter.bar)");
    }

    @Test
    public void testPrintInputColumn() throws Exception {
        Symbol inputCol = new InputColumn(42);
        assertPrint(inputCol, "INPUT(42)");
    }

    @Test
    public void testPrintLikeOperator() throws Exception {
        Symbol likeQuery = sqlExpressions.asSymbol("foo like '%bla%'");
        assertPrint(likeQuery, "(doc.formatter.foo LIKE '%bla%')");
        assertPrintStatic(likeQuery, "(doc.formatter.foo LIKE '%bla%')");
        assertPrintIsParseable("(foo LIKE 'a')");
    }

    @Test
    public void testPrintAnyEqOperator() throws Exception {
        assertPrintingRoundTrip("foo = ANY (['a', 'b', 'c'])", "(doc.formatter.foo = ANY(['a', 'b', 'c']))");
        assertPrintingRoundTrip("foo = ANY(s_arr)", "(doc.formatter.foo = ANY(doc.formatter.s_arr))");
    }

    @Test
    public void testAnyNeqOperator() throws Exception {
        assertPrintingRoundTrip("not foo != ANY (['a', 'b', 'c'])", "(NOT (doc.formatter.foo <> ANY(['a', 'b', 'c'])))");
        assertPrintingRoundTrip("not foo != ANY(s_arr)", "(NOT (doc.formatter.foo <> ANY(doc.formatter.s_arr)))");
    }

    @Test
    public void testNot() throws Exception {
        assertPrintingRoundTrip("not foo = 'bar'", "(NOT (doc.formatter.foo = 'bar'))");
    }

    @Test
    public void testAnyLikeOperator() throws Exception {
        assertPrintingRoundTrip("foo LIKE ANY (s_arr)", "(doc.formatter.foo LIKE ANY(doc.formatter.s_arr))");
        assertPrintingRoundTrip("foo NOT LIKE ANY (['a', 'b', 'c'])", "(doc.formatter.foo NOT LIKE ANY(['a', 'b', 'c']))");
    }
}

