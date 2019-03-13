/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.server;


import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.common.RpcResponse;
import org.junit.Test;


public class ServiceTest {
    /**
     * Tests if a user can write a decorator with working as() and serviceAdded() using lambda expressions only.
     */
    @Test
    public void lambdaExpressionDecorator() throws Exception {
        final ServiceTest.FooService inner = new ServiceTest.FooService();
        final Service<RpcRequest, RpcResponse> outer = inner.decorate(( delegate, ctx, req) -> {
            final RpcRequest newReq = RpcRequest.of(req.serviceType(), ("new_" + (req.method())), req.params());
            return delegate.serve(ctx, newReq);
        });
        ServiceTest.assertDecoration(inner, outer);
    }

    /**
     * Tests {@link Service#decorate(Class)}.
     */
    @Test
    public void reflectionDecorator() throws Exception {
        final ServiceTest.FooService inner = new ServiceTest.FooService();
        final ServiceTest.FooServiceDecorator outer = decorate(ServiceTest.FooServiceDecorator.class);
        ServiceTest.assertDecoration(inner, outer);
        assertThatThrownBy(() -> inner.decorate(.class)).isInstanceOf(IllegalArgumentException.class);
    }

    private static final class FooService implements Service<RpcRequest, RpcResponse> {
        ServiceConfig cfg;

        @Override
        public void serviceAdded(ServiceConfig cfg) throws Exception {
            this.cfg = cfg;
        }

        @Override
        public RpcResponse serve(ServiceRequestContext ctx, RpcRequest req) throws Exception {
            // Will never reach here.
            throw new Error();
        }
    }

    public static class FooServiceDecorator extends SimpleDecoratingService<RpcRequest, RpcResponse> {
        public FooServiceDecorator(Service<RpcRequest, RpcResponse> delegate) {
            super(delegate);
        }

        @Override
        public RpcResponse serve(ServiceRequestContext ctx, RpcRequest req) throws Exception {
            return delegate().serve(ctx, req);
        }
    }

    public static class BadFooServiceDecorator extends ServiceTest.FooServiceDecorator {
        public BadFooServiceDecorator(Service<RpcRequest, RpcResponse> delegate, @SuppressWarnings("unused")
        Object unused) {
            super(delegate);
        }
    }
}

