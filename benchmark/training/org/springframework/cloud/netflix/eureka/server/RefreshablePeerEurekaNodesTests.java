/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.eureka.server;


import HttpStatus.OK;
import SpringBootTest.WebEnvironment;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.cluster.PeerEurekaNodes;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.resources.ServerCodecs;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration.RefreshablePeerEurekaNodes;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Fahim Farook
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RefreshablePeerEurekaNodesTests.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "spring.application.name=eureka-server", "eureka.client.service-url.defaultZone=http://localhost:8678/eureka/" })
public class RefreshablePeerEurekaNodesTests {
    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private PeerEurekaNodes peerEurekaNodes;

    @Value("${local.server.port}")
    private int port = 0;

    private static final String DEFAULT_ZONE = "eureka.client.service-url.defaultZone";

    private static final String REGION = "eureka.client.region";

    private static final String USE_DNS = "eureka.client.use-dns-for-fetching-service-urls";

    @Test
    public void notUpdatedWhenDnsIsTrue() {
        // to force defaultZone
        changeProperty("eureka.client.use-dns-for-fetching-service-urls=true", "eureka.client.region=unavailable-region", "eureka.client.service-url.defaultZone=http://default-host1:8678/eureka/");
        this.context.publishEvent(new EnvironmentChangeEvent(new HashSet<String>(Arrays.asList(RefreshablePeerEurekaNodesTests.USE_DNS, RefreshablePeerEurekaNodesTests.DEFAULT_ZONE))));
        assertThat(serviceUrlMatches("http://default-host1:8678/eureka/")).as("PeerEurekaNodes' are updated when eureka.client.use-dns-for-fetching-service-urls is true").isFalse();
    }

    @Test
    public void updatedWhenDnsIsFalse() {
        // to force defaultZone
        changeProperty("eureka.client.use-dns-for-fetching-service-urls=false", "eureka.client.region=unavailable-region", "eureka.client.service-url.defaultZone=http://default-host2:8678/eureka/");
        this.context.publishEvent(new EnvironmentChangeEvent(new HashSet<String>(Arrays.asList(RefreshablePeerEurekaNodesTests.USE_DNS, RefreshablePeerEurekaNodesTests.DEFAULT_ZONE))));
        assertThat(serviceUrlMatches("http://default-host2:8678/eureka/")).as("PeerEurekaNodes' are not updated when eureka.client.use-dns-for-fetching-service-urls is false").isTrue();
    }

    @Test
    public void updatedWhenRegionChanged() {
        changeProperty("eureka.client.use-dns-for-fetching-service-urls=false", "eureka.client.region=region1", "eureka.client.availability-zones.region1=region1-zone", "eureka.client.availability-zones.region2=region2-zone", "eureka.client.service-url.region1-zone=http://region1-zone-host:8678/eureka/", "eureka.client.service-url.region2-zone=http://region2-zone-host:8678/eureka/");
        this.context.publishEvent(new EnvironmentChangeEvent(Collections.singleton(RefreshablePeerEurekaNodesTests.REGION)));
        assertThat(serviceUrlMatches("http://region1-zone-host:8678/eureka/")).as("PeerEurekaNodes' are not updated when eureka.client.region is changed").isTrue();
        changeProperty("eureka.client.region=region2");
        this.context.publishEvent(new EnvironmentChangeEvent(Collections.singleton(RefreshablePeerEurekaNodesTests.REGION)));
        assertThat(serviceUrlMatches("http://region2-zone-host:8678/eureka/")).as("PeerEurekaNodes' are not updated when eureka.client.region is changed").isTrue();
    }

    @Test
    public void updatedWhenAvailabilityZoneChanged() {
        changeProperty("eureka.client.use-dns-for-fetching-service-urls=false", "eureka.client.region=region4", "eureka.client.availability-zones.region3=region3-zone", "eureka.client.service-url.region4-zone=http://region4-zone-host:8678/eureka/", "eureka.client.service-url.defaultZone=http://default-host3:8678/eureka/");
        this.context.publishEvent(new EnvironmentChangeEvent(Collections.singleton("eureka.client.availability-zones.region3")));
        assertThat(this.peerEurekaNodes.getPeerEurekaNodes().get(0).getServiceUrl().equals("http://default-host3:8678/eureka/")).isTrue();
        changeProperty("eureka.client.availability-zones.region4=region4-zone");
        this.context.publishEvent(new EnvironmentChangeEvent(Collections.singleton("eureka.client.availability-zones.region4")));
        assertThat(serviceUrlMatches("http://region4-zone-host:8678/eureka/")).as("PeerEurekaNodes' are not updated when eureka.client.availability-zones are changed").isTrue();
    }

