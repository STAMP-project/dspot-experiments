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
package org.apache.hadoop.hbase.client.example;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.coprocessor.example.TestRefreshHFilesBase;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, MediumTests.class })
public class TestRefreshHFilesClient extends TestRefreshHFilesBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRefreshHFilesClient.class);

    @Test
    public void testRefreshHFilesClient() throws Exception {
        addHFilesToRegions();
        Assert.assertEquals(2, TestRefreshHFilesBase.HTU.getNumHFiles(TestRefreshHFilesBase.TABLE_NAME, TestRefreshHFilesBase.FAMILY));
        RefreshHFilesClient tool = new RefreshHFilesClient(TestRefreshHFilesBase.HTU.getConfiguration());
        Assert.assertEquals(0, ToolRunner.run(tool, new String[]{ TestRefreshHFilesBase.TABLE_NAME.getNameAsString() }));
        Assert.assertEquals(4, TestRefreshHFilesBase.HTU.getNumHFiles(TestRefreshHFilesBase.TABLE_NAME, TestRefreshHFilesBase.FAMILY));
    }
}

