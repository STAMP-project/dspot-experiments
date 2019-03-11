/**
 * Copyright 2015 LINE Corporation
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
package com.linecorp.armeria.server.thrift;


import TApplicationException.INTERNAL_ERROR;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.SerializationFormat;
import com.linecorp.armeria.common.thrift.ThriftProtocolFactories;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.service.test.thrift.main.BinaryService;
import com.linecorp.armeria.service.test.thrift.main.DevNullService;
import com.linecorp.armeria.service.test.thrift.main.FileService;
import com.linecorp.armeria.service.test.thrift.main.FileServiceException;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.service.test.thrift.main.Name;
import com.linecorp.armeria.service.test.thrift.main.NameService;
import com.linecorp.armeria.service.test.thrift.main.NameSortService;
import com.linecorp.armeria.service.test.thrift.main.OnewayHelloService;
import com.linecorp.armeria.testing.common.EventLoopRule;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests {@link ThriftCallService} and {@link THttpService}.
 * <p>
 * The test methods have the following naming convention:
 *
 * <pre><i>TestType</i>_<i>ServiceName</i>_<i>MethodName</i>[_<i>AdditionalInfo</i>]()</pre>
 *
 * .. where each field has the following meaning:
 * <ul>
 *     <li>{@code TestType}
 *     <ul>
 *         <li>{@code Sync} tests an invocation of synchronous service. i.e. {@code *.Iface}</li>
 *         <li>{@code Async} tests an invocation of asynchronous service. i.e. {@code *.AsyncIface}</li>
 *         <li>{@code Identity} tests if the results of synchronous and asynchronous operations are identical
 *             at protocol level.</li>
 *         <li>{@code MultipleInheritance} tests the case where a service implementation implements
 *             multiple interfaces.</li>
 *     </ul></li>
 *     <li>{@code ServiceName} - the class name of the service being tested</li>
 *     <li>{@code MethodName} - the name of the method in the service being tested</li>
 *     <li>(Optional) {@code AdditionalInfo} - specified when a service method has more than one test case</li>
 * </ul>
 * </p>
 */
@RunWith(Parameterized.class)
public class ThriftServiceTest {
    private static final Name NAME_A = new Name("a", "a", "a");

    private static final Name NAME_B = new Name("b", "b", "b");

    private static final Name NAME_C = new Name("c", "c", "c");

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String BAZ = "baz";

    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    private final SerializationFormat defaultSerializationFormat;

    private TProtocol inProto;

    private TProtocol outProto;

    private TMemoryInputTransport in;

    private TMemoryBuffer out;

    private CompletableFuture<HttpData> promise;

    private CompletableFuture<HttpData> promise2;

    public ThriftServiceTest(SerializationFormat defaultSerializationFormat) {
        this.defaultSerializationFormat = defaultSerializationFormat;
    }

