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
package io.helidon.config;


import CompositeConfigSource.DEFAULT_CHANGES_EXECUTOR_SERVICE;
import ConfigParser.Content;
import ConfigSources.CompositeBuilder;
import PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.config.spi.ConfigContext;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigParser;
import io.helidon.config.spi.ConfigSource;
import io.helidon.config.spi.TestingParsableConfigSource;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link CompositeConfigSource}.
 */
public class CompositeConfigSourceTest {
    private static final int TEST_DELAY_MS = 1;

    @Test
    public void testDescriptionEmpty() throws MalformedURLException {
        ConfigSource configSource = ConfigSources.create().build();
        MatcherAssert.assertThat(configSource.description(), Matchers.is(""));
    }

    @Test
    public void testDescription() throws MalformedURLException {
        ConfigSource configSource = ConfigSources.create().add(ConfigSources.classpath("application.conf")).add(ConfigSources.create(ObjectNode.builder().addValue("prop1", "1").build())).add(ConfigSources.create(CollectionsHelper.mapOf())).add(ConfigSources.create(ObjectNode.builder().addValue("prop1", "2").build())).build();
        MatcherAssert.assertThat(configSource.description(), Matchers.is(("ClasspathConfig[application.conf]->InMemoryConfig[ObjectNode]->MapConfig[map]->InMemoryConfig[ObjectNode" + "]")));
    }

    @Test
    public void testDescriptionWithEnvVarsAndSysProps() throws MalformedURLException {
        ConfigSource configSource = ConfigSources.create().add(ConfigSources.classpath("application.conf")).add(ConfigSources.create(ObjectNode.builder().addValue("prop1", "1").build())).build();
        MatcherAssert.assertThat(configSource.description(), Matchers.is("ClasspathConfig[application.conf]->InMemoryConfig[ObjectNode]"));
    }

    @Test
    public void testBuildEmptyCompositeBuilder() {
        Optional<ObjectNode> rootNode = ConfigSources.create().build().load();
        MatcherAssert.assertThat(rootNode, Matchers.is(Optional.empty()));
    }

    @Test
    public void testBuildWithDefaultStrategy() {
        ObjectNode rootNode = CompositeConfigSourceTest.initBuilder().build().load().get();
        MatcherAssert.assertThat(rootNode.get("prop1"), ValueNodeMatcher.valueNode("source-1"));
        MatcherAssert.assertThat(rootNode.get("prop2"), ValueNodeMatcher.valueNode("source-2"));
        MatcherAssert.assertThat(rootNode.get("prop3"), ValueNodeMatcher.valueNode("source-3"));
    }

