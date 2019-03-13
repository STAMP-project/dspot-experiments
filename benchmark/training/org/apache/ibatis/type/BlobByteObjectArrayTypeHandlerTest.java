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


import java.io.ByteArrayInputStream;
import java.sql.Blob;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;


class BlobByteObjectArrayTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Byte[]> TYPE_HANDLER = new BlobByteObjectArrayTypeHandler();

    @Mock
    protected Blob blob;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        final ArgumentCaptor<Integer> positionCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<ByteArrayInputStream> byteArrayCaptor = ArgumentCaptor.forClass(ByteArrayInputStream.class);
        final ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.doNothing().when(ps).setBinaryStream(positionCaptor.capture(), byteArrayCaptor.capture(), lengthCaptor.capture());
        BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, new Byte[]{ 1, 2 }, null);
        ByteArrayInputStream actualIn = byteArrayCaptor.getValue();
        assertThat(positionCaptor.getValue()).isEqualTo(1);
        assertThat(actualIn.read()).isEqualTo(1);
        assertThat(actualIn.read()).isEqualTo(2);
        assertThat(actualIn.read()).isEqualTo((-1));
        assertThat(lengthCaptor.getValue()).isEqualTo(2);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        byte[] byteArray = new byte[]{ 1, 2 };
        Mockito.when(rs.getBlob("column")).thenReturn(blob);
        Mockito.when(blob.length()).thenReturn(((long) (byteArray.length)));
        Mockito.when(blob.getBytes(1, 2)).thenReturn(byteArray);
        assertThat(BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column")).isEqualTo(new Byte[]{ 1, 2 });
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getBlob("column")).thenReturn(null);
        assertThat(BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column")).isNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        byte[] byteArray = new byte[]{ 1, 2 };
        Mockito.when(rs.getBlob(1)).thenReturn(blob);
        Mockito.when(blob.length()).thenReturn(((long) (byteArray.length)));
        Mockito.when(blob.getBytes(1, 2)).thenReturn(byteArray);
        assertThat(BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1)).isEqualTo(new Byte[]{ 1, 2 });
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getBlob(1)).thenReturn(null);
        assertThat(BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1)).isNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        byte[] byteArray = new byte[]{ 1, 2 };
        Mockito.when(cs.getBlob(1)).thenReturn(blob);
        Mockito.when(blob.length()).thenReturn(((long) (byteArray.length)));
        Mockito.when(blob.getBytes(1, 2)).thenReturn(byteArray);
        assertThat(BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1)).isEqualTo(new Byte[]{ 1, 2 });
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getBlob(1)).thenReturn(null);
        assertThat(BlobByteObjectArrayTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1)).isNull();
    }
}

