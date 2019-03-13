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


import Position.START_OVERRIDE;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.domain.QDateTest;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import com.querydsl.sql.domain.QUuids;
import com.querydsl.sql.domain.QXmlTest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;


public class InsertBase extends AbstractBaseTest {
    // https://bitbucket.org/xerial/sqlite-jdbc/issue/133/prepstmtsetdate-int-date-calendar-seems
    @Test
    @ExcludeIn(SQLITE)
    public void insert_dates() {
        QDateTest dateTest = QDateTest.qDateTest;
        LocalDate localDate = new LocalDate(1978, 1, 2);
        Path<LocalDate> localDateProperty = ExpressionUtils.path(LocalDate.class, "DATE_TEST");
        Path<DateTime> dateTimeProperty = ExpressionUtils.path(DateTime.class, "DATE_TEST");
        SQLInsertClause insert = insert(dateTest);
        insert.set(localDateProperty, localDate);
        insert.execute();
        Tuple result = query().from(dateTest).select(dateTest.dateTest.year(), dateTest.dateTest.month(), dateTest.dateTest.dayOfMonth(), dateTimeProperty).fetchFirst();
        Assert.assertEquals(Integer.valueOf(1978), result.get(0, Integer.class));
        Assert.assertEquals(Integer.valueOf(1), result.get(1, Integer.class));
        Assert.assertEquals(Integer.valueOf(2), result.get(2, Integer.class));
        DateTime dateTime = result.get(dateTimeProperty);
        if ((target) == (CUBRID)) {
            // XXX Cubrid adds random milliseconds for some reason
            dateTime = dateTime.withMillisOfSecond(0);
        }
        Assert.assertEquals(localDate.toDateTimeAtStartOfDay(), dateTime);
    }

    @Test
    public void complex1() {
        // related to #584795
        QSurvey survey = new QSurvey("survey");
        QEmployee emp1 = new QEmployee("emp1");
        QEmployee emp2 = new QEmployee("emp2");
        SQLInsertClause insert = insert(survey);
        insert.columns(survey.id, survey.name);
        insert.select(SQLExpressions.select(survey.id, emp2.firstname).from(survey).innerJoin(emp1).on(survey.id.eq(emp1.id)).innerJoin(emp2).on(emp1.superiorId.eq(emp2.superiorId), emp1.firstname.eq(emp2.firstname)));
        Assert.assertEquals(0, insert.execute());
    }

    @Test
    public void insert_alternative_syntax() {
        // with columns
        Assert.assertEquals(1, insert(Constants.survey).set(Constants.survey.id, 3).set(Constants.survey.name, "Hello").execute());
    }

