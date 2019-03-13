/**
 * Copyright 2014-2018 the original author or authors.
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
package de.codecentric.boot.admin.client.config;


import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;


public class ClientPropertiesTest {
    @Test
    public void should_default_autoDeregister_to_false() {
        MockEnvironment env = new MockEnvironment();
        ClientProperties clientProperties = new ClientProperties(env);
        assertThat(clientProperties.isAutoDeregistration()).isFalse();
        clientProperties.setAutoDeregistration(false);
        assertThat(clientProperties.isAutoDeregistration()).isFalse();
        clientProperties.setAutoDeregistration(true);
        assertThat(clientProperties.isAutoDeregistration()).isTrue();
    }

    @Test
    public void should_default_autoDeregister_to_true() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("VCAP_APPLICATION", "");
        ClientProperties clientProperties = new ClientProperties(env);
        assertThat(clientProperties.isAutoDeregistration()).isTrue();
        clientProperties.setAutoDeregistration(false);
        assertThat(clientProperties.isAutoDeregistration()).isFalse();
        clientProperties.setAutoDeregistration(true);
        assertThat(clientProperties.isAutoDeregistration()).isTrue();
    }

    @Test
    public void should_return_all_admiUrls() {
        ClientProperties clientProperties = new ClientProperties(new MockEnvironment());
        clientProperties.setApiPath("register");
        clientProperties.setUrl(new String[]{ "http://first", "http://second" });
        assertThat(clientProperties.getAdminUrl()).containsExactly("http://first/register", "http://second/register");
    }
}

