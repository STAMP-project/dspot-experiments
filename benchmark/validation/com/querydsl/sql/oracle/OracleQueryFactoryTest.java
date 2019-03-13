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
package com.querydsl.sql.oracle;


import com.querydsl.sql.domain.QSurvey;
import org.junit.Assert;
import org.junit.Test;


public class OracleQueryFactoryTest {
    private OracleQueryFactory queryFactory;

    @Test
    public void query() {
        Assert.assertNotNull(queryFactory.query());
    }

    @Test
    public void from() {
        Assert.assertNotNull(queryFactory.from(QSurvey.survey));
    }

    @Test
    public void delete() {
        Assert.assertNotNull(queryFactory.delete(QSurvey.survey));
    }

    @Test
    public void insert() {
        Assert.assertNotNull(queryFactory.insert(QSurvey.survey));
    }

    @Test
    public void update() {
        Assert.assertNotNull(queryFactory.update(QSurvey.survey));
    }

    @Test
    public void merge() {
        Assert.assertNotNull(queryFactory.merge(QSurvey.survey));
    }
}

