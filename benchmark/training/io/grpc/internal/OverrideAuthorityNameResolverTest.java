/**
 * Copyright 2017 The gRPC Authors
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
package io.grpc.internal;


import NameResolver.Factory;
import NameResolver.Helper;
import NameResolver.Listener;
import io.grpc.NameResolver;
import io.grpc.ProxyDetector;
import java.net.URI;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link OverrideAuthorityNameResolverFactory}.
 */
@RunWith(JUnit4.class)
public class OverrideAuthorityNameResolverTest {
    private static final Helper HELPER = new NameResolver.Helper() {
        @Override
        public int getDefaultPort() {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public ProxyDetector getProxyDetector() {
            throw new UnsupportedOperationException("Should not be called");
        }
    };

    @Test
    public void overridesAuthority() {
        NameResolver nameResolverMock = Mockito.mock(NameResolver.class);
        NameResolver.Factory wrappedFactory = Mockito.mock(Factory.class);
        Mockito.when(wrappedFactory.newNameResolver(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(Helper.class))).thenReturn(nameResolverMock);
        String override = "override:5678";
        NameResolver.Factory factory = new OverrideAuthorityNameResolverFactory(wrappedFactory, override);
        NameResolver nameResolver = factory.newNameResolver(URI.create("dns:///localhost:443"), OverrideAuthorityNameResolverTest.HELPER);
        TestCase.assertNotNull(nameResolver);
        Assert.assertEquals(override, nameResolver.getServiceAuthority());
    }

    @Test
    public void wontWrapNull() {
        NameResolver.Factory wrappedFactory = Mockito.mock(Factory.class);
        Mockito.when(wrappedFactory.newNameResolver(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(Helper.class))).thenReturn(null);
        NameResolver.Factory factory = new OverrideAuthorityNameResolverFactory(wrappedFactory, "override:5678");
        Assert.assertEquals(null, factory.newNameResolver(URI.create("dns:///localhost:443"), OverrideAuthorityNameResolverTest.HELPER));
    }

    @Test
    public void forwardsNonOverridenCalls() {
        NameResolver.Factory wrappedFactory = Mockito.mock(Factory.class);
        NameResolver mockResolver = Mockito.mock(NameResolver.class);
        Mockito.when(wrappedFactory.newNameResolver(ArgumentMatchers.any(URI.class), ArgumentMatchers.any(Helper.class))).thenReturn(mockResolver);
        NameResolver.Factory factory = new OverrideAuthorityNameResolverFactory(wrappedFactory, "override:5678");
        NameResolver overrideResolver = factory.newNameResolver(URI.create("dns:///localhost:443"), OverrideAuthorityNameResolverTest.HELPER);
        TestCase.assertNotNull(overrideResolver);
        NameResolver.Listener listener = Mockito.mock(Listener.class);
        overrideResolver.start(listener);
        Mockito.verify(mockResolver).start(listener);
        overrideResolver.shutdown();
        Mockito.verify(mockResolver).shutdown();
        overrideResolver.refresh();
        Mockito.verify(mockResolver).refresh();
    }
}

