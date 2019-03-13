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
package com.alibaba.druid.bvt.sql.oracle;


import junit.framework.TestCase;


public class OracleSQLParserResourceTest extends TestCase {
    public void test_0() throws Exception {
        // for (int i = 0; i <= 53; ++i) {
        // String resource = "bvt/parser/oracle-" + i + ".txt";
        // exec_test(resource);
        // }
        exec_test("bvt/parser/oracle-55.txt");
    }
}

