/**
 * Copyright (C) 2016 Google, Inc.
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
package com.google.auto.value.extension.memoized;


import com.google.auto.value.extension.memoized.processor.MemoizedValidator;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MemoizedValidationTest {
    @Test
    public void privateMethod() {
        MemoizedMethodSubjectFactory.assertThatMemoizeMethod("@Memoized private String method() { return \"\"; }").hasError("@Memoized methods cannot be private");
    }

    @Test
    public void staticMethod() {
        MemoizedMethodSubjectFactory.assertThatMemoizeMethod("@Memoized static String method() { return \"\"; }").hasError("@Memoized methods cannot be static");
    }

    @Test
    public void finalMethod() {
        MemoizedMethodSubjectFactory.assertThatMemoizeMethod("@Memoized final String method() { return \"\"; }").hasError("@Memoized methods cannot be final");
    }

    @Test
    public void abstractMethod() {
        MemoizedMethodSubjectFactory.assertThatMemoizeMethod("@Memoized abstract String method();").hasError("@Memoized methods cannot be abstract");
    }

    @Test
    public void voidMethod() {
        MemoizedMethodSubjectFactory.assertThatMemoizeMethod("@Memoized void method() {}").hasError("@Memoized methods cannot be void");
    }

    @Test
    public void parameters() {
        MemoizedMethodSubjectFactory.assertThatMemoizeMethod("@Memoized String method(Object param) { return \"\"; }").hasError("@Memoized methods cannot have parameters");
    }

    @Test
    public void notInAutoValueClass() {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.EnclosingClass", "package test;", "", "import com.google.auto.value.extension.memoized.Memoized;", "", "abstract class EnclosingClass {", "  @Memoized", "  String string() {", "    return \"\";", "  }", "}");
        Compilation compilation = javac().withProcessors(new MemoizedValidator()).compile(source);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("@Memoized methods must be declared only in @AutoValue classes").inFile(source).onLine(6);
    }
}

