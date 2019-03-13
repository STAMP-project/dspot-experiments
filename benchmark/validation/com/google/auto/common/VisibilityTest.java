/**
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.auto.common;


import com.google.testing.compile.CompilationRule;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class VisibilityTest {
    @Rule
    public CompilationRule compilation = new CompilationRule();

    @Test
    public void packageVisibility() {
        assertThat(Visibility.ofElement(compilation.getElements().getPackageElement("java.lang"))).isEqualTo(Visibility.PUBLIC);
        assertThat(Visibility.ofElement(compilation.getElements().getPackageElement("com.google.auto.common"))).isEqualTo(Visibility.PUBLIC);
    }

    @Test
    public void moduleVisibility() throws IllegalAccessException, InvocationTargetException {
        Method getModuleElement;
        try {
            getModuleElement = Elements.class.getMethod("getModuleElement", CharSequence.class);
        } catch (NoSuchMethodException e) {
            // TODO(ronshapiro): rewrite this test without reflection once we're on Java 9
            return;
        }
        Element moduleElement = ((Element) (getModuleElement.invoke(compilation.getElements(), "java.base")));
        assertThat(Visibility.ofElement(moduleElement)).isEqualTo(Visibility.PUBLIC);
    }

    @SuppressWarnings("unused")
    public static class PublicClass {
        public static class NestedPublicClass {}

        protected static class NestedProtectedClass {}

        static class NestedDefaultClass {}

        private static class NestedPrivateClass {}
    }

    @SuppressWarnings("unused")
    protected static class ProtectedClass {
        public static class NestedPublicClass {}

        protected static class NestedProtectedClass {}

        static class NestedDefaultClass {}

        private static class NestedPrivateClass {}
    }

    @SuppressWarnings("unused")
    static class DefaultClass {
        public static class NestedPublicClass {}

        protected static class NestedProtectedClass {}

        static class NestedDefaultClass {}

        private static class NestedPrivateClass {}
    }

    @SuppressWarnings("unused")
    private static class PrivateClass {
        public static class NestedPublicClass {}

        protected static class NestedProtectedClass {}

        static class NestedDefaultClass {}

        private static class NestedPrivateClass {}
    }

    @Test
    public void classVisibility() {
        assertThat(Visibility.ofElement(compilation.getElements().getTypeElement("java.util.Map"))).isEqualTo(Visibility.PUBLIC);
        assertThat(Visibility.ofElement(compilation.getElements().getTypeElement("java.util.Map.Entry"))).isEqualTo(Visibility.PUBLIC);
        assertThat(Visibility.ofElement(compilation.getElements().getTypeElement(VisibilityTest.PublicClass.class.getCanonicalName()))).isEqualTo(Visibility.PUBLIC);
        assertThat(Visibility.ofElement(compilation.getElements().getTypeElement(VisibilityTest.ProtectedClass.class.getCanonicalName()))).isEqualTo(Visibility.PROTECTED);
        assertThat(Visibility.ofElement(compilation.getElements().getTypeElement(VisibilityTest.DefaultClass.class.getCanonicalName()))).isEqualTo(Visibility.DEFAULT);
        assertThat(Visibility.ofElement(compilation.getElements().getTypeElement(VisibilityTest.PrivateClass.class.getCanonicalName()))).isEqualTo(Visibility.PRIVATE);
    }

    @Test
    public void effectiveClassVisibility() {
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PublicClass.class)).isEqualTo(Visibility.PUBLIC);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.ProtectedClass.class)).isEqualTo(Visibility.PROTECTED);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.DefaultClass.class)).isEqualTo(Visibility.DEFAULT);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PrivateClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PublicClass.NestedPublicClass.class)).isEqualTo(Visibility.PUBLIC);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PublicClass.NestedProtectedClass.class)).isEqualTo(Visibility.PROTECTED);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PublicClass.NestedDefaultClass.class)).isEqualTo(Visibility.DEFAULT);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PublicClass.NestedPrivateClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.ProtectedClass.NestedPublicClass.class)).isEqualTo(Visibility.PROTECTED);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.ProtectedClass.NestedProtectedClass.class)).isEqualTo(Visibility.PROTECTED);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.ProtectedClass.NestedDefaultClass.class)).isEqualTo(Visibility.DEFAULT);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.ProtectedClass.NestedPrivateClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.DefaultClass.NestedPublicClass.class)).isEqualTo(Visibility.DEFAULT);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.DefaultClass.NestedProtectedClass.class)).isEqualTo(Visibility.DEFAULT);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.DefaultClass.NestedDefaultClass.class)).isEqualTo(Visibility.DEFAULT);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.DefaultClass.NestedPrivateClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PrivateClass.NestedPublicClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PrivateClass.NestedProtectedClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PrivateClass.NestedDefaultClass.class)).isEqualTo(Visibility.PRIVATE);
        assertThat(effectiveVisiblityOfClass(VisibilityTest.PrivateClass.NestedPrivateClass.class)).isEqualTo(Visibility.PRIVATE);
    }
}

