package org.robolectric.util.inject;


import com.google.auto.service.AutoService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.util.inject.Injector.Builder;


@RunWith(JUnit4.class)
public class InjectorTest {
    private Builder builder;

    private Injector injector;

    private final List<Class<?>> pluginClasses = new ArrayList<>();

    @Test
    public void whenImplSpecified_shouldProvideInstance() throws Exception {
        injector = builder.bind(InjectorTest.Thing.class, InjectorTest.MyThing.class).build();
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isInstanceOf(InjectorTest.MyThing.class);
    }

    @Test
    public void whenImplSpecified_shouldUseSameInstance() throws Exception {
        injector = builder.bind(InjectorTest.Thing.class, InjectorTest.MyThing.class).build();
        InjectorTest.Thing thing = injector.getInstance(InjectorTest.Thing.class);
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isSameAs(thing);
    }

    @Test
    public void whenServiceSpecified_shouldProvideInstance() throws Exception {
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isInstanceOf(InjectorTest.ThingFromServiceConfig.class);
    }

    @Test
    public void whenServiceSpecified_shouldUseSameInstance() throws Exception {
        InjectorTest.Thing thing = injector.getInstance(InjectorTest.Thing.class);
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isSameAs(thing);
    }

    @Test
    public void whenConcreteClassRequested_shouldProvideInstance() throws Exception {
        assertThat(injector.getInstance(InjectorTest.MyUmm.class)).isInstanceOf(InjectorTest.MyUmm.class);
    }

    @Test
    public void whenDefaultSpecified_shouldProvideInstance() throws Exception {
        injector = builder.bindDefault(InjectorTest.Umm.class, InjectorTest.MyUmm.class).build();
        assertThat(injector.getInstance(InjectorTest.Umm.class)).isInstanceOf(InjectorTest.MyUmm.class);
    }

    @Test
    public void whenDefaultSpecified_shouldUseSameInstance() throws Exception {
        InjectorTest.Thing thing = injector.getInstance(InjectorTest.Thing.class);
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isSameAs(thing);
    }

    @Test
    public void whenNoImplOrServiceOrDefaultSpecified_shouldThrow() throws Exception {
        try {
            injector.getInstance(InjectorTest.Umm.class);
            Assert.fail();
        } catch (InjectionException e) {
            // ok
        }
    }

    @Test
    public void registerDefaultService_providesFallbackImplOnlyIfNoServiceSpecified() throws Exception {
        builder.bindDefault(InjectorTest.Thing.class, InjectorTest.MyThing.class);
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isInstanceOf(InjectorTest.ThingFromServiceConfig.class);
        builder.bindDefault(InjectorTest.Umm.class, InjectorTest.MyUmm.class);
        assertThat(injector.getInstance(InjectorTest.Thing.class)).isInstanceOf(InjectorTest.ThingFromServiceConfig.class);
    }

    @Test
    public void shouldPreferSingularPublicConstructorAnnotatedInject() throws Exception {
        injector = builder.bind(InjectorTest.Thing.class, InjectorTest.MyThing.class).bind(InjectorTest.Umm.class, InjectorTest.MyUmm.class).build();
        InjectorTest.Umm umm = injector.getInstance(InjectorTest.Umm.class);
        assertThat(umm).isNotNull();
        assertThat(umm).isInstanceOf(InjectorTest.MyUmm.class);
        InjectorTest.MyUmm myUmm = ((InjectorTest.MyUmm) (umm));
        assertThat(myUmm.thing).isNotNull();
        assertThat(myUmm.thing).isInstanceOf(InjectorTest.MyThing.class);
        assertThat(myUmm.thing).isSameAs(injector.getInstance(InjectorTest.Thing.class));
    }

    @Test
    public void shouldAcceptSingularPublicConstructorWithoutInjectAnnotation() throws Exception {
        injector = builder.bind(InjectorTest.Thing.class, InjectorTest.MyThing.class).bind(InjectorTest.Umm.class, InjectorTest.MyUmmNoInject.class).build();
        InjectorTest.Umm umm = injector.getInstance(InjectorTest.Umm.class);
        assertThat(umm).isNotNull();
        assertThat(umm).isInstanceOf(InjectorTest.MyUmmNoInject.class);
        InjectorTest.MyUmmNoInject myUmm = ((InjectorTest.MyUmmNoInject) (umm));
        assertThat(myUmm.thing).isNotNull();
        assertThat(myUmm.thing).isInstanceOf(InjectorTest.MyThing.class);
        assertThat(myUmm.thing).isSameAs(injector.getInstance(InjectorTest.Thing.class));
    }

    @Test
    public void whenArrayRequested_mayReturnMultiplePlugins() throws Exception {
        InjectorTest.MultiThing[] multiThings = injector.getInstance(InjectorTest.MultiThing[].class);
        // X comes first because it has a higher priority
        assertThat(classesOf(multiThings)).containsExactly(InjectorTest.MultiThingX.class, InjectorTest.MultiThingA.class).inOrder();
    }

