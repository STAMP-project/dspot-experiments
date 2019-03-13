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


class EnumTypeHandlerTest extends BaseTypeHandlerTest {
    enum MyEnum {

        ONE,
        TWO;}

    private static final TypeHandler<EnumTypeHandlerTest.MyEnum> TYPE_HANDLER = new EnumTypeHandler<EnumTypeHandlerTest.MyEnum>(EnumTypeHandlerTest.MyEnum.class);

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        EnumTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, EnumTypeHandlerTest.MyEnum.ONE, null);
        Mockito.verify(ps).setString(1, "ONE");
    }

    @Test
    public void shouldSetNullParameter() throws Exception {
        EnumTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, null, VARCHAR);
        Mockito.verify(ps).setNull(1, TYPE_CODE);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getString("column")).thenReturn("ONE");
        Assertions.assertEquals(EnumTypeHandlerTest.MyEnum.ONE, EnumTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getString("column")).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getString(1)).thenReturn("ONE");
        Assertions.assertEquals(EnumTypeHandlerTest.MyEnum.ONE, EnumTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getString(1)).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getString(1)).thenReturn("ONE");
        Assertions.assertEquals(EnumTypeHandlerTest.MyEnum.ONE, EnumTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getString(1)).thenReturn(null);
        Assertions.assertNull(EnumTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

