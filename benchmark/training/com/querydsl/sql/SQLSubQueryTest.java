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


import Wildcard.all;
import Wildcard.count;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static SQLTemplates.DEFAULT;


public class SQLSubQueryTest {
    private static final QEmployee employee = QEmployee.employee;

    @Test(expected = IllegalArgumentException.class)
    public void unknownOperator() {
        Operator op = new Operator() {
            public String name() {
                return "unknownfn";
            }

            public String toString() {
                return name();
            }

            public Class<?> getType() {
                return Object.class;
            }
        };
        SQLQuery<?> query = new SQLQuery<Void>();
        query.from(SQLSubQueryTest.employee).where(Expressions.booleanOperation(op, SQLSubQueryTest.employee.id)).toString();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void list() {
        SubQueryExpression<?> subQuery = SQLExpressions.select(SQLSubQueryTest.employee.id, Expressions.constant("XXX"), SQLSubQueryTest.employee.firstname).from(SQLSubQueryTest.employee);
        List<? extends Expression<?>> exprs = getArgs();
        Assert.assertEquals(SQLSubQueryTest.employee.id, exprs.get(0));
        Assert.assertEquals(ConstantImpl.create("XXX"), exprs.get(1));
        Assert.assertEquals(SQLSubQueryTest.employee.firstname, exprs.get(2));
    }

    @Test
    public void list_entity() {
        QEmployee employee2 = new QEmployee("employee2");
        Expression<?> expr = SQLExpressions.select(SQLSubQueryTest.employee, employee2.id).from(SQLSubQueryTest.employee).innerJoin(SQLSubQueryTest.employee.superiorIdKey, employee2);
        SQLSerializer serializer = new SQLSerializer(new Configuration(DEFAULT));
        serializer.handle(expr);
        Assert.assertEquals(("(select EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID, employee2.ID as col__ID7\n" + (("from EMPLOYEE EMPLOYEE\n" + "inner join EMPLOYEE employee2\n") + "on EMPLOYEE.SUPERIOR_ID = employee2.ID)")), serializer.toString());
    }

    @Test
    public void in() {
        SubQueryExpression<Integer> ints = SQLExpressions.select(SQLSubQueryTest.employee.id).from(SQLSubQueryTest.employee);
        QEmployee.employee.id.in(ints);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void in_union() {
        SubQueryExpression<Integer> ints1 = SQLExpressions.select(SQLSubQueryTest.employee.id).from(SQLSubQueryTest.employee);
        SubQueryExpression<Integer> ints2 = SQLExpressions.select(SQLSubQueryTest.employee.id).from(SQLSubQueryTest.employee);
        QEmployee.employee.id.in(SQLExpressions.union(ints1, ints2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void in_union2() {
        SubQueryExpression<Integer> ints1 = SQLExpressions.select(SQLSubQueryTest.employee.id).from(SQLSubQueryTest.employee);
        SubQueryExpression<Integer> ints2 = SQLExpressions.select(SQLSubQueryTest.employee.id).from(SQLSubQueryTest.employee);
        QEmployee.employee.id.in(SQLExpressions.union(ints1, ints2));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void unique() {
        SubQueryExpression<?> subQuery = SQLExpressions.select(SQLSubQueryTest.employee.id, Expressions.constant("XXX"), SQLSubQueryTest.employee.firstname).from(SQLSubQueryTest.employee);
        List<? extends Expression<?>> exprs = getArgs();
        Assert.assertEquals(SQLSubQueryTest.employee.id, exprs.get(0));
        Assert.assertEquals(ConstantImpl.create("XXX"), exprs.get(1));
        Assert.assertEquals(SQLSubQueryTest.employee.firstname, exprs.get(2));
    }

    @Test
    public void complex() {
        // related to #584795
        QSurvey survey = new QSurvey("survey");
        QEmployee emp1 = new QEmployee("emp1");
        QEmployee emp2 = new QEmployee("emp2");
        SubQueryExpression<?> subQuery = SQLExpressions.select(survey.id, emp2.firstname).from(survey).innerJoin(emp1).on(survey.id.eq(emp1.id)).innerJoin(emp2).on(emp1.superiorId.eq(emp2.superiorId), emp1.firstname.eq(emp2.firstname));
        Assert.assertEquals(3, subQuery.getMetadata().getJoins().size());
    }

    @Test
    public void validate() {
        NumberPath<Long> operatorTotalPermits = Expressions.numberPath(Long.class, "operator_total_permits");
        QSurvey survey = new QSurvey("survey");
        // select survey.name, count(*) as operator_total_permits
        // from survey
        // where survey.name >= "A"
        // group by survey.name
        // order by operator_total_permits asc
        // limit 10
        Expression<?> e = SQLExpressions.select(survey.name, count.as(operatorTotalPermits)).from(survey).where(survey.name.goe("A")).groupBy(survey.name).orderBy(operatorTotalPermits.asc()).limit(10).as("top");
        SQLExpressions.select(all).from(e);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union1() {
        QSurvey survey = QSurvey.survey;
        SubQueryExpression<Integer> q1 = SQLExpressions.select(survey.id).from(survey);
        SubQueryExpression<Integer> q2 = SQLExpressions.select(survey.id).from(survey);
        SQLExpressions.union(q1, q2);
        SQLExpressions.union(q1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union1_with() {
        QSurvey survey1 = new QSurvey("survey1");
        QSurvey survey2 = new QSurvey("survey2");
        QSurvey survey3 = new QSurvey("survey3");
        SQLQuery<Void> query = new SQLQuery<Void>();
        query.with(survey1, SQLExpressions.select(all()).from(survey1));
        query.union(SQLExpressions.select(all()).from(survey2), SQLExpressions.select(all()).from(survey3));
        Assert.assertEquals(("with survey1 as (select survey1.NAME, survey1.NAME2, survey1.ID\n" + ((((("from SURVEY survey1)\n" + "(select survey2.NAME, survey2.NAME2, survey2.ID\n") + "from SURVEY survey2)\n") + "union\n") + "(select survey3.NAME, survey3.NAME2, survey3.ID\n") + "from SURVEY survey3)")), query.toString());
    }
}

