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
package org.apache.ibatis.submitted.sqlprovider;


import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class SqlProviderTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetTwoUsers() {
        SqlSession sqlSession = SqlProviderTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<Integer> list = new ArrayList<Integer>();
            list.add(1);
            list.add(3);
            List<User> users = mapper.getUsers(list);
            Assert.assertEquals(2, users.size());
            Assert.assertEquals("User1", users.get(0).getName());
            Assert.assertEquals("User3", users.get(1).getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetOneUser() {
        SqlSession sqlSession = SqlProviderTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUser(4);
            Assert.assertNotNull(user);
            Assert.assertEquals("User4", user.getName());
        } finally {
            sqlSession.close();
        }
    }
}

