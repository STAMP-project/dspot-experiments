/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.docker.client.auth;


import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.RegistryAuth;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FixedRegistryAuthSupplierTest {
    @Test
    public void authForReturnsWrappedAuthRegistry() throws DockerException {
        final RegistryAuth registryAuth = Mockito.mock(RegistryAuth.class);
        FixedRegistryAuthSupplier fixedRegistryAuthSupplier = new FixedRegistryAuthSupplier(registryAuth, null);
        Assert.assertEquals(registryAuth, fixedRegistryAuthSupplier.authFor("doesn't matter"));
    }

    @Test
    public void authForReturnsNullForEmptyConstructor() throws DockerException {
        final FixedRegistryAuthSupplier fixedRegistryAuthSupplier = new FixedRegistryAuthSupplier();
        Assert.assertNull(fixedRegistryAuthSupplier.authFor("any"));
        Assert.assertNull(fixedRegistryAuthSupplier.authForBuild());
    }
}

