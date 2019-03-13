/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import ServiceWithNoOuter.Interface;
import TestService.BlockingInterface;
import UnittestNoGenericServices.TestEnum.FOO;
import UnittestNoGenericServices.TestMessage;
import UnittestNoGenericServices.testExtension;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import google.protobuf.no_generic_services_test.UnittestNoGenericServices;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import protobuf_unittest.MessageWithNoOuter;
import protobuf_unittest.ServiceWithNoOuter;
import protobuf_unittest.UnittestProto.BarRequest;
import protobuf_unittest.UnittestProto.BarResponse;
import protobuf_unittest.UnittestProto.FooRequest;
import protobuf_unittest.UnittestProto.FooResponse;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestService;


/**
 * Tests services and stubs.
 *
 * @author kenton@google.com Kenton Varda
 */
public class ServiceTest extends TestCase {
    private IMocksControl control;

    private RpcController mockController;

    private final MethodDescriptor fooDescriptor = TestService.getDescriptor().getMethods().get(0);

    private final MethodDescriptor barDescriptor = TestService.getDescriptor().getMethods().get(1);

    // =================================================================
    /**
     * Tests Service.callMethod().
     */
    public void testCallMethod() throws Exception {
        FooRequest fooRequest = FooRequest.newBuilder().build();
        BarRequest barRequest = BarRequest.newBuilder().build();
        ServiceTest.MockCallback<Message> fooCallback = new ServiceTest.MockCallback<Message>();
        ServiceTest.MockCallback<Message> barCallback = new ServiceTest.MockCallback<Message>();
        TestService mockService = control.createMock(TestService.class);
        mockService.foo(EasyMock.same(mockController), EasyMock.same(fooRequest), this.<FooResponse>wrapsCallback(fooCallback));
        mockService.bar(EasyMock.same(mockController), EasyMock.same(barRequest), this.<BarResponse>wrapsCallback(barCallback));
        control.replay();
        mockService.callMethod(fooDescriptor, mockController, fooRequest, fooCallback);
        mockService.callMethod(barDescriptor, mockController, barRequest, barCallback);
        control.verify();
    }

    /**
     * Tests Service.get{Request,Response}Prototype().
     */
    public void testGetPrototype() throws Exception {
        TestService mockService = control.createMock(TestService.class);
        TestCase.assertSame(mockService.getRequestPrototype(fooDescriptor), FooRequest.getDefaultInstance());
        TestCase.assertSame(mockService.getResponsePrototype(fooDescriptor), FooResponse.getDefaultInstance());
        TestCase.assertSame(mockService.getRequestPrototype(barDescriptor), BarRequest.getDefaultInstance());
        TestCase.assertSame(mockService.getResponsePrototype(barDescriptor), BarResponse.getDefaultInstance());
    }

    /**
     * Tests generated stubs.
     */
    public void testStub() throws Exception {
        FooRequest fooRequest = FooRequest.newBuilder().build();
        BarRequest barRequest = BarRequest.newBuilder().build();
        ServiceTest.MockCallback<FooResponse> fooCallback = new ServiceTest.MockCallback<FooResponse>();
        ServiceTest.MockCallback<BarResponse> barCallback = new ServiceTest.MockCallback<BarResponse>();
        RpcChannel mockChannel = control.createMock(RpcChannel.class);
        TestService stub = TestService.newStub(mockChannel);
        mockChannel.callMethod(EasyMock.same(fooDescriptor), EasyMock.same(mockController), EasyMock.same(fooRequest), EasyMock.same(FooResponse.getDefaultInstance()), this.<Message>wrapsCallback(fooCallback));
        mockChannel.callMethod(EasyMock.same(barDescriptor), EasyMock.same(mockController), EasyMock.same(barRequest), EasyMock.same(BarResponse.getDefaultInstance()), this.<Message>wrapsCallback(barCallback));
        control.replay();
        stub.foo(mockController, fooRequest, fooCallback);
        stub.bar(mockController, barRequest, barCallback);
        control.verify();
    }

    /**
     * Tests generated blocking stubs.
     */
    public void testBlockingStub() throws Exception {
        FooRequest fooRequest = FooRequest.newBuilder().build();
        BarRequest barRequest = BarRequest.newBuilder().build();
        BlockingRpcChannel mockChannel = control.createMock(BlockingRpcChannel.class);
        TestService.BlockingInterface stub = TestService.newBlockingStub(mockChannel);
        FooResponse fooResponse = FooResponse.newBuilder().build();
        BarResponse barResponse = BarResponse.newBuilder().build();
        EasyMock.expect(mockChannel.callBlockingMethod(EasyMock.same(fooDescriptor), EasyMock.same(mockController), EasyMock.same(fooRequest), EasyMock.same(FooResponse.getDefaultInstance()))).andReturn(fooResponse);
        EasyMock.expect(mockChannel.callBlockingMethod(EasyMock.same(barDescriptor), EasyMock.same(mockController), EasyMock.same(barRequest), EasyMock.same(BarResponse.getDefaultInstance()))).andReturn(barResponse);
        control.replay();
        TestCase.assertSame(fooResponse, stub.foo(mockController, fooRequest));
        TestCase.assertSame(barResponse, stub.bar(mockController, barRequest));
        control.verify();
    }

