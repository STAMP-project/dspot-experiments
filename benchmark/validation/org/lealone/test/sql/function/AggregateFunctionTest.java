/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lealone.test.sql.function;


import org.junit.Test;
import org.lealone.test.sql.SqlTestBase;


// TODO H2????14????????group_concat?selectivity?histogram???????
public class AggregateFunctionTest extends SqlTestBase {
    @Test
    public void run() throws Exception {
        init();
        testAggregateFunctions();
        testAggregateFunctionsWithGroupBy();
    }

    int count1;

    int count2;

    int max1;

    int max2;

    int min1;

    int min2;

    int sum1;

    // ?1????????????1???;
    // ?2????????????2?4???
    // ???????H2??????????????????????????
    // ??avg???????????count?sum????count?sum?????????????????????????avg?
    // ??????????????????????????????????????
    int sum2;

    boolean bool_and1;

    boolean bool_and2;

    boolean bool_or1;

    boolean bool_or2;

    double avg1;

    double avg2;

    double stddev_pop1;

    double stddev_pop2;

    double stddev_samp1;

    double stddev_samp2;

    double var_pop1;

    double var_pop2;

    double var_samp1;

    double var_samp2;

    String select = "SELECT count(*), max(f3), min(f3), sum(f3), "// 
     + (((((((" bool_and((f3 % 2)=1), "// 
     + " bool_or(f3=5), ")// 
     + " avg(f3), ")// 
     + " stddev_pop(f3), ")// 
     + " stddev_samp(f3), ")// 
     + " var_pop(f3), ")// 
     + " var_samp(f3) ")// 
     + " FROM AggregateFunctionTest WHERE ");
}

