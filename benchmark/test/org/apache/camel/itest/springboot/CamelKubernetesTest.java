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
package org.apache.camel.itest.springboot;


import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class CamelKubernetesTest extends AbstractSpringBootTestSupport {
    @Test
    public void componentTests() throws Exception {
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-config-maps");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-deployments");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-hpa");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-job");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-namespaces");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-nodes");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-persistent-volumes-claims");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-persistent-volumes");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-pods");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-replication-controllers");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-resources-quota");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-secrets");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-service-accounts");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "kubernetes-services");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "openshift-builds");
        this.runComponentTest(CamelKubernetesTest.createTestConfig(), "openshift-build-configs");
        this.runModuleUnitTestsIfEnabled(config);
    }
}

