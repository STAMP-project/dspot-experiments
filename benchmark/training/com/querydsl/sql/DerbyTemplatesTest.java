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
import Expressions.TRUE;
import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.DIV;
import Ops.EQ;
import Ops.EXISTS;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LOE;
import Ops.LT;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import SQLOps.NEXTVAL;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Assert;
import org.junit.Test;


public class DerbyTemplatesTest extends AbstractSQLTemplatesTest {
    @Test
    public void nextVal() {
        Operation<String> nextval = ExpressionUtils.operation(String.class, NEXTVAL, ConstantImpl.create("myseq"));
        Assert.assertEquals("next value for myseq", new SQLSerializer(new Configuration(new DerbyTemplates())).handle(nextval).toString());
    }

    @Test
    public void precedence() {
        // unary + and -
        int p1 = getPrecedence(NEGATE);
        // *, /, || (concatenation)
        int p2 = getPrecedence(MULT, DIV);
        // binary + and -
        int p3 = getPrecedence(ADD, SUB);
        // comparisons, quantified comparisons, EXISTS, IN, IS NULL, LIKE, BETWEEN, IS
        int p4 = getPrecedence(EQ, NE, LT, GT, LOE, GOE, EXISTS, IN, IS_NULL, LIKE, BETWEEN, IS_NOT_NULL);
        // NOT
        int p5 = getPrecedence(NOT);
        // AND
        int p6 = getPrecedence(AND);
        // OR
        int p7 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
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

