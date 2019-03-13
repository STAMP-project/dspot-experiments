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


class LongTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Long> TYPE_HANDLER = new LongTypeHandler();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        LongTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, 100L, null);
        Mockito.verify(ps).setLong(1, 100L);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getLong("column")).thenReturn(100L);
        Assertions.assertEquals(Long.valueOf(100L), LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.when(rs.getLong("column")).thenReturn(0L);
        Assertions.assertEquals(Long.valueOf(0L), LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getLong("column")).thenReturn(0L);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getLong(1)).thenReturn(100L);
        Assertions.assertEquals(Long.valueOf(100L), LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.when(rs.getLong(1)).thenReturn(0L);
        Assertions.assertEquals(Long.valueOf(0L), LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getLong(1)).thenReturn(0L);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getLong(1)).thenReturn(100L);
        Assertions.assertEquals(Long.valueOf(100L), LongTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.when(cs.getLong(1)).thenReturn(0L);
        Assertions.assertEquals(Long.valueOf(0L), LongTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getLong(1)).thenReturn(0L);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(LongTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

