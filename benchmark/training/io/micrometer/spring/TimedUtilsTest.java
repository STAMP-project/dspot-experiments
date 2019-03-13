/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring;


import io.micrometer.core.annotation.Timed;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;


public class TimedUtilsTest {
    @Test
    public void timedClass() {
        assertThat(TimedUtils.findTimedAnnotations(TimedUtilsTest.TimedClass.class)).isNotEmpty();
    }

    @Test
    public void timedMethod() throws NoSuchMethodException {
        assertThat(TimedUtils.findTimedAnnotations(TimedUtilsTest.TimedClass.class.getDeclaredMethod("foo"))).isNotEmpty();
    }

    @Test
    public void subclassedTimedClass() {
        assertThat(TimedUtils.findTimedAnnotations(TimedUtilsTest.SpecialTimedClass.class)).isNotEmpty();
    }

    @Test
    public void subclassedTimedMethod() throws NoSuchMethodException {
        assertThat(TimedUtils.findTimedAnnotations(TimedUtilsTest.SpecialTimedClass.class.getDeclaredMethod("foo"))).isNotEmpty();
    }

    @Test
    public void inheritedTimedClass() {
        assertThat(TimedUtils.findTimedAnnotations(TimedUtilsTest.SpecialTimedClass.class)).isNotEmpty();
    }

    @Test
    public void inheritedTimedMethod() throws NoSuchMethodException {
        assertThat(TimedUtils.findTimedAnnotations(TimedUtilsTest.SpecialTimedClass.class.getDeclaredMethod("foo"))).isNotEmpty();
    }

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Timed(percentiles = 0.95)
    @interface Timed95 {}

    @Timed
    static class TimedClass {
        @Timed
        public void foo() {
        }
    }

    @TimedUtilsTest.Timed95
    static class SpecialTimedClass {
        @TimedUtilsTest.Timed95
        void foo() {
        }
    }

    class InheritedTimedClass extends TimedUtilsTest.TimedClass {
        @Override
        public void foo() {
            super.foo();
        }
    }
}

