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
import Ops.EXISTS;
import Ops.GOE;
import Ops.GT;
import Ops.IN;
import Ops.IS_NOT_NULL;
import Ops.IS_NULL;
import Ops.LIKE;
import Ops.LIKE_ESCAPE;
import Ops.LOE;
import Ops.LT;
import Ops.MULT;
import Ops.NE;
import Ops.NEGATE;
import Ops.NOT;
import Ops.NOT_IN;
import Ops.OR;
import Ops.SUB;
import QueryFlag.Position;
import SQLOps.NEXTVAL;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.domain.QSurvey;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OracleTemplatesTest extends AbstractSQLTemplatesTest {
    @Override
    @SuppressWarnings("unchecked")
    @Test
    public void union() {
        SimpleExpression<Integer> one = Expressions.template(Integer.class, "1");
        SimpleExpression<Integer> two = Expressions.template(Integer.class, "2");
        SimpleExpression<Integer> three = Expressions.template(Integer.class, "3");
        NumberPath<Integer> col1 = Expressions.numberPath(Integer.class, "col1");
        Union union = query.union(SQLExpressions.select(one.as(col1)), SQLExpressions.select(two), SQLExpressions.select(three));
        Assert.assertEquals(("(select 1 col1 from dual)\n" + ((("union\n" + "(select 2 from dual)\n") + "union\n") + "(select 3 from dual)")), union.toString());
    }

    @Test
    public void modifiers() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(3);
        query.getMetadata().setProjection(OracleTemplatesTest.survey1.id);
        Assert.assertEquals(("select * from (  " + ((("select a.*, rownum rn from (   " + "select survey1.ID from SURVEY survey1  ) ") + "a) ") + "where rn > 3 and rownum <= 5")), query.toString());
    }

    @Test
    public void modifiers2() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(3);
        query.getMetadata().setProjection(OracleTemplatesTest.survey1.id);
        query.getMetadata().addFlag(new com.querydsl.core.QueryFlag(Position.AFTER_PROJECTION, ", count(*) over() "));
        Assert.assertEquals(("select * from (  " + ((("select a.*, rownum rn from (   " + "select survey1.ID, count(*) over()  from SURVEY survey1  ) ") + "a) ") + "where rn > 3 and rownum <= 5")), query.toString());
    }

    @Test
    public void in() {
        List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < 2000; i++) {
            ids.add(i);
        }
        query.where(OracleTemplatesTest.survey1.id.isNotNull());
        query.where(OracleTemplatesTest.survey1.id.in(ids));
        Assert.assertTrue(query.toString().startsWith("from dual where survey1.ID is not null and (survey1.ID in "));
    }

    @Test
    public void nextVal() {
        Operation<String> nextval = ExpressionUtils.operation(String.class, NEXTVAL, ConstantImpl.create("myseq"));
        Assert.assertEquals("myseq.nextval", new SQLSerializer(new Configuration(new OracleTemplates())).handle(nextval).toString());
    }

    @Test
    public void precedence() {
        // +, - (as unary operators), PRIOR, CONNECT_BY_ROOT  identity, negation, location in hierarchy
        int p1 = getPrecedence(NEGATE);
        // *, / multiplication, division
        int p2 = getPrecedence(MULT, DIV);
        // +, - (as binary operators), || addition, subtraction, concatenation
        int p3 = getPrecedence(ADD, SUB, CONCAT);
        // =, !=, <, >, <=, >=, comparison
        int p4 = getPrecedence(EQ, NE, LT, GT, LOE, GOE);
        // IS [NOT] NULL, LIKE, [NOT] BETWEEN, [NOT] IN, EXISTS, IS OF type comparison
        int p5 = getPrecedence(IS_NULL, IS_NOT_NULL, LIKE, LIKE_ESCAPE, BETWEEN, IN, NOT_IN, EXISTS);
        // NOT exponentiation, logical negation
        int p6 = getPrecedence(NOT);
        // AND conjunction
        int p7 = getPrecedence(AND);
        // OR disjunction
        int p8 = getPrecedence(OR);
        Assert.assertTrue((p1 < p2));
        Assert.assertTrue((p2 < p3));
        Assert.assertTrue((p3 < p4));
        Assert.assertTrue((p4 < p5));
        Assert.assertTrue((p5 < p6));
        Assert.assertTrue((p6 < p7));
        Assert.assertTrue((p7 < p8));
    }
}

