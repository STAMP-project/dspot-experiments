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
package org.apache.ibatis.submitted.stringlist;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class StringListTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetAUser() {
        SqlSession sqlSession = StringListTest.sqlSessionFactory.openSession();
        try {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            List<User> users = mapper.getUsersAndGroups(1);
            Assert.assertEquals(1, users.size());
            Assert.assertEquals(2, users.get(0).getGroups().size());
            Assert.assertEquals(2, users.get(0).getRoles().size());
        } finally {
            sqlSession.close();
        }
    }
}

