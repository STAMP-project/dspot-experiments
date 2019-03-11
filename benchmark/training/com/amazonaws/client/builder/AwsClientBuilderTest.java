/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.client.builder;


import Regions.AP_NORTHEAST_1;
import Regions.EU_CENTRAL_1;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.PredefinedClientConfigurations;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.AwsAsyncClientParams;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.RegionMetadata;
import com.amazonaws.regions.RegionMetadataProvider;
import com.amazonaws.regions.RegionUtils;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AwsClientBuilderTest {
    // Note that the tests rely on the socket timeout being set to some arbitrary unique value
    private static final ClientConfiguration DEFAULT_CLIENT_CONFIG = new ClientConfiguration().withSocketTimeout(9001);

    private static class ConcreteRequestHandler extends RequestHandler2 {}

    private static class MockClientConfigurationFactory extends ClientConfigurationFactory {
        @Override
        protected ClientConfiguration getDefaultConfig() {
            return AwsClientBuilderTest.DEFAULT_CLIENT_CONFIG;
        }
    }

    private static class ConcreteAsyncBuilder extends AwsAsyncClientBuilder<AwsClientBuilderTest.ConcreteAsyncBuilder, AwsClientBuilderTest.AmazonConcreteClient> {
        private ConcreteAsyncBuilder() {
            super(new AwsClientBuilderTest.MockClientConfigurationFactory());
        }

        private ConcreteAsyncBuilder(AwsRegionProvider mockRegionProvider) {
            super(new AwsClientBuilderTest.MockClientConfigurationFactory(), mockRegionProvider);
        }

        @Override
        protected AwsClientBuilderTest.AmazonConcreteClient build(AwsAsyncClientParams asyncClientParams) {
            return new AwsClientBuilderTest.AmazonConcreteClient(asyncClientParams);
        }
    }

    private static class ConcreteSyncBuilder extends AwsSyncClientBuilder<AwsClientBuilderTest.ConcreteSyncBuilder, AwsClientBuilderTest.AmazonConcreteClient> {
        private ConcreteSyncBuilder() {
            super(new AwsClientBuilderTest.MockClientConfigurationFactory());
        }

        @Override
        protected AwsClientBuilderTest.AmazonConcreteClient build(AwsSyncClientParams asyncClientParams) {
            return new AwsClientBuilderTest.AmazonConcreteClient(asyncClientParams);
        }
    }

    /**
     * Dummy client used by both the {@link ConcreteSyncBuilder} and {@link ConcreteAsyncBuilder}.
     * Captures the param object the client was created for for verification in tests.
     */
    private static class AmazonConcreteClient extends AmazonWebServiceClient {
        private AwsAsyncClientParams asyncParams;

        private AwsSyncClientParams syncParams;

        private AmazonConcreteClient(AwsAsyncClientParams asyncParams) {
            super(new ClientConfiguration());
            this.asyncParams = asyncParams;
        }

        private AmazonConcreteClient(AwsSyncClientParams syncParams) {
            super(new ClientConfiguration());
            this.syncParams = syncParams;
        }

        @Override
        public String getServiceNameIntern() {
            return "mockservice";
        }

        @Override
        public String getEndpointPrefix() {
            return "mockprefix";
        }

        public URI getEndpoint() {
            return this.endpoint;
        }

        public AwsAsyncClientParams getAsyncParams() {
            return asyncParams;
        }

        public AwsSyncClientParams getSyncParams() {
            return syncParams;
        }
    }

    /**
     * The sync client is tested less thoroughly then the async client primarily because the async
     * client exercises most of the same code paths so a bug introduced in the sync client builder
     * should be exposed via tests written against the async builder. This test is mainly for
     * additional coverage of the sync builder in case there is a regression specific to sync
     * builders.
     */
    @Test
    public void syncClientBuilder() {
        final List<RequestHandler2> requestHandlers = createRequestHandlerList(new AwsClientBuilderTest.ConcreteRequestHandler(), new AwsClientBuilderTest.ConcreteRequestHandler());
        final AWSCredentialsProvider credentials = Mockito.mock(AWSCredentialsProvider.class);
        final RequestMetricCollector metrics = Mockito.mock(RequestMetricCollector.class);
        // @formatter:off
        AwsClientBuilderTest.AmazonConcreteClient client = new AwsClientBuilderTest.ConcreteSyncBuilder().withRegion(EU_CENTRAL_1).withClientConfiguration(new ClientConfiguration().withSocketTimeout(1234)).withCredentials(credentials).withMetricsCollector(metrics).withRequestHandlers(requestHandlers.toArray(new RequestHandler2[requestHandlers.size()])).build();
        // @formatter:on
        Assert.assertEquals(URI.create("https://mockprefix.eu-central-1.amazonaws.com"), client.getEndpoint());
        Assert.assertEquals(1234, client.getSyncParams().getClientConfiguration().getSocketTimeout());
        Assert.assertEquals(requestHandlers, client.getSyncParams().getRequestHandlers());
        Assert.assertEquals(credentials, client.getSyncParams().getCredentialsProvider());
        Assert.assertEquals(metrics, client.getSyncParams().getRequestMetricCollector());
    }

    @Test
    public void credentialsNotExplicitlySet_UsesDefaultCredentialChain() throws Exception {
        AwsAsyncClientParams params = builderWithRegion().build().getAsyncParams();
        Assert.assertThat(params.getCredentialsProvider(), Matchers.instanceOf(DefaultAWSCredentialsProviderChain.class));
    }

    @Test
    public void regionProvidedExplicitly_WhenRegionNotFoundInMetadata_ThrowsIllegalArgumentException() throws Exception {
        try {
            RegionUtils.initializeWithMetadata(new RegionMetadata(Mockito.mock(RegionMetadataProvider.class)));
            new AwsClientBuilderTest.ConcreteAsyncBuilder().withRegion(AP_NORTHEAST_1);
            Assert.fail("Expected SdkClientException");
        } catch (SdkClientException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Could not find region information"));
        } finally {
            // Reset region metadata
            RegionUtils.initialize();
        }
    }

    /**
     * Customers may not need to explicitly configure a builder with a region if one can be found
     * from the {@link AwsRegionProvider} implementation. We mock the provider to yield consistent
     * results for the tests.
     */
    @Test
    public void regionProvidedByChain_WhenRegionNotFoundInMetadata_ThrowsIllegalArgumentException() {
        try {
            RegionUtils.initializeWithMetadata(new RegionMetadata(Mockito.mock(RegionMetadataProvider.class)));
            AwsRegionProvider mockRegionProvider = Mockito.mock(AwsRegionProvider.class);
            Mockito.when(mockRegionProvider.getRegion()).thenReturn("ap-southeast-2");
            new AwsClientBuilderTest.ConcreteAsyncBuilder(mockRegionProvider).build();
            Assert.fail("Expected SdkClientException");
        } catch (SdkClientException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Could not find region information"));
        } finally {
            // Reset region metadata
            RegionUtils.initialize();
        }
    }

    @Test
    public void credentialsExplicitlySet_UsesExplicitCredentials() throws Exception {
        AWSCredentialsProvider provider = new com.amazonaws.internal.StaticCredentialsProvider(new BasicAWSCredentials("akid", "skid"));
        AwsAsyncClientParams params = builderWithRegion().withCredentials(provider).build().getAsyncParams();
        Assert.assertEquals(provider, params.getCredentialsProvider());
    }

    @Test
    public void metricCollectorNotExplicitlySet_UsesNullMetricsCollector() throws Exception {
        Assert.assertNull(builderWithRegion().build().getAsyncParams().getRequestMetricCollector());
    }

    @Test
    public void metricsCollectorExplicitlySet_UsesExplicitMetricsCollector() throws Exception {
        RequestMetricCollector metricCollector = RequestMetricCollector.NONE;
        AwsAsyncClientParams params = builderWithRegion().withMetricsCollector(metricCollector).build().getAsyncParams();
        Assert.assertEquals(metricCollector, params.getRequestMetricCollector());
    }

    @Test
    public void clientConfigurationNotExplicitlySet_UsesServiceDefaultClientConfiguration() {
        AwsAsyncClientParams params = builderWithRegion().build().getAsyncParams();
        ClientConfiguration actualConfig = params.getClientConfiguration();
        Assert.assertEquals(AwsClientBuilderTest.DEFAULT_CLIENT_CONFIG.getSocketTimeout(), actualConfig.getSocketTimeout());
    }

    @Test
    public void clientConfigurationExplicitlySet_UsesExplicitConfiguration() {
        ClientConfiguration config = new ClientConfiguration().withSocketTimeout(1000);
        AwsAsyncClientParams params = builderWithRegion().withClientConfiguration(config).build().getAsyncParams();
        Assert.assertEquals(config.getSocketTimeout(), params.getClientConfiguration().getSocketTimeout());
    }

    @Test
    public void explicitRegionIsSet_UsesRegionToConstructEndpoint() {
        URI actualUri = new AwsClientBuilderTest.ConcreteAsyncBuilder().withRegion(Regions.US_WEST_2).build().getEndpoint();
        Assert.assertEquals(URI.create("https://mockprefix.us-west-2.amazonaws.com"), actualUri);
    }

    /**
     * If no region is explicitly given and no region can be found from the {@link AwsRegionProvider} implementation then the builder should fail to build clients. We mock the
     * provider to yield consistent results for the tests.
     */
    @Test(expected = AmazonClientException.class)
    public void noRegionProvidedExplicitlyOrImplicitly_ThrowsException() {
        AwsRegionProvider mockRegionProvider = Mockito.mock(AwsRegionProvider.class);
        Mockito.when(mockRegionProvider.getRegion()).thenReturn(null);
        new AwsClientBuilderTest.ConcreteAsyncBuilder(mockRegionProvider).build();
    }

    /**
     * Customers may not need to explicitly configure a builder with a region if one can be found
     * from the {@link AwsRegionProvider} implementation. We mock the provider to yield consistent
     * results for the tests.
     */
    @Test
    public void regionImplicitlyProvided_UsesRegionToConstructEndpoint() {
        AwsRegionProvider mockRegionProvider = Mockito.mock(AwsRegionProvider.class);
        Mockito.when(mockRegionProvider.getRegion()).thenReturn("ap-southeast-2");
        final URI actualUri = new AwsClientBuilderTest.ConcreteAsyncBuilder(mockRegionProvider).build().getEndpoint();
        Assert.assertEquals(URI.create("https://mockprefix.ap-southeast-2.amazonaws.com"), actualUri);
    }

    @Test
    public void endpointAndSigningRegionCanBeUsedInPlaceOfSetRegion() {
        AwsClientBuilderTest.AmazonConcreteClient client = new AwsClientBuilderTest.ConcreteSyncBuilder().withEndpointConfiguration(new EndpointConfiguration("https://mockprefix.ap-southeast-2.amazonaws.com", "us-east-1")).build();
        Assert.assertEquals("us-east-1", getSignerRegionOverride());
        Assert.assertEquals(URI.create("https://mockprefix.ap-southeast-2.amazonaws.com"), client.getEndpoint());
    }

    @Test(expected = IllegalStateException.class)
    public void cannotSetBothEndpointConfigurationAndRegionOnBuilder() {
        new AwsClientBuilderTest.ConcreteSyncBuilder().withEndpointConfiguration(new EndpointConfiguration("http://localhost:3030", "us-west-2")).withRegion("us-east-1").build();
    }

    @Test
    public void defaultClientConfigAndNoExplicitExecutor_UsesDefaultExecutorBasedOnMaxConns() {
        ExecutorService executor = builderWithRegion().build().getAsyncParams().getExecutor();
        Assert.assertThat(executor, Matchers.instanceOf(ThreadPoolExecutor.class));
        Assert.assertEquals(PredefinedClientConfigurations.defaultConfig().getMaxConnections(), ((ThreadPoolExecutor) (executor)).getMaximumPoolSize());
    }

    @Test
    public void customMaxConnsAndNoExplicitExecutor_UsesDefaultExecutorBasedOnMaxConns() {
        final int maxConns = 10;
        ExecutorService executor = builderWithRegion().withClientConfiguration(new ClientConfiguration().withMaxConnections(maxConns)).build().getAsyncParams().getExecutor();
        Assert.assertThat(executor, Matchers.instanceOf(ThreadPoolExecutor.class));
        Assert.assertEquals(maxConns, ((ThreadPoolExecutor) (executor)).getMaximumPoolSize());
    }

    /**
     * If a custom executor is set then the Max Connections in Client Configuration should be
     * ignored and the executor should be used as is.
     */
    @Test
    public void customMaxConnsAndExplicitExecutor_UsesExplicitExecutor() throws Exception {
        final int clientConfigMaxConns = 10;
        final int customExecutorThreadCount = 15;
        final ExecutorService customExecutor = Executors.newFixedThreadPool(customExecutorThreadCount);
        ExecutorService actualExecutor = builderWithRegion().withClientConfiguration(new ClientConfiguration().withMaxConnections(clientConfigMaxConns)).withExecutorFactory(new utils.builder.StaticExecutorFactory(customExecutor)).build().getAsyncParams().getExecutor();
        Assert.assertThat(actualExecutor, Matchers.instanceOf(ThreadPoolExecutor.class));
        Assert.assertEquals(customExecutor, actualExecutor);
        Assert.assertEquals(customExecutorThreadCount, ((ThreadPoolExecutor) (actualExecutor)).getMaximumPoolSize());
    }

    @Test
    public void noRequestHandlersExplicitlySet_UsesEmptyRequestHandlerList() throws Exception {
        List<RequestHandler2> requestHandlers = builderWithRegion().build().getAsyncParams().getRequestHandlers();
        Assert.assertThat(requestHandlers, Matchers.empty());
    }

    @Test
    public void requestHandlersExplicitlySet_UsesClonedListOfExplicitRequestHandlers() throws Exception {
        List<RequestHandler2> expectedHandlers = createRequestHandlerList(new AwsClientBuilderTest.ConcreteRequestHandler(), new AwsClientBuilderTest.ConcreteRequestHandler());
        List<RequestHandler2> actualHandlers = builderWithRegion().withRequestHandlers(expectedHandlers.toArray(new RequestHandler2[0])).build().getAsyncParams().getRequestHandlers();
        Assert.assertEquals(expectedHandlers, actualHandlers);
        // List should be copied or cloned
        Assert.assertThat(actualHandlers, Matchers.not(Matchers.sameInstance(expectedHandlers)));
    }

    @Test
    public void requestHandlersExplicitlySetWithVarArgs_UsesExplicitRequestHandlers() throws Exception {
        RequestHandler2 handlerOne = new AwsClientBuilderTest.ConcreteRequestHandler();
        RequestHandler2 handlerTwo = new AwsClientBuilderTest.ConcreteRequestHandler();
        RequestHandler2 handlerThree = new AwsClientBuilderTest.ConcreteRequestHandler();
        List<RequestHandler2> actualHandlers = builderWithRegion().withRequestHandlers(handlerOne, handlerTwo, handlerThree).build().getAsyncParams().getRequestHandlers();
        Assert.assertEquals(createRequestHandlerList(handlerOne, handlerTwo, handlerThree), actualHandlers);
    }
}

