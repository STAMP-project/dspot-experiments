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
package org.apache.camel.util.toolbox;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.NotifyBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;


/**
 * Unit tests for the {@link FlexibleAggregationStrategy}.
 *
 * @since 2.12
 */
public class FlexibleAggregationStrategiesTest extends ContextTestSupport {
    private final CountDownLatch completionLatch = new CountDownLatch(1);

    private final CountDownLatch timeoutLatch = new CountDownLatch(1);

    @Test
    @SuppressWarnings("unchecked")
    public void testFlexibleAggregationStrategyNoCondition() throws Exception {
        getMockEndpoint("mock:result1").expectedMessageCount(1);
        getMockEndpoint("mock:result1").message(0).body().isInstanceOf(ArrayList.class);
        template.sendBodyAndHeader("direct:start1", "AGGREGATE1", "id", "123");
        template.sendBodyAndHeader("direct:start1", "AGGREGATE2", "id", "123");
        template.sendBodyAndHeader("direct:start1", "AGGREGATE3", "id", "123");
        template.sendBodyAndHeader("direct:start1", "AGGREGATE4", "id", "123");
        template.sendBodyAndHeader("direct:start1", "AGGREGATE5", "id", "123");
        assertMockEndpointsSatisfied();
        List<String> resultList = getMockEndpoint("mock:result1").getReceivedExchanges().get(0).getIn().getBody(List.class);
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(("AGGREGATE" + (i + 1)), resultList.get(i));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFlexibleAggregationStrategyCondition() throws Exception {
        getMockEndpoint("mock:result1").expectedMessageCount(1);
        getMockEndpoint("mock:result1").message(0).body().isInstanceOf(ArrayList.class);
        template.sendBodyAndHeader("direct:start1", "AGGREGATE1", "id", "123");
        template.sendBodyAndHeader("direct:start1", "DISCARD", "id", "123");
        template.sendBodyAndHeader("direct:start1", "AGGREGATE2", "id", "123");
        template.sendBodyAndHeader("direct:start1", "DISCARD", "id", "123");
        template.sendBodyAndHeader("direct:start1", "AGGREGATE3", "id", "123");
        assertMockEndpointsSatisfied();
        List<String> resultList = getMockEndpoint("mock:result1").getReceivedExchanges().get(0).getIn().getBody(List.class);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(("AGGREGATE" + (i + 1)), resultList.get(i));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFlexibleAggregationStrategyStoreInPropertyHashSet() throws Exception {
        getMockEndpoint("mock:result2").expectedMessageCount(1);
        getMockEndpoint("mock:result2").message(0).exchangeProperty("AggregationResult").isInstanceOf(HashSet.class);
        template.sendBodyAndHeader("direct:start2", "ignored body", "input", "AGGREGATE1");
        template.sendBodyAndHeader("direct:start2", "ignored body", "input", "DISCARD");
        template.sendBodyAndHeader("direct:start2", "ignored body", "input", "AGGREGATE2");
        template.sendBodyAndHeader("direct:start2", "ignored body", "input", "DISCARD");
        template.sendBodyAndHeader("direct:start2", "ignored body", "input", "AGGREGATE3");
        assertMockEndpointsSatisfied();
        HashSet<String> resultSet = getMockEndpoint("mock:result2").getReceivedExchanges().get(0).getProperty("AggregationResult", HashSet.class);
        Assert.assertEquals(3, resultSet.size());
        Assert.assertTrue((((resultSet.contains("AGGREGATE1")) && (resultSet.contains("AGGREGATE2"))) && (resultSet.contains("AGGREGATE3"))));
    }

    @Test
    public void testFlexibleAggregationStrategyStoreInHeaderSingleValue() throws Exception {
        getMockEndpoint("mock:result3").expectedMessageCount(1);
        getMockEndpoint("mock:result3").message(0).header("AggregationResult").isInstanceOf(String.class);
        getMockEndpoint("mock:result3").message(0).header("AggregationResult").isEqualTo("AGGREGATE3");
        template.sendBody("direct:start3", "AGGREGATE1");
        template.sendBody("direct:start3", "AGGREGATE2");
        template.sendBody("direct:start3", "AGGREGATE3");
        assertMockEndpointsSatisfied();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFlexibleAggregationStrategyGenericArrayListWithoutNulls() throws Exception {
        getMockEndpoint("mock:result4").expectedMessageCount(1);
        getMockEndpoint("mock:result4").message(0).body().isInstanceOf(ArrayList.class);
        template.sendBody("direct:start4", "AGGREGATE1");
        template.sendBody("direct:start4", 123.0);
        template.sendBody("direct:start4", null);
        assertMockEndpointsSatisfied();
        ArrayList list = getMockEndpoint("mock:result4").getReceivedExchanges().get(0).getIn().getBody(ArrayList.class);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("AGGREGATE1"));
        Assert.assertTrue(list.contains(123.0));
    }

    @Test
    public void testFlexibleAggregationStrategyFailWithInvalidCast() throws Exception {
        getMockEndpoint("mock:result5").expectedMessageCount(0);
        try {
            template.sendBody("direct:start5", "AGGREGATE1");
        } catch (Exception exception) {
            assertMockEndpointsSatisfied();
            return;
        }
        Assert.fail("Type Conversion exception expected, as we are not ignoring invalid casts");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testFlexibleAggregationStrategyFailOnInvalidCast() throws Exception {
        getMockEndpoint("mock:result6").expectedMessageCount(1);
        getMockEndpoint("mock:result6").message(0).body().isInstanceOf(ArrayList.class);
        template.sendBody("direct:start6", "AGGREGATE1");
        template.sendBody("direct:start6", "AGGREGATE2");
        template.sendBody("direct:start6", "AGGREGATE3");
        ArrayList list = getMockEndpoint("mock:result6").getReceivedExchanges().get(0).getIn().getBody(ArrayList.class);
        Assert.assertEquals(3, list.size());
        for (Object object : list) {
            Assert.assertNull(object);
        }
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testFlexibleAggregationStrategyTimeoutCompletionMixins() throws Exception {
        getMockEndpoint("mock:result.timeoutAndCompletionAware").expectedMessageCount(2);
        getMockEndpoint("mock:result.timeoutAndCompletionAware").message(0).body().isEqualTo("AGGREGATE1");
        getMockEndpoint("mock:result.timeoutAndCompletionAware").message(0).exchangeProperty("Timeout").isEqualTo(true);
        getMockEndpoint("mock:result.timeoutAndCompletionAware").message(1).body().isEqualTo("AGGREGATE3");
        template.sendBody("direct:start.timeoutAndCompletionAware", "AGGREGATE1");
        Assert.assertTrue(timeoutLatch.await(2500, TimeUnit.MILLISECONDS));
        template.sendBody("direct:start.timeoutAndCompletionAware", "AGGREGATE2");
        template.sendBody("direct:start.timeoutAndCompletionAware", "AGGREGATE3");
        Assert.assertTrue(completionLatch.await(2500, TimeUnit.MILLISECONDS));
        getMockEndpoint("mock:result.timeoutAndCompletionAware").getReceivedExchanges();
        assertMockEndpointsSatisfied();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFlexibleAggregationStrategyPickXPath() throws Exception {
        getMockEndpoint("mock:result.xpath1").expectedMessageCount(1);
        getMockEndpoint("mock:result.xpath1").message(0).body().isInstanceOf(ArrayList.class);
        template.sendBody("direct:start.xpath1", "<envelope><result>ok</result></envelope>");
        template.sendBody("direct:start.xpath1", "<envelope><result>error</result></envelope>");
        template.sendBody("direct:start.xpath1", "<envelope>no result</envelope>");
        assertMockEndpointsSatisfied();
        ArrayList<Node> list = getMockEndpoint("mock:result.xpath1").getReceivedExchanges().get(0).getIn().getBody(ArrayList.class);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("ok", list.get(0).getTextContent());
        Assert.assertEquals("error", list.get(1).getTextContent());
    }

    @Test
    public void testLinkedList() throws Exception {
        NotifyBuilder notify = whenDone(1).and().whenExactlyFailed(0).create();
        template.sendBody("direct:linkedlist", Arrays.asList("FIRST", "SECOND"));
        Assert.assertTrue(notify.matches(10, TimeUnit.SECONDS));
    }

    @Test
    public void testHashSet() throws Exception {
        HashSet<String> r = new HashSet<>();
        r.add("FIRST");
        r.add("SECOND");
        NotifyBuilder notify = whenDone(1).and().whenExactlyFailed(0).create();
        Set result = template.requestBody("direct:hashset", Arrays.asList("FIRST", "SECOND"), Set.class);
        Assert.assertTrue(notify.matches(10, TimeUnit.SECONDS));
        Assert.assertEquals(r, result);
    }
}

