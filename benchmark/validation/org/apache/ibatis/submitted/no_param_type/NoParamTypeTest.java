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
package org.apache.ibatis.submitted.no_param_type;


import ExecutorType.BATCH;
import java.util.List;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class NoParamTypeTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldAcceptDifferentTypeInTheSameBatch() {
        SqlSession sqlSession = NoParamTypeTest.sqlSessionFactory.openSession(BATCH);
        try {
            NoParamTypeTest.ObjA a = new NoParamTypeTest.ObjA();
            a.setId(1);
            a.setName(111);
            sqlSession.insert("insertUser", a);
            NoParamTypeTest.ObjB b = new NoParamTypeTest.ObjB();
            b.setId(2);
            b.setName("222");
            sqlSession.insert("insertUser", b);
            List<BatchResult> batchResults = sqlSession.flushStatements();
            batchResults.clear();
            sqlSession.clearCache();
            sqlSession.commit();
            List<User> users = sqlSession.selectList("selectUser");
            Assert.assertEquals(2, users.size());
        } finally {
            sqlSession.close();
        }
    }

    public static class ObjA {
        private Integer id;

        private Integer name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getName() {
            return name;
        }

        public void setName(Integer name) {
            this.name = name;
        }
    }

    public static class ObjB {
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

