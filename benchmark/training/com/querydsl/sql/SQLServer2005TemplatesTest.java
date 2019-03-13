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


import Expressions.ONE;
import Ops.ADD;
import Ops.AND;
import Ops.BETWEEN;
import Ops.CONCAT;
import Ops.DIV;
import Ops.EQ;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LOE;
import Ops.LT;
import Ops.MOD;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.OR;
import Ops.SUB;
import SQLOps.NEXTVAL;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class SQLServer2005TemplatesTest extends AbstractSQLTemplatesTest {
    @Override
    @Test
    public void noFrom() {
        query.getMetadata().setProjection(ONE);
        Assert.assertEquals("select 1", query.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    @Override
    public void union() {
        NumberExpression<Integer> one = Expressions.ONE;
        NumberExpression<Integer> two = Expressions.TWO;
        NumberExpression<Integer> three = Expressions.THREE;
        Path<Integer> col1 = Expressions.path(Integer.class, "col1");
        Union union = query.union(SQLExpressions.select(one.as(col1)), SQLExpressions.select(two), SQLExpressions.select(three));
        Assert.assertEquals(("(select 1 as col1)\n" + ((("union\n" + "(select 2)\n") + "union\n") + "(select 3)")), union.toString());
    }

    @Test
    public void limit() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5);
        query.getMetadata().setProjection(SQLServer2005TemplatesTest.survey1.id);
        Assert.assertEquals("select top (?) survey1.ID from SURVEY survey1", query.toString());
    }

    @Test
    public void modifiers() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(3);
        query.orderBy(SQLServer2005TemplatesTest.survey1.id.asc());
        query.getMetadata().setProjection(SQLServer2005TemplatesTest.survey1.id);
        Assert.assertEquals(("select * from (" + ("   select survey1.ID, row_number() over (order by survey1.ID asc) as rn from SURVEY survey1) a " + "where rn > ? and rn <= ? order by rn")), query.toString());
    }

    @Test
    public void modifiers_noOrder() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(3);
        query.getMetadata().setProjection(SQLServer2005TemplatesTest.survey1.id);
        Assert.assertEquals(("select * from (" + ("   select survey1.ID, row_number() over (order by current_timestamp asc) as rn from SURVEY survey1) a " + "where rn > ? and rn <= ? order by rn")), query.toString());
    }

    @Test
    public void nextVal() {
        Operation<String> nextval = ExpressionUtils.operation(String.class, NEXTVAL, ConstantImpl.create("myseq"));
        Assert.assertEquals("myseq.nextval", new SQLSerializer(new Configuration(new SQLServerTemplates())).handle(nextval).toString());
    }

    @Test
    public void precedence() {
        // 1  ~ (Bitwise NOT)
        // 2  (Multiply), / (Division), % (Modulo)
        int p2 = getPrecedence(MULT, DIV, MOD);
        // 3 + (Positive), - (Negative), + (Add), (+ Concatenate), - (Subtract), & (Bitwise AND), ^ (Bitwise Exclusive OR), | (Bitwise OR)
        int p3 = getPrecedence(NEGATE, ADD, SUB, CONCAT);
        // 4 =, >, <, >=, <=, <>, !=, !>, !< (Comparison operators)
        int p4 = getPrecedence(EQ, GT, LT, GOE, LOE, NE);
        // 5 NOT
        int p5 = getPrecedence(NOT);
        // 6 AND
        int p6 = getPrecedence(AND);
        // 7 ALL, ANY, BETWEEN, IN, LIKE, OR, SOME
        int p7 = getPrecedence(BETWEEN, IN, LIKE, LIKE_ESCAPE, OR);
        // 8 = (Assignment)
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
    }
}

