/**
 * Copyright 2012 the original author or authors.
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
package org.apache.ibatis.submitted.serializecircular;


import org.apache.ibatis.session.SqlSession;
import org.junit.Test;


// @Ignore("see issue #614")
public class SerializeCircularTest {
    @Test
    public void serializeAndDeserializeObjectsWithAggressiveLazyLoadingWithoutPreloadingAttribute() throws Exception {
        SqlSession sqlSession = createSessionWithAggressiveLazyLoading();
        try {
            testSerializeWithoutPreloadingAttribute(sqlSession);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void serializeAndDeserializeObjectsWithAggressiveLazyLoadingWithPreloadingAttribute() throws Exception {
        SqlSession sqlSession = createSessionWithAggressiveLazyLoading();
        try {
            testSerializeWithPreloadingAttribute(sqlSession);
        } finally {
            sqlSession.close();
        }
    }

    // @Ignore("See http://code.google.com/p/mybatis/issues/detail?id=614")
    @Test
    public void serializeAndDeserializeObjectsWithoutAggressiveLazyLoadingWithoutPreloadingAttribute() throws Exception {
        SqlSession sqlSession = createSessionWithoutAggressiveLazyLoading();
        try {
            // expected problem with deserializing
            testSerializeWithoutPreloadingAttribute(sqlSession);
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void serializeAndDeserializeObjectsWithoutAggressiveLazyLoadingWithPreloadingAttribute() throws Exception {
        SqlSession sqlSession = createSessionWithoutAggressiveLazyLoading();
        try {
            testSerializeWithPreloadingAttribute(sqlSession);
        } finally {
            sqlSession.close();
        }
    }
}

