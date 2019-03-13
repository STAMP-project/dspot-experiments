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
package org.apache.camel.component.openshift.producer;


import KubernetesConstants.KUBERNETES_BUILD_CONFIGS_LABELS;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.BuildConfigListBuilder;
import io.fabric8.openshift.client.server.mock.OpenShiftServer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.kubernetes.KubernetesTestSupport;
import org.junit.Rule;
import org.junit.Test;


public class OpenshiftBuildConfigsProducerTest extends KubernetesTestSupport {
    @Rule
    public OpenShiftServer server = new OpenShiftServer();

    @Test
    public void listTest() throws Exception {
        server.expect().withPath("/oapi/v1/buildconfigs").andReturn(200, new BuildConfigListBuilder().addNewItem().and().addNewItem().and().build()).once();
        List<BuildConfig> result = template.requestBody("direct:list", "", List.class);
        assertEquals(2, result.size());
    }

    @Test
    public void listByLabelsTest() throws Exception {
        server.expect().withPath(("/oapi/v1/buildconfigs?labelSelector=" + (KubernetesTestSupport.toUrlEncoded("key1=value1,key2=value2")))).andReturn(200, new BuildConfigListBuilder().addNewItem().and().addNewItem().and().build()).once();
        Exchange ex = template.request("direct:listByLabels", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Map<String, String> labels = new HashMap<>();
                labels.put("key1", "value1");
                labels.put("key2", "value2");
                exchange.getIn().setHeader(KUBERNETES_BUILD_CONFIGS_LABELS, labels);
            }
        });
        List<BuildConfig> result = ex.getOut().getBody(List.class);
        assertEquals(2, result.size());
    }
}

