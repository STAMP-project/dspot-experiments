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
package org.apache.camel.processor.aggregate.cassandra;


import com.datastax.driver.core.Cluster;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Unite test for {@link CassandraAggregationRepository}
 */
public class CassandraAggregationTest extends CamelTestSupport {
    private Cluster cluster;

    private CassandraAggregationRepository aggregationRepository;

    @Test
    public void testAggregationRoute() throws Exception {
        if (!(CassandraAggregationTest.canTest())) {
            return;
        }
        // Given
        MockEndpoint mockOutput = getMockEndpoint("mock:output");
        mockOutput.expectedMessageCount(2);
        mockOutput.expectedBodiesReceivedInAnyOrder("A,C,E", "B,D");
        // When
        send("1", "A");
        send("2", "B");
        send("1", "C");
        send("2", "D");
        send("1", "E");
        // Then
        mockOutput.assertIsSatisfied(4000L);
    }
}

