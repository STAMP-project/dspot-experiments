/**
 * Copyright 2016 The gRPC Authors
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


import MethodType.CLIENT_STREAMING;
import MethodType.SERVER_STREAMING;
import MethodType.UNARY;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link MethodDescriptor}.
 */
@RunWith(JUnit4.class)
public class MethodDescriptorTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void createMethodDescriptor() {
        MethodDescriptor<String, String> descriptor = MethodDescriptor.<String, String>newBuilder().setType(CLIENT_STREAMING).setFullMethodName("package.service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).build();
        Assert.assertEquals(CLIENT_STREAMING, descriptor.getType());
        Assert.assertEquals("package.service/method", descriptor.getFullMethodName());
        Assert.assertFalse(descriptor.isIdempotent());
        Assert.assertFalse(descriptor.isSafe());
    }

    @Test
    public void idempotent() {
        MethodDescriptor<String, String> descriptor = MethodDescriptor.<String, String>newBuilder().setType(SERVER_STREAMING).setFullMethodName("package.service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).build();
        Assert.assertFalse(descriptor.isIdempotent());
        // Create a new desriptor by setting idempotent to true
        MethodDescriptor<String, String> newDescriptor = descriptor.toBuilder().setIdempotent(true).build();
        Assert.assertTrue(newDescriptor.isIdempotent());
        // All other fields should staty the same
        Assert.assertEquals(SERVER_STREAMING, newDescriptor.getType());
        Assert.assertEquals("package.service/method", newDescriptor.getFullMethodName());
    }

    @Test
    public void safe() {
        MethodDescriptor<String, String> descriptor = MethodDescriptor.<String, String>newBuilder().setType(UNARY).setFullMethodName("package.service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).build();
        Assert.assertFalse(descriptor.isSafe());
        // Create a new desriptor by setting safe to true
        MethodDescriptor<String, String> newDescriptor = descriptor.toBuilder().setSafe(true).build();
        Assert.assertTrue(newDescriptor.isSafe());
        // All other fields should staty the same
        Assert.assertEquals(UNARY, newDescriptor.getType());
        Assert.assertEquals("package.service/method", newDescriptor.getFullMethodName());
    }

    @Test
    public void safeAndNonUnary() {
        MethodDescriptor<String, String> descriptor = MethodDescriptor.<String, String>newBuilder().setType(SERVER_STREAMING).setFullMethodName("package.service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).build();
        thrown.expect(IllegalArgumentException.class);
        MethodDescriptor<String, String> unused = descriptor.toBuilder().setSafe(true).build();
    }

    @Test
    public void sampledToLocalTracing() {
        MethodDescriptor<String, String> md1 = MethodDescriptor.<String, String>newBuilder().setType(SERVER_STREAMING).setFullMethodName("package.service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).setSampledToLocalTracing(true).build();
        Assert.assertTrue(md1.isSampledToLocalTracing());
        MethodDescriptor<String, String> md2 = md1.toBuilder().setFullMethodName("package.service/method2").build();
        Assert.assertTrue(md2.isSampledToLocalTracing());
        // Same method name as md1, but not setting sampledToLocalTracing
        MethodDescriptor<String, String> md3 = MethodDescriptor.<String, String>newBuilder().setType(SERVER_STREAMING).setFullMethodName("package.service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).build();
        Assert.assertFalse(md3.isSampledToLocalTracing());
        MethodDescriptor<String, String> md4 = md3.toBuilder().setFullMethodName("package.service/method2").setSampledToLocalTracing(true).build();
        Assert.assertTrue(md4.isSampledToLocalTracing());
    }

    @Test
    public void toBuilderTest() {
        MethodDescriptor<String, String> md1 = MethodDescriptor.<String, String>newBuilder().setType(UNARY).setFullMethodName("package.service/method").setRequestMarshaller(StringMarshaller.INSTANCE).setResponseMarshaller(StringMarshaller.INSTANCE).setSampledToLocalTracing(true).setIdempotent(true).setSafe(true).setSchemaDescriptor(new Object()).build();
        // Verify that we are not using any default builder values, so if md1 and md2 matches,
        // it's because toBuilder explicitly copied it.
        MethodDescriptor<String, String> defaults = MethodDescriptor.<String, String>newBuilder().setType(UNARY).setFullMethodName("package.service/method").setRequestMarshaller(StringMarshaller.INSTANCE).setResponseMarshaller(StringMarshaller.INSTANCE).build();
        Assert.assertNotEquals(md1.isSampledToLocalTracing(), defaults.isSampledToLocalTracing());
        Assert.assertNotEquals(md1.isIdempotent(), defaults.isIdempotent());
        Assert.assertNotEquals(md1.isSafe(), defaults.isSafe());
        Assert.assertNotEquals(md1.getSchemaDescriptor(), defaults.getSchemaDescriptor());
        // Verify that the builder correctly copied over the values
        MethodDescriptor<Integer, Integer> md2 = md1.toBuilder(IntegerMarshaller.INSTANCE, IntegerMarshaller.INSTANCE).build();
        TestCase.assertSame(md1.getType(), md2.getType());
        TestCase.assertSame(md1.getFullMethodName(), md2.getFullMethodName());
        TestCase.assertSame(IntegerMarshaller.INSTANCE, md2.getRequestMarshaller());
        TestCase.assertSame(IntegerMarshaller.INSTANCE, md2.getResponseMarshaller());
        Assert.assertEquals(md1.isSampledToLocalTracing(), md2.isSampledToLocalTracing());
        Assert.assertEquals(md1.isIdempotent(), md2.isIdempotent());
        Assert.assertEquals(md1.isSafe(), md2.isSafe());
        TestCase.assertSame(md1.getSchemaDescriptor(), md2.getSchemaDescriptor());
    }

    @Test
    public void toStringTest() {
        MethodDescriptor<String, String> descriptor = MethodDescriptor.<String, String>newBuilder().setType(UNARY).setFullMethodName("package.service/method").setRequestMarshaller(StringMarshaller.INSTANCE).setResponseMarshaller(StringMarshaller.INSTANCE).setSampledToLocalTracing(true).setIdempotent(true).setSafe(true).setSchemaDescriptor(new Object()).build();
        String toString = descriptor.toString();
        Assert.assertTrue(toString.contains("MethodDescriptor"));
        Assert.assertTrue(toString.contains("fullMethodName=package.service/method"));
        Assert.assertTrue(toString.contains("type=UNARY"));
        Assert.assertTrue(toString.contains("idempotent=true"));
        Assert.assertTrue(toString.contains("safe=true"));
        Assert.assertTrue(toString.contains("sampledToLocalTracing=true"));
        Assert.assertTrue(toString.contains("requestMarshaller=io.grpc.StringMarshaller"));
        Assert.assertTrue(toString.contains("responseMarshaller=io.grpc.StringMarshaller"));
        Assert.assertTrue(toString.contains("schemaDescriptor=java.lang.Object"));
    }
}

