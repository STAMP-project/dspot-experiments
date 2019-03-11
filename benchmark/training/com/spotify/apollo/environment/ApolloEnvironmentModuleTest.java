/**
 * -\-\-
 * Spotify Apollo API Environment
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.environment;


import Service.Builder;
import Service.Instance;
import Status.GONE;
import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.AppInit;
import com.spotify.apollo.Environment;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestMetadata;
import com.spotify.apollo.Response;
import com.spotify.apollo.core.Service;
import com.spotify.apollo.request.OngoingRequest;
import com.spotify.apollo.request.RequestHandler;
import com.spotify.apollo.request.RequestMetadataImpl;
import com.spotify.apollo.route.Route;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Test;


public class ApolloEnvironmentModuleTest {
    private static final String[] ZERO_ARGS = new String[0];

    private ApolloEnvironmentModule appModule;

    private Builder service;

    @Test
    public void shouldHandleAppInit() throws Exception {
        final AtomicBoolean init = new AtomicBoolean();
        final AtomicBoolean destroy = new AtomicBoolean();
        try (Service.Instance i = service.build().start()) {
            final ApolloEnvironment environment = ApolloEnvironmentModule.environment(i);
            final RequestHandler handler = environment.initialize(( env) -> {
                init.set(true);
                env.closer().register(() -> destroy.set(true));
            });
            Assert.assertNotNull(handler);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(init.get());
        Assert.assertTrue(destroy.get());
    }

    @Test
    public void shouldGetFunctioningEnvironment() throws Exception {
        final AtomicReference<Environment> envReference = new AtomicReference<>();
        try (Service.Instance i = service.build().start()) {
            final ApolloEnvironment environment = ApolloEnvironmentModule.environment(i);
            final RequestHandler handler = environment.initialize(new ApolloEnvironmentModuleTest.EnvApp(envReference::set));
            Assert.assertNotNull(handler);
            final Environment e = envReference.get();
            ApolloEnvironmentModuleTest.validateEnv(e);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldGetFunctioningEnvironmentInAppInit() throws Exception {
        final AtomicReference<Environment> envReference = new AtomicReference<>();
        try (Service.Instance i = service.build().start()) {
            final ApolloEnvironment environment = ApolloEnvironmentModule.environment(i);
            final RequestHandler handler = environment.initialize(envReference::set);
            Assert.assertNotNull(handler);
            final Environment e = envReference.get();
            ApolloEnvironmentModuleTest.validateEnv(e);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldSetUpConfig() throws Exception {
        final AtomicReference<Environment> envReference = new AtomicReference<>();
        final Map<String, String> env = ImmutableMap.of("APOLLO_APOLLO_DOMAIN", "my-domain");
        try (Service.Instance i = service.build().start(ApolloEnvironmentModuleTest.ZERO_ARGS, env)) {
            final ApolloEnvironment environment = ApolloEnvironmentModule.environment(i);
            final RequestHandler handler = environment.initialize(new ApolloEnvironmentModuleTest.EnvApp(envReference::set));
            Assert.assertNotNull(handler);
            final Environment e = envReference.get();
            Assert.assertNotNull(e);
            Assert.assertNotNull(e.config());
            Assert.assertEquals("baz", e.config().getString("bar"));// from ping.conf

            Assert.assertEquals("my-domain", e.domain());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldUseRequestRunnableFactoryDecorator() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        final Service service = this.service.withModule(new RequestInspectingModule("http://foo", counter)).build();
        try (Service.Instance i = service.start()) {
            final ApolloEnvironment environment = ApolloEnvironmentModule.environment(i);
            final RequestHandler handler = environment.initialize(( env) -> {
            });
            Assert.assertNotNull(handler);
            final OngoingRequest ongoingRequest = ongoingRequest("http://foo");
            handler.handle(ongoingRequest);
            Assert.assertEquals(1, counter.get());
            handler.handle(ongoingRequest);
            Assert.assertEquals(2, counter.get());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void shouldUseEndpointRunnableFactoryDecorator() throws Exception {
        AtomicReference<String> lastResponseRef = new AtomicReference<>();
        final Service service = this.service.withModule(new LastResponseModule(lastResponseRef)).build();
        try (Service.Instance i = service.start()) {
            final ApolloEnvironment environment = ApolloEnvironmentModule.environment(i);
            final RequestHandler handler = environment.initialize(( env) -> {
                env.routingEngine().registerAutoRoute(Route.sync("GET", "/", ( ctx) -> "hello"));
            });
            Assert.assertNotNull(handler);
            final ApolloEnvironmentModuleTest.FakeOngoingRequest ongoingRequest = ongoingRequest("http://foo");
            handler.handle(ongoingRequest);
            Assert.assertThat(ongoingRequest.getReply(), hasStatus(GONE));
            Assert.assertEquals("hello", lastResponseRef.get());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    private static class EnvApp implements AppInit {
        private final Consumer<Environment> envCallback;

        EnvApp(Consumer<Environment> envCallback) {
            this.envCallback = envCallback;
        }

        @Override
        public void create(Environment environment) {
            Assert.assertNotNull(environment);
            envCallback.accept(environment);
        }
    }

    private static class FakeOngoingRequest implements OngoingRequest {
        private final Request request;

        private volatile Response<ByteString> reply = null;

        FakeOngoingRequest(Request request) {
            this.request = request;
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public void reply(Response<ByteString> response) {
            reply = response;
        }

        @Override
        public void drop() {
        }

        @Override
        public boolean isExpired() {
            return false;
        }

        @Override
        public RequestMetadata metadata() {
            return RequestMetadataImpl.create(Instant.EPOCH, Optional.empty(), Optional.empty());
        }

        public Response<ByteString> getReply() {
            return reply;
        }
    }
}

