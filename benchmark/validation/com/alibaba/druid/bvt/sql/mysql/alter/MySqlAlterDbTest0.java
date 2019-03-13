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
package com.alibaba.druid.bvt.sql.mysql.alter;


import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import Token.EOF;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import junit.framework.TestCase;


public class MySqlAlterDbTest0 extends TestCase {
    public void test_alter_first() throws Exception {
        String sql = "ALTER DATABASE `#mysql50#a-b-c` UPGRADE DATA DIRECTORY NAME;";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        parser.match(EOF);
        TestCase.assertEquals("ALTER DATABASE `#mysql50#a-b-c` UPGRADE DATA DIRECTORY NAME;", SQLUtils.toMySqlString(stmt));
        TestCase.assertEquals("alter database `#mysql50#a-b-c` upgrade data directory name;", SQLUtils.toMySqlString(stmt, DEFAULT_LCASE_FORMAT_OPTION));
    }
}

