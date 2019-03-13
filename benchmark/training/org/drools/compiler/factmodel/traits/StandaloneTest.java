/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.factmodel.traits;


import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.Entity;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.util.StandaloneTraitFactory;
import org.junit.Assert;
import org.junit.Test;


public class StandaloneTest {
    private StandaloneTraitFactory factory;

    @Test
    public void testThing() throws LogicalTypeInconsistencyException {
        // Entity is @Traitable and implements TraitableBean natively
        // Thing is a Trait
        // --> just call getProxy
        Entity core = new Entity("x");
        Thing thing = factory.don(core, Thing.class);
        Assert.assertNotNull(thing);
    }

    @Test
    public void testHierarchy() throws LogicalTypeInconsistencyException {
        Imp imp = new Imp();
        imp.setName("john doe");
        // Imp is not a TraitableBean, so we need to wrap it first
        // IStudent is a Thing, so it will work directly
        CoreWrapper<Imp> core = factory.makeTraitable(imp, Imp.class);
        IStudent student = ((IStudent) (factory.don(core, IStudent.class)));
        System.out.println(student.getName());
        System.out.println(student.getSchool());
        Assert.assertEquals("john doe", student.getName());
        Assert.assertNull(student.getSchool());
        IPerson p = ((IPerson) (factory.don(core, IPerson.class)));
        student.setName("alan ford");
        System.out.println(p.getName());
        Assert.assertEquals("alan ford", p.getName());
    }

    @Trait
    public static interface IFoo {
        public String getName();

        public void setName(String n);
    }

    @Test
    public void testLegacy() throws LogicalTypeInconsistencyException {
        Imp imp = new Imp();
        imp.setName("john doe");
        // Imp is not a TraitableBean, so we need to wrap it first
        // IFoo is not a Thing, but it will be extended internally
        CoreWrapper<Imp> core = factory.makeTraitable(imp, Imp.class);
        StandaloneTest.IFoo foo = ((StandaloneTest.IFoo) (factory.don(core, StandaloneTest.IFoo.class)));
        System.out.println(foo.getName());
        System.out.println((foo instanceof Thing));
        Assert.assertEquals("john doe", foo.getName());
        Assert.assertTrue((foo instanceof Thing));
    }
}

