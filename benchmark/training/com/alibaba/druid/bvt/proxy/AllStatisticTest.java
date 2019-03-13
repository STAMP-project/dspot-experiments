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
package com.alibaba.druid.bvt.proxy;


import java.sql.Connection;
import java.util.concurrent.atomic.AtomicLong;
import junit.framework.TestCase;


public class AllStatisticTest extends TestCase {
    String url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=statTest:jdbc:derby:classpath:petstore-db";

    private AtomicLong fetchRowCout = new AtomicLong();

    Connection globalConnection = null;

    public void test_stmt() throws Exception {
        // ////////////////////////
        f1();
        f2();
        f3();
    }
}

