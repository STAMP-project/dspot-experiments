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
package io.grpc;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests that Channel and Server builders properly hide the static constructors.
 *
 * <p>This test does nothing on Java 9.
 */
@RunWith(Parameterized.class)
public class ChannelAndServerBuilderTest {
    @Parameterized.Parameter
    public Class<?> builderClass;

    @Test
    public void serverBuilderHidesMethod_forPort() throws Exception {
        Assume.assumeTrue(ServerBuilder.class.isAssignableFrom(builderClass));
        Method method = builderClass.getMethod("forPort", int.class);
        Assert.assertTrue(Modifier.isStatic(method.getModifiers()));
        Assert.assertTrue(ServerBuilder.class.isAssignableFrom(method.getReturnType()));
        Assert.assertSame(builderClass, method.getDeclaringClass());
    }

    @Test
    public void channelBuilderHidesMethod_forAddress() throws Exception {
        Assume.assumeTrue(ManagedChannelBuilder.class.isAssignableFrom(builderClass));
        Method method = builderClass.getMethod("forAddress", String.class, int.class);
        Assert.assertTrue(Modifier.isStatic(method.getModifiers()));
        Assert.assertTrue(ManagedChannelBuilder.class.isAssignableFrom(method.getReturnType()));
        Assert.assertSame(builderClass, method.getDeclaringClass());
    }

    @Test
    public void channelBuilderHidesMethod_forTarget() throws Exception {
        Assume.assumeTrue(ManagedChannelBuilder.class.isAssignableFrom(builderClass));
        Method method = builderClass.getMethod("forTarget", String.class);
        Assert.assertTrue(Modifier.isStatic(method.getModifiers()));
        Assert.assertTrue(ManagedChannelBuilder.class.isAssignableFrom(method.getReturnType()));
        Assert.assertSame(builderClass, method.getDeclaringClass());
    }
}

