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
package org.apache.camel.model;


import RoutingSlipDefinition.DEFAULT_DELIMITER;
import java.util.List;
import org.apache.camel.TestSupport;
import org.apache.camel.model.loadbalancer.FailoverLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.RandomLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.RoundRobinLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.StickyLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.TopicLoadBalancerDefinition;
import org.junit.Assert;
import org.junit.Test;


public class XmlParseTest extends XmlTestSupport {
    @Test
    public void testParseSimpleRouteXml() throws Exception {
        RouteDefinition route = assertOneRoute("simpleRoute.xml");
        assertFrom(route, "seda:a");
        assertChildTo("to", route, "seda:b");
    }

    @Test
    public void testParseProcessorXml() throws Exception {
        RouteDefinition route = assertOneRoute("processor.xml");
        assertFrom(route, "seda:a");
        ProcessDefinition to = assertOneProcessorInstanceOf(ProcessDefinition.class, route);
        Assert.assertEquals("Processor ref", "myProcessor", to.getRef());
    }

    @Test
    public void testParseProcessorWithFilterXml() throws Exception {
        RouteDefinition route = assertOneRoute("processorWithFilter.xml");
        assertFrom(route, "seda:a");
        FilterDefinition filter = assertOneProcessorInstanceOf(FilterDefinition.class, route);
        assertExpression(filter.getExpression(), "juel", "in.header.foo == 'bar'");
    }

    @Test
    public void testParseProcessorWithHeaderFilterXml() throws Exception {
        RouteDefinition route = assertOneRoute("processorWithHeaderFilter.xml");
        assertFrom(route, "seda:a");
        FilterDefinition filter = assertOneProcessorInstanceOf(FilterDefinition.class, route);
        assertExpression(filter.getExpression(), "header", "foo");
    }

    @Test
    public void testParseProcessorWithSimpleFilterXml() throws Exception {
        RouteDefinition route = assertOneRoute("processorWithSimpleFilter.xml");
        assertFrom(route, "seda:a");
        FilterDefinition filter = assertOneProcessorInstanceOf(FilterDefinition.class, route);
        assertExpression(filter.getExpression(), "simple", "${in.header.foo} == 'bar'");
    }

    @Test
    public void testParseProcessorWithGroovyFilterXml() throws Exception {
        RouteDefinition route = assertOneRoute("processorWithGroovyFilter.xml");
        assertFrom(route, "seda:a");
        FilterDefinition filter = assertOneProcessorInstanceOf(FilterDefinition.class, route);
        assertExpression(filter.getExpression(), "groovy", "in.headers.any { h -> h.startsWith('foo')}");
    }

    @Test
    public void testParseRecipientListXml() throws Exception {
        RouteDefinition route = assertOneRoute("dynamicRecipientList.xml");
        assertFrom(route, "seda:a");
        RecipientListDefinition<?> node = assertOneProcessorInstanceOf(RecipientListDefinition.class, route);
        assertExpression(node.getExpression(), "header", "foo");
    }

    @Test
    public void testParseStaticRecipientListXml() throws Exception {
        RouteDefinition route = assertOneRoute("staticRecipientList.xml");
        assertFrom(route, "seda:a");
        assertChildTo(route, "seda:b", "seda:c", "seda:d");
    }

    @Test
    public void testParseTransformXml() throws Exception {
        RouteDefinition route = assertOneRoute("transform.xml");
        assertFrom(route, "direct:start");
        TransformDefinition node = assertNthProcessorInstanceOf(TransformDefinition.class, route, 0);
        assertExpression(node.getExpression(), "simple", "${in.body} extra data!");
        assertChildTo(route, "mock:end", 1);
    }

    @Test
    public void testParseSagaXml() throws Exception {
        RouteDefinition route = assertOneRoute("saga.xml");
        assertFrom(route, "direct:start");
        SagaDefinition node = assertNthProcessorInstanceOf(SagaDefinition.class, route, 0);
        Assert.assertNotNull(node.getCompensation());
        assertChildTo(route, "mock:end", 2);
    }

    @Test
    public void testParseScriptXml() throws Exception {
        RouteDefinition route = assertOneRoute("script.xml");
        assertFrom(route, "direct:start");
        ScriptDefinition node = assertNthProcessorInstanceOf(ScriptDefinition.class, route, 0);
        assertExpression(node.getExpression(), "groovy", "System.out.println(\"groovy was here\")");
        assertChildTo(route, "mock:end", 1);
    }

    @Test
    public void testParseSetBodyXml() throws Exception {
        RouteDefinition route = assertOneRoute("setBody.xml");
        assertFrom(route, "direct:start");
        SetBodyDefinition node = assertNthProcessorInstanceOf(SetBodyDefinition.class, route, 0);
        assertExpression(node.getExpression(), "simple", "${in.body} extra data!");
        assertChildTo(route, "mock:end", 1);
    }

