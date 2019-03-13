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
package org.apache.ibatis.submitted.force_flush_on_select;


import ExecutorType.SIMPLE;
import LocalCacheScope.STATEMENT;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class ForceFlushOnSelectTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testShouldFlushLocalSessionCacheOnQuery() throws SQLException {
        SqlSession sqlSession = ForceFlushOnSelectTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            personMapper.selectByIdFlush(1);
            updateDatabase(sqlSession.getConnection());
            Person updatedPerson = personMapper.selectByIdFlush(1);
            Assert.assertEquals("Simone", updatedPerson.getFirstName());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testShouldNotFlushLocalSessionCacheOnQuery() throws SQLException {
        SqlSession sqlSession = ForceFlushOnSelectTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            personMapper.selectByIdNoFlush(1);
            updateDatabase(sqlSession.getConnection());
            Person updatedPerson = personMapper.selectByIdNoFlush(1);
            Assert.assertEquals("John", updatedPerson.getFirstName());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testShouldFlushLocalSessionCacheOnQueryForList() throws SQLException {
        SqlSession sqlSession = ForceFlushOnSelectTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            List<Person> people = personMapper.selectAllFlush();
            updateDatabase(sqlSession.getConnection());
            people = personMapper.selectAllFlush();
            Assert.assertEquals("Simone", people.get(0).getFirstName());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testShouldNotFlushLocalSessionCacheOnQueryForList() throws SQLException {
        SqlSession sqlSession = ForceFlushOnSelectTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            List<Person> people = personMapper.selectAllNoFlush();
            updateDatabase(sqlSession.getConnection());
            people = personMapper.selectAllNoFlush();
            Assert.assertEquals("John", people.get(0).getFirstName());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateShouldFlushLocalCache() throws SQLException {
        SqlSession sqlSession = ForceFlushOnSelectTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            Person person = personMapper.selectByIdNoFlush(1);
            person.setLastName("Perez");// it is ignored in update

            personMapper.update(person);
            Person updatedPerson = personMapper.selectByIdNoFlush(1);
            Assert.assertEquals("Smith", updatedPerson.getLastName());
            Assert.assertNotSame(person, updatedPerson);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectShouldFlushLocalCacheIfFlushLocalCacheAtferEachStatementIsTrue() throws SQLException {
        ForceFlushOnSelectTest.sqlSessionFactory.getConfiguration().setLocalCacheScope(STATEMENT);
        SqlSession sqlSession = ForceFlushOnSelectTest.sqlSessionFactory.openSession(SIMPLE);
        try {
            PersonMapper personMapper = sqlSession.getMapper(PersonMapper.class);
            List<Person> people = personMapper.selectAllNoFlush();
            updateDatabase(sqlSession.getConnection());
            people = personMapper.selectAllFlush();
            Assert.assertEquals("Simone", people.get(0).getFirstName());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}

