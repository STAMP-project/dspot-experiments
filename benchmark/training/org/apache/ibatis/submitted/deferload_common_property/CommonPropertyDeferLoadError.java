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
package org.apache.ibatis.submitted.deferload_common_property;


import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class CommonPropertyDeferLoadError {
    private static SqlSessionFactory sqlSessionFactory;

    private static SqlSessionFactory lazyLoadSqlSessionFactory;

    @Test
    public void testDeferLoadAfterResultHandler() {
        SqlSession sqlSession = CommonPropertyDeferLoadError.sqlSessionFactory.openSession();
        try {
            class MyResultHandler implements ResultHandler {
                List<Child> children = new ArrayList<Child>();

                public void handleResult(ResultContext context) {
                    Child child = ((Child) (context.getResultObject()));
                    children.add(child);
                }
            }
            MyResultHandler myResultHandler = new MyResultHandler();
            sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", myResultHandler);
            for (Child child : myResultHandler.children) {
                Assert.assertNotNull(child.getFather());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDeferLoadDuringResultHandler() {
        SqlSession sqlSession = CommonPropertyDeferLoadError.sqlSessionFactory.openSession();
        try {
            class MyResultHandler implements ResultHandler {
                public void handleResult(ResultContext context) {
                    Child child = ((Child) (context.getResultObject()));
                    Assert.assertNotNull(child.getFather());
                }
            }
            sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", new MyResultHandler());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDeferLoadAfterResultHandlerWithLazyLoad() {
        SqlSession sqlSession = CommonPropertyDeferLoadError.lazyLoadSqlSessionFactory.openSession();
        try {
            class MyResultHandler implements ResultHandler {
                List<Child> children = new ArrayList<Child>();

                public void handleResult(ResultContext context) {
                    Child child = ((Child) (context.getResultObject()));
                    children.add(child);
                }
            }
            MyResultHandler myResultHandler = new MyResultHandler();
            sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", myResultHandler);
            for (Child child : myResultHandler.children) {
                Assert.assertNotNull(child.getFather());
            }
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testDeferLoadDuringResultHandlerWithLazyLoad() {
        SqlSession sqlSession = CommonPropertyDeferLoadError.lazyLoadSqlSessionFactory.openSession();
        try {
            class MyResultHandler implements ResultHandler {
                public void handleResult(ResultContext context) {
                    Child child = ((Child) (context.getResultObject()));
                    Assert.assertNotNull(child.getFather());
                }
            }
            sqlSession.select("org.apache.ibatis.submitted.deferload_common_property.ChildMapper.selectAll", new MyResultHandler());
        } finally {
            sqlSession.close();
        }
    }
}

