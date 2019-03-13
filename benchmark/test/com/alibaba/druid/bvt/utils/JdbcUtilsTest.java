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
package com.alibaba.druid.bvt.utils;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JdbcUtilsTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_curd() throws Exception {
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select * from user");
            Assert.assertEquals(0, list.size());
        }
        {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", 123);
            data.put("name", "????");
            JdbcUtils.insertToTable(dataSource, "user", data);
        }
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select * from user");
            Assert.assertEquals(1, list.size());
            Map<String, Object> data = list.get(0);
            Assert.assertEquals(123, data.get("ID"));
            Assert.assertEquals("????", data.get("NAME"));
        }
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select id \"id\", name \"name\" from user");
            Assert.assertEquals(1, list.size());
            Map<String, Object> data = list.get(0);
            Assert.assertEquals(123, data.get("id"));
            Assert.assertEquals("????", data.get("name"));
        }
        {
            JdbcUtils.executeUpdate(dataSource, "delete from user");
        }
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select * from user");
            Assert.assertEquals(0, list.size());
        }
    }
}

