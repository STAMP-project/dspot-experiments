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
package org.springframework.boot.diagnostics.analyzer;


import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.analyzer.nounique.TestBean;
import org.springframework.boot.diagnostics.analyzer.nounique.TestBeanConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


/**
 * Tests for {@link NoUniqueBeanDefinitionFailureAnalyzer}.
 *
 * @author Andy Wilkinson
 */
public class NoUniqueBeanDefinitionFailureAnalyzerTests {
    private final NoUniqueBeanDefinitionFailureAnalyzer analyzer = new NoUniqueBeanDefinitionFailureAnalyzer();

    @Test
    public void failureAnalysisForFieldConsumer() {
        FailureAnalysis failureAnalysis = analyzeFailure(createFailure(NoUniqueBeanDefinitionFailureAnalyzerTests.FieldConsumer.class));
        assertThat(failureAnalysis.getDescription()).startsWith((("Field testBean in " + (NoUniqueBeanDefinitionFailureAnalyzerTests.FieldConsumer.class.getName())) + " required a single bean, but 6 were found:"));
        assertFoundBeans(failureAnalysis);
    }

    @Test
    public void failureAnalysisForMethodConsumer() {
        FailureAnalysis failureAnalysis = analyzeFailure(createFailure(NoUniqueBeanDefinitionFailureAnalyzerTests.MethodConsumer.class));
        assertThat(failureAnalysis.getDescription()).startsWith((("Parameter 0 of method consumer in " + (NoUniqueBeanDefinitionFailureAnalyzerTests.MethodConsumer.class.getName())) + " required a single bean, but 6 were found:"));
        assertFoundBeans(failureAnalysis);
    }

    @Test
    public void failureAnalysisForConstructorConsumer() {
        FailureAnalysis failureAnalysis = analyzeFailure(createFailure(NoUniqueBeanDefinitionFailureAnalyzerTests.ConstructorConsumer.class));
        assertThat(failureAnalysis.getDescription()).startsWith((("Parameter 0 of constructor in " + (NoUniqueBeanDefinitionFailureAnalyzerTests.ConstructorConsumer.class.getName())) + " required a single bean, but 6 were found:"));
        assertFoundBeans(failureAnalysis);
    }

    @Test
    public void failureAnalysisForObjectProviderMethodConsumer() {
        FailureAnalysis failureAnalysis = analyzeFailure(createFailure(NoUniqueBeanDefinitionFailureAnalyzerTests.ObjectProviderMethodConsumer.class));
        assertThat(failureAnalysis.getDescription()).startsWith((("Method consumer in " + (NoUniqueBeanDefinitionFailureAnalyzerTests.ObjectProviderMethodConsumer.class.getName())) + " required a single bean, but 6 were found:"));
        assertFoundBeans(failureAnalysis);
    }

    @Test
    public void failureAnalysisForXmlConsumer() {
        FailureAnalysis failureAnalysis = analyzeFailure(createFailure(NoUniqueBeanDefinitionFailureAnalyzerTests.XmlConsumer.class));
        assertThat(failureAnalysis.getDescription()).startsWith((("Parameter 0 of constructor in " + (TestBeanConsumer.class.getName())) + " required a single bean, but 6 were found:"));
        assertFoundBeans(failureAnalysis);
    }

    @Test
    public void failureAnalysisForObjectProviderConstructorConsumer() {
        FailureAnalysis failureAnalysis = analyzeFailure(createFailure(NoUniqueBeanDefinitionFailureAnalyzerTests.ObjectProviderConstructorConsumer.class));
        assertThat(failureAnalysis.getDescription()).startsWith((("Constructor in " + (NoUniqueBeanDefinitionFailureAnalyzerTests.ObjectProviderConstructorConsumer.class.getName())) + " required a single bean, but 6 were found:"));
        assertFoundBeans(failureAnalysis);
    }

    @Configuration
    @ComponentScan(basePackageClasses = TestBean.class)
    @ImportResource("/org/springframework/boot/diagnostics/analyzer/nounique/producer.xml")
    static class DuplicateBeansProducer {
        @Bean
        TestBean beanOne() {
            return new TestBean();
        }

        @Bean
        TestBean beanTwo() {
            return new TestBean();
        }
    }

    @Configuration
    static class ParentProducer {
        @Bean
        TestBean beanThree() {
            return new TestBean();
        }
    }

    @Configuration
    static class FieldConsumer {
        @SuppressWarnings("unused")
        @Autowired
        private TestBean testBean;
    }

    @Configuration
    static class ObjectProviderConstructorConsumer {
        ObjectProviderConstructorConsumer(ObjectProvider<TestBean> objectProvider) {
            objectProvider.getIfAvailable();
        }
    }

    @Configuration
    static class ConstructorConsumer {
        ConstructorConsumer(TestBean testBean) {
        }
    }

    @Configuration
    static class MethodConsumer {
        @Bean
        String consumer(TestBean testBean) {
            return "foo";
        }
    }

    @Configuration
    static class ObjectProviderMethodConsumer {
        @Bean
        String consumer(ObjectProvider<TestBean> testBeanProvider) {
            testBeanProvider.getIfAvailable();
            return "foo";
        }
    }

    @Configuration
    @ImportResource("/org/springframework/boot/diagnostics/analyzer/nounique/consumer.xml")
    static class XmlConsumer {}
}

