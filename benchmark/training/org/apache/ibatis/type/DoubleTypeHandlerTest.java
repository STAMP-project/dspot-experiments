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


class DoubleTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Double> TYPE_HANDLER = new DoubleTypeHandler();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        DoubleTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, 100.0, null);
        Mockito.verify(ps).setDouble(1, 100.0);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getDouble("column")).thenReturn(100.0);
        Assertions.assertEquals(Double.valueOf(100.0), DoubleTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.when(rs.getDouble("column")).thenReturn(0.0);
        Assertions.assertEquals(Double.valueOf(0.0), DoubleTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getDouble("column")).thenReturn(0.0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(DoubleTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getDouble(1)).thenReturn(100.0);
        Assertions.assertEquals(Double.valueOf(100.0), DoubleTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.when(rs.getDouble(1)).thenReturn(0.0);
        Assertions.assertEquals(Double.valueOf(0.0), DoubleTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getDouble(1)).thenReturn(0.0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(DoubleTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getDouble(1)).thenReturn(100.0);
        Assertions.assertEquals(Double.valueOf(100.0), DoubleTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.when(cs.getDouble(1)).thenReturn(0.0);
        Assertions.assertEquals(Double.valueOf(0.0), DoubleTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getDouble(1)).thenReturn(0.0);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(DoubleTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

