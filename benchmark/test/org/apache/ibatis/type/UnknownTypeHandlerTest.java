/**
 * Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.type;


import JdbcType.VARCHAR.TYPE_CODE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class UnknownTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Object> TYPE_HANDLER = Mockito.spy(new UnknownTypeHandler(new TypeHandlerRegistry()));

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        UnknownTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, "Hello", null);
        Mockito.verify(ps).setString(1, "Hello");
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getMetaData()).thenReturn(rsmd);
        Mockito.when(rsmd.getColumnCount()).thenReturn(1);
        Mockito.when(rsmd.getColumnName(1)).thenReturn("column");
        Mockito.when(rsmd.getColumnClassName(1)).thenReturn(String.class.getName());
        Mockito.when(rsmd.getColumnType(1)).thenReturn(TYPE_CODE);
        Mockito.when(rs.getString("column")).thenReturn("Hello");
        Assertions.assertEquals("Hello", UnknownTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getMetaData()).thenReturn(rsmd);
        Mockito.when(rsmd.getColumnClassName(1)).thenReturn(String.class.getName());
        Mockito.when(rsmd.getColumnType(1)).thenReturn(TYPE_CODE);
        Mockito.when(rs.getString(1)).thenReturn("Hello");
        Assertions.assertEquals("Hello", UnknownTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getObject(1)).thenReturn("Hello");
        Assertions.assertEquals("Hello", UnknownTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getObject(1)).thenReturn(null);
        Assertions.assertNull(UnknownTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

