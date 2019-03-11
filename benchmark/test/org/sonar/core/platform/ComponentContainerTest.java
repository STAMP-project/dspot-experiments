/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.platform;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.picocontainer.injectors.ProviderAdapter;
import org.sonar.api.Property;
import org.sonar.api.config.PropertyDefinitions;


public class ComponentContainerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldRegisterItself() {
        ComponentContainer container = new ComponentContainer();
        assertThat(container.getComponentByType(ComponentContainer.class)).isSameAs(container);
    }

    @Test
    public void should_start_and_stop() {
        ComponentContainer container = Mockito.spy(new ComponentContainer());
        container.addSingleton(ComponentContainerTest.StartableStoppableComponent.class);
        container.startComponents();
        assertThat(container.getComponentByType(ComponentContainerTest.StartableStoppableComponent.class).started).isTrue();
        assertThat(container.getComponentByType(ComponentContainerTest.StartableStoppableComponent.class).stopped).isFalse();
        Mockito.verify(container).doBeforeStart();
        Mockito.verify(container).doAfterStart();
        container.stopComponents();
        assertThat(container.getComponentByType(ComponentContainerTest.StartableStoppableComponent.class).stopped).isTrue();
    }

    @Test
    public void should_start_and_stop_hierarchy_of_containers() {
        ComponentContainerTest.StartableStoppableComponent parentComponent = new ComponentContainerTest.StartableStoppableComponent();
        final ComponentContainerTest.StartableStoppableComponent childComponent = new ComponentContainerTest.StartableStoppableComponent();
        ComponentContainer parentContainer = new ComponentContainer() {
            @Override
            public void doAfterStart() {
                ComponentContainer childContainer = new ComponentContainer(this);
                childContainer.add(childComponent);
                childContainer.execute();
            }
        };
        parentContainer.add(parentComponent);
        parentContainer.execute();
        assertThat(parentComponent.started).isTrue();
        assertThat(parentComponent.stopped).isTrue();
        assertThat(childComponent.started).isTrue();
        assertThat(childComponent.stopped).isTrue();
    }

    @Test
    public void should_stop_hierarchy_of_containers_on_failure() {
        ComponentContainerTest.StartableStoppableComponent parentComponent = new ComponentContainerTest.StartableStoppableComponent();
        final ComponentContainerTest.StartableStoppableComponent childComponent1 = new ComponentContainerTest.StartableStoppableComponent();
        final ComponentContainerTest.UnstartableComponent childComponent2 = new ComponentContainerTest.UnstartableComponent();
        ComponentContainer parentContainer = new ComponentContainer() {
            @Override
            public void doAfterStart() {
                ComponentContainer childContainer = new ComponentContainer(this);
                childContainer.add(childComponent1);
                childContainer.add(childComponent2);
                childContainer.execute();
            }
        };
        parentContainer.add(parentComponent);
        try {
            parentContainer.execute();
            Assert.fail();
        } catch (Exception e) {
            assertThat(parentComponent.started).isTrue();
            assertThat(parentComponent.stopped).isTrue();
            assertThat(childComponent1.started).isTrue();
            assertThat(childComponent1.stopped).isTrue();
        }
    }

    @Test
    public void testChild() {
        ComponentContainer parent = new ComponentContainer();
        parent.startComponents();
        ComponentContainer child = parent.createChild();
        child.addSingleton(ComponentContainerTest.StartableStoppableComponent.class);
        child.startComponents();
        assertThat(child.getParent()).isSameAs(parent);
        assertThat(parent.getChildren()).containsOnly(child);
        assertThat(child.getComponentByType(ComponentContainer.class)).isSameAs(child);
        assertThat(parent.getComponentByType(ComponentContainer.class)).isSameAs(parent);
        assertThat(child.getComponentByType(ComponentContainerTest.StartableStoppableComponent.class)).isNotNull();
        assertThat(parent.getComponentByType(ComponentContainerTest.StartableStoppableComponent.class)).isNull();
        parent.stopComponents();
    }

    @Test
    public void testRemoveChild() {
        ComponentContainer parent = new ComponentContainer();
        parent.startComponents();
        ComponentContainer child = parent.createChild();
        assertThat(parent.getChildren()).containsOnly(child);
        parent.removeChild(child);
        assertThat(parent.getChildren()).isEmpty();
    }

    @Test
    public void support_multiple_children() {
        ComponentContainer parent = new ComponentContainer();
        parent.startComponents();
        ComponentContainer child1 = parent.createChild();
        child1.startComponents();
        ComponentContainer child2 = parent.createChild();
        child2.startComponents();
        assertThat(parent.getChildren()).containsOnly(child1, child2);
        child1.stopComponents();
        assertThat(parent.getChildren()).containsOnly(child2);
        parent.stopComponents();
        assertThat(parent.getChildren()).isEmpty();
    }

    @Test
    public void shouldForwardStartAndStopToDescendants() {
        ComponentContainer grandParent = new ComponentContainer();
        ComponentContainer parent = grandParent.createChild();
        ComponentContainer child = parent.createChild();
        child.addSingleton(ComponentContainerTest.StartableStoppableComponent.class);
        grandParent.startComponents();
        ComponentContainerTest.StartableStoppableComponent component = child.getComponentByType(ComponentContainerTest.StartableStoppableComponent.class);
        Assert.assertTrue(component.started);
        parent.stopComponents();
        Assert.assertTrue(component.stopped);
    }

    @Test
    public void shouldDeclareComponentProperties() {
        ComponentContainer container = new ComponentContainer();
        container.addSingleton(ComponentContainerTest.ComponentWithProperty.class);
        PropertyDefinitions propertyDefinitions = container.getComponentByType(PropertyDefinitions.class);
        assertThat(propertyDefinitions.get("foo")).isNotNull();
        assertThat(propertyDefinitions.get("foo").defaultValue()).isEqualTo("bar");
    }

    @Test
    public void shouldDeclareExtensionWithoutAddingIt() {
        ComponentContainer container = new ComponentContainer();
        PluginInfo plugin = Mockito.mock(PluginInfo.class);
        container.declareExtension(plugin, ComponentContainerTest.ComponentWithProperty.class);
        PropertyDefinitions propertyDefinitions = container.getComponentByType(PropertyDefinitions.class);
        assertThat(propertyDefinitions.get("foo")).isNotNull();
        assertThat(container.getComponentByType(ComponentContainerTest.ComponentWithProperty.class)).isNull();
    }

    @Test
    public void shouldDeclareExtensionWhenAdding() {
        ComponentContainer container = new ComponentContainer();
        PluginInfo plugin = Mockito.mock(PluginInfo.class);
        container.addExtension(plugin, ComponentContainerTest.ComponentWithProperty.class);
        PropertyDefinitions propertyDefinitions = container.getComponentByType(PropertyDefinitions.class);
        assertThat(propertyDefinitions.get("foo")).isNotNull();
        assertThat(container.getComponentByType(ComponentContainerTest.ComponentWithProperty.class)).isNotNull();
        assertThat(container.getComponentByKey(ComponentContainerTest.ComponentWithProperty.class)).isNotNull();
    }

    @Test
    public void test_add_class() {
        ComponentContainer container = new ComponentContainer();
        container.add(ComponentContainerTest.ComponentWithProperty.class, ComponentContainerTest.SimpleComponent.class);
        assertThat(container.getComponentByType(ComponentContainerTest.ComponentWithProperty.class)).isNotNull();
        assertThat(container.getComponentByType(ComponentContainerTest.SimpleComponent.class)).isNotNull();
    }

    @Test
    public void test_add_collection() {
        ComponentContainer container = new ComponentContainer();
        container.add(Arrays.asList(ComponentContainerTest.ComponentWithProperty.class, ComponentContainerTest.SimpleComponent.class));
        assertThat(container.getComponentByType(ComponentContainerTest.ComponentWithProperty.class)).isNotNull();
        assertThat(container.getComponentByType(ComponentContainerTest.SimpleComponent.class)).isNotNull();
    }

    @Test
    public void test_add_adapter() {
        ComponentContainer container = new ComponentContainer();
        container.add(new ComponentContainerTest.SimpleComponentProvider());
        assertThat(container.getComponentByType(ComponentContainerTest.SimpleComponent.class)).isNotNull();
    }

    @Test
    public void should_sanitize_pico_exception_on_start_failure() {
        ComponentContainer container = new ComponentContainer();
        container.add(ComponentContainerTest.UnstartableComponent.class);
        // do not expect a PicoException
        thrown.expect(IllegalStateException.class);
        container.startComponents();
    }

    @Test
    public void display_plugin_name_when_failing_to_add_extension() {
        ComponentContainer container = new ComponentContainer();
        PluginInfo plugin = Mockito.mock(PluginInfo.class);
        container.startComponents();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Unable to register extension org.sonar.core.platform.ComponentContainerTest$UnstartableComponent");
        container.addExtension(plugin, ComponentContainerTest.UnstartableComponent.class);
    }

    @Test
    public void test_start_failure() {
        ComponentContainer container = new ComponentContainer();
        ComponentContainerTest.StartableStoppableComponent startable = new ComponentContainerTest.StartableStoppableComponent();
        container.add(startable, ComponentContainerTest.UnstartableComponent.class);
        try {
            container.execute();
            Assert.fail();
        } catch (Exception e) {
            assertThat(startable.started).isTrue();
            // container stops the components that have already been started
            assertThat(startable.stopped).isTrue();
        }
    }

    @Test
    public void stop_container_does_not_fail_and_all_stoppable_components_are_stopped_even_if_one_or_more_stop_method_call_fail() {
        ComponentContainer container = new ComponentContainer();
        container.add(ComponentContainerTest.FailingStopWithISEComponent.class, ComponentContainerTest.FailingStopWithISEComponent2.class, ComponentContainerTest.FailingStopWithErrorComponent.class, ComponentContainerTest.FailingStopWithErrorComponent2.class);
        container.startComponents();
        ComponentContainerTest.StartableStoppableComponent[] components = new ComponentContainerTest.StartableStoppableComponent[]{ container.getComponentByType(ComponentContainerTest.FailingStopWithISEComponent.class), container.getComponentByType(ComponentContainerTest.FailingStopWithISEComponent2.class), container.getComponentByType(ComponentContainerTest.FailingStopWithErrorComponent.class), container.getComponentByType(ComponentContainerTest.FailingStopWithErrorComponent2.class) };
        container.stopComponents();
        Arrays.stream(components).forEach(( startableComponent) -> assertThat(startableComponent.stopped).isTrue());
    }

    @Test
    public void stop_container_stops_all_stoppable_components_even_in_case_of_OOM_in_any_stop_method() {
        ComponentContainer container = new ComponentContainer();
        container.add(ComponentContainerTest.FailingStopWithOOMComponent.class, ComponentContainerTest.FailingStopWithOOMComponent2.class);
        container.startComponents();
        ComponentContainerTest.StartableStoppableComponent[] components = new ComponentContainerTest.StartableStoppableComponent[]{ container.getComponentByType(ComponentContainerTest.FailingStopWithOOMComponent.class), container.getComponentByType(ComponentContainerTest.FailingStopWithOOMComponent2.class) };
    }

    @Test
    public void stop_exception_should_not_hide_start_exception() {
        ComponentContainer container = new ComponentContainer();
        container.add(ComponentContainerTest.UnstartableComponent.class, ComponentContainerTest.FailingStopWithISEComponent.class);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Fail to start");
        container.execute();
    }

    @Test
    public void should_execute_components() {
        ComponentContainer container = new ComponentContainer();
        ComponentContainerTest.StartableStoppableComponent component = new ComponentContainerTest.StartableStoppableComponent();
        container.add(component);
        container.execute();
        assertThat(component.started).isTrue();
        assertThat(component.stopped).isTrue();
    }

    /**
     * Method close() must be called even if the methods start() or stop()
     * are not defined.
     */
    @Test
    public void should_close_components_without_lifecycle() {
        ComponentContainer container = new ComponentContainer();
        ComponentContainerTest.CloseableComponent component = new ComponentContainerTest.CloseableComponent();
        container.add(component);
        container.execute();
        assertThat(component.isClosed).isTrue();
    }

    /**
     * Method close() must be executed after stop()
     */
    @Test
    public void should_close_components_with_lifecycle() {
        ComponentContainer container = new ComponentContainer();
        ComponentContainerTest.StartableCloseableComponent component = new ComponentContainerTest.StartableCloseableComponent();
        container.add(component);
        container.execute();
        assertThat(component.isStopped).isTrue();
        assertThat(component.isClosed).isTrue();
        assertThat(component.isClosedAfterStop).isTrue();
    }

    public static class StartableStoppableComponent {
        public boolean started = false;

        public boolean stopped = false;

        public void start() {
            started = true;
        }

        public void stop() {
            stopped = true;
        }
    }

    public static class UnstartableComponent {
        public void start() {
            throw new IllegalStateException("Fail to start");
        }

        public void stop() {
        }
    }

    public static class FailingStopWithISEComponent extends ComponentContainerTest.StartableStoppableComponent {
        public void stop() {
            super.stop();
            throw new IllegalStateException(("Faking IllegalStateException thrown by stop method of " + (getClass().getSimpleName())));
        }
    }

    public static class FailingStopWithISEComponent2 extends ComponentContainerTest.FailingStopWithErrorComponent {}

    public static class FailingStopWithErrorComponent extends ComponentContainerTest.StartableStoppableComponent {
        public void stop() {
            super.stop();
            throw new Error(("Faking Error thrown by stop method of " + (getClass().getSimpleName())));
        }
    }

    public static class FailingStopWithErrorComponent2 extends ComponentContainerTest.FailingStopWithErrorComponent {}

    public static class FailingStopWithOOMComponent extends ComponentContainerTest.StartableStoppableComponent {
        public void stop() {
            super.stop();
            ComponentContainerTest.FailingStopWithOOMComponent.consumeAvailableMemory();
        }

        private static List<Object> consumeAvailableMemory() {
            List<Object> holder = new ArrayList<>();
            while (true) {
                holder.add(new byte[128 * 1024]);
            } 
        }
    }

    public static class FailingStopWithOOMComponent2 extends ComponentContainerTest.FailingStopWithOOMComponent {}

    @Property(key = "foo", defaultValue = "bar", name = "Foo")
    public static class ComponentWithProperty {}

    public static class SimpleComponent {}

    public static class SimpleComponentProvider extends ProviderAdapter {
        public ComponentContainerTest.SimpleComponent provide() {
            return new ComponentContainerTest.SimpleComponent();
        }
    }

    public static class CloseableComponent implements AutoCloseable {
        public boolean isClosed = false;

        @Override
        public void close() throws Exception {
            isClosed = true;
        }
    }

    public static class StartableCloseableComponent implements AutoCloseable {
        public boolean isClosed = false;

        public boolean isStopped = false;

        public boolean isClosedAfterStop = false;

        public void stop() {
            isStopped = true;
        }

        @Override
        public void close() throws Exception {
            isClosed = true;
            isClosedAfterStop = isStopped;
        }
    }
}

