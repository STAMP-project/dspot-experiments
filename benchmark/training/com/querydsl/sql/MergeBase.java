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


import AbstractSQLQuery.PARENT_CONTEXT;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.dml.SQLMergeClause;
import com.querydsl.sql.domain.QSurvey;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class MergeBase extends AbstractBaseTest {
    @Test
    @ExcludeIn({ H2, CUBRID, SQLSERVER })
    public void merge_with_keys() throws SQLException {
        ResultSet rs = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 7).set(Constants.survey.name, "Hello World").executeWithKeys();
        Assert.assertTrue(rs.next());
        Assert.assertTrue(((rs.getObject(1)) != null));
        rs.close();
    }

    @Test
    @ExcludeIn({ H2, CUBRID, SQLSERVER })
    public void merge_with_keys_listener() throws SQLException {
        final AtomicBoolean result = new AtomicBoolean();
        SQLListener listener = new SQLBaseListener() {
            @Override
            public void end(SQLListenerContext context) {
                result.set(true);
            }
        };
        SQLMergeClause clause = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 7).set(Constants.survey.name, "Hello World");
        clause.addListener(listener);
        ResultSet rs = clause.executeWithKeys();
        Assert.assertTrue(rs.next());
        Assert.assertTrue(((rs.getObject(1)) != null));
        rs.close();
        Assert.assertTrue(result.get());
    }

    @Test
    @IncludeIn(H2)
    public void merge_with_keys_and_subQuery() {
        Assert.assertEquals(1, insert(Constants.survey).set(Constants.survey.id, 6).set(Constants.survey.name, "H").execute());
        // keys + subquery
        QSurvey survey2 = new QSurvey("survey2");
        Assert.assertEquals(2, merge(Constants.survey).keys(Constants.survey.id).select(query().from(survey2).select(survey2.id.add(1), survey2.name, survey2.name2)).execute());
    }

    @Test
    @IncludeIn(H2)
    public void merge_with_keys_and_values() {
        // NOTE : doesn't work with composite merge implementation
        // keys + values
        Assert.assertEquals(1, merge(Constants.survey).keys(Constants.survey.id).values(5, "Hello World", "Hello").execute());
    }

    @Test
    public void merge_with_keys_columns_and_values() {
        // keys + columns + values
        Assert.assertEquals(1, merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 5).set(Constants.survey.name, "Hello World").execute());
    }

    @Test
    public void merge_with_keys_columns_and_values_using_null() {
        // keys + columns + values
        Assert.assertEquals(1, merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 5).set(Constants.survey.name, ((String) (null))).execute());
    }

    @Test
    @ExcludeIn({ CUBRID, DB2, DERBY, POSTGRESQL, SQLSERVER, TERADATA })
    public void merge_with_keys_Null_Id() throws SQLException {
        ResultSet rs = merge(Constants.survey).keys(Constants.survey.id).setNull(Constants.survey.id).set(Constants.survey.name, "Hello World").executeWithKeys();
        Assert.assertTrue(rs.next());
        Assert.assertTrue(((rs.getObject(1)) != null));
        rs.close();
    }

    @Test
    @ExcludeIn({ H2, CUBRID, SQLSERVER })
    public void merge_with_keys_Projected() throws SQLException {
        Assert.assertNotNull(merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 8).set(Constants.survey.name, "Hello you").executeWithKey(Constants.survey.id));
    }

    @Test
    @ExcludeIn({ H2, CUBRID, SQLSERVER })
    public void merge_with_keys_Projected2() throws SQLException {
        Path<Object> idPath = ExpressionUtils.path(Object.class, "id");
        Object id = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 9).set(Constants.survey.name, "Hello you").executeWithKey(idPath);
        Assert.assertNotNull(id);
    }

    @Test
    @IncludeIn(H2)
    public void mergeBatch() {
        SQLMergeClause merge = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 5).set(Constants.survey.name, "5").addBatch();
        Assert.assertEquals(1, merge.getBatchCount());
        Assert.assertFalse(merge.isEmpty());
        merge.keys(Constants.survey.id).set(Constants.survey.id, 6).set(Constants.survey.name, "6").addBatch();
        Assert.assertEquals(2, merge.getBatchCount());
        Assert.assertEquals(2, merge.execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("5")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("6")).fetchCount());
    }

    @Test
    @IncludeIn(H2)
    public void mergeBatch_templates() {
        SQLMergeClause merge = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 5).set(Constants.survey.name, Expressions.stringTemplate("'5'")).addBatch();
        merge.keys(Constants.survey.id).set(Constants.survey.id, 6).set(Constants.survey.name, Expressions.stringTemplate("'6'")).addBatch();
        Assert.assertEquals(2, merge.execute());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("5")).fetchCount());
        Assert.assertEquals(1L, query().from(Constants.survey).where(Constants.survey.name.eq("6")).fetchCount());
    }

    @Test
    @IncludeIn(H2)
    public void mergeBatch_with_subquery() {
        SQLMergeClause merge = merge(Constants.survey).keys(Constants.survey.id).columns(Constants.survey.id, Constants.survey.name).select(query().from(Constants.survey2).select(Constants.survey2.id.add(20), Constants.survey2.name)).addBatch();
        merge(Constants.survey).keys(Constants.survey.id).columns(Constants.survey.id, Constants.survey.name).select(query().from(Constants.survey2).select(Constants.survey2.id.add(40), Constants.survey2.name)).addBatch();
        Assert.assertEquals(1, merge.execute());
    }

    @Test
    @IncludeIn(H2)
    public void merge_with_templateExpression_in_batch() {
        SQLMergeClause merge = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 5).set(Constants.survey.name, Expressions.stringTemplate("'5'")).addBatch();
        Assert.assertEquals(1, merge.execute());
    }

    @Test
    public void merge_listener() {
        final AtomicInteger calls = new AtomicInteger(0);
        SQLListener listener = new SQLBaseListener() {
            @Override
            public void end(SQLListenerContext context) {
                if ((context.getData(PARENT_CONTEXT)) == null) {
                    calls.incrementAndGet();
                }
            }
        };
        SQLMergeClause clause = merge(Constants.survey).keys(Constants.survey.id).set(Constants.survey.id, 5).set(Constants.survey.name, "Hello World");
        clause.addListener(listener);
        Assert.assertEquals(1, clause.execute());
        Assert.assertEquals(1, calls.intValue());
    }
}

