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
package org.apache.hadoop.hbase.security.access;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ SecurityTests.class, LargeTests.class })
public class TestAccessControlFilter extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAccessControlFilter.class);

    @Rule
    public TestName name = new TestName();

    private static HBaseTestingUtility TEST_UTIL;

    private static User READER;

    private static User LIMITED;

    private static User DENIED;

    private static TableName TABLE;

    private static byte[] FAMILY = Bytes.toBytes("f1");

    private static byte[] PRIVATE_COL = Bytes.toBytes("private");

    private static byte[] PUBLIC_COL = Bytes.toBytes("public");

    @Test
    public void testQualifierAccess() throws Exception {
        final Table table = SecureTestUtil.createTable(TestAccessControlFilter.TEST_UTIL, TestAccessControlFilter.TABLE, new byte[][]{ TestAccessControlFilter.FAMILY });
        try {
            doQualifierAccess(table);
        } finally {
            table.close();
        }
    }
}

