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
package com.alibaba.druid.bvt.sql.sqlserver.select;


import JdbcConstants.SQL_SERVER;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import java.util.List;
import junit.framework.TestCase;


public class SQLServerSelectTest31 extends TestCase {
    public void test_simple() throws Exception {
        // 
        String sql = "select top 1 (CAST(OriganID AS VARCHAR(20)) + ',' + MobilePhoneUrl) FROM T";// 

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, SQL_SERVER);
        {
            String text = SQLUtils.toSQLString(stmtList, SQL_SERVER);
            TestCase.assertEquals(("SELECT TOP 1 CAST(OriganID AS VARCHAR(20)) + \',\' + MobilePhoneUrl\n" + "FROM T"), text);
        }
        {
            String text = SQLUtils.toSQLString(stmtList, SQL_SERVER, DEFAULT_LCASE_FORMAT_OPTION);
            TestCase.assertEquals(("select top 1 cast(OriganID as VARCHAR(20)) + \',\' + MobilePhoneUrl\n" + "from T"), text);
        }
    }
}

