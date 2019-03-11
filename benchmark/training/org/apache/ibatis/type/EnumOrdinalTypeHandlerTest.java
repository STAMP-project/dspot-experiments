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


import JdbcType.VARCHAR;
import JdbcType.VARCHAR.TYPE_CODE;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class EnumOrdinalTypeHandlerTest extends BaseTypeHandlerTest {
    enum MyEnum {

        ONE,
        TWO;}

    private static final TypeHandler<EnumOrdinalTypeHandlerTest.MyEnum> TYPE_HANDLER = new EnumOrdinalTypeHandler<EnumOrdinalTypeHandlerTest.MyEnum>(EnumOrdinalTypeHandlerTest.MyEnum.class);

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        EnumOrdinalTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, EnumOrdinalTypeHandlerTest.MyEnum.ONE, null);
        Mockito.verify(ps).setInt(1, 0);
    }

    @Test
    public void shouldSetNullParameter() throws Exception {
        EnumOrdinalTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, null, VARCHAR);
        Mockito.verify(ps).setNull(1, TYPE_CODE);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assertions.assertEquals(EnumOrdinalTypeHandlerTest.MyEnum.ONE, EnumOrdinalTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(EnumOrdinalTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assertions.assertEquals(EnumOrdinalTypeHandlerTest.MyEnum.ONE, EnumOrdinalTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(EnumOrdinalTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assertions.assertEquals(EnumOrdinalTypeHandlerTest.MyEnum.ONE, EnumOrdinalTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(EnumOrdinalTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

