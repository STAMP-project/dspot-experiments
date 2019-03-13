/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.dataset;


import Exchange.DATASET_INDEX;
import org.apache.camel.ContextTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileDataSetProducerWithSplitTest extends ContextTestSupport {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    protected FileDataSet dataSet;

    final String testDataFileName = "src/test/data/file-dataset-test.txt";

    final int testDataFileRecordCount = 10;

    final String sourceUri = "direct://source";

    final String dataSetName = "foo";

    final String dataSetUri = "dataset://" + (dataSetName);

    @Test
    public void testDefaultListDataSet() throws Exception {
        for (int i = 0; i < (testDataFileRecordCount); ++i) {
            template.sendBodyAndHeader(sourceUri, ("Line " + (1 + i)), DATASET_INDEX, i);
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDefaultListDataSetWithSizeGreaterThanListSize() throws Exception {
        int messageCount = 20;
        dataSet.setSize(messageCount);
        getMockEndpoint(dataSetUri).expectedMessageCount(messageCount);
        for (int i = 0; i < messageCount; ++i) {
            template.sendBodyAndHeader(sourceUri, ("Line " + (1 + (i % (testDataFileRecordCount)))), DATASET_INDEX, i);
        }
        assertMockEndpointsSatisfied();
    }
}

