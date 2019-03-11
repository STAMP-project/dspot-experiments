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


import java.io.Reader;
import java.io.StringReader;
import java.sql.Clob;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ClobReaderTypeHandler}.
 *
 * @since 3.4.0
 * @author Kazuki Shimizu
 */
class ClobReaderTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<Reader> TYPE_HANDLER = new ClobReaderTypeHandler();

    private static SqlSessionFactory sqlSessionFactory;

    @Mock
    protected Clob clob;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        Reader reader = new StringReader("Hello");
        ClobReaderTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, reader, null);
        Mockito.verify(ps).setClob(1, reader);
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        Reader reader = new StringReader("Hello");
        Mockito.when(rs.getClob("column")).thenReturn(clob);
        Mockito.when(clob.getCharacterStream()).thenReturn(reader);
        Assertions.assertEquals(reader, ClobReaderTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getClob("column")).thenReturn(null);
        Assertions.assertNull(ClobReaderTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getClob(1)).thenReturn(clob);
        Assertions.assertNull(ClobReaderTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getClob(1)).thenReturn(null);
        Assertions.assertNull(ClobReaderTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        Reader reader = new StringReader("Hello");
        Mockito.when(cs.getClob(1)).thenReturn(clob);
        Mockito.when(clob.getCharacterStream()).thenReturn(reader);
        Assertions.assertEquals(reader, ClobReaderTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getClob(1)).thenReturn(null);
        Assertions.assertNull(ClobReaderTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    interface Mapper {
        @Select("SELECT ID, CONTENT FROM TEST_CLOB WHERE ID = #{id}")
        ClobReaderTypeHandlerTest.ClobContent findOne(int id);

        @Insert("INSERT INTO TEST_CLOB (ID, CONTENT) VALUES(#{id}, #{content})")
        void insert(ClobReaderTypeHandlerTest.ClobContent blobContent);
    }

    static class ClobContent {
        private int id;

        private Reader content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Reader getContent() {
            return content;
        }

        public void setContent(Reader content) {
            this.content = content;
        }
    }
}

