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
package org.apache.hadoop.mapreduce.lib.join;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;


public class TestJoinProperties {
    private static MiniDFSCluster cluster = null;

    static final int SOURCES = 3;

    static final int ITEMS = ((TestJoinProperties.SOURCES) + 1) * ((TestJoinProperties.SOURCES) + 1);

    static int[][] source = new int[TestJoinProperties.SOURCES][];

    static Path[] src;

    static Path base;

    enum TestType {

        OUTER_ASSOCIATIVITY,
        INNER_IDENTITY,
        INNER_ASSOCIATIVITY;}

    // outer(outer(A, B), C) == outer(A,outer(B, C)) == outer(A, B, C)
    @Test
    public void testOuterAssociativity() throws Exception {
        Configuration conf = new Configuration();
        testExpr1(conf, "outer", TestJoinProperties.TestType.OUTER_ASSOCIATIVITY, 33);
        testExpr2(conf, "outer", TestJoinProperties.TestType.OUTER_ASSOCIATIVITY, 33);
        testExpr3(conf, "outer", TestJoinProperties.TestType.OUTER_ASSOCIATIVITY, 33);
    }

    // inner(inner(A, B), C) == inner(A,inner(B, C)) == inner(A, B, C)
    @Test
    public void testInnerAssociativity() throws Exception {
        Configuration conf = new Configuration();
        testExpr1(conf, "inner", TestJoinProperties.TestType.INNER_ASSOCIATIVITY, 2);
        testExpr2(conf, "inner", TestJoinProperties.TestType.INNER_ASSOCIATIVITY, 2);
        testExpr3(conf, "inner", TestJoinProperties.TestType.INNER_ASSOCIATIVITY, 2);
    }

    // override(inner(A, B), A) == A
    @Test
    public void testIdentity() throws Exception {
        Configuration conf = new Configuration();
        testExpr4(conf);
    }
}

