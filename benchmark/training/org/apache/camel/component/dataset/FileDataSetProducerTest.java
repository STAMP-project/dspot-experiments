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


public class FileDataSetProducerTest extends ContextTestSupport {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    protected FileDataSet dataSet;

    final String testPayload = String.format("Line 1%nLine 2%nLine 3%nLine 4%nLine 5%nLine 6%nLine 7%nLine 8%nLine 9%nLine 10%n");

    final String sourceUri = "direct://source";

    final String dataSetName = "foo";

    final String dataSetUri = "dataset://" + (dataSetName);

    @Test
    public void testDefaultListDataSet() throws Exception {
        template.sendBodyAndHeader(sourceUri, testPayload, DATASET_INDEX, 0);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDefaultListDataSetWithSizeGreaterThanListSize() throws Exception {
        int messageCount = 20;
        dataSet.setSize(messageCount);
        getMockEndpoint(dataSetUri).expectedMessageCount(messageCount);
        for (int i = 0; i < messageCount; ++i) {
            template.sendBodyAndHeader(sourceUri, testPayload, DATASET_INDEX, i);
        }
        assertMockEndpointsSatisfied();
    }
}

