/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.service.providers;


import TypeEnum.DOCKER;
import TypeEnum.TARBALL;
import org.apache.hadoop.yarn.service.api.records.Artifact;
import org.apache.hadoop.yarn.service.provider.ProviderFactory;
import org.apache.hadoop.yarn.service.provider.defaultImpl.DefaultClientProvider;
import org.apache.hadoop.yarn.service.provider.defaultImpl.DefaultProviderFactory;
import org.apache.hadoop.yarn.service.provider.defaultImpl.DefaultProviderService;
import org.apache.hadoop.yarn.service.provider.docker.DockerClientProvider;
import org.apache.hadoop.yarn.service.provider.docker.DockerProviderFactory;
import org.apache.hadoop.yarn.service.provider.docker.DockerProviderService;
import org.apache.hadoop.yarn.service.provider.tarball.TarballClientProvider;
import org.apache.hadoop.yarn.service.provider.tarball.TarballProviderFactory;
import org.apache.hadoop.yarn.service.provider.tarball.TarballProviderService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test provider factories.
 */
public class TestProviderFactory {
    @Test
    public void testDockerFactory() throws Throwable {
        ProviderFactory factory = ProviderFactory.createServiceProviderFactory(new Artifact().type(DOCKER));
        Assert.assertTrue((factory instanceof DockerProviderFactory));
        Assert.assertTrue(((factory.createClientProvider()) instanceof DockerClientProvider));
        Assert.assertTrue(((factory.createServerProvider()) instanceof DockerProviderService));
        Assert.assertTrue(((ProviderFactory.getProviderService(new Artifact().type(DOCKER))) instanceof DockerProviderService));
    }

    @Test
    public void testTarballFactory() throws Throwable {
        ProviderFactory factory = ProviderFactory.createServiceProviderFactory(new Artifact().type(TARBALL));
        Assert.assertTrue((factory instanceof TarballProviderFactory));
        Assert.assertTrue(((factory.createClientProvider()) instanceof TarballClientProvider));
        Assert.assertTrue(((factory.createServerProvider()) instanceof TarballProviderService));
        Assert.assertTrue(((ProviderFactory.getProviderService(new Artifact().type(TARBALL))) instanceof TarballProviderService));
    }

    @Test
    public void testDefaultFactory() throws Throwable {
        ProviderFactory factory = ProviderFactory.createServiceProviderFactory(null);
        Assert.assertTrue((factory instanceof DefaultProviderFactory));
        Assert.assertTrue(((factory.createClientProvider()) instanceof DefaultClientProvider));
        Assert.assertTrue(((factory.createServerProvider()) instanceof DefaultProviderService));
        Assert.assertTrue(((ProviderFactory.getProviderService(null)) instanceof DefaultProviderService));
    }
}

