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
package org.apache.ibatis.submitted.lazyload_common_property;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;


public class CommonPropertyLazyLoadError {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void testLazyLoadWithNoAncestor() {
        SqlSession sqlSession = CommonPropertyLazyLoadError.sqlSessionFactory.openSession();
        try {
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            childMapper.selectById(1);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLazyLoadWithFirstAncestor() {
        SqlSession sqlSession = CommonPropertyLazyLoadError.sqlSessionFactory.openSession();
        try {
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            fatherMapper.selectById(1);
            childMapper.selectById(1);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLazyLoadWithAllAncestors() {
        SqlSession sqlSession = CommonPropertyLazyLoadError.sqlSessionFactory.openSession();
        try {
            GrandFatherMapper grandFatherMapper = sqlSession.getMapper(GrandFatherMapper.class);
            FatherMapper fatherMapper = sqlSession.getMapper(FatherMapper.class);
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            grandFatherMapper.selectById(1);
            fatherMapper.selectById(1);
            childMapper.selectById(1);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testLazyLoadSkipFirstAncestor() {
        SqlSession sqlSession = CommonPropertyLazyLoadError.sqlSessionFactory.openSession();
        try {
            GrandFatherMapper grandFatherMapper = sqlSession.getMapper(GrandFatherMapper.class);
            ChildMapper childMapper = sqlSession.getMapper(ChildMapper.class);
            grandFatherMapper.selectById(1);
            childMapper.selectById(1);
        } finally {
            sqlSession.close();
        }
    }
}

