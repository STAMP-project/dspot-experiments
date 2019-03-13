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
package org.apache.hadoop.hbase.favored;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestStartcodeAgnosticServerName {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStartcodeAgnosticServerName.class);

    @Test
    public void testStartCodeServerName() {
        ServerName sn = ServerName.valueOf("www.example.org", 1234, 5678);
        StartcodeAgnosticServerName snStartCode = new StartcodeAgnosticServerName("www.example.org", 1234, 5678);
        Assert.assertTrue(ServerName.isSameAddress(sn, snStartCode));
        Assert.assertTrue(snStartCode.equals(sn));
        Assert.assertTrue(sn.equals(snStartCode));
        Assert.assertEquals(0, snStartCode.compareTo(sn));
        StartcodeAgnosticServerName snStartCodeFNPort = new StartcodeAgnosticServerName("www.example.org", 1234, ServerName.NON_STARTCODE);
        Assert.assertTrue(ServerName.isSameAddress(snStartCodeFNPort, snStartCode));
        Assert.assertTrue(snStartCode.equals(snStartCodeFNPort));
        Assert.assertTrue(snStartCodeFNPort.equals(snStartCode));
        Assert.assertEquals(0, snStartCode.compareTo(snStartCodeFNPort));
    }
}

