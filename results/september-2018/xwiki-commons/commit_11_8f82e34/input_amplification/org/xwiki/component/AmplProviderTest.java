package org.xwiki.component;


import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.embed.EmbeddableComponentManagerTest;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentRepositoryException;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;


public class AmplProviderTest {
    @ComponentRole
    public static interface TestComponentRole {}

    @Component
    @Singleton
    public static class TestComponentWithProviders implements AmplProviderTest.TestComponentRole {
        @Inject
        public Provider<String> provider1;

        @Inject
        @Named("another")
        public Provider<String> provider12;

        @Inject
        public Provider<Integer> provider2;

        @Inject
        public Provider<List<EmbeddableComponentManagerTest.Role>> providerList;

        @Inject
        public Provider<Map<String, EmbeddableComponentManagerTest.Role>> providerMap;
    }

    public static class TestProvider1 implements Provider<String> {
        @Override
        public String get() {
            return "value";
        }
    }

    @Named("another")
    public static class TestProvider12 implements Provider<String> {
        @Override
        public String get() {
            return "another value";
        }
    }

    public static class TestProvider2 implements Provider<Integer> {
        @Override
        public Integer get() {
            return 1;
        }
    }

    @Component
    @Named("exception")
    @Singleton
    public static class TestComponentWithProviderInException implements AmplProviderTest.TestComponentRole {
        @Inject
        @Named("exception")
        public Provider<String> providerWithExceptionInInitialize;
    }

    @Named("exception")
    public static class TestProviderWithExceptionInInitialize implements Provider<String> , Initializable {
        @Override
        public void initialize() throws InitializationException {
            throw new InitializationException("Some error in init");
        }

        @Override
        public String get() {
            throw new RuntimeException("should not be called!");
        }
    }

    @Test(timeout = 10000)
    public void loadAndInjectProviders_remove45_failAssert2() throws Exception, ComponentLookupException, ComponentRepositoryException {
        try {
            EmbeddableComponentManager cm = new EmbeddableComponentManager();
            DefaultComponentDescriptor<EmbeddableComponentManagerTest.Role> cd1 = new DefaultComponentDescriptor<EmbeddableComponentManagerTest.Role>();
            cd1.setRole(EmbeddableComponentManagerTest.Role.class);
            cd1.setRoleHint("hint1");
            cd1.setImplementation(EmbeddableComponentManagerTest.RoleImpl.class);
            cm.registerComponent(cd1);
            DefaultComponentDescriptor<EmbeddableComponentManagerTest.Role> cd2 = new DefaultComponentDescriptor<EmbeddableComponentManagerTest.Role>();
            cd2.setRole(EmbeddableComponentManagerTest.Role.class);
            cd2.setRoleHint("hint2");
            cd2.setImplementation(EmbeddableComponentManagerTest.RoleImpl.class);
            cm.registerComponent(cd2);
            AmplProviderTest.TestComponentWithProviders component = cm.getInstance(AmplProviderTest.TestComponentRole.class);
            component.provider1.get();
            component.provider12.get();
            Integer.valueOf(1);
            component.provider2.get();
            component.providerList.get().size();
            component.providerMap.get().size();
            org.junit.Assert.fail("loadAndInjectProviders_remove45 should have thrown ComponentLookupException");
        } catch (ComponentLookupException expected) {
            Assert.assertEquals("Can\'t find descriptor for the component [role = [interface org.xwiki.component.AmplProviderTest$TestComponentRole] hint = [default]]", expected.getMessage());
        }
    }
}

