/**
 * Copyright 2013 the original author or authors.
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
package org.apache.ibatis.submitted.associationtype;


import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class AssociationTypeTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldGetAUser() {
        SqlSession sqlSession = AssociationTypeTest.sqlSessionFactory.openSession();
        try {
            List<Map> results = sqlSession.selectList("getUser");
            for (Map r : results) {
                Assert.assertEquals(String.class, r.get("a1").getClass());
                Assert.assertEquals(String.class, r.get("a2").getClass());
            }
        } finally {
            sqlSession.close();
        }
    }
}

