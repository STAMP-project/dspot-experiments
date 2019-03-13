/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.config;


import AnnotationConfigUtils.PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME;
import java.util.Arrays;
import java.util.Collections;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Metamodel;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.instrument.classloading.ShadowingClassLoader;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;


/**
 * Unit tests for {@link JpaRepositoryConfigExtension}.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@RunWith(MockitoJUnitRunner.class)
public class JpaRepositoryConfigExtensionUnitTests {
    @Mock
    RepositoryConfigurationSource configSource;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void registersDefaultBeanPostProcessorsByDefault() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        RepositoryConfigurationExtension extension = new JpaRepositoryConfigExtension();
        extension.registerBeansForRoot(factory, configSource);
        Iterable<String> names = Arrays.asList(factory.getBeanDefinitionNames());
        Assert.assertThat(names, CoreMatchers.hasItems(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    @Test
    public void doesNotRegisterProcessorIfAlreadyPresent() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        RootBeanDefinition pabppDefinition = new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class);
        String beanName = BeanDefinitionReaderUtils.generateBeanName(pabppDefinition, factory);
        factory.registerBeanDefinition(beanName, pabppDefinition);
        assertOnlyOnePersistenceAnnotationBeanPostProcessorRegistered(factory, beanName);
    }

    @Test
    public void doesNotRegisterProcessorIfAutoRegistered() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        RootBeanDefinition pabppDefinition = new RootBeanDefinition(PersistenceAnnotationBeanPostProcessor.class);
        String beanName = AnnotationConfigUtils.PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME;
        factory.registerBeanDefinition(beanName, pabppDefinition);
        assertOnlyOnePersistenceAnnotationBeanPostProcessorRegistered(factory, beanName);
    }

    // DATAJPA-525
    @Test
    public void guardsAgainstNullJavaTypesReturnedFromJpaMetamodel() throws Exception {
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        EntityManagerFactory emf = Mockito.mock(EntityManagerFactory.class);
        Metamodel metamodel = Mockito.mock(Metamodel.class);
        Mockito.when(context.getBeansOfType(EntityManagerFactory.class)).thenReturn(Collections.singletonMap("emf", emf));
        Mockito.when(emf.getMetamodel()).thenReturn(metamodel);
        JpaMetamodelMappingContextFactoryBean factoryBean = new JpaMetamodelMappingContextFactoryBean();
        factoryBean.setApplicationContext(context);
        factoryBean.createInstance().afterPropertiesSet();
    }

    // DATAJPA-1250
    @Test
    public void shouldUseInspectionClassLoader() {
        JpaRepositoryConfigExtension extension = new JpaRepositoryConfigExtension();
        ClassLoader classLoader = extension.getConfigurationInspectionClassLoader(new GenericApplicationContext());
        Assert.assertThat(classLoader).isInstanceOf(InspectionClassLoader.class);
    }

    // DATAJPA-1250
    @Test
    public void shouldNotUseInspectionClassLoaderWithoutEclipseLink() {
        ShadowingClassLoader shadowingClassLoader = new ShadowingClassLoader(getClass().getClassLoader(), false) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if ((name.startsWith("org.springframework.instrument.")) || (name.startsWith("org.eclipse."))) {
                    throw new ClassNotFoundException(("Excluded: " + name));
                }
                return getClass().getClassLoader().loadClass(name);
            }
        };
        GenericApplicationContext context = new GenericApplicationContext();
        context.setClassLoader(shadowingClassLoader);
        JpaRepositoryConfigExtension extension = new JpaRepositoryConfigExtension();
        ClassLoader classLoader = extension.getConfigurationInspectionClassLoader(context);
        Assert.assertThat(classLoader).isNotInstanceOf(InspectionClassLoader.class);
    }
}

