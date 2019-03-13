/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc.util;


import MethodType.UNKNOWN;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.ServerCallHandler;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Unit tests for {@link MutableHandlerRegistry}.
 */
@RunWith(JUnit4.class)
public class MutableHandlerRegistryTest {
    private MutableHandlerRegistry registry = new MutableHandlerRegistry();

    @Mock
    private Marshaller<String> requestMarshaller;

    @Mock
    private Marshaller<Integer> responseMarshaller;

    @Mock
    private ServerCallHandler<String, Integer> flowHandler;

    @Mock
    private ServerCallHandler<String, Integer> coupleHandler;

    @Mock
    private ServerCallHandler<String, Integer> fewHandler;

    @Mock
    private ServerCallHandler<String, Integer> otherFlowHandler;

    private ServerServiceDefinition basicServiceDefinition;

    private ServerServiceDefinition multiServiceDefinition;

    @SuppressWarnings("rawtypes")
    private ServerMethodDefinition flowMethodDefinition;

    @Test
    public void simpleLookup() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        ServerMethodDefinition<?, ?> method = registry.lookupMethod("basic/flow");
        Assert.assertSame(flowMethodDefinition, method);
        Assert.assertNull(registry.lookupMethod("/basic/flow"));
        Assert.assertNull(registry.lookupMethod("basic/basic"));
        Assert.assertNull(registry.lookupMethod("flow/flow"));
        Assert.assertNull(registry.lookupMethod("completely/random"));
    }

    @Test
    public void simpleLookupWithBindable() {
        BindableService bindableService = new BindableService() {
            @Override
            public ServerServiceDefinition bindService() {
                return basicServiceDefinition;
            }
        };
        Assert.assertNull(registry.addService(bindableService));
        ServerMethodDefinition<?, ?> method = registry.lookupMethod("basic/flow");
        Assert.assertSame(flowMethodDefinition, method);
    }

    @Test
    public void multiServiceLookup() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertNull(registry.addService(multiServiceDefinition));
        ServerCallHandler<?, ?> handler = registry.lookupMethod("basic/flow").getServerCallHandler();
        Assert.assertSame(flowHandler, handler);
        handler = registry.lookupMethod("multi/couple").getServerCallHandler();
        Assert.assertSame(coupleHandler, handler);
        handler = registry.lookupMethod("multi/few").getServerCallHandler();
        Assert.assertSame(fewHandler, handler);
    }

    @Test
    public void removeAndLookup() {
        Assert.assertNull(registry.addService(multiServiceDefinition));
        Assert.assertNotNull(registry.lookupMethod("multi/couple"));
        Assert.assertNotNull(registry.lookupMethod("multi/few"));
        Assert.assertTrue(registry.removeService(multiServiceDefinition));
        Assert.assertNull(registry.lookupMethod("multi/couple"));
        Assert.assertNull(registry.lookupMethod("multi/few"));
    }

    @Test
    public void replaceAndLookup() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertNotNull(registry.lookupMethod("basic/flow"));
        MethodDescriptor<String, Integer> anotherMethod = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName("basic/another").setRequestMarshaller(requestMarshaller).setResponseMarshaller(responseMarshaller).build();
        ServerServiceDefinition replaceServiceDefinition = ServerServiceDefinition.builder(new ServiceDescriptor("basic", anotherMethod)).addMethod(anotherMethod, flowHandler).build();
        ServerMethodDefinition<?, ?> anotherMethodDefinition = replaceServiceDefinition.getMethod("basic/another");
        Assert.assertSame(basicServiceDefinition, registry.addService(replaceServiceDefinition));
        Assert.assertNull(registry.lookupMethod("basic/flow"));
        ServerMethodDefinition<?, ?> method = registry.lookupMethod("basic/another");
        Assert.assertSame(anotherMethodDefinition, method);
    }

    @Test
    public void removeSameSucceeds() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertTrue(registry.removeService(basicServiceDefinition));
    }

    @Test
    public void doubleRemoveFails() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertTrue(registry.removeService(basicServiceDefinition));
        Assert.assertFalse(registry.removeService(basicServiceDefinition));
    }

    @Test
    public void removeMissingFails() {
        Assert.assertFalse(registry.removeService(basicServiceDefinition));
    }

    @Test
    public void removeMissingNameConflictFails() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertFalse(registry.removeService(ServerServiceDefinition.builder(new ServiceDescriptor("basic")).build()));
    }

    @Test
    public void initialAddReturnsNull() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertNull(registry.addService(multiServiceDefinition));
    }

    @Test
    public void missingMethodLookupReturnsNull() {
        Assert.assertNull(registry.lookupMethod("bad"));
    }

    @Test
    public void addAfterRemoveReturnsNull() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertTrue(registry.removeService(basicServiceDefinition));
        Assert.assertNull(registry.addService(basicServiceDefinition));
    }

    @Test
    public void addReturnsPrevious() {
        Assert.assertNull(registry.addService(basicServiceDefinition));
        Assert.assertSame(basicServiceDefinition, registry.addService(ServerServiceDefinition.builder(new ServiceDescriptor("basic")).build()));
    }
}

