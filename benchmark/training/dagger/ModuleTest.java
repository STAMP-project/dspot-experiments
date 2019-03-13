/**
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


import dagger.internal.TestingLoader;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


// TODO: Migrate to compiler.
@RunWith(JUnit4.class)
public final class ModuleTest {
    static class TestEntryPoint {
        @Inject
        String s;
    }

    @Module(injects = ModuleTest.TestEntryPoint.class)
    static class ModuleWithEntryPoint {}

    @Test
    public void childModuleWithEntryPoint() {
        @Module(includes = ModuleTest.ModuleWithEntryPoint.class)
        class TestModule {
            @Provides
            String provideString() {
                return "injected";
            }
        }
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        ModuleTest.TestEntryPoint entryPoint = objectGraph.get(ModuleTest.TestEntryPoint.class);
        assertThat(entryPoint.s).isEqualTo("injected");
    }

    static class TestStaticInjection {
        @Inject
        static String s;
    }

    @Module(staticInjections = ModuleTest.TestStaticInjection.class)
    static class ModuleWithStaticInjection {}

    @Test
    public void childModuleWithStaticInjection() {
        @Module(includes = ModuleTest.ModuleWithStaticInjection.class)
        class TestModule {
            @Provides
            String provideString() {
                return "injected";
            }
        }
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        ModuleTest.TestStaticInjection.s = null;
        objectGraph.injectStatics();
        assertThat(ModuleTest.TestStaticInjection.s).isEqualTo("injected");
    }

    @Module
    static class ModuleWithBinding {
        @Provides
        String provideString() {
            return "injected";
        }
    }

    @Test
    public void childModuleWithBinding() {
        @Module(injects = ModuleTest.TestEntryPoint.class, includes = ModuleTest.ModuleWithBinding.class)
        class TestModule {}
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        ModuleTest.TestEntryPoint entryPoint = new ModuleTest.TestEntryPoint();
        objectGraph.inject(entryPoint);
        assertThat(entryPoint.s).isEqualTo("injected");
    }

    @Module(includes = ModuleTest.ModuleWithBinding.class)
    static class ModuleWithChildModule {}

    @Test
    public void childModuleWithChildModule() {
        @Module(injects = ModuleTest.TestEntryPoint.class, includes = ModuleTest.ModuleWithChildModule.class)
        class TestModule {}
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new TestModule());
        ModuleTest.TestEntryPoint entryPoint = new ModuleTest.TestEntryPoint();
        objectGraph.inject(entryPoint);
        assertThat(entryPoint.s).isEqualTo("injected");
    }

    @Module
    static class ModuleWithConstructor {
        private final String value;

        ModuleWithConstructor(String value) {
            this.value = value;
        }

        @Provides
        String provideString() {
            return value;
        }
    }

    @Test
    public void childModuleMissingManualConstruction() {
        @Module(includes = ModuleTest.ModuleWithConstructor.class)
        class TestModule {}
        try {
            ObjectGraph.createWith(new TestingLoader(), new TestModule());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void childModuleWithManualConstruction() {
        @Module(injects = ModuleTest.TestEntryPoint.class, includes = ModuleTest.ModuleWithConstructor.class)
        class TestModule {}
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new ModuleTest.ModuleWithConstructor("a"), new TestModule());
        ModuleTest.TestEntryPoint entryPoint = new ModuleTest.TestEntryPoint();
        objectGraph.inject(entryPoint);
        assertThat(entryPoint.s).isEqualTo("a");
    }

    static class A {}

    static class B {
        @Inject
        ModuleTest.A a;
    }

    @Module(injects = ModuleTest.A.class)
    public static class TestModuleA {
        @Provides
        ModuleTest.A a() {
            return new ModuleTest.A();
        }
    }

    @Module(includes = ModuleTest.TestModuleA.class, injects = ModuleTest.B.class)
    public static class TestModuleB {}

    @Test
    public void autoInstantiationOfModules() {
        // Have to make these non-method-scoped or instantiation errors occur.
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), ModuleTest.TestModuleA.class);
        assertThat(objectGraph.get(ModuleTest.A.class)).isNotNull();
    }

    @Test
    public void autoInstantiationOfIncludedModules() {
        // Have to make these non-method-scoped or instantiation errors occur.
        ObjectGraph objectGraph = ObjectGraph.createWith(new TestingLoader(), new ModuleTest.TestModuleB());// TestModuleA auto-created.

        assertThat(objectGraph.get(ModuleTest.A.class)).isNotNull();
        assertThat(objectGraph.get(ModuleTest.B.class).a).isNotNull();
    }

    static class ModuleMissingModuleAnnotation {}

    @Module(includes = ModuleTest.ModuleMissingModuleAnnotation.class)
    static class ChildModuleMissingModuleAnnotation {}

    @Test
    public void childModuleMissingModuleAnnotation() {
        try {
            ObjectGraph.createWith(new TestingLoader(), new ModuleTest.ChildModuleMissingModuleAnnotation());
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("No @Module on dagger.ModuleTest$ModuleMissingModuleAnnotation");
        }
    }

    @Module
    static class ThreadModule extends Thread {}

    @Test
    public void moduleExtendingClassThrowsException() {
        try {
            ObjectGraph.createWith(new TestingLoader(), new ModuleTest.ThreadModule());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).startsWith("Modules must not extend from other classes: ");
        }
    }

    @Test
    public void provideProviderFails() {
        @Module
        class ProvidesProviderModule {
            @Provides
            Provider<Object> provideObject() {
                return null;
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new ProvidesProviderModule());
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).startsWith("@Provides method must not return Provider directly: ");
            assertThat(e.getMessage()).endsWith("ProvidesProviderModule.provideObject");
        }
    }

    @Test
    public void provideRawProviderFails() {
        @Module
        class ProvidesRawProviderModule {
            @Provides
            Provider provideObject() {
                return null;
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new ProvidesRawProviderModule());
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).startsWith("@Provides method must not return Provider directly: ");
            assertThat(e.getMessage()).endsWith("ProvidesRawProviderModule.provideObject");
        }
    }

    @Test
    public void provideLazyFails() {
        @Module
        class ProvidesLazyModule {
            @Provides
            Lazy<Object> provideObject() {
                return null;
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new ProvidesLazyModule());
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).startsWith("@Provides method must not return Lazy directly: ");
            assertThat(e.getMessage()).endsWith("ProvidesLazyModule.provideObject");
        }
    }

    @Test
    public void provideRawLazyFails() {
        @Module
        class ProvidesRawLazyModule {
            @Provides
            Lazy provideObject() {
                return null;
            }
        }
        try {
            ObjectGraph.createWith(new TestingLoader(), new ProvidesRawLazyModule());
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).startsWith("@Provides method must not return Lazy directly: ");
            assertThat(e.getMessage()).endsWith("ProvidesRawLazyModule.provideObject");
        }
    }
}

