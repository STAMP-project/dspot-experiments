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
package org.apache.hadoop.hbase.backup;


import TableBackupClient.BACKUP_CLIENT_IMPL_CLASS;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.backup.impl.TableBackupClient.Stage;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestFullBackupWithFailures extends TestBackupBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFullBackupWithFailures.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFullBackupWithFailures.class);

    @Test
    public void testFullBackupWithFailures() throws Exception {
        TestBackupBase.conf1.set(BACKUP_CLIENT_IMPL_CLASS, TestBackupBase.FullTableBackupClientForTest.class.getName());
        int maxStage = (Stage.values().length) - 1;
        // Fail stages between 0 and 4 inclusive
        for (int stage = 0; stage <= maxStage; stage++) {
            TestFullBackupWithFailures.LOG.info(("Running stage " + stage));
            runBackupAndFailAtStage(stage);
        }
    }
}

