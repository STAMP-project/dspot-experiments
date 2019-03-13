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
package com.alibaba.druid.bvt.proxy.filter;


import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


public class MergeStatFilterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_merge() throws Exception {
        for (int i = 0; i < 100; ++i) {
            String sql = "select * from t where id = " + i;
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        }
        Assert.assertEquals(1, dataSource.getDataSourceStat().getSqlStatMap().size());
    }
}

