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
package org.apache.camel.component.kubernetes.consumer;


import KubernetesConstants.KUBERNETES_EVENT_ACTION;
import KubernetesConstants.KUBERNETES_NAMESPACE_NAME;
import KubernetesConstants.KUBERNETES_SERVICE_LABELS;
import KubernetesConstants.KUBERNETES_SERVICE_NAME;
import KubernetesConstants.KUBERNETES_SERVICE_SPEC;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.kubernetes.KubernetesTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.util.ObjectHelper;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Requires a running Kubernetes Cluster")
public class KubernetesServicesConsumerTest extends KubernetesTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mockResultEndpoint;

    @Test
    public void createAndDeleteService() throws Exception {
        if (ObjectHelper.isEmpty(authToken)) {
            return;
        }
        mockResultEndpoint.expectedMessageCount(2);
        mockResultEndpoint.expectedHeaderValuesReceivedInAnyOrder(KUBERNETES_EVENT_ACTION, "ADDED", "DELETED");
        Exchange ex = template.request("direct:createService", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KUBERNETES_NAMESPACE_NAME, "default");
                exchange.getIn().setHeader(KUBERNETES_SERVICE_NAME, "test");
                Map<String, String> labels = new HashMap<>();
                labels.put("this", "rocks");
                exchange.getIn().setHeader(KUBERNETES_SERVICE_LABELS, labels);
                ServiceSpec serviceSpec = new ServiceSpec();
                List<ServicePort> lsp = new ArrayList<>();
                ServicePort sp = new ServicePort();
                sp.setPort(8080);
                sp.setTargetPort(new IntOrString(8080));
                sp.setProtocol("TCP");
                lsp.add(sp);
                serviceSpec.setPorts(lsp);
                Map<String, String> selectorMap = new HashMap<>();
                selectorMap.put("containter", "test");
                serviceSpec.setSelector(selectorMap);
                exchange.getIn().setHeader(KUBERNETES_SERVICE_SPEC, serviceSpec);
            }
        });
        Service serv = ex.getOut().getBody(Service.class);
        assertEquals(serv.getMetadata().getName(), "test");
        ex = template.request("direct:deleteService", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(KUBERNETES_NAMESPACE_NAME, "default");
                exchange.getIn().setHeader(KUBERNETES_SERVICE_NAME, "test");
            }
        });
        boolean servDeleted = ex.getOut().getBody(Boolean.class);
        assertTrue(servDeleted);
        Thread.sleep(3000);
        mockResultEndpoint.assertIsSatisfied();
    }

    public class KubernertesProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Message in = exchange.getIn();
            log.info(((("Got event with body: " + (in.getBody())) + " and action ") + (in.getHeader(KUBERNETES_EVENT_ACTION))));
        }
    }
}

