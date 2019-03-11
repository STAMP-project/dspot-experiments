/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DefaultAgentRegistryTest {
    private DefaultAgentRegistry agentRegistry;

    private static final String GUID = "guid";

    private static final String TOKEN = "token";

    private GuidService guidService;

    private TokenService tokenService;

    @Test
    public void shouldCreateGuidIfOneNotAlreadySet() throws Exception {
        guidService.delete();
        String guid = agentRegistry.uuid();
        Assert.assertNotNull(guid);
        Assert.assertThat(guid, Matchers.is(agentRegistry.uuid()));
        Assert.assertThat(guid, Matchers.is(Matchers.not(DefaultAgentRegistryTest.GUID)));
    }

    @Test
    public void shouldUseGuidThatAlreadyExists() throws Exception {
        Assert.assertThat(agentRegistry.uuid(), Matchers.is(DefaultAgentRegistryTest.GUID));
    }

    @Test
    public void shouldCheckGuidPresent() throws Exception {
        Assert.assertTrue(agentRegistry.guidPresent());
        guidService.delete();
        Assert.assertFalse(agentRegistry.guidPresent());
    }

    @Test
    public void shouldGetTokenFromFile() throws Exception {
        Assert.assertThat(agentRegistry.token(), Matchers.is(DefaultAgentRegistryTest.TOKEN));
    }

    @Test
    public void shouldCheckTokenPresent() throws Exception {
        Assert.assertTrue(agentRegistry.tokenPresent());
        tokenService.delete();
        Assert.assertFalse(agentRegistry.tokenPresent());
    }

    @Test
    public void shouldStoreTokenToDisk() throws Exception {
        Assert.assertThat(agentRegistry.token(), Matchers.is(DefaultAgentRegistryTest.TOKEN));
        agentRegistry.storeTokenToDisk("foo-token");
        Assert.assertThat(agentRegistry.token(), Matchers.is("foo-token"));
    }

    @Test
    public void shouldDeleteTokenFromDisk() throws Exception {
        Assert.assertThat(agentRegistry.token(), Matchers.is(DefaultAgentRegistryTest.TOKEN));
        Assert.assertTrue(agentRegistry.tokenPresent());
        agentRegistry.deleteToken();
        Assert.assertFalse(agentRegistry.tokenPresent());
    }
}

