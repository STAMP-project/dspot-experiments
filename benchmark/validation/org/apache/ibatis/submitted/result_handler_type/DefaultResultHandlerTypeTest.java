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
package org.apache.ibatis.submitted.result_handler_type;


import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class DefaultResultHandlerTypeTest {
    @Test
    public void testSelectList() throws Exception {
        String xmlConfig = "org/apache/ibatis/submitted/result_handler_type/MapperConfig.xml";
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.result_handler_type.PersonMapper.doSelect");
            Assert.assertEquals(list.size(), 2);
            Assert.assertEquals("java.util.LinkedList", list.getClass().getCanonicalName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectMap() throws Exception {
        String xmlConfig = "org/apache/ibatis/submitted/result_handler_type/MapperConfig.xml";
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Map<Integer, Person> map = sqlSession.selectMap("org.apache.ibatis.submitted.result_handler_type.PersonMapper.doSelect", "id");
            Assert.assertEquals(map.size(), 2);
            Assert.assertEquals("java.util.LinkedHashMap", map.getClass().getCanonicalName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectMapAnnotation() throws Exception {
        String xmlConfig = "org/apache/ibatis/submitted/result_handler_type/MapperConfig.xml";
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PersonMapper mapper = sqlSession.getMapper(PersonMapper.class);
            Map<Integer, Person> map = mapper.selectAsMap();
            Assert.assertEquals(map.size(), 2);
            Assert.assertEquals("java.util.LinkedHashMap", map.getClass().getCanonicalName());
        } finally {
            sqlSession.close();
        }
    }
}

