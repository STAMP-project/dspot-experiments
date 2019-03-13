/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.netty;


import io.grpc.ServerProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link NettyServerProvider}.
 */
@RunWith(JUnit4.class)
public class NettyServerProviderTest {
    private NettyServerProvider provider = new NettyServerProvider();

    @Test
    public void provided() {
        Assert.assertSame(NettyServerProvider.class, ServerProvider.provider().getClass());
    }

    @Test
    public void basicMethods() {
        Assert.assertTrue(provider.isAvailable());
        Assert.assertEquals(5, provider.priority());
    }

    @Test
    public void builderIsANettyBuilder() {
        Assert.assertSame(NettyServerBuilder.class, provider.builderForPort(443).getClass());
    }
}

