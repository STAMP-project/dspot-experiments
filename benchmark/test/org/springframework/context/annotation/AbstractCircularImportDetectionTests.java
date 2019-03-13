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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;


/**
 * TCK-style unit tests for handling circular use of the {@link Import} annotation.
 * Explore the subclass hierarchy for specific concrete implementations.
 *
 * @author Chris Beams
 */
public abstract class AbstractCircularImportDetectionTests {
    @Test
    public void simpleCircularImportIsDetected() throws Exception {
        boolean threw = false;
        try {
            newParser().parse(loadAsConfigurationSource(AbstractCircularImportDetectionTests.A.class), "A");
        } catch (BeanDefinitionParsingException ex) {
            Assert.assertTrue(("Wrong message. Got: " + (ex.getMessage())), ex.getMessage().contains(("Illegal attempt by @Configuration class 'AbstractCircularImportDetectionTests.B' " + "to import class 'AbstractCircularImportDetectionTests.A'")));
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void complexCircularImportIsDetected() throws Exception {
        boolean threw = false;
        try {
            newParser().parse(loadAsConfigurationSource(AbstractCircularImportDetectionTests.X.class), "X");
        } catch (BeanDefinitionParsingException ex) {
            Assert.assertTrue(("Wrong message. Got: " + (ex.getMessage())), ex.getMessage().contains(("Illegal attempt by @Configuration class 'AbstractCircularImportDetectionTests.Z2' " + "to import class 'AbstractCircularImportDetectionTests.Z'")));
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Configuration
    @Import(AbstractCircularImportDetectionTests.B.class)
    static class A {
        @Bean
        TestBean b1() {
            return new TestBean();
        }
    }

    @Configuration
    @Import(AbstractCircularImportDetectionTests.A.class)
    static class B {
        @Bean
        TestBean b2() {
            return new TestBean();
        }
    }

    @Configuration
    @Import({ AbstractCircularImportDetectionTests.Y.class, AbstractCircularImportDetectionTests.Z.class })
    class X {
        @Bean
        TestBean x() {
            return new TestBean();
        }
    }

    @Configuration
    class Y {
        @Bean
        TestBean y() {
            return new TestBean();
        }
    }

    @Configuration
    @Import({ AbstractCircularImportDetectionTests.Z1.class, AbstractCircularImportDetectionTests.Z2.class })
    class Z {
        @Bean
        TestBean z() {
            return new TestBean();
        }
    }

    @Configuration
    class Z1 {
        @Bean
        TestBean z1() {
            return new TestBean();
        }
    }

    @Configuration
    @Import(AbstractCircularImportDetectionTests.Z.class)
    class Z2 {
        @Bean
        TestBean z2() {
            return new TestBean();
        }
    }
}

