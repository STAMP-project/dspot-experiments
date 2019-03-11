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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.pig;


import IOConstants.AVRO;
import IOConstants.JSONFILE;
import IOConstants.PARQUETFILE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestHCatLoaderComplexSchema {
    // private static MiniCluster cluster = MiniCluster.buildCluster();
    private static IDriver driver;

    // private static Properties props;
    private static final Logger LOG = LoggerFactory.getLogger(TestHCatLoaderComplexSchema.class);

    private static final Map<String, Set<String>> DISABLED_STORAGE_FORMATS = new HashMap<String, Set<String>>() {
        {
            put(AVRO, new HashSet<String>() {
                {
                    add("testMapNullKey");
                }
            });
            put(PARQUETFILE, new HashSet<String>() {
                {
                    add("testMapNullKey");
                }
            });
            put(JSONFILE, new HashSet<String>() {
                {
                    add("testMapNullKey");
                }
            });
        }
    };

    private String storageFormat;

    public TestHCatLoaderComplexSchema(String storageFormat) {
        this.storageFormat = storageFormat;
    }

    private static final TupleFactory tf = TupleFactory.getInstance();

    private static final BagFactory bf = BagFactory.getInstance();

    /**
     * artificially complex nested schema to test nested schema conversion
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSyntheticComplexSchema() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatLoaderComplexSchema.DISABLED_STORAGE_FORMATS))));
        String pigSchema = "a: " + (((((((((("(" + "aa: chararray, ") + "ab: long, ") + "ac: map[], ") + "ad: { t: (ada: long) }, ") + "ae: { t: (aea:long, aeb: ( aeba: chararray, aebb: long)) },") + "af: (afa: chararray, afb: long) ") + "),") + "b: chararray, ") + "c: long, ") + "d:  { t: (da:long, db: ( dba: chararray, dbb: long), dc: { t: (dca: long) } ) } ");
        // with extra structs
        String tableSchema = "a struct<" + ((((((((("aa: string, " + "ab: bigint, ") + "ac: map<string, string>, ") + "ad: array<struct<ada:bigint>>, ") + "ae: array<struct<aea:bigint, aeb: struct<aeba: string, aebb: bigint>>>,") + "af: struct<afa: string, afb: bigint> ") + ">, ") + "b string, ") + "c bigint, ") + "d array<struct<da: bigint, db: struct<dba:string, dbb:bigint>, dc: array<struct<dca: bigint>>>>");
        // without extra structs
        String tableSchema2 = "a struct<" + ((((((((("aa: string, " + "ab: bigint, ") + "ac: map<string, string>, ") + "ad: array<bigint>, ") + "ae: array<struct<aea:bigint, aeb: struct<aeba: string, aebb: bigint>>>,") + "af: struct<afa: string, afb: bigint> ") + ">, ") + "b string, ") + "c bigint, ") + "d array<struct<da: bigint, db: struct<dba:string, dbb:bigint>, dc: array<bigint>>>");
        List<Tuple> data = new ArrayList<Tuple>();
        for (int i = 0; i < 10; i++) {
            Tuple t = t(t("aa test", 2L, new HashMap<String, String>() {
                {
                    put("ac test1", "test 1");
                    put("ac test2", "test 2");
                }
            }, b(t(3L), t(4L)), b(t(5L, t("aeba test", 6L))), t("afa test", 7L)), "b test", ((long) (i)), b(t(8L, t("dba test", 9L), b(t(10L)))));
            data.add(t);
        }
        verifyWriteRead("testSyntheticComplexSchema", pigSchema, tableSchema, data, true);
        verifyWriteRead("testSyntheticComplexSchema", pigSchema, tableSchema, data, false);
        verifyWriteRead("testSyntheticComplexSchema2", pigSchema, tableSchema2, data, true);
        verifyWriteRead("testSyntheticComplexSchema2", pigSchema, tableSchema2, data, false);
    }

    /**
     * tests that unnecessary tuples are drop while converting schema
     * (Pig requires Tuples in Bags)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTupleInBagInTupleInBag() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatLoaderComplexSchema.DISABLED_STORAGE_FORMATS))));
        String pigSchema = "a: { b : ( c: { d: (i : long) } ) }";
        String tableSchema = "a array< array< bigint > >";
        List<Tuple> data = new ArrayList<Tuple>();
        data.add(t(b(t(b(t(100L), t(101L))), t(b(t(110L))))));
        data.add(t(b(t(b(t(200L))), t(b(t(210L))), t(b(t(220L))))));
        data.add(t(b(t(b(t(300L), t(301L))))));
        data.add(t(b(t(b(t(400L))), t(b(t(410L), t(411L), t(412L))))));
        verifyWriteRead("TupleInBagInTupleInBag1", pigSchema, tableSchema, data, true);
        verifyWriteRead("TupleInBagInTupleInBag2", pigSchema, tableSchema, data, false);
        // test that we don't drop the unnecessary tuple if the table has the corresponding Struct
        String tableSchema2 = "a array< struct< c: array< struct< i: bigint > > > >";
        verifyWriteRead("TupleInBagInTupleInBag3", pigSchema, tableSchema2, data, true);
        verifyWriteRead("TupleInBagInTupleInBag4", pigSchema, tableSchema2, data, false);
    }

    @Test
    public void testMapWithComplexData() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatLoaderComplexSchema.DISABLED_STORAGE_FORMATS))));
        String pigSchema = "a: long, b: map[]";
        String tableSchema = "a bigint, b map<string, struct<aa:bigint, ab:string>>";
        List<Tuple> data = new ArrayList<Tuple>();
        for (int i = 0; i < 10; i++) {
            Tuple t = t(((long) (i)), new HashMap<String, Object>() {
                {
                    put("b test 1", t(1L, "test 1"));
                    put("b test 2", t(2L, "test 2"));
                }
            });
            data.add(t);
        }
        verifyWriteRead("testMapWithComplexData", pigSchema, tableSchema, data, true);
        verifyWriteRead("testMapWithComplexData2", pigSchema, tableSchema, data, false);
    }

    /**
     * artificially complex nested schema to test nested schema conversion
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMapNullKey() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatLoaderComplexSchema.DISABLED_STORAGE_FORMATS))));
        String pigSchema = "m:map[]";
        String tableSchema = "m map<string, string>";
        List<Tuple> data = new ArrayList<Tuple>();
        Tuple t = t(new HashMap<String, String>() {
            {
                put("ac test1", "test 1");
                put("ac test2", "test 2");
                put(null, "test 3");
            }
        });
        data.add(t);
        List<Tuple> result = new ArrayList<Tuple>();
        t = t(new HashMap<String, String>() {
            {
                put("ac test1", "test 1");
                put("ac test2", "test 2");
            }
        });
        result.add(t);
        verifyWriteRead("testSyntheticComplexSchema", pigSchema, tableSchema, data, result, true);
        verifyWriteRead("testSyntheticComplexSchema", pigSchema, tableSchema, data, result, false);
    }
}

