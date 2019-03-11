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


import Config.Type.LIST;
import Config.Type.OBJECT;
import ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.TestingConfigSource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests related to {@link ConfigValue#optionalSupplier()} ()} and other {@code *Supplier()} methods.
 */
public class ConfigSupplierTest {
    private static final int TEST_DELAY_MS = 1;

    @Test
    public void testSupplierFromMissingToObjectNode() throws InterruptedException {
        // config source
        TestingConfigSource configSource = TestingConfigSource.builder().build();
        // config
        Config config = Config.builder().sources(configSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        Supplier<Optional<Config>> supplier = config.get("key1").asNode().optionalSupplier();
        // change config source
        TimeUnit.MILLISECONDS.sleep(ConfigSupplierTest.TEST_DELAY_MS);// Make sure time changes to trigger notification.

        configSource.changeLoadedObjectNode(ObjectNode.builder().addObject("key1", ObjectNode.builder().addValue("sub1", "string value").build()).build());
        // new: key exists
        ConfigTest.waitForAssert(() -> supplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> supplier.get().get().type(), Matchers.is(OBJECT));
        ConfigTest.waitForAssert(() -> supplier.get().get().get("sub1").asString(), Matchers.is(ConfigValues.simpleValue("string value")));
    }

    @Test
    public void testSupplierSubscribeOnLeafNode() throws InterruptedException {
        // config source
        TestingConfigSource configSource = TestingConfigSource.builder().build();
        // config
        Config config = Config.builder().sources(configSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        Supplier<Optional<String>> supplier = config.get("key1.sub1").asString().optionalSupplier();
        // change config source
        TimeUnit.MILLISECONDS.sleep(ConfigSupplierTest.TEST_DELAY_MS);// Make sure time changes to trigger notification.

        configSource.changeLoadedObjectNode(ObjectNode.builder().addObject("key1", ObjectNode.builder().addValue("sub1", "string value").build()).build());
        ConfigTest.waitForAssert(() -> supplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> supplier.get().get(), Matchers.is("string value"));
        // change config source
        TimeUnit.MILLISECONDS.sleep(ConfigSupplierTest.TEST_DELAY_MS);// Make sure time changes to trigger notification.

        configSource.changeLoadedObjectNode(ObjectNode.builder().addObject("key1", ObjectNode.builder().addValue("sub1", "new value").build()).build());
        // new: key exists
        ConfigTest.waitForAssert(() -> supplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> supplier.get().get(), Matchers.is("new value"));
    }

    @Test
    public void testSupplierSubscribeOnParentNode() throws InterruptedException {
        // config source
        TestingConfigSource configSource = TestingConfigSource.builder().objectNode(ObjectNode.builder().addValue("key-1-1.key-2-1", "item 1").build()).build();
        // config
        Config config = Config.builder().sources(configSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        // register subscriber1
        Supplier<Optional<Config>> configSupplier = config.get("key-1-1").asNode().optionalSupplier();
        // register subscriber2 on DETACHED leaf
        Supplier<Optional<Config>> detachedConfigSupplier = config.get("key-1-1").detach().asNode().optionalSupplier();
        // wait for event
        MatcherAssert.assertThat(configSupplier.get().isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(configSupplier.get().get().key().toString(), Matchers.is("key-1-1"));
        MatcherAssert.assertThat(detachedConfigSupplier.get().isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(detachedConfigSupplier.get().get().key().toString(), Matchers.is(""));
        // change config source
        TimeUnit.MILLISECONDS.sleep(ConfigSupplierTest.TEST_DELAY_MS);// Make sure time changes to trigger notification.

        configSource.changeLoadedObjectNode(ObjectNode.builder().addValue("key-1-1.key-2-1", "NEW item 1").build());
        // wait for event
        ConfigTest.waitForAssert(() -> configSupplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> configSupplier.get().get().get("key-2-1").asString(), Matchers.is(ConfigValues.simpleValue("NEW item 1")));
        ConfigTest.waitForAssert(() -> detachedConfigSupplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> detachedConfigSupplier.get().get().get("key-2-1").asString(), Matchers.is(ConfigValues.simpleValue("NEW item 1")));
    }

    @Test
    public void testSupplierSubscribeOnRootNode() throws InterruptedException {
        // config source
        TestingConfigSource configSource = TestingConfigSource.builder().objectNode(ObjectNode.builder().addValue("key-1-1.key-2-1", "item 1").build()).build();
        // config
        Config config = Config.builder().sources(configSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        Supplier<Optional<Config>> optionalSupplier = config.asNode().optionalSupplier();
        // change config source
        TimeUnit.MILLISECONDS.sleep(ConfigSupplierTest.TEST_DELAY_MS);// Make sure time changes to trigger notification.

        configSource.changeLoadedObjectNode(ObjectNode.builder().addValue("key-1-1.key-2-1", "NEW item 1").build());
        // wait for event
        ConfigTest.waitForAssert(() -> optionalSupplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> optionalSupplier.get().get().key().toString(), Matchers.is(""));
        ConfigTest.waitForAssert(() -> optionalSupplier.get().get().get("key-1-1.key-2-1").asString(), Matchers.is(ConfigValues.simpleValue("NEW item 1")));
    }

    @Test
    public void testSupplierFromMissingToListNode() throws InterruptedException {
        // config source
        TestingConfigSource configSource = TestingConfigSource.builder().build();
        // config
        Config config = Config.builder().sources(configSource).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        // key does not exist
        MatcherAssert.assertThat(config.get("key1").exists(), Matchers.is(false));
        // register subscriber
        Supplier<Optional<Config>> nodeSupplier = config.get("key1").asNode().optionalSupplier();
        // change config source
        TimeUnit.MILLISECONDS.sleep(ConfigSupplierTest.TEST_DELAY_MS);// Make sure time changes to trigger notification.

        configSource.changeLoadedObjectNode(ObjectNode.builder().addList("key1", ListNode.builder().addValue("item 1").addValue("item 2").build()).build());
        // new: key exists
        ConfigTest.waitForAssert(() -> nodeSupplier.get().isPresent(), Matchers.is(true));
        ConfigTest.waitForAssert(() -> nodeSupplier.get().get().type(), Matchers.is(LIST));
        ConfigTest.waitForAssert(() -> nodeSupplier.get().get().asList(.class).get(), Matchers.contains("item 1", "item 2"));
    }
}

