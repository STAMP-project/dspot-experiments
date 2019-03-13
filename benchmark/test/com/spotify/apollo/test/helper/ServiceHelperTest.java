/**
 * -\-\-
 * Spotify Apollo Testing Helpers
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
package com.spotify.apollo.test.helper;


import com.google.common.base.Stopwatch;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.module.AbstractApolloModule;
import com.spotify.apollo.test.ServiceHelper;
import com.spotify.apollo.test.StubClient;
import java.util.concurrent.TimeUnit;
import okio.ByteString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(MockitoJUnitRunner.class)
public class ServiceHelperTest {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceHelperTest.class);

    private static final String TEST_THING = "a test string";

    private static final String TEST_CONFIG_THING = "a-test-string-in-the-config";

    private static final String SERVICE_NAME = "test-service";

    @Rule
    public ServiceHelper serviceHelper = ServiceHelper.create(this::appInit, ServiceHelperTest.SERVICE_NAME).startTimeoutSeconds(10).domain("xyz99.spotify.net").conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).args("-v");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private StubClient stubClient = serviceHelper.stubClient();

    @Mock
    SomeApplication.SomeService someService;

    @Mock
    static SomeApplication.CloseCall closeCall;

    @Test
    public void smokeTest() throws Exception {
        Mockito.when(someService.thatDoesThings()).thenReturn(ServiceHelperTest.TEST_THING);
        String response = doGet();
        Assert.assertThat(response, Matchers.is(ServiceHelperTest.TEST_THING));
    }

    @Test
    public void smokeTest2() throws Exception {
        Mockito.when(someService.thatDoesThings()).thenReturn(((ServiceHelperTest.TEST_THING) + " and more"));
        String response = doGet();
        Assert.assertThat(response, Matchers.is(((ServiceHelperTest.TEST_THING) + " and more")));
    }

    @Test
    public void testDomain() throws Exception {
        String response = doGet("/domain");
        Assert.assertThat(response, Matchers.is("xyz99.spotify.net"));
    }

    @Test
    public void shouldSeeConfigValues() throws Exception {
        String response = doGet("/conf-key");
        Assert.assertThat(response, Matchers.is(ServiceHelperTest.TEST_CONFIG_THING));
    }

    @Test
    public void shouldOverrideConfig() throws Exception {
        serviceHelper = ServiceHelper.create(this::appInit, ServiceHelperTest.SERVICE_NAME).conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).conf("some.key", "different config").args("-v");
        serviceHelper.start();
        String response = doGet("/conf-key");
        Assert.assertThat(response, Matchers.is("different config"));
    }

    @Test
    public void shouldResetConfig() throws Exception {
        serviceHelper = ServiceHelper.create(this::appInit, ServiceHelperTest.SERVICE_NAME).conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).resetConf("some.key").args("-v");
        serviceHelper.start();
        String response = doGet("/conf-key");
        Assert.assertThat(response, Matchers.is("no value found for some.key"));
    }

    @Test
    public void callTest() throws Exception {
        stubClient.respond(Response.forPayload(ByteString.encodeUtf8("hello from something"))).in(300, TimeUnit.MILLISECONDS).to("http://something/foo");
        String response = doGet("/call/something/foo");
        Assert.assertThat(response, Matchers.is("hello from something"));
    }

    @Test
    public void testPayload() throws Exception {
        Response<ByteString> thing = serviceHelper.request("POST", "/post", ByteString.encodeUtf8("bytes")).toCompletableFuture().get();
        Assert.assertThat(thing.payload().get().utf8(), Matchers.is("bytes"));
    }

    @Test
    public void testCallServiceClient() throws Exception {
        stubClient.respond(Response.forPayload(ByteString.encodeUtf8("hello from something"))).to("http://something/foo");
        Request request = Request.forUri((("http://" + (ServiceHelperTest.SERVICE_NAME)) + "/call/something/foo"));
        Response<ByteString> response = serviceHelper.serviceClient().send(request).toCompletableFuture().get();
        Assert.assertThat(response.payload().get().utf8(), Matchers.is("hello from something"));
    }

    @Test
    public void shouldDisallowRequestsBeforeStarted() throws Exception {
        ServiceHelper notStarted = ServiceHelper.create(this::appInit, "test-service").conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).args("-v");
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("ServiceHelper not started");
        notStarted.request("GET", "/");
    }

    @Test
    public void shouldTimeoutIfStartupIsSlow() throws Exception {
        ServiceHelper timeout = ServiceHelper.create(ServiceHelperTest::tooSlow, "test-service").conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).args("-v").startTimeoutSeconds(1);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            timeout.start();
        } catch (RuntimeException e) {
            if (!(e.getMessage().contains("a reasonable time"))) {
                throw e;
            }
        }
        Assert.assertThat(stopwatch.elapsed(TimeUnit.SECONDS), Matchers.lessThan(3L));
    }

    @Test
    public void shouldCatchExceptionBeforeTimeout() throws Exception {
        ServiceHelper timeout = ServiceHelper.create(ServiceHelperTest::throwsException, "test-service").conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).args("-v").startTimeoutSeconds(2);
        Stopwatch stopwatch = Stopwatch.createStarted();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Service failed during startup");
        timeout.start();
        Assert.assertThat(stopwatch.elapsed(TimeUnit.SECONDS), Matchers.lessThan(1L));
    }

    @Test
    public void shouldCatchErrorBeforeTimeout() throws Exception {
        ServiceHelper timeout = ServiceHelper.create(ServiceHelperTest::throwsError, "test-service").conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).args("-v").startTimeoutSeconds(2);
        Stopwatch stopwatch = Stopwatch.createStarted();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Service failed during startup");
        timeout.start();
        Assert.assertThat(stopwatch.elapsed(TimeUnit.SECONDS), Matchers.lessThan(1L));
    }

    @Test
    public void shouldSupportRegisteringAdditionalModules() throws Exception {
        stubClient.respond(Response.forPayload(ByteString.encodeUtf8("hello from something"))).to("http://something/foo");
        ServiceHelper withModule = ServiceHelper.create(( environment) -> {
            assertThat(environment.resolve(.class), is(notNullValue()));
        }, ServiceHelperTest.SERVICE_NAME).domain("xyz99.spotify.net").conf("some.key", ServiceHelperTest.TEST_CONFIG_THING).withModule(new ServiceHelperTest.TestModule()).args("-v");
        withModule.start();
    }

    @Test
    public void shouldUseScheme() throws Exception {
        ServiceHelper scheme = ServiceHelper.create(this::appInit, "test-service").scheme("gopher+my-go.pher");
        scheme.start();
        Response<ByteString> response = scheme.request("GET", "/uri").toCompletableFuture().get();
        scheme.close();
        Assert.assertThat(response.payload().get().utf8(), Matchers.is("gopher+my-go.pher://test-service/uri"));
    }

    @Test
    public void shouldValidateScheme() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal scheme format");
        ServiceHelper.create(this::appInit, "test-service").scheme("http://");// invalid

    }

    @Test
    public void shouldAllowSemanticMetricRegistryResolution() throws Exception {
        ServiceHelper serviceHelper = ServiceHelper.create(this::resolveRegistry, "resolve-registry");
        serviceHelper.start();
    }

    private static class TestModule extends AbstractApolloModule {
        @Override
        protected void configure() {
            bind(ServiceHelperTest.TestModule.class).toInstance(this);
        }

        @Override
        public String getId() {
            return "addheaderdecorator";
        }
    }
}

