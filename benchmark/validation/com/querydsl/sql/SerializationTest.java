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


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import static SQLTemplates.DEFAULT;


public class SerializationTest {
    private static final QSurvey survey = QSurvey.survey;

    private final Connection connection = EasyMock.createMock(Connection.class);

    @Test
    public void innerJoin() {
        SQLQuery<?> query = new SQLQuery<Void>(connection, DEFAULT);
        query.from(new QSurvey("s1")).innerJoin(new QSurvey("s2"));
        Assert.assertEquals("from SURVEY s1\ninner join SURVEY s2", query.toString());
    }

    @Test
    public void leftJoin() {
        SQLQuery<?> query = new SQLQuery<Void>(connection, DEFAULT);
        query.from(new QSurvey("s1")).leftJoin(new QSurvey("s2"));
        Assert.assertEquals("from SURVEY s1\nleft join SURVEY s2", query.toString());
    }

    @Test
    public void rightJoin() {
        SQLQuery<?> query = new SQLQuery<Void>(connection, DEFAULT);
        query.from(new QSurvey("s1")).rightJoin(new QSurvey("s2"));
        Assert.assertEquals("from SURVEY s1\nright join SURVEY s2", query.toString());
    }

    @Test
    public void fullJoin() {
        SQLQuery<?> query = new SQLQuery<Void>(connection, DEFAULT);
        query.from(new QSurvey("s1")).fullJoin(new QSurvey("s2"));
        Assert.assertEquals("from SURVEY s1\nfull join SURVEY s2", query.toString());
    }

    @Test
    public void update() {
        SQLUpdateClause updateClause = new SQLUpdateClause(connection, DEFAULT, SerializationTest.survey);
        updateClause.set(SerializationTest.survey.id, 1);
        updateClause.set(SerializationTest.survey.name, ((String) (null)));
        Assert.assertEquals("update SURVEY\nset ID = ?, NAME = ?", updateClause.toString());
    }

    @Test
    public void update_where() {
        SQLUpdateClause updateClause = new SQLUpdateClause(connection, DEFAULT, SerializationTest.survey);
        updateClause.set(SerializationTest.survey.id, 1);
        updateClause.set(SerializationTest.survey.name, ((String) (null)));
        updateClause.where(SerializationTest.survey.name.eq("XXX"));
        Assert.assertEquals("update SURVEY\nset ID = ?, NAME = ?\nwhere SURVEY.NAME = ?", updateClause.toString());
    }

    @Test
    public void insert() {
        SQLInsertClause insertClause = new SQLInsertClause(connection, DEFAULT, SerializationTest.survey);
        insertClause.set(SerializationTest.survey.id, 1);
        insertClause.set(SerializationTest.survey.name, ((String) (null)));
        Assert.assertEquals("insert into SURVEY (ID, NAME)\nvalues (?, ?)", insertClause.toString());
    }

