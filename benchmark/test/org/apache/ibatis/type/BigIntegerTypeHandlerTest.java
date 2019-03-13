/**
 * Copyright 2009-2013 the original author or authors.
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


import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class BigIntegerTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<BigInteger> TYPE_HANDLER = new BigIntegerTypeHandler();

    @Test
    public void shouldSetParameter() throws Exception {
        BigIntegerTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, new BigInteger("707070656505050302797979792923232303"), null);
        Mockito.verify(ps).setBigDecimal(1, new BigDecimal("707070656505050302797979792923232303"));
    }

    @Test
    public void shouldGetResultFromResultSet() throws Exception {
        Mockito.when(rs.getBigDecimal("column")).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
        Mockito.when(rs.wasNull()).thenReturn(false);
        Assert.assertEquals(new BigInteger("707070656505050302797979792923232303"), BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Mockito.when(cs.getBigDecimal(1)).thenReturn(new BigDecimal("707070656505050302797979792923232303"));
        Mockito.when(cs.wasNull()).thenReturn(false);
        Assert.assertEquals(new BigInteger("707070656505050302797979792923232303"), BigIntegerTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }
}

