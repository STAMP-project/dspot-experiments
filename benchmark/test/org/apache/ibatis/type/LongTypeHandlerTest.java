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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class LongTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Long> TYPE_HANDLER = new LongTypeHandler();

    @Test
    public void shouldSetParameter() throws Exception {
        LongTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, 100L, null);
        Mockito.verify(ps).setLong(1, 100L);
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getLong("column")).thenReturn(100L);
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertEquals(new Long(100L), LongTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getLong(1)).thenReturn(100L);
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertEquals(new Long(100L), LongTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

