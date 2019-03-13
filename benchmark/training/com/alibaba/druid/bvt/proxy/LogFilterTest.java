/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.proxy;


import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.logging.CommonsLogFilter;
import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import junit.framework.TestCase;


public class LogFilterTest extends TestCase {
    public void test_logFilter_0() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        config.setRawUrl("jdbc:mock:");
        DataSourceProxyImpl dataSource = new DataSourceProxyImpl(new MockDriver(), config);
        Log4jFilter log4jFilter = new Log4jFilter();
        log4jFilter.init(dataSource);
        config.getFilters().add(log4jFilter);
        setLogDisableAll(log4jFilter, true);
        CommonsLogFilter commonLogFilter = new CommonsLogFilter() {
            @Override
            public boolean isDataSourceLogEnabled() {
                return true;
            }

            @Override
            public boolean isConnectionLogEnabled() {
                return true;
            }

            @Override
            public boolean isStatementLogEnabled() {
                return true;
            }

            @Override
            public boolean isResultSetLogEnabled() {
                return true;
            }

            @Override
            public boolean isResultSetLogErrorEnabled() {
                return true;
            }

            @Override
            public boolean isResultSetNextAfterLogEnabled() {
                return true;
            }
        };
        commonLogFilter.init(dataSource);
        config.getFilters().add(commonLogFilter);
        setLogDisableAll(commonLogFilter, false);
        executeSQL(dataSource);
    }

    public void test_logFilter_1() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        config.setRawUrl("jdbc:mock:");
        DataSourceProxyImpl dataSource = new DataSourceProxyImpl(new MockDriver(), config);
        Log4jFilter log4jFilter = new Log4jFilter();
        log4jFilter.init(dataSource);
        config.getFilters().add(log4jFilter);
        setLogDisableAll(log4jFilter, true);
        CommonsLogFilter commonLogFilter = new CommonsLogFilter() {
            @Override
            public boolean isDataSourceLogEnabled() {
                return false;
            }

            @Override
            public boolean isConnectionLogEnabled() {
                return false;
            }

            @Override
            public boolean isStatementLogEnabled() {
                return false;
            }

            @Override
            public boolean isResultSetLogEnabled() {
                return false;
            }

            @Override
            public boolean isResultSetLogErrorEnabled() {
                return false;
            }

            @Override
            public boolean isResultSetNextAfterLogEnabled() {
                return false;
            }
        };
        commonLogFilter.init(dataSource);
        config.getFilters().add(commonLogFilter);
        setLogDisableAll(commonLogFilter, true);
        executeSQL(dataSource);
    }

    public void test_logFilter_2() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        config.setRawUrl("jdbc:mock:");
        DataSourceProxyImpl dataSource = new DataSourceProxyImpl(new MockDriver(), config);
        Log4jFilter log4jFilter = new Log4jFilter();
        {
            log4jFilter.init(dataSource);
            setLogDisableAll(log4jFilter, true);
            config.getFilters().add(log4jFilter);
        }
        CommonsLogFilter logFilter = new CommonsLogFilter();
        {
            logFilter.init(dataSource);
            setLogDisableAll(logFilter, true);
            config.getFilters().add(logFilter);
        }
        final MockResultSetMetaData rsMeta = new MockResultSetMetaData() {
            private int[] types = new int[]{ Types.BLOB, Types.CLOB, Types.NCLOB, Types.BINARY, Types.OTHER };

            @Override
            public int getColumnCount() throws SQLException {
                return types.length;
            }

            @Override
            public int getColumnType(int column) throws SQLException {
                return types[(column - 1)];
            }
        };
        ConnectionProxy conn = ((ConnectionProxy) (dataSource.connect(new Properties())));
        {
            StatementProxy stmt = ((StatementProxy) (conn.createStatement()));
            MockResultSet rs = new MockResultSet(null) {
                @Override
                public ResultSetMetaData getMetaData() throws SQLException {
                    return rsMeta;
                }

                @Override
                public boolean next() throws SQLException {
                    return true;
                }

                @Override
                public Object getObject(int columnIndex) throws SQLException {
                    if (columnIndex == 5) {
                        throw new SQLException();
                    }
                    return null;
                }
            };
            FilterChainImpl chain = new FilterChainImpl(dataSource);
            chain.resultSet_next(new com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl(stmt, rs, 1001, null));
        }
        {
            final MockResultSet rs = new MockResultSet(null) {
                @Override
                public ResultSetMetaData getMetaData() throws SQLException {
                    throw new SQLException();
                }
            };
            StatementProxy stmt = new com.alibaba.druid.proxy.jdbc.StatementProxyImpl(conn, new MockStatement(conn) {
                public ResultSet getResultSet() throws SQLException {
                    return rs;
                }
            }, 0);
            FilterChainImpl chain = new FilterChainImpl(dataSource);
            chain.statement_getResultSet(stmt);
        }
        {
            StatementProxy stmt = ((StatementProxy) (conn.createStatement()));
            MockResultSet rs = new MockResultSet(null) {
                @Override
                public ResultSetMetaData getMetaData() throws SQLException {
                    return rsMeta;
                }

                @Override
                public boolean next() throws SQLException {
                    return true;
                }

                @Override
                public Object getObject(int columnIndex) throws SQLException {
                    if (columnIndex == 5) {
                        throw new SQLException();
                    }
                    return null;
                }
            };
            {
                logFilter.setResultSetLogEnabled(false);
                FilterChainImpl chain = new FilterChainImpl(dataSource);
                chain.resultSet_next(new com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl(stmt, rs, 1001, null));
            }
            {
                logFilter.setResultSetNextAfterLogEnabled(false);
                FilterChainImpl chain = new FilterChainImpl(dataSource);
                chain.resultSet_next(new com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl(stmt, rs, 1001, null));
            }
        }
    }
}

