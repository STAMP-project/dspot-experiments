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


import java.util.ArrayList;
import java.util.Arrays;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.drools.core.util.StandaloneTraitFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.KieSession;


@RunWith(Parameterized.class)
public class LegacyTraitTest {
    public VirtualPropertyMode mode;

    public LegacyTraitTest(VirtualPropertyMode m) {
        this.mode = m;
    }

    // Getters and setters are both needed. They should refer to an attribute with the same name
    public static class PatientImpl implements LegacyTraitTest.Patient {
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static interface Pers {
        public String getName();
    }

    public static interface Patient extends LegacyTraitTest.Pers {}

    public static interface Procedure {
        public LegacyTraitTest.Patient getSubject();

        public void setSubject(LegacyTraitTest.Patient p);
    }

    public static interface ExtendedProcedure extends LegacyTraitTest.Procedure {
        public LegacyTraitTest.Pers getPers();

        public void setPers(LegacyTraitTest.Pers pers);
    }

    public class ProcedureImpl implements LegacyTraitTest.Procedure {
        public ProcedureImpl() {
        }

        private LegacyTraitTest.Patient subject;

        @Override
        public LegacyTraitTest.Patient getSubject() {
            return this.subject;
        }

        public void setSubject(LegacyTraitTest.Patient patient) {
            this.subject = patient;
        }
    }

    public class ExtendedProcedureImpl extends LegacyTraitTest.ProcedureImpl implements LegacyTraitTest.ExtendedProcedure {
        public ExtendedProcedureImpl() {
        }

        private LegacyTraitTest.Pers pers;

        @Override
        public LegacyTraitTest.Pers getPers() {
            return this.pers;
        }

        public void setPers(LegacyTraitTest.Pers pers) {
            this.pers = pers;
        }
    }

    @Test
    public void traitWithPojoInterface() {
        String source = "package org.drools.compiler.test;" + (((((((((((((((((((((((((((((((((((((("import org.drools.compiler.factmodel.traits.LegacyTraitTest.Procedure; " + "import org.drools.compiler.factmodel.traits.LegacyTraitTest; ") + "import org.drools.compiler.factmodel.traits.LegacyTraitTest.ExtendedProcedureImpl; ") + "import org.drools.compiler.factmodel.traits.LegacyTraitTest.ExtendedProcedure; ") + // enhanced so that declaration is not needed
        // "declare ProcedureImpl end " +
        "declare trait ExtendedProcedure ") + "   @role( event )") + "end ") + // Surgery must be declared as trait, since it does not extend Thing
        "declare trait Surgery extends ExtendedProcedure end ") + "declare ExtendedProcedureImpl ") + "    @Traitable ") + "end ") + "rule 'Don Procedure' ") + "when ") + "    $p : ExtendedProcedure() ") + "then ") + "    don( $p, Surgery.class ); ") + "end ") + "rule 'Test 1' ") + "dialect 'mvel' ") + "when ") + "    $s1 : ExtendedProcedure( $subject : subject ) ") + "    $s2 : ExtendedProcedure( subject == $subject ) ") + "then ") + "end ") + "rule 'Test 2' ") + "dialect 'mvel' ") + "when ") + "    $s1 : ExtendedProcedure( $subject : subject.name ) ") + "    $s2 : ExtendedProcedure( subject.name == $subject ) ") + "then ") + "end ") + "rule 'Test 3' ") + "dialect 'mvel' ") + "when ") + "    $s1 : ExtendedProcedure( ) ") + "then ") + "    update( $s1 ); ") + "end ") + "\n");
        KieSession ks = getSessionFromString(source);
        TraitFactory.setMode(mode, ks.getKieBase());
        LegacyTraitTest.ExtendedProcedureImpl procedure1 = new LegacyTraitTest.ExtendedProcedureImpl();
        LegacyTraitTest.ExtendedProcedureImpl procedure2 = new LegacyTraitTest.ExtendedProcedureImpl();
        LegacyTraitTest.PatientImpl patient1 = new LegacyTraitTest.PatientImpl();
        patient1.setName("John");
        procedure1.setSubject(patient1);
        procedure1.setPers(new LegacyTraitTest.PatientImpl());
        LegacyTraitTest.PatientImpl patient2 = new LegacyTraitTest.PatientImpl();
        patient2.setName("John");
        procedure2.setSubject(patient2);
        procedure2.setPers(new LegacyTraitTest.PatientImpl());
        ks.insert(procedure1);
        ks.insert(procedure2);
        ks.fireAllRules(500);
    }

    @Traitable
    @PropertyReactive
    public static class BarImpl implements LegacyTraitTest.Foo {}

    public static interface Root {}

    public static interface Trunk extends LegacyTraitTest.Root {}

    @PropertyReactive
    @Trait
    public static interface Foo extends LegacyTraitTest.Trunk {}

    @Test
    public void traitWithMixedInterfacesExtendingEachOther() {
        String source = ((((((((((((((((((((((((((((((((((((((("package org.drools.compiler.test;" + "import ") + (LegacyTraitTest.BarImpl.class.getCanonicalName())) + "; ") + "import ") + (LegacyTraitTest.Foo.class.getCanonicalName())) + "; ") + "import ") + (LegacyTraitTest.Trunk.class.getCanonicalName())) + "; ") + "global java.util.List list; ") + // We need to redeclare the interfaces as traits, the annotation on the original class is not enough here
        "declare trait Foo end ") + // notice that the declarations do not include supertypes, and are out of order. The engine will figure out what to do
        "declare trait Root end ") + "declare trait Foo2 extends Foo ") + "  @propertyReactive ") + "end ") + "rule 'Bar Don'") + "when ") + "   $b : BarImpl( this isA Foo.class, this not isA Foo2.class )\n") + "   String()\n") + "then ") + "   list.add( 3 ); ") + "   retract( $b ); ") + "end ") + "rule 'Don Bar' ") + "no-loop ") + "when ") + "    $b : Foo( ) ") + "then ") + "    list.add( 1 ); ") + "    don( $b, Foo2.class ); ") + "end ") + "rule 'Cant really shed Foo but Foo2' ") + "when ") + "   $b : Foo2() ") + "then ") + "   list.add( 2 ); ") + "   shed( $b, Foo.class ); ") + "   insert( \"done\" );") + "end ") + "";
        KieSession ks = getSessionFromString(source);
        TraitFactory.setMode(mode, ks.getKieBase());
        ArrayList list = new ArrayList();
        ks.setGlobal("list", list);
        ks.insert(new LegacyTraitTest.BarImpl());
        int n = ks.fireAllRules();
        System.out.println(list);
        Assert.assertEquals(Arrays.asList(1, 2, 3), list);
        Assert.assertEquals(3, n);
    }

    @Test
    public void testTraitWithNonAccessorMethodShadowing() {
        StandaloneTraitFactory factory = new StandaloneTraitFactory(ProjectClassLoader.createProjectClassLoader());
        try {
            SomeInterface r = ((SomeInterface) (factory.don(new SomeClass(), SomeInterface.class)));
            r.prepare();
            Assert.assertEquals(42, r.getFoo());
            Assert.assertEquals("I did that", r.doThis("that"));
        } catch (LogicalTypeInconsistencyException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

