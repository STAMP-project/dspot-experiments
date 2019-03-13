/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot;


import Modifier.INTERFACE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FastHierarchyTest {
    @Test
    public void testGetAllSubinterfaces() {
        G.reset();
        Scene s = Scene.v();
        s.loadNecessaryClasses();
        SootClass scA = FastHierarchyTest.generacteSceneClass("InterfaceA", INTERFACE);
        SootClass scB = FastHierarchyTest.generacteSceneClass("InterfaceB", INTERFACE);
        SootClass scC1 = FastHierarchyTest.generacteSceneClass("InterfaceC1", INTERFACE);
        SootClass scC2 = FastHierarchyTest.generacteSceneClass("InterfaceC2", INTERFACE);
        SootClass scD = FastHierarchyTest.generacteSceneClass("InterfaceD", INTERFACE);
        SootClass scE1 = FastHierarchyTest.generacteSceneClass("InterfaceE1", INTERFACE);
        SootClass scE2 = FastHierarchyTest.generacteSceneClass("InterfaceE2", INTERFACE);
        SootClass sc6 = FastHierarchyTest.generacteSceneClass("Class1", 0);
        scA.addInterface(scB);
        scB.addInterface(scC1);
        scB.addInterface(scC2);
        scC1.addInterface(scD);
        scD.addInterface(scE1);
        scD.addInterface(scE2);
        FastHierarchy fh = s.getOrMakeFastHierarchy();
        // A sc6 is not an interface -> empty result
        Assert.assertEquals(Collections.emptySet(), fh.getAllSubinterfaces(sc6));
        Assert.assertThat(fh.getAllSubinterfaces(scA), Matchers.containsInAnyOrder(scA));
        Assert.assertThat(fh.getAllSubinterfaces(scB), Matchers.containsInAnyOrder(scA, scB));
        Assert.assertThat(fh.getAllSubinterfaces(scC1), Matchers.containsInAnyOrder(scA, scB, scC1));
        Assert.assertThat(fh.getAllSubinterfaces(scC2), Matchers.containsInAnyOrder(scA, scB, scC2));
        Assert.assertThat(fh.getAllSubinterfaces(scD), Matchers.containsInAnyOrder(scA, scB, scC1, scD));
        Assert.assertThat(fh.getAllSubinterfaces(scE1), Matchers.containsInAnyOrder(scA, scB, scC1, scD, scE1));
        Assert.assertThat(fh.getAllSubinterfaces(scE2), Matchers.containsInAnyOrder(scA, scB, scC1, scD, scE2));
    }

    /**
     * Execute {@link FastHierarchy#getAllSubinterfaces(SootClass)} concurrently and check the result
     *
     * This test uses a subclass of {@link FastHierarchy} that has a built-in delay to increase the time-span a potential
     * problem because of concurrent access can arise.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetAllSubinterfacesMultiThreaded() throws Exception {
        G.reset();
        Scene s = Scene.v();
        s.loadNecessaryClasses();
        SootClass scA = FastHierarchyTest.generacteSceneClass("InterfaceA", INTERFACE);
        SootClass scB = FastHierarchyTest.generacteSceneClass("InterfaceB", INTERFACE);
        SootClass scC = FastHierarchyTest.generacteSceneClass("InterfaceC", INTERFACE);
        SootClass scD = FastHierarchyTest.generacteSceneClass("InterfaceD", INTERFACE);
        SootClass scE = FastHierarchyTest.generacteSceneClass("InterfaceE", INTERFACE);
        scA.addInterface(scB);
        scB.addInterface(scC);
        scC.addInterface(scD);
        scD.addInterface(scE);
        final FastHierarchy hierarchy = new FastHierarchyTest.FastHierarchyForUnittest();
        s.setFastHierarchy(hierarchy);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        Callable<Set<SootClass>> c = new Callable<Set<SootClass>>() {
            @Override
            public Set<SootClass> call() throws Exception {
                return hierarchy.getAllSubinterfaces(scE);
            }
        };
        ArrayList<Future<Set<SootClass>>> results = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            results.add(executor.submit(c));
        }
        for (Future<Set<SootClass>> future : results) {
            Set<SootClass> res = future.get();
            Assert.assertThat(res, Matchers.containsInAnyOrder(scA, scB, scC, scD, scE));
        }
        executor.shutdown();
    }

    @Test
    public void testGetAllImplementersOfInterface() {
        G.reset();
        Scene s = Scene.v();
        s.loadNecessaryClasses();
        SootClass interfaceA = FastHierarchyTest.generacteSceneClass("InterfaceA", INTERFACE);
        SootClass interfaceB = FastHierarchyTest.generacteSceneClass("InterfaceB", INTERFACE);
        SootClass interfaceC1 = FastHierarchyTest.generacteSceneClass("InterfaceC1", INTERFACE);
        SootClass interfaceC2 = FastHierarchyTest.generacteSceneClass("InterfaceC2", INTERFACE);
        SootClass interfaceD = FastHierarchyTest.generacteSceneClass("InterfaceD", INTERFACE);
        SootClass scA = FastHierarchyTest.generacteSceneClass("ClassA", 0);
        SootClass scB = FastHierarchyTest.generacteSceneClass("ClassB", 0);
        SootClass scC1 = FastHierarchyTest.generacteSceneClass("ClassC1", 0);
        SootClass scC2 = FastHierarchyTest.generacteSceneClass("ClassC2", 0);
        SootClass scD = FastHierarchyTest.generacteSceneClass("ClassD", 0);
        SootClass scZ = FastHierarchyTest.generacteSceneClass("ClassZ", 0);
        interfaceA.addInterface(interfaceB);
        interfaceB.addInterface(interfaceC1);
        interfaceB.addInterface(interfaceC2);
        interfaceC1.addInterface(interfaceD);
        scA.addInterface(interfaceA);
        scB.addInterface(interfaceB);
        scC1.addInterface(interfaceC1);
        scC2.addInterface(interfaceC2);
        scD.addInterface(interfaceD);
        FastHierarchy fh = s.getOrMakeFastHierarchy();
        // A sc6 is not an interface -> empty result
        Assert.assertEquals(Collections.emptySet(), fh.getAllImplementersOfInterface(scZ));
        Assert.assertThat(fh.getAllImplementersOfInterface(interfaceA), Matchers.containsInAnyOrder(scA));
        Assert.assertThat(fh.getAllImplementersOfInterface(interfaceB), Matchers.containsInAnyOrder(scA, scB));
        Assert.assertThat(fh.getAllImplementersOfInterface(interfaceC1), Matchers.containsInAnyOrder(scA, scB, scC1));
        Assert.assertThat(fh.getAllImplementersOfInterface(interfaceC2), Matchers.containsInAnyOrder(scA, scB, scC2));
        Assert.assertThat(fh.getAllImplementersOfInterface(interfaceD), Matchers.containsInAnyOrder(scA, scB, scC1, scD));
    }

    private static class FastHierarchyForUnittest extends FastHierarchy {
        @Override
        public Set<SootClass> getAllSubinterfaces(SootClass parent) {
            try {
                // We add a delay to increase the chance of a concurrent access
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            return super.getAllSubinterfaces(parent);
        }
    }
}

