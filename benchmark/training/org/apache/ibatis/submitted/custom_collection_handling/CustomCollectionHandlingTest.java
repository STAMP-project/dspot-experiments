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
package org.apache.ibatis.submitted.custom_collection_handling;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class CustomCollectionHandlingTest {
    /**
     * Custom collections with nested resultMap.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSelectListWithNestedResultMap() throws Exception {
        String xmlConfig = "org/apache/ibatis/submitted/custom_collection_handling/MapperConfig.xml";
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.custom_collection_handling.PersonMapper.findWithResultMap");
            Assert.assertEquals(2, list.size());
            Assert.assertEquals(2, list.get(0).getContacts().size());
            Assert.assertEquals(1, list.get(1).getContacts().size());
            Assert.assertEquals("3 Wall Street", list.get(0).getContacts().get(1).getAddress());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * Custom collections with nested select.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSelectListWithNestedSelect() throws Exception {
        String xmlConfig = "org/apache/ibatis/submitted/custom_collection_handling/MapperConfig.xml";
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactoryXmlConfig(xmlConfig);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<Person> list = sqlSession.selectList("org.apache.ibatis.submitted.custom_collection_handling.PersonMapper.findWithSelect");
            Assert.assertEquals(2, list.size());
            Assert.assertEquals(2, list.get(0).getContacts().size());
            Assert.assertEquals(1, list.get(1).getContacts().size());
            Assert.assertEquals("3 Wall Street", list.get(0).getContacts().get(1).getAddress());
        } finally {
            sqlSession.close();
        }
    }
}

