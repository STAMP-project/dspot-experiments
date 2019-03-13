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
package org.apache.ibatis.submitted.duplicate_statements;


import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class DuplicateStatementsTest {
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetAllUsers() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<User> users = mapper.getAllUsers();
            Assert.assertEquals(10, users.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void shouldGetFirstFourUsers() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<User> users = mapper.getAllUsers(new RowBounds(0, 4));
            Assert.assertEquals(4, users.size());
        } finally {
            sqlSession.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForDuplicateMethod() {
        sqlSessionFactory.getConfiguration().addMapper(AnnotatedMapperExtended.class);
    }
}

