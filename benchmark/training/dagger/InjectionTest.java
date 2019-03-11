/**
 * Copyright (C) 2010 Google Inc.
 * Copyright (C) 2012 Square Inc.
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
package dagger;


import Provides.Type;
import dagger.internal.TestingLoader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class InjectionTest {
    @Test
    public void basicInjection() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.G> gProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            InjectionTest.E provideE(InjectionTest.F f) {
                return new InjectionTest.E(f);
            }

            @Provides
            InjectionTest.F provideF() {
                return new InjectionTest.F();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        InjectionTest.G g = entryPoint.gProvider.get();
        assertThat(g.a).isNotNull();
        assertThat(g.b).isNotNull();
        assertThat(g.c).isNotNull();
        assertThat(g.d).isNotNull();
        assertThat(g.e).isNotNull();
        assertThat(g.e.f).isNotNull();
    }

    static class A {
        @Inject
        A() {
        }
    }

    static class B {
        @Inject
        B() {
        }
    }

    @Singleton
    static class C {
        @Inject
        C() {
        }
    }

    @Singleton
    static class D {
        @Inject
        D() {
        }
    }

    static class E {
        InjectionTest.F f;

        E(InjectionTest.F f) {
            this.f = f;
        }
    }

    static class F {}

    static class G {
        @Inject
        InjectionTest.A a;

        @Inject
        InjectionTest.B b;

        InjectionTest.C c;

        InjectionTest.D d;

        @Inject
        InjectionTest.E e;

        @Inject
        G(InjectionTest.C c, InjectionTest.D d) {
            this.c = c;
            this.d = d;
        }
    }

    @Test
    public void providerInjection() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.A> aProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {}
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.aProvider.get()).isNotNull();
        assertThat(entryPoint.aProvider.get()).isNotNull();
        assertThat(entryPoint.aProvider.get()).isNotSameAs(entryPoint.aProvider.get());
    }

    @Test
    public void singletons() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.F> fProvider;

            @Inject
            Provider<InjectionTest.I> iProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            @Singleton
            InjectionTest.F provideF() {
                return new InjectionTest.F();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.fProvider.get()).isSameAs(entryPoint.fProvider.get());
        assertThat(entryPoint.iProvider.get()).isSameAs(entryPoint.iProvider.get());
    }

    @Singleton
    static class I {
        @Inject
        I() {
        }
    }

    @Test
    public void bindingAnnotations() {
        final InjectionTest.A one = new InjectionTest.A();
        final InjectionTest.A two = new InjectionTest.A();
        class TestEntryPoint {
            @Inject
            InjectionTest.A a;

            @Inject
            @Named("one")
            InjectionTest.A aOne;

            @Inject
            @Named("two")
            InjectionTest.A aTwo;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            @Named("one")
            InjectionTest.A getOne() {
                return one;
            }

            @Provides
            @Named("two")
            InjectionTest.A getTwo() {
                return two;
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.a).isNotNull();
        assertThat(one).isSameAs(entryPoint.aOne);
        assertThat(two).isSameAs(entryPoint.aTwo);
    }

    @Test
    public void singletonBindingAnnotationAndProvider() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.L> lProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            InjectionTest.A a1;

            InjectionTest.A a2;

            @Provides
            @Singleton
            @Named("one")
            InjectionTest.F provideF(Provider<InjectionTest.A> aProvider) {
                a1 = aProvider.get();
                a2 = aProvider.get();
                return new InjectionTest.F();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        TestModule module = new TestModule();
        ObjectGraph.createWith(new TestingLoader(), module).inject(entryPoint);
        entryPoint.lProvider.get();
        assertThat(module.a1).isNotNull();
        assertThat(module.a2).isNotNull();
        assertThat(module.a1).isNotSameAs(module.a2);
        assertThat(entryPoint.lProvider.get()).isSameAs(entryPoint.lProvider.get());
    }

    @Singleton
    public static class L {
        @Inject
        @Named("one")
        InjectionTest.F f;

        @Inject
        Provider<InjectionTest.L> lProvider;
    }

    @Test
    public void singletonInGraph() {
        class TestEntryPoint {
            @Inject
            InjectionTest.N n1;

            @Inject
            InjectionTest.N n2;

            @Inject
            InjectionTest.F f1;

            @Inject
            InjectionTest.F f2;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            @Singleton
            InjectionTest.F provideF() {
                return new InjectionTest.F();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.f1).isSameAs(entryPoint.f2);
        assertThat(entryPoint.f1).isSameAs(entryPoint.n1.f1);
        assertThat(entryPoint.f1).isSameAs(entryPoint.n1.f2);
        assertThat(entryPoint.f1).isSameAs(entryPoint.n2.f1);
        assertThat(entryPoint.f1).isSameAs(entryPoint.n2.f2);
        assertThat(entryPoint.f1).isSameAs(entryPoint.n1.fProvider.get());
        assertThat(entryPoint.f1).isSameAs(entryPoint.n2.fProvider.get());
    }

    public static class N {
        @Inject
        InjectionTest.F f1;

        @Inject
        InjectionTest.F f2;

        @Inject
        Provider<InjectionTest.F> fProvider;
    }

    @Test
    public void noJitBindingsForAnnotations() {
        class TestEntryPoint {
            @Inject
            @Named("a")
            InjectionTest.A a;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {}
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.validate();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void injectableSupertypes() {
        class TestEntryPoint {
            @Inject
            InjectionTest.Q q;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            InjectionTest.F provideF() {
                return new InjectionTest.F();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.q.f).isNotNull();
    }

    @Test
    public void uninjectableSupertypes() {
        class TestEntryPoint {
            @Inject
            InjectionTest.T t;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {}
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.t).isNotNull();
    }

    public static class P {
        @Inject
        InjectionTest.F f;
    }

    public static class Q extends InjectionTest.P {
        @Inject
        Q() {
        }
    }

    static class S {}

    public static class T extends InjectionTest.S {
        @Inject
        T() {
        }
    }

    @Test
    public void singletonsAreNotEager() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.A> aProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            boolean sInjected = false;

            @Provides
            InjectionTest.F provideF(InjectionTest.R r) {
                return new InjectionTest.F();
            }

            @Provides
            @Singleton
            InjectionTest.S provideS() {
                sInjected = true;
                return new InjectionTest.S();
            }
        }
        InjectionTest.R.injected = false;
        TestEntryPoint entryPoint = new TestEntryPoint();
        TestModule module = new TestModule();
        ObjectGraph.createWith(new TestingLoader(), module).inject(entryPoint);
        assertThat(InjectionTest.R.injected).isFalse();
        assertThat(module.sInjected).isFalse();
    }

    @Singleton
    static class R {
        static boolean injected = false;

        @Inject
        R() {
            InjectionTest.R.injected = true;
        }
    }

    @Test
    public void providesSet() {
        final Set<InjectionTest.A> set = Collections.emptySet();
        class TestEntryPoint {
            @Inject
            Set<InjectionTest.A> set;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            Set<InjectionTest.A> provideSet() {
                return set;
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        TestModule module = new TestModule();
        ObjectGraph.createWith(new TestingLoader(), module).inject(entryPoint);
        assertThat(entryPoint.set).isSameAs(set);
    }

    @Test
    public void providesSetValues() {
        final Set<InjectionTest.A> set = Collections.emptySet();
        class TestEntryPoint {
            @Inject
            Set<InjectionTest.A> set;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides(type = Type.SET_VALUES)
            Set<InjectionTest.A> provideSet() {
                return set;
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        TestModule module = new TestModule();
        ObjectGraph.createWith(new TestingLoader(), module).inject(entryPoint);
        // copies into immutable collection
        assertThat(entryPoint.set).isNotSameAs(set);
        assertThat(entryPoint.set).isEqualTo(set);
    }

    @Test
    public void providerMethodsConflict() {
        @Module
        class TestModule {
            @Provides
            InjectionTest.A provideA1() {
                throw new AssertionError();
            }

            @Provides
            InjectionTest.A provideA2() {
                throw new AssertionError();
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void providesSetConflictsWithProvidesTypeSet() {
        @Module
        class TestModule {
            @Provides(type = Type.SET)
            InjectionTest.A provideSetElement() {
                throw new AssertionError();
            }

            @Provides
            Set<InjectionTest.A> provideSet() {
                throw new AssertionError();
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void providesSetConflictsWithProvidesTypeSetValues() {
        @Module
        class TestModule {
            @Provides(type = Type.SET_VALUES)
            Set<InjectionTest.A> provideSetContribution() {
                throw new AssertionError();
            }

            @Provides
            Set<InjectionTest.A> provideSet() {
                throw new AssertionError();
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void providesSetOfProvidersIsDifferentThanProvidesTypeSetValues() {
        final Set<InjectionTest.A> set = Collections.emptySet();
        final Set<Provider<InjectionTest.A>> providers = Collections.emptySet();
        class TestEntryPoint {
            @Inject
            Set<InjectionTest.A> set;

            @Inject
            Set<Provider<InjectionTest.A>> providers;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides(type = Type.SET_VALUES)
            Set<InjectionTest.A> provideSetContribution() {
                return set;
            }

            @Provides
            Set<Provider<InjectionTest.A>> provideProviders() {
                return providers;
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        TestModule module = new TestModule();
        ObjectGraph.createWith(new TestingLoader(), module).inject(entryPoint);
        // copies into immutable collection
        assertThat(entryPoint.set).isNotSameAs(set);
        assertThat(entryPoint.set).isEqualTo(set);
        assertThat(entryPoint.providers).isSameAs(providers);
    }

    @Test
    public void singletonsInjectedOnlyIntoProviders() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.A> aProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            @Singleton
            InjectionTest.A provideA() {
                return new InjectionTest.A();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.aProvider.get()).isSameAs(entryPoint.aProvider.get());
    }

    @Test
    public void moduleOverrides() {
        class TestEntryPoint {
            @Inject
            Provider<InjectionTest.E> eProvider;
        }
        @Module(injects = TestEntryPoint.class)
        class BaseModule {
            @Provides
            InjectionTest.F provideF() {
                throw new AssertionError();
            }

            @Provides
            InjectionTest.E provideE(InjectionTest.F f) {
                return new InjectionTest.E(f);
            }
        }
        @Module(overrides = true)
        class OverridesModule {
            @Provides
            InjectionTest.F provideF() {
                return new InjectionTest.F();
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new BaseModule(), new OverridesModule()).inject(entryPoint);
        InjectionTest.E e = entryPoint.eProvider.get();
        assertThat(e).isNotNull();
        assertThat(e.f).isNotNull();
    }

    @Test
    public void noJitBindingsForInterfaces() {
        class TestEntryPoint {
            @Inject
            RandomAccess randomAccess;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {}
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.validate();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void objectGraphGetInterface() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        @Module(injects = Runnable.class)
        class TestModule {
            @Provides
            Runnable provideRunnable() {
                return runnable;
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        graph.validate();
        assertThat(graph.get(Runnable.class)).isSameAs(runnable);
    }

    @Test
    public void noProvideBindingsForAbstractClasses() {
        class TestEntryPoint {
            @Inject
            AbstractList abstractList;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {}
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.validate();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    static class ExtendsParameterizedType extends AbstractList<Integer> {
        @Inject
        String string;

        @Override
        public Integer get(int i) {
            throw new AssertionError();
        }

        @Override
        public int size() {
            throw new AssertionError();
        }
    }

    /**
     * We've had bugs where we look for the wrong keys when a class extends a
     * parameterized class. Explicitly test that we can inject such classes.
     */
    @Test
    public void extendsParameterizedType() {
        class TestEntryPoint {
            @Inject
            InjectionTest.ExtendsParameterizedType extendsParameterizedType;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            String provideString() {
                return "injected";
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.extendsParameterizedType.string).isEqualTo("injected");
    }

    @Test
    public void injectParameterizedType() {
        class TestEntryPoint {
            @Inject
            List<String> listOfStrings;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            List<String> provideList() {
                return Arrays.asList("a", "b");
            }
        }
        TestEntryPoint entryPoint = new TestEntryPoint();
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(entryPoint);
        assertThat(entryPoint.listOfStrings).isEqualTo(Arrays.asList("a", "b"));
    }

    @Test
    public void injectWildcardType() {
        class TestEntryPoint {
            @Inject
            List<? extends Number> listOfNumbers;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            List<? extends Number> provideList() {
                return Arrays.asList(1, 2);
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule());
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    static class Parameterized<T> {
        @Inject
        String string;
    }

    @Test
    public void noConstructorInjectionsForClassesWithTypeParameters() {
        class TestEntryPoint {
            @Inject
            InjectionTest.Parameterized<Long> parameterized;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            String provideString() {
                return "injected";
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.validate();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void moduleWithNoProvidesMethods() {
        @Module
        class TestModule {}
        ObjectGraph.createWith(new TestingLoader(), new TestModule());
    }

    @Test
    public void getInstance() {
        final AtomicInteger next = new AtomicInteger(0);
        @Module(injects = Integer.class)
        class TestModule {
            @Provides
            Integer provideInteger() {
                return next.getAndIncrement();
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        assertThat(((int) (graph.get(Integer.class)))).isEqualTo(0);
        assertThat(((int) (graph.get(Integer.class)))).isEqualTo(1);
    }

    @Test
    public void getInstanceRequiresEntryPoint() {
        @Module
        class TestModule {
            @Provides
            Integer provideInteger() {
                throw new AssertionError();
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.get(Integer.class);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void getInstanceOfPrimitive() {
        @Module(injects = int.class)
        class TestModule {
            @Provides
            int provideInt() {
                return 1;
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        Assert.assertEquals(1, ((int) (graph.get(int.class))));
    }

    @Test
    public void getInstanceOfArray() {
        @Module(injects = int[].class)
        class TestModule {
            @Provides
            int[] provideIntArray() {
                return new int[]{ 1, 2, 3 };
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        Assert.assertEquals("[1, 2, 3]", Arrays.toString(graph.get(int[].class)));
    }

    @Test
    public void getInstanceAndInjectMembersUseDifferentKeys() {
        class BoundTwoWays {
            @Inject
            String s;
        }
        @Module(injects = BoundTwoWays.class)
        class TestModule {
            @Provides
            BoundTwoWays provideBoundTwoWays() {
                BoundTwoWays result = new BoundTwoWays();
                result.s = "Pepsi";
                return result;
            }

            @Provides
            String provideString() {
                return "Coke";
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        BoundTwoWays provided = graph.get(BoundTwoWays.class);
        Assert.assertEquals("Pepsi", provided.s);
        BoundTwoWays membersInjected = new BoundTwoWays();
        graph.inject(membersInjected);
        Assert.assertEquals("Coke", membersInjected.s);
    }

    static class NoInjections {
        NoInjections(Void noDefaultConstructorEither) {
        }
    }

    @Test
    public void entryPointNeedsNoInjectAnnotation() {
        @Module(injects = InjectionTest.NoInjections.class)
        class TestModule {}
        ObjectGraph.createWith(new TestingLoader(), new TestModule()).validate();
    }

    static class InjectMembersOnly {
        InjectMembersOnly(Void noInjectableConstructor) {
        }

        @Inject
        String string;
    }

    @Test
    public void cannotGetOnMembersOnlyInjectionPoint() {
        @Module(injects = InjectionTest.InjectMembersOnly.class)
        class TestModule {
            @Provides
            String provideString() {
                return "injected";
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.get(InjectionTest.InjectMembersOnly.class);
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        InjectionTest.InjectMembersOnly instance = new InjectionTest.InjectMembersOnly(null);
        graph.inject(instance);
        assertThat(instance.string).isEqualTo("injected");
    }

    @Test
    public void nonEntryPointNeedsInjectAnnotation() {
        @Module
        class TestModule {
            @Provides
            String provideString(InjectionTest.NoInjections noInjections) {
                throw new AssertionError();
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.validate();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    static class TwoAtInjectConstructors {
        @Inject
        TwoAtInjectConstructors() {
        }

        @Inject
        TwoAtInjectConstructors(String s) {
        }
    }

    @Test
    public void twoAtInjectConstructorsIsRejected() {
        @Module(injects = InjectionTest.TwoAtInjectConstructors.class)
        class TestModule {
            @Provides
            String provideString() {
                throw new AssertionError();
            }
        }
        ObjectGraph graph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        try {
            graph.validate();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void runtimeProvidesMethodsExceptionsAreNotWrapped() {
        class TestEntryPoint {
            @Inject
            String string;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            String provideString() {
                throw new ClassCastException("foo");
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(new TestEntryPoint());
            Assert.fail();
        } catch (ClassCastException e) {
            assertThat(e.getMessage()).isEqualTo("foo");
        }
    }

    static class ThrowsOnConstruction {
        @Inject
        ThrowsOnConstruction() {
            throw new ClassCastException("foo");
        }
    }

    @Test
    public void runtimeConstructorExceptionsAreNotWrapped() {
        @Module(injects = InjectionTest.ThrowsOnConstruction.class)
        class TestModule {}
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule()).get(InjectionTest.ThrowsOnConstruction.class);
            Assert.fail();
        } catch (ClassCastException e) {
            assertThat(e.getMessage()).isEqualTo("foo");
        }
    }

    static class SingletonLinkedFromExtension {
        @Inject
        InjectionTest.C c;// Singleton.

    }

    @Module(complete = false, injects = InjectionTest.C.class)
    static class RootModule {}

    @Module(addsTo = InjectionTest.RootModule.class, injects = InjectionTest.SingletonLinkedFromExtension.class)
    static class ExtensionModule {}

    @Test
    public void testSingletonLinkingThroughExtensionGraph() {
        ObjectGraph root = ObjectGraph.createWith(new TestingLoader(), new InjectionTest.RootModule());
        // DO NOT CALL root.get(C.class)) HERE to get forced-linking behaviour from plus();
        ObjectGraph extension = root.plus(new InjectionTest.ExtensionModule());
        assertThat(extension.get(InjectionTest.SingletonLinkedFromExtension.class).c).isSameAs(root.get(InjectionTest.C.class));
    }

    @Test
    public void privateFieldsFail() {
        class Test {
            @Inject
            private Object nope;
        }
        @Module(injects = Test.class)
        class TestModule {
            @Provides
            Object provideObject() {
                return null;
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule()).inject(new Test());
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("Can't inject private field: ");
        }
    }

    @Test
    public void privateConstructorsFail() {
        class Test {
            @Inject
            private Test() {
            }
        }
        @Module(injects = Test.class)
        class TestModule {}
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule()).get(Test.class);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("Can't inject private constructor: ");
        }
    }

    /**
     * https://github.com/square/dagger/issues/231
     */
    @Test
    public void atInjectAlwaysRequiredForConstruction() {
        @Module(injects = ArrayList.class)
        class TestModule {}
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        objectGraph.validate();
        try {
            objectGraph.get(ArrayList.class);
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("Unable to create binding for java.util.ArrayList");
        }
    }
}

