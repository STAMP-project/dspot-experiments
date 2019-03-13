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
package org.apache.ibatis.submitted.multidb;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class MultiDbTest {
    protected static SqlSessionFactory sqlSessionFactory;

    protected static SqlSessionFactory sqlSessionFactory2;

    @Test
    public void shouldExecuteHsqlQuery() {
        SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession();
        try {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select1(1);
            Assert.assertEquals("hsql", answer);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldExecuteCommonQuery() {
        SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession();
        try {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select2(1);
            Assert.assertEquals("common", answer);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldExecuteHsqlQueryWithDynamicIf() {
        SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession();
        try {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select3(1);
            Assert.assertEquals("hsql", answer);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldExecuteHsqlQueryWithInclude() {
        SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession();
        try {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select4(1);
            Assert.assertEquals("hsql", answer);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldInsertInCommonWithSelectKey() {
        SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession();
        try {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            mapper.insert(new User(2, "test"));
            String answer = mapper.select2(1);
            Assert.assertEquals("common", answer);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldInsertInCommonWithSelectKey2() {
        SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession();
        try {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            mapper.insert2(new User(2, "test"));
            String answer = mapper.select2(1);
            Assert.assertEquals("common", answer);
        } finally {
            sqlSession.close();
        }
    }
}

