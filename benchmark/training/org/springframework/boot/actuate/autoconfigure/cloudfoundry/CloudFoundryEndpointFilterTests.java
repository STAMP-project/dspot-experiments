/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry;


import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.annotation.DiscoveredEndpoint;


/**
 * Tests for {@link CloudFoundryEndpointFilter}.
 *
 * @author Madhura Bhave
 */
public class CloudFoundryEndpointFilterTests {
    private CloudFoundryEndpointFilter filter = new CloudFoundryEndpointFilter();

    @Test
    public void matchIfDiscovererCloudFoundryShouldReturnFalse() {
        DiscoveredEndpoint<?> endpoint = Mockito.mock(DiscoveredEndpoint.class);
        BDDMockito.given(endpoint.wasDiscoveredBy(CloudFoundryWebEndpointDiscoverer.class)).willReturn(true);
        assertThat(this.filter.match(endpoint)).isTrue();
    }

    @Test
    public void matchIfDiscovererNotCloudFoundryShouldReturnFalse() {
        DiscoveredEndpoint<?> endpoint = Mockito.mock(DiscoveredEndpoint.class);
        BDDMockito.given(endpoint.wasDiscoveredBy(CloudFoundryWebEndpointDiscoverer.class)).willReturn(false);
        assertThat(this.filter.match(endpoint)).isFalse();
    }
}

