package cucumber.runtime.java.guice.impl;


import CucumberModules.SCENARIO;
import Scopes.SINGLETON;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import cucumber.api.guice.CucumberModules;
import cucumber.api.java.ObjectFactory;
import cucumber.runtime.java.guice.ScenarioScoped;
import cucumber.runtime.java.guice.matcher.ElementsAreAllEqualMatcher;
import cucumber.runtime.java.guice.matcher.ElementsAreAllUniqueMatcher;
import java.util.List;
import javax.inject.Singleton;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GuiceFactoryTest {
    private ObjectFactory factory;

    private List<?> instancesFromSameScenario;

    private List<?> instancesFromDifferentScenarios;

    @Test
    public void factoryCanBeIntantiatedWithDefaultConstructor() throws Exception {
        factory = new GuiceFactory();
        Assert.assertThat(factory, CoreMatchers.notNullValue());
    }

    @Test
    public void factoryCanBeIntantiatedWithArgConstructor() {
        factory = new GuiceFactory(Guice.createInjector());
        Assert.assertThat(factory, CoreMatchers.notNullValue());
    }

    @Test
    public void factoryStartFailsIfScenarioScopeIsNotBound() {
        factory = new GuiceFactory(Guice.createInjector());
        try {
            factory.start();
            Assert.fail();
        } catch (ConfigurationException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("No implementation for cucumber.runtime.java.guice.ScenarioScope was bound"));
        }
    }

    static class UnscopedClass {}

    @Test
    public void shouldGiveNewInstancesOfUnscopedClassWithinAScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule()));
        instancesFromSameScenario = getInstancesFromSameScenario(factory, GuiceFactoryTest.UnscopedClass.class);
        Assert.assertThat(instancesFromSameScenario, ElementsAreAllUniqueMatcher.elementsAreAllUnique());
    }

    @Test
    public void shouldGiveNewInstanceOfUnscopedClassForEachScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule()));
        instancesFromDifferentScenarios = getInstancesFromDifferentScenarios(factory, GuiceFactoryTest.UnscopedClass.class);
        Assert.assertThat(instancesFromDifferentScenarios, ElementsAreAllUniqueMatcher.elementsAreAllUnique());
    }

    @ScenarioScoped
    static class AnnotatedScenarioScopedClass {}

    @Test
    public void shouldGiveTheSameInstanceOfAnnotatedScenarioScopedClassWithinAScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule()));
        instancesFromSameScenario = getInstancesFromSameScenario(factory, GuiceFactoryTest.AnnotatedScenarioScopedClass.class);
        Assert.assertThat(instancesFromSameScenario, ElementsAreAllEqualMatcher.elementsAreAllEqual());
    }

    @Test
    public void shouldGiveNewInstanceOfAnnotatedScenarioScopedClassForEachScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule()));
        instancesFromDifferentScenarios = getInstancesFromDifferentScenarios(factory, GuiceFactoryTest.AnnotatedScenarioScopedClass.class);
        Assert.assertThat(instancesFromDifferentScenarios, ElementsAreAllUniqueMatcher.elementsAreAllUnique());
    }

    @Singleton
    static class AnnotatedSingletonClass {}

    @Test
    public void shouldGiveTheSameInstanceOfAnnotatedSingletonClassWithinAScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule()));
        instancesFromSameScenario = getInstancesFromSameScenario(factory, GuiceFactoryTest.AnnotatedSingletonClass.class);
        Assert.assertThat(instancesFromSameScenario, ElementsAreAllEqualMatcher.elementsAreAllEqual());
    }

    @Test
    public void shouldGiveTheSameInstanceOfAnnotatedSingletonClassForEachScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule()));
        instancesFromDifferentScenarios = getInstancesFromDifferentScenarios(factory, GuiceFactoryTest.AnnotatedSingletonClass.class);
        Assert.assertThat(instancesFromDifferentScenarios, ElementsAreAllEqualMatcher.elementsAreAllEqual());
    }

    static class BoundScenarioScopedClass {}

    final AbstractModule boundScenarioScopedClassModule = new AbstractModule() {
        @Override
        protected void configure() {
            bind(GuiceFactoryTest.BoundScenarioScopedClass.class).in(ScenarioScoped.class);
        }
    };

    @Test
    public void shouldGiveTheSameInstanceOfBoundScenarioScopedClassWithinAScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule(), boundScenarioScopedClassModule));
        instancesFromSameScenario = getInstancesFromSameScenario(factory, GuiceFactoryTest.BoundScenarioScopedClass.class);
        Assert.assertThat(instancesFromSameScenario, ElementsAreAllEqualMatcher.elementsAreAllEqual());
    }

    @Test
    public void shouldGiveNewInstanceOfBoundScenarioScopedClassForEachScenario() {
        factory = new GuiceFactory(injector(SCENARIO, boundScenarioScopedClassModule));
        instancesFromDifferentScenarios = getInstancesFromDifferentScenarios(factory, GuiceFactoryTest.BoundScenarioScopedClass.class);
        Assert.assertThat(instancesFromDifferentScenarios, ElementsAreAllUniqueMatcher.elementsAreAllUnique());
    }

    static class BoundSingletonClass {}

    final AbstractModule boundSingletonClassModule = new AbstractModule() {
        @Override
        protected void configure() {
            bind(GuiceFactoryTest.BoundSingletonClass.class).in(SINGLETON);
        }
    };

    @Test
    public void shouldGiveTheSameInstanceOfBoundSingletonClassWithinAScenario() {
        factory = new GuiceFactory(injector(SCENARIO, boundSingletonClassModule));
        instancesFromSameScenario = getInstancesFromSameScenario(factory, GuiceFactoryTest.BoundSingletonClass.class);
        Assert.assertThat(instancesFromSameScenario, ElementsAreAllEqualMatcher.elementsAreAllEqual());
    }

    @Test
    public void shouldGiveTheSameInstanceOfBoundSingletonClassForEachScenario() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule(), boundSingletonClassModule));
        instancesFromDifferentScenarios = getInstancesFromDifferentScenarios(factory, GuiceFactoryTest.BoundSingletonClass.class);
        Assert.assertThat(instancesFromDifferentScenarios, ElementsAreAllEqualMatcher.elementsAreAllEqual());
    }

    @Test
    public void shouldGiveNewInstanceOfAnnotatedSingletonClassForEachScenarioWhenOverridingModuleBindingIsScenarioScope() {
        factory = new GuiceFactory(injector(CucumberModules.createScenarioModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(GuiceFactoryTest.AnnotatedSingletonClass.class).in(ScenarioScoped.class);
            }
        }));
        instancesFromDifferentScenarios = getInstancesFromDifferentScenarios(factory, GuiceFactoryTest.AnnotatedSingletonClass.class);
        Assert.assertThat(instancesFromDifferentScenarios, ElementsAreAllUniqueMatcher.elementsAreAllUnique());
    }
}

