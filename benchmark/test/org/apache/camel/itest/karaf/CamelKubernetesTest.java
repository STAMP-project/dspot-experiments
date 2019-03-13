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
package org.apache.camel.itest.karaf;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;


@RunWith(PaxExam.class)
public class CamelKubernetesTest extends BaseKarafTest {
    public static final String COMPONENT = extractName(CamelKubernetesTest.class);

    @Test
    public void test() throws Exception {
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-config-maps");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-deployments");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-hpa");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-job");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-namespaces");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-nodes");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-persistent-volumes-claims");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-persistent-volumes");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-pods");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-replication-controllers");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-resources-quota");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-secrets");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-service-accounts");
        testComponent(CamelKubernetesTest.COMPONENT, "kubernetes-services");
        testComponent(CamelKubernetesTest.COMPONENT, "openshift-builds");
        testComponent(CamelKubernetesTest.COMPONENT, "openshift-build-configs");
    }
}

