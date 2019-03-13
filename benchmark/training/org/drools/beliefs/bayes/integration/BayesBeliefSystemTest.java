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
package org.drools.beliefs.bayes.integration;


import EntryPointId.DEFAULT;
import junit.framework.TestCase;
import org.drools.beliefs.bayes.BayesBeliefSystem;
import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.BayesModeFactory;
import org.drools.beliefs.bayes.BayesModeFactoryImpl;
import org.drools.beliefs.bayes.PropertyReference;
import org.drools.beliefs.bayes.runtime.BayesRuntime;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;


public class BayesBeliefSystemTest {
    @Test
    public void testBayes() {
        String drl = ((((((((((((((((((((((((((((((((((((((("package org.drools.defeasible; " + "import ") + (Garden.class.getCanonicalName())) + "; \n") + "import ") + (PropertyReference.class.getCanonicalName())) + "; \n") + "global ") + (BayesModeFactory.class.getCanonicalName())) + " bsFactory; \n") + "dialect \'mvel\'; \n") + " ") + "rule rule1 when ") + "        String( this == \'rule1\') \n") + "    g : Garden()") + "then ") + "    System.out.println(\"rule 1\"); \n") + "    insertLogical( new PropertyReference(g, \'cloudy\'), bsFactory.create( new double[] {1.0,0.0} ) ); \n ") + "end ") + "rule rule2 when ") + "        String( this == \'rule2\') \n") + "    g : Garden()") + "then ") + "    System.out.println(\"rule2\"); \n") + "    insertLogical( new PropertyReference(g, \'sprinkler\'), bsFactory.create( new double[] {1.0,0.0} ) ); \n ") + "end ") + "rule rule3 when ") + "        String( this == \'rule3\') \n") + "    g : Garden()") + "then ") + "    System.out.println(\"rule3\"); \n") + "    insertLogical( new PropertyReference(g, \'sprinkler\'), bsFactory.create( new double[] {1.0,0.0} ) ); \n ") + "end ") + "rule rule4 when ") + "        String( this == \'rule4\') \n") + "    g : Garden()") + "then ") + "    System.out.println(\"rule4\"); \n") + "    insertLogical( new PropertyReference(g, \'sprinkler\'), bsFactory.create( new double[] {0.0,1.0} ) ); \n ") + "end ") + "\n";
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (getSessionFromString(drl)));
        NamedEntryPoint ep = ((NamedEntryPoint) (ksession.getEntryPoint(DEFAULT.getEntryPointId())));
        BayesBeliefSystem bayesBeliefSystem = new BayesBeliefSystem(ep, ep.getTruthMaintenanceSystem());
        BayesModeFactoryImpl bayesModeFactory = new BayesModeFactoryImpl(bayesBeliefSystem);
        ksession.setGlobal("bsFactory", bayesModeFactory);
        BayesRuntime bayesRuntime = ksession.getKieRuntime(BayesRuntime.class);
        BayesInstance<Garden> instance = bayesRuntime.createInstance(Garden.class);
        Assert.assertNotNull(instance);
        Assert.assertTrue(instance.isDecided());
        instance.globalUpdate();
        Garden garden = instance.marginalize();
        Assert.assertTrue(garden.isWetGrass());
        FactHandle fh = ksession.insert(garden);
        FactHandle fh1 = ksession.insert("rule1");
        ksession.fireAllRules();
        Assert.assertTrue(instance.isDecided());
        instance.globalUpdate();// rule1 has added evidence, update the bayes network

        garden = instance.marginalize();
        Assert.assertTrue(garden.isWetGrass());// grass was wet before rule1 and continues to be wet

        FactHandle fh2 = ksession.insert("rule2");// applies 2 logical insertions

        ksession.fireAllRules();
        Assert.assertTrue(instance.isDecided());
        instance.globalUpdate();
        garden = instance.marginalize();
        TestCase.assertFalse(garden.isWetGrass());// new evidence means grass is no longer wet

        FactHandle fh3 = ksession.insert("rule3");// adds an additional support for the sprinkler, belief set of 2

        ksession.fireAllRules();
        Assert.assertTrue(instance.isDecided());
        instance.globalUpdate();
        garden = instance.marginalize();
        TestCase.assertFalse(garden.isWetGrass());// nothing has changed

        FactHandle fh4 = ksession.insert("rule4");// rule4 introduces a conflict, and the BayesFact becomes undecided

        ksession.fireAllRules();
        TestCase.assertFalse(instance.isDecided());
        try {
            instance.globalUpdate();
            Assert.fail("The BayesFact is undecided, it should throw an exception, as it cannot be updated.");
        } catch (Exception e) {
            // this should fail
        }
        ksession.delete(fh4);// the conflict is resolved, so it should be decided again

        ksession.fireAllRules();
        Assert.assertTrue(instance.isDecided());
        instance.globalUpdate();
        garden = instance.marginalize();
        TestCase.assertFalse(garden.isWetGrass());// back to grass is not wet

        ksession.delete(fh2);// takes the sprinkler belief set back to 1

        ksession.fireAllRules();
        instance.globalUpdate();
        garden = instance.marginalize();
        TestCase.assertFalse(garden.isWetGrass());// still grass is not wet

        ksession.delete(fh3);// no sprinkler support now

        ksession.fireAllRules();
        instance.globalUpdate();
        garden = instance.marginalize();
        Assert.assertTrue(garden.isWetGrass());// grass is wet again

    }
}

