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
package org.apache.ibatis.submitted.dynsql;


import java.math.BigDecimal;
import java.math.BigInteger;
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
    public void testSelect() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(1);
            ids.add(3);
            ids.add(5);
            Parameter parameter = new Parameter();
            parameter.setEnabled(true);
            parameter.setSchema("ibtest");
            parameter.setIds(ids);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.select", parameter);
            Assert.assertTrue(((answer.size()) == 3));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectSimple() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            List<Integer> ids = new ArrayList<Integer>();
            ids.add(1);
            ids.add(3);
            ids.add(5);
            Parameter parameter = new Parameter();
            parameter.setEnabled(true);
            parameter.setSchema("ibtest");
            parameter.setIds(ids);
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.select_simple", parameter);
            Assert.assertTrue(((answer.size()) == 3));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSelectLike() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.selectLike", "Ba");
            Assert.assertTrue(((answer.size()) == 2));
            Assert.assertEquals(new Integer(4), answer.get(0).get("ID"));
            Assert.assertEquals(new Integer(6), answer.get(1).get("ID"));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNumerics() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            List<NumericRow> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.selectNumerics");
            Assert.assertTrue(((answer.size()) == 1));
            NumericRow row = answer.get(0);
            Assert.assertEquals(1, ((int) (row.getId())));
            Assert.assertEquals(2, ((int) (row.getTinynumber())));
            Assert.assertEquals(3, ((int) (row.getSmallnumber())));
            Assert.assertEquals(4L, ((long) (row.getLonginteger())));
            Assert.assertEquals(new BigInteger("5"), row.getBiginteger());
            Assert.assertEquals(new BigDecimal("6.00"), row.getNumericnumber());
            Assert.assertEquals(new BigDecimal("7.00"), row.getDecimalnumber());
            Assert.assertEquals(((Float) (8.0F)), row.getRealnumber());
            Assert.assertEquals(((Float) (9.0F)), row.getFloatnumber());
            Assert.assertEquals(((Double) (10.0)), row.getDoublenumber());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testOgnlStaticMethodCall() {
        SqlSession sqlSession = DynSqlTest.sqlSessionFactory.openSession();
        try {
            List<Map<String, Object>> answer = sqlSession.selectList("org.apache.ibatis.submitted.dynsql.ognlStaticMethodCall", "Rock 'n Roll");
            Assert.assertTrue(((answer.size()) == 1));
            Assert.assertEquals(new Integer(7), answer.get(0).get("ID"));
        } finally {
            sqlSession.close();
        }
    }
}

