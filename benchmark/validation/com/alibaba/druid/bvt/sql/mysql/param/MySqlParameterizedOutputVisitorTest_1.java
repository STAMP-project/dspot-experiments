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
package com.alibaba.druid.bvt.sql.mysql.param;


import JdbcConstants.DB2;
import JdbcConstants.DERBY;
import JdbcConstants.H2;
import JdbcConstants.HSQL;
import JdbcConstants.MYSQL;
import JdbcConstants.ORACLE;
import JdbcConstants.POSTGRESQL;
import JdbcConstants.SQL_SERVER;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class MySqlParameterizedOutputVisitorTest_1 extends TestCase {
    public void test_0() throws Exception {
        String sql = "SELECT * FROM T WHERE ID IN (?)";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, HSQL), sql);
    }

    public void test_1() throws Exception {
        String sql = "SELECT * FROM T WHERE ID = ?";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, HSQL), sql);
    }

    public void test_2() throws Exception {
        String sql = "SELECT * FROM T WHERE ID = ? AND Name = ?";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, HSQL), sql);
    }

    public void test_3() throws Exception {
        String sql = "SELECT * FROM T WHERE ID IS NULL";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, HSQL), sql);
    }

    public void test_4() throws Exception {
        String sql = "INSERT INTO T (FID, FNAME) VALUES(?, ?)";
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, ORACLE), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DB2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, SQL_SERVER), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, POSTGRESQL), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, H2), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, DERBY), sql);
        Assert.assertSame(ParameterizedOutputVisitorUtils.parameterize(sql, HSQL), sql);
    }

    public void test_mysql() throws Exception {
        String sql = "INSERT INTO T (FID, FNAME) VALUES(?, ?), (?, ?)";
        Assert.assertNotSame(ParameterizedOutputVisitorUtils.parameterize(sql, MYSQL), sql);
    }
}

