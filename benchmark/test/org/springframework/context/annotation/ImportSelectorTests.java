/**
 * Copyright 2002-2018 the original author or authors.
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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;


/**
 * Tests for {@link ImportSelector} and {@link DeferredImportSelector}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
@SuppressWarnings("resource")
public class ImportSelectorTests {
    static Map<Class<?>, String> importFrom = new HashMap<>();

    @Test
    public void importSelectors() {
        DefaultListableBeanFactory beanFactory = Mockito.spy(new DefaultListableBeanFactory());
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(beanFactory);
        context.register(ImportSelectorTests.Config.class);
        context.refresh();
        context.getBean(ImportSelectorTests.Config.class);
        InOrder ordered = Mockito.inOrder(beanFactory);
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("a"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("b"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("d"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("c"), ArgumentMatchers.any());
    }

    @Test
    public void invokeAwareMethodsInImportSelector() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportSelectorTests.AwareConfig.class);
        Assert.assertThat(ImportSelectorTests.SampleImportSelector.beanFactory, is(context.getBeanFactory()));
        Assert.assertThat(ImportSelectorTests.SampleImportSelector.classLoader, is(context.getBeanFactory().getBeanClassLoader()));
        Assert.assertThat(ImportSelectorTests.SampleImportSelector.resourceLoader, is(notNullValue()));
        Assert.assertThat(ImportSelectorTests.SampleImportSelector.environment, is(context.getEnvironment()));
    }

    @Test
    public void correctMetaDataOnIndirectImports() {
        new AnnotationConfigApplicationContext(ImportSelectorTests.IndirectConfig.class);
        Matcher<String> isFromIndirect = equalTo(ImportSelectorTests.IndirectImport.class.getName());
        Assert.assertThat(ImportSelectorTests.importFrom.get(ImportSelectorTests.ImportSelector1.class), isFromIndirect);
        Assert.assertThat(ImportSelectorTests.importFrom.get(ImportSelectorTests.ImportSelector2.class), isFromIndirect);
        Assert.assertThat(ImportSelectorTests.importFrom.get(ImportSelectorTests.DeferredImportSelector1.class), isFromIndirect);
        Assert.assertThat(ImportSelectorTests.importFrom.get(ImportSelectorTests.DeferredImportSelector2.class), isFromIndirect);
    }

    @Test
    public void importSelectorsWithGroup() {
        DefaultListableBeanFactory beanFactory = Mockito.spy(new DefaultListableBeanFactory());
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(beanFactory);
        context.register(ImportSelectorTests.GroupedConfig.class);
        context.refresh();
        InOrder ordered = Mockito.inOrder(beanFactory);
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("a"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("b"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("c"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("d"), ArgumentMatchers.any());
        Assert.assertThat(ImportSelectorTests.TestImportGroup.instancesCount.get(), equalTo(1));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.imports.size(), equalTo(1));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.imports.values().iterator().next().size(), equalTo(2));
    }

    @Test
    public void importSelectorsSeparateWithGroup() {
        DefaultListableBeanFactory beanFactory = Mockito.spy(new DefaultListableBeanFactory());
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(beanFactory);
        context.register(ImportSelectorTests.GroupedConfig1.class);
        context.register(ImportSelectorTests.GroupedConfig2.class);
        context.refresh();
        InOrder ordered = Mockito.inOrder(beanFactory);
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("c"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("d"), ArgumentMatchers.any());
        Assert.assertThat(ImportSelectorTests.TestImportGroup.instancesCount.get(), equalTo(1));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.imports.size(), equalTo(2));
        Iterator<AnnotationMetadata> iterator = ImportSelectorTests.TestImportGroup.imports.keySet().iterator();
        Assert.assertThat(iterator.next().getClassName(), equalTo(ImportSelectorTests.GroupedConfig2.class.getName()));
        Assert.assertThat(iterator.next().getClassName(), equalTo(ImportSelectorTests.GroupedConfig1.class.getName()));
    }

    @Test
    public void importSelectorsWithNestedGroup() {
        DefaultListableBeanFactory beanFactory = Mockito.spy(new DefaultListableBeanFactory());
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(beanFactory);
        context.register(ImportSelectorTests.ParentConfiguration1.class);
        context.refresh();
        InOrder ordered = Mockito.inOrder(beanFactory);
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("a"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("e"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("c"), ArgumentMatchers.any());
        Assert.assertThat(ImportSelectorTests.TestImportGroup.instancesCount.get(), equalTo(2));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.imports.size(), equalTo(2));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.allImports(), hasEntry(is(ImportSelectorTests.ParentConfiguration1.class.getName()), IsIterableContainingInOrder.contains(ImportSelectorTests.DeferredImportSelector1.class.getName(), ImportSelectorTests.ChildConfiguration1.class.getName())));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.allImports(), hasEntry(is(ImportSelectorTests.ChildConfiguration1.class.getName()), IsIterableContainingInOrder.contains(ImportSelectorTests.DeferredImportedSelector3.class.getName())));
    }

    @Test
    public void importSelectorsWithNestedGroupSameDeferredImport() {
        DefaultListableBeanFactory beanFactory = Mockito.spy(new DefaultListableBeanFactory());
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(beanFactory);
        context.register(ImportSelectorTests.ParentConfiguration2.class);
        context.refresh();
        InOrder ordered = Mockito.inOrder(beanFactory);
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("b"), ArgumentMatchers.any());
        ordered.verify(beanFactory).registerBeanDefinition(ArgumentMatchers.eq("d"), ArgumentMatchers.any());
        Assert.assertThat(ImportSelectorTests.TestImportGroup.instancesCount.get(), equalTo(2));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.allImports().size(), equalTo(2));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.allImports(), hasEntry(is(ImportSelectorTests.ParentConfiguration2.class.getName()), IsIterableContainingInOrder.contains(ImportSelectorTests.DeferredImportSelector2.class.getName(), ImportSelectorTests.ChildConfiguration2.class.getName())));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.allImports(), hasEntry(is(ImportSelectorTests.ChildConfiguration2.class.getName()), IsIterableContainingInOrder.contains(ImportSelectorTests.DeferredImportSelector2.class.getName())));
    }

    @Test
    public void invokeAwareMethodsInImportGroup() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImportSelectorTests.GroupedConfig1.class);
        Assert.assertThat(ImportSelectorTests.TestImportGroup.beanFactory, is(context.getBeanFactory()));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.classLoader, is(context.getBeanFactory().getBeanClassLoader()));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.resourceLoader, is(notNullValue()));
        Assert.assertThat(ImportSelectorTests.TestImportGroup.environment, is(context.getEnvironment()));
    }

    @Configuration
    @Import(ImportSelectorTests.SampleImportSelector.class)
    static class AwareConfig {}

    private static class SampleImportSelector implements BeanClassLoaderAware , BeanFactoryAware , EnvironmentAware , ResourceLoaderAware , ImportSelector {
        static ClassLoader classLoader;

        static ResourceLoader resourceLoader;

        static BeanFactory beanFactory;

        static Environment environment;

        static void cleanup() {
            ImportSelectorTests.SampleImportSelector.classLoader = null;
            ImportSelectorTests.SampleImportSelector.beanFactory = null;
            ImportSelectorTests.SampleImportSelector.resourceLoader = null;
            ImportSelectorTests.SampleImportSelector.environment = null;
        }

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            ImportSelectorTests.SampleImportSelector.classLoader = classLoader;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            ImportSelectorTests.SampleImportSelector.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            ImportSelectorTests.SampleImportSelector.resourceLoader = resourceLoader;
        }

        @Override
        public void setEnvironment(Environment environment) {
            ImportSelectorTests.SampleImportSelector.environment = environment;
        }

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{  };
        }
    }

    @ImportSelectorTests.Sample
    @Configuration
    static class Config {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import({ ImportSelectorTests.DeferredImportSelector1.class, ImportSelectorTests.DeferredImportSelector2.class, ImportSelectorTests.ImportSelector1.class, ImportSelectorTests.ImportSelector2.class })
    public @interface Sample {}

    public static class ImportSelector1 implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.ImportedSelector1.class.getName() };
        }
    }

    public static class ImportSelector2 implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.ImportedSelector2.class.getName() };
        }
    }

    public static class DeferredImportSelector1 implements DeferredImportSelector , Ordered {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.DeferredImportedSelector1.class.getName() };
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class DeferredImportSelector2 implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.DeferredImportedSelector2.class.getName() };
        }
    }

    @Configuration
    public static class ImportedSelector1 {
        @Bean
        public String a() {
            return "a";
        }
    }

    @Configuration
    public static class ImportedSelector2 {
        @Bean
        public String b() {
            return "b";
        }
    }

    @Configuration
    public static class DeferredImportedSelector1 {
        @Bean
        public String c() {
            return "c";
        }
    }

    @Configuration
    public static class DeferredImportedSelector2 {
        @Bean
        public String d() {
            return "d";
        }
    }

    @Configuration
    public static class DeferredImportedSelector3 {
        @Bean
        public String e() {
            return "e";
        }
    }

    @Configuration
    @Import(ImportSelectorTests.IndirectImportSelector.class)
    public static class IndirectConfig {}

    public static class IndirectImportSelector implements ImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{ ImportSelectorTests.IndirectImport.class.getName() };
        }
    }

    @ImportSelectorTests.Sample
    public static class IndirectImport {}

    @ImportSelectorTests.GroupedSample
    @Configuration
    static class GroupedConfig {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import({ ImportSelectorTests.GroupedDeferredImportSelector1.class, ImportSelectorTests.GroupedDeferredImportSelector2.class, ImportSelectorTests.ImportSelector1.class, ImportSelectorTests.ImportSelector2.class })
    public @interface GroupedSample {}

    @Configuration
    @Import(ImportSelectorTests.GroupedDeferredImportSelector1.class)
    static class GroupedConfig1 {}

    @Configuration
    @Import(ImportSelectorTests.GroupedDeferredImportSelector2.class)
    static class GroupedConfig2 {}

    public static class GroupedDeferredImportSelector1 extends ImportSelectorTests.DeferredImportSelector1 {
        @Nullable
        @Override
        public Class<? extends Group> getImportGroup() {
            return ImportSelectorTests.TestImportGroup.class;
        }
    }

    public static class GroupedDeferredImportSelector2 extends ImportSelectorTests.DeferredImportSelector2 {
        @Nullable
        @Override
        public Class<? extends Group> getImportGroup() {
            return ImportSelectorTests.TestImportGroup.class;
        }
    }

    @Configuration
    @Import({ ImportSelectorTests.ImportSelector1.class, ImportSelectorTests.ParentDeferredImportSelector1.class })
    public static class ParentConfiguration1 {}

    public static class ParentDeferredImportSelector1 implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.DeferredImportSelector1.class.getName(), ImportSelectorTests.ChildConfiguration1.class.getName() };
        }

        @Nullable
        @Override
        public Class<? extends DeferredImportSelector.Group> getImportGroup() {
            return ImportSelectorTests.TestImportGroup.class;
        }
    }

    @Configuration
    @Import({ ImportSelectorTests.ImportSelector2.class, ImportSelectorTests.ParentDeferredImportSelector2.class })
    public static class ParentConfiguration2 {}

    public static class ParentDeferredImportSelector2 implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.DeferredImportSelector2.class.getName(), ImportSelectorTests.ChildConfiguration2.class.getName() };
        }

        @Nullable
        @Override
        public Class<? extends DeferredImportSelector.Group> getImportGroup() {
            return ImportSelectorTests.TestImportGroup.class;
        }
    }

    @Configuration
    @Import(ImportSelectorTests.ChildDeferredImportSelector1.class)
    public static class ChildConfiguration1 {}

    public static class ChildDeferredImportSelector1 implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.DeferredImportedSelector3.class.getName() };
        }

        @Nullable
        @Override
        public Class<? extends DeferredImportSelector.Group> getImportGroup() {
            return ImportSelectorTests.TestImportGroup.class;
        }
    }

    @Configuration
    @Import(ImportSelectorTests.ChildDeferredImportSelector2.class)
    public static class ChildConfiguration2 {}

    public static class ChildDeferredImportSelector2 implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            ImportSelectorTests.importFrom.put(getClass(), importingClassMetadata.getClassName());
            return new String[]{ ImportSelectorTests.DeferredImportSelector2.class.getName() };
        }

        @Nullable
        @Override
        public Class<? extends DeferredImportSelector.Group> getImportGroup() {
            return ImportSelectorTests.TestImportGroup.class;
        }
    }

    public static class TestImportGroup implements BeanClassLoaderAware , BeanFactoryAware , EnvironmentAware , ResourceLoaderAware , DeferredImportSelector.Group {
        static ClassLoader classLoader;

        static ResourceLoader resourceLoader;

        static BeanFactory beanFactory;

        static Environment environment;

        static AtomicInteger instancesCount = new AtomicInteger();

        static MultiValueMap<AnnotationMetadata, String> imports = new org.springframework.util.LinkedMultiValueMap();

        public TestImportGroup() {
            ImportSelectorTests.TestImportGroup.instancesCount.incrementAndGet();
        }

        static void cleanup() {
            ImportSelectorTests.TestImportGroup.classLoader = null;
            ImportSelectorTests.TestImportGroup.beanFactory = null;
            ImportSelectorTests.TestImportGroup.resourceLoader = null;
            ImportSelectorTests.TestImportGroup.environment = null;
            ImportSelectorTests.TestImportGroup.instancesCount = new AtomicInteger();
            ImportSelectorTests.TestImportGroup.imports.clear();
        }

        static Map<String, List<String>> allImports() {
            return ImportSelectorTests.TestImportGroup.imports.entrySet().stream().collect(Collectors.toMap(( entry) -> entry.getKey().getClassName(), Map.Entry::getValue));
        }

        private final List<Entry> instanceImports = new ArrayList<>();

        @Override
        public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
            for (String importClassName : selector.selectImports(metadata)) {
                this.instanceImports.add(new Entry(metadata, importClassName));
            }
            ImportSelectorTests.TestImportGroup.imports.addAll(metadata, Arrays.asList(selector.selectImports(metadata)));
        }

        @Override
        public Iterable<Entry> selectImports() {
            LinkedList<Entry> content = new LinkedList(this.instanceImports);
            Collections.reverse(content);
            return content;
        }

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            ImportSelectorTests.TestImportGroup.classLoader = classLoader;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            ImportSelectorTests.TestImportGroup.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            ImportSelectorTests.TestImportGroup.resourceLoader = resourceLoader;
        }

        @Override
        public void setEnvironment(Environment environment) {
            ImportSelectorTests.TestImportGroup.environment = environment;
        }
    }
}

