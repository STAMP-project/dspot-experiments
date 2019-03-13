/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.feel.codegen.feel11;


import BuiltInType.NUMBER;
import BuiltInType.STRING;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.DynamicTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DirectCompilerTest {
    public static final Logger LOG = LoggerFactory.getLogger(DirectCompilerTest.class);

    @Test
    public void test_FEEL_number() {
        Assert.assertThat(parseCompileEvaluate("10"), CoreMatchers.is(BigDecimal.valueOf(10)));
    }

    @Test
    public void test_FEEL_negative_number() {
        Assert.assertThat(parseCompileEvaluate("-10"), CoreMatchers.is(BigDecimal.valueOf((-10))));
    }

    @Test
    public void test_FEEL_DROOLS_2143() {
        // DROOLS-2143: Allow ''--1' expression as per FEEL grammar rule 26
        Assert.assertThat(parseCompileEvaluate("--10"), CoreMatchers.is(BigDecimal.valueOf(10)));
        Assert.assertThat(parseCompileEvaluate("---10"), CoreMatchers.is(BigDecimal.valueOf((-10))));
        Assert.assertThat(parseCompileEvaluate("+10"), CoreMatchers.is(BigDecimal.valueOf(10)));
    }

    @Test
    public void test_FEEL_boolean() {
        Assert.assertThat(parseCompileEvaluate("false"), CoreMatchers.is(false));
        Assert.assertThat(parseCompileEvaluate("true"), CoreMatchers.is(true));
        Assert.assertThat(parseCompileEvaluate("null"), CoreMatchers.nullValue());
    }

    @Test
    public void test_FEEL_null() {
        Assert.assertThat(parseCompileEvaluate("null"), CoreMatchers.nullValue());
    }

    @Test
    public void test_FEEL_string() {
        Assert.assertThat(parseCompileEvaluate("\"some string\""), CoreMatchers.is("some string"));
    }

    @Test
    public void test_primary_parens() {
        Assert.assertThat(parseCompileEvaluate("(\"some string\")"), CoreMatchers.is("some string"));
        Assert.assertThat(parseCompileEvaluate("(123)"), CoreMatchers.is(BigDecimal.valueOf(123)));
        Assert.assertThat(parseCompileEvaluate("(-123)"), CoreMatchers.is(BigDecimal.valueOf((-123))));
        Assert.assertThat(parseCompileEvaluate("-(123)"), CoreMatchers.is(BigDecimal.valueOf((-123))));
        Assert.assertThat(parseCompileEvaluate("(false)"), CoreMatchers.is(false));
        Assert.assertThat(parseCompileEvaluate("(true)"), CoreMatchers.is(true));
    }

    /**
     * See {@link FEELTernaryLogicTest}
     */
    @Test
    public void test_ternary_logic() {
        Assert.assertThat(parseCompileEvaluate("true and true"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("true and false"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("true and null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("false and true"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("false and false"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("false and null"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("null and true"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("null and false"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("null and null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("true or true"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("true or false"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("true or null"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("false or true"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("false or false"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("false or null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("null or true"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("null or false"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("null or null"), CoreMatchers.nullValue());
        // logical operator priority
        Assert.assertThat(parseCompileEvaluate("false and false or true"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("false and (false or true)"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("true or false and false"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("(true or false) and false"), CoreMatchers.is(Boolean.FALSE));
    }

    /**
     * Partially from {@link FEELConditionsAndLoopsTest}
     */
    @Test
    public void test_if() {
        Assert.assertThat(parseCompileEvaluate("if true then 15 else 5"), CoreMatchers.is(BigDecimal.valueOf(15)));
        Assert.assertThat(parseCompileEvaluate("if false then 15 else 5"), CoreMatchers.is(BigDecimal.valueOf(5)));
        Assert.assertThat(parseCompileEvaluate("if null then 15 else 5"), CoreMatchers.is(BigDecimal.valueOf(5)));
        Assert.assertThat(parseCompileEvaluate("if \"hello\" then 15 else 5"), CoreMatchers.is(BigDecimal.valueOf(5)));
    }

    @Test
    public void test_additiveExpression() {
        Assert.assertThat(parseCompileEvaluate("1 + 2"), CoreMatchers.is(BigDecimal.valueOf(3)));
        Assert.assertThat(parseCompileEvaluate("1 + null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("1 - 2"), CoreMatchers.is(BigDecimal.valueOf((-1))));
        Assert.assertThat(parseCompileEvaluate("1 - null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("\"Hello, \" + \"World\""), CoreMatchers.is("Hello, World"));
    }

    @Test
    public void test_multiplicativeExpression() {
        Assert.assertThat(parseCompileEvaluate("3 * 5"), CoreMatchers.is(BigDecimal.valueOf(15)));
        Assert.assertThat(parseCompileEvaluate("3 * null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("10 / 2"), CoreMatchers.is(BigDecimal.valueOf(5)));
        Assert.assertThat(parseCompileEvaluate("10 / null"), CoreMatchers.nullValue());
    }

    @Test
    public void test_exponentiationExpression() {
        Assert.assertThat(parseCompileEvaluate("3 ** 3"), CoreMatchers.is(BigDecimal.valueOf(27)));
        Assert.assertThat(parseCompileEvaluate("3 ** null"), CoreMatchers.nullValue());
    }

    @Test
    public void test_logicalNegationExpression() {
        // this is all invalid syntax
        Assert.assertThat(parseCompileEvaluate("not true"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("not false"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("not null"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("not 3"), CoreMatchers.nullValue());
    }

    @Test
    public void test_listExpression() {
        Assert.assertThat(parseCompileEvaluate("[]"), CoreMatchers.is(Collections.emptyList()));
        Assert.assertThat(parseCompileEvaluate("[ ]"), CoreMatchers.is(Collections.emptyList()));
        Assert.assertThat(parseCompileEvaluate("[1]"), CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(1))));
        Assert.assertThat(parseCompileEvaluate("[1, 2,3]"), CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3))));
    }

    @Test
    public void test_instanceOfExpression() {
        Assert.assertThat(parseCompileEvaluate("123 instance of number"), CoreMatchers.is(true));
        Assert.assertThat(parseCompileEvaluate("\"ciao\" instance of number"), CoreMatchers.is(false));
        Assert.assertThat(parseCompileEvaluate("123 instance of string"), CoreMatchers.is(false));
        Assert.assertThat(parseCompileEvaluate("\"ciao\" instance of string"), CoreMatchers.is(true));
    }

    @Test
    public void test_between() {
        Assert.assertThat(parseCompileEvaluate("10 between 5 and 12"), CoreMatchers.is(true));
        Assert.assertThat(parseCompileEvaluate("10 between 20 and 30"), CoreMatchers.is(false));
        Assert.assertThat(parseCompileEvaluate("10 between 5 and \"foo\""), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("\"foo\" between 5 and 12"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("\"foo\" between \"bar\" and \"zap\""), CoreMatchers.is(true));
        Assert.assertThat(parseCompileEvaluate("\"foo\" between null and \"zap\""), CoreMatchers.nullValue());
    }

    @Test
    public void test_filterPath() {
        // Filtering by index
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][1]"), CoreMatchers.is("a"));
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][2]"), CoreMatchers.is("b"));
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][3]"), CoreMatchers.is("c"));
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-1]"), CoreMatchers.is("c"));
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-2]"), CoreMatchers.is("b"));
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-3]"), CoreMatchers.is("a"));
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][4]"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][984]"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-4]"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("[\"a\", \"b\", \"c\"][-984]"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("\"a\"[1]"), CoreMatchers.is("a"));
        Assert.assertThat(parseCompileEvaluate("\"a\"[2]"), CoreMatchers.nullValue());
        Assert.assertThat(parseCompileEvaluate("\"a\"[-1]"), CoreMatchers.is("a"));
        Assert.assertThat(parseCompileEvaluate("\"a\"[-2]"), CoreMatchers.nullValue());
        // Filtering by boolean expression
        Assert.assertThat(parseCompileEvaluate("[1, 2, 3, 4][item = 4]"), CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(4))));
        Assert.assertThat(parseCompileEvaluate("[1, 2, 3, 4][item > 2]"), CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(4))));
        Assert.assertThat(parseCompileEvaluate("[1, 2, 3, 4][item > 5]"), CoreMatchers.is(Collections.emptyList()));
        Assert.assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x = 1]"), CoreMatchers.is(Arrays.asList(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("x", new BigDecimal(1)), DynamicTypeUtils.entry("y", new BigDecimal(2))))));
        Assert.assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x > 1]"), CoreMatchers.is(Arrays.asList(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("x", new BigDecimal(2)), DynamicTypeUtils.entry("y", new BigDecimal(3))))));
        Assert.assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ][x = 0]"), CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void test_filterPath_tricky1() {
        CompiledFEELExpression nameRef = parse("[ {x:1, y:2}, {x:2, y:3} ][x]");
        DirectCompilerTest.LOG.debug("{}", nameRef);
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("x", 2);
        Object result = nameRef.apply(context);
        DirectCompilerTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("x", new BigDecimal(2)), DynamicTypeUtils.entry("y", new BigDecimal(3)))));
    }

    @Test
    public void test_filterPath_tricky2() {
        CompiledFEELExpression nameRef = parse("[ {x:1, y:2}, {x:2, y:3} ][x]");
        DirectCompilerTest.LOG.debug("{}", nameRef);
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("x", false);
        Object result = nameRef.apply(context);
        DirectCompilerTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void test_filterPathSelection() {
        // Selection
        Assert.assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ].y"), CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3))));
        Assert.assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2} ].y"), CoreMatchers.is(Arrays.asList(BigDecimal.valueOf(2))));
        Assert.assertThat(parseCompileEvaluate("[ {x:1, y:2}, {x:2, y:3} ].z"), CoreMatchers.is(Collections.emptyList()));
    }

    @Test
    public void test_for() {
        // for
        Assert.assertThat(parseCompileEvaluate("for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y"), CoreMatchers.is(Arrays.asList(10, 20, 30, 20, 40, 60, 30, 60, 90).stream().map(( x) -> BigDecimal.valueOf(x)).collect(Collectors.toList())));
        // normal:
        Assert.assertThat(parseCompileEvaluate("for x in [1, 2, 3] return x+1"), CoreMatchers.is(Arrays.asList(1, 2, 3).stream().map(( x) -> BigDecimal.valueOf((x + 1))).collect(Collectors.toList())));
        // TODO in order to parse correctly the enhanced for loop it is required to configure the FEEL Profiles
    }

    @Test
    public void test_quantifiedExpressions() {
        // quantified expressions
        Assert.assertThat(parseCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > 100"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("some price in [ 80, 11, 90 ] satisfies price > 100"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("some x in [ 5, 6, 7 ], y in [ 10, 11, 6 ] satisfies x > y"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 10"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 70"), CoreMatchers.is(Boolean.FALSE));
        Assert.assertThat(parseCompileEvaluate("some x in [ 5, 6, 7 ], y in [ 10, 11, 12 ] satisfies x < y"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > max(100, 50, 10)"), CoreMatchers.is(Boolean.TRUE));
    }

    @Test
    public void test_basicFunctionInvocation() {
        Assert.assertThat(parseCompileEvaluate("max(1, 2, 3)"), CoreMatchers.is(new BigDecimal(3)));
    }

    @Test
    public void test_basicFunctionDefinition() {
        Assert.assertThat(parseCompileEvaluate("function (a, b) a + b"), CoreMatchers.is(CoreMatchers.instanceOf(CompiledCustomFEELFunction.class)));
        Assert.assertThat(parseCompileEvaluate("{ s : function (a, b) a + b, x : 1, y : 2, r : s(x,y) }.r"), CoreMatchers.is(new BigDecimal(3)));
    }

    @Test
    public void test_namedFunctionInvocation() {
        Assert.assertThat(parseCompileEvaluate("substring(start position: 2, string: \"FOOBAR\")"), CoreMatchers.is("OOBAR"));
        Assert.assertThat(parseCompileEvaluate("ceiling( n : 1.5 )"), CoreMatchers.is(new BigDecimal("2")));
    }

    @Test
    public void test_Misc_fromOriginalFEELInterpretedTestSuite() {
        Assert.assertThat(parseCompileEvaluate("if null then \"foo\" else \"bar\""), CoreMatchers.is("bar"));
        Assert.assertThat(parseCompileEvaluate("{ hello world : function() \"Hello World!\", message : hello world() }.message"), CoreMatchers.is("Hello World!"));
        Assert.assertThat(parseCompileEvaluate("1 + if true then 1 else 2"), CoreMatchers.is(new BigDecimal("2")));
        Assert.assertThat(parseCompileEvaluate("\"string with \\\"quotes\\\"\""), CoreMatchers.is("string with \"quotes\""));
        Assert.assertThat(parseCompileEvaluate("date( -0105, 8, 2 )"), CoreMatchers.is(LocalDate.of((-105), 8, 2)));
        Assert.assertThat(parseCompileEvaluate("string(null)"), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(parseCompileEvaluate("[ null ]"), CoreMatchers.is(Arrays.asList(new Object[]{ null })));
        Assert.assertThat(parseCompileEvaluate("[ null, null ]"), CoreMatchers.is(Arrays.asList(new Object[]{ null, null })));
        Assert.assertThat(parseCompileEvaluate("[ null, 47, null ]"), CoreMatchers.is(Arrays.asList(new Object[]{ null, BigDecimal.valueOf(47), null })));
    }

    @Test
    public void test_Benchmark_feelExpressions() {
        Assert.assertThat(parseCompileEvaluate("{ full name: { first name: \"John\", last name: \"Doe\" } }.full name.last name"), CoreMatchers.is("Doe"));
        Assert.assertThat(parseCompileEvaluate("some price in [ 80, 11, 110 ] satisfies price > 100"), CoreMatchers.is(Boolean.TRUE));
        Assert.assertThat(parseCompileEvaluate("every price in [ 80, 11, 90 ] satisfies price > 10"), CoreMatchers.is(Boolean.TRUE));
    }

    @Test
    public void test_contextExpression() {
        Assert.assertThat(parseCompileEvaluate("{}"), CoreMatchers.is(Collections.emptyMap()));
        Assert.assertThat(parseCompileEvaluate("{ }"), CoreMatchers.is(Collections.emptyMap()));
        Assert.assertThat(parseCompileEvaluate("{ a : 1 }"), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a", new BigDecimal(1)))));
        Assert.assertThat(parseCompileEvaluate("{ \"a\" : 1 }"), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a", new BigDecimal(1)))));
        Assert.assertThat(parseCompileEvaluate("{ \" a\" : 1 }"), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry(" a", new BigDecimal(1)))));// Demonstrating a bad practice.

        Assert.assertThat(parseCompileEvaluate("{ a : 1, b : 2, c : 3 }"), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a", new BigDecimal(1)), DynamicTypeUtils.entry("b", new BigDecimal(2)), DynamicTypeUtils.entry("c", new BigDecimal(3)))));
        Assert.assertThat(parseCompileEvaluate("{ a : 1, a name : \"John Doe\" }"), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a", new BigDecimal(1)), DynamicTypeUtils.entry("a name", "John Doe"))));
        Assert.assertThat(parseCompileEvaluate("{ a : 1, b : a }"), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a", new BigDecimal(1)), DynamicTypeUtils.entry("b", new BigDecimal(1)))));
    }

    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testContextWithMultipleEntries() {
        String inputExpression = "{ \"a string key\" : 10," + ((("\n" + " a non-string key : 11,") + "\n") + " a key.with + /' odd chars : 12 }");
        Assert.assertThat(parseCompileEvaluate(inputExpression), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a string key", new BigDecimal(10)), DynamicTypeUtils.entry("a non-string key", new BigDecimal(11)), DynamicTypeUtils.entry("a key.with + /' odd chars", new BigDecimal(12)))));
    }

    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testNestedContexts() {
        String inputExpression = "{ a value : 10," + ((((((((((((((((((((("\n" + " an applicant : { ") + "\n") + "    first name : \"Edson\", ") + "\n") + "    last + name : \"Tirelli\", ") + "\n") + "    full name : first name + last + name, ") + "\n") + "    address : {") + "\n") + "        street : \"55 broadway st\",") + "\n") + "        city : \"New York\" ") + "\n") + "    }, ") + "\n") + "    xxx: last + name") + "\n") + " } ") + "\n") + "}");
        Assert.assertThat(parseCompileEvaluate(inputExpression), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a value", new BigDecimal(10)), DynamicTypeUtils.entry("an applicant", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("first name", "Edson"), DynamicTypeUtils.entry("last + name", "Tirelli"), DynamicTypeUtils.entry("full name", "EdsonTirelli"), DynamicTypeUtils.entry("address", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("street", "55 broadway st"), DynamicTypeUtils.entry("city", "New York"))), DynamicTypeUtils.entry("xxx", "Tirelli"))))));
    }

    /**
     * See {@link FEELParserTest}
     */
    @Test
    public void testNestedContexts2() {
        String complexContext = "{ an applicant : {                                \n" + (((((("    home address : {                              \n" + "        street name: \"broadway st\",             \n") + "        city : \"New York\"                       \n") + "    }                                             \n") + "   },                                             \n") + "   street : an applicant.home address.street name \n") + "}                                                 ");
        Assert.assertThat(parseCompileEvaluate(complexContext), CoreMatchers.is(DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("an applicant", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("home address", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("street name", "broadway st"), DynamicTypeUtils.entry("city", "New York"))))), DynamicTypeUtils.entry("street", "broadway st"))));
    }

    @Test
    public void testNameReference() {
        String inputExpression = "someSimpleName";
        CompiledFEELExpression nameRef = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("someSimpleName", STRING)));
        DirectCompilerTest.LOG.debug("{}", nameRef);
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("someSimpleName", 123L);
        Object result = nameRef.apply(context);
        DirectCompilerTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(BigDecimal.valueOf(123)));
    }

    @Test
    public void testQualifiedName() {
        String inputExpression = "My Person.Full Name";
        Type personType = new org.kie.dmn.feel.lang.impl.MapBackedType("Person", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("Full Name", STRING), DynamicTypeUtils.entry("Age", NUMBER)));
        CompiledFEELExpression qualRef = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("My Person", personType)));
        DirectCompilerTest.LOG.debug("{}", qualRef);
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("My Person", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("Full Name", "John Doe"), DynamicTypeUtils.entry("Age", 47)));
        Object result = qualRef.apply(context);
        DirectCompilerTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is("John Doe"));
        // check number coercion for qualified name
        CompiledFEELExpression personAgeExpression = parse("My Person.Age", DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("My Person", personType)));
        DirectCompilerTest.LOG.debug("{}", personAgeExpression);
        Object resultPersonAge = personAgeExpression.apply(context);// Please notice input variable in context is a Map containing and entry value for int 47.

        DirectCompilerTest.LOG.debug("{}", resultPersonAge);
        Assert.assertThat(resultPersonAge, CoreMatchers.is(BigDecimal.valueOf(47)));
    }

    public static class MyPerson {
        @FEELProperty("Full Name")
        public String getFullName() {
            return "John Doe";
        }
    }

    @Test
    public void testQualifiedName2() {
        String inputExpression = "My Person.Full Name";
        Type personType = JavaBackedType.of(DirectCompilerTest.MyPerson.class);
        CompiledFEELExpression qualRef = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("My Person", personType)));
        DirectCompilerTest.LOG.debug("{}", qualRef);
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("My Person", new DirectCompilerTest.MyPerson());
        Object result = qualRef.apply(context);
        DirectCompilerTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is("John Doe"));
    }

    @Test
    public void testQualifiedName3() {
        String inputExpression = "a date.year";
        Type dateType = BuiltInType.DATE;
        CompiledFEELExpression qualRef = parse(inputExpression, DynamicTypeUtils.mapOf(DynamicTypeUtils.entry("a date", dateType)));
        DirectCompilerTest.LOG.debug("{}", qualRef);
        EvaluationContext context = CodegenTestUtil.newEmptyEvaluationContext();
        context.setValue("a date", LocalDate.of(2016, 8, 2));
        Object result = qualRef.apply(context);
        DirectCompilerTest.LOG.debug("{}", result);
        Assert.assertThat(result, CoreMatchers.is(BigDecimal.valueOf(2016)));
    }
}

