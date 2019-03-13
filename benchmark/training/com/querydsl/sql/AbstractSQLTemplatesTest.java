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
import Expressions.THREE;
import Expressions.TRUE;
import Expressions.TWO;
import Ops.LIST;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.domain.QSurvey;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSQLTemplatesTest {
    protected static final QSurvey survey1 = new QSurvey("survey1");

    protected static final QSurvey survey2 = new QSurvey("survey2");

    private SQLTemplates templates;

    protected SQLQuery<?> query;

    @Test
    public void noFrom() {
        query.getMetadata().setProjection(ONE);
        if ((templates.getDummyTable()) == null) {
            Assert.assertEquals("select 1", query.toString());
        } else {
            Assert.assertEquals(("select 1 from " + (templates.getDummyTable())), query.toString());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union() {
        NumberExpression<Integer> one = Expressions.ONE;
        NumberExpression<Integer> two = Expressions.TWO;
        NumberExpression<Integer> three = Expressions.THREE;
        Path<Integer> col1 = Expressions.path(Integer.class, "col1");
        Union union = query.union(SQLExpressions.select(one.as(col1)), SQLExpressions.select(two), SQLExpressions.select(three));
        if ((templates.getDummyTable()) == null) {
            if (templates.isUnionsWrapped()) {
                Assert.assertEquals(("(select 1 as col1)\n" + ((("union\n" + "(select 2)\n") + "union\n") + "(select 3)")), union.toString());
            } else {
                Assert.assertEquals(("select 1 as col1)\n" + ((("union\n" + "select 2\n") + "union\n") + "select 3")), union.toString());
            }
        } else {
            String dummyTable = templates.getDummyTable();
            if (templates.isUnionsWrapped()) {
                Assert.assertEquals((((((((((("(select 1 as col1 from " + dummyTable) + ")\n") + "union\n") + "(select 2 from ") + dummyTable) + ")\n") + "union\n") + "(select 3 from ") + dummyTable) + ")"), union.toString());
            } else {
                Assert.assertEquals(((((((((("select 1 as col1 from " + dummyTable) + "\n") + "union\n") + "select 2 from ") + dummyTable) + "\n") + "union\n") + "select 3 from ") + dummyTable), union.toString());
            }
        }
    }

    @Test
    public void innerJoin() {
        query.from(AbstractSQLTemplatesTest.survey1).innerJoin(AbstractSQLTemplatesTest.survey2);
        Assert.assertEquals("from SURVEY survey1 inner join SURVEY survey2", query.toString());
    }

    @Test
    public void generic_precedence() {
        TemplatesTestUtils.testPrecedence(templates);
    }

    @Test
    public void arithmetic() {
        NumberExpression<Integer> one = Expressions.numberPath(Integer.class, "one");
        NumberExpression<Integer> two = Expressions.numberPath(Integer.class, "two");
        // add
        assertSerialized(one.add(two), "one + two");
        assertSerialized(one.add(two).multiply(1), "(one + two) * ?");
        assertSerialized(one.add(two).divide(1), "(one + two) / ?");
        assertSerialized(one.add(two).add(1), "one + two + ?");
        assertSerialized(one.add(two.multiply(1)), "one + two * ?");
        assertSerialized(one.add(two.divide(1)), "one + two / ?");
        assertSerialized(one.add(two.add(1)), "one + (two + ?)");// XXX could be better

        // sub
        assertSerialized(one.subtract(two), "one - two");
        assertSerialized(one.subtract(two).multiply(1), "(one - two) * ?");
        assertSerialized(one.subtract(two).divide(1), "(one - two) / ?");
        assertSerialized(one.subtract(two).add(1), "one - two + ?");
        assertSerialized(one.subtract(two.multiply(1)), "one - two * ?");
        assertSerialized(one.subtract(two.divide(1)), "one - two / ?");
        assertSerialized(one.subtract(two.add(1)), "one - (two + ?)");
        // mult
        assertSerialized(one.multiply(two), "one * two");
        assertSerialized(one.multiply(two).multiply(1), "one * two * ?");
        assertSerialized(one.multiply(two).divide(1), "one * two / ?");
        assertSerialized(one.multiply(two).add(1), "one * two + ?");
        assertSerialized(one.multiply(two.multiply(1)), "one * (two * ?)");// XXX could better

        assertSerialized(one.multiply(two.divide(1)), "one * (two / ?)");
        assertSerialized(one.multiply(two.add(1)), "one * (two + ?)");
    }

    @Test
    public void booleanTemplate() {
        assertSerialized(Expressions.booleanPath("b").eq(TRUE), "b = 1");
        assertSerialized(Expressions.booleanPath("b").eq(FALSE), "b = 0");
        query.setUseLiterals(true);
        query.where(Expressions.booleanPath("b").eq(true));
        Assert.assertTrue(query.toString(), query.toString().endsWith("where b = 1"));
    }

    @Test
    public void in() {
        CollectionExpression<Collection<Integer>, Integer> ints = Expressions.collectionOperation(Integer.class, LIST, Expressions.collectionOperation(Integer.class, LIST, ONE, TWO), THREE);
        query.from(AbstractSQLTemplatesTest.survey1).where(AbstractSQLTemplatesTest.survey1.id.in(ints));
        query.getMetadata().setProjection(AbstractSQLTemplatesTest.survey1.name);
        Assert.assertEquals("select survey1.NAME from SURVEY survey1 where survey1.ID in (1, 2, 3)", query.toString());
    }
}

