/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.types.dsl;


import Ops.AND;
import Ops.DateTimeOps.CURRENT_DATE;
import Ops.DateTimeOps.CURRENT_TIME;
import Ops.DateTimeOps.CURRENT_TIMESTAMP;
import Ops.SUBSTR_1ARG;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Expressions;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.StringPath;
import com.querydsl.core.types.Template;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionsTest {
    private static final StringPath str = new StringPath("str");

    private static final BooleanExpression a = new BooleanPath("a");

    private static final BooleanExpression b = new BooleanPath("b");

    private enum testEnum {

        TEST;}

    private TimeZone timeZone = null;

    @Test
    public void Signature() throws NoSuchMethodException {
        List<String> types = ImmutableList.of("boolean", "comparable", "date", "dsl", "dateTime", "enum", "number", "simple", "string", "time");
        for (String type : types) {
            if ((type.equals("boolean")) || (type.equals("string"))) {
                assertReturnType(Expressions.class.getMethod((type + "Path"), String.class));
                assertReturnType(Expressions.class.getMethod((type + "Path"), Path.class, String.class));
                assertReturnType(Expressions.class.getMethod((type + "Path"), PathMetadata.class));
                assertReturnType(Expressions.class.getMethod((type + "Operation"), Operator.class, Expression[].class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), String.class, Object[].class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), String.class, ImmutableList.class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), String.class, List.class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Template.class, Object[].class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Template.class, ImmutableList.class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Template.class, List.class));
            } else {
                assertReturnType(Expressions.class.getMethod((type + "Path"), Class.class, String.class));
                assertReturnType(Expressions.class.getMethod((type + "Path"), Class.class, Path.class, String.class));
                assertReturnType(Expressions.class.getMethod((type + "Path"), Class.class, PathMetadata.class));
                assertReturnType(Expressions.class.getMethod((type + "Operation"), Class.class, Operator.class, Expression[].class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Class.class, String.class, Object[].class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Class.class, String.class, ImmutableList.class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Class.class, String.class, List.class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Class.class, Template.class, Object[].class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Class.class, Template.class, ImmutableList.class));
                assertReturnType(Expressions.class.getMethod((type + "Template"), Class.class, Template.class, List.class));
            }
        }
        // arrays
        assertReturnType(Expressions.class.getMethod("arrayPath", Class.class, String.class));
        assertReturnType(Expressions.class.getMethod("arrayPath", Class.class, Path.class, String.class));
        assertReturnType(Expressions.class.getMethod("arrayPath", Class.class, PathMetadata.class));
    }

    @Test
    public void as() {
        Assert.assertEquals("null as str", Expressions.as(null, ExpressionsTest.str).toString());
        Assert.assertEquals("s as str", Expressions.as(new StringPath("s"), ExpressionsTest.str).toString());
    }

    @Test
    public void allOf() {
        Assert.assertEquals("a && b", Expressions.allOf(ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void allOf_with_nulls() {
        Assert.assertEquals("a && b", Expressions.allOf(ExpressionsTest.a, ExpressionsTest.b, null).toString());
        Assert.assertEquals("a", Expressions.allOf(ExpressionsTest.a, null).toString());
        Assert.assertEquals("a", Expressions.allOf(null, ExpressionsTest.a).toString());
    }

    @Test
    public void anyOf() {
        Assert.assertEquals("a || b", Expressions.anyOf(ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void anyOf_with_nulls() {
        Assert.assertEquals("a || b", Expressions.anyOf(ExpressionsTest.a, ExpressionsTest.b, null).toString());
        Assert.assertEquals("a", Expressions.anyOf(ExpressionsTest.a, null).toString());
        Assert.assertEquals("a", Expressions.anyOf(null, ExpressionsTest.a).toString());
    }

    @Test
    public void constant() {
        Assert.assertEquals("X", Expressions.constant("X").toString());
    }

    @Test
    public void constant_as() {
        Assert.assertEquals("str as str", Expressions.constantAs("str", ExpressionsTest.str).toString());
    }

    @Test
    public void template() {
        Assert.assertEquals("a && b", Expressions.template(Object.class, "{0} && {1}", ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void comparableTemplate() {
        Assert.assertEquals("a && b", Expressions.comparableTemplate(Boolean.class, "{0} && {1}", ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void numberTemplate() {
        Assert.assertEquals("1", Expressions.numberTemplate(Integer.class, "1").toString());
    }

    @Test
    public void stringTemplate() {
        Assert.assertEquals("X", Expressions.stringTemplate("X").toString());
    }

    @Test
    public void booleanTemplate() {
        Assert.assertEquals("a && b", Expressions.booleanTemplate("{0} && {1}", ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void subQuery() {
        // TODO
    }

    @Test
    public void operation() {
        Assert.assertEquals("a && b", Expressions.operation(Boolean.class, AND, ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void predicate() {
        Assert.assertEquals("a && b", Expressions.predicate(AND, ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void pathClassOfTString() {
        Assert.assertEquals("variable", Expressions.path(String.class, "variable").toString());
    }

    @Test
    public void pathClassOfTPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.path(String.class, Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void comparablePathClassOfTString() {
        Assert.assertEquals("variable", Expressions.comparablePath(String.class, "variable").toString());
    }

    @Test
    public void comparablePathClassOfTPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.comparablePath(String.class, Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void datePathClassOfTString() {
        Assert.assertEquals("variable", Expressions.datePath(Date.class, "variable").toString());
    }

    @Test
    public void datePathClassOfTPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.datePath(Date.class, Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void dateTimePathClassOfTString() {
        Assert.assertEquals("variable", Expressions.dateTimePath(Date.class, "variable").toString());
    }

    @Test
    public void dateTimePathClassOfTPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.dateTimePath(Date.class, Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void timePathClassOfTString() {
        Assert.assertEquals("variable", Expressions.timePath(Date.class, "variable").toString());
    }

    @Test
    public void timePathClassOfTPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.timePath(Date.class, Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void numberPathClassOfTString() {
        Assert.assertEquals("variable", Expressions.numberPath(Integer.class, "variable").toString());
    }

    @Test
    public void numberPathClassOfTPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.numberPath(Integer.class, Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void stringPathString() {
        Assert.assertEquals("variable", Expressions.stringPath("variable").toString());
    }

    @Test
    public void stringPathPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.stringPath(Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void stringOperation() {
        Assert.assertEquals("substring(str,2)", Expressions.stringOperation(SUBSTR_1ARG, ExpressionsTest.str, ConstantImpl.create(2)).toString());
    }

    @Test
    public void booleanPathString() {
        Assert.assertEquals("variable", Expressions.booleanPath("variable").toString());
    }

    @Test
    public void booleanPathPathOfQString() {
        Assert.assertEquals("variable.property", Expressions.booleanPath(Expressions.path(Object.class, "variable"), "property").toString());
    }

    @Test
    public void booleanOperation() {
        Assert.assertEquals("a && b", Expressions.booleanOperation(AND, ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void comparableOperation() {
        Assert.assertEquals("a && b", Expressions.comparableOperation(Boolean.class, AND, ExpressionsTest.a, ExpressionsTest.b).toString());
    }

    @Test
    public void dateOperation() {
        Assert.assertEquals("current_date()", Expressions.dateOperation(Date.class, CURRENT_DATE).toString());
    }

    @Test
    public void dateTimeOperation() {
        Assert.assertEquals("current_timestamp()", Expressions.dateTimeOperation(Date.class, CURRENT_TIMESTAMP).toString());
    }

    @Test
    public void timeOperation() {
        Assert.assertEquals("current_time()", Expressions.timeOperation(Time.class, CURRENT_TIME).toString());
    }

    @Test
    public void cases() {
        // TODO
    }

    @Test
    public void asBoolean_returns_a_corresponding_BooleanExpression_for_a_given_Expression() {
        Assert.assertEquals("true = true", Expressions.asBoolean(Expressions.constant(true)).isTrue().toString());
    }

    @Test
    public void asBoolean_returns_a_corresponding_BooleanExpression_for_a_given_Constant() {
        Assert.assertEquals("true = true", Expressions.asBoolean(true).isTrue().toString());
    }

    @Test
    public void asComparable_returns_a_corresponding_ComparableExpression_for_a_given_Expression() {
        Assert.assertEquals("1 = 1", Expressions.asComparable(Expressions.constant(1L)).eq(Expressions.constant(1L)).toString());
    }

    @Test
    public void asComparable_returns_a_corresponding_ComparableExpression_for_a_given_Constant() {
        Assert.assertEquals("1 = 1", Expressions.asComparable(1L).eq(1L).toString());
    }

    @Test
    public void asDate_returns_a_corresponding_DateExpression_for_a_given_Expression() {
        Assert.assertEquals("year(Thu Jan 01 00:00:00 UTC 1970)", Expressions.asDate(Expressions.constant(new Date(1L))).year().toString());
    }

    @Test
    public void asDate_returns_a_corresponding_DateExpression_for_a_given_Constant() {
        Assert.assertEquals("year(Thu Jan 01 00:00:00 UTC 1970)", Expressions.asDate(new Date(1L)).year().toString());
    }

    @Test
    public void asDateTime_returns_a_corresponding_DateTimeExpression_for_a_given_Expression() {
        Assert.assertEquals("min(Thu Jan 01 00:00:00 UTC 1970)", Expressions.asDateTime(Expressions.constant(new Date(1L))).min().toString());
    }

    @Test
    public void asDateTime_returns_a_corresponding_DateTimeExpression_for_a_given_Constant() {
        Assert.assertEquals("min(Thu Jan 01 00:00:00 UTC 1970)", Expressions.asDateTime(new Date(1L)).min().toString());
    }

    @Test
    public void asTime_returns_a_corresponding_TimeExpression_for_a_given_Expression() {
        Assert.assertEquals("hour(Thu Jan 01 00:00:00 UTC 1970)", Expressions.asTime(Expressions.constant(new Date(1L))).hour().toString());
    }

    @Test
    public void asTime_returns_a_corresponding_TimeExpression_for_a_given_Constant() {
        Assert.assertEquals("hour(Thu Jan 01 00:00:00 UTC 1970)", Expressions.asTime(new Date(1L)).hour().toString());
    }

    @Test
    public void asEnum_returns_a_corresponding_EnumExpression_for_a_given_Expression() {
        Assert.assertEquals("ordinal(TEST)", Expressions.asEnum(Expressions.constant(ExpressionsTest.testEnum.TEST)).ordinal().toString());
    }

    @Test
    public void asEnum_returns_a_corresponding_EnumExpression_for_a_given_Constant() {
        Assert.assertEquals("ordinal(TEST)", Expressions.asEnum(ExpressionsTest.testEnum.TEST).ordinal().toString());
    }

    @Test
    public void asNumber_returns_a_corresponding_NumberExpression_for_a_given_Expression() {
        Assert.assertEquals("1 + 1", Expressions.asNumber(Expressions.constant(1L)).add(Expressions.constant(1L)).toString());
    }

    @Test
    public void asNumber_returns_a_corresponding_NumberExpression_for_a_given_Constant() {
        Assert.assertEquals("1 + 1", Expressions.asNumber(1L).add(Expressions.constant(1L)).toString());
    }

    @Test
    public void asString_returns_a_corresponding_StringExpression_for_a_given_Expression() {
        Assert.assertEquals("left + right", Expressions.asString(Expressions.constant("left")).append(Expressions.constant("right")).toString());
    }

    @Test
    public void asString_returns_a_corresponding_StringExpression_for_a_given_Constant() {
        Assert.assertEquals("left + right", Expressions.asString("left").append(Expressions.constant("right")).toString());
    }
}

