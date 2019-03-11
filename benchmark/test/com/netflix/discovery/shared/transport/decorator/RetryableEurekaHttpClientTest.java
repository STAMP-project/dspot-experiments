/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.discovery.shared.transport.decorator;


import com.netflix.discovery.shared.resolver.ClusterResolver;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.resolver.aws.SampleCluster;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.shared.transport.TransportException;
import com.netflix.discovery.shared.transport.decorator.EurekaHttpClientDecorator.RequestExecutor;
import com.netflix.discovery.shared.transport.decorator.EurekaHttpClientDecorator.RequestType;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Tomasz Bak
 */
public class RetryableEurekaHttpClientTest {
    private static final int NUMBER_OF_RETRIES = 2;

    private static final int CLUSTER_SIZE = 3;

    public static final RequestType TEST_REQUEST_TYPE = RequestType.Register;

    private static final List<AwsEndpoint> CLUSTER_ENDPOINTS = SampleCluster.UsEast1a.builder().withServerPool(RetryableEurekaHttpClientTest.CLUSTER_SIZE).build();

    private final EurekaTransportConfig transportConfig = Mockito.mock(EurekaTransportConfig.class);

    private final ClusterResolver clusterResolver = Mockito.mock(ClusterResolver.class);

    private final TransportClientFactory clientFactory = Mockito.mock(TransportClientFactory.class);

    private final ServerStatusEvaluator serverStatusEvaluator = ServerStatusEvaluators.legacyEvaluator();

    private final RequestExecutor<Void> requestExecutor = Mockito.mock(RequestExecutor.class);

    private RetryableEurekaHttpClient retryableClient;

    private List<EurekaHttpClient> clusterDelegates;

