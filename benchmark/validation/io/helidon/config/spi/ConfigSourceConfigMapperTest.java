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


import com.xebialabs.restito.server.StubServer;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.config.internal.ClasspathConfigSource;
import io.helidon.config.internal.DirectoryConfigSource;
import io.helidon.config.internal.FileConfigSource;
import io.helidon.config.internal.MapConfigSource;
import io.helidon.config.internal.PrefixedConfigSource;
import io.helidon.config.internal.UrlConfigSource;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.test.infra.RestoreSystemPropertiesExt;
import io.helidon.config.test.infra.TemporaryFolderExt;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;


/**
 * Tests {@link ConfigSourceConfigMapper}.
 */
public class ConfigSourceConfigMapperTest {
    private static final String TEST_SYS_PROP_NAME = "this_is_my_property-ConfigSourceConfigMapperTest";

    private static final String TEST_SYS_PROP_VALUE = "This Is My SYS PROPS Value.";

    private static final String TEST_ENV_VAR_NAME = "CONFIG_SOURCE_TEST_PROPERTY";

    private static final String TEST_ENV_VAR_VALUE = "This Is My ENV VARS Value.";

    private static final String RELATIVE_PATH_TO_RESOURCE = "/src/test/resources/";

    @RegisterExtension
    public TemporaryFolderExt folder = TemporaryFolderExt.build();

    @Test
    @ExtendWith(RestoreSystemPropertiesExt.class)
    public void testSystemProperties() {
        System.setProperty(ConfigSourceConfigMapperTest.TEST_SYS_PROP_NAME, ConfigSourceConfigMapperTest.TEST_SYS_PROP_VALUE);
        Config metaConfig = ConfigSourceConfigMapperTest.justFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "system-properties").build()));
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(MapConfigSource.class)));
        Config config = ConfigSourceConfigMapperTest.justFrom(source);
        MatcherAssert.assertThat(config.get(ConfigSourceConfigMapperTest.TEST_SYS_PROP_NAME).asString().get(), CoreMatchers.is(ConfigSourceConfigMapperTest.TEST_SYS_PROP_VALUE));
    }

    @Test
    public void testEnvironmentVariables() {
        Config metaConfig = ConfigSourceConfigMapperTest.justFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "environment-variables").build()));
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(MapConfigSource.class)));
        Config config = ConfigSourceConfigMapperTest.justFrom(source);
        MatcherAssert.assertThat(config.get(ConfigSourceConfigMapperTest.TEST_ENV_VAR_NAME).asString().get(), CoreMatchers.is(ConfigSourceConfigMapperTest.TEST_ENV_VAR_VALUE));
    }

    @Test
    public void testClasspath() {
        Config metaConfig = ConfigSourceConfigMapperTest.builderFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "classpath").addObject("properties", ObjectNode.builder().addValue("resource", "io/helidon/config/application.properties").build()).build())).build();
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(ClasspathConfigSource.class)));
        Config config = ConfigSourceConfigMapperTest.justFrom(source);
        MatcherAssert.assertThat(config.get("app.page-size").asInt().get(), CoreMatchers.is(10));
    }

    @Test
    public void testFile() {
        Config metaConfig = ConfigSourceConfigMapperTest.builderFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "file").addObject("properties", ObjectNode.builder().addValue("path", ((ConfigSourceConfigMapperTest.getDir()) + "io/helidon/config/application.properties")).build()).build())).build();
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(FileConfigSource.class)));
        Config config = ConfigSourceConfigMapperTest.justFrom(source);
        MatcherAssert.assertThat(config.get("app.page-size").asInt().get(), CoreMatchers.is(10));
    }

    @Test
    public void testDirectory() throws IOException {
        File folder = this.folder.newFolder();
        Files.write(Files.createFile(new File(folder, "username").toPath()), "libor".getBytes());
        Files.write(Files.createFile(new File(folder, "password").toPath()), "^ery$ecretP&ssword".getBytes());
        Config metaConfig = ConfigSourceConfigMapperTest.builderFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "directory").addObject("properties", ObjectNode.builder().addValue("path", folder.getAbsolutePath()).build()).build())).build();
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(DirectoryConfigSource.class)));
        Config config = ConfigSourceConfigMapperTest.justFrom(source);
        MatcherAssert.assertThat(config.get("username").asString().get(), CoreMatchers.is("libor"));
        MatcherAssert.assertThat(config.get("password").asString().get(), CoreMatchers.is("^ery$ecretP&ssword"));
    }

    @Test
    public void testUrl() {
        StubServer server = new StubServer().run();
        try {
            whenHttp(server).match(get("/application.properties")).then(status(OK_200), stringContent("greeting = Hello"));
            Config metaConfig = ConfigSourceConfigMapperTest.builderFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "url").addObject("properties", ObjectNode.builder().addValue("url", String.format("http://127.0.0.1:%d/application.properties", server.getPort())).build()).build())).build();
            ConfigSource source = metaConfig.as(ConfigSource::create).get();
            MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(UrlConfigSource.class)));
            Config config = ConfigSourceConfigMapperTest.justFrom(source);
            MatcherAssert.assertThat(config.get("greeting").asString().get(), CoreMatchers.is("Hello"));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testPrefixed() {
        Config metaConfig = ConfigSourceConfigMapperTest.builderFrom(ConfigSources.create(ObjectNode.builder().addValue("type", "prefixed").addObject("properties", ObjectNode.builder().addValue("key", "this.is.prefix.key").addValue("type", "classpath").addObject("properties", ObjectNode.builder().addValue("resource", "io/helidon/config/application.properties").build()).build()).build())).build();
        ConfigSource source = metaConfig.as(ConfigSource::create).get();
        MatcherAssert.assertThat(source, CoreMatchers.is(Matchers.instanceOf(PrefixedConfigSource.class)));
        Config config = ConfigSourceConfigMapperTest.justFrom(source);
        MatcherAssert.assertThat(config.get("this.is.prefix.key.app.page-size").asInt().get(), CoreMatchers.is(10));
    }

    /**
     * Testing implementation of config source endpoint.
     */
    public static class MyEndpoint {
        private final String myProp1;

        private final int myProp2;

        public MyEndpoint(String myProp1, int myProp2) {
            this.myProp1 = myProp1;
            this.myProp2 = myProp2;
        }

        public String getMyProp1() {
            return myProp1;
        }

        public int getMyProp2() {
            return myProp2;
        }

        @Override
        public String toString() {
            return ((((("MyEndpoint{" + "myProp1='") + (myProp1)) + '\'') + ", myProp2=") + (myProp2)) + '}';
        }
    }
}

