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


import java.time.Year;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Eduardo Macarron
 */
class YearTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Year> TYPE_HANDLER = new YearTypeHandler();

    private static final Year INSTANT = Year.now();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        YearTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, YearTypeHandlerTest.INSTANT, null);
        Mockito.verify(ps).setInt(1, YearTypeHandlerTest.INSTANT.getValue());
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(YearTypeHandlerTest.INSTANT.getValue());
        Assertions.assertEquals(YearTypeHandlerTest.INSTANT, YearTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Assertions.assertEquals(Year.of(0), YearTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(YearTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(YearTypeHandlerTest.INSTANT.getValue());
        Assertions.assertEquals(YearTypeHandlerTest.INSTANT, YearTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Assertions.assertEquals(Year.of(0), YearTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(YearTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(YearTypeHandlerTest.INSTANT.getValue());
        Assertions.assertEquals(YearTypeHandlerTest.INSTANT, YearTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Assertions.assertEquals(Year.of(0), YearTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(YearTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