    @Test
    public void testRequestsReuseSameConnectionIfThereIsNoError() throws Exception {
        Mockito.when(clientFactory.newClient(Matchers.<EurekaEndpoint>anyVararg())).thenReturn(clusterDelegates.get(0));
        Mockito.when(requestExecutor.execute(clusterDelegates.get(0))).thenReturn(EurekaHttpResponse.status(200));
        // First request creates delegate, second reuses it
        for (int i = 0; i < 3; i++) {
            EurekaHttpResponse<Void> httpResponse = retryableClient.execute(requestExecutor);
            Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        }
        Mockito.verify(clientFactory, Mockito.times(1)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(requestExecutor, Mockito.times(3)).execute(clusterDelegates.get(0));
    }

    @Test
    public void testRequestIsRetriedOnConnectionError() throws Exception {
        Mockito.when(clientFactory.newClient(Matchers.<EurekaEndpoint>anyVararg())).thenReturn(clusterDelegates.get(0), clusterDelegates.get(1));
        Mockito.when(requestExecutor.execute(clusterDelegates.get(0))).thenThrow(new TransportException("simulated network error"));
        Mockito.when(requestExecutor.execute(clusterDelegates.get(1))).thenReturn(EurekaHttpResponse.status(200));
        EurekaHttpResponse<Void> httpResponse = retryableClient.execute(requestExecutor);
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Mockito.verify(clientFactory, Mockito.times(2)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(requestExecutor, Mockito.times(1)).execute(clusterDelegates.get(0));
        Mockito.verify(requestExecutor, Mockito.times(1)).execute(clusterDelegates.get(1));
    }

    @Test(expected = TransportException.class)
    public void testErrorResponseIsReturnedIfRetryLimitIsReached() throws Exception {
        simulateTransportError(0, ((RetryableEurekaHttpClientTest.NUMBER_OF_RETRIES) + 1));
        retryableClient.execute(requestExecutor);
    }

    @Test
    public void testQuarantineListIsResetWhenNoMoreServerAreAvailable() throws Exception {
        // First two call fail
        simulateTransportError(0, RetryableEurekaHttpClientTest.CLUSTER_SIZE);
        for (int i = 0; i < 2; i++) {
            executeWithTransportErrorExpectation();
        }
        // Second call, should reset cluster quarantine list, and hit health node 0
        Mockito.when(clientFactory.newClient(Matchers.<EurekaEndpoint>anyVararg())).thenReturn(clusterDelegates.get(0));
        Mockito.reset(requestExecutor);
        Mockito.when(requestExecutor.execute(clusterDelegates.get(0))).thenReturn(EurekaHttpResponse.status(200));
        retryableClient.execute(requestExecutor);
    }

    @Test
    public void test5xxStatusCodeResultsInRequestRetry() throws Exception {
        Mockito.when(clientFactory.newClient(Matchers.<EurekaEndpoint>anyVararg())).thenReturn(clusterDelegates.get(0), clusterDelegates.get(1));
        Mockito.when(requestExecutor.execute(clusterDelegates.get(0))).thenReturn(EurekaHttpResponse.status(500));
        Mockito.when(requestExecutor.execute(clusterDelegates.get(1))).thenReturn(EurekaHttpResponse.status(200));
        EurekaHttpResponse<Void> httpResponse = retryableClient.execute(requestExecutor);
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Mockito.verify(requestExecutor, Mockito.times(1)).execute(clusterDelegates.get(0));
        Mockito.verify(requestExecutor, Mockito.times(1)).execute(clusterDelegates.get(1));
    }

    @Test(timeout = 10000)
    public void testConcurrentRequestsLeaveLastSuccessfulDelegate() throws Exception {
        Mockito.when(clientFactory.newClient(Matchers.<EurekaEndpoint>anyVararg())).thenReturn(clusterDelegates.get(0), clusterDelegates.get(1));
        RetryableEurekaHttpClientTest.BlockingRequestExecutor executor0 = new RetryableEurekaHttpClientTest.BlockingRequestExecutor();
        RetryableEurekaHttpClientTest.BlockingRequestExecutor executor1 = new RetryableEurekaHttpClientTest.BlockingRequestExecutor();
        Thread thread0 = new Thread(new RetryableEurekaHttpClientTest.RequestExecutorRunner(executor0));
        Thread thread1 = new Thread(new RetryableEurekaHttpClientTest.RequestExecutorRunner(executor1));
        // Run parallel requests
        thread0.start();
        executor0.awaitReady();
        thread1.start();
        executor1.awaitReady();
        // Complete request, first thread first, second afterwards
        executor0.complete();
        thread0.join();
        executor1.complete();
        thread1.join();
        // Verify subsequent request done on delegate1
        Mockito.when(requestExecutor.execute(clusterDelegates.get(1))).thenReturn(EurekaHttpResponse.status(200));
        EurekaHttpResponse<Void> httpResponse = retryableClient.execute(requestExecutor);
        Assert.assertThat(httpResponse.getStatusCode(), CoreMatchers.is(CoreMatchers.equalTo(200)));
        Mockito.verify(clientFactory, Mockito.times(2)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(requestExecutor, Mockito.times(0)).execute(clusterDelegates.get(0));
        Mockito.verify(requestExecutor, Mockito.times(1)).execute(clusterDelegates.get(1));
    }

    class RequestExecutorRunner implements Runnable {
        private final RequestExecutor<Void> requestExecutor;

        RequestExecutorRunner(RequestExecutor<Void> requestExecutor) {
            this.requestExecutor = requestExecutor;
        }

        @Override
        public void run() {
            retryableClient.execute(requestExecutor);
        }
    }

    static class BlockingRequestExecutor implements RequestExecutor<Void> {
        private final CountDownLatch readyLatch = new CountDownLatch(1);

        private final CountDownLatch completeLatch = new CountDownLatch(1);

        @Override
        public EurekaHttpResponse<Void> execute(EurekaHttpClient delegate) {
            readyLatch.countDown();
            try {
                completeLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException("never released");
            }
            return EurekaHttpResponse.status(200);
        }

        @Override
        public RequestType getRequestType() {
            return RetryableEurekaHttpClientTest.TEST_REQUEST_TYPE;
        }

        void awaitReady() {
            try {
                readyLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException("never released");
            }
        }

        void complete() {
            completeLatch.countDown();
        }
    }
}

