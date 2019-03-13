/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.handler;


import Constants.CHANNEL_ATTRIBUTE_READONLY_KEY;
import Request.READONLY_EVENT;
import Response.BAD_REQUEST;
import Response.OK;
import Response.SERVICE_ERROR;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.exchange.ExchangeChannel;
import org.apache.dubbo.remoting.exchange.ExchangeHandler;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.exchange.Response;
import org.apache.dubbo.remoting.exchange.support.header.HeaderExchangeHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


// TODO response test
public class HeaderExchangeHandlerTest {
    @Test
    public void test_received_request_oneway() throws RemotingException {
        final Channel mchannel = new MockedChannel();
        final HeaderExchangeHandlerTest.Person requestdata = new HeaderExchangeHandlerTest.Person("charles");
        Request request = new Request();
        request.setTwoWay(false);
        request.setData(requestdata);
        ExchangeHandler exhandler = new HeaderExchangeHandlerTest.MockedExchangeHandler() {
            @Override
            public void received(Channel channel, Object message) throws RemotingException {
                Assertions.assertEquals(requestdata, message);
            }
        };
        HeaderExchangeHandler hexhandler = new HeaderExchangeHandler(exhandler);
        hexhandler.received(mchannel, request);
    }

    @Test
    public void test_received_request_twoway() throws RemotingException {
        final HeaderExchangeHandlerTest.Person requestdata = new HeaderExchangeHandlerTest.Person("charles");
        final Request request = new Request();
        request.setTwoWay(true);
        request.setData(requestdata);
        final AtomicInteger count = new AtomicInteger(0);
        final Channel mchannel = new MockedChannel() {
            @Override
            public void send(Object message) throws RemotingException {
                Response res = ((Response) (message));
                Assertions.assertEquals(request.getId(), res.getId());
                Assertions.assertEquals(request.getVersion(), res.getVersion());
                Assertions.assertEquals(OK, res.getStatus());
                Assertions.assertEquals(requestdata, res.getResult());
                Assertions.assertEquals(null, res.getErrorMessage());
                count.incrementAndGet();
            }
        };
        ExchangeHandler exhandler = new HeaderExchangeHandlerTest.MockedExchangeHandler() {
            @Override
            public CompletableFuture<Object> reply(ExchangeChannel channel, Object request) throws RemotingException {
                return CompletableFuture.completedFuture(request);
            }

            @Override
            public void received(Channel channel, Object message) throws RemotingException {
                Assertions.fail();
            }
        };
        HeaderExchangeHandler hexhandler = new HeaderExchangeHandler(exhandler);
        hexhandler.received(mchannel, request);
        Assertions.assertEquals(1, count.get());
    }

    @Test
    public void test_received_request_twoway_error_nullhandler() throws RemotingException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HeaderExchangeHandler(null));
    }

    @Test
    public void test_received_request_twoway_error_reply() throws RemotingException {
        final HeaderExchangeHandlerTest.Person requestdata = new HeaderExchangeHandlerTest.Person("charles");
        final Request request = new Request();
        request.setTwoWay(true);
        request.setData(requestdata);
        final AtomicInteger count = new AtomicInteger(0);
        final Channel mchannel = new MockedChannel() {
            @Override
            public void send(Object message) throws RemotingException {
                Response res = ((Response) (message));
                Assertions.assertEquals(request.getId(), res.getId());
                Assertions.assertEquals(request.getVersion(), res.getVersion());
                Assertions.assertEquals(SERVICE_ERROR, res.getStatus());
                Assertions.assertNull(res.getResult());
                Assertions.assertTrue(res.getErrorMessage().contains(HeaderExchangeHandlerTest.BizException.class.getName()));
                count.incrementAndGet();
            }
        };
        ExchangeHandler exhandler = new HeaderExchangeHandlerTest.MockedExchangeHandler() {
            @Override
            public CompletableFuture<Object> reply(ExchangeChannel channel, Object request) throws RemotingException {
                throw new HeaderExchangeHandlerTest.BizException();
            }
        };
        HeaderExchangeHandler hexhandler = new HeaderExchangeHandler(exhandler);
        hexhandler.received(mchannel, request);
        Assertions.assertEquals(1, count.get());
    }

    @Test
    public void test_received_request_twoway_error_reqeustBroken() throws RemotingException {
        final Request request = new Request();
        request.setTwoWay(true);
        request.setData(new HeaderExchangeHandlerTest.BizException());
        request.setBroken(true);
        final AtomicInteger count = new AtomicInteger(0);
        final Channel mchannel = new MockedChannel() {
            @Override
            public void send(Object message) throws RemotingException {
                Response res = ((Response) (message));
                Assertions.assertEquals(request.getId(), res.getId());
                Assertions.assertEquals(request.getVersion(), res.getVersion());
                Assertions.assertEquals(BAD_REQUEST, res.getStatus());
                Assertions.assertNull(res.getResult());
                Assertions.assertTrue(res.getErrorMessage().contains(HeaderExchangeHandlerTest.BizException.class.getName()));
                count.incrementAndGet();
            }
        };
        HeaderExchangeHandler hexhandler = new HeaderExchangeHandler(new HeaderExchangeHandlerTest.MockedExchangeHandler());
        hexhandler.received(mchannel, request);
        Assertions.assertEquals(1, count.get());
    }

    @Test
    public void test_received_request_event_readonly() throws RemotingException {
        final Request request = new Request();
        request.setTwoWay(true);
        request.setEvent(READONLY_EVENT);
        final Channel mchannel = new MockedChannel();
        HeaderExchangeHandler hexhandler = new HeaderExchangeHandler(new HeaderExchangeHandlerTest.MockedExchangeHandler());
        hexhandler.received(mchannel, request);
        Assertions.assertTrue(mchannel.hasAttribute(CHANNEL_ATTRIBUTE_READONLY_KEY));
    }

    @Test
    public void test_received_request_event_other_discard() throws RemotingException {
        final Request request = new Request();
        request.setTwoWay(true);
        request.setEvent("my event");
        final Channel mchannel = new MockedChannel() {
            @Override
            public void send(Object message) throws RemotingException {
                Assertions.fail();
            }
        };
        HeaderExchangeHandler hexhandler = new HeaderExchangeHandler(new HeaderExchangeHandlerTest.MockedExchangeHandler() {
            @Override
            public CompletableFuture reply(ExchangeChannel channel, Object request) throws RemotingException {
                Assertions.fail();
                throw new RemotingException(channel, "");
            }

            @Override
            public void received(Channel channel, Object message) throws RemotingException {
                Assertions.fail();
                throw new RemotingException(channel, "");
            }
        });
        hexhandler.received(mchannel, request);
    }

    private class BizException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private class MockedExchangeHandler extends MockedChannelHandler implements ExchangeHandler {
        public String telnet(Channel channel, String message) throws RemotingException {
            throw new UnsupportedOperationException();
        }

        public CompletableFuture<Object> reply(ExchangeChannel channel, Object request) throws RemotingException {
            throw new UnsupportedOperationException();
        }
    }

    private class Person {
        private String name;

        public Person(String name) {
            super();
            this.name = name;
        }

        @Override
        public String toString() {
            return ("Person [name=" + (name)) + "]";
        }
    }
}

