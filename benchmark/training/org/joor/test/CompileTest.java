/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * [java-8]
 */
package org.joor.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.validator.TestClassValidator;


/**
 *
 *
 * @author Lukas Eder
 */
public class CompileTest {
    @Test
    public void testCompileWithClasspathDependency() throws Exception {
        TestClassValidator v = Reflect.compile("org.joor.test.TestClassValidatorImplementation", ("package org.joor.test; public class TestClassValidatorImplementation implements org.junit.validator.TestClassValidator {" + ((("public java.util.List<Exception> validateTestClass(org.junit.runners.model.TestClass testClass) {" + "return new java.util.ArrayList<>();") + "}") + "}"))).create().get();
        Assert.assertEquals(new ArrayList<Object>(), v.validateTestClass(null));
    }

    @Test
    public void testCompileSamePackage() {
        Supplier<String> supplier = Reflect.compile("org.joor.test.CompileTest3", ("package org.joor.test;\n" + (((("class CompileTest3 implements java.util.function.Supplier<String> {\n" + "  public String get() {\n") + "    return \"Hello World!\";\n") + "  }\n") + "}\n"))).create().get();
        Assert.assertEquals("Hello World!", supplier.get());
    }

    @Test
    public void testCompileDifferentPackage() {
        Supplier<String> supplier = Reflect.compile("com.example.CompileTestDifferentPackage", ("package com.example;\n" + (((("class CompileTestDifferentPackage implements java.util.function.Supplier<String> {\n" + "  public String get() {\n") + "    return \"Hello World!\";\n") + "  }\n") + "}\n"))).create().get();
        Assert.assertEquals("Hello World!", supplier.get());
    }

    @Test
    public void testCompileEnums() {
        Class<Enum<?>> e = Reflect.compile("org.joor.test.CompiledEnum", ("package org.joor.test;" + "enum CompiledEnum { a, b, c }")).get();
        Assert.assertEquals(Arrays.asList("a", "b", "c"), Stream.of(e.getEnumConstants()).map(Enum::name).collect(Collectors.toList()));
    }

    @Test
    public void testCompilationError() {
        try {
            Reflect.compile("org.joor.test.CompilationError", ("package org.joor.test;" + "class CompilationError { a }"));
        } catch (ReflectException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("Compilation error:"));
        }
    }

    interface J {
        default String m() {
            return "J.m()";
        }
    }

    @Test
    public void testCompileNestedClass() {
        Class<?> c = Reflect.compile("org.joor.test.CompiledNestedClass", ("package org.joor.test;" + ((((("public class CompiledNestedClass {" + "public class Inner {") + "int foo() { return 42; }") + "}") + "Inner inner() { return new Inner(); }") + "}"))).get();
        int foo = Reflect.onClass(c).create().call("inner").call("foo").get();
        Assert.assertEquals(42, foo);
    }

    @Test
    public void testCompileTopLevelClasses() {
        Class<?> c = Reflect.compile("org.joor.test.CompileTopLevelClasses", ("package org.joor.test;" + ((((("public class CompileTopLevelClasses {" + "Other other() { return new Other(); }") + "}") + "class Other {") + "int foo() { return 42; }") + "}"))).get();
        int foo = Reflect.onClass(c).create().call("other").call("foo").get();
        Assert.assertEquals(42, foo);
    }
}

