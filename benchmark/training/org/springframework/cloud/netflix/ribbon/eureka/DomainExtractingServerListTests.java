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
package org.springframework.cloud.netflix.ribbon.eureka;


import CommonClientConfigKey.UseIPAddrForServer;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author Spencer Gibb
 */
public class DomainExtractingServerListTests {
    static final String IP_ADDR = "10.0.0.2";

    static final int PORT = 8080;

    static final String ZONE = "myzone.mydomain.com";

    static final String HOST_NAME = "myHostName." + (DomainExtractingServerListTests.ZONE);

    static final String INSTANCE_ID = "myInstanceId";

    private Map<String, String> metadata = Collections.<String, String>singletonMap("instanceId", DomainExtractingServerListTests.INSTANCE_ID);

    @Test
    public void testDomainExtractingServer() {
        DomainExtractingServerList serverList = getDomainExtractingServerList(new DefaultClientConfigImpl(), true);
        List<DiscoveryEnabledServer> servers = serverList.getInitialListOfServers();
        assertThat(servers).as("servers was null").isNotNull();
        assertThat(servers.size()).as("servers was not size 1").isEqualTo(1);
        DomainExtractingServer des = assertDomainExtractingServer(servers, DomainExtractingServerListTests.ZONE);
        assertThat(des.getHostPort()).as("hostPort was wrong").isEqualTo((((DomainExtractingServerListTests.HOST_NAME) + ":") + (DomainExtractingServerListTests.PORT)));
    }

    @Test
    public void testZoneInMetaData() {
        this.metadata = new HashMap<String, String>();
        this.metadata.put("zone", "us-west-1");
        this.metadata.put("instanceId", DomainExtractingServerListTests.INSTANCE_ID);
        DomainExtractingServerList serverList = getDomainExtractingServerList(new DefaultClientConfigImpl(), false);
        List<DiscoveryEnabledServer> servers = serverList.getInitialListOfServers();
        assertThat(servers).as("servers was null").isNotNull();
        assertThat(servers.size()).as("servers was not size 1").isEqualTo(1);
        DomainExtractingServer des = assertDomainExtractingServer(servers, "us-west-1");
        assertThat(des.getZone()).as("Zone was wrong").isEqualTo("us-west-1");
    }

    @Test
    public void testDomainExtractingServerDontApproximateZone() {
        DomainExtractingServerList serverList = getDomainExtractingServerList(new DefaultClientConfigImpl(), false);
        List<DiscoveryEnabledServer> servers = serverList.getInitialListOfServers();
        assertThat(servers).as("servers was null").isNotNull();
        assertThat(servers.size()).as("servers was not size 1").isEqualTo(1);
        DomainExtractingServer des = assertDomainExtractingServer(servers, null);
        assertThat(des.getHostPort()).as("hostPort was wrong").isEqualTo((((DomainExtractingServerListTests.HOST_NAME) + ":") + (DomainExtractingServerListTests.PORT)));
    }

    @Test
    public void testDomainExtractingServerUseIpAddress() {
        DefaultClientConfigImpl config = new DefaultClientConfigImpl();
        config.setProperty(UseIPAddrForServer, true);
        DomainExtractingServerList serverList = getDomainExtractingServerList(config, true);
        List<DiscoveryEnabledServer> servers = serverList.getInitialListOfServers();
        assertThat(servers).as("servers was null").isNotNull();
        assertThat(servers.size()).as("servers was not size 1").isEqualTo(1);
        DomainExtractingServer des = assertDomainExtractingServer(servers, DomainExtractingServerListTests.ZONE);
        assertThat(des.getHostPort()).as("hostPort was wrong").isEqualTo((((DomainExtractingServerListTests.IP_ADDR) + ":") + (DomainExtractingServerListTests.PORT)));
    }
}

