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


import java.time.Month;
import org.apache.ibatis.executor.result.ResultMapException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Eduardo Macarron
 */
class MonthTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Month> TYPE_HANDLER = new MonthTypeHandler();

    private static final Month INSTANT = Month.JANUARY;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        MonthTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, MonthTypeHandlerTest.INSTANT, null);
        Mockito.verify(ps).setInt(1, MonthTypeHandlerTest.INSTANT.getValue());
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(MonthTypeHandlerTest.INSTANT.getValue());
        Assertions.assertEquals(MonthTypeHandlerTest.INSTANT, MonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.when(rs.getInt("column")).thenReturn(0);
        try {
            MonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column");
            Assertions.fail();
        } catch (ResultMapException e) {
            Assertions.assertEquals("Error attempting to get column 'column' from result set.  Cause: java.time.DateTimeException: Invalid value for MonthOfYear: 0", e.getMessage());
        }
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getInt("column")).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(MonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(MonthTypeHandlerTest.INSTANT.getValue());
        Assertions.assertEquals(MonthTypeHandlerTest.INSTANT, MonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.when(rs.getInt(1)).thenReturn(0);
        try {
            MonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1);
            Assertions.fail();
        } catch (ResultMapException e) {
            Assertions.assertEquals("Error attempting to get column #1 from result set.  Cause: java.time.DateTimeException: Invalid value for MonthOfYear: 0", e.getMessage());
        }
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getInt(1)).thenReturn(0);
        Mockito.when(rs.wasNull()).thenReturn(true);
        Assertions.assertNull(MonthTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(MonthTypeHandlerTest.INSTANT.getValue());
        Assertions.assertEquals(MonthTypeHandlerTest.INSTANT, MonthTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.when(cs.getInt(1)).thenReturn(0);
        try {
            MonthTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1);
            Assertions.fail();
        } catch (ResultMapException e) {
            Assertions.assertEquals("Error attempting to get column #1 from callable statement.  Cause: java.time.DateTimeException: Invalid value for MonthOfYear: 0", e.getMessage());
        }
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getInt(1)).thenReturn(0);
        Mockito.when(cs.wasNull()).thenReturn(true);
        Assertions.assertNull(MonthTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

