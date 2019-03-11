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


import java.sql.Array;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


class ArrayTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Object> TYPE_HANDLER = new ArrayTypeHandler();

    @Mock
    Array mockArray;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        ArrayTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, mockArray, null);
        Mockito.verify(ps).setArray(1, mockArray);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getArray("column")).thenReturn(mockArray);
        String[] stringArray = new String[]{ "a", "b" };
        Mockito.when(mockArray.getArray()).thenReturn(stringArray);
        Assertions.assertEquals(stringArray, ArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(mockArray).free();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getArray("column")).thenReturn(null);
        Assertions.assertNull(ArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getArray(1)).thenReturn(mockArray);
        String[] stringArray = new String[]{ "a", "b" };
        Mockito.when(mockArray.getArray()).thenReturn(stringArray);
        Assertions.assertEquals(stringArray, ArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(mockArray).free();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getArray(1)).thenReturn(null);
        Assertions.assertNull(ArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getArray(1)).thenReturn(mockArray);
        String[] stringArray = new String[]{ "a", "b" };
        Mockito.when(mockArray.getArray()).thenReturn(stringArray);
        Assertions.assertEquals(stringArray, ArrayTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(mockArray).free();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getArray(1)).thenReturn(null);
        Assertions.assertNull(ArrayTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