    @Test
    public void notUpdatedWhenIrrelevantPropertiesChanged() {
        // Only way to test this is verifying whether updatePeerEurekaNodes() is invoked.
        // PeerEurekaNodes.updatePeerEurekaNodes() is not public, hence cannot verify with
        // Mockito.
        class VerifyablePeerEurekNode extends RefreshablePeerEurekaNodes {
            VerifyablePeerEurekNode(PeerAwareInstanceRegistry registry, EurekaServerConfig serverConfig, EurekaClientConfig clientConfig, ServerCodecs serverCodecs, ApplicationInfoManager applicationInfoManager) {
                super(registry, serverConfig, clientConfig, serverCodecs, applicationInfoManager);
            }

            protected void updatePeerEurekaNodes(List<String> newPeerUrls) {
                super.updatePeerEurekaNodes(newPeerUrls);
            }
        }
        // Create stubs.
        final EurekaClientConfigBean configClientBean = Mockito.mock(EurekaClientConfigBean.class);
        Mockito.when(configClientBean.isUseDnsForFetchingServiceUrls()).thenReturn(false);
        final VerifyablePeerEurekNode mock = Mockito.spy(new VerifyablePeerEurekNode(null, null, configClientBean, null, null));
        mock.onApplicationEvent(new EnvironmentChangeEvent(Collections.singleton("some.irrelevant.property")));
        Mockito.verify(mock, Mockito.never()).updatePeerEurekaNodes(ArgumentMatchers.anyListOf(String.class));
    }

    @Test
    public void peerEurekaNodesIsRefreshablePeerEurekaNodes() {
        assertThat(this.peerEurekaNodes).isNotNull();
        assertThat(((this.peerEurekaNodes) instanceof RefreshablePeerEurekaNodes)).as("PeerEurekaNodes should be an instance of RefreshablePeerEurekaNodes").isTrue();
    }

    @Test
    public void serviceUrlsCountAsSoonAsRefreshed() {
        changeProperty("eureka.client.service-url.defaultZone=http://defaul-host3:8678/eureka/,http://defaul-host4:8678/eureka/");
        forceUpdate();
        assertThat(this.peerEurekaNodes.getPeerEurekaNodes().size()).as("PeerEurekaNodes' peer count is incorrect.").isEqualTo(2);
    }

    @Test
    public void serviceUrlsValueAsSoonAsRefreshed() {
        changeProperty("eureka.client.service-url.defaultZone=http://defaul-host4:8678/eureka/");
        forceUpdate();
        assertThat(serviceUrlMatches("http://defaul-host4:8678/eureka/")).as("PeerEurekaNodes' new peer[0] is incorrect").isTrue();
    }

    @Test
    public void dashboardUpdatedAsSoonAsRefreshed() {
        changeProperty("eureka.client.service-url.defaultZone=http://defaul-host5:8678/eureka/");
        forceUpdate();
        final ResponseEntity<String> entity = new TestRestTemplate().getForEntity((("http://localhost:" + (this.port)) + "/"), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(OK);
        final String body = entity.getBody();
        assertThat(body).isNotNull();
        assertThat(body.contains("http://defaul-host5:8678/eureka/")).as("DS Replicas not updated in the Eureka Server dashboard").isTrue();
    }

    @Test
    public void notUpdatedForRelaxedKeys() {
        // to force defaultZone
        changeProperty("eureka.client.use-dns-for-fetching-service-urls=false", "eureka.client.region=unavailable-region", "eureka.client.service-url.defaultZone=http://defaul-host6:8678/eureka/");
        this.context.publishEvent(new EnvironmentChangeEvent(Collections.singleton("eureka.client.serviceUrl.defaultZone")));
        assertThat(serviceUrlMatches("http://defaul-host6:8678/eureka/")).as("PeerEurekaNodes' are updated for keys with relaxed binding").isFalse();
    }

    @EnableEurekaServer
    @Configuration
    @EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
    protected static class Application {}
}