    @Test
    public void testParseSetHeaderXml() throws Exception {
        RouteDefinition route = assertOneRoute("setHeader.xml");
        assertFrom(route, "seda:a");
        SetHeaderDefinition node = assertNthProcessorInstanceOf(SetHeaderDefinition.class, route, 0);
        Assert.assertEquals("oldBodyValue", node.getHeaderName());
        assertExpression(node.getExpression(), "simple", "body");
        assertChildTo(route, "mock:b", 1);
    }

    @Test
    public void testParseSetHeaderToConstantXml() throws Exception {
        RouteDefinition route = assertOneRoute("setHeaderToConstant.xml");
        assertFrom(route, "seda:a");
        SetHeaderDefinition node = assertNthProcessorInstanceOf(SetHeaderDefinition.class, route, 0);
        Assert.assertEquals("theHeader", node.getHeaderName());
        assertExpression(node.getExpression(), "constant", "a value");
        assertChildTo(route, "mock:b", 1);
    }

    @Test
    public void testParseConvertBodyXml() throws Exception {
        RouteDefinition route = assertOneRoute("convertBody.xml");
        assertFrom(route, "seda:a");
        ConvertBodyDefinition node = assertOneProcessorInstanceOf(ConvertBodyDefinition.class, route);
        Assert.assertEquals("java.lang.Integer", node.getType());
    }

    @Test
    public void testParseRoutingSlipXml() throws Exception {
        RouteDefinition route = assertOneRoute("routingSlip.xml");
        assertFrom(route, "seda:a");
        RoutingSlipDefinition<?> node = assertOneProcessorInstanceOf(RoutingSlipDefinition.class, route);
        Assert.assertEquals("destinations", node.getExpression().getExpression());
        Assert.assertEquals(DEFAULT_DELIMITER, node.getUriDelimiter());
    }

    @Test
    public void testParseRoutingSlipWithHeaderSetXml() throws Exception {
        RouteDefinition route = assertOneRoute("routingSlipHeaderSet.xml");
        assertFrom(route, "seda:a");
        RoutingSlipDefinition<?> node = assertOneProcessorInstanceOf(RoutingSlipDefinition.class, route);
        Assert.assertEquals("theRoutingSlipHeader", node.getExpression().getExpression());
        Assert.assertEquals(DEFAULT_DELIMITER, node.getUriDelimiter());
    }

    @Test
    public void testParseRoutingSlipWithHeaderAndDelimiterSetXml() throws Exception {
        RouteDefinition route = assertOneRoute("routingSlipHeaderAndDelimiterSet.xml");
        assertFrom(route, "seda:a");
        RoutingSlipDefinition<?> node = assertOneProcessorInstanceOf(RoutingSlipDefinition.class, route);
        Assert.assertEquals("theRoutingSlipHeader", node.getExpression().getExpression());
        Assert.assertEquals("#", node.getUriDelimiter());
    }

