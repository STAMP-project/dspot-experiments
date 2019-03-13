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
package org.apache.ibatis.submitted.foreach;


import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class ForEachTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetAUser() {
        SqlSession sqlSession = ForEachTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User testProfile = new User();
            testProfile.setId(2);
            User friendProfile = new User();
            friendProfile.setId(6);
            List<User> friendList = new ArrayList<User>();
            friendList.add(friendProfile);
            testProfile.setFriendList(friendList);
            User user = mapper.getUser(testProfile);
            Assert.assertEquals("User6", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldHandleComplexNullItem() {
        SqlSession sqlSession = ForEachTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user1 = new User();
            user1.setId(2);
            user1.setName("User2");
            List<User> users = new ArrayList<User>();
            users.add(user1);
            users.add(null);
            int count = mapper.countByUserList(users);
            Assert.assertEquals(1, count);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldHandleMoreComplexNullItem() {
        SqlSession sqlSession = ForEachTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user1 = new User();
            User bestFriend = new User();
            bestFriend.setId(5);
            user1.setBestFriend(bestFriend);
            List<User> users = new ArrayList<User>();
            users.add(user1);
            users.add(null);
            int count = mapper.countByBestFriend(users);
            Assert.assertEquals(1, count);
        } finally {
            sqlSession.close();
        }
    }
}

