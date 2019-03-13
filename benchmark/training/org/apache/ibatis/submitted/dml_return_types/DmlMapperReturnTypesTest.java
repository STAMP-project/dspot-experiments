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
package org.apache.ibatis.submitted.dml_return_types;


import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class DmlMapperReturnTypesTest {
    private static final String SQL = "org/apache/ibatis/submitted/dml_return_types/CreateDB.sql";

    private static final String XML = "org/apache/ibatis/submitted/dml_return_types/mybatis-config.xml";

    private static SqlSessionFactory sqlSessionFactory;

    private SqlSession sqlSession;

    private Mapper mapper;

    @Test
    public void updateShouldReturnVoid() {
        mapper.updateReturnsVoid(new User(1, "updateShouldReturnVoid"));
    }

    @Test
    public void shouldReturnPrimitiveInteger() {
        final int rows = mapper.updateReturnsPrimitiveInteger(new User(1, "shouldReturnPrimitiveInteger"));
        Assert.assertEquals(1, rows);
    }

    @Test
    public void shouldReturnInteger() {
        final Integer rows = mapper.updateReturnsInteger(new User(1, "shouldReturnInteger"));
        Assert.assertEquals(Integer.valueOf(1), rows);
    }

    @Test
    public void shouldReturnPrimitiveLong() {
        final long rows = mapper.updateReturnsPrimitiveLong(new User(1, "shouldReturnPrimitiveLong"));
        Assert.assertEquals(1L, rows);
    }

    @Test
    public void shouldReturnLong() {
        final Long rows = mapper.updateReturnsLong(new User(1, "shouldReturnLong"));
        Assert.assertEquals(Long.valueOf(1), rows);
    }

    @Test
    public void shouldReturnPrimitiveBoolean() {
        final boolean rows = mapper.updateReturnsPrimitiveBoolean(new User(1, "shouldReturnPrimitiveBoolean"));
        Assert.assertEquals(true, rows);
    }

    @Test
    public void shouldReturnBoolean() {
        final Boolean rows = mapper.updateReturnsBoolean(new User(1, "shouldReturnBoolean"));
        Assert.assertEquals(Boolean.TRUE, rows);
    }
}

