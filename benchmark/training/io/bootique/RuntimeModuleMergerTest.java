/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique;


import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.log.BootLogger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RuntimeModuleMergerTest {
    private BootLogger bootLogger;

    private List<BQModule> mockBqModules;

    private List<Module> testModules;

    @Test
    public void testGetModules_Empty() {
        Assert.assertTrue(toGuiceModules(Collections.emptyList()).isEmpty());
    }

    @Test
    public void testGetModules_One() {
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(2));
        Collection<Module> modules = new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
        Assert.assertEquals(1, modules.size());
        Assert.assertTrue(modules.contains(testModules.get(2)));
    }

    @Test
    public void testGetModules_Two() {
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(2), mockBqModules.get(1));
        Collection<Module> modules = new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
        Assert.assertEquals(2, modules.size());
        Assert.assertTrue(modules.contains(testModules.get(1)));
        Assert.assertTrue(modules.contains(testModules.get(2)));
    }

    @Test
    public void testGetModules_Three_Dupes() {
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(2), mockBqModules.get(1), mockBqModules.get(2));
        Collection<Module> modules = new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
        Assert.assertEquals(2, modules.size());
        Assert.assertTrue(modules.contains(testModules.get(1)));
        Assert.assertTrue(modules.contains(testModules.get(2)));
    }

    @Test
    public void testGetModules_Overrides() {
        // 0 overrides 3
        mockBqModules.set(0, createBQModule(testModules.get(0), RuntimeModuleMergerTest.M3.class));
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(0), mockBqModules.get(3));
        Collection<Module> modules = new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
        Assert.assertEquals(1, modules.size());
        assertOverrideModule(modules.iterator().next());
    }

    @Test
    public void testGetModules_Overrides_Chain() {
        // 0 overrides 3 ; 3 overrides 4
        mockBqModules.set(0, createBQModule(testModules.get(0), RuntimeModuleMergerTest.M3.class));
        mockBqModules.set(3, createBQModule(testModules.get(3), RuntimeModuleMergerTest.M4.class));
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(4), mockBqModules.get(0), mockBqModules.get(1), mockBqModules.get(3));
        Collection<Module> modules = new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
        Assert.assertEquals(2, modules.size());
        Assert.assertFalse(modules.contains(testModules.get(4)));
        Assert.assertTrue(modules.contains(testModules.get(1)));
        Assert.assertFalse(modules.contains(testModules.get(0)));
        Assert.assertFalse(modules.contains(testModules.get(3)));
    }

    @Test(expected = RuntimeException.class)
    public void testGetModules_OverrideCycle() {
        // 0 replaces 3 ; 3 replaces 0
        mockBqModules.set(0, createBQModule(testModules.get(0), RuntimeModuleMergerTest.M3.class));
        mockBqModules.set(3, createBQModule(testModules.get(3), RuntimeModuleMergerTest.M0.class));
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(0), mockBqModules.get(3));
        new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
    }

    @Test(expected = RuntimeException.class)
    public void testGetModules_OverrideIndirectCycle() {
        // 0 replaces 3 ; 3 replaces 4 ; 4 replaces 0
        mockBqModules.set(0, createBQModule(testModules.get(0), RuntimeModuleMergerTest.M3.class));
        mockBqModules.set(3, createBQModule(testModules.get(3), RuntimeModuleMergerTest.M4.class));
        mockBqModules.set(4, createBQModule(testModules.get(4), RuntimeModuleMergerTest.M0.class));
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(0), mockBqModules.get(4), mockBqModules.get(3));
        new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
    }

    @Test(expected = RuntimeException.class)
    public void testGetModules_OverrideDupe() {
        // 0 overrides 3 ; 4 overrides 3
        mockBqModules.set(0, createBQModule(testModules.get(0), RuntimeModuleMergerTest.M3.class));
        mockBqModules.set(4, createBQModule(testModules.get(4), RuntimeModuleMergerTest.M3.class));
        Collection<BQModule> bqModules = Arrays.asList(mockBqModules.get(0), mockBqModules.get(4), mockBqModules.get(3));
        new RuntimeModuleMerger(bootLogger).toGuiceModules(bqModules);
    }

    class M0 implements Module {
        @Override
        public void configure(Binder binder) {
            // noop
        }
    }

    class M1 implements Module {
        @Override
        public void configure(Binder binder) {
            // noop
        }
    }

    class M2 implements Module {
        @Override
        public void configure(Binder binder) {
            // noop
        }
    }

    class M3 implements Module {
        @Override
        public void configure(Binder binder) {
            // noop
        }
    }

    class M4 implements Module {
        @Override
        public void configure(Binder binder) {
            // noop
        }
    }
}

