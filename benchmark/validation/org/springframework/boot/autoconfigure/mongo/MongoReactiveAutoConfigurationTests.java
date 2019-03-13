/**
 * Copyright 2012-2018 the original author or authors.
 *
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
 */
package org.springframework.boot.autoconfigure.mongo;


import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.connection.AsynchronousSocketChannelStreamFactoryFactory;
import com.mongodb.connection.StreamFactory;
import com.mongodb.connection.StreamFactoryFactory;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static SimpleCustomizerConfig.streamFactoryFactory;


/**
 * Tests for {@link MongoReactiveAutoConfiguration}.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 */
public class MongoReactiveAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MongoReactiveAutoConfiguration.class));

    @Test
    public void clientExists() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void optionsAdded() {
        this.contextRunner.withPropertyValues("spring.data.mongodb.host:localhost").withUserConfiguration(MongoReactiveAutoConfigurationTests.OptionsConfig.class).run(( context) -> assertThat(getSettings(context).getSocketSettings().getReadTimeout(TimeUnit.SECONDS)).isEqualTo(300));
    }

    @Test
    public void optionsAddedButNoHost() {
        this.contextRunner.withPropertyValues("spring.data.mongodb.uri:mongodb://localhost/test").withUserConfiguration(MongoReactiveAutoConfigurationTests.OptionsConfig.class).run(( context) -> assertThat(getSettings(context).getReadPreference()).isEqualTo(ReadPreference.nearest()));
    }

    @Test
    public void optionsSslConfig() {
        this.contextRunner.withPropertyValues("spring.data.mongodb.uri:mongodb://localhost/test").withUserConfiguration(MongoReactiveAutoConfigurationTests.SslOptionsConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            MongoClientSettings settings = getSettings(context);
            assertThat(settings.getApplicationName()).isEqualTo("test-config");
            assertThat(settings.getStreamFactoryFactory()).isSameAs(context.getBean("myStreamFactoryFactory"));
        });
    }

    @Test
    public void nettyStreamFactoryFactoryIsConfiguredAutomatically() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(getSettings(context).getStreamFactoryFactory()).isInstanceOf(.class);
        });
    }

    @Test
    public void customizerOverridesAutoConfig() {
        this.contextRunner.withPropertyValues("spring.data.mongodb.uri:mongodb://localhost/test?appname=auto-config").withUserConfiguration(MongoReactiveAutoConfigurationTests.SimpleCustomizerConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            MongoClientSettings settings = getSettings(context);
            assertThat(settings.getApplicationName()).isEqualTo("overridden-name");
            assertThat(settings.getStreamFactoryFactory()).isEqualTo(SimpleCustomizerConfig.streamFactoryFactory);
        });
    }

    @Configuration
    static class OptionsConfig {
        @Bean
        public MongoClientSettings mongoClientSettings() {
            return MongoClientSettings.builder().readPreference(ReadPreference.nearest()).applyToSocketSettings(( socket) -> socket.readTimeout(300, TimeUnit.SECONDS)).build();
        }
    }

    @Configuration
    static class SslOptionsConfig {
        @Bean
        public MongoClientSettings mongoClientSettings() {
            return MongoClientSettings.builder().applicationName("test-config").streamFactoryFactory(myStreamFactoryFactory()).build();
        }

        @Bean
        public StreamFactoryFactory myStreamFactoryFactory() {
            StreamFactoryFactory streamFactoryFactory = Mockito.mock(StreamFactoryFactory.class);
            BDDMockito.given(streamFactoryFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(Mockito.mock(StreamFactory.class));
            return streamFactoryFactory;
        }
    }

    @Configuration
    static class SimpleCustomizerConfig {
        private static final StreamFactoryFactory streamFactoryFactory = new AsynchronousSocketChannelStreamFactoryFactory.Builder().build();

        @Bean
        public MongoClientSettingsBuilderCustomizer customizer() {
            return ( clientSettingsBuilder) -> clientSettingsBuilder.applicationName("overridden-name").streamFactoryFactory(SimpleCustomizerConfig.streamFactoryFactory);
        }
    }
}

