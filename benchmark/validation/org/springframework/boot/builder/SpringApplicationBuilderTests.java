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
package org.springframework.boot.builder;


import WebApplicationType.NONE;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link SpringApplicationBuilder}.
 *
 * @author Dave Syer
 */
public class SpringApplicationBuilderTests {
    private ConfigurableApplicationContext context;

    @Test
    public void profileAndProperties() {
        SpringApplicationBuilder application = new SpringApplicationBuilder().sources(SpringApplicationBuilderTests.ExampleConfig.class).contextClass(StaticApplicationContext.class).profiles("foo").properties("foo=bar");
        this.context = application.run();
        assertThat(this.context).isInstanceOf(StaticApplicationContext.class);
        assertThat(this.context.getEnvironment().getProperty("foo")).isEqualTo("bucket");
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("foo"))).isTrue();
    }

    @Test
    public void propertiesAsMap() {
        SpringApplicationBuilder application = new SpringApplicationBuilder().sources(SpringApplicationBuilderTests.ExampleConfig.class).contextClass(StaticApplicationContext.class).properties(Collections.singletonMap("bar", "foo"));
        this.context = application.run();
        assertThat(this.context.getEnvironment().getProperty("bar")).isEqualTo("foo");
    }

    @Test
    public void propertiesAsProperties() {
        SpringApplicationBuilder application = new SpringApplicationBuilder().sources(SpringApplicationBuilderTests.ExampleConfig.class).contextClass(StaticApplicationContext.class).properties(StringUtils.splitArrayElementsIntoProperties(new String[]{ "bar=foo" }, "="));
        this.context = application.run();
        assertThat(this.context.getEnvironment().getProperty("bar")).isEqualTo("foo");
    }

    @Test
    public void propertiesWithRepeatSeparator() {
        SpringApplicationBuilder application = new SpringApplicationBuilder().sources(SpringApplicationBuilderTests.ExampleConfig.class).contextClass(StaticApplicationContext.class).properties("one=c:\\logging.file.name", "two=a:b", "three:c:\\logging.file.name", "four:a:b");
        this.context = application.run();
        ConfigurableEnvironment environment = this.context.getEnvironment();
        assertThat(environment.getProperty("one")).isEqualTo("c:\\logging.file.name");
        assertThat(environment.getProperty("two")).isEqualTo("a:b");
        assertThat(environment.getProperty("three")).isEqualTo("c:\\logging.file.name");
        assertThat(environment.getProperty("four")).isEqualTo("a:b");
    }

    @Test
    public void specificApplicationContextClass() {
        SpringApplicationBuilder application = new SpringApplicationBuilder().sources(SpringApplicationBuilderTests.ExampleConfig.class).contextClass(StaticApplicationContext.class);
        this.context = application.run();
        assertThat(this.context).isInstanceOf(StaticApplicationContext.class);
    }

    @Test
    public void parentContextCreationThatIsRunDirectly() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ChildConfig.class).contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class);
        application.parent(SpringApplicationBuilderTests.ExampleConfig.class);
        this.context = application.run("foo.bar=baz");
        Mockito.verify(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getApplicationContext()).setParent(ArgumentMatchers.any(ApplicationContext.class));
        assertThat(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getRegisteredShutdownHook()).isFalse();
        assertThat(this.context.getParent().getBean(ApplicationArguments.class).getNonOptionArgs()).contains("foo.bar=baz");
        assertThat(this.context.getBean(ApplicationArguments.class).getNonOptionArgs()).contains("foo.bar=baz");
    }

    @Test
    public void parentContextCreationThatIsBuiltThenRun() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ChildConfig.class).contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class);
        application.parent(SpringApplicationBuilderTests.ExampleConfig.class);
        this.context = application.build("a=alpha").run("b=bravo");
        Mockito.verify(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getApplicationContext()).setParent(ArgumentMatchers.any(ApplicationContext.class));
        assertThat(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getRegisteredShutdownHook()).isFalse();
        assertThat(this.context.getParent().getBean(ApplicationArguments.class).getNonOptionArgs()).contains("a=alpha");
        assertThat(this.context.getBean(ApplicationArguments.class).getNonOptionArgs()).contains("b=bravo");
    }

    @Test
    public void parentContextCreationWithChildShutdown() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ChildConfig.class).contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class).registerShutdownHook(true);
        application.parent(SpringApplicationBuilderTests.ExampleConfig.class);
        this.context = application.run();
        Mockito.verify(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getApplicationContext()).setParent(ArgumentMatchers.any(ApplicationContext.class));
        assertThat(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getRegisteredShutdownHook()).isTrue();
    }

    @Test
    public void contextWithClassLoader() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class);
        ClassLoader classLoader = new URLClassLoader(new URL[0], getClass().getClassLoader());
        application.resourceLoader(new DefaultResourceLoader(classLoader));
        this.context = application.run();
        assertThat(getClassLoader()).isEqualTo(classLoader);
    }

    @Test
    public void parentContextWithClassLoader() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ChildConfig.class).contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class);
        ClassLoader classLoader = new URLClassLoader(new URL[0], getClass().getClassLoader());
        application.resourceLoader(new DefaultResourceLoader(classLoader));
        application.parent(SpringApplicationBuilderTests.ExampleConfig.class);
        this.context = application.run();
        assertThat(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getResourceLoader().getClassLoader()).isEqualTo(classLoader);
    }

    @Test
    public void parentFirstCreation() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).child(SpringApplicationBuilderTests.ChildConfig.class);
        application.contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class);
        this.context = application.run();
        Mockito.verify(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getApplicationContext()).setParent(ArgumentMatchers.any(ApplicationContext.class));
        assertThat(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getRegisteredShutdownHook()).isFalse();
    }

    @Test
    public void parentFirstCreationWithProfileAndDefaultArgs() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).profiles("node").properties("transport=redis").child(SpringApplicationBuilderTests.ChildConfig.class).web(NONE);
        this.context = application.run();
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("node"))).isTrue();
        assertThat(this.context.getEnvironment().getProperty("transport")).isEqualTo("redis");
        assertThat(this.context.getParent().getEnvironment().acceptsProfiles(Profiles.of("node"))).isTrue();
        assertThat(this.context.getParent().getEnvironment().getProperty("transport")).isEqualTo("redis");
        // only defined in node profile
        assertThat(this.context.getEnvironment().getProperty("bar")).isEqualTo("spam");
    }

    @Test
    public void parentFirstWithDifferentProfile() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).profiles("node").properties("transport=redis").child(SpringApplicationBuilderTests.ChildConfig.class).profiles("admin").web(NONE);
        this.context = application.run();
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("node", "admin"))).isTrue();
        assertThat(this.context.getParent().getEnvironment().acceptsProfiles(Profiles.of("admin"))).isFalse();
    }

    @Test
    public void parentWithDifferentProfile() {
        SpringApplicationBuilder shared = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).profiles("node").properties("transport=redis");
        SpringApplicationBuilder application = shared.child(SpringApplicationBuilderTests.ChildConfig.class).profiles("admin").web(NONE);
        shared.profiles("parent");
        this.context = application.run();
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("node", "admin"))).isTrue();
        assertThat(this.context.getParent().getEnvironment().acceptsProfiles(Profiles.of("node", "parent"))).isTrue();
        assertThat(this.context.getParent().getEnvironment().acceptsProfiles(Profiles.of("admin"))).isFalse();
    }

    @Test
    public void parentFirstWithDifferentProfileAndExplicitEnvironment() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).environment(new StandardEnvironment()).profiles("node").properties("transport=redis").child(SpringApplicationBuilderTests.ChildConfig.class).profiles("admin").web(NONE);
        this.context = application.run();
        assertThat(this.context.getEnvironment().acceptsProfiles(Profiles.of("node", "admin"))).isTrue();
        // Now they share an Environment explicitly so there's no way to keep the profiles
        // separate
        assertThat(this.context.getParent().getEnvironment().acceptsProfiles(Profiles.of("admin"))).isTrue();
    }

    @Test
    public void parentContextIdentical() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class);
        application.parent(SpringApplicationBuilderTests.ExampleConfig.class);
        application.contextClass(SpringApplicationBuilderTests.SpyApplicationContext.class);
        this.context = application.run();
        Mockito.verify(((SpringApplicationBuilderTests.SpyApplicationContext) (this.context)).getApplicationContext()).setParent(ArgumentMatchers.any(ApplicationContext.class));
    }

    @Test
    public void initializersCreatedOnce() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).web(NONE);
        this.context = application.run();
        assertThat(application.application().getInitializers()).hasSize(4);
    }

    @Test
    public void initializersCreatedOnceForChild() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).child(SpringApplicationBuilderTests.ChildConfig.class).web(NONE);
        this.context = application.run();
        assertThat(application.application().getInitializers()).hasSize(5);
    }

    @Test
    public void initializersIncludeDefaults() {
        SpringApplicationBuilder application = new SpringApplicationBuilder(SpringApplicationBuilderTests.ExampleConfig.class).web(NONE).initializers((ConfigurableApplicationContext applicationContext) -> {
        });
        this.context = application.run();
        assertThat(application.application().getInitializers()).hasSize(5);
    }

    @Test
    public void sourcesWithBoundSources() {
        SpringApplicationBuilder application = new SpringApplicationBuilder().web(NONE).sources(SpringApplicationBuilderTests.ExampleConfig.class).properties(("spring.main.sources=" + (SpringApplicationBuilderTests.ChildConfig.class.getName())));
        this.context = application.run();
        this.context.getBean(SpringApplicationBuilderTests.ExampleConfig.class);
        this.context.getBean(SpringApplicationBuilderTests.ChildConfig.class);
    }

    @Configuration
    static class ExampleConfig {}

    @Configuration
    static class ChildConfig {}

    public static class SpyApplicationContext extends AnnotationConfigApplicationContext {
        private final ConfigurableApplicationContext applicationContext = Mockito.spy(new AnnotationConfigApplicationContext());

        private ResourceLoader resourceLoader;

        private boolean registeredShutdownHook;

        @Override
        public void setParent(ApplicationContext parent) {
            this.applicationContext.setParent(parent);
        }

        public ConfigurableApplicationContext getApplicationContext() {
            return this.applicationContext;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            super.setResourceLoader(resourceLoader);
            this.resourceLoader = resourceLoader;
        }

        public ResourceLoader getResourceLoader() {
            return this.resourceLoader;
        }

        @Override
        public void registerShutdownHook() {
            super.registerShutdownHook();
            this.registeredShutdownHook = true;
        }

        public boolean getRegisteredShutdownHook() {
            return this.registeredShutdownHook;
        }

        @Override
        public void close() {
            super.close();
            this.applicationContext.close();
        }

        @Override
        public ApplicationContext getParent() {
            return this.applicationContext.getParent();
        }
    }
}

