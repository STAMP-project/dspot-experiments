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
package com.alibaba.druid.mysql;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import junit.framework.TestCase;


public class MySqlBigTableTest extends TestCase {
    final int COUNT = 800;

    private String jdbcUrl;

    private String user;

    private String password;

    private String driverClass;

    private DruidDataSource dataSource;

    public void test_0() throws Exception {
        StringBuffer ddl = new StringBuffer();
        ddl.append("INSERT INTO t_big (");
        for (int i = 0; i < (COUNT); ++i) {
            if (i != 0) {
                ddl.append(", ");
            }
            ddl.append(("F" + i));
        }
        ddl.append(") VALUES (");
        for (int i = 0; i < (COUNT); ++i) {
            if (i != 0) {
                ddl.append(", ");
            }
            ddl.append("?");
        }
        ddl.append(")");
        Connection conn = dataSource.getConnection();
        System.out.println(ddl.toString());
        PreparedStatement stmt = conn.prepareStatement(ddl.toString());
        for (int i = 0; i < (COUNT); ++i) {
            stmt.setInt((i + 1), i);
        }
        stmt.execute();
        stmt.close();
        conn.close();
    }
}

