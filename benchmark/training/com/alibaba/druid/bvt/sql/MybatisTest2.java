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
package com.alibaba.druid.bvt.sql;


import JdbcUtils.ALI_ORACLE;
import JdbcUtils.MYSQL;
import JdbcUtils.ORACLE;
import JdbcUtils.POSTGRESQL;
import com.alibaba.druid.sql.SQLUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class MybatisTest2 extends TestCase {
    private String sql = "select * from t where id = ${id}";

    public void test_mysql() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = ${id}", SQLUtils.format(sql, MYSQL));
    }

    public void test_oracle() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = ${id}", SQLUtils.format(sql, ORACLE));
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = ${id}", SQLUtils.format(sql, ALI_ORACLE));
    }

    public void test_postgres() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = ${id}", SQLUtils.format(sql, POSTGRESQL));
    }

    public void test_sql92() throws Exception {
        Assert.assertEquals("SELECT *\nFROM t\nWHERE id = ${id}", SQLUtils.format(sql, null));
    }
}

