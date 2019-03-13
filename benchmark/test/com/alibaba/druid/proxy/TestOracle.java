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
package com.alibaba.druid.proxy;


import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.management.openmbean.TabularData;
import junit.framework.TestCase;


public class TestOracle extends TestCase {
    private String jdbcUrl;

    private String user;

    private String password;

    public void test_0() throws Exception {
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
        rs.next();
        rs.close();
        stmt.close();
        conn.close();
        TabularData dataSourcesList = JdbcStatManager.getInstance().getDataSourceList();
        for (Object item : dataSourcesList.values()) {
            String text = JSONUtils.toJSONString(item);
            System.out.println(text);
        }
    }
}

