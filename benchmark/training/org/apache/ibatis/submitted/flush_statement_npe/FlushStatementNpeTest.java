/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.flush_statement_npe;


import ExecutorType.BATCH;
import ExecutorType.REUSE;
import ExecutorType.SIMPLE;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;


public class FlushStatementNpeTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testSameUpdateAfterCommitSimple() {
        SqlSession sqlSession = FlushStatementNpeTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectById(1);
            person.setFirstName("Simone");
            // Execute first update then commit.
            personMapper.update(person);
            sqlSession.commit();
            // Execute same update a second time. This used to raise an NPE.
            personMapper.update(person);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSameUpdateAfterCommitReuse() {
        SqlSession sqlSession = FlushStatementNpeTest.sqlSessionFactory.openSession(REUSE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectById(1);
            person.setFirstName("Simone");
            // Execute first update then commit.
            personMapper.update(person);
            sqlSession.commit();
            // Execute same update a second time. This used to raise an NPE.
            personMapper.update(person);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSameUpdateAfterCommitBatch() {
        SqlSession sqlSession = FlushStatementNpeTest.sqlSessionFactory.openSession(BATCH);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectById(1);
            person.setFirstName("Simone");
            // Execute first update then commit.
            personMapper.update(person);
            sqlSession.commit();
            // Execute same update a second time. This used to raise an NPE.
            personMapper.update(person);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}

