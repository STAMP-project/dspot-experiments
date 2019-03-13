/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.http.client.reactive;


import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.netty.http.HttpResources;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;


/**
 * Unit tests for {@link ReactorResourceFactory}.
 *
 * @author Rossen Stoyanchev
 */
public class ReactorResourceFactoryTests {
    private final ReactorResourceFactory resourceFactory = new ReactorResourceFactory();

    private final ConnectionProvider connectionProvider = Mockito.mock(ConnectionProvider.class);

    private final LoopResources loopResources = Mockito.mock(LoopResources.class);

    @Test
    public void globalResources() throws Exception {
        this.resourceFactory.setUseGlobalResources(true);
        this.resourceFactory.afterPropertiesSet();
        HttpResources globalResources = HttpResources.get();
        Assert.assertSame(globalResources, this.resourceFactory.getConnectionProvider());
        Assert.assertSame(globalResources, this.resourceFactory.getLoopResources());
        Assert.assertFalse(globalResources.isDisposed());
        this.resourceFactory.destroy();
        Assert.assertTrue(globalResources.isDisposed());
    }

    @Test
    public void globalResourcesWithConsumer() throws Exception {
        AtomicBoolean invoked = new AtomicBoolean(false);
        this.resourceFactory.addGlobalResourcesConsumer(( httpResources) -> invoked.set(true));
        this.resourceFactory.afterPropertiesSet();
        Assert.assertTrue(invoked.get());
        this.resourceFactory.destroy();
    }

    @Test
    public void localResources() throws Exception {
        this.resourceFactory.setUseGlobalResources(false);
        this.resourceFactory.afterPropertiesSet();
        ConnectionProvider connectionProvider = this.resourceFactory.getConnectionProvider();
        LoopResources loopResources = this.resourceFactory.getLoopResources();
        Assert.assertNotSame(HttpResources.get(), connectionProvider);
        Assert.assertNotSame(HttpResources.get(), loopResources);
        // The below does not work since ConnectionPoolProvider simply checks if pool is empty.
        // assertFalse(connectionProvider.isDisposed());
        Assert.assertFalse(loopResources.isDisposed());
        this.resourceFactory.destroy();
        Assert.assertTrue(connectionProvider.isDisposed());
        Assert.assertTrue(loopResources.isDisposed());
    }

    @Test
    public void localResourcesViaSupplier() throws Exception {
        this.resourceFactory.setUseGlobalResources(false);
        this.resourceFactory.setConnectionProviderSupplier(() -> this.connectionProvider);
        this.resourceFactory.setLoopResourcesSupplier(() -> this.loopResources);
        this.resourceFactory.afterPropertiesSet();
        ConnectionProvider connectionProvider = this.resourceFactory.getConnectionProvider();
        LoopResources loopResources = this.resourceFactory.getLoopResources();
        Assert.assertSame(this.connectionProvider, connectionProvider);
        Assert.assertSame(this.loopResources, loopResources);
        Mockito.verifyNoMoreInteractions(this.connectionProvider, this.loopResources);
        this.resourceFactory.destroy();
        // Managed (destroy disposes)..
        Mockito.verify(this.connectionProvider).dispose();
        Mockito.verify(this.loopResources).dispose();
        Mockito.verifyNoMoreInteractions(this.connectionProvider, this.loopResources);
    }

    @Test
    public void externalResources() throws Exception {
        this.resourceFactory.setUseGlobalResources(false);
        this.resourceFactory.setConnectionProvider(this.connectionProvider);
        this.resourceFactory.setLoopResources(this.loopResources);
        this.resourceFactory.afterPropertiesSet();
        ConnectionProvider connectionProvider = this.resourceFactory.getConnectionProvider();
        LoopResources loopResources = this.resourceFactory.getLoopResources();
        Assert.assertSame(this.connectionProvider, connectionProvider);
        Assert.assertSame(this.loopResources, loopResources);
        Mockito.verifyNoMoreInteractions(this.connectionProvider, this.loopResources);
        this.resourceFactory.destroy();
        // Not managed (destroy has no impact)..
        Mockito.verifyNoMoreInteractions(this.connectionProvider, this.loopResources);
    }
}

