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
import java.io.InputStream;
import java.sql.Blob;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link BlobInputStreamTypeHandler}.
 *
 * @since 3.4.0
 * @author Kazuki Shimizu
 */
class BlobInputStreamTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<InputStream> TYPE_HANDLER = new BlobInputStreamTypeHandler();

    private static SqlSessionFactory sqlSessionFactory;

    @Mock
    protected Blob blob;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        InputStream in = new ByteArrayInputStream("Hello".getBytes());
        BlobInputStreamTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, in, null);
        Mockito.verify(ps).setBlob(1, in);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        InputStream in = new ByteArrayInputStream("Hello".getBytes());
        Mockito.when(rs.getBlob("column")).thenReturn(blob);
        Mockito.when(blob.getBinaryStream()).thenReturn(in);
        assertThat(BlobInputStreamTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column")).isEqualTo(in);
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getBlob("column")).thenReturn(null);
        assertThat(BlobInputStreamTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column")).isNull();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        InputStream in = new ByteArrayInputStream("Hello".getBytes());
        Mockito.when(rs.getBlob(1)).thenReturn(blob);
        Mockito.when(blob.getBinaryStream()).thenReturn(in);
        assertThat(BlobInputStreamTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1)).isEqualTo(in);
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getBlob(1)).thenReturn(null);
        assertThat(BlobInputStreamTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1)).isNull();
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        InputStream in = new ByteArrayInputStream("Hello".getBytes());
        Mockito.when(cs.getBlob(1)).thenReturn(blob);
        Mockito.when(blob.getBinaryStream()).thenReturn(in);
        assertThat(BlobInputStreamTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1)).isEqualTo(in);
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getBlob(1)).thenReturn(null);
        assertThat(BlobInputStreamTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1)).isNull();
    }

    interface Mapper {
        @Select("SELECT ID, CONTENT FROM TEST_BLOB WHERE ID = #{id}")
        BlobInputStreamTypeHandlerTest.BlobContent findOne(int id);

        @Insert("INSERT INTO TEST_BLOB (ID, CONTENT) VALUES(#{id}, #{content})")
        void insert(BlobInputStreamTypeHandlerTest.BlobContent blobContent);
    }

    static class BlobContent {
        private int id;

        private InputStream content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public InputStream getContent() {
            return content;
        }

        public void setContent(InputStream content) {
            this.content = content;
        }
    }
}

