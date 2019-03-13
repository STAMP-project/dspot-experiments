/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;


import java.sql.SQLException;
import org.junit.Test;


/**
 * Tests for {@link org.apache.calcite.sql.advise.SqlAdvisor}.
 */
public class SqlAdvisorJdbcTest {
    @Test
    public void testSqlAdvisorGetHintsFunction() throws ClassNotFoundException, SQLException {
        adviseSql(1, "select e.e^ from \"emps\" e", CalciteAssert.checkResultUnordered("id=e; names=null; type=MATCH", "id=empid; names=[empid]; type=COLUMN"));
    }

    @Test
    public void testSqlAdvisorGetHintsFunction2() throws ClassNotFoundException, SQLException {
        adviseSql(2, "select [e].e^ from [emps] e", CalciteAssert.checkResultUnordered("id=e; names=null; type=MATCH; replacement=null", "id=empid; names=[empid]; type=COLUMN; replacement=empid"));
    }

    @Test
    public void testSqlAdvisorNonExistingColumn() throws ClassNotFoundException, SQLException {
        adviseSql(1, "select e.empdid_wrong_name.^ from \"hr\".\"emps\" e", CalciteAssert.checkResultUnordered("id=*; names=[*]; type=KEYWORD", "id=; names=null; type=MATCH"));
    }

    @Test
    public void testSqlAdvisorNonStructColumn() throws ClassNotFoundException, SQLException {
        adviseSql(1, "select e.\"empid\".^ from \"hr\".\"emps\" e", CalciteAssert.checkResultUnordered("id=*; names=[*]; type=KEYWORD", "id=; names=null; type=MATCH"));
    }

    @Test
    public void testSqlAdvisorSubSchema() throws ClassNotFoundException, SQLException {
        adviseSql(1, "select * from \"hr\".^.test_test_test", CalciteAssert.checkResultUnordered("id=; names=null; type=MATCH", "id=hr.dependents; names=[hr, dependents]; type=TABLE", "id=hr.depts; names=[hr, depts]; type=TABLE", "id=hr.emps; names=[hr, emps]; type=TABLE", "id=hr.locations; names=[hr, locations]; type=TABLE", "id=hr; names=[hr]; type=SCHEMA"));
    }

    @Test
    public void testSqlAdvisorSubSchema2() throws ClassNotFoundException, SQLException {
        adviseSql(2, "select * from [hr].^.test_test_test", CalciteAssert.checkResultUnordered("id=; names=null; type=MATCH; replacement=null", "id=hr.dependents; names=[hr, dependents]; type=TABLE; replacement=dependents", "id=hr.depts; names=[hr, depts]; type=TABLE; replacement=depts", "id=hr.emps; names=[hr, emps]; type=TABLE; replacement=emps", "id=hr.locations; names=[hr, locations]; type=TABLE; replacement=locations", "id=hr; names=[hr]; type=SCHEMA; replacement=hr"));
    }

    @Test
    public void testSqlAdvisorTableInSchema() throws ClassNotFoundException, SQLException {
        adviseSql(1, "select * from \"hr\".^", CalciteAssert.checkResultUnordered("id=; names=null; type=MATCH", "id=hr.dependents; names=[hr, dependents]; type=TABLE", "id=hr.depts; names=[hr, depts]; type=TABLE", "id=hr.emps; names=[hr, emps]; type=TABLE", "id=hr.locations; names=[hr, locations]; type=TABLE", "id=hr; names=[hr]; type=SCHEMA"));
    }

    /**
     * Tests {@link org.apache.calcite.sql.advise.SqlAdvisorGetHintsFunction}.
     */
    @Test
    public void testSqlAdvisorSchemaNames() throws ClassNotFoundException, SQLException {
        adviseSql(1, "select empid from \"emps\" e, ^", CalciteAssert.checkResultUnordered("id=; names=null; type=MATCH", "id=(; names=[(]; type=KEYWORD", "id=LATERAL; names=[LATERAL]; type=KEYWORD", "id=TABLE; names=[TABLE]; type=KEYWORD", "id=UNNEST; names=[UNNEST]; type=KEYWORD", "id=hr; names=[hr]; type=SCHEMA", "id=metadata; names=[metadata]; type=SCHEMA", "id=s; names=[s]; type=SCHEMA", "id=hr.dependents; names=[hr, dependents]; type=TABLE", "id=hr.depts; names=[hr, depts]; type=TABLE", "id=hr.emps; names=[hr, emps]; type=TABLE", "id=hr.locations; names=[hr, locations]; type=TABLE"));
    }
}

/**
 * End SqlAdvisorJdbcTest.java
 */
