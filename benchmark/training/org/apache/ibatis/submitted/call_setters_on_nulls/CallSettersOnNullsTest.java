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
package org.apache.ibatis.submitted.call_setters_on_nulls;


import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class CallSettersOnNullsTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldCallNullOnMappedProperty() {
        SqlSession sqlSession = CallSettersOnNullsTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUserMapped(1);
            Assert.assertTrue(user.nullReceived);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldCallNullOnAutomaticMapping() {
        SqlSession sqlSession = CallSettersOnNullsTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUserUnmapped(1);
            Assert.assertTrue(user.nullReceived);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldCallNullOnMap() {
        SqlSession sqlSession = CallSettersOnNullsTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            Map user = mapper.getUserInMap(1);
            Assert.assertTrue(user.containsKey("NAME"));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldCallNullOnMapForSingleColumn() {
        SqlSession sqlSession = CallSettersOnNullsTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<Map<String, Object>> oneColumns = mapper.getNameOnly();
            Assert.assertNotNull(oneColumns.get(1));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldCallNullOnMapForSingleColumnWithResultMap() {
        SqlSession sqlSession = CallSettersOnNullsTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<Map<String, Object>> oneColumns = mapper.getNameOnlyMapped();
            Assert.assertNotNull(oneColumns.get(1));
        } finally {
            sqlSession.close();
        }
    }
}

