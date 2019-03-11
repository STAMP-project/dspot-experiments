/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common.property;


import junit.framework.TestCase;
import org.junit.Test;


public class PropertyAccessStrategyTest {
    private PropertyAccessStrategy mock1 = new PropertyAccessStrategyTest.StubPropertyAccessStrategy(1000, "mock1");

    private PropertyAccessStrategy mock2 = new PropertyAccessStrategyTest.StubPropertyAccessStrategy(1200, "mock2");

    private PropertyAccessStrategy mock3 = new PropertyAccessStrategyTest.StubPropertyAccessStrategy(1000, "mock3");

    private PropertyAccessStrategy mock4 = new PropertyAccessStrategyTest.StubPropertyAccessStrategy(1000, "mock4");

    private PropertyAccessStrategyTest.TestPropertyAccessStrategy testPropertyAccessStrategy = new PropertyAccessStrategyTest.TestPropertyAccessStrategy();

    @Test
    public void test_BeanPropertyAccess() {
        TestCase.assertEquals("beanProperty", PropertyAccessStrategy.getProperty(PropertyAccessStrategyTest.Bean.class, "beanProperty").getValue(new PropertyAccessStrategyTest.Bean()));
    }

    @Test
    public void test_UniformPropertyAccess() {
        TestCase.assertEquals("uniformProperty", PropertyAccessStrategy.getProperty(PropertyAccessStrategyTest.Bean.class, "uniformProperty").getValue(new PropertyAccessStrategyTest.Bean()));
    }

    @Test
    public void test_Register() {
        PropertyAccessStrategy.register(testPropertyAccessStrategy);
        TestCase.assertEquals("testGetterInvoked", PropertyAccessStrategy.getProperty(PropertyAccessStrategyTest.Bean.class, "testProperty").getValue(new PropertyAccessStrategyTest.Bean()));
    }

    @Test
    public void testInvocationOrdering() {
        PropertyAccessStrategy.register(mock1);
        PropertyAccessStrategy.register(mock2);
        TestCase.assertEquals("mock2", PropertyAccessStrategy.getProperty(PropertyAccessStrategyTest.Bean.class, "testProperty").getValue(new PropertyAccessStrategyTest.Bean()));
    }

    @Test
    public void testInvocationOrdering_EqualPriorityUsesClassName() {
        PropertyAccessStrategy.register(mock3);
        PropertyAccessStrategy.register(mock4);
        TestCase.assertEquals("mock3", PropertyAccessStrategy.getProperty(PropertyAccessStrategyTest.Bean.class, "testProperty").getValue(new PropertyAccessStrategyTest.Bean()));
    }

    static class TestPropertyAccessStrategy extends PropertyAccessStrategy {
        @Override
        protected int getPriority() {
            return Integer.MAX_VALUE;
        }

        @Override
        protected <T> Property<T> propertyFor(Class<? extends T> targetClass, String property) {
            return new PropertyAccessStrategyTest.TestPropertyAccessStrategy.StubProperty("testGetterInvoked");
        }

        private static class StubProperty<T> implements Property<T> {
            private final String value;

            private StubProperty(String value) {
                this.value = value;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <V> V getValue(T target) {
                return ((V) (value));
            }
        }
    }

    static class Bean {
        private String beanProperty = "beanProperty";

        private String uniformProperty = "uniformProperty";

        public String getBeanProperty() {
            return beanProperty;
        }

        public String uniformProperty() {
            return uniformProperty;
        }
    }

    private static class StubPropertyAccessStrategy extends PropertyAccessStrategy {
        private final int priority;

        private final String value;

        public StubPropertyAccessStrategy(int priority, String value) {
            this.priority = priority;
            this.value = value;
        }

        @Override
        protected int getPriority() {
            return priority;
        }

        @Override
        protected <T> Property<T> propertyFor(Class<? extends T> targetClass, String property) {
            return new PropertyAccessStrategyTest.TestPropertyAccessStrategy.StubProperty(value);
        }
    }
}

