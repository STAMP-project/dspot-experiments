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
package org.apache.ibatis.submitted.collectionparameters;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class CollectionParametersTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetTwoUsersPassingAList() {
        SqlSession sqlSession = CollectionParametersTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(1);
            list.add(2);
            List<User> users = mapper.getUsersFromList(list);
            Assert.assertEquals(2, users.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetTwoUsersPassingAnArray() {
        SqlSession sqlSession = CollectionParametersTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            Integer[] list = new Integer[2];
            list[0] = 1;
            list[1] = 2;
            List<User> users = mapper.getUsersFromArray(list);
            Assert.assertEquals(2, users.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetTwoUsersPassingACollection() {
        SqlSession sqlSession = CollectionParametersTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            Set<Integer> list = new HashSet<Integer>();
            list.add(1);
            list.add(2);
            List<User> users = mapper.getUsersFromCollection(list);
            Assert.assertEquals(2, users.size());
        } finally {
            sqlSession.close();
        }
    }
}

