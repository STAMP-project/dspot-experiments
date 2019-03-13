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
package org.apache.ibatis.type;


import JdbcType.VARCHAR;
import JdbcType.VARCHAR.TYPE_CODE;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EnumTypeHandlerTest extends BaseTypeHandlerTest {
    enum MyEnum {

        ONE,
        TWO;}

    private static final TypeHandler<EnumTypeHandlerTest.MyEnum> TYPE_HANDLER = new EnumTypeHandler<EnumTypeHandlerTest.MyEnum>(EnumTypeHandlerTest.MyEnum.class);

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

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getString("column")).thenReturn("ONE");
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertEquals(EnumTypeHandlerTest.MyEnum.ONE, EnumTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetNullResultFromResultSet() throws Exception {
        Mockito.when(rs.getString("column")).thenReturn(null);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assert.assertEquals(null, EnumTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getString(1)).thenReturn("ONE");
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertEquals(EnumTypeHandlerTest.MyEnum.ONE, EnumTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Test
    public void shouldGetNullResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getString(1)).thenReturn(null);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assert.assertEquals(null, EnumTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

