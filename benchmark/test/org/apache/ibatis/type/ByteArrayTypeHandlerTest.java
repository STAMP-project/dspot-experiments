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


public class ByteArrayTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<byte[]> TYPE_HANDLER = new ByteArrayTypeHandler();

    @Test
    public void shouldSetParameter() throws Exception {
        ByteArrayTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, new byte[]{ 1, 2, 3 }, null);
        Mockito.verify(ps).setBytes(1, new byte[]{ 1, 2, 3 });
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getBytes("column")).thenReturn(new byte[]{ 1, 2, 3 });
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, ByteArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getBytes(1)).thenReturn(new byte[]{ 1, 2, 3 });
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertArrayEquals(new byte[]{ 1, 2, 3 }, ByteArrayTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

