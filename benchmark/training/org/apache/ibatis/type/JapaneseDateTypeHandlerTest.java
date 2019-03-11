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


import java.sql.Date;
import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class JapaneseDateTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<JapaneseDate> TYPE_HANDLER = new JapaneseDateTypeHandler();

    private static final JapaneseDate JAPANESE_DATE = JapaneseDate.now();

    private static final Date DATE = Date.valueOf(LocalDate.ofEpochDay(JapaneseDateTypeHandlerTest.JAPANESE_DATE.toEpochDay()));

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        JapaneseDateTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, JapaneseDateTypeHandlerTest.JAPANESE_DATE, null);
        Mockito.verify(ps).setDate(1, JapaneseDateTypeHandlerTest.DATE);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getDate("column")).thenReturn(JapaneseDateTypeHandlerTest.DATE);
        Assertions.assertEquals(JapaneseDateTypeHandlerTest.JAPANESE_DATE, JapaneseDateTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getDate("column")).thenReturn(null);
        Assertions.assertNull(JapaneseDateTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getDate(1)).thenReturn(JapaneseDateTypeHandlerTest.DATE);
        Assertions.assertEquals(JapaneseDateTypeHandlerTest.JAPANESE_DATE, JapaneseDateTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getDate(1)).thenReturn(null);
        Assertions.assertNull(JapaneseDateTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getDate(1)).thenReturn(JapaneseDateTypeHandlerTest.DATE);
        Assertions.assertEquals(JapaneseDateTypeHandlerTest.JAPANESE_DATE, JapaneseDateTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getDate(1)).thenReturn(null);
        Assertions.assertNull(JapaneseDateTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

