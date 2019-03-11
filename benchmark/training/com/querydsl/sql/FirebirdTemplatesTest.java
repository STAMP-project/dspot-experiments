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


import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.CONCAT;
import Ops.DIV;
import Ops.EQ;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LT;
import Ops.MATCHES;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import Ops.XNOR;
import Ops.XOR;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.Assert;
import org.junit.Test;


public class FirebirdTemplatesTest extends AbstractSQLTemplatesTest {
    @SuppressWarnings("unchecked")
    @Test
    @Override
    public void union() {
        NumberExpression<Integer> one = Expressions.ONE;
        NumberExpression<Integer> two = Expressions.TWO;
        NumberExpression<Integer> three = Expressions.THREE;
        Path<Integer> col1 = Expressions.numberPath(Integer.class, "col1");
        Union union = query.union(SQLExpressions.select(one.as(col1)), SQLExpressions.select(two), SQLExpressions.select(three));
        Assert.assertEquals(("select 1 as col1 from RDB$DATABASE\n" + ((("union\n" + "select 2 from RDB$DATABASE\n") + "union\n") + "select 3 from RDB$DATABASE")), union.toString());
    }

    @Test
    public void precedence() {
        // concat
        // *, /, +, -
        // comparison
        // NOT
        // AND
        // OR
        int p1 = getPrecedence(CONCAT);
        int p2 = getPrecedence(NEGATE);
        int p3 = getPrecedence(MULT, DIV);
        int p4 = getPrecedence(SUB, ADD);
        int p5 = getPrecedence(EQ, GOE, GT, LT, NE, IS_NULL, IS_NOT_NULL, MATCHES, IN, LIKE, LIKE_ESCAPE, BETWEEN);
        int p6 = getPrecedence(NOT);
        int p7 = getPrecedence(AND);
        int p8 = getPrecedence(XOR, XNOR);
        int p9 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
        Assert.assertTrue((p7 < p8));
        Assert.assertTrue((p8 < p9));
    }
}

