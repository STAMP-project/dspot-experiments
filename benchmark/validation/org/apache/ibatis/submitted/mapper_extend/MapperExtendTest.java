/**
 * Copyright 2009-2013 the original author or authors.
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
package org.apache.ibatis.submitted.mapper_extend;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class MapperExtendTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetAUserWithAnExtendedXMLMethod() {
        SqlSession sqlSession = MapperExtendTest.sqlSessionFactory.openSession();
        try {
            ParentMapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUserXML();
            Assert.assertEquals("User1", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetAUserWithAnExtendedAnnotatedMethod() {
        SqlSession sqlSession = MapperExtendTest.sqlSessionFactory.openSession();
        try {
            ParentMapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUserAnnotated();
            Assert.assertEquals("User1", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetAUserWithAnOverloadedXMLMethod() {
        SqlSession sqlSession = MapperExtendTest.sqlSessionFactory.openSession();
        try {
            ParentMapper mapper = sqlSession.getMapper(MapperOverload.class);
            User user = mapper.getUserXML();
            Assert.assertEquals("User2", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetAUserWithAnOverloadedAnnotatedMethod() {
        SqlSession sqlSession = MapperExtendTest.sqlSessionFactory.openSession();
        try {
            ParentMapper mapper = sqlSession.getMapper(MapperOverload.class);
            User user = mapper.getUserAnnotated();
            Assert.assertEquals("User2", user.getName());
        } finally {
            sqlSession.close();
        }
    }
}

