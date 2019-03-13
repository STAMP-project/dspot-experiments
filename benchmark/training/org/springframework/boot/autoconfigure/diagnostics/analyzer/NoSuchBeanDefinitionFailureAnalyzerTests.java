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
package org.springframework.boot.autoconfigure.diagnostics.analyzer;


import org.junit.Test;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;


/**
 * Tests for {@link NoSuchBeanDefinitionFailureAnalyzer}.
 *
 * @author Stephane Nicoll
 */
public class NoSuchBeanDefinitionFailureAnalyzerTests {
    private final NoSuchBeanDefinitionFailureAnalyzer analyzer = new NoSuchBeanDefinitionFailureAnalyzer();

    @Test
    public void failureAnalysisForMultipleBeans() {
        FailureAnalysis analysis = analyzeFailure(new NoUniqueBeanDefinitionException(String.class, 2, "Test"));
        assertThat(analysis).isNull();
    }

    @Test
    public void failureAnalysisForNoMatchType() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class));
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class, 0, String.class);
        assertThat(analysis.getDescription()).doesNotContain("No matching auto-configuration has been found for this type.");
        assertThat(analysis.getAction()).startsWith(String.format("Consider defining a bean of type '%s' in your configuration.", String.class.getName()));
    }

    @Test
    public void failureAnalysisForMissingPropertyExactType() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.StringPropertyTypeConfiguration.class));
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class, 0, String.class);
        assertBeanMethodDisabled(analysis, "did not find property 'spring.string.enabled'", NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class, "string");
        assertActionMissingType(analysis, String.class);
    }

    @Test
    public void failureAnalysisForMissingPropertySubType() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.IntegerPropertyTypeConfiguration.class));
        assertThat(analysis).isNotNull();
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.NumberHandler.class, 0, Number.class);
        assertBeanMethodDisabled(analysis, "did not find property 'spring.integer.enabled'", NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class, "integer");
        assertActionMissingType(analysis, Number.class);
    }

    @Test
    public void failureAnalysisForMissingClassOnAutoConfigurationType() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.MissingClassOnAutoConfigurationConfiguration.class));
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class, 0, String.class);
        assertClassDisabled(analysis, "did not find required class 'com.example.FooBar'", "string", ClassUtils.getShortName(NoSuchBeanDefinitionFailureAnalyzerTests.TestTypeClassAutoConfiguration.class));
        assertActionMissingType(analysis, String.class);
    }

    @Test
    public void failureAnalysisForExcludedAutoConfigurationType() {
        FatalBeanException failure = createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class);
        NoSuchBeanDefinitionFailureAnalyzerTests.addExclusions(this.analyzer, NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class);
        FailureAnalysis analysis = analyzeFailure(failure);
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class, 0, String.class);
        String configClass = ClassUtils.getShortName(NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class.getName());
        assertClassDisabled(analysis, String.format("auto-configuration '%s' was excluded", configClass), "string", ClassUtils.getShortName(NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class));
        assertActionMissingType(analysis, String.class);
    }

    @Test
    public void failureAnalysisForSeveralConditionsType() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.SeveralAutoConfigurationTypeConfiguration.class));
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class, 0, String.class);
        assertBeanMethodDisabled(analysis, "did not find property 'spring.string.enabled'", NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class, "string");
        assertClassDisabled(analysis, "did not find required class 'com.example.FooBar'", "string", ClassUtils.getShortName(NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class));
        assertActionMissingType(analysis, String.class);
    }

    @Test
    public void failureAnalysisForNoMatchName() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.StringNameHandler.class));
        assertThat(analysis.getDescription()).startsWith(String.format("Constructor in %s required a bean named '%s' that could not be found", NoSuchBeanDefinitionFailureAnalyzerTests.StringNameHandler.class.getName(), "test-string"));
        assertThat(analysis.getAction()).startsWith(String.format("Consider defining a bean named '%s' in your configuration.", "test-string"));
    }

    @Test
    public void failureAnalysisForMissingBeanName() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.StringMissingBeanNameConfiguration.class));
        assertThat(analysis.getDescription()).startsWith(String.format("Constructor in %s required a bean named '%s' that could not be found", NoSuchBeanDefinitionFailureAnalyzerTests.StringNameHandler.class.getName(), "test-string"));
        assertBeanMethodDisabled(analysis, "@ConditionalOnBean (types: java.lang.Integer; SearchStrategy: all) did not find any beans", NoSuchBeanDefinitionFailureAnalyzerTests.TestMissingBeanAutoConfiguration.class, "string");
        assertActionMissingName(analysis, "test-string");
    }

    @Test
    public void failureAnalysisForNullBeanByType() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.StringNullBeanConfiguration.class));
        assertDescriptionConstructorMissingType(analysis, NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class, 0, String.class);
        assertUserDefinedBean(analysis, "as the bean value is null", NoSuchBeanDefinitionFailureAnalyzerTests.TestNullBeanConfiguration.class, "string");
        assertActionMissingType(analysis, String.class);
    }

    @Test
    public void failureAnalysisForUnmatchedQualifier() {
        FailureAnalysis analysis = analyzeFailure(createFailure(NoSuchBeanDefinitionFailureAnalyzerTests.QualifiedBeanConfiguration.class));
        assertThat(analysis.getDescription()).containsPattern("@org.springframework.beans.factory.annotation.Qualifier\\(value=\"*alpha\"*\\)");
    }

    @Configuration
    @ImportAutoConfiguration(NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class)
    @Import(NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class)
    protected static class StringPropertyTypeConfiguration {}

    @Configuration
    @ImportAutoConfiguration(NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class)
    @Import(NoSuchBeanDefinitionFailureAnalyzerTests.NumberHandler.class)
    protected static class IntegerPropertyTypeConfiguration {}

    @Configuration
    @ImportAutoConfiguration(NoSuchBeanDefinitionFailureAnalyzerTests.TestTypeClassAutoConfiguration.class)
    @Import(NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class)
    protected static class MissingClassOnAutoConfigurationConfiguration {}

    @Configuration
    @ImportAutoConfiguration({ NoSuchBeanDefinitionFailureAnalyzerTests.TestPropertyAutoConfiguration.class, NoSuchBeanDefinitionFailureAnalyzerTests.TestTypeClassAutoConfiguration.class })
    @Import(NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class)
    protected static class SeveralAutoConfigurationTypeConfiguration {}

    @Configuration
    @ImportAutoConfiguration(NoSuchBeanDefinitionFailureAnalyzerTests.TestMissingBeanAutoConfiguration.class)
    @Import(NoSuchBeanDefinitionFailureAnalyzerTests.StringNameHandler.class)
    protected static class StringMissingBeanNameConfiguration {}

    @Configuration
    @ImportAutoConfiguration(NoSuchBeanDefinitionFailureAnalyzerTests.TestNullBeanConfiguration.class)
    @Import(NoSuchBeanDefinitionFailureAnalyzerTests.StringHandler.class)
    protected static class StringNullBeanConfiguration {}

    @Configuration
    public static class TestPropertyAutoConfiguration {
        @ConditionalOnProperty("spring.string.enabled")
        @Bean
        public String string() {
            return "Test";
        }

        @ConditionalOnProperty("spring.integer.enabled")
        @Bean
        public Integer integer() {
            return 42;
        }
    }

    @Configuration
    @ConditionalOnClass(name = "com.example.FooBar")
    public static class TestTypeClassAutoConfiguration {
        @Bean
        public String string() {
            return "Test";
        }
    }

    @Configuration
    public static class TestMissingBeanAutoConfiguration {
        @ConditionalOnBean(Integer.class)
        @Bean(name = "test-string")
        public String string() {
            return "Test";
        }
    }

    @Configuration
    public static class TestNullBeanConfiguration {
        @Bean
        public String string() {
            return null;
        }
    }

    @Configuration
    public static class QualifiedBeanConfiguration {
        @Bean
        public String consumer(@Qualifier("alpha")
        NoSuchBeanDefinitionFailureAnalyzerTests.QualifiedBeanConfiguration.Thing thing) {
            return "consumer";
        }

        @Bean
        public NoSuchBeanDefinitionFailureAnalyzerTests.QualifiedBeanConfiguration.Thing producer() {
            return new NoSuchBeanDefinitionFailureAnalyzerTests.QualifiedBeanConfiguration.Thing();
        }

        class Thing {}
    }

    protected static class StringHandler {
        public StringHandler(String foo) {
        }
    }

    protected static class NumberHandler {
        public NumberHandler(Number foo) {
        }
    }

    protected static class StringNameHandler {
        public StringNameHandler(BeanFactory beanFactory) {
            beanFactory.getBean("test-string");
        }
    }
}

