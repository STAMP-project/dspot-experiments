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


import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link BeanCurrentlyInCreationFailureAnalyzer}.
 *
 * @author Andy Wilkinson
 */
public class BeanCurrentlyInCreationFailureAnalyzerTests {
    private final FailureAnalyzer analyzer = new BeanCurrentlyInCreationFailureAnalyzer();

    @Test
    public void cyclicBeanMethods() throws IOException {
        FailureAnalysis analysis = performAnalysis(BeanCurrentlyInCreationFailureAnalyzerTests.CyclicBeanMethodsConfiguration.class);
        List<String> lines = readDescriptionLines(analysis);
        assertThat(lines).hasSize(9);
        assertThat(lines.get(0)).isEqualTo("The dependencies of some of the beans in the application context form a cycle:");
        assertThat(lines.get(1)).isEqualTo("");
        assertThat(lines.get(2)).isEqualTo("???????");
        assertThat(lines.get(3)).startsWith(("|  one defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CyclicBeanMethodsConfiguration.InnerConfiguration.InnerInnerConfiguration.class.getName())));
        assertThat(lines.get(4)).isEqualTo("?     ?");
        assertThat(lines.get(5)).startsWith(("|  two defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CyclicBeanMethodsConfiguration.InnerConfiguration.class.getName())));
        assertThat(lines.get(6)).isEqualTo("?     ?");
        assertThat(lines.get(7)).startsWith(("|  three defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CyclicBeanMethodsConfiguration.class.getName())));
        assertThat(lines.get(8)).isEqualTo("???????");
    }

    @Test
    public void cycleWithAutowiredFields() throws IOException {
        FailureAnalysis analysis = performAnalysis(BeanCurrentlyInCreationFailureAnalyzerTests.CycleWithAutowiredFields.class);
        assertThat(analysis.getDescription()).startsWith("The dependencies of some of the beans in the application context form a cycle:");
        List<String> lines = readDescriptionLines(analysis);
        assertThat(lines).hasSize(9);
        assertThat(lines.get(0)).isEqualTo("The dependencies of some of the beans in the application context form a cycle:");
        assertThat(lines.get(1)).isEqualTo("");
        assertThat(lines.get(2)).isEqualTo("???????");
        assertThat(lines.get(3)).startsWith(("|  three defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CycleWithAutowiredFields.BeanThreeConfiguration.class.getName())));
        assertThat(lines.get(4)).isEqualTo("?     ?");
        assertThat(lines.get(5)).startsWith(("|  one defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CycleWithAutowiredFields.class.getName())));
        assertThat(lines.get(6)).isEqualTo("?     ?");
        assertThat(lines.get(7)).startsWith(((("|  " + (BeanCurrentlyInCreationFailureAnalyzerTests.CycleWithAutowiredFields.BeanTwoConfiguration.class.getName())) + " (field private ") + (BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree.class.getName())));
        assertThat(lines.get(8)).isEqualTo("???????");
    }

    @Test
    public void cycleReferencedViaOtherBeans() throws IOException {
        FailureAnalysis analysis = performAnalysis(BeanCurrentlyInCreationFailureAnalyzerTests.CycleReferencedViaOtherBeansConfiguration.class);
        List<String> lines = readDescriptionLines(analysis);
        assertThat(lines).hasSize(12);
        assertThat(lines.get(0)).isEqualTo("The dependencies of some of the beans in the application context form a cycle:");
        assertThat(lines.get(1)).isEqualTo("");
        assertThat(lines.get(2)).contains((("refererOne " + "(field ") + (BeanCurrentlyInCreationFailureAnalyzerTests.RefererTwo.class.getName())));
        assertThat(lines.get(3)).isEqualTo("      ?");
        assertThat(lines.get(4)).contains((("refererTwo " + "(field ") + (BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne.class.getName())));
        assertThat(lines.get(5)).isEqualTo("???????");
        assertThat(lines.get(6)).startsWith(("|  one defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CycleReferencedViaOtherBeansConfiguration.class.getName())));
        assertThat(lines.get(7)).isEqualTo("?     ?");
        assertThat(lines.get(8)).startsWith(("|  two defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CycleReferencedViaOtherBeansConfiguration.class.getName())));
        assertThat(lines.get(9)).isEqualTo("?     ?");
        assertThat(lines.get(10)).startsWith(("|  three defined in " + (BeanCurrentlyInCreationFailureAnalyzerTests.CycleReferencedViaOtherBeansConfiguration.class.getName())));
        assertThat(lines.get(11)).isEqualTo("???????");
    }

    @Test
    public void cycleWithAnUnknownStartIsNotAnalyzed() {
        assertThat(this.analyzer.analyze(new BeanCurrentlyInCreationException("test"))).isNull();
    }

    @Configuration
    static class CyclicBeanMethodsConfiguration {
        @Bean
        public BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree three(BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne one) {
            return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree();
        }

        @Configuration
        static class InnerConfiguration {
            @Bean
            public BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo two(BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree three) {
                return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo();
            }

            @Configuration
            static class InnerInnerConfiguration {
                @Bean
                public BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne one(BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo two) {
                    return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne();
                }
            }
        }
    }

    @Configuration
    static class CycleReferencedViaOtherBeansConfiguration {
        @Bean
        public BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne one(BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo two) {
            return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne();
        }

        @Bean
        public BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo two(BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree three) {
            return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo();
        }

        @Bean
        public BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree three(BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne beanOne) {
            return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree();
        }

        @Configuration
        static class InnerConfiguration {
            @Bean
            public BeanCurrentlyInCreationFailureAnalyzerTests.RefererTwo refererTwo() {
                return new BeanCurrentlyInCreationFailureAnalyzerTests.RefererTwo();
            }

            @Configuration
            static class InnerInnerConfiguration {
                @Bean
                public BeanCurrentlyInCreationFailureAnalyzerTests.RefererOne refererOne() {
                    return new BeanCurrentlyInCreationFailureAnalyzerTests.RefererOne();
                }
            }
        }
    }

    @Configuration
    public static class CycleWithAutowiredFields {
        @Bean
        public BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne one(BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo two) {
            return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne();
        }

        @Configuration
        public static class BeanTwoConfiguration {
            @SuppressWarnings("unused")
            @Autowired
            private BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree three;

            @Bean
            public BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo two() {
                return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanTwo();
            }
        }

        @Configuration
        public static class BeanThreeConfiguration {
            @Bean
            public BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree three(BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne one) {
                return new BeanCurrentlyInCreationFailureAnalyzerTests.BeanThree();
            }
        }
    }

    static class RefererOne {
        @Autowired
        BeanCurrentlyInCreationFailureAnalyzerTests.RefererTwo refererTwo;
    }

    static class RefererTwo {
        @Autowired
        BeanCurrentlyInCreationFailureAnalyzerTests.BeanOne beanOne;
    }

    static class BeanOne {}

    static class BeanTwo {}

    static class BeanThree {}
}

