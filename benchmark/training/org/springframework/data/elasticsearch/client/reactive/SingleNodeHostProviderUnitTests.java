/**
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.client.reactive;


import State.OFFLINE;
import State.ONLINE;
import org.junit.Test;
import org.springframework.data.elasticsearch.client.NoReachableHostException;

import static org.springframework.data.elasticsearch.client.reactive.ReactiveMockClientTestsUtils.MockWebClientProvider.Receive.error;
import static org.springframework.data.elasticsearch.client.reactive.ReactiveMockClientTestsUtils.MockWebClientProvider.Receive.ok;


/**
 *
 *
 * @author Christoph Strobl
 * @unknown Golden Fool - Robin Hobb
 */
public class SingleNodeHostProviderUnitTests {
    static final String HOST_1 = ":9200";

    ReactiveMockClientTestsUtils.MockDelegatingElasticsearchHostProvider<SingleNodeHostProvider> mock;

    SingleNodeHostProvider provider;

    // DATAES-488
    @Test
    public void refreshHostStateShouldUpdateNodeStateCorrectly() {
        mock.when(SingleNodeHostProviderUnitTests.HOST_1).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::error);
        provider.clusterInfo().as(StepVerifier::create).expectNextCount(1).verifyComplete();
        assertThat(provider.getCachedHostState()).extracting(ElasticsearchHost::getState).isEqualTo(OFFLINE);
    }

    // DATAES-488
    @Test
    public void getActiveReturnsFirstActiveHost() {
        mock.when(SingleNodeHostProviderUnitTests.HOST_1).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::ok);
        provider.clusterInfo().as(StepVerifier::create).expectNextCount(1).verifyComplete();
        assertThat(provider.getCachedHostState()).extracting(ElasticsearchHost::getState).isEqualTo(ONLINE);
    }

    // DATAES-488
    @Test
    public void getActiveErrorsWhenNoActiveHostFound() {
        mock.when(SingleNodeHostProviderUnitTests.HOST_1).receive(ReactiveMockClientTestsUtils.MockWebClientProvider.Receive::error);
        provider.getActive().as(StepVerifier::create).expectError(NoReachableHostException.class);
    }
}

