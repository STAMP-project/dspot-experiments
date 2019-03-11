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
package com.querydsl.jpa;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.Assert;
import org.junit.Test;


public class CastTest extends AbstractQueryTest {
    private static NumberExpression<Integer> expr = Expressions.numberPath(Integer.class, "int");

    @Test
    public void bytes() {
        Assert.assertEquals(Byte.class, CastTest.expr.byteValue().getType());
    }

    @Test
    public void doubles() {
        Assert.assertEquals(Double.class, CastTest.expr.doubleValue().getType());
    }

    @Test
    public void floats() {
        Assert.assertEquals(Float.class, CastTest.expr.floatValue().getType());
    }

    @Test
    public void integers() {
        Assert.assertEquals(Integer.class, CastTest.expr.intValue().getType());
    }

    @Test
    public void longs() {
        Assert.assertEquals(Long.class, CastTest.expr.longValue().getType());
    }

    @Test
    public void shorts() {
        Assert.assertEquals(Short.class, CastTest.expr.shortValue().getType());
    }

    @Test
    public void stringCast() {
        Assert.assertEquals(String.class, CastTest.expr.stringValue().getType());
    }
}

