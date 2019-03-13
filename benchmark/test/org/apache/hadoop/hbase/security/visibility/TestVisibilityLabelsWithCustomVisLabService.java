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
package org.apache.hadoop.hbase.security.visibility;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SecurityTests.class, MediumTests.class })
public class TestVisibilityLabelsWithCustomVisLabService extends TestVisibilityLabels {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVisibilityLabelsWithCustomVisLabService.class);

    // Extending this test from super as we don't verify predefined labels in ExpAsStringVisibilityLabelServiceImpl
    @Override
    @Test
    public void testVisibilityLabelsInPutsThatDoesNotMatchAnyDefinedLabels() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        // This put with label "SAMPLE_LABEL" should not get failed.
        TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, "SAMPLE_LABEL", "TEST");
    }
}

