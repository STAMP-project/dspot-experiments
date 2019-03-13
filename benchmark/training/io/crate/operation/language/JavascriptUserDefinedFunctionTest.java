/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.operation.language;


import DataTypes.DOUBLE;
import DataTypes.DOUBLE_ARRAY;
import DataTypes.FLOAT;
import DataTypes.GEO_POINT;
import DataTypes.INTEGER;
import DataTypes.IP;
import DataTypes.LONG;
import DataTypes.SHORT;
import DataTypes.STRING;
import DataTypes.STRING_ARRAY;
import DataTypes.TIMESTAMP;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.crate.analyze.FunctionArgumentDefinition;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Literal;
import io.crate.expression.udf.UserDefinedFunctionMetaData;
import io.crate.expression.udf.UserDefinedFunctionService;
import io.crate.metadata.FunctionIdent;
import io.crate.metadata.FunctionImplementation;
import io.crate.metadata.Schemas;
import io.crate.types.DataTypes;
import io.crate.types.ObjectType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptException;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.common.lucene.BytesRefs;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JavascriptUserDefinedFunctionTest extends AbstractScalarFunctionsTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private static final String JS = "javascript";

    private UserDefinedFunctionService udfService;

    private Map<FunctionIdent, FunctionImplementation> functionImplementations = new HashMap<>();

    @Test
    public void testObjectReturnType() throws Exception {
        registerUserDefinedFunction("f", ObjectType.untyped(), ImmutableList.of(), "function f() { return JSON.parse(\'{\"foo\": \"bar\"}\'); }");
        assertEvaluate("f()", ImmutableMap.of("foo", "bar"));
    }

    @Test
    public void testValidateCatchesScriptException() {
        UserDefinedFunctionMetaData udfMeta = new UserDefinedFunctionMetaData(Schemas.DOC_SCHEMA_NAME, "f", Collections.singletonList(FunctionArgumentDefinition.of(DOUBLE)), DataTypes.DOUBLE_ARRAY, JavascriptUserDefinedFunctionTest.JS, "function f(a) { return a[0]1*#?; }");
        String validation = udfService.getLanguage(JavascriptUserDefinedFunctionTest.JS).validate(udfMeta);
        assertThat(validation, Matchers.startsWith("Invalid JavaScript in function 'doc.f(double)'"));
        assertThat(validation, Matchers.endsWith("^ in <eval> at line number 1 at column number 27"));
    }

    @Test
    public void testValidateCatchesAssertionError() {
        UserDefinedFunctionMetaData udfMeta = new UserDefinedFunctionMetaData(Schemas.DOC_SCHEMA_NAME, "f", Collections.singletonList(FunctionArgumentDefinition.of(DOUBLE)), DataTypes.DOUBLE_ARRAY, JavascriptUserDefinedFunctionTest.JS, "var f = (a) => a * a;");
        String validation = udfService.getLanguage(JavascriptUserDefinedFunctionTest.JS).validate(udfMeta);
        String javaVersion = System.getProperty("java.specification.version");
        try {
            if ((Integer.parseInt(javaVersion)) >= 9) {
                assertThat(validation, Matchers.is(Matchers.nullValue()));
            }
        } catch (NumberFormatException e) {
            assertThat(validation, Matchers.startsWith("Invalid JavaScript in function 'doc.f(double)'"));
            assertThat(validation, Matchers.endsWith("Failed generating bytecode for <eval>:1"));
        }
    }

    @Test
    public void testValidJavascript() throws Exception {
        UserDefinedFunctionMetaData udfMeta = new UserDefinedFunctionMetaData(Schemas.DOC_SCHEMA_NAME, "f", Collections.singletonList(FunctionArgumentDefinition.of(DOUBLE_ARRAY)), DataTypes.DOUBLE, JavascriptUserDefinedFunctionTest.JS, "function f(a) { return a[0]; }");
        String validation = udfService.getLanguage(JavascriptUserDefinedFunctionTest.JS).validate(udfMeta);
        assertNull(validation);
    }

    @Test
    public void testArrayReturnType() throws Exception {
        registerUserDefinedFunction("f", DOUBLE_ARRAY, ImmutableList.of(), "function f() { return [1, 2]; }");
        assertEvaluate("f()", new double[]{ 1.0, 2.0 });
    }

    @Test
    public void testTimestampReturnType() throws Exception {
        registerUserDefinedFunction("f", TIMESTAMP, ImmutableList.of(), "function f() { return \"1990-01-01T00:00:00\"; }");
        assertEvaluate("f()", 631152000000L);
    }

    @Test
    public void testIpReturnType() throws Exception {
        registerUserDefinedFunction("f", IP, ImmutableList.of(), "function f() { return \"127.0.0.1\"; }");
        assertEvaluate("f()", IP.value("127.0.0.1"));
    }

    @Test
    public void testPrimitiveReturnType() throws Exception {
        registerUserDefinedFunction("f", INTEGER, ImmutableList.of(), "function f() { return 10; }");
        assertEvaluate("f()", 10);
    }

    @Test
    public void testObjectReturnTypeAndInputArguments() throws Exception {
        registerUserDefinedFunction("f", FLOAT, ImmutableList.of(DOUBLE, SHORT), "function f(x, y) { return x + y; }");
        assertEvaluate("f(double_val, short_val)", 3.0F, Literal.of(1), Literal.of(2));
    }

    @Test
    public void testPrimitiveReturnTypeAndInputArguments() throws Exception {
        registerUserDefinedFunction("f", FLOAT, ImmutableList.of(DOUBLE, SHORT), "function f(x, y) { return x + y; }");
        assertEvaluate("f(double_val, short_val)", 3.0F, Literal.of(1), Literal.of(2));
    }

    @Test
    public void testGeoTypeReturnTypeWithDoubleArray() throws Exception {
        registerUserDefinedFunction("f", GEO_POINT, ImmutableList.of(), "function f() { return [1, 1]; }");
        assertEvaluate("f()", new double[]{ 1.0, 1.0 });
    }

    @Test
    public void testGeoTypeReturnTypeWithWKT() throws Exception {
        registerUserDefinedFunction("f", GEO_POINT, ImmutableList.of(), "function f() { return \"POINT (1.0 2.0)\"; }");
        assertEvaluate("f()", new double[]{ 1.0, 2.0 });
    }

    @Test
    public void testOverloadingUserDefinedFunctions() throws Exception {
        registerUserDefinedFunction("f", LONG, ImmutableList.of(), "function f() { return 1; }");
        registerUserDefinedFunction("f", LONG, ImmutableList.of(LONG), "function f(x) { return x; }");
        registerUserDefinedFunction("f", LONG, ImmutableList.of(LONG, INTEGER), "function f(x, y) { return x + y; }");
        assertEvaluate("f()", 1L);
        assertEvaluate("f(x)", 2L, Literal.of(2));
        assertEvaluate("f(x, a)", 3L, Literal.of(2), Literal.of(1));
    }

    @Test
    public void testFunctionWrongNameInFunctionBody() throws Exception {
        exception.expect(ScriptException.class);
        exception.expectMessage("The name of the function signature doesn't match");
        registerUserDefinedFunction("test", LONG, ImmutableList.of(), "function f() { return 1; }");
        assertEvaluate("test()", 1L);
    }

    @Test
    public void testNormalizeOnObjectInput() throws Exception {
        registerUserDefinedFunction("f", ObjectType.untyped(), ImmutableList.of(ObjectType.untyped()), "function f(x) { return x; }");
        assertNormalize("f({})", isLiteral(new HashMap()));
    }

    @Test
    public void testNormalizeOnArrayInput() throws Exception {
        registerUserDefinedFunction("f", LONG, ImmutableList.of(DOUBLE_ARRAY), "function f(x) { return x[1]; }");
        assertNormalize("f([1.0, 2.0])", isLiteral(2L));
    }

    @Test
    public void testNormalizeOnStringInputs() throws Exception {
        registerUserDefinedFunction("f", STRING, ImmutableList.of(STRING), "function f(x) { return x; }");
        assertNormalize("f('bar')", isLiteral("bar"));
    }

    @Test
    public void testAccessJavaClasses() throws Exception {
        exception.expect(ScriptException.class);
        exception.expectMessage(Matchers.anyOf(Matchers.containsString("has no such function \"type\""), Matchers.containsString("ReferenceError: \"Java\" is not defined")));
        registerUserDefinedFunction("f", LONG, ImmutableList.of(LONG), "function f(x) { var File = Java.type(\"java.io.File\"); return x; }");
        assertEvaluate("f(x)", 1L, Literal.of(1L));
    }

    @Test
    public void testEvaluateBytesRefConvertedToString() throws Exception {
        registerUserDefinedFunction("f", STRING, ImmutableList.of(STRING), "function f(name) { return 'foo' + name; }");
        assertEvaluate("f(name)", "foobar", Literal.of("bar"));
    }

    @Test
    public void testJavaScriptFunctionReturnsUndefined() throws Exception {
        registerUserDefinedFunction("f", STRING, ImmutableList.of(STRING), "function f(name) { }");
        assertEvaluate("f(name)", null, Literal.of("bar"));
    }

    @Test
    public void testJavaScriptFunctionReturnsNull() throws Exception {
        registerUserDefinedFunction("f", STRING, ImmutableList.of(), "function f() { return null; }");
        assertEvaluate("f()", null);
    }

    @Test
    public void testStringArrayTypeArgument() throws Exception {
        registerUserDefinedFunction("f", STRING, ImmutableList.of(STRING_ARRAY), "function f(a) { return Array.prototype.join.call(a, '.'); }");
        assertEvaluate("f(['a', 'b'])", Matchers.is("a.b"));
        assertEvaluate("f(['a', 'b'])", Matchers.is("a.b"), Literal.of(new BytesRef[]{ BytesRefs.toBytesRef("a"), BytesRefs.toBytesRef("b") }, STRING_ARRAY));
    }
}

