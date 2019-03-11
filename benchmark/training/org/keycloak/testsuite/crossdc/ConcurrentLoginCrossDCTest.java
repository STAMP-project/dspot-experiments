/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.crossdc;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.concurrency.AbstractConcurrencyTest;
import org.keycloak.testsuite.admin.concurrency.ConcurrentLoginTest;
import org.keycloak.testsuite.arquillian.LoadBalancerController;
import org.keycloak.testsuite.arquillian.annotation.InitialDcState;
import org.keycloak.testsuite.arquillian.annotation.LoadBalancer;

import static ServerSetup.ALL_NODES_IN_EVERY_DC;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@InitialDcState(authServers = ALL_NODES_IN_EVERY_DC)
public class ConcurrentLoginCrossDCTest extends ConcurrentLoginTest {
    @ArquillianResource
    @LoadBalancer(AbstractCrossDCTest.QUALIFIER_NODE_BALANCER)
    protected LoadBalancerController loadBalancerCtrl;

    @ArquillianResource
    protected ContainerController containerController;

    private static final int INVOCATIONS_BEFORE_SIMULATING_DC_FAILURE = 10;

    private static final int LOGIN_TASK_DELAY_MS = 100;

    private static final int LOGIN_TASK_RETRIES = 15;

    @Test
    public void concurrentLoginWithRandomDcFailures() throws Throwable {
        log.info("*********************************************");
        long start = System.currentTimeMillis();
        AtomicReference<String> userSessionId = new AtomicReference<>();
        ConcurrentLoginTest.LoginTask loginTask = null;
        try (CloseableHttpClient httpClient = getHttpsAwareClient()) {
            loginTask = new ConcurrentLoginTest.LoginTask(httpClient, userSessionId, ConcurrentLoginCrossDCTest.LOGIN_TASK_DELAY_MS, ConcurrentLoginCrossDCTest.LOGIN_TASK_RETRIES, false, Arrays.asList(createHttpClientContextForUser(httpClient, "test-user@localhost", "password")));
            HttpUriRequest request = handleLogin(getPageContent(oauth.getLoginFormUrl(), httpClient, HttpClientContext.create()), "test-user@localhost", "password");
            log.debug("Executing login request");
            Assert.assertTrue(parseAndCloseResponse(httpClient.execute(request)).contains("<title>AUTH_RESPONSE</title>"));
            run(ConcurrentLoginTest.DEFAULT_THREADS, ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT, loginTask, new ConcurrentLoginCrossDCTest.SwapDcAvailability());
            int clientSessionsCount = testingClient.testing().getClientSessionsCountInUserSession("test", userSessionId.get());
            Assert.assertEquals((1 + (ConcurrentLoginTest.DEFAULT_CLIENTS_COUNT)), clientSessionsCount);
        } finally {
            long end = (System.currentTimeMillis()) - start;
            log.infof("Statistics: %s", (loginTask == null ? "??" : loginTask.getHistogram()));
            log.info((("concurrentLoginWithRandomDcFailures took " + (end / 1000)) + "s"));
            log.info("*********************************************");
        }
    }

    private class SwapDcAvailability implements AbstractConcurrencyTest.KeycloakRunnable {
        private final AtomicInteger invocationCounter = new AtomicInteger();

        @Override
        public void run(int threadIndex, Keycloak keycloak, RealmResource realm) throws Throwable {
            final int currentInvocarion = invocationCounter.getAndIncrement();
            if ((currentInvocarion % (ConcurrentLoginCrossDCTest.INVOCATIONS_BEFORE_SIMULATING_DC_FAILURE)) == 0) {
                int failureIndex = currentInvocarion / (ConcurrentLoginCrossDCTest.INVOCATIONS_BEFORE_SIMULATING_DC_FAILURE);
                int dcToEnable = failureIndex % 2;
                int dcToDisable = (failureIndex + 1) % 2;
                // Ensure nodes from dcToEnable are available earlier then previous nodes from dcToDisable are disabled.
                suiteContext.getDcAuthServerBackendsInfo().get(dcToEnable).forEach(( c) -> org.keycloak.testsuite.crossdc.loadBalancerCtrl.enableBackendNodeByName(c.getQualifier()));
                suiteContext.getDcAuthServerBackendsInfo().get(dcToDisable).forEach(( c) -> org.keycloak.testsuite.crossdc.loadBalancerCtrl.disableBackendNodeByName(c.getQualifier()));
            }
        }
    }
}

