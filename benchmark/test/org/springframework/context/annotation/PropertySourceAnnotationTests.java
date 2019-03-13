/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.context.annotation;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.PropertySourceFactory;


/**
 * Tests the processing of @PropertySource annotations on @Configuration classes.
 *
 * @author Chris Beams
 * @author Phillip Webb
 * @since 3.1
 */
public class PropertySourceAnnotationTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void withExplicitName() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithExplicitName.class);
        ctx.refresh();
        Assert.assertTrue("property source p1 was not added", ctx.getEnvironment().getPropertySources().contains("p1"));
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p1TestBean"));
        // assert that the property source was added last to the set of sources
        String name;
        MutablePropertySources sources = ctx.getEnvironment().getPropertySources();
        Iterator<PropertySource<?>> iterator = sources.iterator();
        do {
            name = iterator.next().getName();
        } while (iterator.hasNext() );
        Assert.assertThat(name, CoreMatchers.is("p1"));
    }

    @Test
    public void withImplicitName() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithImplicitName.class);
        ctx.refresh();
        Assert.assertTrue("property source p1 was not added", ctx.getEnvironment().getPropertySources().contains("class path resource [org/springframework/context/annotation/p1.properties]"));
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p1TestBean"));
    }

    @Test
    public void withTestProfileBeans() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithTestProfileBeans.class);
        ctx.refresh();
        Assert.assertTrue(ctx.containsBean("testBean"));
        Assert.assertTrue(ctx.containsBean("testProfileBean"));
    }

    /**
     * Tests the LIFO behavior of @PropertySource annotaitons.
     * The last one registered should 'win'.
     */
    @Test
    public void orderingIsLifo() {
        {
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.register(PropertySourceAnnotationTests.ConfigWithImplicitName.class, PropertySourceAnnotationTests.P2Config.class);
            ctx.refresh();
            // p2 should 'win' as it was registered last
            Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p2TestBean"));
        }
        {
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.register(PropertySourceAnnotationTests.P2Config.class, PropertySourceAnnotationTests.ConfigWithImplicitName.class);
            ctx.refresh();
            // p1 should 'win' as it was registered last
            Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p1TestBean"));
        }
    }

    @Test
    public void withCustomFactory() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithImplicitName.class, PropertySourceAnnotationTests.WithCustomFactory.class);
        ctx.refresh();
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("P2TESTBEAN"));
    }

    @Test
    public void withCustomFactoryAsMeta() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithImplicitName.class, PropertySourceAnnotationTests.WithCustomFactoryAsMeta.class);
        ctx.refresh();
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("P2TESTBEAN"));
    }

    @Test
    public void withUnresolvablePlaceholder() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithUnresolvablePlaceholder.class);
        try {
            ctx.refresh();
        } catch (BeanDefinitionStoreException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void withUnresolvablePlaceholderAndDefault() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithUnresolvablePlaceholderAndDefault.class);
        ctx.refresh();
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p1TestBean"));
    }

    @Test
    public void withResolvablePlaceholder() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithResolvablePlaceholder.class);
        System.setProperty("path.to.properties", "org/springframework/context/annotation");
        ctx.refresh();
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p1TestBean"));
        System.clearProperty("path.to.properties");
    }

    @Test
    public void withResolvablePlaceholderAndFactoryBean() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithResolvablePlaceholderAndFactoryBean.class);
        System.setProperty("path.to.properties", "org/springframework/context/annotation");
        ctx.refresh();
        Assert.assertThat(ctx.getBean(TestBean.class).getName(), CoreMatchers.equalTo("p1TestBean"));
        System.clearProperty("path.to.properties");
    }

    @Test
    public void withEmptyResourceLocations() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(PropertySourceAnnotationTests.ConfigWithEmptyResourceLocations.class);
        try {
            ctx.refresh();
        } catch (BeanDefinitionStoreException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void withNameAndMultipleResourceLocations() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithNameAndMultipleResourceLocations.class);
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p2"), CoreMatchers.is(true));
        // p2 should 'win' as it was registered last
        Assert.assertThat(ctx.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
    }

    @Test
    public void withMultipleResourceLocations() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithMultipleResourceLocations.class);
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p2"), CoreMatchers.is(true));
        // p2 should 'win' as it was registered last
        Assert.assertThat(ctx.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
    }

    @Test
    public void withPropertySources() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithPropertySources.class);
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p2"), CoreMatchers.is(true));
        // p2 should 'win' as it was registered last
        Assert.assertThat(ctx.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
    }

    @Test
    public void withNamedPropertySources() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithNamedPropertySources.class);
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p2"), CoreMatchers.is(true));
        // p2 should 'win' as it was registered last
        Assert.assertThat(ctx.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
    }

    @Test
    public void withMissingPropertySource() {
        thrown.expect(BeanDefinitionStoreException.class);
        thrown.expectCause(CoreMatchers.isA(FileNotFoundException.class));
        new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithMissingPropertySource.class);
    }

    @Test
    public void withIgnoredPropertySource() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithIgnoredPropertySource.class);
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p2"), CoreMatchers.is(true));
    }

    @Test
    public void withSameSourceImportedInDifferentOrder() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithSameSourceImportedInDifferentOrder.class);
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p1"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().containsProperty("from.p2"), CoreMatchers.is(true));
        Assert.assertThat(ctx.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
    }

    @Test
    public void orderingWithAndWithoutNameAndMultipleResourceLocations() {
        // SPR-10820: p2 should 'win' as it was registered last
        AnnotationConfigApplicationContext ctxWithName = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithNameAndMultipleResourceLocations.class);
        AnnotationConfigApplicationContext ctxWithoutName = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithMultipleResourceLocations.class);
        Assert.assertThat(ctxWithoutName.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
        Assert.assertThat(ctxWithName.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p2TestBean"));
    }

    @Test
    public void orderingWithAndWithoutNameAndFourResourceLocations() {
        // SPR-12198: p4 should 'win' as it was registered last
        AnnotationConfigApplicationContext ctxWithoutName = new AnnotationConfigApplicationContext(PropertySourceAnnotationTests.ConfigWithFourResourceLocations.class);
        Assert.assertThat(ctxWithoutName.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("p4TestBean"));
    }

    @Test
    public void orderingDoesntReplaceExisting() throws Exception {
        // SPR-12198: mySource should 'win' as it was registered manually
        AnnotationConfigApplicationContext ctxWithoutName = new AnnotationConfigApplicationContext();
        MapPropertySource mySource = new MapPropertySource("mine", Collections.singletonMap("testbean.name", "myTestBean"));
        ctxWithoutName.getEnvironment().getPropertySources().addLast(mySource);
        ctxWithoutName.register(PropertySourceAnnotationTests.ConfigWithFourResourceLocations.class);
        ctxWithoutName.refresh();
        Assert.assertThat(ctxWithoutName.getEnvironment().getProperty("testbean.name"), CoreMatchers.equalTo("myTestBean"));
    }

    @Configuration
    @PropertySource("classpath:${unresolvable}/p1.properties")
    static class ConfigWithUnresolvablePlaceholder {}

    @Configuration
    @PropertySource("classpath:${unresolvable:org/springframework/context/annotation}/p1.properties")
    static class ConfigWithUnresolvablePlaceholderAndDefault {
        @Inject
        Environment env;

        @Bean
        public TestBean testBean() {
            return new TestBean(env.getProperty("testbean.name"));
        }
    }

    @Configuration
    @PropertySource("classpath:${path.to.properties}/p1.properties")
    static class ConfigWithResolvablePlaceholder {
        @Inject
        Environment env;

        @Bean
        public TestBean testBean() {
            return new TestBean(env.getProperty("testbean.name"));
        }
    }

    @Configuration
    @PropertySource("classpath:${path.to.properties}/p1.properties")
    static class ConfigWithResolvablePlaceholderAndFactoryBean {
        @Inject
        Environment env;

        @Bean
        public FactoryBean testBean() {
            final String name = env.getProperty("testbean.name");
            return new FactoryBean() {
                @Override
                public Object getObject() {
                    return new TestBean(name);
                }

                @Override
                public Class<?> getObjectType() {
                    return TestBean.class;
                }

                @Override
                public boolean isSingleton() {
                    return false;
                }
            };
        }
    }

    @Configuration
    @PropertySource(name = "p1", value = "classpath:org/springframework/context/annotation/p1.properties")
    static class ConfigWithExplicitName {
        @Inject
        Environment env;

        @Bean
        public TestBean testBean() {
            return new TestBean(env.getProperty("testbean.name"));
        }
    }

    @Configuration
    @PropertySource("classpath:org/springframework/context/annotation/p1.properties")
    static class ConfigWithImplicitName {
        @Inject
        Environment env;

        @Bean
        public TestBean testBean() {
            return new TestBean(env.getProperty("testbean.name"));
        }
    }

    @Configuration
    @PropertySource(name = "p1", value = "classpath:org/springframework/context/annotation/p1.properties")
    @ComponentScan("org.springframework.context.annotation.spr12111")
    static class ConfigWithTestProfileBeans {
        @Inject
        Environment env;

        @Bean
        @Profile("test")
        public TestBean testBean() {
            return new TestBean(env.getProperty("testbean.name"));
        }
    }

    @Configuration
    @PropertySource("classpath:org/springframework/context/annotation/p2.properties")
    static class P2Config {}

    @Configuration
    @PropertySource(value = "classpath:org/springframework/context/annotation/p2.properties", factory = PropertySourceAnnotationTests.MyCustomFactory.class)
    static class WithCustomFactory {}

    @Configuration
    @PropertySourceAnnotationTests.MyPropertySource("classpath:org/springframework/context/annotation/p2.properties")
    static class WithCustomFactoryAsMeta {}

    @Retention(RetentionPolicy.RUNTIME)
    @PropertySource(value = {  }, factory = PropertySourceAnnotationTests.MyCustomFactory.class)
    public @interface MyPropertySource {
        @AliasFor(annotation = PropertySource.class)
        String value();
    }

    public static class MyCustomFactory implements PropertySourceFactory {
        @Override
        public PropertySource createPropertySource(String name, EncodedResource resource) throws IOException {
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            return new PropertySource<Properties>(("my" + name), props) {
                @Override
                public Object getProperty(String name) {
                    String value = props.getProperty(name);
                    return value != null ? value.toUpperCase() : null;
                }
            };
        }
    }

    @Configuration
    @PropertySource(name = "psName", value = { "classpath:org/springframework/context/annotation/p1.properties", "classpath:org/springframework/context/annotation/p2.properties" })
    static class ConfigWithNameAndMultipleResourceLocations {}

    @Configuration
    @PropertySource({ "classpath:org/springframework/context/annotation/p1.properties", "classpath:org/springframework/context/annotation/p2.properties" })
    static class ConfigWithMultipleResourceLocations {}

    @Configuration
    @PropertySources({ @PropertySource("classpath:org/springframework/context/annotation/p1.properties"), @PropertySource("classpath:${base.package}/p2.properties") })
    static class ConfigWithPropertySources {}

    @Configuration
    @PropertySources({ @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/p1.properties"), @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/p2.properties") })
    static class ConfigWithNamedPropertySources {}

    @Configuration
    @PropertySources({ @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/p1.properties"), @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/missing.properties"), @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/p2.properties") })
    static class ConfigWithMissingPropertySource {}

    @Configuration
    @PropertySources({ @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/p1.properties"), @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/missing.properties", ignoreResourceNotFound = true), @PropertySource(name = "psName", value = "classpath:${myPath}/missing.properties", ignoreResourceNotFound = true), @PropertySource(name = "psName", value = "classpath:org/springframework/context/annotation/p2.properties") })
    static class ConfigWithIgnoredPropertySource {}

    @Configuration
    @PropertySource({  })
    static class ConfigWithEmptyResourceLocations {}

    @Import(PropertySourceAnnotationTests.ConfigImportedWithSameSourceImportedInDifferentOrder.class)
    @PropertySources({ @PropertySource("classpath:org/springframework/context/annotation/p1.properties"), @PropertySource("classpath:org/springframework/context/annotation/p2.properties") })
    @Configuration
    public static class ConfigWithSameSourceImportedInDifferentOrder {}

    @Configuration
    @PropertySources({ @PropertySource("classpath:org/springframework/context/annotation/p2.properties"), @PropertySource("classpath:org/springframework/context/annotation/p1.properties") })
    public static class ConfigImportedWithSameSourceImportedInDifferentOrder {}

    @Configuration
    @PropertySource({ "classpath:org/springframework/context/annotation/p1.properties", "classpath:org/springframework/context/annotation/p2.properties", "classpath:org/springframework/context/annotation/p3.properties", "classpath:org/springframework/context/annotation/p4.properties" })
    static class ConfigWithFourResourceLocations {}
}

