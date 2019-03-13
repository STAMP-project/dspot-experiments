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
package org.apache.ibatis.submitted.nested;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class NestedForEachTest {
    protected static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testSimpleSelect() {
        SqlSession sqlSession = NestedForEachTest.sqlSessionFactory.openSession();
        try {
            Name name = new Name();
            name.setLastName("Flintstone");
            Parameter parameter = new Parameter();
            parameter.addName(name);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.nested.Mapper.simpleSelect", parameter);
            Assert.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSimpleSelectWithPrimitives() {
        SqlSession sqlSession = NestedForEachTest.sqlSessionFactory.openSession();
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            int[] array = new int[]{ 1, 3, 5 };
            parameter.put("ids", array);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.nested.Mapper.simpleSelectWithPrimitives", parameter);
            Assert.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSimpleSelectWithMapperAndPrimitives() {
        SqlSession sqlSession = NestedForEachTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<Map<String, Object>> answer = mapper.simpleSelectWithMapperAndPrimitives(1, 3, 5);
            Assert.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNestedSelect() {
        SqlSession sqlSession = NestedForEachTest.sqlSessionFactory.openSession();
        try {
            Name name = new Name();
            name.setLastName("Flintstone");
            name.addFirstName("Fred");
            name.addFirstName("Wilma");
            Parameter parameter = new Parameter();
            parameter.addName(name);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.nested.Mapper.nestedSelect", parameter);
            Assert.assertEquals(2, answer.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNestedSelect2() {
        SqlSession sqlSession = NestedForEachTest.sqlSessionFactory.openSession();
        try {
            Name name = new Name();
            name.setLastName("Flintstone");
            name.addFirstName("Fred");
            name.addFirstName("Wilma");
            Parameter parameter = new Parameter();
            parameter.addName(name);
            name = new Name();
            name.setLastName("Rubble");
            name.addFirstName("Betty");
            parameter.addName(name);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.nested.Mapper.nestedSelect", parameter);
            Assert.assertEquals(3, answer.size());
        } finally {
            sqlSession.close();
        }
    }
}

