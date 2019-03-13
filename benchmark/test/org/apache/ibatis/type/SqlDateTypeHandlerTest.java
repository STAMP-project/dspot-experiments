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


import java.sql.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SqlDateTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Date> TYPE_HANDLER = new SqlDateTypeHandler();

    private static final Date SQL_DATE = new Date(new java.util.Date().getTime());

    @Test
    public void shouldSetParameter() throws Exception {
        SqlDateTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, SqlDateTypeHandlerTest.SQL_DATE, null);
        Mockito.verify(ps).setDate(1, SqlDateTypeHandlerTest.SQL_DATE);
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getDate("column")).thenReturn(SqlDateTypeHandlerTest.SQL_DATE);
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertEquals(SqlDateTypeHandlerTest.SQL_DATE, SqlDateTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getDate(1)).thenReturn(SqlDateTypeHandlerTest.SQL_DATE);
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertEquals(SqlDateTypeHandlerTest.SQL_DATE, SqlDateTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

