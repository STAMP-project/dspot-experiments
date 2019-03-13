/**
 * Copyright 2009-2018 the original author or authors.
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MultiDbTest {
    protected static SqlSessionFactory sqlSessionFactory;

    protected static SqlSessionFactory sqlSessionFactory2;

    @Test
    public void shouldExecuteHsqlQuery() {
        try (SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession()) {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select1(1);
            Assertions.assertEquals("hsql", answer);
        }
    }

    @Test
    public void shouldExecuteCommonQuery() {
        try (SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession()) {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select2(1);
            Assertions.assertEquals("common", answer);
        }
    }

    @Test
    public void shouldExecuteHsqlQueryWithDynamicIf() {
        try (SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession()) {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select3(1);
            Assertions.assertEquals("hsql", answer);
        }
    }

    @Test
    public void shouldExecuteHsqlQueryWithInclude() {
        try (SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession()) {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            String answer = mapper.select4(1);
            Assertions.assertEquals("hsql", answer);
        }
    }

    @Test
    public void shouldInsertInCommonWithSelectKey() {
        try (SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession()) {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            mapper.insert(new User(2, "test"));
            String answer = mapper.select2(1);
            Assertions.assertEquals("common", answer);
        }
    }

    @Test
    public void shouldInsertInCommonWithSelectKey2() {
        try (SqlSession sqlSession = MultiDbTest.sqlSessionFactory.openSession()) {
            MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
            mapper.insert2(new User(2, "test"));
            String answer = mapper.select2(1);
            Assertions.assertEquals("common", answer);
        }
    }
}

