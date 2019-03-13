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
package com.alibaba.druid.bvt.sql.odps;


import junit.framework.TestCase;


public class OdpsSelect_distribute_by_Test extends TestCase {
    public void test_0() throws Exception {
        exec_test("bvt/parser/odps-1.sql");
        exec_test("bvt/parser/odps-2.sql");
        exec_test("bvt/parser/odps-3.sql");
    }
}

