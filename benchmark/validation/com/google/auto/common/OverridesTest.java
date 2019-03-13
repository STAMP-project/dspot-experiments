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
package com.google.auto.common;


import Overrides.ExplicitOverrides;
import com.google.common.base.Charsets;
import com.google.common.base.Converter;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.io.Files;
import com.google.common.truth.Expect;
import com.google.testing.compile.CompilationRule;
import java.io.File;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;


/**
 * Tests that the {@link Overrides} class has behaviour consistent with javac. We test this in
 * two ways: once with {@link Overrides.ExplicitOverrides} using javac's own {@link Elements} and
 * {@link Types}, and once with it using the version of those objects from the Eclipse compiler
 * (ecj).
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(Parameterized.class)
public class OverridesTest {
    private static final ImmutableSet<String> TOO_NEW_FOR_ECJ = ImmutableSet.of("9", "10", "11");

    @Rule
    public CompilationRule compilation = new CompilationRule();

    @Rule
    public OverridesTest.EcjCompilationRule ecjCompilation = new OverridesTest.EcjCompilationRule();

    @Rule
    public Expect expect = Expect.create();

    public enum CompilerType {

        JAVAC() {
            @Override
            void initUtils(OverridesTest test) {
                test.typeUtils = test.compilation.getTypes();
                test.elementUtils = test.compilation.getElements();
            }
        },
        ECJ() {
            @Override
            void initUtils(OverridesTest test) {
                test.typeUtils = test.ecjCompilation.types;
                test.elementUtils = test.ecjCompilation.elements;
            }
        };
        abstract void initUtils(OverridesTest test);
    }

    private final OverridesTest.CompilerType compilerType;

    private Types typeUtils;

    private Elements elementUtils;

    private Elements javacElementUtils;

    private Overrides javacOverrides;

    private ExplicitOverrides explicitOverrides;

    public OverridesTest(OverridesTest.CompilerType compilerType) {
        this.compilerType = compilerType;
    }

    static class TypesForInheritance {
        interface One {
            void m();

            void m(String x);

            void n();
        }

        interface Two {
            void m();

            void m(int x);
        }

        static class Parent {
            public void m() {
            }
        }

        static class ChildOfParent extends OverridesTest.TypesForInheritance.Parent {}

        static class ChildOfOne implements OverridesTest.TypesForInheritance.One {
            @Override
            public void m() {
            }

            @Override
            public void m(String x) {
            }

            @Override
            public void n() {
            }
        }

        static class ChildOfOneAndTwo implements OverridesTest.TypesForInheritance.One , OverridesTest.TypesForInheritance.Two {
            @Override
            public void m() {
            }

            @Override
            public void m(String x) {
            }

            @Override
            public void m(int x) {
            }

            @Override
            public void n() {
            }
        }

        static class ChildOfParentAndOne extends OverridesTest.TypesForInheritance.Parent implements OverridesTest.TypesForInheritance.One {
            @Override
            public void m() {
            }

            @Override
            public void m(String x) {
            }

            @Override
            public void n() {
            }
        }

        static class ChildOfParentAndOneAndTwo extends OverridesTest.TypesForInheritance.Parent implements OverridesTest.TypesForInheritance.One , OverridesTest.TypesForInheritance.Two {
            @Override
            public void m(String x) {
            }

            @Override
            public void m(int x) {
            }

            @Override
            public void n() {
            }
        }

        abstract static class AbstractChildOfOne implements OverridesTest.TypesForInheritance.One {}

        abstract static class AbstractChildOfOneAndTwo implements OverridesTest.TypesForInheritance.One , OverridesTest.TypesForInheritance.Two {}

        abstract static class AbstractChildOfParentAndOneAndTwo extends OverridesTest.TypesForInheritance.Parent implements OverridesTest.TypesForInheritance.One , OverridesTest.TypesForInheritance.Two {}
    }

    static class MoreTypesForInheritance {
        interface Key {}

        interface BindingType {}

        interface ContributionType {}

        interface HasKey {
            OverridesTest.MoreTypesForInheritance.Key key();
        }

        interface HasBindingType {
            OverridesTest.MoreTypesForInheritance.BindingType bindingType();
        }

        interface HasContributionType {
            OverridesTest.MoreTypesForInheritance.ContributionType contributionType();
        }

        abstract static class BindingDeclaration implements OverridesTest.MoreTypesForInheritance.HasKey {
            abstract Optional<Element> bindingElement();

            abstract Optional<TypeElement> contributingModule();
        }

        abstract static class MultibindingDeclaration extends OverridesTest.MoreTypesForInheritance.BindingDeclaration implements OverridesTest.MoreTypesForInheritance.HasBindingType , OverridesTest.MoreTypesForInheritance.HasContributionType {
            @Override
            public abstract OverridesTest.MoreTypesForInheritance.Key key();

            @Override
            public abstract OverridesTest.MoreTypesForInheritance.ContributionType contributionType();

            @Override
            public abstract OverridesTest.MoreTypesForInheritance.BindingType bindingType();
        }
    }

    static class TypesForVisibility {
        public abstract static class PublicGrandparent {
            public abstract String foo();
        }

        private static class PrivateParent extends OverridesTest.TypesForVisibility.PublicGrandparent {
            @Override
            public String foo() {
                return "foo";
            }
        }

        static class Child extends OverridesTest.TypesForVisibility.PrivateParent {}
    }

    static class TypesForGenerics {
        interface XCollection<E> {
            boolean add(E x);
        }

        interface XList<E> extends OverridesTest.TypesForGenerics.XCollection<E> {
            @Override
            public boolean add(E x);
        }

        static class StringList implements OverridesTest.TypesForGenerics.XList<String> {
            @Override
            public boolean add(String x) {
                return false;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    static class TypesForRaw {
        static class RawParent {
            void frob(List x) {
            }
        }

        static class RawChildOfRaw extends OverridesTest.TypesForRaw.RawParent {
            @Override
            void frob(List x) {
            }
        }

        static class NonRawParent {
            void frob(List<String> x) {
            }
        }

        static class RawChildOfNonRaw extends OverridesTest.TypesForRaw.NonRawParent {
            @Override
            void frob(List x) {
            }
        }
    }

    @Test
    public void overridesInheritance() {
        checkOverridesInContainedClasses(OverridesTest.TypesForInheritance.class);
    }

    @Test
    public void overridesMoreInheritance() {
        checkOverridesInContainedClasses(OverridesTest.MoreTypesForInheritance.class);
    }

    @Test
    public void overridesVisibility() {
        checkOverridesInContainedClasses(OverridesTest.TypesForVisibility.class);
    }

    @Test
    public void overridesGenerics() {
        checkOverridesInContainedClasses(OverridesTest.TypesForGenerics.class);
    }

    @Test
    public void overridesRaw() {
        checkOverridesInContainedClasses(OverridesTest.TypesForRaw.class);
    }

    // Test a tricky diamond inheritance hierarchy:
    // Collection
    // /          \
    // AbstractCollection     List
    // \          /
    // AbstractList
    // This also tests that we do the right thing with generics, since naively the TypeMirror
    // that you get for List<E> will not appear to be a subtype of the one you get for Collection<E>
    // since the two Es are not the same.
    @Test
    public void overridesDiamond() {
        checkOverridesInSet(ImmutableSet.<Class<?>>of(Collection.class, List.class, AbstractCollection.class, AbstractList.class));
    }

    // These skeletal parallels to the real collection classes ensure that the test is independent
    // of the details of those classes, for example whether List<E> redeclares add(E) even though
    // it also inherits it from Collection<E>.
    private interface XCollection<E> {
        boolean add(E e);
    }

    private interface XList<E> extends OverridesTest.XCollection<E> {}

    private abstract static class XAbstractCollection<E> implements OverridesTest.XCollection<E> {
        @Override
        public boolean add(E e) {
            return false;
        }
    }

    private abstract static class XAbstractList<E> extends OverridesTest.XAbstractCollection<E> implements OverridesTest.XList<E> {
        @Override
        public boolean add(E e) {
            return true;
        }
    }

    private abstract static class XStringList extends OverridesTest.XAbstractList<String> {}

    private abstract static class XAbstractStringList implements OverridesTest.XList<String> {}

    private abstract static class XNumberList<E extends Number> extends OverridesTest.XAbstractList<E> {}

    // Parameter of add(E) in StringList is String.
    // That means that we successfully recorded E[AbstractList] = String and E[List] = E[AbstractList]
    // and String made it all the way through.
    @Test
    public void methodParameters_StringList() {
        TypeElement xAbstractList = getTypeElement(OverridesTest.XAbstractList.class);
        TypeElement xStringList = getTypeElement(OverridesTest.XStringList.class);
        TypeElement string = getTypeElement(String.class);
        ExecutableElement add = getMethod(xAbstractList, "add", TypeKind.TYPEVAR);
        List<TypeMirror> params = explicitOverrides.erasedParameterTypes(add, xStringList);
        List<TypeMirror> expectedParams = ImmutableList.of(string.asType());
        assertTypeListsEqual(params, expectedParams);
    }

    // Parameter of add(E) in AbstractStringList is String.
    // That means that we successfully recorded E[List] = String and E[Collection] = E[List].
    @Test
    public void methodParameters_AbstractStringList() {
        TypeElement xCollection = getTypeElement(OverridesTest.XCollection.class);
        TypeElement xAbstractStringList = getTypeElement(OverridesTest.XAbstractStringList.class);
        TypeElement string = getTypeElement(String.class);
        ExecutableElement add = getMethod(xCollection, "add", TypeKind.TYPEVAR);
        List<TypeMirror> params = explicitOverrides.erasedParameterTypes(add, xAbstractStringList);
        List<TypeMirror> expectedParams = ImmutableList.of(string.asType());
        assertTypeListsEqual(params, expectedParams);
    }

    // Parameter of add(E) in NumberList is Number.
    // That means that we successfully recorded E[AbstractList] = Number and on from
    // there, with Number being used because it is the erasure of <E extends Number>.
    @Test
    public void methodParams_NumberList() {
        TypeElement xCollection = getTypeElement(OverridesTest.XCollection.class);
        TypeElement xNumberList = getTypeElement(OverridesTest.XNumberList.class);
        TypeElement number = getTypeElement(Number.class);
        ExecutableElement add = getMethod(xCollection, "add", TypeKind.TYPEVAR);
        List<TypeMirror> params = explicitOverrides.erasedParameterTypes(add, xNumberList);
        List<TypeMirror> expectedParams = ImmutableList.of(number.asType());
        assertTypeListsEqual(params, expectedParams);
    }

    // This is derived from a class that provoked a StackOverflowError in an earlier version.
    private abstract static class StringToRangeConverter<T extends Comparable<T>> extends Converter<String, Range<T>> {
        @Override
        protected String doBackward(Range<T> b) {
            return null;
        }
    }

    @Test
    public void methodParams_RecursiveBound() {
        TypeElement stringToRangeConverter = getTypeElement(OverridesTest.StringToRangeConverter.class);
        TypeElement range = getTypeElement(Range.class);
        ExecutableElement valueConverter = getMethod(stringToRangeConverter, "doBackward", TypeKind.DECLARED);
        List<TypeMirror> params = explicitOverrides.erasedParameterTypes(valueConverter, stringToRangeConverter);
        List<TypeMirror> expectedParams = ImmutableList.<TypeMirror>of(typeUtils.erasure(range.asType()));
        assertTypeListsEqual(params, expectedParams);
    }

    @Test
    public void methodFromSuperclasses() {
        TypeElement xAbstractCollection = getTypeElement(OverridesTest.XAbstractCollection.class);
        TypeElement xAbstractList = getTypeElement(OverridesTest.XAbstractList.class);
        TypeElement xAbstractStringList = getTypeElement(OverridesTest.XAbstractStringList.class);
        TypeElement xStringList = getTypeElement(OverridesTest.XStringList.class);
        ExecutableElement add = getMethod(xAbstractCollection, "add", TypeKind.TYPEVAR);
        ExecutableElement addInAbstractStringList = explicitOverrides.methodFromSuperclasses(xAbstractStringList, add);
        assertThat(addInAbstractStringList).isNull();
        ExecutableElement addInStringList = explicitOverrides.methodFromSuperclasses(xStringList, add);
        assertThat(addInStringList.getEnclosingElement()).isEqualTo(xAbstractList);
    }

    @Test
    public void methodFromSuperinterfaces() {
        TypeElement xCollection = getTypeElement(OverridesTest.XCollection.class);
        TypeElement xAbstractList = getTypeElement(OverridesTest.XAbstractList.class);
        TypeElement xAbstractStringList = getTypeElement(OverridesTest.XAbstractStringList.class);
        TypeElement xNumberList = getTypeElement(OverridesTest.XNumberList.class);
        TypeElement xList = getTypeElement(OverridesTest.XList.class);
        ExecutableElement add = getMethod(xCollection, "add", TypeKind.TYPEVAR);
        ExecutableElement addInAbstractStringList = explicitOverrides.methodFromSuperinterfaces(xAbstractStringList, add);
        assertThat(addInAbstractStringList.getEnclosingElement()).isEqualTo(xCollection);
        ExecutableElement addInNumberList = explicitOverrides.methodFromSuperinterfaces(xNumberList, add);
        assertThat(addInNumberList.getEnclosingElement()).isEqualTo(xAbstractList);
        ExecutableElement addInList = explicitOverrides.methodFromSuperinterfaces(xList, add);
        assertThat(addInList.getEnclosingElement()).isEqualTo(xCollection);
    }

    // TODO(emcmanus): replace this with something from compile-testing when that's available.
    /**
     * An equivalent to {@link CompilationRule} that uses ecj (the Eclipse compiler) instead of javac.
     * If the parameterized test is not selecting ecj then this rule has no effect.
     */
    public class EcjCompilationRule implements TestRule {
        Elements elements;

        Types types;

        @Override
        public Statement apply(Statement base, Description description) {
            if (!(compilerType.equals(OverridesTest.CompilerType.ECJ))) {
                return base;
            }
            return new OverridesTest.EcjCompilationStatement(base);
        }
    }

    private class EcjCompilationStatement extends Statement {
        private final Statement statement;

        EcjCompilationStatement(Statement base) {
            this.statement = base;
        }

        @Override
        public void evaluate() throws Throwable {
            File tmpDir = File.createTempFile("OverridesTest", "dir");
            tmpDir.delete();
            tmpDir.mkdir();
            File dummySourceFile = new File(tmpDir, "Dummy.java");
            try {
                Files.asCharSink(dummySourceFile, Charsets.UTF_8).write("class Dummy {}");
                evaluate(dummySourceFile);
            } finally {
                dummySourceFile.delete();
                tmpDir.delete();
            }
        }

        private void evaluate(File dummySourceFile) throws Throwable {
            JavaCompiler compiler = new EclipseCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, Charsets.UTF_8);
            Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjects(dummySourceFile);
            JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, null, null, sources);
            OverridesTest.EcjTestProcessor processor = new OverridesTest.EcjTestProcessor(statement);
            task.setProcessors(ImmutableList.of(processor));
            assertThat(task.call()).isTrue();
            processor.maybeThrow();
        }
    }

    @SupportedAnnotationTypes("*")
    private class EcjTestProcessor extends AbstractProcessor {
        private final Statement statement;

        private Throwable thrown;

        EcjTestProcessor(Statement statement) {
            this.statement = statement;
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latest();
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) {
                ecjCompilation.elements = processingEnv.getElementUtils();
                ecjCompilation.types = processingEnv.getTypeUtils();
                try {
                    statement.evaluate();
                } catch (Throwable t) {
                    thrown = t;
                }
            }
            return false;
        }

        void maybeThrow() throws Throwable {
            if ((thrown) != null) {
                throw thrown;
            }
        }
    }

    private static final TypeVisitor<String, Void> ERASED_STRING_TYPE_VISITOR = new SimpleTypeVisitor6<String, Void>() {
        @Override
        protected String defaultAction(TypeMirror e, Void p) {
            return e.toString();
        }

        @Override
        public String visitArray(ArrayType t, Void p) {
            return (visit(t.getComponentType())) + "[]";
        }

        @Override
        public String visitDeclared(DeclaredType t, Void p) {
            return MoreElements.asType(t.asElement()).getQualifiedName().toString();
        }

        @Override
        public String visitTypeVariable(TypeVariable t, Void p) {
            return visit(t.getUpperBound());
        }
    };
}

