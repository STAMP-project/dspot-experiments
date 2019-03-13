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
package org.springframework.core.annotation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.core.Ordered;

import static AnnotationAwareOrderComparator.INSTANCE;


/**
 *
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 */
public class OrderSourceProviderTests {
    private final AnnotationAwareOrderComparator comparator = INSTANCE;

    @Test
    public void plainComparator() {
        List<Object> items = new ArrayList<>();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C(5);
        OrderSourceProviderTests.C c2 = new OrderSourceProviderTests.C((-5));
        items.add(c);
        items.add(c2);
        Collections.sort(items, comparator);
        assertOrder(items, c2, c);
    }

    @Test
    public void listNoFactoryMethod() {
        OrderSourceProviderTests.A a = new OrderSourceProviderTests.A();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C((-50));
        OrderSourceProviderTests.B b = new OrderSourceProviderTests.B();
        List<?> items = Arrays.asList(a, c, b);
        Collections.sort(items, comparator.withSourceProvider(( obj) -> null));
        assertOrder(items, c, a, b);
    }

    @Test
    public void listFactoryMethod() {
        OrderSourceProviderTests.A a = new OrderSourceProviderTests.A();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C(3);
        OrderSourceProviderTests.B b = new OrderSourceProviderTests.B();
        List<?> items = Arrays.asList(a, c, b);
        Collections.sort(items, comparator.withSourceProvider(( obj) -> {
            if (obj == a) {
                return new org.springframework.core.annotation.C(4);
            }
            if (obj == b) {
                return new org.springframework.core.annotation.C(2);
            }
            return null;
        }));
        assertOrder(items, b, c, a);
    }

    @Test
    public void listFactoryMethodOverridesStaticOrder() {
        OrderSourceProviderTests.A a = new OrderSourceProviderTests.A();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C(5);
        OrderSourceProviderTests.C c2 = new OrderSourceProviderTests.C((-5));
        List<?> items = Arrays.asList(a, c, c2);
        Collections.sort(items, comparator.withSourceProvider(( obj) -> {
            if (obj == a) {
                return 4;
            }
            if (obj == c2) {
                return 2;
            }
            return null;
        }));
        assertOrder(items, c2, a, c);
    }

    @Test
    public void arrayNoFactoryMethod() {
        OrderSourceProviderTests.A a = new OrderSourceProviderTests.A();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C((-50));
        OrderSourceProviderTests.B b = new OrderSourceProviderTests.B();
        Object[] items = new Object[]{ a, c, b };
        Arrays.sort(items, comparator.withSourceProvider(( obj) -> null));
        assertOrder(items, c, a, b);
    }

    @Test
    public void arrayFactoryMethod() {
        OrderSourceProviderTests.A a = new OrderSourceProviderTests.A();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C(3);
        OrderSourceProviderTests.B b = new OrderSourceProviderTests.B();
        Object[] items = new Object[]{ a, c, b };
        Arrays.sort(items, comparator.withSourceProvider(( obj) -> {
            if (obj == a) {
                return new org.springframework.core.annotation.C(4);
            }
            if (obj == b) {
                return new org.springframework.core.annotation.C(2);
            }
            return null;
        }));
        assertOrder(items, b, c, a);
    }

    @Test
    public void arrayFactoryMethodOverridesStaticOrder() {
        OrderSourceProviderTests.A a = new OrderSourceProviderTests.A();
        OrderSourceProviderTests.C c = new OrderSourceProviderTests.C(5);
        OrderSourceProviderTests.C c2 = new OrderSourceProviderTests.C((-5));
        Object[] items = new Object[]{ a, c, c2 };
        Arrays.sort(items, comparator.withSourceProvider(( obj) -> {
            if (obj == a) {
                return 4;
            }
            if (obj == c2) {
                return 2;
            }
            return null;
        }));
        assertOrder(items, c2, a, c);
    }

    @Order(1)
    private static class A {}

    @Order(2)
    private static class B {}

    private static class C implements Ordered {
        private final int order;

        private C(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return order;
        }
    }
}

