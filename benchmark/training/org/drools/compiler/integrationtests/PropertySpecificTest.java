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
package org.drools.compiler.integrationtests;


import PropertySpecificOption.ALLOWED;
import PropertySpecificOption.ALWAYS;
import ResourceType.DRL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.Assertions;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.EmptyBitMask;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.io.ResourceFactory;


public class PropertySpecificTest extends CommonTestMethodBase {
    @Test
    public void testRTNodeEmptyLHS() {
        String rule = "package org.drools.compiler.integrationtests\n" + ((("rule r1\n" + "when\n") + "then\n") + "end\n");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "InitialFactImpl");
        Assert.assertNotNull(otn);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), rtNode.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), rtNode.getInferredMask());
    }

    @Test
    public void testRTNodeNoConstraintsNoPropertySpecific() {
        String rule = ((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   Person()\n") + "then\n") + "end\n";
        KieBase kbase = new org.kie.internal.utils.KieHelper(PropertySpecificOption.ALLOWED).addContent(rule, DRL).build();
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Person");
        Assert.assertNotNull(otn);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), rtNode.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), rtNode.getInferredMask());
    }

    @Test
    public void testRTNodeWithConstraintsNoPropertySpecific() {
        String rule = ((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   Person( name == \'bobba\')\n") + "then\n") + "end\n";
        KieBase kbase = new org.kie.internal.utils.KieHelper(PropertySpecificOption.ALLOWED).addContent(rule, DRL).build();
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Person");
        Assert.assertNotNull(otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), alphaNode.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), alphaNode.getInferredMask());
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), rtNode.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), rtNode.getInferredMask());
    }

    @Test
    public void testBetaNodeNoConstraintsNoPropertySpecific() {
        String rule = ((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "import ") + (Cheese.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   Person()\n") + "   Cheese()\n") + "then\n") + "end\n";
        KieBase kbase = new org.kie.internal.utils.KieHelper(PropertySpecificOption.ALLOWED).addContent(rule, DRL).build();
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Cheese");
        Assert.assertNotNull(otn);
        BetaNode betaNode = ((BetaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightInferredMask());
    }

    @Test
    public void testBetaNodeWithConstraintsNoPropertySpecific() {
        String rule = ((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "import ") + (Cheese.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   Person()\n") + "   Cheese( type == \'brie\' )\n") + "then\n") + "end\n";
        KieBase kbase = new org.kie.internal.utils.KieHelper(PropertySpecificOption.ALLOWED).addContent(rule, DRL).build();
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Cheese");
        Assert.assertNotNull(otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), alphaNode.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), alphaNode.getInferredMask());
        BetaNode betaNode = ((BetaNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightInferredMask());
    }

    @Test
    public void testInitialFactBetaNodeWithRightInputAdapter() {
        String rule = (((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "import ") + (Cheese.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   exists(eval(1==1))\n") + "then\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "InitialFactImpl");
        Assert.assertNotNull(otn);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        BetaNode betaNode = ((BetaNode) (liaNode.getSinkPropagator().getSinks()[1]));
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getLeftInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightInferredMask());
    }

    @Test
    public void testPersonFactBetaNodeWithRightInputAdapter() {
        String rule = ((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "import ") + (Cheese.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   Person()\n") + "   exists(eval(1==1))\n") + "then\n") + "end\n";
        // assumption is this test was intended to be for the case
        // property reactivity is NOT enabled by default.
        KieBase kbase = new org.kie.internal.utils.KieHelper(PropertySpecificOption.ALLOWED).addContent(rule, DRL).build();
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Person");
        Assert.assertNotNull(otn);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        BetaNode betaNode = ((BetaNode) (liaNode.getSinkPropagator().getSinks()[1]));
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getLeftInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode.getRightInferredMask());
    }

    @Test
    public void testSharedAlphanodeWithBetaNodeConstraintsNoPropertySpecific() {
        String rule = ((((((((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "\n") + "import ") + (Cheese.class.getCanonicalName())) + "\n") + "rule r1\n") + "when\n") + "   Person()\n") + "   Cheese( type == \'brie\', price == 1.5 )\n") + "then\n") + "end\n") + "rule r2\n") + "when\n") + "   Person()\n") + "   Cheese( type == \'brie\', price == 2.5 )\n") + "then\n") + "end\n";
        KieBase kbase = new org.kie.internal.utils.KieHelper(PropertySpecificOption.ALLOWED).addContent(rule, DRL).build();
        ObjectTypeNode otn = getObjectTypeNode(kbase, "Cheese");
        Assert.assertNotNull(otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), alphaNode1.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), alphaNode1_1.getInferredMask());
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode1.getRightInferredMask());
        // second share
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(AllSetBitMask.get(), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), alphaNode1_2.getInferredMask());
        BetaNode betaNode2 = ((BetaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(AllSetBitMask.get(), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNode2.getRightInferredMask());
    }

    @Test
    public void testRtnNoConstraintsNoWatches() {
        String rule1 = "A()";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), rtNode.getDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), rtNode.getInferredMask());
    }

    @Test
    public void testRtnNoConstraintsWithWatches() {
        String rule1 = "A() @watch(a)";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), rtNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), rtNode.getInferredMask());
    }

    @Test
    public void testRtnWithConstraintsNoWatches() {
        String rule1 = "A( a == 10 )";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getInferredMask());
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), rtNode.getDeclaredMask());// rtn declares nothing

        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), rtNode.getInferredMask());// rtn infers from alpha

    }

    @Test
    public void testRtnWithConstraintsWithWatches() {
        String rule1 = "A( a == 10 ) @watch(b)";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode.getInferredMask());
        LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode = ((RuleTerminalNode) (liaNode.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), rtNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), rtNode.getInferredMask());
    }

    @Test
    public void testRtnSharedAlphaNoWatches() {
        String rule1 = "A( a == 10, b == 15 )";
        String rule2 = "A( a == 10, i == 20 )";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "i"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode1_1.getInferredMask());
        LeftInputAdapterNode liaNode1 = ((LeftInputAdapterNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode1 = ((RuleTerminalNode) (liaNode1.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), rtNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), rtNode1.getInferredMask());
        // second share
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i"), sp), alphaNode1_2.getInferredMask());
        LeftInputAdapterNode liaNode2 = ((LeftInputAdapterNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode2 = ((RuleTerminalNode) (liaNode2.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), rtNode2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i"), sp), rtNode2.getInferredMask());
        // test rule removal
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i"), sp), alphaNode1.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i"), sp), alphaNode1_2.getInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), rtNode2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i"), sp), rtNode2.getInferredMask());
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        kbase.removeRule("org.drools.compiler.integrationtests", "r1");
        otn = getObjectTypeNode(kbase, "A");
        alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode1.getInferredMask());
        alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode1_1.getInferredMask());
        liaNode1 = ((LeftInputAdapterNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        rtNode1 = ((RuleTerminalNode) (liaNode1.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), rtNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), rtNode1.getInferredMask());
    }

    @Test
    public void testRtnSharedAlphaWithWatches() {
        String rule1 = "A( a == 10, b == 15 ) @watch(c, !a)";
        String rule2 = "A( a == 10, i == 20 ) @watch(s, !i)";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c", "s", "i"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        LeftInputAdapterNode liaNode1 = ((LeftInputAdapterNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode1 = ((RuleTerminalNode) (liaNode1.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), rtNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), rtNode1.getInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), rtNode1.getNegativeMask());
        // second share
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), alphaNode1_2.getInferredMask());
        LeftInputAdapterNode liaNode2 = ((LeftInputAdapterNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        RuleTerminalNode rtNode2 = ((RuleTerminalNode) (liaNode2.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), rtNode2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s"), sp), rtNode2.getInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!i"), sp), rtNode2.getNegativeMask());
        // test rule removal
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), alphaNode1.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), alphaNode1_2.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), rtNode2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s"), sp), rtNode2.getInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!i"), sp), rtNode2.getNegativeMask());
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        kbase.removeRule("org.drools.compiler.integrationtests", "r1");
        otn = getObjectTypeNode(kbase, "A");
        alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1.getInferredMask());
        alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        liaNode1 = ((LeftInputAdapterNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        rtNode1 = ((RuleTerminalNode) (liaNode1.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), rtNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), rtNode1.getInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), rtNode1.getNegativeMask());
    }

    @Test
    public void testBetaNoConstraintsNoWatches() {
        String rule1 = "B() A()";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        BetaNode betaNode = ((BetaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), betaNode.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNode.getRightInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNode.getLeftInferredMask());
    }

    @Test
    public void testBetaNoConstraintsWithWatches() {
        String rule1 = "B() @watch(a) A() @watch(a)";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        BetaNode betaNode = ((BetaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), betaNode.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), betaNode.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), betaNode.getLeftInferredMask());
    }

    @Test
    public void testBetaWithConstraintsNoWatches() {
        String rule1 = "$b : B(a == 15) A( a == 10, b == $b.b )";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode.getInferredMask());
        BetaNode betaNode = ((BetaNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNode.getRightDeclaredMask());// beta declares nothing

        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), betaNode.getRightInferredMask());// beta infers from alpha

        otn = getObjectTypeNode(kbase, "B");
        alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), betaNode.getLeftInferredMask());
    }

    @Test
    public void testBetaWithConstraintsWithWatches() {
        String rule1 = "$b : B( a == 15) @watch(c) A( a == 10, b == $b.b ) @watch(s)";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "s"), sp), alphaNode.getInferredMask());
        BetaNode betaNode = ((BetaNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "s"), sp), betaNode.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "s"), sp), betaNode.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), betaNode.getLeftInferredMask());
        otn = getObjectTypeNode(kbase, "B");
        alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), betaNode.getLeftInferredMask());
    }

    @Test
    public void testBetaWithConstraintsWithNegativeWatches() {
        String rule1 = "$b : B( a == 15) @watch(c, !a) A( a == 10, b == $b.b ) @watch(s, !a, !b)";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "s"), sp), alphaNode.getInferredMask());
        BetaNode betaNode = ((BetaNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "s"), sp), betaNode.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), betaNode.getRightInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a", "!b"), sp), betaNode.getRightNegativeMask());
        otn = getObjectTypeNode(kbase, "B");
        alphaNode = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode.getLeftNegativeMask());
    }

    @Test
    public void testBetaSharedAlphaNoWatches() {
        String rule1 = "$b : B( a == 15) @watch(c, !a) A( a == 10, s == 15, b == $b.b  )";
        String rule2 = "$b : B( a == 15) @watch(j, !i) A( a == 10, i == 20, b == $b.b  )";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "s", "i"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s", "b"), sp), alphaNode1_1.getInferredMask());
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s", "b"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode1.getLeftNegativeMask());
        // second share
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b"), sp), alphaNode1_2.getInferredMask());
        BetaNode betaNode2 = ((BetaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "j"), sp), betaNode2.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!i"), sp), betaNode2.getLeftNegativeMask());
        // test rule removal
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b"), sp), alphaNode1.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b"), sp), alphaNode1_2.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode1.getLeftNegativeMask());
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        kbase.removeRule("org.drools.compiler.integrationtests", "r1");
        otn = getObjectTypeNode(kbase, "A");
        alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s", "b"), sp), alphaNode1.getInferredMask());
        alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s", "b"), sp), alphaNode1_1.getInferredMask());
        betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "s", "b"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "j"), sp), betaNode2.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!i"), sp), betaNode2.getLeftNegativeMask());
    }

    @Test
    public void testBetaSharedAlphaWithWatches() {
        String rule1 = "$b : B( a == 15) @watch(c, !a) A( a == 10, b == 15, b == $b.b  ) @watch(c, !b)";
        String rule2 = "$b : B( a == 15) @watch(j) A( a == 10, i == 20, b == $b.b ) @watch(s, !a)";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c", "s", "i"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "c"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!b"), sp), betaNode1.getRightNegativeMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode1.getLeftNegativeMask());
        // second share
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "s"), sp), alphaNode1_2.getInferredMask());
        BetaNode betaNode2 = ((BetaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "s"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i", "b", "s"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode2.getRightNegativeMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode1.getLeftNegativeMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "j"), sp), betaNode2.getLeftInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNode2.getLeftNegativeMask());
        // test rule removal
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "s"), sp), alphaNode1.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "s"), sp), alphaNode1_2.getInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "s"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i", "b", "s"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode2.getRightNegativeMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "j"), sp), betaNode2.getLeftInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNode2.getLeftNegativeMask());
        // have to rebuild to remove r1
        kbase = getKnowledgeBase(rule1, rule2);
        kbase.removeRule("org.drools.compiler.integrationtests", "r1");
        otn = getObjectTypeNode(kbase, "A");
        alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1.getInferredMask());
        alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "c"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!b"), sp), betaNode1.getRightNegativeMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNode1.getLeftInferredMask());
        Assert.assertEquals(calculateNegativeMask(classType, list("!a"), sp), betaNode1.getLeftNegativeMask());
    }

    @Test
    public void testComplexBetaSharedAlphaWithWatches() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KieBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c", "s", "i", "j"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "i"), sp), betaNode1.getLeftInferredMask());
        // second share
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "i", "s", "j"), sp), alphaNode1_2.getInferredMask());
        BetaNode betaNode2 = ((BetaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode2.getLeftInferredMask());
        // third share
        AlphaNode alphaNode1_4 = ((AlphaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_4.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "i", "j"), sp), alphaNode1_4.getInferredMask());
        BetaNode betaNode3 = ((BetaNode) (alphaNode1_4.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNode3.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "j"), sp), betaNode3.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("k"), sp), betaNode3.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("c", "k"), sp), betaNode3.getLeftInferredMask());
    }

    @Test
    public void testComplexBetaSharedAlphaWithWatchesRemoveR1() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KieBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "s", "j"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "s", "j"), sp), alphaNode1_1.getInferredMask());
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode1.getLeftInferredMask());
        // second split, third alpha
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "i", "j"), sp), alphaNode1_2.getInferredMask());
        BetaNode betaNode3 = ((BetaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNode3.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "j"), sp), betaNode3.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("k"), sp), betaNode3.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("c", "k"), sp), betaNode3.getLeftInferredMask());
    }

    @Test
    public void testComplexBetaSharedAlphaWithWatchesRemoveR2() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KieBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        kbase.removeRule("org.drools.compiler.integrationtests", "r1");
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c", "i", "j"), sp), alphaNode1.getInferredMask());
        // first split
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "i"), sp), betaNode1.getLeftInferredMask());
        // fist share, second alpha
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "j"), sp), alphaNode1_2.getInferredMask());
        AlphaNode alphaNode1_3 = ((AlphaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_3.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "j"), sp), alphaNode1_3.getInferredMask());
        BetaNode betaNode2 = ((BetaNode) (alphaNode1_3.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "b", "j"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("k"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("c", "k"), sp), betaNode2.getLeftInferredMask());
    }

    @Test
    public void testComplexBetaSharedAlphaWithWatchesRemoveR3() {
        String rule1 = "$b : B( b == 15) @watch(i) A( a == 10, b == 15 ) @watch(c)";
        String rule2 = "$b : B( b == 15) @watch(j) A( a == 10, i == 20 ) @watch(s)";
        String rule3 = "$b : B( c == 15) @watch(k) A( a == 10, i == 20, b == 10 ) @watch(j)";
        KieBase kbase = getKnowledgeBase(rule1, rule2, rule3);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        kbase.removeRule("org.drools.compiler.integrationtests", "r2");
        ObjectTypeNode otn = getObjectTypeNode(kbase, "A");
        Assert.assertNotNull(otn);
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otn);
        AlphaNode alphaNode1 = ((AlphaNode) (otn.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c", "i", "s"), sp), alphaNode1.getInferredMask());
        // first share
        AlphaNode alphaNode1_1 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), alphaNode1_1.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode1_1.getInferredMask());
        // first split
        BetaNode betaNode1 = ((BetaNode) (alphaNode1_1.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), betaNode1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), betaNode1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), betaNode1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "i"), sp), betaNode1.getLeftInferredMask());
        // second split
        AlphaNode alphaNode1_2 = ((AlphaNode) (alphaNode1.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), alphaNode1_2.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), alphaNode1_2.getInferredMask());
        BetaNode betaNode2 = ((BetaNode) (alphaNode1_2.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("s"), sp), betaNode2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "i", "s"), sp), betaNode2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNode2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "j"), sp), betaNode2.getLeftInferredMask());
    }

    @Test
    public void testPropertySpecificSimplified() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + ((((((((((((((((((((("dialect \"mvel\"\n" + "declare A\n") + "    s : String\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    on : boolean\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    A($s : s)\n") + "    $b : B(s != $s) @watch( ! s, on )\n") + "then\n") + "    modify($b) { setS($s) }\n") + "end\n") + "rule R2\n") + "when\n") + "    $b : B(on == false)\n") + "then\n") + "    modify($b) { setOn(true) }\n") + "end\n");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        FactType factTypeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        Object factA = factTypeA.newInstance();
        factTypeA.set(factA, "s", "y");
        ksession.insert(factA);
        FactType factTypeB = kbase.getFactType("org.drools.compiler.integrationtests", "B");
        Object factB = factTypeB.newInstance();
        factTypeB.set(factB, "on", false);
        factTypeB.set(factB, "s", "x");
        ksession.insert(factB);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
        Assert.assertEquals(true, factTypeB.get(factB, "on"));
        Assert.assertEquals("y", factTypeB.get(factB, "s"));
        ksession.dispose();
    }

    @Test
    public void testWatchNothing() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + ((((((((((((((((((((("dialect \"mvel\"\n" + "declare A\n") + "    s : String\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    on : boolean\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    A($s : s)\n") + "    $b : B(s != $s) @watch( !* )\n") + "then\n") + "    modify($b) { setS($s) }\n") + "end\n") + "rule R2\n") + "when\n") + "    $b : B(on == false)\n") + "then\n") + "    modify($b) { setOn(true) }\n") + "end\n");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        FactType factTypeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        Object factA = factTypeA.newInstance();
        factTypeA.set(factA, "s", "y");
        ksession.insert(factA);
        FactType factTypeB = kbase.getFactType("org.drools.compiler.integrationtests", "B");
        Object factB = factTypeB.newInstance();
        factTypeB.set(factB, "on", false);
        factTypeB.set(factB, "s", "x");
        ksession.insert(factB);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
        Assert.assertEquals(true, factTypeB.get(factB, "on"));
        Assert.assertEquals("y", factTypeB.get(factB, "s"));
        ksession.dispose();
    }

    @Test
    public void testWrongPropertyNameInWatchAnnotation() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + ((((((((((((((((((((("dialect \"mvel\"\n" + "declare A\n") + "    s : String\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    on : boolean\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    A($s : s)\n") + "    $b : B(s != $s) @watch( !s1, on )\n") + "then\n") + "    modify($b) { setS($s) }\n") + "end\n") + "rule R2\n") + "when\n") + "    $b : B(on == false)\n") + "then\n") + "    modify($b) { setOn(true) }\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testDuplicatePropertyNamesInWatchAnnotation() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + ((((((((((((((((((((("dialect \"mvel\"\n" + "declare A\n") + "    s : String\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    on : boolean\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    A($s : s)\n") + "    $b : B(s != $s) @watch( s, !s )\n") + "then\n") + "    modify($b) { setS($s) }\n") + "end\n") + "rule R2\n") + "when\n") + "    $b : B(on == false)\n") + "then\n") + "    modify($b) { setOn(true) }\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertEquals(1, kbuilder.getErrors().size());
    }

    @Test
    public void testWrongUasgeOfWatchAnnotationOnNonPropertySpecificClass() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + (((((((((((((((((((("dialect \"mvel\"\n" + "declare A\n") + "    s : String\n") + "end\n") + "declare B\n") + "    on : boolean\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    A($s : s)\n") + "    $b : B(s != $s) @watch( !s, on )\n") + "then\n") + "    modify($b) { setS($s) }\n") + "end\n") + "rule R2\n") + "when\n") + "    $b : B(on == false)\n") + "then\n") + "    modify($b) { setOn(true) }\n") + "end\n");
        // The compilation of the above ^ would turn error under the assumption Property Reactivity is NOT enabled by default.
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        conf.setOption(ALLOWED);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        kbuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes()), DRL);
        Assert.assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testPropertySpecificJavaBean() throws Exception {
        String rule = (((((((((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.C.class.getCanonicalName())) + "\n") + "declare A\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    A($s : s)\n") + "    $c : C(s != $s)\n") + "then\n") + "    modify($c) { setS($s) }\n") + "end\n") + "rule R2\n") + "when\n") + "    $c : C(on == false)\n") + "then\n") + "    modify($c) { turnOn() }\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        FactType factTypeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        Object factA = factTypeA.newInstance();
        factTypeA.set(factA, "s", "y");
        ksession.insert(factA);
        PropertySpecificTest.C c = new PropertySpecificTest.C();
        c.setOn(false);
        c.setS("x");
        ksession.insert(c);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
        Assert.assertEquals(true, c.isOn());
        Assert.assertEquals("y", c.getS());
        ksession.dispose();
    }

    @Test(timeout = 5000)
    public void testPropertySpecificOnAlphaNode() throws Exception {
        String rule = (((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.C.class.getCanonicalName())) + "\n") + "rule R1\n") + "when\n") + "    $c : C(s == \"test\")\n") + "then\n") + "    modify($c) { setOn(true) }\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        PropertySpecificTest.C c = new PropertySpecificTest.C();
        c.setOn(false);
        c.setS("test");
        ksession.insert(c);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        Assert.assertEquals(true, c.isOn());
        ksession.dispose();
    }

    @Test(timeout = 5000)
    public void testPropertySpecificWithUpdate() throws Exception {
        String rule = ((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.C.class.getCanonicalName())) + "\n") + "rule R1\n") + "when\n") + "    $c : C(s == \"test\")\n") + "then\n") + "   $c.setOn(true);\n") + "   update($c);\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        PropertySpecificTest.C c = new PropertySpecificTest.C();
        c.setOn(false);
        c.setS("test");
        ksession.insert(c);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        Assert.assertEquals(true, c.isOn());
        ksession.dispose();
    }

    @Test
    public void testInfiniteLoop() throws Exception {
        String rule = (((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.C.class.getCanonicalName())) + "\n") + "global java.util.concurrent.atomic.AtomicInteger counter\n") + "rule R1\n") + "when\n") + "    $c : C(s == \"test\") @watch( on )\n") + "then\n") + "    modify($c) { turnOn() }\n") + "    if (counter.incrementAndGet() > 10) throw new RuntimeException();\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        AtomicInteger counter = new AtomicInteger(0);
        ksession.setGlobal("counter", counter);
        PropertySpecificTest.C c = new PropertySpecificTest.C();
        c.setOn(false);
        c.setS("test");
        ksession.insert(c);
        Assertions.assertThatThrownBy(() -> ksession.fireAllRules()).isInstanceOf(RuntimeException.class).hasMessageContaining("Exception executing consequence for rule \"R1\"");
    }

    @Test
    public void testClassReactive() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + (((((((((((("global java.util.concurrent.atomic.AtomicInteger counter\n" + "declare B\n") + "    @classReactive\n") + "    on : boolean\n") + "    s : String\n") + "end\n") + "rule R1\n") + "when\n") + "    $b : B(s == \"test\")\n") + "then\n") + "    modify($b) { setOn(true) }\n") + "    if (counter.incrementAndGet() > 10) throw new RuntimeException();\n") + "end\n");
        KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        config.setOption(ALWAYS);
        KieBase kbase = loadKnowledgeBaseFromString(config, rule);
        KieSession ksession = kbase.newKieSession();
        AtomicInteger counter = new AtomicInteger(0);
        ksession.setGlobal("counter", counter);
        FactType factTypeB = kbase.getFactType("org.drools.compiler.integrationtests", "B");
        Object factB = factTypeB.newInstance();
        factTypeB.set(factB, "s", "test");
        factTypeB.set(factB, "on", false);
        ksession.insert(factB);
        Assertions.assertThatThrownBy(() -> ksession.fireAllRules()).isInstanceOf(RuntimeException.class).hasMessageContaining("Exception executing consequence for rule \"R1\"");
    }

    @Test(timeout = 5000)
    public void testSharedWatchAnnotation() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + (((((((((((((((((((((((("declare A\n" + "    @propertyReactive\n") + "    a : int\n") + "    b : int\n") + "    s : String\n") + "    i : int\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    s : String\n") + "    i : int\n") + "end\n") + "rule R1\n") + "when\n") + "    $a : A(a == 0) @watch( i )\n") + "    $b : B(i == $a.i) @watch( s )\n") + "then\n") + "    modify($a) { setS(\"end\") }\n") + "end\n") + "rule R2\n") + "when\n") + "    $a : A(a == 0) @watch( b )\n") + "then\n") + "    modify($a) { setI(1) }\n") + "end\n");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        FactType factTypeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        Object factA = factTypeA.newInstance();
        factTypeA.set(factA, "a", 0);
        factTypeA.set(factA, "b", 0);
        factTypeA.set(factA, "i", 0);
        factTypeA.set(factA, "s", "start");
        ksession.insert(factA);
        FactType factTypeB = kbase.getFactType("org.drools.compiler.integrationtests", "B");
        Object factB = factTypeB.newInstance();
        factTypeB.set(factB, "i", 1);
        factTypeB.set(factB, "s", "start");
        ksession.insert(factB);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
        Assert.assertEquals("end", factTypeA.get(factA, "s"));
    }

    @PropertyReactive
    public static class C {
        private boolean on;

        private String s;

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        @Modifies({ "on" })
        public void turnOn() {
            setOn(true);
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    @Test
    public void testBetaNodePropagation() throws Exception {
        String rule = ((((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Hero.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.MoveCommand.class.getCanonicalName())) + "\n") + "rule \"Move\" when\n") + "   $mc : MoveCommand( move == 1 )") + "   $h  : Hero( canMove == true )") + "then\n") + "   modify( $h ) { setPosition($h.getPosition() + 1) };\n") + "   retract ( $mc );\n") + "   System.out.println( \"Move: \" + $h + \" : \" + $mc );") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        PropertySpecificTest.Hero hero = new PropertySpecificTest.Hero();
        hero.setPosition(0);
        hero.setCanMove(true);
        ksession.insert(hero);
        ksession.fireAllRules();
        PropertySpecificTest.MoveCommand moveCommand = new PropertySpecificTest.MoveCommand();
        moveCommand.setMove(1);
        ksession.insert(moveCommand);
        ksession.fireAllRules();
        moveCommand = moveCommand = new PropertySpecificTest.MoveCommand();
        moveCommand.setMove(1);
        ksession.insert(moveCommand);
        ksession.fireAllRules();
        Assert.assertEquals(2, hero.getPosition());
    }

    @Test(timeout = 5000)
    public void testPropSpecOnPatternWithThis() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + (((((((((((((("declare A\n" + "    @propertyReactive\n") + "    i : int\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    a : A\n") + "end\n") + "rule R1\n") + "when\n") + "    $b : B();\n") + "    $a : A(this == $b.a);\n") + "then\n") + "    modify($b) { setA(null) };\n") + "end\n");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        FactType factTypeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        Object factA = factTypeA.newInstance();
        factTypeA.set(factA, "i", 1);
        ksession.insert(factA);
        FactType factTypeB = kbase.getFactType("org.drools.compiler.integrationtests", "B");
        Object factB = factTypeB.newInstance();
        factTypeB.set(factB, "a", factA);
        ksession.insert(factB);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testPropSpecOnBetaNode() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + ((((((((((((((("declare A\n" + "    @propertyReactive\n") + "    i : int\n") + "end\n") + "declare B\n") + "    @propertyReactive\n") + "    i : int\n") + "    j : int\n") + "end\n") + "rule R1\n") + "when\n") + "    $a : A()\n") + "    $b : B($i : i < 4, j < 2, j == $a.i)\n") + "then\n") + "    modify($b) { setI($i+1) };\n") + "end\n");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        FactType typeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        FactType typeB = kbase.getFactType("org.drools.compiler.integrationtests", "B");
        Object a1 = typeA.newInstance();
        typeA.set(a1, "i", 1);
        ksession.insert(a1);
        Object a2 = typeA.newInstance();
        typeA.set(a2, "i", 2);
        ksession.insert(a2);
        Object b1 = typeB.newInstance();
        typeB.set(b1, "i", 1);
        typeB.set(b1, "j", 1);
        ksession.insert(b1);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(3, rules);
    }

    @Test(timeout = 5000)
    public void testConfig() throws Exception {
        String rule = "package org.drools.compiler.integrationtests\n" + ((((((((("declare A\n" + "    i : int\n") + "    j : int\n") + "end\n") + "rule R1\n") + "when\n") + "    $a : A(i == 1)\n") + "then\n") + "    modify($a) { setJ(2) };\n") + "end\n");
        KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        config.setOption(ALWAYS);
        KieBase kbase = loadKnowledgeBaseFromString(config, rule);
        KieSession ksession = kbase.newKieSession();
        FactType typeA = kbase.getFactType("org.drools.compiler.integrationtests", "A");
        Object a = typeA.newInstance();
        typeA.set(a, "i", 1);
        ksession.insert(a);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testEmptyBetaConstraint() throws Exception {
        String rule = (((((((((((((((((((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Hero.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.Cell.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.Init.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.CompositeImageName.class.getCanonicalName())) + "\n") + "declare CompositeImageName\n") + "   @propertyReactive\n") + "end\n") + "rule \"Show First Cell\" when\n") + "   Init()\n") + "   $c : Cell( row == 0, col == 0 )\n") + "then\n") + "   modify( $c ) { hidden = false };\n") + "end\n") + "\n") + "rule \"Paint Empty Hero\" when\n") + "   $c : Cell()\n") + "   $cin : CompositeImageName( cell == $c )\n") + "   not Hero( row == $c.row, col == $c.col  )\n") + "then\n") + "   modify( $cin ) { hero = \"\" };\n") + "end";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        ksession.insert(new PropertySpecificTest.Init());
        PropertySpecificTest.Cell cell = new PropertySpecificTest.Cell();
        cell.setRow(0);
        cell.setCol(0);
        cell.hidden = true;
        ksession.insert(cell);
        PropertySpecificTest.Hero hero = new PropertySpecificTest.Hero();
        hero.setRow(1);
        hero.setCol(1);
        ksession.insert(hero);
        PropertySpecificTest.CompositeImageName cin = new PropertySpecificTest.CompositeImageName();
        cin.setHero("hero");
        cin.setCell(cell);
        ksession.insert(cin);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
    }

    @Test(timeout = 5000)
    public void testNoConstraint() throws Exception {
        String rule = ((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Cell.class.getCanonicalName())) + "\n") + "rule R1 when\n") + "   $c : Cell()\n") + "then\n") + "   modify( $c ) { hidden = true };\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        ksession.insert(new PropertySpecificTest.Cell());
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test(timeout = 5000)
    public void testNodeSharing() throws Exception {
        String rule = ((((((((((((((((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Cell.class.getCanonicalName())) + "\n") + "rule R1 when\n") + "   $c : Cell()\n") + "then\n") + "   modify( $c ) { hidden = true };\n") + "   System.out.println( \"R1\");\n") + "end\n") + "rule R2 when\n") + "   $c : Cell(hidden == true)\n") + "then\n") + "   System.out.println( \"R2\");\n") + "end\n") + "rule R3 when\n") + "   $c : Cell(hidden == true, row == 0)\n") + "then\n") + "   modify( $c ) { setCol(1) };\n") + "   System.out.println( \"R3\");\n") + "end\n") + "rule R4 when\n") + "   $c : Cell(hidden == true, col == 1)\n") + "then\n") + "   modify( $c ) { setRow(1) };\n") + "   System.out.println( \"R4\");\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        ksession.insert(new PropertySpecificTest.Cell());
        int rules = ksession.fireAllRules();
        Assert.assertEquals(4, rules);
    }

    @PropertyReactive
    public static class Init {}

    @PropertyReactive
    public static class Hero {
        private boolean canMove;

        private int position;

        private int col;

        private int row;

        public boolean isCanMove() {
            return canMove;
        }

        public void setCanMove(boolean canMove) {
            this.canMove = canMove;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        @Override
        public String toString() {
            return (("Hero{" + "position=") + (position)) + '}';
        }
    }

    @PropertyReactive
    public static class MoveCommand {
        private int move;

        public int getMove() {
            return move;
        }

        public void setMove(int move) {
            this.move = move;
        }

        @Override
        public String toString() {
            return (("MoveCommand{" + "move=") + (move)) + '}';
        }
    }

    @PropertyReactive
    public static class Cell {
        private int col;

        private int row;

        public boolean hidden;

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    public static class CompositeImageName {
        private PropertySpecificTest.Cell cell;

        public String hero;

        public PropertySpecificTest.Cell getCell() {
            return cell;
        }

        public void setCell(PropertySpecificTest.Cell cell) {
            this.cell = cell;
        }

        public String getHero() {
            return hero;
        }

        public void setHero(String hero) {
            this.hero = hero;
        }
    }

    @Test(timeout = 5000)
    public void testNoConstraint2() throws Exception {
        String rule = ((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Order.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.OrderItem.class.getCanonicalName())) + "\n") + "rule R1 when\n") + "   $o : Order()\n") + "   $i : OrderItem( orderId == $o.id, quantity > 2 )\n") + "then\n") + "   modify( $o ) { setDiscounted( true ) };\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        PropertySpecificTest.Order order1 = new PropertySpecificTest.Order("1");
        PropertySpecificTest.OrderItem orderItem11 = new PropertySpecificTest.OrderItem("1", 1, 1.1);
        PropertySpecificTest.OrderItem orderItem12 = new PropertySpecificTest.OrderItem("1", 2, 1.2);
        PropertySpecificTest.OrderItem orderItem13 = new PropertySpecificTest.OrderItem("1", 3, 1.3);
        order1.setItems(list(orderItem11, orderItem12, orderItem13));
        ksession.insert(order1);
        ksession.insert(orderItem11);
        ksession.insert(orderItem12);
        ksession.insert(orderItem13);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        Assert.assertTrue(order1.isDiscounted());
    }

    @Test(timeout = 5000)
    public void testFrom() throws Exception {
        String rule = (((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Order.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.OrderItem.class.getCanonicalName())) + "\n") + "rule R1 when\n") + "   $o : Order()\n") + "   $i : OrderItem( $price : price, quantity > 1 ) from $o.items\n") + "then\n") + "   modify( $o ) { setDiscounted( true ) };\n") + "   modify( $i ) { setPrice( $price - 0.1 ) };\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        PropertySpecificTest.Order order1 = new PropertySpecificTest.Order("1");
        PropertySpecificTest.OrderItem orderItem11 = new PropertySpecificTest.OrderItem("1", 1, 1.1);
        PropertySpecificTest.OrderItem orderItem12 = new PropertySpecificTest.OrderItem("1", 2, 1.2);
        PropertySpecificTest.OrderItem orderItem13 = new PropertySpecificTest.OrderItem("1", 3, 1.3);
        order1.setItems(list(orderItem11, orderItem12, orderItem13));
        ksession.insert(order1);
        ksession.insert(orderItem11);
        ksession.insert(orderItem12);
        ksession.insert(orderItem13);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
        Assert.assertEquals(1.1, orderItem11.getPrice(), 0.005);
        Assert.assertEquals(1.1, orderItem12.getPrice(), 0.005);
        Assert.assertEquals(1.2, orderItem13.getPrice(), 0.005);
        Assert.assertTrue(order1.isDiscounted());
    }

    @Test(timeout = 5000)
    public void testAccumulate() throws Exception {
        String rule = (((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (PropertySpecificTest.Order.class.getCanonicalName())) + "\n") + "import ") + (PropertySpecificTest.OrderItem.class.getCanonicalName())) + "\n") + "rule R1 when\n") + "   $o : Order()\n") + "   $i : Number( doubleValue > 5 ) from accumulate( OrderItem( orderId == $o.id, $value : value ),\n") + "                                                   sum( $value ) )\n") + "then\n") + "   modify( $o ) { setDiscounted( true ) };\n") + "end\n";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        PropertySpecificTest.Order order1 = new PropertySpecificTest.Order("1");
        PropertySpecificTest.OrderItem orderItem11 = new PropertySpecificTest.OrderItem("1", 1, 1.1);
        PropertySpecificTest.OrderItem orderItem12 = new PropertySpecificTest.OrderItem("1", 2, 1.2);
        PropertySpecificTest.OrderItem orderItem13 = new PropertySpecificTest.OrderItem("1", 3, 1.3);
        order1.setItems(list(orderItem11, orderItem12, orderItem13));
        ksession.insert(order1);
        ksession.insert(orderItem11);
        ksession.insert(orderItem12);
        ksession.insert(orderItem13);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        Assert.assertTrue(order1.isDiscounted());
    }

    @PropertyReactive
    public static class Order {
        private String id;

        private List<PropertySpecificTest.OrderItem> items;

        private boolean discounted;

        public Order(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<PropertySpecificTest.OrderItem> getItems() {
            return items;
        }

        public void setItems(List<PropertySpecificTest.OrderItem> items) {
            this.items = items;
        }

        public boolean isDiscounted() {
            return discounted;
        }

        public void setDiscounted(boolean discounted) {
            this.discounted = discounted;
        }
    }

    @PropertyReactive
    public static class OrderItem {
        private String orderId;

        private int quantity;

        private double price;

        public OrderItem(String orderId, int quantity, double price) {
            this.orderId = orderId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getValue() {
            return (price) * (quantity);
        }
    }

    @Test
    public void testBetaWithWatchAfterBeta() {
        String rule1 = "$b : B(a == 15) @watch(k) C() A(i == $b.j) @watch(b, c)";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otnA = getObjectTypeNode(kbase, "A");
        Class classType = getClassType();
        ObjectTypeNode otnC = getObjectTypeNode(kbase, "C");
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otnA);
        BetaNode betaNodeA = ((BetaNode) (otnA.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i", "b", "c"), sp), betaNodeA.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i", "b", "c"), sp), betaNodeA.getRightInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeA.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeA.getLeftInferredMask());
        BetaNode betaNodeC = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j", "k"), sp), betaNodeC.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "j", "k"), sp), betaNodeC.getLeftInferredMask());
    }

    @Test
    public void testBetaAfterBetaWithWatch() {
        String rule1 = "$b : B(a == 15) @watch(k) A(i == $b.j) @watch(b, c) C()";
        KieBase kbase = getKnowledgeBase(rule1);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otnA = getObjectTypeNode(kbase, "A");
        Class classType = getClassType();
        ObjectTypeNode otnC = getObjectTypeNode(kbase, "C");
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otnA);
        BetaNode betaNodeA = ((BetaNode) (otnA.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i", "b", "c"), sp), betaNodeA.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i", "b", "c"), sp), betaNodeA.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j", "k"), sp), betaNodeA.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "j", "k"), sp), betaNodeA.getLeftInferredMask());
        BetaNode betaNodeC = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeC.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeC.getLeftInferredMask());
    }

    @Test
    public void test2DifferentAlphaWatchBeforeSameBeta() {
        String rule1 = "B(a == 15) @watch(b) C()";
        String rule2 = "B(a == 15) @watch(c) C()";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otnB = getObjectTypeNode(kbase, "B");
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otnB);
        Class classType = getClassType();
        AlphaNode alphaNode = ((AlphaNode) (otnB.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode.getInferredMask());
        ObjectTypeNode otnC = getObjectTypeNode(kbase, "C");
        BetaNode betaNodeC1 = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[0]));
        BetaNode betaNodeC2 = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[1]));
        LeftInputAdapterNode lia1 = ((LeftInputAdapterNode) (alphaNode.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertSame(betaNodeC1, lia1.getSinkPropagator().getSinks()[0]);
        LeftInputAdapterNode lia2 = ((LeftInputAdapterNode) (alphaNode.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertSame(betaNodeC2, lia2.getSinkPropagator().getSinks()[0]);
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC1.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNodeC1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), betaNodeC1.getLeftInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC2.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), betaNodeC2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "c"), sp), betaNodeC2.getLeftInferredMask());
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "c"), sp), alphaNode.getInferredMask());
        Assert.assertEquals(1, lia2.getSinkPropagator().getSinks().length);
        BetaNode betaNodeC = ((BetaNode) (lia2.getSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC2.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), betaNodeC2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "c"), sp), betaNodeC2.getLeftInferredMask());
    }

    @Test
    public void testSameBetasWith2RTNSinks() {
        String rule1 = "B(a == 15) C() A()";
        String rule2 = "B(a == 15) C() A() @watch(b, c)";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otnA = getObjectTypeNode(kbase, "A");
        Class classType = getClassType();
        ObjectTypeNode otnC = getObjectTypeNode(kbase, "C");
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otnA);
        BetaNode betaNodeC = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[0]));
        BetaNode betaNodeA1 = ((BetaNode) (otnA.getObjectSinkPropagator().getSinks()[0]));
        BetaNode betaNodeA2 = ((BetaNode) (otnA.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertSame(betaNodeC.getSinkPropagator().getSinks()[0], betaNodeA1);
        Assert.assertSame(betaNodeC.getSinkPropagator().getSinks()[1], betaNodeA2);
        Assert.assertSame(betaNodeA1.getLeftTupleSource(), betaNodeC);
        Assert.assertSame(betaNodeA2.getLeftTupleSource(), betaNodeC);
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), betaNodeC.getLeftInferredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeA1.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeA1.getRightInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeA1.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeA1.getLeftInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNodeA2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b", "c"), sp), betaNodeA2.getRightInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeA2.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeA2.getLeftInferredMask());
        kbase.removeRule("org.drools.compiler.integrationtests", "r0");
        Assert.assertEquals(1, betaNodeC.getSinkPropagator().getSinks().length);
    }

    @Test
    public void testBetaWith2BetaSinks() {
        String rule1 = "B(a == 15) @watch(b) A() @watch(i) C()";
        String rule2 = "B(a == 15) @watch(c) A() @watch(j) D()";
        KieBase kbase = getKnowledgeBase(rule1, rule2);
        InternalWorkingMemory wm = ((InternalWorkingMemory) (kbase.newKieSession()));
        ObjectTypeNode otnB = getObjectTypeNode(kbase, "B");
        Class classType = getClassType();
        List<String> sp = PropertySpecificTest.getSettableProperties(wm, otnB);
        AlphaNode alphaNode = ((AlphaNode) (otnB.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b", "c"), sp), alphaNode.getInferredMask());
        ObjectTypeNode otnA = getObjectTypeNode(kbase, "A");
        BetaNode betaNodeA1 = ((BetaNode) (otnA.getObjectSinkPropagator().getSinks()[0]));
        BetaNode betaNodeA2 = ((BetaNode) (otnA.getObjectSinkPropagator().getSinks()[1]));
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), betaNodeA1.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("i"), sp), betaNodeA1.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("b"), sp), betaNodeA1.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), betaNodeA1.getLeftInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNodeA2.getRightDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("j"), sp), betaNodeA2.getRightInferredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("c"), sp), betaNodeA2.getLeftDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "c"), sp), betaNodeA2.getLeftInferredMask());
        ObjectTypeNode otnC = getObjectTypeNode(kbase, "C");
        BetaNode betaNodeC = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeC.getRightInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeC.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeC.getLeftInferredMask());
        ObjectTypeNode otnD = getObjectTypeNode(kbase, "D");
        BetaNode betaNodeD = ((BetaNode) (otnC.getObjectSinkPropagator().getSinks()[0]));
        Assert.assertEquals(EmptyBitMask.get(), betaNodeD.getRightDeclaredMask());
        Assert.assertEquals(EmptyBitMask.get(), betaNodeD.getRightInferredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeD.getLeftDeclaredMask());
        Assert.assertEquals(AllSetBitMask.get(), betaNodeD.getLeftInferredMask());
        kbase.removeRule("org.drools.compiler.integrationtests", "r1");
        Assert.assertEquals(calculatePositiveMask(classType, list("a"), sp), alphaNode.getDeclaredMask());
        Assert.assertEquals(calculatePositiveMask(classType, list("a", "b"), sp), alphaNode.getInferredMask());
    }

    @Test(timeout = 5000)
    public void testBetaWith2RTNSinksExecNoLoop() throws Exception {
        testBetaWith2RTNSinksExec(false);
    }

    @Test
    public void testBetaWith2RTNSinksExecInfiniteLoop() throws Exception {
        Assertions.assertThatThrownBy(() -> testBetaWith2RTNSinksExec(true)).isInstanceOf(RuntimeException.class).hasMessageContaining("Exception executing consequence for rule \"R1\"");
    }

    @Test(timeout = 5000)
    public void testBetaWith2BetaSinksExecNoLoop() throws Exception {
        testBetaWith2BetaSinksExec(false);
    }

    @Test
    public void testBetaWith2BetaSinksExecInfiniteLoop() throws Exception {
        Assertions.assertThatThrownBy(() -> testBetaWith2BetaSinksExec(true)).isInstanceOf(RuntimeException.class).hasMessageContaining("Exception executing consequence for rule \"R1\"");
    }

    @Test(timeout = 5000)
    public void testTypeDeclarationInitializationForPropertyReactive() {
        // JBRULES-3686
        String rule = ((((((((((((((((((((((((((((((((((((((((((("package org.drools.compiler.integrationtests\n" + (("import java.util.Map;\n" + "import java.util.EnumMap;\n") + "import ")) + (PropertySpecificTest.DataSample.class.getCanonicalName())) + ";\n") + "import ") + (PropertySpecificTest.Model.class.getCanonicalName())) + ";\n") + "import ") + (PropertySpecificTest.Parameter.class.getCanonicalName())) + ";\n") + "\n") + "rule \'Init\'\n") + "when\n") + "    $m: Model()\n") + "then\n") + "    insert(new DataSample($m));\n") + "end\n") + "\n") + "rule \"Rule 1\"\n") + "when\n") + "    $m: Model()\n") + "    $d: DataSample(model == $m)\n") + "then\n") + "    modify($d){\n") + "        addValue(Parameter.PARAM_A, 10.0)\n") + "    }\n") + "end\n") + "\n") + "rule \"Rule 2\"\n") + "when\n") + "    $m: Model()\n") + "    $d: DataSample(model == $m, $v: values[Parameter.PARAM_A] > 9.0)\n") + "then\n") + "    modify($d){\n") + "        addMessage(\"Hello\")\n") + "    }\n") + "end\n") + "\n") + "rule \"Data without messages\"\n") + "salience -100\n") + "when\n") + "    $m: Model()\n") + "    $d: DataSample(model == $m, messaged == false)\n") + "then\n") + "end";
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        ksession.insert(new PropertySpecificTest.Model());
        ksession.fireAllRules();
    }

    @PropertyReactive
    public static class DataSample {
        private PropertySpecificTest.Model model;

        private Map<PropertySpecificTest.Parameter, Double> values = new EnumMap<PropertySpecificTest.Parameter, Double>(PropertySpecificTest.Parameter.class);

        private List<String> messages = new ArrayList<String>();

        public DataSample() {
        }

        public DataSample(PropertySpecificTest.Model model) {
            this.model = model;
        }

        public PropertySpecificTest.Model getModel() {
            return model;
        }

        public void setModel(PropertySpecificTest.Model model) {
            this.model = model;
        }

        public Map<PropertySpecificTest.Parameter, Double> getValues() {
            return values;
        }

        public void setValues(Map<PropertySpecificTest.Parameter, Double> values) {
            this.values = values;
        }

        @Modifies({ "values" })
        public void addValue(PropertySpecificTest.Parameter p, double value) {
            this.values.put(p, value);
        }

        public boolean isEmpty() {
            return this.values.isEmpty();
        }

        public List<String> getMessages() {
            return messages;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
        }

        @Modifies({ "messages", "messaged" })
        public void addMessage(String message) {
            this.messages.add(message);
        }

        public boolean isMessaged() {
            return !(this.messages.isEmpty());
        }

        public void setMessaged(boolean b) {
        }
    }

    public static class Model {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static enum Parameter {

        PARAM_A,
        PARAM_B;}

    @Test
    public void testRemovedPendingActivation() {
        String rule = "declare Person\n" + ((((((((((((((((((((((((((((((((((((((("@propertyReactive\n" + "   name   : String\n") + "   age    : int\n") + "   weight : int\n") + "end\n") + "\n") + "rule kickoff\n") + "salience 100\n") + "when\n") + "then\n") + "    Person p = new Person( \"Joe\", 20, 20 );\n") + "    insert( p );\n") + "end\n") + "\n") + "rule y\n") + "when\n") + "    $p : Person(name == \"Joe\" )\n") + "then\n") + "    modify($p){\n") + "       setAge( 100 )\n") + "    }\n") + "end\n") + "\n") + "rule x\n") + "when\n") + "    $p : Person(name == \"Joe\" )\n") + "then\n") + "    modify($p){\n") + "        setWeight( 100 )\n") + "    }\n") + "end\n") + "\n") + "rule z\n") + "salience -100\n") + "when\n") + "    $p : Person()\n") + "then\n") + "    System.out.println( $p );\n") + "    if ($p.getAge() != 100 || $p.getWeight() != 100) throw new RuntimeException();\n") + "end");
        KieBase kbase = loadKnowledgeBaseFromString(rule);
        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();
    }
}

