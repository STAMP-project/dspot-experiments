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


import java.sql.Time;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class TimeOnlyTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Date> TYPE_HANDLER = new TimeOnlyTypeHandler();

    private static final Date DATE = new Date();

    private static final Time SQL_TIME = new Time(TimeOnlyTypeHandlerTest.DATE.getTime());

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        TimeOnlyTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, TimeOnlyTypeHandlerTest.DATE, null);
        Mockito.verify(ps).setTime(1, TimeOnlyTypeHandlerTest.SQL_TIME);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getTime("column")).thenReturn(TimeOnlyTypeHandlerTest.SQL_TIME);
        Assertions.assertEquals(TimeOnlyTypeHandlerTest.DATE, TimeOnlyTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getTime("column")).thenReturn(null);
        Assertions.assertNull(TimeOnlyTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getTime(1)).thenReturn(TimeOnlyTypeHandlerTest.SQL_TIME);
        Assertions.assertEquals(TimeOnlyTypeHandlerTest.DATE, TimeOnlyTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getTime(1)).thenReturn(null);
        Assertions.assertNull(TimeOnlyTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getTime(1)).thenReturn(TimeOnlyTypeHandlerTest.SQL_TIME);
        Assertions.assertEquals(TimeOnlyTypeHandlerTest.DATE, TimeOnlyTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getTime(1)).thenReturn(null);
        Assertions.assertNull(TimeOnlyTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

