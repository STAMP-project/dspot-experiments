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
package io.helidon.config.spi;


import AbstractSource.Builder.DEFAULT_CHANGES_EXECUTOR;
import ConfigParser.Content;
import PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.config.Config;
import io.helidon.config.ConfigException;
import io.helidon.config.ConfigHelperTest;
import io.helidon.config.ConfigSources;
import io.helidon.config.PollingStrategies;
import io.helidon.config.TestingConfigSourceChangeSubscriber;
import io.helidon.config.TestingPollingStrategy;
import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;


/**
 * Tests {@link AbstractParsableConfigSource}.
 */
public class AbstractParsableConfigSourceTest {
    private static final String TEST_MEDIA_TYPE = "my/media/type";

    private static final String TEST_KEY = "test-key";

    private static final String TEST_CONFIG = (AbstractParsableConfigSourceTest.TEST_KEY) + " = test-value";

    private static final int TEST_DELAY_MS = 1;

    @Test
    public void testBuilderContentNotExists() {
        TestingParsableConfigSource source = TestingParsableConfigSource.builder().build();
        MatcherAssert.assertThat(isMandatory(), Matchers.is(true));
        MatcherAssert.assertThat(mediaType(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(parser(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testBuilderContentExists() throws IOException {
        TestingParsableConfigSource source = TestingParsableConfigSource.builder().content(mockContent()).build();
        MatcherAssert.assertThat(isMandatory(), Matchers.is(true));
        MatcherAssert.assertThat(mediaType(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(parser(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(mediaType(), Matchers.is(AbstractParsableConfigSourceTest.TEST_MEDIA_TYPE));
        MatcherAssert.assertThat(ConfigHelperTest.readerToString(content().asReadable()), Matchers.is(AbstractParsableConfigSourceTest.TEST_CONFIG));
    }

    @Test
    public void testFromReadable() throws IOException {
        AbstractParsableConfigSource source = ((AbstractParsableConfigSource) (ConfigSources.create(new StringReader(AbstractParsableConfigSourceTest.TEST_CONFIG), AbstractParsableConfigSourceTest.TEST_MEDIA_TYPE)));
        MatcherAssert.assertThat(source.isMandatory(), Matchers.is(true));
        MatcherAssert.assertThat(source.mediaType(), Matchers.is(AbstractParsableConfigSourceTest.TEST_MEDIA_TYPE));
        MatcherAssert.assertThat(source.parser(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(mediaType(), Matchers.is(AbstractParsableConfigSourceTest.TEST_MEDIA_TYPE));
        MatcherAssert.assertThat(ConfigHelperTest.readerToString(source.content().asReadable()), Matchers.is(AbstractParsableConfigSourceTest.TEST_CONFIG));
    }

    @Test
    public void testCompleteBuilder() {
        ConfigParser parser = Mockito.mock(ConfigParser.class);
        TestingParsableConfigSource source = optional().build();
        MatcherAssert.assertThat(isMandatory(), Matchers.is(false));
        MatcherAssert.assertThat(mediaType(), Matchers.is(AbstractParsableConfigSourceTest.TEST_MEDIA_TYPE));
        MatcherAssert.assertThat(parser(), Matchers.is(parser));
    }

    @Test
    public void testMandatoryParserSetContentExistsParserRegistered() {
        ConfigParser.Content content = mockContent();
        ConfigParser registeredParser = mockParser("registered");
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(content.mediaType())).thenReturn(Optional.of(registeredParser));
        ConfigParser setParser = mockParser("set");
        TestingParsableConfigSource source = TestingParsableConfigSource.builder().content(content).parser(setParser).build();
        source.init(context);
        MatcherAssert.assertThat(load().get().get(AbstractParsableConfigSourceTest.TEST_KEY), ValueNodeMatcher.valueNode("set"));
    }

    @Test
    public void testMandatoryParserSetContentNotExistsParserRegistered() {
        ConfigParser.Content content = mockContent();
        ConfigParser registeredParser = mockParser("registered");
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(content.mediaType())).thenReturn(Optional.of(registeredParser));
        ConfigParser setParser = mockParser("set");
        TestingParsableConfigSource source = TestingParsableConfigSource.builder().parser(setParser).build();
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            source.init(context);
            load();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("Cannot load data from mandatory source", "TestingParsableConfig[parsable-test]")));
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));// Cannot find suitable parser for 'my/media/type' media type.

    }

    @Test
    public void testMandatoryParserNotSetContentExistsParserRegistered() {
        ConfigParser.Content content = mockContent();
        ConfigParser registeredParser = mockParser("registered");
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(content.mediaType())).thenReturn(Optional.of(registeredParser));
        TestingParsableConfigSource source = TestingParsableConfigSource.builder().content(content).build();
        source.init(context);
        MatcherAssert.assertThat(load().get().get(AbstractParsableConfigSourceTest.TEST_KEY), ValueNodeMatcher.valueNode("registered"));
    }

    @Test
    public void testMandatoryParserNotSetContentExistsParserNotRegistered() {
        ConfigParser.Content content = mockContent();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(content.mediaType())).thenReturn(Optional.empty());
        TestingParsableConfigSource source = TestingParsableConfigSource.builder().content(content).build();
        ConfigException ex = Assertions.assertThrows(ConfigException.class, () -> {
            source.init(context);
            load();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("Cannot load data from mandatory source", "TestingParsableConfig[parsable-test]")));
        MatcherAssert.assertThat(ex.getCause(), Matchers.instanceOf(ConfigException.class));// Cannot find suitable parser for 'my/media/type' media type.

    }

    @Test
    public void testOptionalParserNotSetContentExistsParserNotRegistered() {
        ConfigParser.Content content = mockContent();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(content.mediaType())).thenReturn(Optional.empty());
        TestingParsableConfigSource source = optional().build();
        source.init(context);
        MatcherAssert.assertThat(load(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testOptionalParserSetContentNotExistsParserNotRegistered() {
        ConfigParser.Content content = mockContent();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(content.mediaType())).thenReturn(Optional.empty());
        ConfigParser setParser = mockParser("set");
        TestingParsableConfigSource source = optional().parser(setParser).build();
        source.init(context);
        MatcherAssert.assertThat(load(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testFormatDescriptionOptionalNoParams() {
        TestingParsableConfigSource configSource = optional().build();
        MatcherAssert.assertThat(formatDescription(""), Matchers.is("TestingParsableConfig[]?"));
    }

    @Test
    public void testFormatDescriptionOptionalWithParams() {
        TestingParsableConfigSource configSource = optional().build();
        MatcherAssert.assertThat(formatDescription("PA,RAMS"), Matchers.is("TestingParsableConfig[PA,RAMS]?"));
    }

    @Test
    public void testFormatDescriptionMandatoryNoParams() {
        TestingParsableConfigSource configSource = TestingParsableConfigSource.builder().build();
        MatcherAssert.assertThat(formatDescription(""), Matchers.is("TestingParsableConfig[]"));
    }

    @Test
    public void testFormatDescriptionMandatoryWithParams() {
        TestingParsableConfigSource configSource = TestingParsableConfigSource.builder().build();
        MatcherAssert.assertThat(formatDescription("PA,RAMS"), Matchers.is("TestingParsableConfig[PA,RAMS]"));
    }

    @Test
    public void testChangesFromNoContentToStillNoContent() throws InterruptedException {
        AtomicReference<ConfigParser.Content> contentReference = new AtomicReference<>();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        TestingParsableConfigSource configSource = parser(io.helidon.config.ConfigParsers.properties()).pollingStrategy(() -> pollingStrategy).build();
        configSource.init(context);
        // load from 'no' content
        MatcherAssert.assertThat(load(), Matchers.is(Optional.empty()));
        // last data is empty
        MatcherAssert.assertThat(lastData(), Matchers.is(Optional.empty()));
        // listen on changes
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        changes().subscribe(subscriber);
        subscriber.request1();
        // polling ticks event
        pollingStrategy.submitEvent();
        // NO changes event
        MatcherAssert.assertThat(subscriber.getLastOnNext(200, false), Matchers.is(Matchers.nullValue()));
        // objectNode still empty
        MatcherAssert.assertThat(lastData(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testChangesFromNoContentToNewOne() throws InterruptedException {
        AtomicReference<ConfigParser.Content> contentReference = new AtomicReference<>();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        TestingParsableConfigSource configSource = parser(io.helidon.config.ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        configSource.init(context);
        // load 'no' content
        MatcherAssert.assertThat(load(), Matchers.is(Optional.empty()));
        // last data is empty
        MatcherAssert.assertThat(lastData(), Matchers.is(Optional.empty()));
        // listen on changes
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        changes().subscribe(subscriber);
        subscriber.request1();
        // change content
        TimeUnit.MILLISECONDS.sleep(AbstractParsableConfigSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        Optional<Instant> contentTimestamp = Optional.of(Instant.now());
        contentReference.set(Content.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, contentTimestamp));
        // polling ticks event
        pollingStrategy.submitEvent();
        // wait for event
        MatcherAssert.assertThat(subscriber.getLastOnNext(150, true).get(), Matchers.is(lastData().get().data().get()));
        // objectNode
        MatcherAssert.assertThat(lastData().get().data().get().get("aaa"), ValueNodeMatcher.valueNode("bbb"));
        // content timestamp
        MatcherAssert.assertThat(lastData().get().stamp(), Matchers.is(contentTimestamp));
    }

    @Test
    public void testChangesFromContentToSameContent() throws InterruptedException {
        AtomicReference<ConfigParser.Content<Instant>> contentReference = new AtomicReference<>();
        // set content
        Optional<Instant> contentTimestamp = Optional.of(Instant.now());
        contentReference.set(Content.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, contentTimestamp));
        ConfigContext context = Mockito.mock(ConfigContext.class);
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        TestingParsableConfigSource configSource = parser(io.helidon.config.ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(lastData().get().data().get()));
        // objectNode
        MatcherAssert.assertThat(lastData().get().data().get().get("aaa"), ValueNodeMatcher.valueNode("bbb"));
        // content timestamp
        MatcherAssert.assertThat(lastData().get().stamp(), Matchers.is(contentTimestamp));
        // listen on changes
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        changes().subscribe(subscriber);
        subscriber.request1();
        // reset content
        TimeUnit.MILLISECONDS.sleep(AbstractParsableConfigSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        contentTimestamp = Optional.of(Instant.now());
        contentReference.set(Content.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, contentTimestamp));
        // polling ticks event
        pollingStrategy.submitEvent();
        // NO changes event
        MatcherAssert.assertThat(subscriber.getLastOnNext(200, false), Matchers.is(Matchers.nullValue()));
        // objectNode still null
        MatcherAssert.assertThat(lastData().get().data().get(), Matchers.is(lastObjectNode));
        // timestamp has not changed
        MatcherAssert.assertThat(lastData().get().stamp(), Matchers.is(contentTimestamp));
    }

    @Test
    public void testChangesFromContentToNoContent() throws InterruptedException {
        AtomicReference<ConfigParser.Content<Instant>> contentReference = new AtomicReference<>();
        // set content
        Optional<Instant> contentTimestamp = Optional.of(Instant.now());
        contentReference.set(Content.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, contentTimestamp));
        ConfigContext context = Mockito.mock(ConfigContext.class);
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        TestingParsableConfigSource configSource = parser(io.helidon.config.ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        configSource.init(context);
        // load from content
        MatcherAssert.assertThat(load().get(), Matchers.is(lastData().get().data().get()));
        // objectNode
        MatcherAssert.assertThat(lastData().get().data().get().get("aaa"), ValueNodeMatcher.valueNode("bbb"));
        // content timestamp
        MatcherAssert.assertThat(lastData().get().stamp(), Matchers.is(contentTimestamp));
        // listen on changes
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        changes().subscribe(subscriber);
        subscriber.request1();
        // remove content
        TimeUnit.MILLISECONDS.sleep(AbstractParsableConfigSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        contentReference.set(null);
        // polling ticks event
        pollingStrategy.submitEvent();
        // wait for event
        MatcherAssert.assertThat(subscriber.getLastOnNext(5000, true), Matchers.is(Optional.empty()));
        // last data is empty
        MatcherAssert.assertThat(lastData().get().data(), Matchers.is(Optional.empty()));
    }

    @Test
    public void testChangesFromContentToDifferentOne() throws InterruptedException {
        AtomicReference<ConfigParser.Content> contentReference = new AtomicReference<>();
        // set content
        Optional<Instant> contentTimestamp = Optional.of(Instant.now());
        contentReference.set(Content.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, contentTimestamp));
        ConfigContext context = Mockito.mock(ConfigContext.class);
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        TestingParsableConfigSource configSource = parser(io.helidon.config.ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(lastData().get().data().get()));
        // objectNode
        MatcherAssert.assertThat(lastData().get().data().get().get("aaa"), ValueNodeMatcher.valueNode("bbb"));
        // content timestamp
        MatcherAssert.assertThat(lastData().get().stamp(), Matchers.is(contentTimestamp));
        // listen on changes
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        changes().subscribe(subscriber);
        subscriber.request1();
        // reset content
        TimeUnit.MILLISECONDS.sleep(AbstractParsableConfigSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        contentTimestamp = Optional.of(Instant.now());
        contentReference.set(Content.create(new StringReader("aaa=ccc"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, contentTimestamp));
        // polling ticks event
        pollingStrategy.submitEvent();
        // wait for event
        MatcherAssert.assertThat(subscriber.getLastOnNext(150, true).get(), Matchers.is(lastData().get().data().get()));
        // objectNode
        MatcherAssert.assertThat(lastData().get().data().get().get("aaa"), ValueNodeMatcher.valueNode("ccc"));
        // content timestamp
        MatcherAssert.assertThat(lastData().get().stamp(), Matchers.is(contentTimestamp));
    }

    @Test
    public void testChangesTransitivePollingSubscription() throws InterruptedException {
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        AtomicReference<ConfigParser.Content> contentReference = new AtomicReference<>();
        TestingParsableConfigSource configSource = parser(io.helidon.config.ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        // config source is not subscribed on polling strategy yet
        MatcherAssert.assertThat(configSource.isSubscribePollingStrategyInvoked(), Matchers.is(false));
        // first subscribe
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        changes().subscribe(subscriber);
        // config source is not subscribed on polling strategy yet
        MatcherAssert.assertThat(configSource.isSubscribePollingStrategyInvoked(), Matchers.is(false));
        // first request
        subscriber.getSubscription().request(1);
        MatcherAssert.assertThat(configSource.isSubscribePollingStrategyInvoked(), Matchers.is(true));
        MatcherAssert.assertThat(configSource.isCancelPollingStrategyInvoked(), Matchers.is(false));
        // cancel subscription
        subscriber.getSubscription().cancel();
        TimeUnit.MILLISECONDS.sleep(150);
        // config source is no longer subscribed on polling strategy
        MatcherAssert.assertThat(configSource.isCancelPollingStrategyInvoked(), Matchers.is(true));
    }

    @Test
    public void testBuilderDefault() {
        TestingParsableConfigSource.Builder builder = TestingParsableConfigSource.builder();
        MatcherAssert.assertThat(builder.pollingStrategy(), Matchers.is(PollingStrategies.nop()));
        MatcherAssert.assertThat(builder.changesExecutor(), Matchers.is(DEFAULT_CHANGES_EXECUTOR));
        MatcherAssert.assertThat(builder.changesMaxBuffer(), Matchers.is(Flow.defaultBufferSize()));
    }

    @Test
    public void testBuilderCustomChanges() {
        Executor myExecutor = Runnable::run;
        TestingParsableConfigSource.Builder builder = changesExecutor(myExecutor).changesMaxBuffer(1);
        MatcherAssert.assertThat(builder.changesExecutor(), Matchers.is(myExecutor));
        MatcherAssert.assertThat(builder.changesMaxBuffer(), Matchers.is(1));
    }

    @Test
    public void testInitAll() {
        TestingParsableConfigSource.TestingBuilder builder = TestingParsableConfigSource.builder().init(Config.create(ConfigSources.create(CollectionsHelper.mapOf("media-type", "application/x-yaml"))));
        // media-type
        MatcherAssert.assertThat(mediaType(), Matchers.is("application/x-yaml"));
    }

    @Test
    public void testInitNothing() {
        TestingParsableConfigSource.TestingBuilder builder = TestingParsableConfigSource.builder().init(Config.empty());
        // media-type
        MatcherAssert.assertThat(mediaType(), Matchers.is(Matchers.nullValue()));
    }
}

