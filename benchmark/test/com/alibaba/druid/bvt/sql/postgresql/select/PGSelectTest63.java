/**
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.postgresql.select;


import JdbcConstants.POSTGRESQL;
import SQLUtils.DEFAULT_LCASE_FORMAT_OPTION;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import java.util.List;
import junit.framework.TestCase;


public class PGSelectTest63 extends TestCase {
    public void test_0() throws Exception {
        String sql = "select * from a where a.id in (VALUES (4096), (4097))";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, POSTGRESQL);
        SQLStatement stmt = stmtList.get(0);
        TestCase.assertEquals(("SELECT *\n" + ("FROM a\n" + "WHERE a.id IN (VALUES ((4096), (4097))")), SQLUtils.toPGString(stmt));
        TestCase.assertEquals(("select *\n" + ("from a\n" + "where a.id in (values ((4096), (4097))")), SQLUtils.toPGString(stmt, DEFAULT_LCASE_FORMAT_OPTION));
        TestCase.assertEquals(1, stmtList.size());
        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(POSTGRESQL);
        stmt.accept(visitor);
        // System.out.println("Tables : " + visitor.getTables());
        // System.out.println("fields : " + visitor.getColumns());
        // System.out.println("coditions : " + visitor.getConditions());
        TestCase.assertEquals(2, visitor.getColumns().size());
        TestCase.assertEquals(1, visitor.getTables().size());
    }
}

