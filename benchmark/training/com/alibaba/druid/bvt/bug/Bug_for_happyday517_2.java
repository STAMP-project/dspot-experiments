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
package com.alibaba.druid.bvt.bug;


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.DataTruncation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_happyday517_2 extends TestCase {
    private DruidDataSource dataSource;

    private MockDriver driver;

    final DataTruncation exception = new DataTruncation(0, true, true, 0, 0);

    private int originalDataSourceCount = 0;

    public void test_bug() throws Exception {
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("insert into message.dbo.TempSMS(sms) values ('333')");
        Exception error = null;
        try {
            stmt.execute();
        } catch (SQLException ex) {
            error = ex;
        }
        Assert.assertTrue(((exception) == error));
        stmt.close();
        conn.close();
    }
}

