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


import java.sql.Connection;
import java.sql.SQLXML;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;


@Tag("EmbeddedPostgresqlTests")
class SqlxmlTypeHandlerTest extends BaseTypeHandlerTest {
    private static final TypeHandler<String> TYPE_HANDLER = new SqlxmlTypeHandler();

    private static final EmbeddedPostgres postgres = new EmbeddedPostgres();

    private static SqlSessionFactory sqlSessionFactory;

    @Mock
    private SQLXML sqlxml;

    @Mock
    private Connection connection;

    @Override
    @Test
    public void shouldSetParameter() throws Exception {
        Mockito.when(connection.createSQLXML()).thenReturn(sqlxml);
        Mockito.when(ps.getConnection()).thenReturn(connection);
        String xml = "<message>test</message>";
        SqlxmlTypeHandlerTest.TYPE_HANDLER.setParameter(ps, 1, xml, null);
        Mockito.verify(ps).setSQLXML(1, sqlxml);
        Mockito.verify(sqlxml).setString(xml);
        Mockito.verify(sqlxml).free();
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByName() throws Exception {
        String xml = "<message>test</message>";
        Mockito.when(sqlxml.getString()).thenReturn(xml);
        Mockito.when(rs.getSQLXML("column")).thenReturn(sqlxml);
        Assertions.assertEquals(xml, SqlxmlTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
        Mockito.verify(sqlxml).free();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByName() throws Exception {
        Mockito.when(rs.getSQLXML("column")).thenReturn(null);
        Assertions.assertNull(SqlxmlTypeHandlerTest.TYPE_HANDLER.getResult(rs, "column"));
    }

    @Override
    @Test
    public void shouldGetResultFromResultSetByPosition() throws Exception {
        String xml = "<message>test</message>";
        Mockito.when(sqlxml.getString()).thenReturn(xml);
        Mockito.when(rs.getSQLXML(1)).thenReturn(sqlxml);
        Assertions.assertEquals(xml, SqlxmlTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
        Mockito.verify(sqlxml).free();
    }

    @Override
    @Test
    public void shouldGetResultNullFromResultSetByPosition() throws Exception {
        Mockito.when(rs.getSQLXML(1)).thenReturn(null);
        Assertions.assertNull(SqlxmlTypeHandlerTest.TYPE_HANDLER.getResult(rs, 1));
    }

    @Override
    @Test
    public void shouldGetResultFromCallableStatement() throws Exception {
        String xml = "<message>test</message>";
        Mockito.when(sqlxml.getString()).thenReturn(xml);
        Mockito.when(cs.getSQLXML(1)).thenReturn(sqlxml);
        Assertions.assertEquals(xml, SqlxmlTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
        Mockito.verify(sqlxml).free();
    }

    @Override
    @Test
    public void shouldGetResultNullFromCallableStatement() throws Exception {
        Mockito.when(cs.getSQLXML(1)).thenReturn(null);
        Assertions.assertNull(SqlxmlTypeHandlerTest.TYPE_HANDLER.getResult(cs, 1));
    }

    interface Mapper {
        @Select("select id, content from mbtest.test_sqlxml where id = #{id}")
        SqlxmlTypeHandlerTest.XmlBean select(Integer id);

        @Insert("insert into mbtest.test_sqlxml (id, content) values (#{id}, #{content,jdbcType=SQLXML})")
        void insert(SqlxmlTypeHandlerTest.XmlBean bean);
    }

    public static class XmlBean {
        private Integer id;

        private String content;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

