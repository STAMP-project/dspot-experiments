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
package org.apache.hadoop.hbase.chaos.actions;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.IntegrationTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ MediumTests.class })
public class TestChangeSplitPolicyAction extends Action {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestChangeSplitPolicyAction.class);

    private static final IntegrationTestingUtility TEST_UTIL = new IntegrationTestingUtility();

    private static ChangeSplitPolicyAction action;

    private Admin admin;

    private TableName tableName = TableName.valueOf("ChangeSplitPolicyAction");

    @Test
    public void testChangeSplitPolicyAction() throws Exception {
        Action.ActionContext ctx = Mockito.mock(Action.ActionContext.class);
        Mockito.when(ctx.getHBaseIntegrationTestingUtility()).thenReturn(TestChangeSplitPolicyAction.TEST_UTIL);
        Mockito.when(ctx.getHBaseCluster()).thenReturn(getHBaseCluster());
        TestChangeSplitPolicyAction.action = new ChangeSplitPolicyAction(tableName);
        TestChangeSplitPolicyAction.action.init(ctx);
        TestChangeSplitPolicyAction.action.perform();
    }
}