    @Test
    public void insert_batch() {
        SQLInsertClause insert = insert(Constants.survey).set(Constants.survey.id, 5).set(Constants.survey.name, "55").addBatch();
        Assert.assertEquals(1, insert.getBatchCount());
        insert.set(Constants.survey.id, 6).set(Constants.survey.name, "66").addBatch();
        Assert.assertEquals(2, insert.getBatchCount());
        Assert.assertEquals(2, insert.execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("55")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("66")).fetchCount());
    }

    @Test
    public void insert_batch_to_bulk() {
        SQLInsertClause insert = insert(Constants.survey);
        insert.setBatchToBulk(true);
        insert.set(Constants.survey.id, 5).set(Constants.survey.name, "55").addBatch();
        Assert.assertEquals(1, insert.getBatchCount());
        insert.set(Constants.survey.id, 6).set(Constants.survey.name, "66").addBatch();
        Assert.assertEquals(2, insert.getBatchCount());
        Assert.assertEquals(2, insert.execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("55")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("66")).fetchCount());
    }

    @Test
    public void insert_batch_Templates() {
        SQLInsertClause insert = insert(Constants.survey).set(Constants.survey.id, 5).set(Constants.survey.name, Expressions.stringTemplate("'55'")).addBatch();
        insert.set(Constants.survey.id, 6).set(Constants.survey.name, Expressions.stringTemplate("'66'")).addBatch();
        Assert.assertEquals(2, insert.execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("55")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("66")).fetchCount());
    }

    @Test
    public void insert_batch2() {
        SQLInsertClause insert = insert(Constants.survey).set(Constants.survey.id, 5).set(Constants.survey.name, "55").addBatch();
        insert.set(Constants.survey.id, 6).setNull(Constants.survey.name).addBatch();
        Assert.assertEquals(2, insert.execute());
    }

    @Test
    public void insert_null_with_columns() {
        Assert.assertEquals(1, insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).values(3, null).execute());
    }

    @Test
    @ExcludeIn({ DB2, DERBY })
    public void insert_null_without_columns() {
        Assert.assertEquals(1, insert(Constants.survey).values(4, null, null).execute());
    }

    @Test
    @ExcludeIn({ FIREBIRD, HSQLDB, DB2, DERBY, ORACLE })
    public void insert_without_values() {
        Assert.assertEquals(1, insert(Constants.survey).execute());
    }

    @Test
    @ExcludeIn(ORACLE)
    public void insert_nulls_in_batch() {
        // QFoo f= QFoo.foo;
        // SQLInsertClause sic = new SQLInsertClause(c, new H2Templates(), f);
        // sic.columns(f.c1,f.c2).values(null,null).addBatch();
        // sic.columns(f.c1,f.c2).values(null,1).addBatch();
        // sic.execute();
        SQLInsertClause sic = insert(Constants.survey);
        sic.columns(Constants.survey.name, Constants.survey.name2).values(null, null).addBatch();
        sic.columns(Constants.survey.name, Constants.survey.name2).values(null, "X").addBatch();
        Assert.assertEquals(2, sic.execute());
    }

    @Test
    public void insert_with_columns() {
        Assert.assertEquals(1, insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).values(3, "Hello").execute());
    }

    @Test
    @ExcludeIn({ CUBRID, SQLSERVER })
    public void insert_with_keys() throws SQLException {
        ResultSet rs = insert(Constants.survey).set(Constants.survey.name, "Hello World").executeWithKeys();
        Assert.assertTrue(rs.next());
        Assert.assertTrue(((rs.getObject(1)) != null));
        rs.close();
    }

    @Test
    @ExcludeIn({ CUBRID, SQLSERVER })
    public void insert_with_keys_listener() throws SQLException {
        final AtomicBoolean result = new AtomicBoolean();
        SQLListener listener = new SQLBaseListener() {
            @Override
            public void end(SQLListenerContext context) {
                result.set(true);
            }
        };
        SQLInsertClause clause = insert(Constants.survey).set(Constants.survey.name, "Hello World");
        clause.addListener(listener);
        ResultSet rs = clause.executeWithKeys();
        Assert.assertFalse(result.get());
        Assert.assertTrue(rs.next());
        Assert.assertTrue(((rs.getObject(1)) != null));
        rs.close();
        Assert.assertTrue(result.get());
    }

    @Test
    @ExcludeIn({ CUBRID, SQLSERVER })
    public void insert_with_keys_Projected() throws SQLException {
        Assert.assertNotNull(insert(Constants.survey).set(Constants.survey.name, "Hello you").executeWithKey(Constants.survey.id));
    }

    @Test
    @ExcludeIn({ CUBRID, SQLSERVER })
    public void insert_with_keys_Projected2() throws SQLException {
        Path<Object> idPath = ExpressionUtils.path(Object.class, "id");
        Object id = insert(Constants.survey).set(Constants.survey.name, "Hello you").executeWithKey(idPath);
        Assert.assertNotNull(id);
    }

    // http://sourceforge.net/tracker/index.php?func=detail&aid=3513432&group_id=280608&atid=2377440
    @Test
    public void insert_with_set() {
        Assert.assertEquals(1, insert(Constants.survey).set(Constants.survey.id, 5).set(Constants.survey.name, ((String) (null))).execute());
    }

    @Test
    @IncludeIn(MYSQL)
    @SkipForQuoted
    public void insert_with_special_options() {
        SQLInsertClause clause = insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).values(3, "Hello");
        clause.addFlag(START_OVERRIDE, "insert ignore into ");
        Assert.assertEquals("insert ignore into SURVEY (ID, NAME) values (?, ?)", clause.toString());
        Assert.assertEquals(1, clause.execute());
    }

    // too slow
    @Test
    @ExcludeIn(FIREBIRD)
    public void insert_with_subQuery() {
        int count = ((int) (query().from(Constants.survey).fetchCount()));
        Assert.assertEquals(count, insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).select(query().from(Constants.survey2).select(Constants.survey2.id.add(20), Constants.survey2.name)).execute());
    }

    @Test
    @ExcludeIn({ HSQLDB, CUBRID, DERBY, FIREBIRD })
    public void insert_with_subQuery2() {
        // insert into modules(name)
        // select 'MyModule'
        // where not exists
        // (select 1 from modules where modules.name = 'MyModule')
        Assert.assertEquals(1, insert(Constants.survey).set(Constants.survey.name, query().where(query().from(Constants.survey2).where(Constants.survey2.name.eq("MyModule")).notExists()).select(Expressions.constant("MyModule")).fetchFirst()).execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("MyModule")).fetchCount());
    }

    @Test
    @ExcludeIn({ HSQLDB, CUBRID, DERBY })
    public void insert_with_subQuery3() {
        // insert into modules(name)
        // select 'MyModule'
        // where not exists
        // (select 1 from modules where modules.name = 'MyModule')
        Assert.assertEquals(1, insert(Constants.survey).columns(Constants.survey.name).select(query().where(query().from(Constants.survey2).where(Constants.survey2.name.eq("MyModule2")).notExists()).select(Expressions.constant("MyModule2"))).execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("MyModule2")).fetchCount());
    }

    // too slow
    @Test
    @ExcludeIn(FIREBIRD)
    public void insert_with_subQuery_Params() {
        Param<Integer> param = new Param<Integer>(Integer.class, "param");
        SQLQuery<?> sq = query().from(Constants.survey2);
        sq.set(param, 20);
        int count = ((int) (query().from(Constants.survey).fetchCount()));
        Assert.assertEquals(count, insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).select(sq.select(Constants.survey2.id.add(param), Constants.survey2.name)).execute());
    }

    // too slow
    @Test
    @ExcludeIn(FIREBIRD)
    public void insert_with_subQuery_Via_Constructor() {
        int count = ((int) (query().from(Constants.survey).fetchCount()));
        SQLInsertClause insert = insert(Constants.survey, query().from(Constants.survey2));
        insert.set(Constants.survey.id, Constants.survey2.id.add(20));
        insert.set(Constants.survey.name, Constants.survey2.name);
        Assert.assertEquals(count, insert.execute());
    }

    // too slow
    @Test
    @ExcludeIn(FIREBIRD)
    public void insert_with_subQuery_Without_Columns() {
        int count = ((int) (query().from(Constants.survey).fetchCount()));
        Assert.assertEquals(count, insert(Constants.survey).select(query().from(Constants.survey2).select(Constants.survey2.id.add(10), Constants.survey2.name, Constants.survey2.name2)).execute());
    }

    // too slow
    @Test
    @ExcludeIn(FIREBIRD)
    public void insert_without_columns() {
        Assert.assertEquals(1, insert(Constants.survey).values(4, "Hello", "World").execute());
    }

    // too slow
    @Test
    @ExcludeIn(FIREBIRD)
    public void insertBatch_with_subquery() {
        SQLInsertClause insert = insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).select(query().from(Constants.survey2).select(Constants.survey2.id.add(20), Constants.survey2.name)).addBatch();
        insert(Constants.survey).columns(Constants.survey.id, Constants.survey.name).select(query().from(Constants.survey2).select(Constants.survey2.id.add(40), Constants.survey2.name)).addBatch();
        Assert.assertEquals(1, insert.execute());
    }

    @Test
    public void like() {
        insert(Constants.survey).values(11, "Hello World", "a\\b").execute();
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name2.contains("a\\b")).fetchCount());
    }

    @Test
    public void like_with_escape() {
        SQLInsertClause insert = insert(Constants.survey);
        insert.set(Constants.survey.id, 5).set(Constants.survey.name, "aaa").addBatch();
        insert.set(Constants.survey.id, 6).set(Constants.survey.name, "a_").addBatch();
        insert.set(Constants.survey.id, 7).set(Constants.survey.name, "a%").addBatch();
        Assert.assertEquals(3, insert.execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.like("a|%", '|')).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.like("a|_", '|')).fetchCount());
        Assert.assertEquals(3L, query().from(Constants.survey).where(Constants.survey.name.like("a%")).fetchCount());
        Assert.assertEquals(2L, query().from(Constants.survey).where(Constants.survey.name.like("a_")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.startsWith("a_")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.startsWith("a%")).fetchCount());
    }

    @Test
    @IncludeIn(MYSQL)
    @SkipForQuoted
    public void replace() {
        SQLInsertClause clause = mysqlReplace(Constants.survey);
        clause.columns(Constants.survey.id, Constants.survey.name).values(3, "Hello");
        Assert.assertEquals("replace into SURVEY (ID, NAME) values (?, ?)", clause.toString());
        clause.execute();
    }

    @Test
    public void insert_with_tempateExpression_in_batch() {
        Assert.assertEquals(1, insert(Constants.survey).set(Constants.survey.id, 3).set(Constants.survey.name, Expressions.stringTemplate("'Hello'")).addBatch().execute());
    }

    @Test
    @IncludeIn({ H2, POSTGRESQL })
    @SkipForQuoted
    public void uuids() {
        delete(QUuids.uuids).execute();
        QUuids uuids = QUuids.uuids;
        UUID uuid = UUID.randomUUID();
        insert(uuids).set(uuids.field, uuid).execute();
        Assert.assertEquals(uuid, query().from(uuids).select(uuids.field).fetchFirst());
    }

    @Test
    @ExcludeIn({ ORACLE })
    public void xml() {
        delete(QXmlTest.xmlTest).execute();
        QXmlTest xmlTest = QXmlTest.xmlTest;
        String contents = "<html><head>a</head><body>b</body></html>";
        insert(xmlTest).set(xmlTest.col, contents).execute();
        Assert.assertEquals(contents, query().from(xmlTest).select(xmlTest.col).fetchFirst());
    }
}