    @Test
    public void whenCollectionRequested_mayReturnMultiplePlugins() throws Exception {
        InjectorTest.ThingRequiringMultiThings it = injector.getInstance(InjectorTest.ThingRequiringMultiThings.class);
        // X comes first because it has a higher priority
        assertThat(classesOf(it.multiThings)).containsExactly(InjectorTest.MultiThingX.class, InjectorTest.MultiThingA.class).inOrder();
    }

    @Test
    public void whenListRequested_itIsUnmodifiable() throws Exception {
        InjectorTest.ThingRequiringMultiThings it = injector.getInstance(InjectorTest.ThingRequiringMultiThings.class);
        try {
            it.multiThings.clear();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Test
    public void autoFactory_factoryMethodsCreateNewInstances() throws Exception {
        injector = builder.bind(InjectorTest.Umm.class, InjectorTest.MyUmm.class).build();
        InjectorTest.FooFactory factory = injector.getInstance(InjectorTest.FooFactory.class);
        InjectorTest.Foo chauncey = factory.create("Chauncey");
        assertThat(chauncey.name).isEqualTo("Chauncey");
        InjectorTest.Foo anotherChauncey = factory.create("Chauncey");
        assertThat(anotherChauncey).isNotSameAs(chauncey);
    }

    @Test
    public void autoFactory_injectedValuesComeFromSuperInjector() throws Exception {
        injector = builder.bind(InjectorTest.Umm.class, InjectorTest.MyUmm.class).build();
        InjectorTest.FooFactory factory = injector.getInstance(InjectorTest.FooFactory.class);
        InjectorTest.Foo chauncey = factory.create("Chauncey");
        assertThat(chauncey.thing).isSameAs(injector.getInstance(InjectorTest.Thing.class));
    }

    @Test
    public void whenFactoryRequested_createsInjectedFactory() throws Exception {
        injector = builder.bind(InjectorTest.Umm.class, InjectorTest.MyUmm.class).build();
        InjectorTest.FooFactory factory = injector.getInstance(InjectorTest.FooFactory.class);
        InjectorTest.Foo chauncey = factory.create("Chauncey");
        assertThat(chauncey.name).isEqualTo("Chauncey");
        InjectorTest.Foo anotherChauncey = factory.create("Chauncey");
        assertThat(anotherChauncey).isNotSameAs(chauncey);
        assertThat(chauncey.thing).isSameAs(injector.getInstance(InjectorTest.Thing.class));
    }

    @Test
    public void scopedInjector_shouldCheckParentBeforeProvidingDefault() throws Exception {
        injector = builder.build();
        Injector subInjector = build();
        InjectorTest.MyUmm subUmm = subInjector.getInstance(InjectorTest.MyUmm.class);
        assertThat(injector.getInstance(InjectorTest.MyUmm.class)).isSameAs(subUmm);
    }

    @Test
    public void shouldInjectByNamedKeys() throws Exception {
        injector = builder.bind(new Injector.Key<>(.class, "namedThing"), "named value").bind(String.class, "unnamed value").build();
        InjectorTest.NamedParams namedParams = injector.getInstance(InjectorTest.NamedParams.class);
        assertThat(namedParams.withName).isEqualTo("named value");
        assertThat(namedParams.withoutName).isEqualTo("unnamed value");
    }

    @Test
    public void shouldPreferPluginsOverConcreteClass() throws Exception {
        PluginFinder pluginFinder = new PluginFinder(new MyServiceFinderAdapter(pluginClasses));
        Injector injector = build();
        pluginClasses.add(InjectorTest.SubclassOfConcreteThing.class);
        InjectorTest.ConcreteThing instance = injector.getInstance(InjectorTest.ConcreteThing.class);
        assertThat(instance.getClass()).isEqualTo(InjectorTest.SubclassOfConcreteThing.class);
    }

    @Test
    public void subInjectorIsUsedForResolvingTransitiveDependencies() throws Exception {
        InjectorTest.FakeSandboxManager sandboxManager = injector.getInstance(InjectorTest.FakeSandboxManager.class);
        InjectorTest.FakeSdk runtimeSdk = new InjectorTest.FakeSdk("runtime");
        InjectorTest.FakeSdk compileSdk = new InjectorTest.FakeSdk("compile");
        InjectorTest.FakeSandbox sandbox = sandboxManager.getSandbox(runtimeSdk, compileSdk);
        assertThat(sandbox.runtimeSdk).isSameAs(runtimeSdk);
        assertThat(sandbox.compileSdk).isSameAs(compileSdk);
    }

    @Test
    public void shouldProvideDecentErrorMessages() throws Exception {
        InjectorTest.FakeSandboxManager sandboxManager = injector.getInstance(InjectorTest.FakeSandboxManager.class);
        Exception actualException = null;
        try {
            sandboxManager.brokenGetSandbox();
            Assert.fail();
        } catch (Exception e) {
            actualException = e;
        }
        assertThat(actualException.getMessage()).contains("Failed to resolve dependency: FakeSandbox/FakeSdk/String");
    }

    /**
     * A thing.
     */
    public interface Thing {}

    public static class MyThing implements InjectorTest.Thing {}

    public static class ConcreteThing {}

    public static class SubclassOfConcreteThing extends InjectorTest.ConcreteThing {}

    /**
     * Class for test.
     */
    @AutoService(InjectorTest.Thing.class)
    public static class ThingFromServiceConfig implements InjectorTest.Thing {}

    private interface Umm {}

    public static class MyUmm implements InjectorTest.Umm {
        private final InjectorTest.Thing thing;

        @Inject
        public MyUmm(InjectorTest.Thing thing) {
            this.thing = thing;
        }

        @SuppressWarnings("unused")
        public MyUmm(String thingz) {
            this.thing = null;
        }
    }

    /**
     * Class for test.
     */
    public static class MyUmmNoInject implements InjectorTest.Umm {
        private final InjectorTest.Thing thing;

        public MyUmmNoInject(InjectorTest.Thing thing) {
            this.thing = thing;
        }
    }

    private interface MultiThing {}

    /**
     * Class for test.
     */
    @Priority(-5)
    @AutoService(InjectorTest.MultiThing.class)
    public static class MultiThingA implements InjectorTest.MultiThing {}

    /**
     * Class for test.
     */
    @AutoService(InjectorTest.MultiThing.class)
    public static class MultiThingX implements InjectorTest.MultiThing {}

    /**
     * Class for test.
     */
    public static class ThingRequiringMultiThings {
        private List<InjectorTest.MultiThing> multiThings;

        public ThingRequiringMultiThings(List<InjectorTest.MultiThing> multiThings) {
            this.multiThings = multiThings;
        }
    }

    static class Foo {
        private final InjectorTest.Thing thing;

        private final InjectorTest.Umm umm;

        private final String name;

        public Foo(InjectorTest.Thing thing, InjectorTest.Umm umm, String name) {
            this.thing = thing;
            this.umm = umm;
            this.name = name;
        }
    }

    @AutoFactory
    interface FooFactory {
        InjectorTest.Foo create(String name);
    }

    static class NamedParams {
        private final InjectorTest.Thing thing;

        private final String withName;

        private final String withoutName;

        public NamedParams(InjectorTest.Thing thing, @Named("namedThing")
        String withName, String withoutName) {
            this.thing = thing;
            this.withName = withName;
            this.withoutName = withoutName;
        }
    }

    static class FakeSdk {
        private final String name;

        public FakeSdk(String name) {
            this.name = name;
        }
    }

    static class FakeSandbox {
        private final InjectorTest.FakeSdk runtimeSdk;

        private final InjectorTest.FakeSdk compileSdk;

        private final InjectorTest.FakeSandboxClassLoader sandboxClassLoader;

        public FakeSandbox(@Named("runtimeSdk")
        InjectorTest.FakeSdk runtimeSdk, @Named("compileSdk")
        InjectorTest.FakeSdk compileSdk, InjectorTest.FakeSandboxClassLoader sandboxClassLoader) {
            this.runtimeSdk = runtimeSdk;
            this.compileSdk = compileSdk;
            this.sandboxClassLoader = sandboxClassLoader;
        }
    }

    static class FakeSandboxClassLoader {
        private final InjectorTest.FakeSdk runtimeSdk;

        public FakeSandboxClassLoader(@Named("runtimeSdk")
        InjectorTest.FakeSdk runtimeSdk) {
            this.runtimeSdk = runtimeSdk;
        }
    }

    static class FakeSandboxManager {
        private final InjectorTest.FakeSandboxFactory sandboxFactory;

        public FakeSandboxManager(InjectorTest.FakeSandboxFactory sandboxFactory) {
            this.sandboxFactory = sandboxFactory;
        }

        public InjectorTest.FakeSandbox getSandbox(InjectorTest.FakeSdk runtimeSdk, InjectorTest.FakeSdk compileSdk) {
            return sandboxFactory.createSandbox(runtimeSdk, compileSdk);
        }

        public InjectorTest.FakeSandbox brokenGetSandbox() {
            return sandboxFactory.createSandbox();
        }
    }

    @AutoFactory
    private interface FakeSandboxFactory {
        InjectorTest.FakeSandbox createSandbox(@Named("runtimeSdk")
        InjectorTest.FakeSdk runtimeSdk, @Named("compileSdk")
        InjectorTest.FakeSdk compileSdk);

        InjectorTest.FakeSandbox createSandbox();
    }
}

