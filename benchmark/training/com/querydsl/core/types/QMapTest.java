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
package com.querydsl.core.types;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class QMapTest {
    StringPath str1 = Expressions.stringPath("str1");

    StringPath str2 = Expressions.stringPath("str2");

    StringPath str3 = Expressions.stringPath("str3");

    StringPath str4 = Expressions.stringPath("str4");

    Expression<?>[] exprs1 = new Expression[]{ str1, str2 };

    Expression<?>[] exprs2 = new Expression[]{ str3, str4 };

    Concatenation concat = new Concatenation(str1, str2);

    @Test
    public void twoExpressions_getArgs() {
        Assert.assertEquals(Arrays.asList(str1, str2), getArgs());
    }

    @Test
    public void oneArray_getArgs() {
        Assert.assertEquals(Arrays.asList(str1, str2), getArgs());
    }

    @Test
    public void twoExpressionArrays_getArgs() {
        Assert.assertEquals(Arrays.asList(str1, str2, str3, str4), getArgs());
    }

    @Test
    public void nestedProjection_getArgs() {
        Assert.assertEquals(Arrays.asList(str1, str2), getArgs());
    }

    @Test
    public void nestedProjection_getArgs2() {
        Assert.assertEquals(Arrays.asList(str1, str2, str3), getArgs());
    }

    @Test
    public void nestedProjection_newInstance() {
        QMap expr = new QMap(concat);
        Assert.assertEquals("1234", FactoryExpressionUtils.wrap(expr).newInstance("12", "34").get(concat));
    }

    @Test
    public void nestedProjection_newInstance2() {
        QMap expr = new QMap(str1, str2, concat);
        Assert.assertEquals("1234", FactoryExpressionUtils.wrap(expr).newInstance("1", "2", "12", "34").get(concat));
    }

    @Test
    public void tuple_equals() {
        QMap expr = new QMap(str1, str2);
        Assert.assertEquals(expr.newInstance("str1", "str2"), expr.newInstance("str1", "str2"));
    }

    @Test
    public void tuple_hashCode() {
        QMap expr = new QMap(str1, str2);
        Assert.assertEquals(expr.newInstance("str1", "str2").hashCode(), expr.newInstance("str1", "str2").hashCode());
    }

    @Test
    public void null_value() {
        QMap expr = new QMap(str1, str2);
        Assert.assertNotNull(expr.newInstance("str1", null));
    }
}

