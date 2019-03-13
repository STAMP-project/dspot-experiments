/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.context.properties;


import AbstractBeanDefinition.AUTOWIRE_BY_TYPE;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

import static ConfigurationPropertiesBindingPostProcessorRegistrar.VALIDATOR_BEAN_NAME;


/**
 * Tests for {@link ConfigurationProperties} annotated beans. Covers
 * {@link EnableConfigurationProperties},
 * {@link ConfigurationPropertiesBindingPostProcessorRegistrar},
 * {@link ConfigurationPropertiesBindingPostProcessor} and
 * {@link ConfigurationPropertiesBinder}.
 *
 * @author Dave Syer
 * @author Christian Dupuis
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
public class ConfigurationPropertiesTests {
    private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void loadShouldBind() {
        load(ConfigurationPropertiesTests.BasicConfiguration.class, "name=foo");
        assertThat(this.context.getBeanNamesForType(ConfigurationPropertiesTests.BasicProperties.class)).hasSize(1);
        assertThat(this.context.containsBean(ConfigurationPropertiesTests.BasicProperties.class.getName())).isTrue();
        assertThat(this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class).name).isEqualTo("foo");
    }

    @Test
    public void loadShouldBindNested() {
        load(ConfigurationPropertiesTests.NestedConfiguration.class, "name=foo", "nested.name=bar");
        assertThat(this.context.getBeanNamesForType(ConfigurationPropertiesTests.NestedProperties.class)).hasSize(1);
        assertThat(this.context.getBean(ConfigurationPropertiesTests.NestedProperties.class).name).isEqualTo("foo");
        assertThat(this.context.getBean(ConfigurationPropertiesTests.NestedProperties.class).nested.name).isEqualTo("bar");
    }

    @Test
    public void loadWhenUsingSystemPropertiesShouldBind() {
        System.setProperty("name", "foo");
        load(ConfigurationPropertiesTests.BasicConfiguration.class);
        assertThat(this.context.getBeanNamesForType(ConfigurationPropertiesTests.BasicProperties.class)).hasSize(1);
        assertThat(this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenUsingSystemPropertiesShouldBindNested() {
        System.setProperty("name", "foo");
        System.setProperty("nested.name", "bar");
        load(ConfigurationPropertiesTests.NestedConfiguration.class);
        assertThat(this.context.getBeanNamesForType(ConfigurationPropertiesTests.NestedProperties.class)).hasSize(1);
        assertThat(this.context.getBean(ConfigurationPropertiesTests.NestedProperties.class).name).isEqualTo("foo");
        assertThat(this.context.getBean(ConfigurationPropertiesTests.NestedProperties.class).nested.name).isEqualTo("bar");
    }

    @Test
    public void loadWhenHasIgnoreUnknownFieldsFalseAndNoUnknownFieldsShouldBind() {
        removeSystemProperties();
        load(ConfigurationPropertiesTests.IgnoreUnknownFieldsFalseConfiguration.class, "name=foo");
        ConfigurationPropertiesTests.IgnoreUnknownFieldsFalseProperties bean = this.context.getBean(ConfigurationPropertiesTests.IgnoreUnknownFieldsFalseProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenHasIgnoreUnknownFieldsFalseAndUnknownFieldsShouldFail() {
        removeSystemProperties();
        assertThatExceptionOfType(ConfigurationPropertiesBindException.class).isThrownBy(() -> load(.class, "name=foo", "bar=baz")).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadWhenHasIgnoreInvalidFieldsTrueAndInvalidFieldsShouldBind() {
        load(ConfigurationPropertiesTests.IgnoreInvalidFieldsFalseProperties.class, "com.example.bar=spam");
        ConfigurationPropertiesTests.IgnoreInvalidFieldsFalseProperties bean = this.context.getBean(ConfigurationPropertiesTests.IgnoreInvalidFieldsFalseProperties.class);
        assertThat(bean.getBar()).isEqualTo(0);
    }

    @Test
    public void loadWhenHasPrefixShouldBind() {
        load(ConfigurationPropertiesTests.PrefixConfiguration.class, "spring.foo.name=foo");
        ConfigurationPropertiesTests.PrefixProperties bean = this.context.getBean(ConfigurationPropertiesTests.PrefixProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenPropertiesHaveAnnotationOnBaseClassShouldBind() {
        load(ConfigurationPropertiesTests.AnnotationOnBaseClassConfiguration.class, "name=foo");
        ConfigurationPropertiesTests.AnnotationOnBaseClassProperties bean = this.context.getBean(ConfigurationPropertiesTests.AnnotationOnBaseClassProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenBindingArrayShouldBind() {
        load(ConfigurationPropertiesTests.BasicConfiguration.class, "name=foo", "array=1,2,3");
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.array).containsExactly(1, 2, 3);
    }

    @Test
    public void loadWhenBindingArrayFromYamlArrayShouldBind() {
        load(ConfigurationPropertiesTests.BasicConfiguration.class, "name=foo", "list[0]=1", "list[1]=2", "list[2]=3");
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.list).containsExactly(1, 2, 3);
    }

    @Test
    public void loadWhenBindingOver256ElementsShouldBind() {
        List<String> pairs = new ArrayList<>();
        pairs.add("name:foo");
        for (int i = 0; i < 1000; i++) {
            pairs.add(((("list[" + i) + "]:") + i));
        }
        load(ConfigurationPropertiesTests.BasicConfiguration.class, StringUtils.toStringArray(pairs));
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.list).hasSize(1000);
    }

    @Test
    public void loadWhenBindingWithoutAndAnnotationShouldFail() {
        assertThatIllegalArgumentException().isThrownBy(() -> load(.class, "name:foo")).withMessageContaining("No ConfigurationProperties annotation found");
    }

    @Test
    public void loadWhenBindingWithoutAnnotationValueShouldBind() {
        load(ConfigurationPropertiesTests.WithoutAnnotationValueConfiguration.class, "name=foo");
        ConfigurationPropertiesTests.WithoutAnnotationValueProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithoutAnnotationValueProperties.class);
        assertThat(bean.name).isEqualTo("foo");
    }

    @Test
    public void loadWhenBindingWithDefaultsInXmlShouldBind() {
        load(new Class<?>[]{ ConfigurationPropertiesTests.BasicConfiguration.class, ConfigurationPropertiesTests.DefaultsInXmlConfiguration.class });
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.name).isEqualTo("bar");
    }

    @Test
    public void loadWhenBindingWithDefaultsInJavaConfigurationShouldBind() {
        load(ConfigurationPropertiesTests.DefaultsInJavaConfiguration.class);
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.name).isEqualTo("bar");
    }

    @Test
    public void loadWhenBindingTwoBeansShouldBind() {
        load(new Class<?>[]{ ConfigurationPropertiesTests.WithoutAnnotationValueConfiguration.class, ConfigurationPropertiesTests.BasicConfiguration.class });
        assertThat(this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class)).isNotNull();
        assertThat(this.context.getBean(ConfigurationPropertiesTests.WithoutAnnotationValueProperties.class)).isNotNull();
    }

    @Test
    public void loadWhenBindingWithParentContextShouldBind() {
        AnnotationConfigApplicationContext parent = load(ConfigurationPropertiesTests.BasicConfiguration.class, "name=parent");
        this.context = new AnnotationConfigApplicationContext();
        this.context.setParent(parent);
        load(new Class[]{ ConfigurationPropertiesTests.BasicConfiguration.class, ConfigurationPropertiesTests.BasicPropertiesConsumer.class }, "name=child");
        assertThat(this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class)).isNotNull();
        assertThat(parent.getBean(ConfigurationPropertiesTests.BasicProperties.class)).isNotNull();
        assertThat(this.context.getBean(ConfigurationPropertiesTests.BasicPropertiesConsumer.class).getName()).isEqualTo("parent");
        parent.close();
    }

    @Test
    public void loadWhenBindingOnlyParentContextShouldBind() {
        AnnotationConfigApplicationContext parent = load(ConfigurationPropertiesTests.BasicConfiguration.class, "name=foo");
        this.context = new AnnotationConfigApplicationContext();
        this.context.setParent(parent);
        load(ConfigurationPropertiesTests.BasicPropertiesConsumer.class);
        assertThat(this.context.getBeanNamesForType(ConfigurationPropertiesTests.BasicProperties.class)).isEmpty();
        assertThat(parent.getBeanNamesForType(ConfigurationPropertiesTests.BasicProperties.class)).hasSize(1);
        assertThat(this.context.getBean(ConfigurationPropertiesTests.BasicPropertiesConsumer.class).getName()).isEqualTo("foo");
    }

    @Test
    public void loadWhenPrefixedPropertiesDeclaredAsBeanShouldBind() {
        load(ConfigurationPropertiesTests.PrefixPropertiesDeclaredAsBeanConfiguration.class, "spring.foo.name=foo");
        ConfigurationPropertiesTests.PrefixProperties bean = this.context.getBean(ConfigurationPropertiesTests.PrefixProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenPrefixedPropertiesDeclaredAsAnnotationValueShouldBind() {
        load(ConfigurationPropertiesTests.PrefixPropertiesDeclaredAsAnnotationValueConfiguration.class, "spring.foo.name=foo");
        ConfigurationPropertiesTests.PrefixProperties bean = this.context.getBean(("spring.foo-" + (ConfigurationPropertiesTests.PrefixProperties.class.getName())), ConfigurationPropertiesTests.PrefixProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenMultiplePrefixedPropertiesDeclaredAsAnnotationValueShouldBind() {
        load(ConfigurationPropertiesTests.MultiplePrefixPropertiesDeclaredAsAnnotationValueConfiguration.class, "spring.foo.name=foo", "spring.bar.name=bar");
        ConfigurationPropertiesTests.PrefixProperties bean1 = this.context.getBean(ConfigurationPropertiesTests.PrefixProperties.class);
        ConfigurationPropertiesTests.AnotherPrefixProperties bean2 = this.context.getBean(ConfigurationPropertiesTests.AnotherPrefixProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean1)).name).isEqualTo("foo");
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean2)).name).isEqualTo("bar");
    }

    @Test
    public void loadWhenBindingToMapKeyWithPeriodShouldBind() {
        load(ConfigurationPropertiesTests.MapProperties.class, "mymap.key1.key2:value12", "mymap.key3:value3");
        ConfigurationPropertiesTests.MapProperties bean = this.context.getBean(ConfigurationPropertiesTests.MapProperties.class);
        assertThat(bean.mymap).containsOnly(entry("key3", "value3"), entry("key1.key2", "value12"));
    }

    @Test
    public void loadWhenPrefixedPropertiesAreReplacedOnBeanMethodShouldBind() {
        load(ConfigurationPropertiesTests.PrefixedPropertiesReplacedOnBeanMethodConfiguration.class, "external.name=bar", "spam.name=foo");
        ConfigurationPropertiesTests.PrefixProperties bean = this.context.getBean(ConfigurationPropertiesTests.PrefixProperties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadShouldBindToJavaTimeDuration() {
        load(ConfigurationPropertiesTests.BasicConfiguration.class, "duration=PT1M");
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.getDuration().getSeconds()).isEqualTo(60);
    }

    @Test
    public void loadWhenBindingToValidatedImplementationOfInterfaceShouldBind() {
        load(ConfigurationPropertiesTests.ValidatedImplementationConfiguration.class, "test.foo=bar");
        ConfigurationPropertiesTests.ValidatedImplementationProperties bean = this.context.getBean(ConfigurationPropertiesTests.ValidatedImplementationProperties.class);
        assertThat(bean.getFoo()).isEqualTo("bar");
    }

    @Test
    public void loadWithPropertyPlaceholderValueShouldBind() {
        load(ConfigurationPropertiesTests.WithPropertyPlaceholderValueConfiguration.class, "default.value=foo");
        ConfigurationPropertiesTests.WithPropertyPlaceholderValueProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithPropertyPlaceholderValueProperties.class);
        assertThat(bean.getValue()).isEqualTo("foo");
    }

    @Test
    public void loadWithPropertyPlaceholderShouldNotAlterPropertySourceOrder() {
        load(ConfigurationPropertiesTests.WithPropertyPlaceholderWithLocalPropertiesValueConfiguration.class, "com.example.bar=a");
        ConfigurationPropertiesTests.SimplePrefixedProperties bean = this.context.getBean(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        assertThat(bean.getBar()).isEqualTo("a");
    }

    @Test
    public void loadWhenHasPostConstructShouldTriggerPostConstructWithBoundBean() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("bar", "foo");
        this.context.setEnvironment(environment);
        this.context.register(ConfigurationPropertiesTests.WithPostConstructConfiguration.class);
        this.context.refresh();
        ConfigurationPropertiesTests.WithPostConstructConfiguration bean = this.context.getBean(ConfigurationPropertiesTests.WithPostConstructConfiguration.class);
        assertThat(bean.initialized).isTrue();
    }

    @Test
    public void loadShouldNotInitializeFactoryBeans() {
        ConfigurationPropertiesTests.WithFactoryBeanConfiguration.factoryBeanInitialized = false;
        this.context = new AnnotationConfigApplicationContext() {
            @Override
            protected void onRefresh() throws BeansException {
                assertThat(ConfigurationPropertiesTests.WithFactoryBeanConfiguration.factoryBeanInitialized).as("Initialized too early").isFalse();
                super.onRefresh();
            }
        };
        this.context.register(ConfigurationPropertiesTests.WithFactoryBeanConfiguration.class);
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(ConfigurationPropertiesTests.FactoryBeanTester.class);
        beanDefinition.setAutowireMode(AUTOWIRE_BY_TYPE);
        this.context.registerBeanDefinition("test", beanDefinition);
        this.context.refresh();
        assertThat(ConfigurationPropertiesTests.WithFactoryBeanConfiguration.factoryBeanInitialized).as("Not Initialized").isTrue();
    }

    @Test
    public void loadWhenUsingRelaxedFormsShouldBindToEnum() {
        bindToEnum("test.theValue=FOO");
        bindToEnum("test.theValue=foo");
        bindToEnum("test.the-value=FoO");
        bindToEnum("test.THE_VALUE=FoO");
    }

    @Test
    public void loadWhenUsingRelaxedFormsShouldBindToEnumSet() {
        bindToEnumSet("test.the-values=foo,bar", ConfigurationPropertiesTests.FooEnum.FOO, ConfigurationPropertiesTests.FooEnum.BAR);
        bindToEnumSet("test.the-values=foo", ConfigurationPropertiesTests.FooEnum.FOO);
    }

    @Test
    public void loadShouldBindToCharArray() {
        load(ConfigurationPropertiesTests.WithCharArrayProperties.class, "test.chars=word");
        ConfigurationPropertiesTests.WithCharArrayProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithCharArrayProperties.class);
        assertThat(bean.getChars()).isEqualTo("word".toCharArray());
    }

    @Test
    public void loadWhenUsingRelaxedFormsAndOverrideShouldBind() {
        load(ConfigurationPropertiesTests.WithRelaxedNamesProperties.class, "test.FOO_BAR=test1", "test.FOO_BAR=test2", "test.BAR-B-A-Z=testa", "test.BAR-B-A-Z=testb");
        ConfigurationPropertiesTests.WithRelaxedNamesProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithRelaxedNamesProperties.class);
        assertThat(bean.getFooBar()).isEqualTo("test2");
        assertThat(bean.getBarBAZ()).isEqualTo("testb");
    }

    @Test
    public void loadShouldBindToMap() {
        load(ConfigurationPropertiesTests.WithMapProperties.class, "test.map.foo=bar");
        ConfigurationPropertiesTests.WithMapProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithMapProperties.class);
        assertThat(bean.getMap()).containsOnly(entry("foo", "bar"));
    }

    @Test
    public void loadShouldBindToMapWithNumericKey() {
        load(ConfigurationPropertiesTests.MapWithNumericKeyProperties.class, "sample.properties.1.name=One");
        ConfigurationPropertiesTests.MapWithNumericKeyProperties bean = this.context.getBean(ConfigurationPropertiesTests.MapWithNumericKeyProperties.class);
        assertThat(bean.getProperties().get("1").name).isEqualTo("One");
    }

    @Test
    public void loadWhenUsingSystemPropertiesShouldBindToMap() {
        this.context.getEnvironment().getPropertySources().addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, Collections.singletonMap("TEST_MAP_FOO_BAR", "baz")));
        load(ConfigurationPropertiesTests.WithComplexMapProperties.class);
        ConfigurationPropertiesTests.WithComplexMapProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithComplexMapProperties.class);
        assertThat(bean.getMap()).containsOnlyKeys("foo");
        assertThat(bean.getMap().get("foo")).containsOnly(entry("bar", "baz"));
    }

    @Test
    public void loadWhenDotsInSystemEnvironmentPropertiesShouldBind() {
        this.context.getEnvironment().getPropertySources().addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, Collections.singletonMap("com.example.bar", "baz")));
        load(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        ConfigurationPropertiesTests.SimplePrefixedProperties bean = this.context.getBean(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        assertThat(bean.getBar()).isEqualTo("baz");
    }

    @Test
    public void loadWhenOverridingPropertiesShouldBind() {
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        sources.addFirst(new SystemEnvironmentPropertySource("system", Collections.singletonMap("SPRING_FOO_NAME", "Jane")));
        sources.addLast(new MapPropertySource("test", Collections.singletonMap("spring.foo.name", "John")));
        load(ConfigurationPropertiesTests.PrefixConfiguration.class);
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.name).isEqualTo("Jane");
    }

    @Test
    public void loadWhenJsr303ConstraintDoesNotMatchShouldFail() {
        assertThatExceptionOfType(ConfigurationPropertiesBindException.class).isThrownBy(() -> load(.class, "description=")).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadValidatedOnBeanMethodAndJsr303ConstraintDoesNotMatchShouldFail() {
        assertThatExceptionOfType(ConfigurationPropertiesBindException.class).isThrownBy(() -> load(.class, "description=")).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadWhenJsr303ConstraintDoesNotMatchOnNestedThatIsNotDirectlyAnnotatedShouldFail() {
        assertThatExceptionOfType(ConfigurationPropertiesBindException.class).isThrownBy(() -> load(.class, "properties.description=")).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadWhenJsr303ConstraintDoesNotMatchOnNestedThatIsNotDirectlyAnnotatedButIsValidShouldFail() {
        assertThatExceptionOfType(ConfigurationPropertiesBindException.class).isThrownBy(() -> load(.class)).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadWhenJsr303ConstraintMatchesShouldBind() {
        load(ConfigurationPropertiesTests.ValidatedJsr303Configuration.class, "description=foo");
        ConfigurationPropertiesTests.ValidatedJsr303Properties bean = this.context.getBean(ConfigurationPropertiesTests.ValidatedJsr303Properties.class);
        assertThat(bean.getDescription()).isEqualTo("foo");
    }

    @Test
    public void loadWhenJsr303ConstraintDoesNotMatchAndNotValidatedAnnotationShouldBind() {
        load(ConfigurationPropertiesTests.NonValidatedJsr303Configuration.class, "name=foo");
        ConfigurationPropertiesTests.NonValidatedJsr303Properties bean = this.context.getBean(ConfigurationPropertiesTests.NonValidatedJsr303Properties.class);
        assertThat(((ConfigurationPropertiesTests.BasicProperties) (bean)).name).isEqualTo("foo");
    }

    @Test
    public void loadWhenHasMultiplePropertySourcesPlaceholderConfigurerShouldLogWarning() {
        load(ConfigurationPropertiesTests.MultiplePropertySourcesPlaceholderConfigurerConfiguration.class);
        assertThat(this.output.toString()).contains("Multiple PropertySourcesPlaceholderConfigurer beans registered");
    }

    @Test
    public void loadWhenOverridingPropertiesWithPlaceholderResolutionInEnvironmentShouldBindWithOverride() {
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        sources.addFirst(new SystemEnvironmentPropertySource("system", Collections.singletonMap("COM_EXAMPLE_BAR", "10")));
        Map<String, Object> source = new HashMap<>();
        source.put("com.example.bar", 5);
        source.put("com.example.foo", "${com.example.bar}");
        sources.addLast(new MapPropertySource("test", source));
        load(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        ConfigurationPropertiesTests.SimplePrefixedProperties bean = this.context.getBean(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        assertThat(bean.getFoo()).isEqualTo(10);
    }

    @Test
    public void loadWhenHasUnboundElementsFromSystemEnvironmentShouldNotThrowException() {
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        sources.addFirst(new MapPropertySource("test", Collections.singletonMap("com.example.foo", 5)));
        sources.addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, Collections.singletonMap("COM_EXAMPLE_OTHER", "10")));
        load(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        ConfigurationPropertiesTests.SimplePrefixedProperties bean = this.context.getBean(ConfigurationPropertiesTests.SimplePrefixedProperties.class);
        assertThat(bean.getFoo()).isEqualTo(5);
    }

    @Test
    public void loadShouldSupportRebindableConfigurationProperties() {
        // gh-9160
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("example.one", "foo");
        sources.addFirst(new MapPropertySource("test-source", source));
        this.context.register(ConfigurationPropertiesTests.PrototypePropertiesConfiguration.class);
        this.context.refresh();
        ConfigurationPropertiesTests.PrototypeBean first = this.context.getBean(ConfigurationPropertiesTests.PrototypeBean.class);
        assertThat(first.getOne()).isEqualTo("foo");
        source.put("example.one", "bar");
        sources.addFirst(new MapPropertySource("extra", Collections.singletonMap("example.two", "baz")));
        ConfigurationPropertiesTests.PrototypeBean second = this.context.getBean(ConfigurationPropertiesTests.PrototypeBean.class);
        assertThat(second.getOne()).isEqualTo("bar");
        assertThat(second.getTwo()).isEqualTo("baz");
    }

    @Test
    public void loadWhenHasPropertySourcesPlaceholderConfigurerShouldSupportRebindableConfigurationProperties() {
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("example.one", "foo");
        sources.addFirst(new MapPropertySource("test-source", source));
        this.context.register(ConfigurationPropertiesTests.PrototypePropertiesConfiguration.class);
        this.context.register(PropertySourcesPlaceholderConfigurer.class);
        this.context.refresh();
        ConfigurationPropertiesTests.PrototypeBean first = this.context.getBean(ConfigurationPropertiesTests.PrototypeBean.class);
        assertThat(first.getOne()).isEqualTo("foo");
        source.put("example.one", "bar");
        sources.addFirst(new MapPropertySource("extra", Collections.singletonMap("example.two", "baz")));
        ConfigurationPropertiesTests.PrototypeBean second = this.context.getBean(ConfigurationPropertiesTests.PrototypeBean.class);
        assertThat(second.getOne()).isEqualTo("bar");
        assertThat(second.getTwo()).isEqualTo("baz");
    }

    @Test
    public void customProtocolResolverIsInvoked() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.context, "test.resource=application.properties");
        ProtocolResolver protocolResolver = Mockito.mock(ProtocolResolver.class);
        BDDMockito.given(protocolResolver.resolve(ArgumentMatchers.anyString(), ArgumentMatchers.any(ResourceLoader.class))).willReturn(null);
        this.context.addProtocolResolver(protocolResolver);
        this.context.register(ConfigurationPropertiesTests.PropertiesWithResource.class);
        this.context.refresh();
        Mockito.verify(protocolResolver).resolve(ArgumentMatchers.eq("application.properties"), ArgumentMatchers.any(ResourceLoader.class));
    }

    @Test
    public void customProtocolResolver() {
        this.context = new AnnotationConfigApplicationContext();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(this.context, "test.resource=test:/application.properties");
        this.context.addProtocolResolver(new ConfigurationPropertiesTests.TestProtocolResolver());
        this.context.register(ConfigurationPropertiesTests.PropertiesWithResource.class);
        this.context.refresh();
        Resource resource = this.context.getBean(ConfigurationPropertiesTests.PropertiesWithResource.class).getResource();
        assertThat(resource).isNotNull();
        assertThat(resource).isInstanceOf(ClassPathResource.class);
        assertThat(resource.exists()).isTrue();
        assertThat(getPath()).isEqualTo("application.properties");
    }

    @Test
    public void loadShouldUseConfigurationConverter() {
        prepareConverterContext(ConfigurationPropertiesTests.ConverterConfiguration.class, ConfigurationPropertiesTests.PersonProperties.class);
        ConfigurationPropertiesTests.Person person = this.context.getBean(ConfigurationPropertiesTests.PersonProperties.class).getPerson();
        assertThat(person.firstName).isEqualTo("John");
        assertThat(person.lastName).isEqualTo("Smith");
    }

    @Test
    public void loadWhenConfigurationConverterIsNotQualifiedShouldNotConvert() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> prepareConverterContext(.class, .class)).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadShouldUseGenericConfigurationConverter() {
        prepareConverterContext(ConfigurationPropertiesTests.GenericConverterConfiguration.class, ConfigurationPropertiesTests.PersonProperties.class);
        ConfigurationPropertiesTests.Person person = this.context.getBean(ConfigurationPropertiesTests.PersonProperties.class).getPerson();
        assertThat(person.firstName).isEqualTo("John");
        assertThat(person.lastName).isEqualTo("Smith");
    }

    @Test
    public void loadWhenGenericConfigurationConverterIsNotQualifiedShouldNotConvert() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> prepareConverterContext(.class, .class)).withCauseInstanceOf(BindException.class);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void loadShouldBindToBeanWithGenerics() {
        load(ConfigurationPropertiesTests.GenericConfiguration.class, "foo.bar=hello");
        ConfigurationPropertiesTests.AGenericClass foo = this.context.getBean(ConfigurationPropertiesTests.AGenericClass.class);
        assertThat(foo.getBar()).isNotNull();
    }

    @Test
    public void loadWhenHasConfigurationPropertiesValidatorShouldApplyValidator() {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> load(.class)).satisfies(( ex) -> {
            assertThat(ex).hasCauseInstanceOf(.class);
            assertThat(ex.getCause()).hasCauseExactlyInstanceOf(.class);
        });
    }

    @Test
    public void loadWhenHasUnsupportedConfigurationPropertiesValidatorShouldBind() {
        load(ConfigurationPropertiesTests.WithUnsupportedCustomValidatorConfiguration.class, "test.foo=bar");
        ConfigurationPropertiesTests.WithSetterThatThrowsValidationExceptionProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithSetterThatThrowsValidationExceptionProperties.class);
        assertThat(bean.getFoo()).isEqualTo("bar");
    }

    @Test
    public void loadWhenConfigurationPropertiesIsAlsoValidatorShouldApplyValidator() {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> load(.class)).satisfies(( ex) -> {
            assertThat(ex).hasCauseInstanceOf(.class);
            assertThat(ex.getCause()).hasCauseExactlyInstanceOf(.class);
        });
    }

    @Test
    public void loadWhenSetterThrowsValidationExceptionShouldFail() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> load(.class, "test.foo=spam")).withCauseInstanceOf(BindException.class);
    }

    @Test
    public void loadWhenFailsShouldIncludeAnnotationDetails() {
        removeSystemProperties();
        assertThatExceptionOfType(ConfigurationPropertiesBindException.class).isThrownBy(() -> load(.class, "name=foo", "bar=baz")).withMessageContaining(("Could not bind properties to " + ("'ConfigurationPropertiesTests.IgnoreUnknownFieldsFalseProperties' : " + "prefix=, ignoreInvalidFields=false, ignoreUnknownFields=false;")));
    }

    @Test
    public void loadWhenHasCustomPropertyEditorShouldBind() {
        this.context.getBeanFactory().registerCustomEditor(ConfigurationPropertiesTests.Person.class, ConfigurationPropertiesTests.PersonPropertyEditor.class);
        load(ConfigurationPropertiesTests.PersonProperties.class, "test.person=boot,spring");
        ConfigurationPropertiesTests.PersonProperties bean = this.context.getBean(ConfigurationPropertiesTests.PersonProperties.class);
        assertThat(bean.getPerson().firstName).isEqualTo("spring");
        assertThat(bean.getPerson().lastName).isEqualTo("boot");
    }

    @Test
    public void loadWhenBindingToListOfGenericClassShouldBind() {
        // gh-12166
        load(ConfigurationPropertiesTests.ListOfGenericClassProperties.class, "test.list=java.lang.RuntimeException");
        ConfigurationPropertiesTests.ListOfGenericClassProperties bean = this.context.getBean(ConfigurationPropertiesTests.ListOfGenericClassProperties.class);
        assertThat(bean.getList()).containsExactly(RuntimeException.class);
    }

    @Test
    public void loadWhenBindingCurrentDirectoryToFileShouldBind() {
        load(ConfigurationPropertiesTests.FileProperties.class, "test.file=.");
        ConfigurationPropertiesTests.FileProperties bean = this.context.getBean(ConfigurationPropertiesTests.FileProperties.class);
        assertThat(bean.getFile()).isEqualTo(new File("."));
    }

    @Test
    public void loadWhenBindingToDataSizeShouldBind() {
        load(ConfigurationPropertiesTests.DataSizeProperties.class, "test.size=10GB", "test.another-size=5");
        ConfigurationPropertiesTests.DataSizeProperties bean = this.context.getBean(ConfigurationPropertiesTests.DataSizeProperties.class);
        assertThat(bean.getSize()).isEqualTo(DataSize.ofGigabytes(10));
        assertThat(bean.getAnotherSize()).isEqualTo(DataSize.ofKilobytes(5));
    }

    @Test
    public void loadWhenTopLevelConverterNotFoundExceptionShouldNotFail() {
        load(ConfigurationPropertiesTests.PersonProperties.class, "test=boot");
    }

    @Test
    public void loadWhenConfigurationPropertiesContainsMapWithPositiveAndNegativeIntegerKeys() {
        // gh-14136
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        Map<String, Object> source = new HashMap<>();
        source.put("test.map.x.[-1].a", "baz");
        source.put("test.map.x.1.a", "bar");
        source.put("test.map.x.1.b", 1);
        sources.addLast(new MapPropertySource("test", source));
        load(ConfigurationPropertiesTests.WithIntegerMapProperties.class);
        ConfigurationPropertiesTests.WithIntegerMapProperties bean = this.context.getBean(ConfigurationPropertiesTests.WithIntegerMapProperties.class);
        Map<Integer, ConfigurationPropertiesTests.Foo> x = bean.getMap().get("x");
        assertThat(x.get((-1)).getA()).isEqualTo("baz");
        assertThat(x.get((-1)).getB()).isEqualTo(0);
        assertThat(x.get(1).getA()).isEqualTo("bar");
        assertThat(x.get(1).getB()).isEqualTo(1);
    }

    @Test
    public void loadWhenConfigurationPropertiesInjectsAnotherBeanShouldNotFail() {
        load(ConfigurationPropertiesTests.OtherInjectPropertiesConfiguration.class);
    }

    @Test
    public void loadWhenBindingToConstructorParametersShouldBind() {
        MutablePropertySources sources = this.context.getEnvironment().getPropertySources();
        Map<String, Object> source = new HashMap<>();
        source.put("test.foo", "baz");
        source.put("test.bar", "5");
        sources.addLast(new MapPropertySource("test", source));
        load(ConfigurationPropertiesTests.ConstructorParameterConfiguration.class);
        ConfigurationPropertiesTests.ConstructorParameterProperties bean = this.context.getBean(ConfigurationPropertiesTests.ConstructorParameterProperties.class);
        assertThat(bean.getFoo()).isEqualTo("baz");
        assertThat(bean.getBar()).isEqualTo(5);
    }

    @Test
    public void loadWhenBindingToConstructorParametersWithDefaultValuesShouldBind() {
        load(ConfigurationPropertiesTests.ConstructorParameterConfiguration.class);
        ConfigurationPropertiesTests.ConstructorParameterProperties bean = this.context.getBean(ConfigurationPropertiesTests.ConstructorParameterProperties.class);
        assertThat(bean.getFoo()).isEqualTo("hello");
        assertThat(bean.getBar()).isEqualTo(0);
    }

    @Test
    public void loadWhenBindingToConstructorParametersShouldValidate() {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> load(.class)).satisfies(( ex) -> {
            assertThat(ex).hasCauseInstanceOf(.class);
            assertThat(ex.getCause()).hasCauseExactlyInstanceOf(.class);
        });
    }

    @Test
    public void loadWhenBindingOnBeanWithoutBeanDefinitionShouldBind() {
        load(ConfigurationPropertiesTests.BasicConfiguration.class, "name=test");
        ConfigurationPropertiesTests.BasicProperties bean = this.context.getBean(ConfigurationPropertiesTests.BasicProperties.class);
        assertThat(bean.name).isEqualTo("test");
        bean.name = "override";
        this.context.getBean(ConfigurationPropertiesBindingPostProcessor.class).postProcessBeforeInitialization(bean, "does-not-exist");
        assertThat(bean.name).isEqualTo("test");
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.BasicProperties.class)
    static class BasicConfiguration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.NestedProperties.class)
    static class NestedConfiguration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.IgnoreUnknownFieldsFalseProperties.class)
    static class IgnoreUnknownFieldsFalseConfiguration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.PrefixProperties.class)
    static class PrefixConfiguration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.ValidatedJsr303Properties.class)
    static class ValidatedJsr303Configuration {}

    @Configuration
    @EnableConfigurationProperties
    static class ValidatedOnBeanJsr303Configuration {
        @Bean
        @Validated
        public ConfigurationPropertiesTests.NonValidatedJsr303Properties properties() {
            return new ConfigurationPropertiesTests.NonValidatedJsr303Properties();
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.NonValidatedJsr303Properties.class)
    static class NonValidatedJsr303Configuration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.AnnotationOnBaseClassProperties.class)
    static class AnnotationOnBaseClassConfiguration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.WithoutAndAnnotationConfiguration.class)
    static class WithoutAndAnnotationConfiguration {}

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.WithoutAnnotationValueProperties.class)
    static class WithoutAnnotationValueConfiguration {}

    @Configuration
    @ImportResource("org/springframework/boot/context/properties/testProperties.xml")
    static class DefaultsInXmlConfiguration {}

    @Configuration
    static class DefaultsInJavaConfiguration {
        @Bean
        public ConfigurationPropertiesTests.BasicProperties basicProperties() {
            ConfigurationPropertiesTests.BasicProperties test = new ConfigurationPropertiesTests.BasicProperties();
            test.setName("bar");
            return test;
        }
    }

    @Configuration
    @EnableConfigurationProperties
    static class PrefixPropertiesDeclaredAsBeanConfiguration {
        @Bean
        public ConfigurationPropertiesTests.PrefixProperties prefixProperties() {
            return new ConfigurationPropertiesTests.PrefixProperties();
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.PrefixProperties.class)
    static class PrefixPropertiesDeclaredAsAnnotationValueConfiguration {}

    @Configuration
    @EnableConfigurationProperties({ ConfigurationPropertiesTests.PrefixProperties.class, ConfigurationPropertiesTests.AnotherPrefixProperties.class })
    static class MultiplePrefixPropertiesDeclaredAsAnnotationValueConfiguration {}

    @Configuration
    @EnableConfigurationProperties
    static class PrefixedPropertiesReplacedOnBeanMethodConfiguration {
        @Bean
        @ConfigurationProperties(prefix = "spam")
        public ConfigurationPropertiesTests.PrefixProperties prefixProperties() {
            return new ConfigurationPropertiesTests.PrefixProperties();
        }
    }

    @Configuration
    @EnableConfigurationProperties
    static class ValidatedImplementationConfiguration {
        @Bean
        public ConfigurationPropertiesTests.ValidatedImplementationProperties testProperties() {
            return new ConfigurationPropertiesTests.ValidatedImplementationProperties();
        }
    }

    @Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties
    static class WithPostConstructConfiguration {
        private String bar;

        private boolean initialized;

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return this.bar;
        }

        @PostConstruct
        public void init() {
            assertThat(this.bar).isNotNull();
            this.initialized = true;
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.WithPropertyPlaceholderValueProperties.class)
    static class WithPropertyPlaceholderValueConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer configurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.SimplePrefixedProperties.class)
    static class WithPropertyPlaceholderWithLocalPropertiesValueConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer configurer() {
            PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
            Properties properties = new Properties();
            properties.put("com.example.bar", "b");
            placeholderConfigurer.setProperties(properties);
            return placeholderConfigurer;
        }
    }

    @Configuration
    @EnableConfigurationProperties
    static class WithFactoryBeanConfiguration {
        public static boolean factoryBeanInitialized;
    }

    @Configuration
    @EnableConfigurationProperties
    static class MultiplePropertySourcesPlaceholderConfigurerConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer configurer1() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        public static PropertySourcesPlaceholderConfigurer configurer2() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Configuration
    @EnableConfigurationProperties
    static class PrototypePropertiesConfiguration {
        @Bean
        @Scope("prototype")
        @ConfigurationProperties("example")
        public ConfigurationPropertiesTests.PrototypeBean prototypeBean() {
            return new ConfigurationPropertiesTests.PrototypeBean();
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    public static class PropertiesWithResource {
        private Resource resource;

        public Resource getResource() {
            return this.resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }
    }

    private static class TestProtocolResolver implements ProtocolResolver {
        public static final String PREFIX = "test:/";

        @Override
        public Resource resolve(String location, ResourceLoader resourceLoader) {
            if (location.startsWith(ConfigurationPropertiesTests.TestProtocolResolver.PREFIX)) {
                String path = location.substring(ConfigurationPropertiesTests.TestProtocolResolver.PREFIX.length());
                return new ClassPathResource(path);
            }
            return null;
        }
    }

    @Configuration
    static class ConverterConfiguration {
        @Bean
        @ConfigurationPropertiesBinding
        public Converter<String, ConfigurationPropertiesTests.Person> personConverter() {
            return new ConfigurationPropertiesTests.PersonConverter();
        }
    }

    @Configuration
    static class NonQualifiedConverterConfiguration {
        @Bean
        public Converter<String, ConfigurationPropertiesTests.Person> personConverter() {
            return new ConfigurationPropertiesTests.PersonConverter();
        }
    }

    @Configuration
    static class GenericConverterConfiguration {
        @Bean
        @ConfigurationPropertiesBinding
        public GenericConverter genericPersonConverter() {
            return new ConfigurationPropertiesTests.GenericPersonConverter();
        }
    }

    @Configuration
    static class NonQualifiedGenericConverterConfiguration {
        @Bean
        public GenericConverter genericPersonConverter() {
            return new ConfigurationPropertiesTests.GenericPersonConverter();
        }
    }

    @Configuration
    @EnableConfigurationProperties
    static class GenericConfiguration {
        @Bean
        @ConfigurationProperties("foo")
        public ConfigurationPropertiesTests.AGenericClass<String> aBeanToBind() {
            return new ConfigurationPropertiesTests.AGenericClass<>();
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.WithCustomValidatorProperties.class)
    static class WithCustomValidatorConfiguration {
        @Bean(name = VALIDATOR_BEAN_NAME)
        public ConfigurationPropertiesTests.CustomPropertiesValidator validator() {
            return new ConfigurationPropertiesTests.CustomPropertiesValidator();
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.WithSetterThatThrowsValidationExceptionProperties.class)
    static class WithUnsupportedCustomValidatorConfiguration {
        @Bean(name = ConfigurationPropertiesBindingPostProcessorRegistrar.VALIDATOR_BEAN_NAME)
        public ConfigurationPropertiesTests.CustomPropertiesValidator validator() {
            return new ConfigurationPropertiesTests.CustomPropertiesValidator();
        }
    }

    static class AGenericClass<T> {
        private T bar;

        public T getBar() {
            return this.bar;
        }

        public void setBar(T bar) {
            this.bar = bar;
        }
    }

    static class PrototypeBean {
        private String one;

        private String two;

        public String getOne() {
            return this.one;
        }

        public void setOne(String one) {
            this.one = one;
        }

        public String getTwo() {
            return this.two;
        }

        public void setTwo(String two) {
            this.two = two;
        }
    }

    // Must be a raw type
    @SuppressWarnings("rawtypes")
    static class FactoryBeanTester implements FactoryBean , InitializingBean {
        @Override
        public Object getObject() {
            return Object.class;
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public void afterPropertiesSet() {
            ConfigurationPropertiesTests.WithFactoryBeanConfiguration.factoryBeanInitialized = true;
        }
    }

    @ConfigurationProperties
    static class BasicProperties {
        private String name;

        private int[] array;

        private List<Integer> list = new ArrayList<>();

        private Duration duration;

        // No getter - you should be able to bind to a write-only bean
        public void setName(String name) {
            this.name = name;
        }

        public void setArray(int... values) {
            this.array = values;
        }

        public int[] getArray() {
            return this.array;
        }

        public List<Integer> getList() {
            return this.list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }

        public Duration getDuration() {
            return this.duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }
    }

    @ConfigurationProperties
    static class NestedProperties {
        private String name;

        private final ConfigurationPropertiesTests.NestedProperties.Nested nested = new ConfigurationPropertiesTests.NestedProperties.Nested();

        public void setName(String name) {
            this.name = name;
        }

        public ConfigurationPropertiesTests.NestedProperties.Nested getNested() {
            return this.nested;
        }

        protected static class Nested {
            private String name;

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    @ConfigurationProperties(ignoreUnknownFields = false)
    static class IgnoreUnknownFieldsFalseProperties extends ConfigurationPropertiesTests.BasicProperties {}

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "com.example", ignoreInvalidFields = true)
    static class IgnoreInvalidFieldsFalseProperties {
        private long bar;

        public void setBar(long bar) {
            this.bar = bar;
        }

        public long getBar() {
            return this.bar;
        }
    }

    @ConfigurationProperties(prefix = "spring.foo")
    static class PrefixProperties extends ConfigurationPropertiesTests.BasicProperties {}

    @ConfigurationProperties(prefix = "spring.bar")
    static class AnotherPrefixProperties extends ConfigurationPropertiesTests.BasicProperties {}

    static class Jsr303Properties extends ConfigurationPropertiesTests.BasicProperties {
        @NotEmpty
        private String description;

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @ConfigurationProperties
    @Validated
    static class ValidatedJsr303Properties extends ConfigurationPropertiesTests.Jsr303Properties {}

    @ConfigurationProperties
    static class NonValidatedJsr303Properties extends ConfigurationPropertiesTests.Jsr303Properties {}

    @EnableConfigurationProperties
    @ConfigurationProperties
    @Validated
    static class ValidatedNestedJsr303Properties {
        private ConfigurationPropertiesTests.Jsr303Properties properties;

        public ConfigurationPropertiesTests.Jsr303Properties getProperties() {
            return this.properties;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties
    @Validated
    static class ValidatedValidNestedJsr303Properties {
        @Valid
        private List<ConfigurationPropertiesTests.Jsr303Properties> properties = Collections.singletonList(new ConfigurationPropertiesTests.Jsr303Properties());

        public List<ConfigurationPropertiesTests.Jsr303Properties> getProperties() {
            return this.properties;
        }
    }

    static class AnnotationOnBaseClassProperties extends ConfigurationPropertiesTests.BasicProperties {}

    // No getter - you should be able to bind to a write-only bean
    @ConfigurationProperties
    static class WithoutAnnotationValueProperties {
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties
    static class MapProperties {
        private Map<String, String> mymap;

        public void setMymap(Map<String, String> mymap) {
            this.mymap = mymap;
        }

        public Map<String, String> getMymap() {
            return this.mymap;
        }
    }

    @Component
    static class BasicPropertiesConsumer {
        @Autowired
        private ConfigurationPropertiesTests.BasicProperties properties;

        @PostConstruct
        public void init() {
            assertThat(this.properties).isNotNull();
        }

        public String getName() {
            return this.properties.name;
        }
    }

    interface InterfaceForValidatedImplementation {
        String getFoo();
    }

    @ConfigurationProperties("test")
    @Validated
    static class ValidatedImplementationProperties implements ConfigurationPropertiesTests.InterfaceForValidatedImplementation {
        @NotNull
        private String foo;

        @Override
        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    @ConfigurationProperties(prefix = "test")
    @Validated
    static class WithPropertyPlaceholderValueProperties {
        @Value("${default.value}")
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class WithEnumProperties {
        private ConfigurationPropertiesTests.FooEnum theValue;

        private List<ConfigurationPropertiesTests.FooEnum> theValues;

        public void setTheValue(ConfigurationPropertiesTests.FooEnum value) {
            this.theValue = value;
        }

        public ConfigurationPropertiesTests.FooEnum getTheValue() {
            return this.theValue;
        }

        public List<ConfigurationPropertiesTests.FooEnum> getTheValues() {
            return this.theValues;
        }

        public void setTheValues(List<ConfigurationPropertiesTests.FooEnum> theValues) {
            this.theValues = theValues;
        }
    }

    enum FooEnum {

        FOO,
        BAZ,
        BAR;}

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test", ignoreUnknownFields = false)
    static class WithCharArrayProperties {
        private char[] chars;

        public char[] getChars() {
            return this.chars;
        }

        public void setChars(char[] chars) {
            this.chars = chars;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class WithRelaxedNamesProperties {
        private String fooBar;

        private String barBAZ;

        public String getFooBar() {
            return this.fooBar;
        }

        public void setFooBar(String fooBar) {
            this.fooBar = fooBar;
        }

        public String getBarBAZ() {
            return this.barBAZ;
        }

        public void setBarBAZ(String barBAZ) {
            this.barBAZ = barBAZ;
        }
    }

    @Validated
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class WithMapProperties {
        private Map<String, String> map;

        public Map<String, String> getMap() {
            return this.map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class WithComplexMapProperties {
        private Map<String, Map<String, String>> map;

        public Map<String, Map<String, String>> getMap() {
            return this.map;
        }

        public void setMap(Map<String, Map<String, String>> map) {
            this.map = map;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class WithIntegerMapProperties {
        private Map<String, Map<Integer, ConfigurationPropertiesTests.Foo>> map;

        public Map<String, Map<Integer, ConfigurationPropertiesTests.Foo>> getMap() {
            return this.map;
        }

        public void setMap(Map<String, Map<Integer, ConfigurationPropertiesTests.Foo>> map) {
            this.map = map;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "com.example", ignoreUnknownFields = false)
    static class SimplePrefixedProperties {
        private int foo;

        private String bar;

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return this.bar;
        }

        public int getFoo() {
            return this.foo;
        }

        public void setFoo(int foo) {
            this.foo = foo;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class PersonProperties {
        private ConfigurationPropertiesTests.Person person;

        public ConfigurationPropertiesTests.Person getPerson() {
            return this.person;
        }

        public void setPerson(ConfigurationPropertiesTests.Person person) {
            this.person = person;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "sample")
    static class MapWithNumericKeyProperties {
        private Map<String, ConfigurationPropertiesTests.BasicProperties> properties = new LinkedHashMap<>();

        public Map<String, ConfigurationPropertiesTests.BasicProperties> getProperties() {
            return this.properties;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties
    static class ValidatorProperties implements Validator {
        private String foo;

        @Override
        public boolean supports(Class<?> type) {
            return type == (ConfigurationPropertiesTests.ValidatorProperties.class);
        }

        @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, "foo", "TEST1");
        }

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class WithSetterThatThrowsValidationExceptionProperties {
        private String foo;

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
            if (!(foo.equals("bar"))) {
                throw new IllegalArgumentException("Wrong value for foo");
            }
        }
    }

    @ConfigurationProperties(prefix = "custom")
    static class WithCustomValidatorProperties {
        private String foo;

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class ListOfGenericClassProperties {
        private List<Class<? extends Throwable>> list;

        public List<Class<? extends Throwable>> getList() {
            return this.list;
        }

        public void setList(List<Class<? extends Throwable>> list) {
            this.list = list;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class FileProperties {
        private File file;

        public File getFile() {
            return this.file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "test")
    static class DataSizeProperties {
        private DataSize size;

        @DataSizeUnit(DataUnit.KILOBYTES)
        private DataSize anotherSize;

        public DataSize getSize() {
            return this.size;
        }

        public void setSize(DataSize size) {
            this.size = size;
        }

        public DataSize getAnotherSize() {
            return this.anotherSize;
        }

        public void setAnotherSize(DataSize anotherSize) {
            this.anotherSize = anotherSize;
        }
    }

    @ConfigurationProperties(prefix = "test")
    static class OtherInjectedProperties {
        final ConfigurationPropertiesTests.DataSizeProperties dataSizeProperties;

        @Autowired
        OtherInjectedProperties(ObjectProvider<ConfigurationPropertiesTests.DataSizeProperties> dataSizeProperties) {
            this.dataSizeProperties = dataSizeProperties.getIfUnique();
        }
    }

    @Configuration
    @EnableConfigurationProperties(ConfigurationPropertiesTests.OtherInjectedProperties.class)
    static class OtherInjectPropertiesConfiguration {}

    @ConfigurationProperties(prefix = "test")
    @Validated
    static class ConstructorParameterProperties {
        @NotEmpty
        private final String foo;

        private final int bar;

        ConstructorParameterProperties(@ConfigurationPropertyDefaultValue("hello")
        String foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return this.foo;
        }

        public int getBar() {
            return this.bar;
        }
    }

    @ConfigurationProperties(prefix = "test")
    @Validated
    static class ConstructorParameterValidatedProperties {
        @NotEmpty
        private final String foo;

        ConstructorParameterValidatedProperties(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return this.foo;
        }
    }

    @EnableConfigurationProperties(ConfigurationPropertiesTests.ConstructorParameterProperties.class)
    static class ConstructorParameterConfiguration {}

    @EnableConfigurationProperties(ConfigurationPropertiesTests.ConstructorParameterValidatedProperties.class)
    static class ConstructorParameterValidationConfiguration {}

    static class CustomPropertiesValidator implements Validator {
        @Override
        public boolean supports(Class<?> type) {
            return type == (ConfigurationPropertiesTests.WithCustomValidatorProperties.class);
        }

        @Override
        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, "foo", "TEST1");
        }
    }

    static class PersonConverter implements Converter<String, ConfigurationPropertiesTests.Person> {
        @Override
        public ConfigurationPropertiesTests.Person convert(String source) {
            String[] content = StringUtils.split(source, " ");
            return new ConfigurationPropertiesTests.Person(content[0], content[1]);
        }
    }

    static class GenericPersonConverter implements GenericConverter {
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(String.class, ConfigurationPropertiesTests.Person.class));
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String[] content = StringUtils.split(((String) (source)), " ");
            return new ConfigurationPropertiesTests.Person(content[0], content[1]);
        }
    }

    static class PersonPropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            String[] split = text.split(",");
            setValue(new ConfigurationPropertiesTests.Person(split[1], split[0]));
        }
    }

    static class Person {
        private final String firstName;

        private final String lastName;

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    static class Foo {
        private String a;

        private int b;

        public String getA() {
            return this.a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public int getB() {
            return this.b;
        }

        public void setB(int b) {
            this.b = b;
        }
    }
}

