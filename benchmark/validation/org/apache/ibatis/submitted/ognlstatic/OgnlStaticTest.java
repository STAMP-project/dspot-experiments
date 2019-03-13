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
package org.apache.ibatis.submitted.ognlstatic;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class OgnlStaticTest {
    private static SqlSessionFactory sqlSessionFactory;

    /**
     * This is the log output.
     * DEBUG [main] - ooo Using Connection [org.hsqldb.jdbc.JDBCConnection@5ae1a5c7]
     * DEBUG [main] - ==>  Preparing: SELECT * FROM users WHERE name IN (?) AND id = ?
     * DEBUG [main] - ==> Parameters: 1(Integer), 1(Integer)
     * There are two parameter mappings but DefaulParameterHandler maps them both to input paremeter (integer)
     */
    // see issue #448
    @Test
    public void shouldGetAUserStatic() {
        SqlSession sqlSession = OgnlStaticTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUserStatic(1);
            Assert.assertNotNull(user);
            Assert.assertEquals("User1", user.getName());
        } finally {
            sqlSession.close();
        }
    }

    // see issue #61 (gh)
    @Test
    public void shouldGetAUserWithIfNode() {
        SqlSession sqlSession = OgnlStaticTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            User user = mapper.getUserIfNode("User1");
            Assert.assertEquals("User1", user.getName());
        } finally {
            sqlSession.close();
        }
    }
}

