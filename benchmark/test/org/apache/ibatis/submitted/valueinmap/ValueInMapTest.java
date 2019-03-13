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
package org.apache.ibatis.submitted.valueinmap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class ValueInMapTest {
    private static SqlSessionFactory sqlSessionFactory;

    // issue #165
    @Test
    public void shouldWorkWithAPropertyNamedValue() {
        SqlSession sqlSession = ValueInMapTest.sqlSessionFactory.openSession();
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("table", "users");
            map.put("column", "name");
            map.put("value", "User1");
            Integer count = sqlSession.selectOne("count", map);
            Assert.assertEquals(new Integer(1), count);
        } finally {
            sqlSession.close();
        }
    }

    @Test(expected = PersistenceException.class)
    public void shouldWorkWithAList() {
        SqlSession sqlSession = ValueInMapTest.sqlSessionFactory.openSession();
        try {
            List<String> list = new ArrayList<String>();
            list.add("users");
            Integer count = sqlSession.selectOne("count2", list);
            Assert.assertEquals(new Integer(1), count);
        } finally {
            sqlSession.close();
        }
    }
}

