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
package org.sonar.scanner.bootstrap;


import FieldDecorated.Decorator;
import Phase.Name;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.picocontainer.behaviors.FieldDecorated;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.batch.Phase;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.core.platform.ComponentContainer;
import org.sonar.scanner.sensor.ModuleSensorContext;
import org.sonar.scanner.sensor.ModuleSensorExtensionDictionnary;
import org.sonar.scanner.sensor.ModuleSensorOptimizer;
import org.sonar.scanner.sensor.ModuleSensorWrapper;


public class ModuleSensorExtensionDictionnaryTest {
    private ModuleSensorOptimizer sensorOptimizer = Mockito.mock(ModuleSensorOptimizer.class);

    @Test
    public void testGetFilteredExtensionWithExtensionMatcher() {
        final Sensor sensor1 = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        final Sensor sensor2 = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        ModuleSensorExtensionDictionnary selector = newSelector(sensor1, sensor2);
        Collection<Sensor> sensors = selector.select(Sensor.class, true, ( extension) -> extension.equals(sensor1));
        assertThat(sensors).contains(sensor1);
        Assert.assertEquals(1, sensors.size());
    }

    @Test
    public void testGetFilteredExtensions() {
        Sensor sensor1 = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        Sensor sensor2 = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        FieldDecorated.Decorator decorator = Mockito.mock(Decorator.class);
        ModuleSensorExtensionDictionnary selector = newSelector(sensor1, sensor2, decorator);
        Collection<Sensor> sensors = selector.select(Sensor.class, false, null);
        assertThat(sensors).containsOnly(sensor1, sensor2);
    }

    @Test
    public void shouldSearchInParentContainers() {
        Sensor a = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        Sensor b = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        Sensor c = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        ComponentContainer grandParent = new ComponentContainer();
        grandParent.addSingleton(a);
        ComponentContainer parent = grandParent.createChild();
        parent.addSingleton(b);
        ComponentContainer child = parent.createChild();
        child.addSingleton(c);
        ModuleSensorExtensionDictionnary dictionnary = new ModuleSensorExtensionDictionnary(child, Mockito.mock(ModuleSensorContext.class), Mockito.mock(ModuleSensorOptimizer.class));
        assertThat(dictionnary.select(Sensor.class, true, null)).containsOnly(a, b, c);
    }

    @Test
    public void sortExtensionsByDependency() {
        Object a = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf(null);
        Object b = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf(a);
        Object c = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf(b);
        ModuleSensorExtensionDictionnary selector = newSelector(b, c, a);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(3);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        assertThat(extensions.get(2)).isEqualTo(c);
    }

    @Test
    public void useMethodAnnotationsToSortExtensions() {
        Object a = new ModuleSensorExtensionDictionnaryTest.GeneratesSomething("foo");
        Object b = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf("foo");
        ModuleSensorExtensionDictionnary selector = newSelector(a, b);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions.size()).isEqualTo(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        // different initial order
        selector = newSelector(b, a);
        extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
    }

    @Test
    public void methodDependsUponCollection() {
        Object a = new ModuleSensorExtensionDictionnaryTest.GeneratesSomething("foo");
        Object b = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf(Arrays.asList("foo"));
        ModuleSensorExtensionDictionnary selector = newSelector(a, b);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        // different initial order
        selector = newSelector(b, a);
        extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
    }

    @Test
    public void methodDependsUponArray() {
        Object a = new ModuleSensorExtensionDictionnaryTest.GeneratesSomething("foo");
        Object b = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf(new String[]{ "foo" });
        ModuleSensorExtensionDictionnary selector = newSelector(a, b);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        // different initial order
        selector = newSelector(b, a);
        extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
    }

    @Test
    public void useClassAnnotationsToSortExtensions() {
        Object a = new ModuleSensorExtensionDictionnaryTest.ClassDependedUpon();
        Object b = new ModuleSensorExtensionDictionnaryTest.ClassDependsUpon();
        ModuleSensorExtensionDictionnary selector = newSelector(a, b);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        // different initial order
        selector = newSelector(b, a);
        extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
    }

    @Test
    public void useClassAnnotationsOnInterfaces() {
        Object a = new ModuleSensorExtensionDictionnaryTest.InterfaceDependedUpon() {};
        Object b = new ModuleSensorExtensionDictionnaryTest.InterfaceDependsUpon() {};
        ModuleSensorExtensionDictionnary selector = newSelector(a, b);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        // different initial order
        selector = newSelector(b, a);
        extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
    }

    @Test
    public void inheritAnnotations() {
        Object a = new ModuleSensorExtensionDictionnaryTest.SubClass("foo");
        Object b = new ModuleSensorExtensionDictionnaryTest.MethodDependentOf("foo");
        ModuleSensorExtensionDictionnary selector = newSelector(b, a);
        List<Object> extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
        // change initial order
        selector = newSelector(a, b);
        extensions = Lists.newArrayList(selector.select(ModuleSensorExtensionDictionnaryTest.Marker.class, true, null));
        assertThat(extensions).hasSize(2);
        assertThat(extensions.get(0)).isEqualTo(a);
        assertThat(extensions.get(1)).isEqualTo(b);
    }

