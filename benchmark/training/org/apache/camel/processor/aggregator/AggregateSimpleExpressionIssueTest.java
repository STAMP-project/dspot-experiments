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
package org.apache.camel.processor.aggregator;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("Manual unit test")
public class AggregateSimpleExpressionIssueTest extends ContextTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(AggregateSimpleExpressionIssueTest.class);

    private static final String DATA = "100,200,1,123456,2010-03-01T12:13:14,100,USD,Best Buy,5045,Santa Monica,CA,Type\n";

    private AggregateSimpleExpressionIssueTest.MyBean myBean = new AggregateSimpleExpressionIssueTest.MyBean();

    private AggregateSimpleExpressionIssueTest.AggStrategy aggStrategy = new AggregateSimpleExpressionIssueTest.AggStrategy();

    @Test
    public void testDummy() throws Exception {
        // noop
    }

    public static final class MyBean {
        private volatile int cnt;

        public void invoke(final List<String> strList) {
            AggregateSimpleExpressionIssueTest.LOG.info(("Batch " + (++(cnt))));
        }
    }

    public static final class AggStrategy implements AggregationStrategy {
        private final int batchSize = 1000;

        @SuppressWarnings("unchecked")
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            String str = newExchange.getIn().getBody(String.class);
            if (oldExchange == null) {
                List<String> list = new ArrayList<>(batchSize);
                list.add(str);
                newExchange.getIn().setBody(list);
                return newExchange;
            }
            List<String> list = oldExchange.getIn().getBody(List.class);
            list.add(str);
            return oldExchange;
        }
    }
}

