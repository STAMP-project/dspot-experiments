/**
 * Copyright 2016-2019 the original author or authors.
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


import LeaseInfo.DEFAULT_LEASE_DURATION;
import SpringBootTest.WebEnvironment;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.LeaseInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Bartlomiej Slota
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = InstanceRegistryTests.TestApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT, value = { "spring.application.name=eureka", "logging.level.org.springframework." + "cloud.netflix.eureka.server.InstanceRegistry=DEBUG" })
public class InstanceRegistryTests {
    private static final String APP_NAME = "MY-APP-NAME";

    private static final String HOST_NAME = "my-host-name";

    private static final String INSTANCE_ID = "my-host-name:8008";

    private static final int PORT = 8008;

    @SpyBean(PeerAwareInstanceRegistry.class)
    private InstanceRegistry instanceRegistry;

    @Autowired
    private InstanceRegistryTests.TestEvents testEvents;

    @Test
    public void testRegister() throws Exception {
        // creating instance info
        final LeaseInfo leaseInfo = getLeaseInfo();
        final InstanceInfo instanceInfo = getInstanceInfo(InstanceRegistryTests.APP_NAME, InstanceRegistryTests.HOST_NAME, InstanceRegistryTests.INSTANCE_ID, InstanceRegistryTests.PORT, leaseInfo);
        // calling tested method
        instanceRegistry.register(instanceInfo, false);
        // event of proper type is registered
        assertThat(this.testEvents.applicationEvents.size()).isEqualTo(1);
        assertThat(((this.testEvents.applicationEvents.get(0)) instanceof EurekaInstanceRegisteredEvent)).isTrue();
        // event details are correct
        final EurekaInstanceRegisteredEvent registeredEvent = ((EurekaInstanceRegisteredEvent) (this.testEvents.applicationEvents.get(0)));
        assertThat(registeredEvent.getInstanceInfo()).isEqualTo(instanceInfo);
        assertThat(registeredEvent.getLeaseDuration()).isEqualTo(leaseInfo.getDurationInSecs());
        assertThat(registeredEvent.getSource()).isEqualTo(instanceRegistry);
        assertThat(registeredEvent.isReplication()).isFalse();
    }

    @Test
    public void testDefaultLeaseDurationRegisterEvent() throws Exception {
        // creating instance info
        final InstanceInfo instanceInfo = getInstanceInfo(InstanceRegistryTests.APP_NAME, InstanceRegistryTests.HOST_NAME, InstanceRegistryTests.INSTANCE_ID, InstanceRegistryTests.PORT, null);
        // calling tested method
        instanceRegistry.register(instanceInfo, false);
        // instance info duration is set to default
        final EurekaInstanceRegisteredEvent registeredEvent = ((EurekaInstanceRegisteredEvent) (this.testEvents.applicationEvents.get(0)));
        assertThat(registeredEvent.getLeaseDuration()).isEqualTo(DEFAULT_LEASE_DURATION);
    }

    @Test
    public void testInternalCancel() throws Exception {
        // calling tested method
        instanceRegistry.internalCancel(InstanceRegistryTests.APP_NAME, InstanceRegistryTests.HOST_NAME, false);
        // event of proper type is registered
        assertThat(this.testEvents.applicationEvents.size()).isEqualTo(1);
        assertThat(((this.testEvents.applicationEvents.get(0)) instanceof EurekaInstanceCanceledEvent)).isTrue();
        // event details are correct
        final EurekaInstanceCanceledEvent registeredEvent = ((EurekaInstanceCanceledEvent) (this.testEvents.applicationEvents.get(0)));
        assertThat(registeredEvent.getAppName()).isEqualTo(InstanceRegistryTests.APP_NAME);
        assertThat(registeredEvent.getServerId()).isEqualTo(InstanceRegistryTests.HOST_NAME);
        assertThat(registeredEvent.getSource()).isEqualTo(instanceRegistry);
        assertThat(registeredEvent.isReplication()).isFalse();
    }

    @Test
    public void testRenew() throws Exception {
        // Creating two instances of the app
        final InstanceInfo instanceInfo1 = getInstanceInfo(InstanceRegistryTests.APP_NAME, InstanceRegistryTests.HOST_NAME, InstanceRegistryTests.INSTANCE_ID, InstanceRegistryTests.PORT, null);
        final InstanceInfo instanceInfo2 = getInstanceInfo(InstanceRegistryTests.APP_NAME, InstanceRegistryTests.HOST_NAME, "my-host-name:8009", 8009, null);
        // creating application list with an app having two instances
        final Application application = new Application(InstanceRegistryTests.APP_NAME, Arrays.asList(instanceInfo1, instanceInfo2));
        final List<Application> applications = new ArrayList<>();
        applications.add(application);
        // stubbing applications list
        Mockito.doReturn(applications).when(instanceRegistry).getSortedApplications();
        // calling tested method
        instanceRegistry.renew(InstanceRegistryTests.APP_NAME, InstanceRegistryTests.INSTANCE_ID, false);
        instanceRegistry.renew(InstanceRegistryTests.APP_NAME, "my-host-name:8009", false);
        // event of proper type is registered
        assertThat(this.testEvents.applicationEvents.size()).isEqualTo(2);
        assertThat(((this.testEvents.applicationEvents.get(0)) instanceof EurekaInstanceRenewedEvent)).isTrue();
        assertThat(((this.testEvents.applicationEvents.get(1)) instanceof EurekaInstanceRenewedEvent)).isTrue();
        // event details are correct
        final EurekaInstanceRenewedEvent event1 = ((EurekaInstanceRenewedEvent) (this.testEvents.applicationEvents.get(0)));
        assertThat(event1.getAppName()).isEqualTo(InstanceRegistryTests.APP_NAME);
        assertThat(event1.getServerId()).isEqualTo(InstanceRegistryTests.INSTANCE_ID);
        assertThat(event1.getSource()).isEqualTo(instanceRegistry);
        assertThat(event1.getInstanceInfo()).isEqualTo(instanceInfo1);
        assertThat(event1.isReplication()).isFalse();
        final EurekaInstanceRenewedEvent event2 = ((EurekaInstanceRenewedEvent) (this.testEvents.applicationEvents.get(1)));
        assertThat(event2.getInstanceInfo()).isEqualTo(instanceInfo2);
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableEurekaServer
    protected static class TestApplication {
        @Bean
        public InstanceRegistryTests.TestEvents testEvents() {
            return new InstanceRegistryTests.TestEvents();
        }
    }

    protected static class TestEvents implements SmartApplicationListener {
        public final List<ApplicationEvent> applicationEvents = new LinkedList<>();

        @Override
        public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
            return ((EurekaInstanceRegisteredEvent.class.isAssignableFrom(eventType)) || (EurekaInstanceCanceledEvent.class.isAssignableFrom(eventType))) || (EurekaInstanceRenewedEvent.class.isAssignableFrom(eventType));
        }

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            this.applicationEvents.add(event);
        }
    }
}

