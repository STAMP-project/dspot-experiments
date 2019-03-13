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


import com.querydsl.core.testutil.Derby;
import com.querydsl.sql.domain.QSurvey;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static DerbyTemplates.DEFAULT;


@Category(Derby.class)
public class QueryMutabilityTest {
    private static final QSurvey survey = new QSurvey("survey");

    private Connection connection;

    @Test
    public void test() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        SQLQuery<?> query = new SQLQuery<Void>(connection, DEFAULT);
        query.from(QueryMutabilityTest.survey);
        query.addListener(new TestLoggingListener());
        new com.querydsl.core.QueryMutability(query).test(QueryMutabilityTest.survey.id, QueryMutabilityTest.survey.name);
    }

    @Test
    public void clone_() {
        SQLQuery<?> query = new SQLQuery<Void>(DEFAULT).from(QueryMutabilityTest.survey);
        SQLQuery<?> query2 = query.clone(connection);
        Assert.assertEquals(query.getMetadata().getJoins(), query2.getMetadata().getJoins());
        Assert.assertEquals(query.getMetadata().getWhere(), query2.getMetadata().getWhere());
        query2.select(QueryMutabilityTest.survey.id).fetch();
    }
}

