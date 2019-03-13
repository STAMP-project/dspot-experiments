/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.vector.complex.writer;


import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestComplexTypeWriter extends BaseTestQuery {
    static final Logger logger = LoggerFactory.getLogger(TestComplexTypeReader.class);

    // basic case. convert varchar into json.
    @Test
    public void testA0() throws Exception {
        BaseTestQuery.test(" select convert_from('{x:100, y:215.6}' ,'JSON') as mycol from cp.`tpch/nation.parquet`;");
    }

    // map contains int, float, repeated list , repeated map, nested repeated map, etc.
    @Test
    public void testA1() throws Exception {
        BaseTestQuery.test((" select convert_from('{x:100, y:215.6, z: [1, 2, 3], s : [[5, 6, 7], [8, 9]], " + ((" t : [{a : 100, b: 200}, {a:300, b: 400}], " + " nrmp: [ { x: [{ id: 123}], y: { y : \"SQL\"} }] }\' ,\'JSON\') ") + " as mycol from cp.`tpch/nation.parquet`;")));
    }

    // two convert functions.
    @Test
    public void testA2() throws Exception {
        BaseTestQuery.test(" select convert_from('{x:100, y:215.6}' ,'JSON') as mycol1, convert_from('{x:100, y:215.6}' ,'JSON') as mycol2 from cp.`tpch/nation.parquet`;");
    }

    // two convert functions.  One convert's input comes from a string concat function.
    @Test
    public void testA3() throws Exception {
        BaseTestQuery.test(" select convert_from(concat('{x:100,',  'y:215.6}') ,'JSON') as mycol1, convert_from('{x:100, y:215.6}' ,'JSON') as mycol2 from cp.`tpch/nation.parquet`;");
    }

    // two convert functions. One's input is an empty map.
    @Test
    public void testA4() throws Exception {
        BaseTestQuery.test(" select convert_from('{}' ,'JSON') as mycol1, convert_from('{x:100, y:215.6}' ,'JSON') as mycol2 from cp.`tpch/nation.parquet`;");
    }

    // two convert functions. One's input is an empty list ( ok to have null in the result?)
    @Test
    public void testA5() throws Exception {
        BaseTestQuery.test(" select convert_from('[]' ,'JSON') as mycol1, convert_from('{x:100, y:215.6}' ,'JSON') as mycol2 from cp.`tpch/nation.parquet`;");
    }

    // input is a list of BigInt. Output will be a repeated list vector.
    @Test
    public void testA6() throws Exception {
        BaseTestQuery.test(" select convert_from('[1, 2, 3]' ,'JSON') as mycol1  from cp.`tpch/nation.parquet`;");
    }

    // input is a list of float. Output will be a repeated list vector.
    @Test
    public void testA7() throws Exception {
        BaseTestQuery.test(" select convert_from('[1.2, 2.3, 3.5]' ,'JSON') as mycol1  from cp.`tpch/nation.parquet`;");
    }

    // input is a list of list of big int. Output will be a repeated list vector.
    @Test
    public void testA8() throws Exception {
        BaseTestQuery.test(" select convert_from('[ [1, 2], [3, 4], [5]]' ,'JSON') as mycol1  from cp.`tpch/nation.parquet`;");
    }

    // input is a list of map. Output will be a repeated list vector.
    @Test
    public void testA9() throws Exception {
        BaseTestQuery.test(" select convert_from('[{a : 100, b: 200}, {a:300, b: 400}]' ,'JSON') as mycol1  from cp.`tpch/nation.parquet`;");
    }

    // two convert functions, one regular nest functions, used with Filter op.
    @Test
    public void testA10() throws Exception {
        BaseTestQuery.test((" select convert_from('{x:100, y:215.6}' ,'JSON') as mycol1, " + (("        convert_from('{x:200, y:678.9}' ,'JSON') as mycol2, " + "        1 + 2 * 3 as numvalue ") + " from cp.`tpch/nation.parquet` where n_nationkey > 5;")));
    }

    // convert from string constructed from columns in parquet file.
    @Test
    public void testA11() throws Exception {
        BaseTestQuery.test((" select convert_from(concat(concat(\'{ NationName: \"\', N_NAME) , \'\"}\'), \'JSON\')" + " from cp.`tpch/nation.parquet` where n_nationkey > 5;"));
    }

    // Test multiple batches creation ( require multiple alloc for complex writer during Project ).
    @Test
    public void testA100() throws Exception {
        BaseTestQuery.test((" select convert_from(concat(concat('{ Price : ', L_EXTENDEDPRICE) , '}') , 'JSON') " + " from cp.`tpch/lineitem.parquet` limit 10; "));
    }
}