    @Test
    public void delete_with_subQuery_exists() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        SQLDeleteClause delete = new SQLDeleteClause(connection, DEFAULT, survey1);
        delete.where(survey1.name.eq("XXX"), selectOne().from(employee).where(survey1.id.eq(employee.id)).exists());
        Assert.assertEquals(("delete from SURVEY\n" + (("where SURVEY.NAME = ? and exists (select 1\n" + "from EMPLOYEE e\n") + "where SURVEY.ID = e.ID)")), delete.toString());
    }

    @Test
    public void nextval() {
        SubQueryExpression<?> sq = select(SQLExpressions.SQLExpressions.nextval("myseq")).from(QSurvey.survey);
        SQLSerializer serializer = new SQLSerializer(Configuration.DEFAULT);
        serializer.serialize(sq.getMetadata(), false);
        Assert.assertEquals("select nextval(\'myseq\')\nfrom SURVEY SURVEY", serializer.toString());
    }

    @Test
    public void functionCall() {
        RelationalFunctionCall<String> func = SQLExpressions.SQLExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
        PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
        SubQueryExpression<?> expr = select(SerializationTest.survey.name).from(SerializationTest.survey).join(func, funcAlias).on(SerializationTest.survey.name.like(funcAlias.getString("prop")).not());
        SQLSerializer serializer = new SQLSerializer(new Configuration(new SQLServerTemplates()));
        serializer.serialize(expr.getMetadata(), false);
        Assert.assertEquals(("select SURVEY.NAME\n" + (("from SURVEY SURVEY\n" + "join TableValuedFunction(?) as tokFunc\n") + "on not (SURVEY.NAME like tokFunc.prop escape \'\\\')")), serializer.toString());
    }

    @Test
    public void functionCall2() {
        RelationalFunctionCall<String> func = SQLExpressions.SQLExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
        PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
        SQLQuery<?> q = new SQLQuery<Void>(SQLServerTemplates.DEFAULT);
        q.from(SerializationTest.survey).join(func, funcAlias).on(SerializationTest.survey.name.like(funcAlias.getString("prop")).not());
        Assert.assertEquals(("from SURVEY SURVEY\n" + ("join TableValuedFunction(?) as tokFunc\n" + "on not (SURVEY.NAME like tokFunc.prop escape \'\\\')")), q.toString());
    }

    @Test
    public void functionCall3() {
        RelationalFunctionCall<String> func = SQLExpressions.SQLExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
        PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
        SQLQuery<?> q = new SQLQuery<Void>(HSQLDBTemplates.DEFAULT);
        q.from(SerializationTest.survey).join(func, funcAlias).on(SerializationTest.survey.name.like(funcAlias.getString("prop")).not());
        Assert.assertEquals(("from SURVEY SURVEY\n" + ("join table(TableValuedFunction(?)) as tokFunc\n" + "on not (SURVEY.NAME like tokFunc.prop escape \'\\\')")), q.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union1() {
        Expression<?> q = union(select(all()).from(SerializationTest.survey), select(all()).from(SerializationTest.survey));
        Assert.assertEquals(("(select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID\n" + ((("from SURVEY SURVEY)\n" + "union\n") + "(select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID\n") + "from SURVEY SURVEY)")), q.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union1_groupBy() {
        Expression<?> q = union(select(all()).from(SerializationTest.survey), select(all()).from(SerializationTest.survey)).groupBy(SerializationTest.survey.id);
        Assert.assertEquals(("(select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID\n" + (((("from SURVEY SURVEY)\n" + "union\n") + "(select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID\n") + "from SURVEY SURVEY)\n") + "group by SURVEY.ID")), q.toString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union2() {
        Expression<?> q = new SQLQuery<Void>().union(SerializationTest.survey, select(all()).from(SerializationTest.survey), select(all()).from(SerializationTest.survey));
        Assert.assertEquals(("from ((select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID\n" + ((("from SURVEY SURVEY)\n" + "union\n") + "(select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID\n") + "from SURVEY SURVEY)) as SURVEY")), q.toString());
    }

    @Test
    public void with() {
        QSurvey survey2 = new QSurvey("survey2");
        SQLQuery<?> q = new SQLQuery<Void>();
        q.with(SerializationTest.survey, SerializationTest.survey.id, SerializationTest.survey.name).as(select(survey2.id, survey2.name).from(survey2));
        Assert.assertEquals(("with SURVEY (ID, NAME) as (select survey2.ID, survey2.NAME\n" + ("from SURVEY survey2)\n\n" + "from dual")), q.toString());
    }

    @Test
    public void with_complex() {
        QSurvey s = new QSurvey("s");
        SQLQuery<?> q = new SQLQuery<Void>();
        q.with(s, s.id, s.name).as(select(SerializationTest.survey.id, SerializationTest.survey.name).from(SerializationTest.survey)).select(s.id, s.name, SerializationTest.survey.id, SerializationTest.survey.name).from(s, SerializationTest.survey);
        Assert.assertEquals(("with s (ID, NAME) as (select SURVEY.ID, SURVEY.NAME\n" + (("from SURVEY SURVEY)\n" + "select s.ID, s.NAME, SURVEY.ID, SURVEY.NAME\n") + "from s s, SURVEY SURVEY")), q.toString());
    }

    @Test
    public void with_tuple() {
        PathBuilder<Survey> survey = new PathBuilder<Survey>(Survey.class, "SURVEY");
        QSurvey survey2 = new QSurvey("survey2");
        SQLQuery<?> q = new SQLQuery<Void>();
        q.with(survey, survey.get(survey2.id), survey.get(survey2.name)).as(select(survey2.id, survey2.name).from(survey2));
        Assert.assertEquals(("with SURVEY (ID, NAME) as (select survey2.ID, survey2.NAME\n" + ("from SURVEY survey2)\n\n" + "from dual")), q.toString());
    }

    @Test
    public void with_tuple2() {
        QSurvey survey2 = new QSurvey("survey2");
        SQLQuery<?> q = new SQLQuery<Void>();
        q.with(SerializationTest.survey, SerializationTest.survey.id, SerializationTest.survey.name).as(select(survey2.id, survey2.name).from(survey2));
        Assert.assertEquals(("with SURVEY (ID, NAME) as (select survey2.ID, survey2.NAME\n" + ("from SURVEY survey2)\n\n" + "from dual")), q.toString());
    }

    @Test
    public void with_singleColumn() {
        QSurvey survey2 = new QSurvey("survey2");
        SQLQuery<?> q = new SQLQuery<Void>();
        q.with(SerializationTest.survey, new Path<?>[]{ SerializationTest.survey.id }).as(select(survey2.id).from(survey2));
        Assert.assertEquals(("with SURVEY (ID) as (select survey2.ID\n" + ("from SURVEY survey2)\n\n" + "from dual")), q.toString());
    }
}

