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
package org.apache.ibatis.submitted.substitution_in_annots;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class SubstitutionInAnnotsTest {
    protected static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testSubstitutionWithXml() {
        SqlSession sqlSession = SubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
            Assert.assertEquals("Barney", mapper.getPersonNameByIdWithXml(4));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSubstitutionWithAnnotsValue() {
        SqlSession sqlSession = SubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
            Assert.assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsValue(4));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSubstitutionWithAnnotsParameter() {
        SqlSession sqlSession = SubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
            Assert.assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParameter(4));
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSubstitutionWithAnnotsParamAnnot() {
        SqlSession sqlSession = SubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(SubstitutionInAnnotsMapper.class);
            Assert.assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParamAnnot(4));
        } finally {
            sqlSession.close();
        }
    }
}

