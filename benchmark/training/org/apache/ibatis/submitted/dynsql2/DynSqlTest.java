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
package org.apache.ibatis.submitted.dynsql2;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class DynSqlTest {
    protected static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testDynamicSelectWithTypeHandler() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            List<Name> names = new ArrayList<Name>();
            Name name = new Name();
            name.setFirstName("Fred");
            name.setLastName("Flintstone");
            names.add(name);
            name = new Name();
            name.setFirstName("Barney");
            name.setLastName("Rubble");
            names.add(name);
            Parameter parameter = new Parameter();
            parameter.setNames(names);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql2.dynamicSelectWithTypeHandler", parameter);
            Assert.assertTrue(((answer.size()) == 2));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleSelect() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            Map<String, Object> answer = ((Map<String, Object>) (sqlSession.selectOne("org.apache.ibatis.submitted.dynsql2.simpleSelect", 1)));
            Assert.assertEquals(answer.get("ID"), 1);
            Assert.assertEquals(answer.get("FIRSTNAME"), "Fred");
            Assert.assertEquals(answer.get("LASTNAME"), "Flintstone");
        } finally {
            sqlSession.close();
        }
    }
}

