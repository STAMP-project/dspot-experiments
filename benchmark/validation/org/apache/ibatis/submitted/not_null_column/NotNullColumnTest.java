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
package org.apache.ibatis.submitted.not_null_column;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class NotNullColumnTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testNotNullColumnWithChildrenNoFid() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdNoFid(1);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertEquals(2, test.getChildren().size());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenNoFid() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdNoFid(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenFid() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdFid(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenWithInternalResultMap() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdWithInternalResultMap(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenWithRefResultMap() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdWithRefResultMap(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenFidMultipleNullColumns() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdFidMultipleNullColumns(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenFidMultipleNullColumnsAndBrackets() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdFidMultipleNullColumnsAndBrackets(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testNotNullColumnWithoutChildrenFidWorkaround() {
        SqlSession sqlSession = NotNullColumnTest.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            Father test = fatherMapper.selectByIdFidWorkaround(2);
            Assert.assertNotNull(test);
            Assert.assertNotNull(test.getChildren());
            Assert.assertTrue(test.getChildren().isEmpty());
        } finally {
            sqlSession.close();
        }
    }
}

