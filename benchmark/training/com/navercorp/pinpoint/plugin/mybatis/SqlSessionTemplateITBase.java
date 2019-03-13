/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.mybatis;


import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mybatis.spring.SqlSessionTemplate;


/**
 *
 *
 * @author HyunGil Jeong
 */
@Ignore
public class SqlSessionTemplateITBase extends SqlSessionTestBase {
    private static final ExecutorType EXECUTOR_TYPE = ExecutorType.SIMPLE;

    @Mock
    private SqlSessionFactory sqlSessionFactory;

    @Mock
    private SqlSession sqlSessionProxy;

    private SqlSessionTemplate sqlSessionTemplate;

    @Test
    public void methodCallWithNullSqlIdShouldOnlyTraceMethodName() throws Exception {
        super.testAndVerifyInsertWithNullParameter();
    }

    @Test
    public void selectShouldBeTraced() throws Exception {
        super.testAndVerifySelect();
    }

    @Test
    public void selectOneShouldBeTraced() throws Exception {
        super.testAndVerifySelectOne();
    }

    @Test
    public void selectListShouldBeTraced() throws Exception {
        super.testAndVerifySelectList();
    }

    @Test
    public void selectMapShouldBeTraced() throws Exception {
        super.testAndVerifySelectMap();
    }

    @Test
    public void insertShouldBeTraced() throws Exception {
        super.testAndVerifyInsert();
    }

    @Test
    public void updateShouldBeTraced() throws Exception {
        super.testAndVerifyUpdate();
    }

    @Test
    public void deleteShouldBeTraced() throws Exception {
        super.testAndVerifyDelete();
    }
}

