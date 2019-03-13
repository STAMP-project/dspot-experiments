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


import java.time.OffsetDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class OffsetDateTimeTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<OffsetDateTime> TYPE_HANDLER = new OffsetDateTimeTypeHandler();

    private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.now();

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME, null);
        Mockito.verify(ps).setObject(1, OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Mockito.when(rs.getObject("column", OffsetDateTime.class)).thenReturn(OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME);
        Assertions.assertEquals(OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME, OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getObject("column", OffsetDateTime.class)).thenReturn(null);
        Assertions.assertNull(OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getObject(1, OffsetDateTime.class)).thenReturn(OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME);
        Assertions.assertEquals(OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME, OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getObject(1, OffsetDateTime.class)).thenReturn(null);
        Assertions.assertNull(OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(rs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getObject(1, OffsetDateTime.class)).thenReturn(OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME);
        Assertions.assertEquals(OffsetDateTimeTypeHandlerTest.OFFSET_DATE_TIME, OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getObject(1, OffsetDateTime.class)).thenReturn(null);
        Assertions.assertNull(OffsetDateTimeTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(cs, Mockito.never()).wasNull();
    }
}

