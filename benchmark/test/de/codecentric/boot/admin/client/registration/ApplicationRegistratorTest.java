/**
 * Copyright 2014-2019 the original author or authors.
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
package de.codecentric.boot.admin.client.registration;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;


public class ApplicationRegistratorTest {
    private final Application application = Application.create("AppName").managementUrl("http://localhost:8080/mgmt").healthUrl("http://localhost:8080/health").serviceUrl("http://localhost:8080").build();

    private final RegistrationClient registrationClient = Mockito.mock(RegistrationClient.class);

    @Test
    public void register_should_return_true_when_successful() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, true);
        Mockito.when(this.registrationClient.register(ArgumentMatchers.any(), ArgumentMatchers.eq(this.application))).thenReturn("-id-");
        assertThat(registrator.register()).isTrue();
        assertThat(registrator.getRegisteredId()).isEqualTo("-id-");
    }

    @Test
    public void register_should_return_false_when_failed() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, true);
        Mockito.when(this.registrationClient.register(ArgumentMatchers.any(), ArgumentMatchers.eq(this.application))).thenThrow(new RestClientException("Error"));
        assertThat(registrator.register()).isFalse();
        assertThat(registrator.register()).isFalse();
        assertThat(registrator.getRegisteredId()).isNull();
    }

    @Test
    public void register_should_try_next_on_error() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, true);
        Mockito.when(this.registrationClient.register("http://sba:8080/instances", this.application)).thenThrow(new RestClientException("Error"));
        Mockito.when(this.registrationClient.register("http://sba2:8080/instances", this.application)).thenReturn("-id-");
        assertThat(registrator.register()).isTrue();
        assertThat(registrator.getRegisteredId()).isEqualTo("-id-");
    }

    @Test
    public void deregister_should_deregister_at_server() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, true);
        Mockito.when(this.registrationClient.register(ArgumentMatchers.any(), ArgumentMatchers.eq(this.application))).thenReturn("-id-");
        registrator.register();
        registrator.deregister();
        assertThat(registrator.getRegisteredId()).isNull();
        Mockito.verify(this.registrationClient).deregister("http://sba:8080/instances", "-id-");
    }

    @Test
    public void deregister_should_not_deregister_when_not_registered() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, true);
        registrator.deregister();
        Mockito.verify(this.registrationClient, Mockito.never()).deregister(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void deregister_should_try_next_on_error() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, true);
        Mockito.when(this.registrationClient.register(ArgumentMatchers.any(), ArgumentMatchers.eq(this.application))).thenReturn("-id-");
        Mockito.doThrow(new RestClientException("Error")).when(this.registrationClient).deregister("http://sba:8080/instances", "-id-");
        registrator.register();
        registrator.deregister();
        assertThat(registrator.getRegisteredId()).isNull();
        Mockito.verify(this.registrationClient).deregister("http://sba:8080/instances", "-id-");
        Mockito.verify(this.registrationClient).deregister("http://sba2:8080/instances", "-id-");
    }

    @Test
    public void register_should_register_on_multiple_servers() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, false);
        Mockito.when(this.registrationClient.register(ArgumentMatchers.any(), ArgumentMatchers.eq(this.application))).thenReturn("-id-");
        assertThat(registrator.register()).isTrue();
        assertThat(registrator.getRegisteredId()).isEqualTo("-id-");
        Mockito.verify(this.registrationClient).register("http://sba:8080/instances", this.application);
        Mockito.verify(this.registrationClient).register("http://sba2:8080/instances", this.application);
    }

    @Test
    public void deregister_should_deregister_on_multiple_servers() {
        ApplicationRegistrator registrator = new ApplicationRegistrator(() -> this.application, this.registrationClient, new String[]{ "http://sba:8080/instances", "http://sba2:8080/instances" }, false);
        Mockito.when(this.registrationClient.register(ArgumentMatchers.any(), ArgumentMatchers.eq(this.application))).thenReturn("-id-");
        registrator.register();
        registrator.deregister();
        assertThat(registrator.getRegisteredId()).isNull();
        Mockito.verify(this.registrationClient).deregister("http://sba:8080/instances", "-id-");
        Mockito.verify(this.registrationClient).deregister("http://sba2:8080/instances", "-id-");
    }
}

