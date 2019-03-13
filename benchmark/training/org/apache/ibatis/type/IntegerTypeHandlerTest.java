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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class IntegerTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Integer> TYPE_HANDLER = new IntegerTypeHandler();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        IntegerTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, 100, null);
        Mockito.verify(ps).setInt(1, 100);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(100);
        Assertions.assertEquals(Integer.valueOf(100), IntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Assertions.assertEquals(Integer.valueOf(0), IntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(IntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(100);
        Assertions.assertEquals(Integer.valueOf(100), IntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Assertions.assertEquals(Integer.valueOf(0), IntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(IntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(100);
        Assertions.assertEquals(Integer.valueOf(100), IntegerTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Assertions.assertEquals(Integer.valueOf(0), IntegerTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(IntegerTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

