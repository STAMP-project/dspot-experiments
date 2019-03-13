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
package org.apache.camel.itest.cdi;


import Constants.EXPECTED_BODIES_A;
import Constants.EXPECTED_BODIES_B;
import Constants.EXPECTED_BODIES_C;
import Constants.EXPECTED_BODIES_D;
import Constants.EXPECTED_BODIES_D_A;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.cdi.Uri;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.CamelContextHelper;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Constants.EXPECTED_BODIES_D_A;


@RunWith(Arquillian.class)
public class CamelCdiTest {
    private static final Logger LOG = LoggerFactory.getLogger(CamelCdiTest.class);

    @Any
    @Inject
    Instance<CamelContext> camelContexts;

    @Inject
    @ContextName("contextA")
    RoutesContextA routesA;

    @Inject
    @ContextName("contextB")
    RoutesContextB routesB;

    @Inject
    @ContextName("contextC")
    RoutesContextC routesC;

    @Inject
    @ContextName("contextD")
    RoutesContextD routesD;

    @Inject
    @ContextName("contextD")
    @Uri("seda:foo")
    ProducerTemplate producerD;

    @Test
    public void checkContextsHaveCorrectEndpointsAndRoutes() throws Exception {
        Assert.assertNotNull("camelContexts not injected!", camelContexts);
        for (CamelContext camelContext : camelContexts) {
            CamelCdiTest.LOG.info(((("CamelContext " + camelContext) + " has endpoints: ") + (camelContext.getEndpointMap().keySet())));
            camelContext.start();
        }
        CamelContext contextA = assertCamelContext("contextA");
        CamelCdiTest.assertHasEndpoints(contextA, "seda://A.a", "mock://A.b");
        MockEndpoint mockEndpoint = routesA.b;
        mockEndpoint.expectedBodiesReceived(EXPECTED_BODIES_A);
        routesA.sendMessages();
        mockEndpoint.assertIsSatisfied();
        CamelContext contextB = assertCamelContext("contextB");
        CamelCdiTest.assertHasEndpoints(contextB, "seda://B.a", "mock://B.b");
        MockEndpoint mockEndpointB = routesB.b;
        mockEndpointB.expectedBodiesReceived(EXPECTED_BODIES_B);
        routesB.sendMessages();
        mockEndpointB.assertIsSatisfied();
        CamelContext contextC = assertCamelContext("contextC");
        CamelCdiTest.assertHasEndpoints(contextC, "seda://C.a", "mock://C.b");
        MockEndpoint mockEndpointC = routesC.b;
        mockEndpointC.expectedBodiesReceived(EXPECTED_BODIES_C);
        routesC.sendMessages();
        mockEndpointC.assertIsSatisfied();
        CamelContext contextD = assertCamelContext("contextD");
        CamelCdiTest.assertHasEndpoints(contextD, "seda://D.a", "mock://D.b");
        MockEndpoint mockEndpointD = routesD.b;
        mockEndpointD.expectedBodiesReceived(EXPECTED_BODIES_D);
        routesD.sendMessages();
        mockEndpointD.assertIsSatisfied();
        CamelContext contextE = assertCamelContext("contextD");
        CamelCdiTest.assertHasEndpoints(contextE, "seda://D.a", "mock://D.b");
        MockEndpoint mockDb = CamelContextHelper.getMandatoryEndpoint(contextE, "mock://D.b", MockEndpoint.class);
        mockDb.reset();
        mockDb.expectedBodiesReceived(EXPECTED_BODIES_D_A);
        for (Object body : EXPECTED_BODIES_D_A) {
            producerD.sendBody("seda:D.a", body);
        }
        mockDb.assertIsSatisfied();
    }
}