    @Test
    public void testSync_HelloService_hello() throws Exception {
        final HelloService.Client client = new HelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((HelloService.Iface) (( name) -> ("Hello, " + name) + '!')), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_hello()).isEqualTo("Hello, foo!");
    }

    @Test
    public void testAsync_HelloService_hello() throws Exception {
        final HelloService.Client client = new HelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((HelloService.AsyncIface) (( name, resultHandler) -> resultHandler.onComplete((("Hello, " + name) + '!')))), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_hello()).isEqualTo("Hello, foo!");
    }

    @Test
    public void testSync_HelloService_hello_with_null() throws Exception {
        final HelloService.Client client = new HelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(null);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((HelloService.Iface) (( name) -> String.valueOf((name != null)))), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_hello()).isEqualTo("false");
    }

    @Test
    public void testAsync_HelloService_hello_with_null() throws Exception {
        final HelloService.Client client = new HelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(null);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((HelloService.AsyncIface) (( name, resultHandler) -> resultHandler.onComplete(String.valueOf((name != null))))), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_hello()).isEqualTo("false");
    }

    @Test
    public void testIdentity_HelloService_hello() throws Exception {
        final HelloService.Client client = new HelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService syncService = THttpService.of(((HelloService.Iface) (( name) -> ("Hello, " + name) + '!')), defaultSerializationFormat);
        final THttpService asyncService = THttpService.of(((HelloService.AsyncIface) (( name, resultHandler) -> resultHandler.onComplete((("Hello, " + name) + '!')))), defaultSerializationFormat);
        invokeTwice(syncService, asyncService);
        assertThat(promise.get()).isEqualTo(promise2.get());
    }

    @Test
    public void testSync_OnewayHelloService_hello() throws Exception {
        final AtomicReference<String> actualName = new AtomicReference<>();
        final OnewayHelloService.Client client = new OnewayHelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((OnewayHelloService.Iface) (actualName::set)), defaultSerializationFormat);
        invoke(service);
        assertThat(promise.get().isEmpty()).isTrue();
        assertThat(actualName.get()).isEqualTo(ThriftServiceTest.FOO);
    }

    @Test
    public void testAsync_OnewayHelloService_hello() throws Exception {
        final AtomicReference<String> actualName = new AtomicReference<>();
        final OnewayHelloService.Client client = new OnewayHelloService.Client.Factory().getClient(inProto, outProto);
        client.send_hello(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((OnewayHelloService.AsyncIface) (( name, resultHandler) -> {
            actualName.set(name);
            resultHandler.onComplete(null);
        })), defaultSerializationFormat);
        invoke(service);
        assertThat(promise.get().isEmpty()).isTrue();
        assertThat(actualName.get()).isEqualTo(ThriftServiceTest.FOO);
    }

    @Test
    public void testSync_DevNullService_consume() throws Exception {
        final AtomicReference<String> consumed = new AtomicReference<>();
        final DevNullService.Client client = new DevNullService.Client.Factory().getClient(inProto, outProto);
        client.send_consume(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((DevNullService.Iface) (consumed::set)), defaultSerializationFormat);
        invoke(service);
        assertThat(consumed.get()).isEqualTo(ThriftServiceTest.FOO);
        client.recv_consume();
    }

    @Test
    public void testAsync_DevNullService_consume() throws Exception {
        final AtomicReference<String> consumed = new AtomicReference<>();
        final DevNullService.Client client = new DevNullService.Client.Factory().getClient(inProto, outProto);
        client.send_consume("bar");
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((DevNullService.AsyncIface) (( value, resultHandler) -> {
            consumed.set(value);
            resultHandler.onComplete(null);
        })), defaultSerializationFormat);
        invoke(service);
        assertThat(consumed.get()).isEqualTo("bar");
        client.recv_consume();
    }

    @Test
    public void testIdentity_DevNullService_consume() throws Exception {
        final DevNullService.Client client = new DevNullService.Client.Factory().getClient(inProto, outProto);
        client.send_consume(ThriftServiceTest.FOO);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService syncService = THttpService.of(((DevNullService.Iface) (( value) -> {
            // NOOP
        })), defaultSerializationFormat);
        final THttpService asyncService = THttpService.of(((DevNullService.AsyncIface) (( value, resultHandler) -> resultHandler.onComplete(null))), defaultSerializationFormat);
        invokeTwice(syncService, asyncService);
        assertThat(promise.get()).isEqualTo(promise2.get());
    }

    @Test
    public void testSync_FileService_create_reply() throws Exception {
        final FileService.Client client = new FileService.Client.Factory().getClient(inProto, outProto);
        client.send_create(ThriftServiceTest.BAR);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((FileService.Iface) (( path) -> {
            throw newFileServiceException();
        })), defaultSerializationFormat);
        invoke(service);
        try {
            client.recv_create();
            Assert.fail(((FileServiceException.class.getSimpleName()) + " not raised."));
        } catch (FileServiceException ignored) {
            // Expected
        }
    }

    @Test
    public void testAsync_FileService_create_reply() throws Exception {
        final FileService.Client client = new FileService.Client.Factory().getClient(inProto, outProto);
        client.send_create(ThriftServiceTest.BAR);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((FileService.AsyncIface) (( path, resultHandler) -> resultHandler.onError(newFileServiceException()))), defaultSerializationFormat);
        invoke(service);
        try {
            client.recv_create();
            Assert.fail(((FileServiceException.class.getSimpleName()) + " not raised."));
        } catch (FileServiceException ignored) {
            // Expected
        }
    }

    @Test
    public void testIdentity_FileService_create_reply() throws Exception {
        final FileService.Client client = new FileService.Client.Factory().getClient(inProto, outProto);
        client.send_create(ThriftServiceTest.BAR);
        assertThat(out.length()).isGreaterThan(0);
        final THttpService syncService = THttpService.of(((FileService.Iface) (( path) -> {
            throw newFileServiceException();
        })), defaultSerializationFormat);
        final THttpService asyncService = THttpService.of(((FileService.AsyncIface) (( path, resultHandler) -> resultHandler.onError(newFileServiceException()))), defaultSerializationFormat);
        invokeTwice(syncService, asyncService);
        assertThat(promise.get()).isEqualTo(promise2.get());
    }

    @Test
    public void testSync_FileService_create_exception() throws Exception {
        final FileService.Client client = new FileService.Client.Factory().getClient(inProto, outProto);
        client.send_create(ThriftServiceTest.BAZ);
        assertThat(out.length()).isGreaterThan(0);
        final RuntimeException exception = Exceptions.clearTrace(new RuntimeException());
        final THttpService service = THttpService.of(((FileService.Iface) (( path) -> {
            throw exception;
        })), defaultSerializationFormat);
        invoke(service);
        try {
            client.recv_create();
            Assert.fail(((TApplicationException.class.getSimpleName()) + " not raised."));
        } catch (TApplicationException e) {
            assertThat(e.getType()).isEqualTo(INTERNAL_ERROR);
            assertThat(e.getMessage()).contains(exception.toString());
        }
    }

    @Test
    public void testAsync_FileService_create_exception() throws Exception {
        final FileService.Client client = new FileService.Client.Factory().getClient(inProto, outProto);
        client.send_create(ThriftServiceTest.BAZ);
        assertThat(out.length()).isGreaterThan(0);
        final RuntimeException exception = Exceptions.clearTrace(new RuntimeException());
        final THttpService service = THttpService.of(((FileService.AsyncIface) (( path, resultHandler) -> resultHandler.onError(exception))), defaultSerializationFormat);
        invoke(service);
        try {
            client.recv_create();
            Assert.fail(((TApplicationException.class.getSimpleName()) + " not raised."));
        } catch (TApplicationException e) {
            assertThat(e.getType()).isEqualTo(INTERNAL_ERROR);
            assertThat(e.getMessage()).contains(exception.toString());
        }
    }

    @Test
    public void testIdentity_FileService_create_exception() throws Exception {
        final FileService.Client client = new FileService.Client.Factory().getClient(inProto, outProto);
        client.send_create(ThriftServiceTest.BAZ);
        assertThat(out.length()).isGreaterThan(0);
        final RuntimeException exception = Exceptions.clearTrace(new RuntimeException());
        final THttpService syncService = THttpService.of(((FileService.Iface) (( path) -> {
            throw exception;
        })), defaultSerializationFormat);
        final THttpService asyncService = THttpService.of(((FileService.AsyncIface) (( path, resultHandler) -> resultHandler.onError(exception))), defaultSerializationFormat);
        invokeTwice(syncService, asyncService);
        assertThat(promise.get()).isEqualTo(promise2.get());
    }

    @Test
    public void testSync_NameService_removeMiddle() throws Exception {
        final NameService.Client client = new NameService.Client.Factory().getClient(inProto, outProto);
        client.send_removeMiddle(new Name(ThriftServiceTest.BAZ, ThriftServiceTest.BAR, ThriftServiceTest.FOO));
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((NameService.Iface) (( name) -> new Name(name.first, null, name.last))), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_removeMiddle()).isEqualTo(new Name(ThriftServiceTest.BAZ, null, ThriftServiceTest.FOO));
    }

    @Test
    public void testAsync_NameService_removeMiddle() throws Exception {
        final NameService.Client client = new NameService.Client.Factory().getClient(inProto, outProto);
        client.send_removeMiddle(new Name(ThriftServiceTest.BAZ, ThriftServiceTest.BAR, ThriftServiceTest.FOO));
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((NameService.AsyncIface) (( name, resultHandler) -> resultHandler.onComplete(new Name(name.first, null, name.last)))), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_removeMiddle()).isEqualTo(new Name(ThriftServiceTest.BAZ, null, ThriftServiceTest.FOO));
    }

    @Test
    public void testIdentity_NameService_removeMiddle() throws Exception {
        final NameService.Client client = new NameService.Client.Factory().getClient(inProto, outProto);
        client.send_removeMiddle(new Name(ThriftServiceTest.FOO, ThriftServiceTest.BAZ, ThriftServiceTest.BAR));
        assertThat(out.length()).isGreaterThan(0);
        final THttpService syncService = THttpService.of(((NameService.Iface) (( name) -> new Name(name.first, null, name.last))), defaultSerializationFormat);
        final THttpService asyncService = THttpService.of(((NameService.AsyncIface) (( name, resultHandler) -> resultHandler.onComplete(new Name(name.first, null, name.last)))), defaultSerializationFormat);
        invokeTwice(syncService, asyncService);
        assertThat(promise.get()).isEqualTo(promise2.get());
    }

    @Test
    public void testSync_NameSortService_sort() throws Exception {
        final NameSortService.Client client = new NameSortService.Client.Factory().getClient(inProto, outProto);
        client.send_sort(Arrays.asList(ThriftServiceTest.NAME_C, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_A));
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((NameSortService.Iface) (( names) -> {
            final ArrayList<Name> sorted = new ArrayList<>(names);
            Collections.sort(sorted);
            return sorted;
        })), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_sort()).containsExactly(ThriftServiceTest.NAME_A, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_C);
    }

    @Test
    public void testAsync_NameSortService_sort() throws Exception {
        final NameSortService.Client client = new NameSortService.Client.Factory().getClient(inProto, outProto);
        client.send_sort(Arrays.asList(ThriftServiceTest.NAME_C, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_A));
        assertThat(out.length()).isGreaterThan(0);
        final THttpService service = THttpService.of(((NameSortService.AsyncIface) (( names, resultHandler) -> {
            final ArrayList<Name> sorted = new ArrayList<>(names);
            Collections.sort(sorted);
            resultHandler.onComplete(sorted);
        })), defaultSerializationFormat);
        invoke(service);
        assertThat(client.recv_sort()).containsExactly(ThriftServiceTest.NAME_A, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_C);
    }

    @Test
    public void testIdentity_NameSortService_sort() throws Exception {
        final NameSortService.Client client = new NameSortService.Client.Factory().getClient(inProto, outProto);
        client.send_sort(Arrays.asList(ThriftServiceTest.NAME_C, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_A));
        assertThat(out.length()).isGreaterThan(0);
        final THttpService syncService = THttpService.of(((NameSortService.Iface) (( names) -> {
            final ArrayList<Name> sorted = new ArrayList<>(names);
            Collections.sort(sorted);
            return sorted;
        })), defaultSerializationFormat);
        final THttpService asyncService = THttpService.of(((NameSortService.AsyncIface) (( names, resultHandler) -> {
            final ArrayList<Name> sorted = new ArrayList<>(names);
            Collections.sort(sorted);
            resultHandler.onComplete(sorted);
        })), defaultSerializationFormat);
        invokeTwice(syncService, asyncService);
        assertThat(promise.get()).isEqualTo(promise2.get());
    }

    @Test
    public void testBinary() throws Exception {
        final BinaryService.Client client = new BinaryService.Client.Factory().getClient(inProto, outProto);
        client.send_process(ByteBuffer.wrap(new byte[]{ 1, 2 }));
        final THttpService service = THttpService.of(((BinaryService.Iface) (( data) -> {
            final ByteBuffer result = ByteBuffer.allocate(data.remaining());
            for (int i = data.position(), j = 0; i < (data.limit()); i++ , j++) {
                result.put(j, ((byte) ((data.get(i)) + 1)));
            }
            return result;
        })), defaultSerializationFormat);
        invoke(service);
        final ByteBuffer result = client.recv_process();
        // Convert the result into a Byte[] for more comprehensive comparison.
        final List<Byte> out = new java.util.ArrayList<>();
        for (int i = result.position(); i < (result.limit()); i++) {
            out.add(result.get(i));
        }
        assertThat(out).contains(((byte) (2)), ((byte) (3)));
    }

    @Test
    public void testMultipleInheritance() throws Exception {
        final NameService.Client client1 = new NameService.Client.Factory().getClient(inProto, outProto);
        client1.send_removeMiddle(new Name(ThriftServiceTest.BAZ, ThriftServiceTest.BAR, ThriftServiceTest.FOO));
        assertThat(out.length()).isGreaterThan(0);
        final HttpData req1 = HttpData.of(out.getArray(), 0, out.length());
        out = new TMemoryBuffer(128);
        outProto = ThriftProtocolFactories.get(defaultSerializationFormat).getProtocol(out);
        final NameSortService.Client client2 = new NameSortService.Client.Factory().getClient(inProto, outProto);
        client2.send_sort(Arrays.asList(ThriftServiceTest.NAME_C, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_A));
        assertThat(out.length()).isGreaterThan(0);
        final HttpData req2 = HttpData.of(out.getArray(), 0, out.length());
        final THttpService service = THttpService.of(((ThriftServiceTest.UberNameService) (( names, callback) -> callback.onComplete(names.stream().sorted().collect(toImmutableList())))), defaultSerializationFormat);
        ThriftServiceTest.invoke0(service, req1, promise);
        ThriftServiceTest.invoke0(service, req2, promise2);
        final HttpData res1 = promise.get();
        final HttpData res2 = promise2.get();
        in.reset(res1.array(), res1.offset(), res1.length());
        assertThat(client1.recv_removeMiddle()).isEqualTo(new Name(ThriftServiceTest.BAZ, null, ThriftServiceTest.FOO));
        in.reset(res2.array(), res2.offset(), res2.length());
        assertThat(client2.recv_sort()).containsExactly(ThriftServiceTest.NAME_A, ThriftServiceTest.NAME_B, ThriftServiceTest.NAME_C);
    }

    // NB: By making this interface functional, we can use lambda expression to implement
    // NameSortService.AsyncIface.sort(). By using lambda expression, we can omit the parameter type
    // declarations (i.e. no AsyncMethodCallback<List<Name>>). By omitting the parameter type declaration,
    // We can compile it with both Thrift 0.9 (which uses raw type for AsyncMethodCallback) and
    // 0.10 (which uses a concrete type parameter for AsyncMethodCallback.)
    @FunctionalInterface
    private interface UberNameService extends NameService.Iface , NameSortService.AsyncIface {
        @Override
        default Name removeMiddle(Name name) {
            return new Name(name.first, null, name.last);
        }
    }
}

