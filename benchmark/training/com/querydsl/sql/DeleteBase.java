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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;


public class DeleteBase extends AbstractBaseTest {
    @Test
    public void batch() throws SQLException {
        insert(Constants.survey).values(2, "A", "B").execute();
        insert(Constants.survey).values(3, "B", "C").execute();
        SQLDeleteClause delete = delete(Constants.survey);
        delete.where(Constants.survey.name.eq("A")).addBatch();
        Assert.assertEquals(1, delete.getBatchCount());
        delete.where(Constants.survey.name.eq("B")).addBatch();
        Assert.assertEquals(2, delete.getBatchCount());
        Assert.assertEquals(2, delete.execute());
    }

    @Test
    @ExcludeIn({ CUBRID, SQLITE })
    public void batch_templates() throws SQLException {
        insert(Constants.survey).values(2, "A", "B").execute();
        insert(Constants.survey).values(3, "B", "C").execute();
        SQLDeleteClause delete = delete(Constants.survey);
        delete.where(Constants.survey.name.eq(Expressions.stringTemplate("'A'"))).addBatch();
        delete.where(Constants.survey.name.eq(Expressions.stringTemplate("'B'"))).addBatch();
        Assert.assertEquals(2, delete.execute());
    }

    @Test
    @ExcludeIn(MYSQL)
    public void delete() throws SQLException {
        long count = query().from(Constants.survey).fetchCount();
        Assert.assertEquals(0, delete(Constants.survey).where(Constants.survey.name.eq("XXX")).execute());
        Assert.assertEquals(count, delete(Constants.survey).execute());
    }

    @Test
    @IncludeIn({ CUBRID, H2, MYSQL, ORACLE, SQLSERVER })
    public void delete_limit() {
        insert(Constants.survey).values(2, "A", "B").execute();
        insert(Constants.survey).values(3, "B", "C").execute();
        insert(Constants.survey).values(4, "D", "E").execute();
        Assert.assertEquals(2, delete(Constants.survey).limit(2).execute());
    }

    @Test
    public void delete_with_subQuery_exists() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        SQLDeleteClause delete = delete(survey1);
        delete.where(survey1.name.eq("XXX"), query().from(employee).where(survey1.id.eq(employee.id)).exists());
        Assert.assertEquals(0, delete.execute());
    }

    @Test
    public void delete_with_subQuery_exists_Params() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        Param<Integer> param = new Param<Integer>(Integer.class, "param");
        SQLQuery<?> sq = query().from(employee).where(employee.id.eq(param));
        sq.set(param, (-12478923));
        SQLDeleteClause delete = delete(survey1);
        delete.where(survey1.name.eq("XXX"), sq.exists());
        Assert.assertEquals(0, delete.execute());
    }

    @Test
    public void delete_with_subQuery_exists2() {
        QSurvey survey1 = new QSurvey("s1");
        QEmployee employee = new QEmployee("e");
        SQLDeleteClause delete = delete(survey1);
        delete.where(survey1.name.eq("XXX"), query().from(employee).where(survey1.name.eq(employee.lastname)).exists());
        Assert.assertEquals(0, delete.execute());
    }

    @Test
    @ExcludeIn({ CUBRID, SQLITE })
    public void delete_with_tempateExpression_in_batch() {
        Assert.assertEquals(1, delete(Constants.survey).where(Constants.survey.name.eq(Expressions.stringTemplate("'Hello World'"))).addBatch().execute());
    }
}

