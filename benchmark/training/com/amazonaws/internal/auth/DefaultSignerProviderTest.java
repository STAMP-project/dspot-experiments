/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.internal.auth;


import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.NoOpSigner;
import com.amazonaws.auth.RegionAwareSigner;
import com.amazonaws.auth.ServiceAwareSigner;
import com.amazonaws.auth.Signer;
import com.amazonaws.auth.SignerTypeAware;
import java.net.URI;
import java.net.URISyntaxException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSignerProviderTest {
    public static final Signer DEFAULT_SIGNER = new NoOpSigner();

    private static final String ENDPOINT = "https://mockservice.us-east-1.amazonaws.com";

    private static final Request<?> signerAwareRequest = new com.amazonaws.DefaultRequest<DefaultSignerProviderTest.FooSignedRequest>(new DefaultSignerProviderTest.FooSignedRequest(), "MockService");

    private static final Request<?> nonSignerAwareRequest = new com.amazonaws.DefaultRequest<DefaultSignerProviderTest.NonSignerTypeAware>(new DefaultSignerProviderTest.NonSignerTypeAware(), "MockService");

    private DefaultSignerProvider defaultSignerProvider;

    @Mock
    private AmazonWebServiceClient mockClient;

    @Test
    public void usesDefaultIfSignerOverridden() {
        Mockito.when(mockClient.getSignerOverride()).thenReturn("NoOpSignerType");
        SignerProviderContext ctx = SignerProviderContext.builder().withRequest(DefaultSignerProviderTest.signerAwareRequest).build();
        MatcherAssert.assertThat(defaultSignerProvider.getSigner(ctx), Matchers.is(Matchers.equalTo(DefaultSignerProviderTest.DEFAULT_SIGNER)));
    }

    @Test
    public void usesDefaultIfNotSignerAware() {
        SignerProviderContext ctx = SignerProviderContext.builder().withRequest(DefaultSignerProviderTest.nonSignerAwareRequest).build();
        MatcherAssert.assertThat(defaultSignerProvider.getSigner(ctx), Matchers.is(Matchers.equalTo(DefaultSignerProviderTest.DEFAULT_SIGNER)));
    }

    @Test
    public void usesOperationSignerType() {
        SignerProviderContext ctx = SignerProviderContext.builder().withRequest(DefaultSignerProviderTest.signerAwareRequest).build();
        MatcherAssert.assertThat(((defaultSignerProvider.getSigner(ctx)) instanceof DefaultSignerProviderTest.FooSigner), Matchers.is(true));
    }

    @Test
    public void configuresServiceAndRegionWhenUsingOperationSigner() throws URISyntaxException {
        Mockito.when(mockClient.getServiceName()).thenReturn("MockService");
        SignerProviderContext ctx = SignerProviderContext.builder().withRequest(DefaultSignerProviderTest.signerAwareRequest).build();
        Signer signer = defaultSignerProvider.getSigner(ctx);
        DefaultSignerProviderTest.FooSigner fooSigner = ((DefaultSignerProviderTest.FooSigner) (signer));
        MatcherAssert.assertThat(fooSigner.getRegionName(), Matchers.is(Matchers.equalTo("us-east-1")));
        MatcherAssert.assertThat(fooSigner.getServiceName(), Matchers.is(Matchers.equalTo("MockService")));
    }

    @Test
    public void testSignerRegionWhenUsingNonStandardEndpoint() throws URISyntaxException {
        Mockito.when(mockClient.getServiceName()).thenReturn("MockService");
        Mockito.when(mockClient.getEndpointPrefix()).thenReturn("MockEndpointPrefix");
        Request<?> signerAwareRequest = new com.amazonaws.DefaultRequest<DefaultSignerProviderTest.FooSignedRequest>(new DefaultSignerProviderTest.FooSignedRequest(), "MockService");
        String bjsEndpoint = "https://MockEndpointPrefix.cn-north-1.amazonaws.com.cn";
        signerAwareRequest.setEndpoint(new URI(bjsEndpoint));
        SignerProviderContext ctx = SignerProviderContext.builder().withRequest(signerAwareRequest).build();
        Signer signer = defaultSignerProvider.getSigner(ctx);
        DefaultSignerProviderTest.FooSigner fooSigner = ((DefaultSignerProviderTest.FooSigner) (signer));
        MatcherAssert.assertThat(fooSigner.getRegionName(), Matchers.is(Matchers.equalTo("cn-north-1")));
        MatcherAssert.assertThat(fooSigner.getServiceName(), Matchers.is(Matchers.equalTo("MockService")));
    }

    @Test
    public void usesDefaultSignerWhenNoRequest() {
        SignerProviderContext ctx = SignerProviderContext.builder().build();
        MatcherAssert.assertThat(((defaultSignerProvider.getSigner(ctx)) == (DefaultSignerProviderTest.DEFAULT_SIGNER)), Matchers.is(true));
    }

    public static class FooSigner implements RegionAwareSigner , ServiceAwareSigner , Signer {
        private String regionName;

        private String serviceName;

        @Override
        public void sign(SignableRequest<?> request, AWSCredentials credentials) {
        }

        @Override
        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public String getRegionName() {
            return regionName;
        }

        @Override
        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceName() {
            return serviceName;
        }
    }

    private static class NonSignerTypeAware extends AmazonWebServiceRequest {}

    private static class FooSignedRequest extends AmazonWebServiceRequest implements SignerTypeAware {
        @Override
        public String getSignerType() {
            return "FooSignerType";
        }
    }
}

