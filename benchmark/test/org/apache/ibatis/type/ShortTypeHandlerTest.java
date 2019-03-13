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


public class ShortTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Short> TYPE_HANDLER = new ShortTypeHandler();

    @Test
    public void shouldSetParameter() throws Exception {
        ShortTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, ((short) (100)), null);
        Mockito.verify(ps).setShort(1, ((short) (100)));
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getShort("column")).thenReturn(((short) (100)));
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertEquals(new Short(((short) (100))), ShortTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getShort(1)).thenReturn(((short) (100)));
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertEquals(new Short(((short) (100))), ShortTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

