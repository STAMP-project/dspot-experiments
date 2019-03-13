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


import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class UpdateBase extends AbstractBaseTest {
    @Test
    public void update() throws SQLException {
        // original state
        long count = query().from(Constants.survey).fetchCount();
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.eq("S")).fetchCount());
        // update call with 0 update count
        Assert.assertEquals(0, update(Constants.survey).where(Constants.survey.name.eq("XXX")).set(Constants.survey.name, "S").execute());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.eq("S")).fetchCount());
        // update call with full update count
        Assert.assertEquals(count, update(Constants.survey).set(Constants.survey.name, "S").execute());
        Assert.assertEquals(count, query().from(Constants.survey).where(Constants.survey.name.eq("S")).fetchCount());
    }

    @Test
    @IncludeIn({ CUBRID, H2, MYSQL, ORACLE, SQLSERVER })
    public void update_limit() {
        Assert.assertEquals(1, insert(Constants.survey).values(2, "A", "B").execute());
        Assert.assertEquals(1, insert(Constants.survey).values(3, "B", "C").execute());
        Assert.assertEquals(2, update(Constants.survey).set(Constants.survey.name, "S").limit(2).execute());
    }

    @Test
    public void update2() throws SQLException {
        List<Path<?>> paths = Collections.<Path<?>>singletonList(Constants.survey.name);
        List<?> values = Collections.singletonList("S");
        // original state
        long count = query().from(Constants.survey).fetchCount();
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.eq("S")).fetchCount());
        // update call with 0 update count
        Assert.assertEquals(0, update(Constants.survey).where(Constants.survey.name.eq("XXX")).set(paths, values).execute());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.eq("S")).fetchCount());
        // update call with full update count
        Assert.assertEquals(count, update(Constants.survey).set(paths, values).execute());
        Assert.assertEquals(count, query().from(Constants.survey).where(Constants.survey.name.eq("S")).fetchCount());
    }

    @Test
    public void update3() {
        Assert.assertEquals(1, update(Constants.survey).set(Constants.survey.name, Constants.survey.name.append("X")).execute());
    }

    @Test
    public void update4() {
        Assert.assertEquals(1, insert(Constants.survey).values(2, "A", "B").execute());
        Assert.assertEquals(1, update(Constants.survey).set(Constants.survey.name, "AA").where(Constants.survey.name.eq("A")).execute());
    }

    @Test
    public void update5() {
        Assert.assertEquals(1, insert(Constants.survey).values(3, "B", "C").execute());
        Assert.assertEquals(1, update(Constants.survey).set(Constants.survey.name, "BB").where(Constants.survey.name.eq("B")).execute());
    }

    @Test
    public void setNull() {
        List<Path<?>> paths = Collections.<Path<?>>singletonList(Constants.survey.name);
        List<?> values = Collections.singletonList(null);
        long count = query().from(Constants.survey).fetchCount();
        Assert.assertEquals(count, update(Constants.survey).set(paths, values).execute());
    }

    @Test
    public void setNull2() {
        long count = query().from(Constants.survey).fetchCount();
        Assert.assertEquals(count, update(Constants.survey).set(Constants.survey.name, ((String) (null))).execute());
    }

    @Test
    @SkipForQuoted
    @ExcludeIn({ DERBY })
    public void setNullEmptyRootPath() {
        StringPath name = Expressions.stringPath("name");
        long count = query().from(Constants.survey).fetchCount();
        Assert.assertEquals(count, execute(update(Constants.survey).setNull(name)));
    }

    @Test
    public void batch() throws SQLException {
        Assert.assertEquals(1, insert(Constants.survey).values(2, "A", "B").execute());
        Assert.assertEquals(1, insert(Constants.survey).values(3, "B", "C").execute());
        SQLUpdateClause update = update(Constants.survey);
        update.set(Constants.survey.name, "AA").where(Constants.survey.name.eq("A")).addBatch();
        Assert.assertEquals(1, update.getBatchCount());
        update.set(Constants.survey.name, "BB").where(Constants.survey.name.eq("B")).addBatch();
        Assert.assertEquals(2, update.getBatchCount());
        Assert.assertEquals(2, update.execute());
    }

    @Test
    public void batch_templates() throws SQLException {
        Assert.assertEquals(1, insert(Constants.survey).values(2, "A", "B").execute());
        Assert.assertEquals(1, insert(Constants.survey).values(3, "B", "C").execute());
        SQLUpdateClause update = update(Constants.survey);
        update.set(Constants.survey.name, "AA").where(Constants.survey.name.eq(Expressions.stringTemplate("'A'"))).addBatch();
        update.set(Constants.survey.name, "BB").where(Constants.survey.name.eq(Expressions.stringTemplate("'B'"))).addBatch();
        Assert.assertEquals(2, update.execute());
    }

    @Test
    public void update_with_subQuery_exists() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        SQLUpdateClause update = update(survey1);
        update.set(survey1.name, "AA");
        update.where(SQLExpressions.selectOne().from(employee).where(survey1.id.eq(employee.id)).exists());
        Assert.assertEquals(1, update.execute());
    }

    @Test
    public void update_with_subQuery_exists_Params() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        Param<Integer> param = new Param<Integer>(Integer.class, "param");
        SQLQuery<?> sq = query().from(employee).where(employee.id.eq(param));
        sq.set(param, (-12478923));
        SQLUpdateClause update = update(survey1);
        update.set(survey1.name, "AA");
        update.where(sq.exists());
        Assert.assertEquals(0, update.execute());
    }

    @Test
    public void update_with_subQuery_exists2() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        SQLUpdateClause update = update(survey1);
        update.set(survey1.name, "AA");
        update.where(SQLExpressions.selectOne().from(employee).where(survey1.name.eq(employee.lastname)).exists());
        Assert.assertEquals(0, update.execute());
    }

    @Test
    public void update_with_subQuery_notExists() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        SQLUpdateClause update = update(survey1);
        update.set(survey1.name, "AA");
        update.where(query().from(employee).where(survey1.id.eq(employee.id)).notExists());
        Assert.assertEquals(0, update.execute());
    }

    @Test
    @ExcludeIn(TERADATA)
    public void update_with_templateExpression_in_batch() {
        Assert.assertEquals(1, update(Constants.survey).set(Constants.survey.id, 3).set(Constants.survey.name, Expressions.stringTemplate("'Hello'")).addBatch().execute());
    }
}