    @Test
    public void testParseRouteWithChoiceXml() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithChoice.xml");
        assertFrom(route, "seda:a");
        ChoiceDefinition choice = assertOneProcessorInstanceOf(ChoiceDefinition.class, route);
        List<WhenDefinition> whens = TestSupport.assertListSize(choice.getWhenClauses(), 2);
        assertChildTo("when(0)", whens.get(0), "seda:b");
        assertChildTo("when(1)", whens.get(1), "seda:c");
        OtherwiseDefinition otherwise = choice.getOtherwise();
        Assert.assertNotNull("Otherwise is null", otherwise);
        assertChildTo("otherwise", otherwise, "seda:d");
    }

    @Test
    public void testParseSplitterXml() throws Exception {
        RouteDefinition route = assertOneRoute("splitter.xml");
        assertFrom(route, "seda:a");
        SplitDefinition splitter = assertOneProcessorInstanceOf(SplitDefinition.class, route);
        assertExpression(splitter.getExpression(), "xpath", "/foo/bar");
        assertChildTo("to", splitter, "seda:b");
    }

    @Test
    public void testParseLoadBalance() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithLoadBalance.xml");
        assertFrom(route, "seda:a");
        LoadBalanceDefinition loadBalance = assertOneProcessorInstanceOf(LoadBalanceDefinition.class, route);
        Assert.assertEquals("Here should have 3 output here", 3, loadBalance.getOutputs().size());
        Assert.assertTrue("The loadBalancer should be RoundRobinLoadBalancerDefinition", ((loadBalance.getLoadBalancerType()) instanceof RoundRobinLoadBalancerDefinition));
    }

    @Test
    public void testParseStickyLoadBalance() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithStickyLoadBalance.xml");
        assertFrom(route, "seda:a");
        LoadBalanceDefinition loadBalance = assertOneProcessorInstanceOf(LoadBalanceDefinition.class, route);
        Assert.assertEquals("Here should have 3 output here", 3, loadBalance.getOutputs().size());
        Assert.assertTrue("The loadBalancer should be StickyLoadBalancerDefinition", ((loadBalance.getLoadBalancerType()) instanceof StickyLoadBalancerDefinition));
        StickyLoadBalancerDefinition strategy = ((StickyLoadBalancerDefinition) (loadBalance.getLoadBalancerType()));
        Assert.assertNotNull("the expression should not be null ", strategy.getCorrelationExpression());
    }

    @Test
    public void testParseFailoverLoadBalance() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithFailoverLoadBalance.xml");
        assertFrom(route, "seda:a");
        LoadBalanceDefinition loadBalance = assertOneProcessorInstanceOf(LoadBalanceDefinition.class, route);
        Assert.assertEquals("Here should have 3 output here", 3, loadBalance.getOutputs().size());
        Assert.assertTrue("The loadBalancer should be FailoverLoadBalancerDefinition", ((loadBalance.getLoadBalancerType()) instanceof FailoverLoadBalancerDefinition));
        FailoverLoadBalancerDefinition strategy = ((FailoverLoadBalancerDefinition) (loadBalance.getLoadBalancerType()));
        Assert.assertEquals("there should be 2 exceptions", 2, strategy.getExceptions().size());
    }

    @Test
    public void testParseRandomLoadBalance() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithRandomLoadBalance.xml");
        assertFrom(route, "seda:a");
        LoadBalanceDefinition loadBalance = assertOneProcessorInstanceOf(LoadBalanceDefinition.class, route);
        Assert.assertEquals("Here should have 3 output here", 3, loadBalance.getOutputs().size());
        Assert.assertTrue("The loadBalancer should be RandomLoadBalancerDefinition", ((loadBalance.getLoadBalancerType()) instanceof RandomLoadBalancerDefinition));
    }

    @Test
    public void testParseTopicLoadBalance() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithTopicLoadBalance.xml");
        assertFrom(route, "seda:a");
        LoadBalanceDefinition loadBalance = assertOneProcessorInstanceOf(LoadBalanceDefinition.class, route);
        Assert.assertEquals("Here should have 3 output here", 3, loadBalance.getOutputs().size());
        Assert.assertTrue("The loadBalancer should be TopicLoadBalancerDefinition", ((loadBalance.getLoadBalancerType()) instanceof TopicLoadBalancerDefinition));
    }

    @Test
    public void testParseHL7DataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithHL7DataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseXStreamDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithXStreamDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseJibxDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithJibxDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseXMLSecurityDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithXMLSecurityDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseTidyMarkupDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithTidyMarkupDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseRSSDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithRSSDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseJSonDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithJSonDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseJaxbDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithJaxbDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseFlatpackDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithFlatpackDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseCvsDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithCvsDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseZipFileDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithZipFileDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseBindyDataFormat() throws Exception {
        RouteDefinition route = assertOneRoute("routeWithBindyDataFormat.xml");
        assertFrom(route, "seda:a");
    }

    @Test
    public void testParseBatchResequencerXml() throws Exception {
        RouteDefinition route = assertOneRoute("resequencerBatch.xml");
        ResequenceDefinition resequencer = assertOneProcessorInstanceOf(ResequenceDefinition.class, route);
        Assert.assertNull(resequencer.getStreamConfig());
        Assert.assertNotNull(resequencer.getBatchConfig());
        Assert.assertEquals(500, resequencer.getBatchConfig().getBatchSize());
        Assert.assertEquals(2000L, resequencer.getBatchConfig().getBatchTimeout());
    }

    @Test
    public void testParseStreamResequencerXml() throws Exception {
        RouteDefinition route = assertOneRoute("resequencerStream.xml");
        ResequenceDefinition resequencer = assertOneProcessorInstanceOf(ResequenceDefinition.class, route);
        Assert.assertNotNull(resequencer.getStreamConfig());
        Assert.assertNull(resequencer.getBatchConfig());
        Assert.assertEquals(1000, resequencer.getStreamConfig().getCapacity());
        Assert.assertEquals(2000L, resequencer.getStreamConfig().getTimeout());
    }

    @Test
    public void testLoop() throws Exception {
        RouteDefinition route = assertOneRoute("loop.xml");
        LoopDefinition loop = assertOneProcessorInstanceOf(LoopDefinition.class, route);
        Assert.assertNotNull(loop.getExpression());
        Assert.assertEquals("constant", loop.getExpression().getLanguage());
    }
}

