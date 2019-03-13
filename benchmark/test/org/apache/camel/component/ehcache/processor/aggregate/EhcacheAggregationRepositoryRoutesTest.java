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
package org.apache.camel.component.ehcache.processor.aggregate;


import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.ehcache.EhcacheTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class EhcacheAggregationRepositoryRoutesTest extends EhcacheTestSupport {
    private static final String ENDPOINT_MOCK = "mock:result";

    private static final String ENDPOINT_DIRECT = "direct:one";

    private static final int[] VALUES = EhcacheTestSupport.generateRandomArrayOfInt(10, 0, 30);

    private static final int SUM = IntStream.of(EhcacheAggregationRepositoryRoutesTest.VALUES).reduce(0, ( a, b) -> a + b);

    private static final String CORRELATOR = "CORRELATOR";

    @EndpointInject(uri = EhcacheAggregationRepositoryRoutesTest.ENDPOINT_MOCK)
    private MockEndpoint mock;

    @Produce(uri = EhcacheAggregationRepositoryRoutesTest.ENDPOINT_DIRECT)
    private ProducerTemplate producer;

    @Test
    public void checkAggregationFromOneRoute() throws Exception {
        mock.expectedMessageCount(EhcacheAggregationRepositoryRoutesTest.VALUES.length);
        mock.expectedBodiesReceived(EhcacheAggregationRepositoryRoutesTest.SUM);
        IntStream.of(EhcacheAggregationRepositoryRoutesTest.VALUES).forEach(( i) -> producer.sendBodyAndHeader(i, EhcacheAggregationRepositoryRoutesTest.CORRELATOR, EhcacheAggregationRepositoryRoutesTest.CORRELATOR));
        mock.assertIsSatisfied();
    }
}