    @Test
    public void testBuildWithCustomStrategy() {
        ObjectNode rootNode = CompositeConfigSourceTest.initBuilder().mergingStrategy(new CompositeConfigSourceTest.UseTheLastObjectNodeMergingStrategy()).build().load().get();
        MatcherAssert.assertThat(rootNode.get("prop1"), ValueNodeMatcher.valueNode("source-3"));
        MatcherAssert.assertThat(rootNode.get("prop2"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(rootNode.get("prop3"), ValueNodeMatcher.valueNode("source-3"));
    }

    @Test
    public void testChangesNoChange() throws InterruptedException {
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        // config source AAA
        AtomicReference<ConfigParser.Content<Instant>> contentReferenceAAA = new AtomicReference<>();
        contentReferenceAAA.set(Content.create(new StringReader("ooo=1\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceAAA = TestingParsableConfigSource.builder().content(contentReferenceAAA::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        // config source BBB
        AtomicReference<ConfigParser.Content<Instant>> contentReferenceBBB = new AtomicReference<>();
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=9"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceBBB = TestingParsableConfigSource.builder().content(contentReferenceBBB::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        CompositeConfigSource configSource = ((CompositeConfigSource) (ConfigSources.create().add(configSourceAAA).add(configSourceBBB).build()));
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = configSource.load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(lastObjectNode.get("ooo"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(lastObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(lastObjectNode.get("ppp"), ValueNodeMatcher.valueNode("9"));
        // first subscribe
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber);
        subscriber.request1();
        // polling ticks event
        pollingStrategy.submitEvent();
        // NO changes event
        MatcherAssert.assertThat(subscriber.getLastOnNext(200, false), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
    }

    @Test
    public void testChangesPrimaryLayerValueHasChanged() throws InterruptedException {
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        // config source AAA
        AtomicReference<ConfigParser.Content> contentReferenceAAA = new AtomicReference<>();
        contentReferenceAAA.set(Content.create(new StringReader("ooo=1\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceAAA = TestingParsableConfigSource.builder().content(contentReferenceAAA::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        // config source BBB
        AtomicReference<ConfigParser.Content> contentReferenceBBB = new AtomicReference<>();
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=9"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceBBB = TestingParsableConfigSource.builder().content(contentReferenceBBB::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        CompositeConfigSource configSource = ((CompositeConfigSource) (ConfigSources.create().add(configSourceAAA).add(configSourceBBB).build()));
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = configSource.load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(lastObjectNode.get("ooo"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(lastObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(lastObjectNode.get("ppp"), ValueNodeMatcher.valueNode("9"));
        // first subscribe
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber);
        subscriber.request1();
        // wait for both subscribers
        ConfigTest.waitFor(() -> (pollingStrategy.ticks().getNumberOfSubscribers()) == 2, 1000, 10);
        // change content AAA
        TimeUnit.MILLISECONDS.sleep(CompositeConfigSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        contentReferenceAAA.set(Content.create(new StringReader("ooo=11\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        // NO ticks event -> NO change yet
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        // polling ticks event
        pollingStrategy.submitEvent();
        // wait for event
        ObjectNode newObjectNode = subscriber.getLastOnNext(500, true).get();
        MatcherAssert.assertThat(newObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(newObjectNode.get("ooo"), ValueNodeMatcher.valueNode("11"));
        MatcherAssert.assertThat(newObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(newObjectNode.get("ppp"), ValueNodeMatcher.valueNode("9"));
        // last object-node has changed
        MatcherAssert.assertThat(lastObjectNode, Matchers.not(configSource.lastObjectNode().get()));
    }

    @Test
    public void testChangesOverriddenValueHasChanged() throws InterruptedException {
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        // config source AAA
        AtomicReference<ConfigParser.Content<Instant>> contentReferenceAAA = new AtomicReference<>();
        contentReferenceAAA.set(Content.create(new StringReader("ooo=1\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceAAA = TestingParsableConfigSource.builder().content(contentReferenceAAA::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        // config source BBB
        AtomicReference<ConfigParser.Content<Instant>> contentReferenceBBB = new AtomicReference<>();
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=9"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceBBB = TestingParsableConfigSource.builder().content(contentReferenceBBB::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        CompositeConfigSource configSource = ((CompositeConfigSource) (ConfigSources.create().add(configSourceAAA).add(configSourceBBB).build()));
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = configSource.load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(lastObjectNode.get("ooo"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(lastObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(lastObjectNode.get("ppp"), ValueNodeMatcher.valueNode("9"));
        // first subscribe
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber);
        subscriber.request1();
        // wait for both subscribers
        ConfigTest.waitFor(() -> (pollingStrategy.ticks().getNumberOfSubscribers()) == 2, 1000, 10);
        // change content BBB
        TimeUnit.MILLISECONDS.sleep(CompositeConfigSourceTest.TEST_DELAY_MS);
        contentReferenceBBB.set(Content.create(new StringReader("ooo=22\nppp=9"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        // NO ticks event -> NO change yet
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        // polling ticks event
        pollingStrategy.submitEvent();
        // NO changes event
        MatcherAssert.assertThat(subscriber.getLastOnNext(200, false), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
    }

    @Test
    public void testChangesSecondaryLayerValueHasChanged() throws InterruptedException {
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        // config source AAA
        AtomicReference<ConfigParser.Content> contentReferenceAAA = new AtomicReference<>();
        contentReferenceAAA.set(Content.create(new StringReader("ooo=1\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceAAA = TestingParsableConfigSource.builder().content(contentReferenceAAA::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        // config source BBB
        AtomicReference<ConfigParser.Content> contentReferenceBBB = new AtomicReference<>();
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=9"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceBBB = TestingParsableConfigSource.builder().content(contentReferenceBBB::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        CompositeConfigSource configSource = ((CompositeConfigSource) (ConfigSources.create().add(configSourceAAA).add(configSourceBBB).build()));
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = configSource.load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(lastObjectNode.get("ooo"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(lastObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(lastObjectNode.get("ppp"), ValueNodeMatcher.valueNode("9"));
        // first subscribe
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber);
        subscriber.request1();
        // wait for both subscribers
        ConfigTest.waitFor(() -> (pollingStrategy.ticks().getNumberOfSubscribers()) == 2, 1000, 10);
        // change content BBB
        TimeUnit.MILLISECONDS.sleep(CompositeConfigSourceTest.TEST_DELAY_MS);
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=99"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        // NO ticks event -> NO change yet
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        // polling ticks event
        pollingStrategy.submitEvent();
        // wait for event
        ObjectNode newObjectNode = subscriber.getLastOnNext(333, true).get();
        MatcherAssert.assertThat(newObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(newObjectNode.get("ooo"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(newObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(newObjectNode.get("ppp"), ValueNodeMatcher.valueNode("99"));
        // last object-node has changed
        MatcherAssert.assertThat(lastObjectNode, Matchers.not(configSource.lastObjectNode().get()));
    }

    @Test
    public void testChangesBothLayerHasChangedAtOnce() throws InterruptedException {
        TestingPollingStrategy pollingStrategy = new TestingPollingStrategy();
        ConfigContext context = Mockito.mock(ConfigContext.class);
        // config source AAA
        AtomicReference<ConfigParser.Content> contentReferenceAAA = new AtomicReference<>();
        contentReferenceAAA.set(Content.create(new StringReader("ooo=1\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceAAA = TestingParsableConfigSource.builder().content(contentReferenceAAA::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        // config source BBB
        AtomicReference<ConfigParser.Content> contentReferenceBBB = new AtomicReference<>();
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=9"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        TestingParsableConfigSource configSourceBBB = TestingParsableConfigSource.builder().content(contentReferenceBBB::get).parser(ConfigParsers.properties()).pollingStrategy(pollingStrategy).build();
        CompositeConfigSource configSource = ((CompositeConfigSource) (ConfigSources.create().add(configSourceAAA).add(configSourceBBB).build()));
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = configSource.load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(lastObjectNode.get("ooo"), ValueNodeMatcher.valueNode("1"));
        MatcherAssert.assertThat(lastObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(lastObjectNode.get("ppp"), ValueNodeMatcher.valueNode("9"));
        // first subscribe
        TestingConfigSourceChangeSubscriber subscriber = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber);
        subscriber.request1();
        // wait for both subscribers
        ConfigTest.waitFor(() -> (pollingStrategy.ticks().getNumberOfSubscribers()) == 2, 1000, 10);
        // change content AAA
        TimeUnit.MILLISECONDS.sleep(CompositeConfigSourceTest.TEST_DELAY_MS);// Make sure timestamp changes.

        contentReferenceAAA.set(Content.create(new StringReader("ooo=11\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        // change content BBB
        contentReferenceBBB.set(Content.create(new StringReader("ooo=2\nppp=99"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now())));
        // NO ticks event -> NO change yet
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        // polling ticks event
        pollingStrategy.submitEvent();
        // wait for event
        ObjectNode newObjectNode = subscriber.getLastOnNext(333, true).get();
        MatcherAssert.assertThat(newObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        MatcherAssert.assertThat(newObjectNode.get("ooo"), ValueNodeMatcher.valueNode("11"));
        MatcherAssert.assertThat(newObjectNode.get("rrr"), ValueNodeMatcher.valueNode("5"));
        MatcherAssert.assertThat(newObjectNode.get("ppp"), ValueNodeMatcher.valueNode("99"));
        // last object-node has changed
        MatcherAssert.assertThat(lastObjectNode, Matchers.not(configSource.lastObjectNode().get()));
        // no other change
        subscriber.request1();
        MatcherAssert.assertThat(subscriber.getLastOnNext(333, false), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testChangesRepeatSubscription() throws InterruptedException {
        ConfigContext context = Mockito.mock(ConfigContext.class);
        CompositeConfigSource configSource = ((CompositeConfigSource) (ConfigSources.create(ConfigSources.environmentVariables(), ConfigSources.systemProperties(), TestingParsableConfigSource.builder().content(Content.create(new StringReader("ooo=1\nrrr=5"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES, Optional.of(Instant.now()))).parser(ConfigParsers.properties()).build()).build()));
        configSource.init(context);
        // load from content
        ObjectNode lastObjectNode = configSource.load().get();
        MatcherAssert.assertThat(lastObjectNode, Matchers.is(configSource.lastObjectNode().get()));
        // NO transitive subscribers
        MatcherAssert.assertThat(configSource.compositeConfigSourcesSubscribers(), Matchers.is(Matchers.nullValue()));
        // subscribers
        TestingConfigSourceChangeSubscriber subscriber1 = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber1);
        subscriber1.request1();
        TestingConfigSourceChangeSubscriber subscriber2 = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber2);
        subscriber2.request1();
        TestingConfigSourceChangeSubscriber subscriber3 = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber3);
        subscriber3.request1();
        TestingConfigSourceChangeSubscriber subscriber4 = new TestingConfigSourceChangeSubscriber();
        configSource.changes().subscribe(subscriber4);
        subscriber4.request1();
        // subscribers
        MatcherAssert.assertThat(configSource.compositeConfigSourcesSubscribers(), Matchers.hasSize(3));// env-vars + sys-props + test

        // cancel subscription
        subscriber1.getSubscription().cancel();
        subscriber2.getSubscription().cancel();
        subscriber3.getSubscription().cancel();
        subscriber4.getSubscription().cancel();
        // NO transitive subscribers again
        MatcherAssert.assertThat(configSource.compositeConfigSourcesSubscribers(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testBuilderDefault() {
        ConfigSources.CompositeBuilder builder = ConfigSources.create();
        ConfigSources.CompositeBuilder spyBuilder = Mockito.spy(builder);
        spyBuilder.build();
        // ConfigSources
        // MergingStrategy
        // reloadExecutorService
        // changes debounceTimeout
        // changesMaxBuffer
        Mockito.verify(spyBuilder).createCompositeConfigSource(ArgumentMatchers.argThat(( sources) -> (sources.size()) == 0), ArgumentMatchers.argThat(( strategy) -> strategy instanceof FallbackMergingStrategy), ArgumentMatchers.eq(DEFAULT_CHANGES_EXECUTOR_SERVICE), ArgumentMatchers.eq(Duration.ofMillis(100)), ArgumentMatchers.eq(Flow.defaultBufferSize()));
    }

    @Test
    public void testBuilderCustomChanges() {
        ScheduledExecutorService myExecutor = Mockito.mock(ScheduledExecutorService.class);
        ConfigSources.CompositeBuilder builder = ConfigSources.create().changesExecutor(myExecutor).changesDebounce(Duration.ZERO).changesMaxBuffer(1);
        ConfigSources.CompositeBuilder spyBuilder = Mockito.spy(builder);
        spyBuilder.build();
        // ConfigSources
        // MergingStrategy
        // reloadExecutorService
        // changes debounceTimeout
        // changesMaxBuffer
        Mockito.verify(spyBuilder).createCompositeConfigSource(ArgumentMatchers.argThat(( sources) -> (sources.size()) == 0), ArgumentMatchers.argThat(( strategy) -> strategy instanceof FallbackMergingStrategy), ArgumentMatchers.eq(myExecutor), ArgumentMatchers.eq(Duration.ZERO), ArgumentMatchers.eq(1));
    }

    @Test
    public void testBuilderAddSources() {
        ObjectNode rootNode = CompositeConfigSourceTest.initBuilder().add(ConfigSources.create(ObjectNode.builder().addValue("prop1", "source-4").addValue("prop4", "source-4").build())).add(ConfigSources.create(ObjectNode.builder().addValue("prop1", "source-5").addValue("prop5", "source-5").build())).build().load().get();
        MatcherAssert.assertThat(rootNode.get("prop1"), ValueNodeMatcher.valueNode("source-1"));
        MatcherAssert.assertThat(rootNode.get("prop2"), ValueNodeMatcher.valueNode("source-2"));
        MatcherAssert.assertThat(rootNode.get("prop3"), ValueNodeMatcher.valueNode("source-3"));
        MatcherAssert.assertThat(rootNode.get("prop4"), ValueNodeMatcher.valueNode("source-4"));
        MatcherAssert.assertThat(rootNode.get("prop5"), ValueNodeMatcher.valueNode("source-5"));
    }

    private static class UseTheLastObjectNodeMergingStrategy implements ConfigSources.MergingStrategy {
        @Override
        public ObjectNode merge(List<ObjectNode> rootNodes) {
            return rootNodes.get(((rootNodes.size()) - 1));
        }
    }
}

