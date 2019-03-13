/**
 * Copyright (c) 2017. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.auth;


import ContainerCredentialsProvider.CONTAINER_AUTHORIZATION_TOKEN;
import ContainerCredentialsProvider.CONTAINER_CREDENTIALS_FULL_URI;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.ContainerCredentialsProvider.FullUriCredentialsEndpointProvider;
import java.net.URISyntaxException;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import utils.EnvironmentVariableHelper;


public class FullUriCredentialsEndpointProviderTest {
    private static final EnvironmentVariableHelper helper = new EnvironmentVariableHelper();

    private static final FullUriCredentialsEndpointProvider sut = new FullUriCredentialsEndpointProvider();

    @Test
    public void takesUriFromTheEnvironmentVariable() throws URISyntaxException {
        String fullUri = "http://localhost:8080/endpoint";
        FullUriCredentialsEndpointProviderTest.helper.set(CONTAINER_CREDENTIALS_FULL_URI, fullUri);
        MatcherAssert.assertThat(FullUriCredentialsEndpointProviderTest.sut.getCredentialsEndpoint().toString(), CoreMatchers.equalTo(fullUri));
    }

    @Test
    public void theLoopbackAddressIsAlsoAcceptable() throws URISyntaxException {
        String fullUri = "http://127.0.0.1:9851/endpoint";
        FullUriCredentialsEndpointProviderTest.helper.set(CONTAINER_CREDENTIALS_FULL_URI, fullUri);
        MatcherAssert.assertThat(FullUriCredentialsEndpointProviderTest.sut.getCredentialsEndpoint().toString(), CoreMatchers.equalTo(fullUri));
    }

    @Test(expected = SdkClientException.class)
    public void onlyLocalHostAddressesAreValid() throws URISyntaxException {
        FullUriCredentialsEndpointProviderTest.helper.set(CONTAINER_CREDENTIALS_FULL_URI, "https://google.com/endpoint");
        FullUriCredentialsEndpointProviderTest.sut.getCredentialsEndpoint();
    }

    @Test
    public void authorizationHeaderIsPresentIfEnvironmentVariableSet() {
        FullUriCredentialsEndpointProviderTest.helper.set(CONTAINER_AUTHORIZATION_TOKEN, "hello authorized world!");
        Map<String, String> headers = FullUriCredentialsEndpointProviderTest.sut.getHeaders();
        MatcherAssert.assertThat(headers.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(headers, Matchers.hasEntry("Authorization", "hello authorized world!"));
    }
}

