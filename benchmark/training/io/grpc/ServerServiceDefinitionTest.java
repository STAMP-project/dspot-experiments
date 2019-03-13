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


import MethodDescriptor.MethodType.UNKNOWN;
import ServerServiceDefinition.Builder;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ServerServiceDefinition}.
 */
@RunWith(JUnit4.class)
public class ServerServiceDefinitionTest {
    private String serviceName = "com.example.service";

    private MethodDescriptor<String, Integer> method1 = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName(MethodDescriptor.generateFullMethodName(serviceName, "method1")).setRequestMarshaller(StringMarshaller.INSTANCE).setResponseMarshaller(IntegerMarshaller.INSTANCE).build();

    private MethodDescriptor<String, Integer> diffMethod1 = method1.toBuilder().setIdempotent(true).build();

    private MethodDescriptor<String, Integer> method2 = method1.toBuilder().setFullMethodName(MethodDescriptor.generateFullMethodName(serviceName, "method2")).build();

    private ServerCallHandler<String, Integer> methodHandler1 = new ServerServiceDefinitionTest.NoopServerCallHandler();

    private ServerCallHandler<String, Integer> methodHandler2 = new ServerServiceDefinitionTest.NoopServerCallHandler();

    private ServerMethodDefinition<String, Integer> methodDef1 = ServerMethodDefinition.create(method1, methodHandler1);

    private ServerMethodDefinition<String, Integer> methodDef2 = ServerMethodDefinition.create(method2, methodHandler2);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noMethods() {
        ServiceDescriptor sd = new ServiceDescriptor(serviceName);
        ServerServiceDefinition ssd = ServerServiceDefinition.builder(sd).build();
        Assert.assertSame(sd, ssd.getServiceDescriptor());
        Assert.assertEquals(Collections.<MethodDescriptor<?, ?>>emptyList(), new java.util.ArrayList(ssd.getServiceDescriptor().getMethods()));
    }

    @Test
    public void addMethod_twoArg() {
        ServiceDescriptor sd = new ServiceDescriptor(serviceName, method1, method2);
        ServerServiceDefinition ssd = ServerServiceDefinition.builder(sd).addMethod(method1, methodHandler1).addMethod(method2, methodHandler2).build();
        Assert.assertSame(sd, ssd.getServiceDescriptor());
        for (ServerMethodDefinition<?, ?> serverMethod : ssd.getMethods()) {
            MethodDescriptor<?, ?> method = serverMethod.getMethodDescriptor();
            if (method1.equals(method)) {
                Assert.assertSame(methodHandler1, serverMethod.getServerCallHandler());
            } else
                if (method2.equals(method)) {
                    Assert.assertSame(methodHandler2, serverMethod.getServerCallHandler());
                } else {
                    Assert.fail(("Unexpected method descriptor: " + (method.getFullMethodName())));
                }

        }
    }

    @Test
    public void addMethod_duplicateName() {
        ServiceDescriptor sd = new ServiceDescriptor(serviceName, method1);
        ServerServiceDefinition.Builder ssd = ServerServiceDefinition.builder(sd).addMethod(method1, methodHandler1);
        thrown.expect(IllegalStateException.class);
        ssd.addMethod(diffMethod1, methodHandler2).build();
    }

    @Test
    public void buildMisaligned_extraMethod() {
        ServiceDescriptor sd = new ServiceDescriptor(serviceName);
        ServerServiceDefinition.Builder ssd = ServerServiceDefinition.builder(sd).addMethod(methodDef1);
        thrown.expect(IllegalStateException.class);
        ssd.build();
    }

    @Test
    public void buildMisaligned_diffMethodInstance() {
        ServiceDescriptor sd = new ServiceDescriptor(serviceName, method1);
        ServerServiceDefinition.Builder ssd = ServerServiceDefinition.builder(sd).addMethod(diffMethod1, methodHandler1);
        thrown.expect(IllegalStateException.class);
        ssd.build();
    }

    @Test
    public void buildMisaligned_missingMethod() {
        ServiceDescriptor sd = new ServiceDescriptor(serviceName, method1);
        ServerServiceDefinition.Builder ssd = ServerServiceDefinition.builder(sd);
        thrown.expect(IllegalStateException.class);
        ssd.build();
    }

    @Test
    public void builderWithServiceName() {
        ServerServiceDefinition ssd = ServerServiceDefinition.builder(serviceName).addMethod(methodDef1).addMethod(methodDef2).build();
        Assert.assertEquals(serviceName, ssd.getServiceDescriptor().getName());
        HashSet<MethodDescriptor<?, ?>> goldenMethods = new HashSet<>();
        goldenMethods.add(method1);
        goldenMethods.add(method2);
        Assert.assertEquals(goldenMethods, new HashSet(ssd.getServiceDescriptor().getMethods()));
        HashSet<ServerMethodDefinition<?, ?>> goldenMethodDefs = new HashSet<>();
        goldenMethodDefs.add(methodDef1);
        goldenMethodDefs.add(methodDef2);
        Assert.assertEquals(goldenMethodDefs, new HashSet(ssd.getMethods()));
    }

    @Test
    public void builderWithServiceName_noMethods() {
        ServerServiceDefinition ssd = ServerServiceDefinition.builder(serviceName).build();
        Assert.assertEquals(Collections.<MethodDescriptor<?, ?>>emptyList(), new java.util.ArrayList(ssd.getServiceDescriptor().getMethods()));
        Assert.assertEquals(Collections.<ServerMethodDefinition<?, ?>>emptySet(), new HashSet(ssd.getMethods()));
    }

    private static class NoopServerCallHandler<ReqT, RespT> implements ServerCallHandler<ReqT, RespT> {
        @Override
        public ServerCall.Listener<ReqT> startCall(ServerCall<ReqT, RespT> call, Metadata headers) {
            throw new UnsupportedOperationException();
        }
    }
}