    public void testNewReflectiveService() {
        ServiceWithNoOuter.Interface impl = control.createMock(Interface.class);
        RpcController controller = control.createMock(RpcController.class);
        Service service = ServiceWithNoOuter.newReflectiveService(impl);
        MethodDescriptor fooMethod = ServiceWithNoOuter.getDescriptor().findMethodByName("Foo");
        MessageWithNoOuter request = MessageWithNoOuter.getDefaultInstance();
        RpcCallback<Message> callback = new RpcCallback<Message>() {
            @Override
            public void run(Message parameter) {
                // No reason this should be run.
                TestCase.fail();
            }
        };
        RpcCallback<TestAllTypes> specializedCallback = RpcUtil.specializeCallback(callback);
        impl.foo(EasyMock.same(controller), EasyMock.same(request), EasyMock.same(specializedCallback));
        EasyMock.expectLastCall();
        control.replay();
        service.callMethod(fooMethod, controller, request, callback);
        control.verify();
    }

    public void testNewReflectiveBlockingService() throws ServiceException {
        ServiceWithNoOuter.BlockingInterface impl = control.createMock(ServiceWithNoOuter.BlockingInterface.class);
        RpcController controller = control.createMock(RpcController.class);
        BlockingService service = ServiceWithNoOuter.newReflectiveBlockingService(impl);
        MethodDescriptor fooMethod = ServiceWithNoOuter.getDescriptor().findMethodByName("Foo");
        MessageWithNoOuter request = MessageWithNoOuter.getDefaultInstance();
        TestAllTypes expectedResponse = TestAllTypes.getDefaultInstance();
        EasyMock.expect(impl.foo(EasyMock.same(controller), EasyMock.same(request))).andReturn(expectedResponse);
        control.replay();
        Message response = service.callBlockingMethod(fooMethod, controller, request);
        TestCase.assertEquals(expectedResponse, response);
        control.verify();
    }

    public void testNoGenericServices() throws Exception {
        // Non-services should be usable.
        UnittestNoGenericServices.TestMessage message = TestMessage.newBuilder().setA(123).setExtension(testExtension, 456).build();
        TestCase.assertEquals(123, message.getA());
        TestCase.assertEquals(1, FOO.getNumber());
        // Build a list of the class names nested in UnittestNoGenericServices.
        String outerName = "google.protobuf.no_generic_services_test." + "UnittestNoGenericServices";
        Class<?> outerClass = Class.forName(outerName);
        Set<String> innerClassNames = new HashSet<String>();
        for (Class<?> innerClass : outerClass.getClasses()) {
            String fullName = innerClass.getName();
            // Figure out the unqualified name of the inner class.
            // Note:  Surprisingly, the full name of an inner class will be separated
            // from the outer class name by a '$' rather than a '.'.  This is not
            // mentioned in the documentation for java.lang.Class.  I don't want to
            // make assumptions, so I'm just going to accept any character as the
            // separator.
            TestCase.assertTrue(fullName.startsWith(outerName));
            if (((!(Service.class.isAssignableFrom(innerClass))) && (!(Message.class.isAssignableFrom(innerClass)))) && (!(ProtocolMessageEnum.class.isAssignableFrom(innerClass)))) {
                // Ignore any classes not generated by the base code generator.
                continue;
            }
            innerClassNames.add(fullName.substring(((outerName.length()) + 1)));
        }
        // No service class should have been generated.
        TestCase.assertTrue(innerClassNames.contains("TestMessage"));
        TestCase.assertTrue(innerClassNames.contains("TestEnum"));
        TestCase.assertFalse(innerClassNames.contains("TestService"));
        // But descriptors are there.
        FileDescriptor file = UnittestNoGenericServices.getDescriptor();
        TestCase.assertEquals(1, file.getServices().size());
        TestCase.assertEquals("TestService", file.getServices().get(0).getName());
        TestCase.assertEquals(1, file.getServices().get(0).getMethods().size());
        TestCase.assertEquals("Foo", file.getServices().get(0).getMethods().get(0).getName());
    }

    /**
     * The parameter to wrapsCallback() must be a MockCallback.
     */
    private static class MockCallback<Type extends Message> implements RpcCallback<Type> {
        private boolean called = false;

        public boolean isCalled() {
            return called;
        }

        public void reset() {
            called = false;
        }

        @Override
        public void run(Type message) {
            called = true;
        }
    }

    /**
     * Implementation of the wrapsCallback() argument matcher.
     */
    private static class WrapsCallback implements IArgumentMatcher {
        private ServiceTest.MockCallback<?> callback;

        public WrapsCallback(ServiceTest.MockCallback<?> callback) {
            this.callback = callback;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object actual) {
            if (!(actual instanceof RpcCallback)) {
                return false;
            }
            RpcCallback actualCallback = ((RpcCallback) (actual));
            callback.reset();
            actualCallback.run(null);
            return callback.isCalled();
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append("wrapsCallback(mockCallback)");
        }
    }
}

