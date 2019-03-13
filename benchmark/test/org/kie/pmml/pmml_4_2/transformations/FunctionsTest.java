/**
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.pmml_4_2.transformations;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.kie.pmml.pmml_4_2.PMML4Helper;


@Ignore
public class FunctionsTest extends DroolsAbstractPMMLTest {
    private static final boolean VERBOSE = false;

    private static final String source = "org/kie/pmml/pmml_4_2/test_functions.xml";

    private static final String packageName = "org.kie.pmml.pmml_4_2.test";

    @Test
    public void testFunctions() throws Exception {
        getKSession().getEntryPoint("in_Age").insert(30);
        getKSession().getEntryPoint("in_Age2").insert(2);
        getKSession().fireAllRules();
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(FunctionsTest.packageName, "MappedAge"), true, false, null, 30);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(FunctionsTest.packageName, "NestedAge"), true, false, null, 932.0);
        checkGeneratedRules();
    }

    @Test
    public void testFunctionMapping() {
        PMML4Helper ctx = new PMML4Helper();
        Assert.assertEquals("(2 + 3 + 4)", ctx.mapFunction("+", "2", "3", "4"));
        Assert.assertEquals("(2 - 3)", ctx.mapFunction("-", "2", "3"));
        Assert.assertEquals("(2 * 3 * 4)", ctx.mapFunction("*", "2", "3", "4"));
        Assert.assertEquals("(2 / 4)", ctx.mapFunction("/", "2", "4"));
        Assert.assertEquals("(Math.min(2,Math.min(3,4)))", ctx.mapFunction("min", "2", "3", "4"));
        Assert.assertEquals("(Math.max(2,Math.max(3,4)))", ctx.mapFunction("max", "2", "3", "4"));
        Assert.assertEquals("(2 + 3 + 4)", ctx.mapFunction("sum", "2", "3", "4"));
        Assert.assertEquals("(2 * 3 * 4)", ctx.mapFunction("product", "2", "3", "4"));
        Assert.assertEquals("((2 + 3 + 4) / 3)", ctx.mapFunction("avg", "2", "3", "4"));
        Assert.assertEquals("(3)", ctx.mapFunction("median", "1", "2", "3", "4", "5"));
        Assert.assertEquals("( 0.5 * 3 + 0.5 * 4 )", ctx.mapFunction("median", "1", "2", "3", "4", "5", "6"));
        Assert.assertEquals("(Math.log10(2))", ctx.mapFunction("log10", "2"));
        Assert.assertEquals("(Math.log(2))", ctx.mapFunction("ln", "2"));
        Assert.assertEquals("(Math.sqrt(2))", ctx.mapFunction("sqrt", "2"));
        Assert.assertEquals("(Math.abs(2))", ctx.mapFunction("abs", "2"));
        Assert.assertEquals("(Math.exp(2))", ctx.mapFunction("exp", "2"));
        Assert.assertEquals("(Math.pow(2,3))", ctx.mapFunction("pow", "2", "3"));
        Assert.assertEquals("(1)", ctx.mapFunction("pow", "0", "0"));
        Assert.assertEquals("(2 > 3 ? 1 : 0)", ctx.mapFunction("threshold", "2", "3"));
        Assert.assertEquals("(Math.floor(2))", ctx.mapFunction("floor", "2"));
        Assert.assertEquals("(Math.ceil(2))", ctx.mapFunction("ceil", "2"));
        Assert.assertEquals("(Math.round(2))", ctx.mapFunction("round", "2"));
        Assert.assertEquals("(\"abc\".toString().toUpperCase())", ctx.mapFunction("uppercase", "\"abc\""));
        Assert.assertEquals("(\"testString\".toString().substring(2,6))", ctx.mapFunction("substring", "\"testString\"", "3", "4"));
        Assert.assertEquals("(new java.util.Formatter(new StringBuilder(),java.util.Locale.getDefault()).format(\"%3d\",3.0))", ctx.mapFunction("formatNumber", "\"%3d\"", "3.0"));
        Assert.assertEquals("(new java.text.SimpleDateFormat(\"format\").format(new SimpleDateFormat().parse(\"date\", java.util.Locale.ENGLISH)))", ctx.mapFunction("formatDatetime", "\"date\"", "\"format\""));
        Assert.assertEquals("(( (new java.text.SimpleDateFormat()).parse(\"date\").getTime() - (new java.text.SimpleDateFormat()).parse(\"01/01/1956\").getTime() ) / (1000*60*60*24))", ctx.mapFunction("dateDaysSinceYear", "\"date\"", "1956"));
        Assert.assertEquals("(( (new java.text.SimpleDateFormat()).parse(\"date\").getTime() - (new java.text.SimpleDateFormat()).parse(\"01/01/1956\").getTime() ) / 1000)", ctx.mapFunction("dateSecondsSinceYear", "\"date\"", "1956"));
        Assert.assertEquals("((new java.text.SimpleDateFormat()).parse(\"date\").getTime() % 1000)", ctx.mapFunction("dateSecondsSinceMidnight", "\"date\""));
        Assert.assertEquals("(a == b)", ctx.mapFunction("equal", "a", "b"));
        Assert.assertEquals("(a != b)", ctx.mapFunction("notEqual", "a", "b"));
        Assert.assertEquals("(a < b)", ctx.mapFunction("lessThan", "a", "b"));
        Assert.assertEquals("(a <= b)", ctx.mapFunction("lessOrEqual", "a", "b"));
        Assert.assertEquals("(a > b)", ctx.mapFunction("greaterThan", "a", "b"));
        Assert.assertEquals("(a >= b)", ctx.mapFunction("greaterOrEqual", "a", "b"));
        Assert.assertEquals("(a.contains(b))", ctx.mapFunction("isIn", "a", "b"));
        Assert.assertEquals("((! a.contains(b)))", ctx.mapFunction("isNotIn", "a", "b"));
        Assert.assertEquals("(( ! a ))", ctx.mapFunction("not", "a"));
        Assert.assertEquals("(a && b && c)", ctx.mapFunction("and", "a", "b", "c"));
        Assert.assertEquals("(a || b)", ctx.mapFunction("or", "a", "b"));
        Assert.assertEquals("(a ? b : c)", ctx.mapFunction("if", "a", "b", "c"));
        Assert.assertEquals("(a ? b : null)", ctx.mapFunction("if", "a", "b"));
        checkGeneratedRules();
    }
}

