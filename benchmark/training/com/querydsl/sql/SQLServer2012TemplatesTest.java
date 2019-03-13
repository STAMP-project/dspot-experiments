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
import SQLOps.NEXTVAL;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class SQLServer2012TemplatesTest extends AbstractSQLTemplatesTest {
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
        query.getMetadata().setProjection(SQLServer2012TemplatesTest.survey1.id);
        Assert.assertEquals("select top 5 survey1.ID from SURVEY survey1", query.toString());
    }

    @Test
    public void limitOffset() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(5);
        query.getMetadata().setProjection(SQLServer2012TemplatesTest.survey1.id);
        Assert.assertEquals(("select survey1.ID from SURVEY survey1 " + ("order by 1 asc " + "offset ? rows fetch next ? rows only")), query.toString());
    }

    @Test
    public void delete_limit() {
        SQLDeleteClause clause = new SQLDeleteClause(null, createTemplates(), AbstractSQLTemplatesTest.survey1);
        clause.where(SQLServer2012TemplatesTest.survey1.name.eq("Bob"));
        clause.limit(5);
        Assert.assertEquals(("delete top 5 from SURVEY\n" + "where SURVEY.NAME = ?"), clause.toString());
    }

    @Test
    public void update_limit() {
        SQLUpdateClause clause = new SQLUpdateClause(null, createTemplates(), AbstractSQLTemplatesTest.survey1);
        clause.set(SQLServer2012TemplatesTest.survey1.name, "Bob");
        clause.limit(5);
        Assert.assertEquals(("update top 5 SURVEY\n" + "set NAME = ?"), clause.toString());
    }

    @Test
    public void modifiers() {
        query.from(AbstractSQLTemplatesTest.survey1).limit(5).offset(3).orderBy(SQLServer2012TemplatesTest.survey1.id.asc());
        query.getMetadata().setProjection(SQLServer2012TemplatesTest.survey1.id);
        Assert.assertEquals("select survey1.ID from SURVEY survey1 order by survey1.ID asc offset ? rows fetch next ? rows only", query.toString());
    }

    @Test
    public void nextVal() {
        Operation<String> nextval = ExpressionUtils.operation(String.class, NEXTVAL, ConstantImpl.create("myseq"));
        assertSerialized(nextval, "next value for myseq");
    }
}

