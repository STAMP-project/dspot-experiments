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
package org.apache.ibatis.submitted.batch_keys;


import ExecutorType.BATCH;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class BatchKeysTest {
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void testInsert() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(BATCH);
        try {
            User user1 = new User(null, "Pocoyo");
            sqlSession.insert("insert", user1);
            User user2 = new User(null, "Valentina");
            sqlSession.insert("insert", user2);
            sqlSession.flushStatements();
            Assert.assertEquals(new Integer(50), user1.getId());
            Assert.assertEquals(new Integer(50), user2.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
        sqlSession = sqlSessionFactory.openSession();
        List<User> users = sqlSession.selectList("select");
        Assert.assertTrue(((users.size()) == 2));
    }

    @Test
    public void testInsertJdbc3() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession(BATCH);
        try {
            User user1 = new User(null, "Pocoyo");
            sqlSession.insert("insertIdentity", user1);
            User user2 = new User(null, "Valentina");
            sqlSession.insert("insertIdentity", user2);
            sqlSession.flushStatements();
            Assert.assertEquals(Integer.valueOf(0), user1.getId());
            Assert.assertEquals(Integer.valueOf(1), user2.getId());
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
        sqlSession = sqlSessionFactory.openSession();
        List<User> users = sqlSession.selectList("selectIdentity");
        Assert.assertTrue(((users.size()) == 2));
    }
}

