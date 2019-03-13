/**
 * Copyright 2012 MyBatis.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.submitted.language;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Just a test case. Not a real Velocity implementation.
 */
public class LanguageTest {
    protected static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testDynamicSelectWithPropertyParams() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            List<Name> answer = sqlSession.selectList("selectNames", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
            p = new Parameter(false, "Fli%");
            answer = sqlSession.selectList("selectNames", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertTrue(((n.getLastName()) == null));
            }
            p = new Parameter(false, "Rub%");
            answer = sqlSession.selectList("selectNames", p);
            Assert.assertEquals(2, answer.size());
            for (Name n : answer) {
                Assert.assertTrue(((n.getLastName()) == null));
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDynamicSelectWithExpressionParams() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli");
            List<Name> answer = sqlSession.selectList("selectNamesWithExpressions", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
            p = new Parameter(false, "Fli");
            answer = sqlSession.selectList("selectNamesWithExpressions", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertTrue(((n.getLastName()) == null));
            }
            p = new Parameter(false, "Rub");
            answer = sqlSession.selectList("selectNamesWithExpressions", p);
            Assert.assertEquals(2, answer.size());
            for (Name n : answer) {
                Assert.assertTrue(((n.getLastName()) == null));
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDynamicSelectWithIteration() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            int[] ids = new int[]{ 2, 4, 5 };
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("ids", ids);
            List<Name> answer = sqlSession.selectList("selectNamesWithIteration", param);
            Assert.assertEquals(3, answer.size());
            for (int i = 0; i < (ids.length); i++) {
                Assert.assertEquals(ids[i], answer.get(i).getId());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangRaw() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            List<Name> answer = sqlSession.selectList("selectRaw", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangRawWithInclude() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            List<Name> answer = sqlSession.selectList("selectRawWithInclude", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangRawWithIncludeAndCData() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            List<Name> answer = sqlSession.selectList("selectRawWithIncludeAndCData", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangXmlTags() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            List<Name> answer = sqlSession.selectList("selectXml", p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangRawWithMapper() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            Mapper m = sqlSession.getMapper(Mapper.class);
            List<Name> answer = m.selectRawWithMapper(p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangVelocityWithMapper() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            Mapper m = sqlSession.getMapper(Mapper.class);
            List<Name> answer = m.selectVelocityWithMapper(p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangXmlWithMapper() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            Mapper m = sqlSession.getMapper(Mapper.class);
            List<Name> answer = m.selectXmlWithMapper(p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLangXmlWithMapperAndSqlSymbols() {
        SqlSession sqlSession = LanguageTest.sqlSessionFactory.openSession();
        try {
            Parameter p = new Parameter(true, "Fli%");
            Mapper m = sqlSession.getMapper(Mapper.class);
            List<Name> answer = m.selectXmlWithMapperAndSqlSymbols(p);
            Assert.assertEquals(3, answer.size());
            for (Name n : answer) {
                Assert.assertEquals("Flintstone", n.getLastName());
            }
        } finally {
            sqlSession.close();
        }
    }
}

