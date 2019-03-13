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
package org.apache.ibatis.executor.resultset;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultResultSetHandlerTest {
    @Mock
    private Statement stmt;

    @Mock
    private ResultSet rs;

    @Mock
    private ResultSetMetaData rsmd;

    @Mock
    private Connection conn;

    @Mock
    private DatabaseMetaData dbmd;

    /**
     * Contrary to the spec, some drivers require case-sensitive column names when getting result.
     *
     * @see <a href="http://code.google.com/p/mybatis/issues/detail?id=557">Issue 557</a>
     */
    @Test
    public void shouldRetainColumnNameCase() throws Exception {
        final Configuration config = new Configuration();
        final TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
        final MappedStatement ms = resultMaps(new ArrayList<ResultMap>() {
            {
                add(build());
            }
        }).build();
        final Executor executor = null;
        final ParameterHandler parameterHandler = null;
        final ResultHandler resultHandler = null;
        final BoundSql boundSql = null;
        final RowBounds rowBounds = new RowBounds(0, 100);
        final DefaultResultSetHandler fastResultSetHandler = new DefaultResultSetHandler(executor, ms, parameterHandler, resultHandler, boundSql, rowBounds);
        Mockito.when(stmt.getResultSet()).thenReturn(rs);
        Mockito.when(rs.getMetaData()).thenReturn(rsmd);
        Mockito.when(rs.getType()).thenReturn(ResultSet.TYPE_FORWARD_ONLY);
        Mockito.when(rs.next()).thenReturn(true).thenReturn(false);
        Mockito.when(rs.getInt("CoLuMn1")).thenReturn(100);
        Mockito.when(rs.wasNull()).thenReturn(false);
        Mockito.when(rsmd.getColumnCount()).thenReturn(1);
        Mockito.when(rsmd.getColumnLabel(1)).thenReturn("CoLuMn1");
        Mockito.when(rsmd.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(rsmd.getColumnClassName(1)).thenReturn(Integer.class.getCanonicalName());
        Mockito.when(stmt.getConnection()).thenReturn(conn);
        Mockito.when(conn.getMetaData()).thenReturn(dbmd);
        Mockito.when(dbmd.supportsMultipleResultSets()).thenReturn(false);// for simplicity.

        final List<Object> results = fastResultSetHandler.handleResultSets(stmt);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(Integer.valueOf(100), ((HashMap) (results.get(0))).get("cOlUmN1"));
    }
}

