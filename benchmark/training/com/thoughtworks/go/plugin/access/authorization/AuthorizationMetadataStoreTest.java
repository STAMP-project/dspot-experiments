/**
 * Copyright 2018 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.authorization;


import com.thoughtworks.go.plugin.domain.authorization.AuthorizationPluginInfo;
import java.util.Set;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AuthorizationMetadataStoreTest {
    private AuthorizationMetadataStore store;

    private AuthorizationPluginInfo plugin1;

    private AuthorizationPluginInfo plugin2;

    private AuthorizationPluginInfo plugin3;

    @Test
    public void shouldGetPluginsThatSupportWebBasedAuthorization() {
        Set<AuthorizationPluginInfo> pluginsThatSupportsWebBasedAuthentication = store.getPluginsThatSupportsWebBasedAuthentication();
        Assert.assertThat(pluginsThatSupportsWebBasedAuthentication.size(), Matchers.is(2));
        Assert.assertThat(pluginsThatSupportsWebBasedAuthentication.contains(plugin1), Matchers.is(true));
        Assert.assertThat(pluginsThatSupportsWebBasedAuthentication.contains(plugin3), Matchers.is(true));
    }

    @Test
    public void shouldGetPluginsThatSupportPasswordBasedAuthorization() {
        Set<AuthorizationPluginInfo> pluginsThatSupportsWebBasedAuthentication = store.getPluginsThatSupportsPasswordBasedAuthentication();
        Assert.assertThat(pluginsThatSupportsWebBasedAuthentication.size(), Matchers.is(1));
        Assert.assertThat(pluginsThatSupportsWebBasedAuthentication.contains(plugin2), Matchers.is(true));
    }

    @Test
    public void shouldGetPluginsThatSupportsGetUserRolesCall() {
        Mockito.when(plugin1.getCapabilities().canGetUserRoles()).thenReturn(true);
        Set<String> pluginsThatSupportsGetUserRoles = store.getPluginsThatSupportsGetUserRoles();
        Assert.assertThat(pluginsThatSupportsGetUserRoles.size(), Matchers.is(1));
        Assert.assertThat(pluginsThatSupportsGetUserRoles, Matchers.contains(plugin1.getDescriptor().id()));
    }

    @Test
    public void shouldBeAbleToAnswerIfPluginSupportsPasswordBasedAuthentication() throws Exception {
        Assert.assertTrue(store.doesPluginSupportPasswordBasedAuthentication("password.plugin-2"));
        TestCase.assertFalse(store.doesPluginSupportPasswordBasedAuthentication("web.plugin-1"));
    }

    @Test
    public void shouldBeAbleToAnswerIfPluginSupportsWebBasedAuthentication() throws Exception {
        Assert.assertTrue(store.doesPluginSupportWebBasedAuthentication("web.plugin-1"));
        TestCase.assertFalse(store.doesPluginSupportWebBasedAuthentication("password.plugin-2"));
        Assert.assertTrue(store.doesPluginSupportWebBasedAuthentication("web.plugin-3"));
    }
}

