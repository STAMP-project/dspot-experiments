/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.camel.cdi.std;


import FlowableProducer.PROCESS_ID_PROPERTY;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.flowable.camel.cdi.BaseCamelCdiFlowableTestCase;
import org.flowable.cdi.impl.util.ProgrammaticBeanLookup;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.junit.Assert;
import org.junit.Test;


/**
 * Adapted from {@link SimpleProcessTest}.
 *
 * @author Zach Visagie
 */
public class CdiSimpleProcessTest extends StdCamelCdiFlowableTestCase {
    @Inject
    protected CamelContext camelContext;

    protected MockEndpoint service1;

    protected MockEndpoint service2;

    @Test
    @Deployment(resources = { "process/example.bpmn20.xml" })
    public void testRunProcess() throws Exception {
        CamelContext ctx = ProgrammaticBeanLookup.lookup(CamelContext.class);
        ProducerTemplate tpl = ctx.createProducerTemplate();
        service1.expectedBodiesReceived("ala");
        Exchange exchange = ctx.getEndpoint("direct:start").createExchange();
        exchange.getIn().setBody(Collections.singletonMap("var1", "ala"));
        tpl.send("direct:start", exchange);
        String instanceId = ((String) (exchange.getProperty("PROCESS_ID_PROPERTY")));
        ProcessInstance processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        Assert.assertFalse(processInstance.isEnded());
        tpl.sendBodyAndProperty("direct:receive", null, PROCESS_ID_PROPERTY, instanceId);
        // check process ended
        processInstance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        Assert.assertNull(processInstance);
        service1.assertIsSatisfied();
        Map<?, ?> m = service2.getExchanges().get(0).getIn().getBody(Map.class);
        Assert.assertEquals("ala", m.get("var1"));
        Assert.assertEquals("var2", m.get("var2"));
    }
}

