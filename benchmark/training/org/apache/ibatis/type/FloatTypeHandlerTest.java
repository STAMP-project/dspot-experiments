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


class FloatTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Float> TYPE_HANDLER = new FloatTypeHandler();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        FloatTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, 100.0F, null);
        Mockito.verify(ps).setFloat(1, 100.0F);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getFloat("column")).thenReturn(100.0F);
        Assertions.assertEquals(Float.valueOf(100.0F), FloatTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.when(rs.getFloat("column")).thenReturn(0.0F);
        Assertions.assertEquals(Float.valueOf(0.0F), FloatTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getFloat("column")).thenReturn(0.0F);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(FloatTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getFloat(1)).thenReturn(100.0F);
        Assertions.assertEquals(Float.valueOf(100.0F), FloatTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.when(rs.getFloat(1)).thenReturn(0.0F);
        Assertions.assertEquals(Float.valueOf(0.0F), FloatTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getFloat(1)).thenReturn(0.0F);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(FloatTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getFloat(1)).thenReturn(100.0F);
        Assertions.assertEquals(Float.valueOf(100.0F), FloatTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.when(cs.getFloat(1)).thenReturn(0.0F);
        Assertions.assertEquals(Float.valueOf(0.0F), FloatTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getFloat(1)).thenReturn(0.0F);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(FloatTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

