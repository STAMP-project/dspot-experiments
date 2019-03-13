/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.camel.component;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.writable.Writable;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


public class DataVecComponentTest extends CamelTestSupport {
    @ClassRule
    public static TemporaryFolder testDir = new TemporaryFolder();

    private static File dir;

    private static File irisFile;

    @Test
    public void testDataVec() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        // 1
        mock.expectedMessageCount(1);
        RecordReader reader = new CSVRecordReader();
        reader.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("iris.dat").getFile()));
        Collection<Collection<Writable>> recordAssertion = new ArrayList<>();
        while (reader.hasNext())
            recordAssertion.add(reader.next());

        mock.expectedBodiesReceived(recordAssertion);
        assertMockEndpointsSatisfied();
    }
}

