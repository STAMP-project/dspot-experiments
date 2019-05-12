package org.xwiki.component.embed;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.xwiki.component.annotation.DisposePriority;
import org.xwiki.component.descriptor.ComponentDescriptor;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.Disposable;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;


public class AmplEmbeddableComponentManagerTest {
    public static interface Role {}

    public static class RoleImpl implements AmplEmbeddableComponentManagerTest.Role {}

    public static class OtherRoleImpl implements AmplEmbeddableComponentManagerTest.Role {}

    private static String lastDisposedComponent;

    public static class InitializableRoleImpl implements AmplEmbeddableComponentManagerTest.Role , Initializable {
        private boolean initialized = false;

        @Override
        public void initialize() throws InitializationException {
            this.initialized = true;
        }

        public boolean isInitialized() {
            return this.initialized;
        }
    }

    public static class DisposableRoleImpl implements AmplEmbeddableComponentManagerTest.Role , Disposable {
        private boolean finalized = false;

        @Override
        public void dispose() throws ComponentLifecycleException {
            this.finalized = true;
            AmplEmbeddableComponentManagerTest.lastDisposedComponent = "DisposableRoleImpl";
        }

        public boolean isFinalized() {
            return this.finalized;
        }
    }

    @DisposePriority(2000)
    public static class DisposableWithPriorityRoleImpl implements AmplEmbeddableComponentManagerTest.Role , Disposable {
        private boolean finalized = false;

        @Override
        public void dispose() throws ComponentLifecycleException {
            this.finalized = true;
            AmplEmbeddableComponentManagerTest.lastDisposedComponent = "DisposableWithPriorityRoleImpl";
        }

        public boolean isFinalized() {
            return this.finalized;
        }
    }

    public static class LoggingRoleImpl implements AmplEmbeddableComponentManagerTest.Role {
        private Logger logger;

        public Logger getLogger() {
            return this.logger;
        }
    }

    @Test(timeout = 10000)
    public void testHasComponent() throws Exception {
        EmbeddableComponentManager ecm = new EmbeddableComponentManager();
        DefaultComponentDescriptor<AmplEmbeddableComponentManagerTest.Role> d1 = new DefaultComponentDescriptor<AmplEmbeddableComponentManagerTest.Role>();
        d1.setRoleType(AmplEmbeddableComponentManagerTest.Role.class);
        d1.setRoleHint("default");
        ecm.registerComponent(d1);
        Assert.assertTrue(ecm.hasComponent(AmplEmbeddableComponentManagerTest.Role.class));
    }

    private ComponentManager createParentComponentManager() throws Exception {
        return createParentComponentManager(null);
    }

    private ComponentManager createParentComponentManager(String hint) throws Exception {
        EmbeddableComponentManager parent = new EmbeddableComponentManager();
        DefaultComponentDescriptor<AmplEmbeddableComponentManagerTest.Role> cd = new DefaultComponentDescriptor<AmplEmbeddableComponentManagerTest.Role>();
        cd.setRoleType(AmplEmbeddableComponentManagerTest.Role.class);
        cd.setImplementation(AmplEmbeddableComponentManagerTest.RoleImpl.class);
        if (hint != null) {
            cd.setRoleHint(hint);
        }
        parent.registerComponent(cd);
        return parent;
    }

    @Test(timeout = 10000)
    public void testRelease() throws Exception {
        final EmbeddableComponentManager ecm = new EmbeddableComponentManager();
        Assert.assertNull(getParent());
    }

    public static class ComponentDescriptorRoleImpl implements AmplEmbeddableComponentManagerTest.Role {
        private ComponentDescriptor<AmplEmbeddableComponentManagerTest.ComponentDescriptorRoleImpl> descriptor;

        public ComponentDescriptor<AmplEmbeddableComponentManagerTest.ComponentDescriptorRoleImpl> getComponentDescriptor() {
            return this.descriptor;
        }
    }
}

