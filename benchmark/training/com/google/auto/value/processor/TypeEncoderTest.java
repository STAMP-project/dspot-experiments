/**
 * Copyright (C) 2017 Google, Inc.
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
package com.google.auto.value.processor;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationRule;
import com.google.testing.compile.JavaFileObjects;
import java.math.BigInteger;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.management.MBeanServer;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static javax.tools.Diagnostic.Kind.ERROR;


/**
 * Tests for {@link TypeEncoder}.
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class TypeEncoderTest {
    @Rule
    public final CompilationRule compilationRule = new CompilationRule();

    private Types typeUtils;

    private Elements elementUtils;

    private static class MultipleBounds<K extends List<V> & Comparable<K>, V> {}

    @Test
    public void testImportsForNoTypes() {
        assertTypeImportsAndSpellings(TypeEncoderTest.typeMirrorSet(), "foo.bar", ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testImportsForImplicitlyImportedTypes() {
        Set<TypeMirror> types = // Same package, so no import.
        TypeEncoderTest.typeMirrorSet(typeMirrorOf(String.class), typeMirrorOf(MBeanServer.class), typeUtils.getPrimitiveType(TypeKind.INT), typeUtils.getPrimitiveType(TypeKind.BOOLEAN));
        assertTypeImportsAndSpellings(types, "javax.management", ImmutableList.of(), ImmutableList.of("String", "MBeanServer", "int", "boolean"));
    }

    @Test
    public void testImportsForPlainTypes() {
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeUtils.getPrimitiveType(TypeKind.INT), typeMirrorOf(String.class), typeMirrorOf(Proxy.class), typeMirrorOf(Proxy.Type.class), typeMirrorOf(Pattern.class), typeMirrorOf(MBeanServer.class));
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of("java.net.Proxy", "java.util.regex.Pattern", "javax.management.MBeanServer"), ImmutableList.of("int", "String", "Proxy", "Proxy.Type", "Pattern", "MBeanServer"));
    }

    @Test
    public void testImportsForComplicatedTypes() {
        TypeElement list = typeElementOf(List.class);
        TypeElement map = typeElementOf(Map.class);
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeUtils.getPrimitiveType(TypeKind.INT), typeMirrorOf(Pattern.class), // List<Timer>
        typeUtils.getDeclaredType(list, typeMirrorOf(Timer.class)), // Map<? extends Timer, ? super BigInteger>
        typeUtils.getDeclaredType(map, typeUtils.getWildcardType(typeMirrorOf(Timer.class), null), typeUtils.getWildcardType(null, typeMirrorOf(BigInteger.class))));
        // Timer is referenced twice but should obviously only be imported once.
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of("java.math.BigInteger", "java.util.List", "java.util.Map", "java.util.Timer", "java.util.regex.Pattern"), ImmutableList.of("int", "Pattern", "List<Timer>", "Map<? extends Timer, ? super BigInteger>"));
    }

    @Test
    public void testImportsForArrayTypes() {
        TypeElement list = typeElementOf(List.class);
        TypeElement set = typeElementOf(Set.class);
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.INT)), typeUtils.getArrayType(typeMirrorOf(Pattern.class)), // Set<Matcher[]>[]
        typeUtils.getArrayType(typeUtils.getDeclaredType(set, typeUtils.getArrayType(typeMirrorOf(Matcher.class)))), // List<Timer[]>
        typeUtils.getDeclaredType(list, typeUtils.getArrayType(typeMirrorOf(Timer.class))));
        // Timer is referenced twice but should obviously only be imported once.
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of("java.util.List", "java.util.Set", "java.util.Timer", "java.util.regex.Matcher", "java.util.regex.Pattern"), ImmutableList.of("int[]", "Pattern[]", "Set<Matcher[]>[]", "List<Timer[]>"));
    }

    @Test
    public void testImportNestedType() {
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeMirrorOf(Proxy.Type.class));
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of("java.net.Proxy"), ImmutableList.of("Proxy.Type"));
    }

    @Test
    public void testImportsForAmbiguousNames() {
        TypeMirror wildcard = typeUtils.getWildcardType(null, null);
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeUtils.getPrimitiveType(TypeKind.INT), typeMirrorOf(java.awt.List.class), typeMirrorOf(String.class), // List<?>
        typeUtils.getDeclaredType(typeElementOf(List.class), wildcard), // Map<?, ?>
        typeUtils.getDeclaredType(typeElementOf(Map.class), wildcard, wildcard));
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of("java.util.Map"), ImmutableList.of("int", "java.awt.List", "String", "java.util.List<?>", "Map<?, ?>"));
    }

    @Test
    public void testSimplifyJavaLangString() {
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeMirrorOf(String.class));
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of(), ImmutableList.of("String"));
    }

    @Test
    public void testSimplifyJavaLangThreadState() {
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(typeMirrorOf(Thread.State.class));
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of(), ImmutableList.of("Thread.State"));
    }

    @Test
    public void testSimplifyJavaLangNamesake() {
        TypeMirror javaLangType = typeMirrorOf(RuntimePermission.class);
        TypeMirror notJavaLangType = typeMirrorOf(com.google.auto.value.processor.testclasses.RuntimePermission.class);
        Set<TypeMirror> types = TypeEncoderTest.typeMirrorSet(javaLangType, notJavaLangType);
        assertTypeImportsAndSpellings(types, "foo.bar", ImmutableList.of(), ImmutableList.of(javaLangType.toString(), notJavaLangType.toString()));
    }

    @Test
    public void testSimplifyComplicatedTypes() {
        // This test constructs a set of types and feeds them to TypeEncoder. Then it verifies that
        // the resultant rewrites of those types are what we would expect.
        TypeElement list = typeElementOf(List.class);
        TypeElement map = typeElementOf(Map.class);
        TypeMirror string = typeMirrorOf(String.class);
        TypeMirror integer = typeMirrorOf(Integer.class);
        TypeMirror pattern = typeMirrorOf(Pattern.class);
        TypeMirror timer = typeMirrorOf(Timer.class);
        TypeMirror bigInteger = typeMirrorOf(BigInteger.class);
        ImmutableMap<TypeMirror, String> typeMap = ImmutableMap.<TypeMirror, String>builder().put(typeUtils.getPrimitiveType(TypeKind.INT), "int").put(typeUtils.getArrayType(typeUtils.getPrimitiveType(TypeKind.BYTE)), "byte[]").put(pattern, "Pattern").put(typeUtils.getArrayType(pattern), "Pattern[]").put(typeUtils.getArrayType(typeUtils.getArrayType(pattern)), "Pattern[][]").put(typeUtils.getDeclaredType(list, typeUtils.getWildcardType(null, null)), "List<?>").put(typeUtils.getDeclaredType(list, timer), "List<Timer>").put(typeUtils.getDeclaredType(map, string, integer), "Map<String, Integer>").put(typeUtils.getDeclaredType(map, typeUtils.getWildcardType(timer, null), typeUtils.getWildcardType(null, bigInteger)), "Map<? extends Timer, ? super BigInteger>").build();
        assertTypeImportsAndSpellings(typeMap.keySet(), "foo.bar", ImmutableList.of("java.math.BigInteger", "java.util.List", "java.util.Map", "java.util.Timer", "java.util.regex.Pattern"), ImmutableList.copyOf(typeMap.values()));
    }

    @Test
    public void testSimplifyMultipleBounds() {
        TypeElement multipleBoundsElement = typeElementOf(TypeEncoderTest.MultipleBounds.class);
        TypeMirror multipleBoundsMirror = multipleBoundsElement.asType();
        String text = "`import`\n";
        text += ("{" + (TypeEncoder.encode(multipleBoundsMirror))) + "}";
        text += ("{" + (TypeEncoder.formalTypeParametersString(multipleBoundsElement))) + "}";
        String myPackage = getClass().getPackage().getName();
        String decoded = TypeEncoder.decode(text, elementUtils, typeUtils, myPackage, baseWithoutContainedTypes());
        String expected = "import java.util.List;\n\n" + ("{TypeEncoderTest.MultipleBounds<K, V>}" + "{<K extends List<V> & Comparable<K>, V>}");
        assertThat(decoded).isEqualTo(expected);
    }

    // This test checks that we correctly throw MissingTypeException if there is an ErrorType anywhere
    // inside a type we are asked to simplify. There's no way to get an ErrorType from typeUtils or
    // elementUtils, so we need to fire up the compiler with an erroneous source file and use an
    // annotation processor to capture the resulting ErrorType. Then we can run tests within that
    // annotation processor, and propagate any failures out of this test.
    @Test
    public void testErrorTypes() {
        JavaFileObject source = JavaFileObjects.forSourceString("ExtendsUndefinedType", "class ExtendsUndefinedType extends UndefinedParent {}");
        Compilation compilation = javac().withProcessors(new TypeEncoderTest.ErrorTestProcessor()).compile(source);
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("UndefinedParent");
        assertThat(compilation).hadErrorCount(1);
    }

    @SupportedAnnotationTypes("*")
    private static class ErrorTestProcessor extends AbstractProcessor {
        Types typeUtils;

        Elements elementUtils;

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) {
                typeUtils = processingEnv.getTypeUtils();
                elementUtils = processingEnv.getElementUtils();
                test();
            }
            return false;
        }

        private void test() {
            TypeElement extendsUndefinedType = elementUtils.getTypeElement("ExtendsUndefinedType");
            ErrorType errorType = ((ErrorType) (extendsUndefinedType.getSuperclass()));
            TypeElement list = elementUtils.getTypeElement("java.util.List");
            TypeMirror listOfError = typeUtils.getDeclaredType(list, errorType);
            TypeMirror queryExtendsError = typeUtils.getWildcardType(errorType, null);
            TypeMirror listOfQueryExtendsError = typeUtils.getDeclaredType(list, queryExtendsError);
            TypeMirror querySuperError = typeUtils.getWildcardType(null, errorType);
            TypeMirror listOfQuerySuperError = typeUtils.getDeclaredType(list, querySuperError);
            TypeMirror arrayOfError = typeUtils.getArrayType(errorType);
            testErrorType(errorType);
            testErrorType(listOfError);
            testErrorType(listOfQueryExtendsError);
            testErrorType(listOfQuerySuperError);
            testErrorType(arrayOfError);
        }

        // error message gets converted into assertion failure
        @SuppressWarnings("MissingFail")
        private void testErrorType(TypeMirror typeWithError) {
            try {
                TypeEncoder.encode(typeWithError);
                processingEnv.getMessager().printMessage(ERROR, ("Expected exception for type: " + typeWithError));
            } catch (MissingTypeException expected) {
            }
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported();
        }
    }
}

