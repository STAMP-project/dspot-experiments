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


import java.time.YearMonth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Bj?rn Raupach
 */
class YearMonthTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<YearMonth> TYPE_HANDLER = new YearMonthTypeHandler();

    private static final YearMonth INSTANT = YearMonth.now();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        YearMonthTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, YearMonthTypeHandlerTest.INSTANT, null);
        Mockito.verify(ps).setString(1, YearMonthTypeHandlerTest.INSTANT.toString());
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getString("column")).thenReturn(YearMonthTypeHandlerTest.INSTANT.toString());
        Assertions.assertEquals(YearMonthTypeHandlerTest.INSTANT, YearMonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Assertions.assertNull(YearMonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getString(1)).thenReturn(YearMonthTypeHandlerTest.INSTANT.toString());
        Assertions.assertEquals(YearMonthTypeHandlerTest.INSTANT, YearMonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Assertions.assertNull(YearMonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getString(1)).thenReturn(YearMonthTypeHandlerTest.INSTANT.toString());
        Assertions.assertEquals(YearMonthTypeHandlerTest.INSTANT, YearMonthTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Assertions.assertNull(YearMonthTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

