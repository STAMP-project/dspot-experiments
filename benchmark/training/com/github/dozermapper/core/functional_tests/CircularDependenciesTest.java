/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;


import com.github.dozermapper.core.Mapper;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CircularDependenciesTest extends AbstractFunctionalTest {
    private Mapper mapper;

    @Test
    public void shouldHandleCircularDependencies() {
        CircularDependenciesTest.A a = newInstance(CircularDependenciesTest.A.class);
        CircularDependenciesTest.B b = newInstance(CircularDependenciesTest.B.class);
        CircularDependenciesTest.C c = newInstance(CircularDependenciesTest.C.class);
        a.setC(c);
        b.setA(a);
        c.setB(b);
        CircularDependenciesTest.A result = mapper.map(a, CircularDependenciesTest.A.class);
        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(result.getC(), CoreMatchers.notNullValue());
        Assert.assertThat(result.getC().getB(), CoreMatchers.notNullValue());
        Assert.assertThat(result.getC().getB().getA(), CoreMatchers.notNullValue());
        Assert.assertThat(result.getC().getB().getA(), CoreMatchers.equalTo(result));
    }

    public static class A {
        CircularDependenciesTest.C c;

        public CircularDependenciesTest.C getC() {
            return c;
        }

        public void setC(CircularDependenciesTest.C c) {
            this.c = c;
        }
    }

    public static class B {
        CircularDependenciesTest.A a;

        public CircularDependenciesTest.A getA() {
            return a;
        }

        public void setA(CircularDependenciesTest.A a) {
            this.a = a;
        }
    }

    public static class C {
        CircularDependenciesTest.B b;

        public CircularDependenciesTest.B getB() {
            return b;
        }

        public void setB(CircularDependenciesTest.B b) {
            this.b = b;
        }
    }
}

