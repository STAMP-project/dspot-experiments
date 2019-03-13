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
package com.alibaba.druid.benckmark.pool.druid;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;


public class DruidCase0 extends TestCase {
    private DruidDataSource dataSource;

    public void test_benchmark() throws Exception {
        for (int i = 0; i < 10; ++i) {
            long startMillis = System.currentTimeMillis();
            benchmark();
            long millis = (System.currentTimeMillis()) - startMillis;
            System.out.println(("millis : " + millis));
        }
    }
}

