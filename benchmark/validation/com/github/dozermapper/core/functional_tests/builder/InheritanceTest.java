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
package com.github.dozermapper.core.functional_tests.builder;


import com.github.dozermapper.core.Mapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class InheritanceTest {
    private Mapper mapper;

    private InheritanceTest.A source;

    @Test
    public void shouldCopyProperties() {
        {
            InheritanceTest.C result = mapper.map(source, InheritanceTest.C.class);
            MatcherAssert.assertThat(result.property1, CoreMatchers.equalTo("1"));
            MatcherAssert.assertThat(result.property2, CoreMatchers.equalTo("2"));
        }
        {
            InheritanceTest.B result = mapper.map(source, InheritanceTest.B.class);
            MatcherAssert.assertThat(result.getClass(), CoreMatchers.equalTo(InheritanceTest.B.class));
            MatcherAssert.assertThat(result.property1, CoreMatchers.equalTo("1"));
        }
    }

    @Test
    public void shouldCopyProperties_Instances() {
        {
            InheritanceTest.C result = new InheritanceTest.C();
            mapper.map(source, result);
            MatcherAssert.assertThat(result.property1, CoreMatchers.equalTo("1"));
            MatcherAssert.assertThat(result.property2, CoreMatchers.equalTo("2"));
        }
        {
            InheritanceTest.B result = new InheritanceTest.B();
            mapper.map(source, result);
            MatcherAssert.assertThat(result.getClass(), CoreMatchers.equalTo(InheritanceTest.B.class));
            MatcherAssert.assertThat(result.property1, CoreMatchers.equalTo("1"));
        }
    }

    public static class A {
        String property1;

        String property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }

    public static class B {
        String property1;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }
    }

    public static class C extends InheritanceTest.B {
        String property2;

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }
}

