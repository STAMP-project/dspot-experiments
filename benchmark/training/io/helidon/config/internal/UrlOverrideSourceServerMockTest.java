/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config.internal;


import com.xebialabs.restito.server.StubServer;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.ConfigTest;
import io.helidon.config.OverrideSources;
import io.helidon.config.PollingStrategies;
import io.helidon.config.TestingConfigChangeSubscriber;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link UrlOverrideSource} with mocked source.
 */
public class UrlOverrideSourceServerMockTest {
    private static final String NO_WILDCARDS = "";

    private static final String WILDCARDS = "*.*.url = URL1";

    private static final String MULTIPLE_WILDCARDS = "" + (("xxx.bbb.url = URLX\n" + "*.*.url = URL1\n") + "*.bbb.url = URL2");

    private static final String MULTIPLE_WILDCARDS_ANOTHER_ORDERING = "" + (("xxx.bbb.url = URLX\n" + "*.bbb.url = URL2\n") + "*.*.url = URL1");

    private static final String NEW_WILDCARDS = "*.*.url = URL2";

    private StubServer server;

    @Test
    public void testWildcards() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(HEAD), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"));
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlOverrideSourceServerMockTest.WILDCARDS));
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("aaa.bbb.url", "URL0"))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort()))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        ConfigTest.waitForAssert(() -> config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
    }

    @Test
    public void testMultipleMatchingWildcards() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(HEAD), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"));
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlOverrideSourceServerMockTest.MULTIPLE_WILDCARDS));
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("aaa.bbb.url", "URL0"))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort()))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        ConfigTest.waitForAssert(() -> config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
    }

    @Test
    public void testMultipleMatchingWildcardsAnotherOrdering() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(HEAD), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"));
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), header("Last-Modified", "Sat, 10 Jun 2017 10:14:02 GMT"), stringContent(UrlOverrideSourceServerMockTest.MULTIPLE_WILDCARDS_ANOTHER_ORDERING));
        Config config = Config.builder().sources(ConfigSources.create(CollectionsHelper.mapOf("aaa.bbb.url", "URL0"))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort()))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        ConfigTest.waitForAssert(() -> config.get("aaa.bbb.url").asString().get(), Matchers.is("URL2"));
    }

    @Test
    public void testWildcardsChanges() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.WILDCARDS));
        whenHttp(server).match(method(GET), uri("/config")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.CONFIG));
        Config config = Config.builder().sources(ConfigSources.url(UrlOverrideSourceServerMockTest.getUrl("/config", server.getPort()))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort())).pollingStrategy(PollingStrategies.regular(Duration.ofMillis(50)))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
        // register subscriber
        TestingConfigChangeSubscriber subscriber = new TestingConfigChangeSubscriber();
        config.get("aaa.bbb.url").changes().subscribe(subscriber);
        subscriber.request1();
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.NEW_WILDCARDS));
        // wait for event
        Config newConfig = subscriber.getLastOnNext(1000, true);
        // new: key exists
        MatcherAssert.assertThat(newConfig.asString().get(), Matchers.is("URL2"));
    }

    @Test
    public void testWildcardsSupplier() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.WILDCARDS));
        whenHttp(server).match(method(GET), uri("/config")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.CONFIG));
        Config config = Config.builder().sources(ConfigSources.url(UrlOverrideSourceServerMockTest.getUrl("/config", server.getPort()))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort())).pollingStrategy(PollingStrategies.regular(Duration.ofMillis(10)))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.NO_WILDCARDS));
        ConfigTest.waitForAssert(() -> config.get("aaa.bbb.url").asString().supplier().get(), Matchers.is("URL0"));
    }

    @Test
    public void testConfigChangingWithOverrideSource() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.WILDCARDS));
        whenHttp(server).match(method(GET), uri("/config")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.CONFIG));
        Config config = // .addFilter(new OverrideConfigFilter(CollectionsHelper.mapOf(Pattern.compile("\\w+\\.\\w+\\.url"), "URL1")))
        Config.builder().sources(ConfigSources.url(UrlOverrideSourceServerMockTest.getUrl("/config", server.getPort())).pollingStrategy(PollingStrategies.regular(Duration.ofMillis(10)))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort())).pollingStrategy(PollingStrategies.regular(Duration.ofMillis(10)))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
        whenHttp(server).match(method(GET), uri("/config")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.CONFIG2));
        ConfigTest.waitForAssert(() -> config.get("aaa.bbb.url").asString().optionalSupplier().get(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testConfigChangingWithFilters() throws InterruptedException, MalformedURLException {
        whenHttp(server).match(method(GET), uri("/override")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.WILDCARDS));
        whenHttp(server).match(method(GET), uri("/config")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.CONFIG));
        Config config = Config.builder().sources(ConfigSources.url(UrlOverrideSourceServerMockTest.getUrl("/config", server.getPort())).pollingStrategy(PollingStrategies.regular(Duration.ofMillis(10)))).overrides(OverrideSources.url(UrlOverrideSourceServerMockTest.getUrl("/override", server.getPort()))).addFilter(new OverrideConfigFilter(() -> OverrideSource.OverrideData.createFromWildcards(CollectionsHelper.mapOf("*.*.url", "URL1").entrySet().stream().map(( e) -> new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue())).collect(Collectors.toList())).data())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("aaa.bbb.url").asString().get(), Matchers.is("URL1"));
        whenHttp(server).match(method(GET), uri("/config")).then(status(OK_200), contentType(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES), stringContent(UrlOverrideSourceServerMockTest.CONFIG2));
        ConfigTest.waitForAssert(() -> config.get("aaa.bbb.url").asString().optionalSupplier().get(), Matchers.is(Optional.empty()));
    }

    private static final String CONFIG = "aaa.bbb.url = URL0\n";

    private static final String CONFIG2 = "bbb = ahoj\n";
}

