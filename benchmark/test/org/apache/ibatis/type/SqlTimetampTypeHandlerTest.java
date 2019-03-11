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


import java.sql.Timestamp;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class SqlTimetampTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Timestamp> TYPE_HANDLER = new SqlTimestampTypeHandler();

    private static final Timestamp SQL_TIME = new Timestamp(new Date().getTime());

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        SqlTimetampTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, SqlTimetampTypeHandlerTest.SQL_TIME, null);
        Mockito.verify(ps).setTimestamp(1, SqlTimetampTypeHandlerTest.SQL_TIME);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getTimestamp("column")).thenReturn(SqlTimetampTypeHandlerTest.SQL_TIME);
        Assertions.assertEquals(SqlTimetampTypeHandlerTest.SQL_TIME, SqlTimetampTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getTimestamp(1)).thenReturn(SqlTimetampTypeHandlerTest.SQL_TIME);
        Assertions.assertEquals(SqlTimetampTypeHandlerTest.SQL_TIME, SqlTimetampTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getTimestamp(1)).thenReturn(SqlTimetampTypeHandlerTest.SQL_TIME);
        Assertions.assertEquals(SqlTimetampTypeHandlerTest.SQL_TIME, SqlTimetampTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

