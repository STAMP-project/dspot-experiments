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
package com.querydsl.sql;


import Expressions.FALSE;
import Expressions.ONE;
import Expressions.TRUE;
import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.DIV;
import Ops.EQ;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LT;
import Ops.MOD;
import Ops.MULT;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.Assert;
import org.junit.Test;


public class PostgreSQLTemplatesTest extends AbstractSQLTemplatesTest {
    @Test
    public void noFrom() {
        query.getMetadata().setProjection(ONE);
        Assert.assertEquals("select 1", query.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union() {
        NumberExpression<Integer> one = Expressions.ONE;
        NumberExpression<Integer> two = Expressions.TWO;
        NumberExpression<Integer> three = Expressions.THREE;
        Path<Integer> col1 = Expressions.path(Integer.class, "col1");
        Union union = query.union(SQLExpressions.select(one.as(col1)), SQLExpressions.select(two), SQLExpressions.select(three));
        Assert.assertEquals(("(select 1 as col1)\n" + ((("union\n" + "(select 2)\n") + "union\n") + "(select 3)")), union.toString());
    }

    @Test
    public void precedence() {
        // .    left    table/column name separator
        // ::    left    PostgreSQL-style typecast
        // [ ]    left    array element selection
        // + -    right    unary plus, unary minus
        int p0 = getPrecedence(NEGATE);
        // ^    left    exponentiation
        // * / %    left    multiplication, division, modulo
        int p1 = getPrecedence(MULT, DIV, MOD);
        // + -    left    addition, subtraction
        int p2 = getPrecedence(ADD, SUB);
        // IS         IS TRUE, IS FALSE, IS NULL, etc
        int p3 = getPrecedence(IS_NULL, IS_NOT_NULL);
        // ISNULL         test for null
        // NOTNULL         test for not null
        // (any other)    left    all other native and user-defined operators
        // IN         set membership
        int p4 = getPrecedence(IN);
        // BETWEEN         range containment
        int p5 = getPrecedence(BETWEEN);
        // OVERLAPS         time interval overlap
        // LIKE ILIKE SIMILAR         string pattern matching
        int p6 = getPrecedence(LIKE, LIKE_ESCAPE);
        // < >         less than, greater than
        int p7 = getPrecedence(LT, GT);
        // =    right    equality, assignment
        int p8 = getPrecedence(EQ);
        // NOT    right    logical negation
        int p9 = getPrecedence(NOT);
        // AND    left    logical conjunction
        int p10 = getPrecedence(AND);
        // OR    left    logical disjunction
        int p11 = getPrecedence(OR);
        Assert.assertTrue((p0 < p1));
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
        Assert.assertTrue((p7 < p8));
        Assert.assertTrue((p8 < p9));
        Assert.assertTrue((p9 < p10));
        Assert.assertTrue((p10 < p11));
    }

    @Test
    @Override
    public void booleanTemplate() {
        assertSerialized(Expressions.booleanPath("b").eq(TRUE), "b = true");
        assertSerialized(Expressions.booleanPath("b").eq(FALSE), "b = false");
        query.setUseLiterals(true);
        query.where(Expressions.booleanPath("b").eq(true));
        Assert.assertTrue(query.toString(), query.toString().endsWith("where b = true"));
    }
}

