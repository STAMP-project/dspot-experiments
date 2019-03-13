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


import java.sql.Time;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SqlTimeTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Time> TYPE_HANDLER = new SqlTimeTypeHandler();

    private static final Time SQL_TIME = new Time(new Date().getTime());

    @Test
    public void shouldSetParameter() throws Exception {
        SqlTimeTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, SqlTimeTypeHandlerTest.SQL_TIME, null);
        Mockito.verify(ps).setTime(1, SqlTimeTypeHandlerTest.SQL_TIME);
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getTime("column")).thenReturn(SqlTimeTypeHandlerTest.SQL_TIME);
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertEquals(SqlTimeTypeHandlerTest.SQL_TIME, SqlTimeTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getTime(1)).thenReturn(SqlTimeTypeHandlerTest.SQL_TIME);
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertEquals(SqlTimeTypeHandlerTest.SQL_TIME, SqlTimeTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

