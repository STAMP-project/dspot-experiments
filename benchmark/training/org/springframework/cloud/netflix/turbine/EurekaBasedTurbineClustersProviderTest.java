/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.cloud.netflix.turbine;


import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;


public class EurekaBasedTurbineClustersProviderTest {
    EurekaClient eurekaClient = Mockito.mock(EurekaClient.class);

    TurbineClustersProvider provider = new EurekaBasedTurbineClustersProvider(eurekaClient);

    @Test
    public void shouldProvideAllClustersNames() throws Exception {
        Applications applications = registeredApplications(Arrays.asList(application("service1"), application("service2"), application("service3")));
        Mockito.when(eurekaClient.getApplications()).thenReturn(applications);
        List<String> clusterNames = provider.getClusterNames();
        assertThat(clusterNames).containsOnly("service1", "service2", "service3");
    }
}