    @Test(expected = IllegalStateException.class)
    public void annotatedMethodsCanNotBePrivate() {
        ModuleSensorExtensionDictionnary selector = newSelector();
        Object wrong = new Object() {
            @DependsUpon
            private Object foo() {
                return "foo";
            }
        };
        selector.evaluateAnnotatedClasses(wrong, DependsUpon.class);
    }

    @Test
    public void dependsUponPhaseForSensors() {
        ModuleSensorExtensionDictionnaryTest.PreSensor pre = new ModuleSensorExtensionDictionnaryTest.PreSensor();
        ModuleSensorExtensionDictionnaryTest.NormalSensor normal = new ModuleSensorExtensionDictionnaryTest.NormalSensor();
        ModuleSensorExtensionDictionnaryTest.PostSensor post = new ModuleSensorExtensionDictionnaryTest.PostSensor();
        ModuleSensorExtensionDictionnary selector = newSelector(normal, post, pre);
        assertThat(selector.selectSensors(false)).extracting("wrappedSensor").containsExactly(pre, normal, post);
    }

    @Test
    public void dependsUponInheritedPhase() {
        ModuleSensorExtensionDictionnaryTest.PreSensorSubclass pre = new ModuleSensorExtensionDictionnaryTest.PreSensorSubclass();
        ModuleSensorExtensionDictionnaryTest.NormalSensor normal = new ModuleSensorExtensionDictionnaryTest.NormalSensor();
        ModuleSensorExtensionDictionnaryTest.PostSensorSubclass post = new ModuleSensorExtensionDictionnaryTest.PostSensorSubclass();
        ModuleSensorExtensionDictionnary selector = newSelector(normal, post, pre);
        List extensions = Lists.newArrayList(selector.select(Sensor.class, true, null));
        assertThat(extensions).containsExactly(pre, normal, post);
    }

    @Test
    public void selectSensors() {
        ModuleSensorExtensionDictionnaryTest.FakeSensor nonGlobalSensor = new ModuleSensorExtensionDictionnaryTest.FakeSensor();
        ModuleSensorExtensionDictionnaryTest.FakeGlobalSensor globalSensor = new ModuleSensorExtensionDictionnaryTest.FakeGlobalSensor();
        ModuleSensorExtensionDictionnary selector = newSelector(nonGlobalSensor, globalSensor);
        // verify non-global sensor
        Collection<ModuleSensorWrapper> extensions = selector.selectSensors(false);
        assertThat(extensions).hasSize(1);
        assertThat(extensions).extracting("wrappedSensor").containsExactly(nonGlobalSensor);
        // verify global sensor
        extensions = selector.selectSensors(true);
        assertThat(extensions).extracting("wrappedSensor").containsExactly(globalSensor);
    }

    interface Marker {}

    class FakeSensor implements Sensor {
        @Override
        public void describe(SensorDescriptor descriptor) {
        }

        @Override
        public void execute(SensorContext context) {
        }
    }

    class FakeGlobalSensor implements Sensor {
        @Override
        public void describe(SensorDescriptor descriptor) {
            descriptor.global();
        }

        @Override
        public void execute(SensorContext context) {
        }
    }

    @ScannerSide
    class MethodDependentOf implements ModuleSensorExtensionDictionnaryTest.Marker {
        private Object dep;

        MethodDependentOf(Object o) {
            this.dep = o;
        }

        @DependsUpon
        public Object dependsUponObject() {
            return dep;
        }
    }

    @ScannerSide
    @DependsUpon("flag")
    class ClassDependsUpon implements ModuleSensorExtensionDictionnaryTest.Marker {}

    @ScannerSide
    @DependedUpon("flag")
    class ClassDependedUpon implements ModuleSensorExtensionDictionnaryTest.Marker {}

    @ScannerSide
    @DependsUpon("flag")
    interface InterfaceDependsUpon extends ModuleSensorExtensionDictionnaryTest.Marker {}

    @ScannerSide
    @DependedUpon("flag")
    interface InterfaceDependedUpon extends ModuleSensorExtensionDictionnaryTest.Marker {}

    @ScannerSide
    class GeneratesSomething implements ModuleSensorExtensionDictionnaryTest.Marker {
        private Object gen;

        GeneratesSomething(Object o) {
            this.gen = o;
        }

        @DependedUpon
        public Object generates() {
            return gen;
        }
    }

    class SubClass extends ModuleSensorExtensionDictionnaryTest.GeneratesSomething implements ModuleSensorExtensionDictionnaryTest.Marker {
        SubClass(Object o) {
            super(o);
        }
    }

    class NormalSensor implements Sensor {
        @Override
        public void describe(SensorDescriptor descriptor) {
        }

        @Override
        public void execute(SensorContext context) {
        }
    }

    @Phase(name = Name.PRE)
    class PreSensor implements Sensor {
        @Override
        public void describe(SensorDescriptor descriptor) {
        }

        @Override
        public void execute(SensorContext context) {
        }
    }

    class PreSensorSubclass extends ModuleSensorExtensionDictionnaryTest.PreSensor {}

    @Phase(name = Name.POST)
    class PostSensor implements Sensor {
        @Override
        public void describe(SensorDescriptor descriptor) {
        }

        @Override
        public void execute(SensorContext context) {
        }
    }

    class PostSensorSubclass extends ModuleSensorExtensionDictionnaryTest.PostSensor {}
}

