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


import ResourceType.DRL;
import java.util.Collection;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.io.ResourceFactory;


public class DynamicEvalTest {
    KieBase kbase;

    KieSession session;

    SessionPseudoClock clock;

    Collection<? extends Object> effects;

    KnowledgeBuilder kbuilder;

    KieBaseConfiguration baseConfig;

    KieSessionConfiguration sessionConfig;

    @Test
    public void testDynamicAdd() {
        String test = "\nrule id3" + (((("\nwhen" + "\neval(0 < 1)")// this eval works
         + "\nthen") + "\ninsertLogical( \"done\" );") + "\nend");
        loadPackages(ResourceFactory.newByteArrayResource(test.getBytes()), DRL);
        ((KnowledgeBaseImpl) (session.getKieBase())).addPackages(kbuilder.getKnowledgePackages());
        session.addEventListener(new DebugRuleRuntimeEventListener());
        int fired = session.fireAllRules();// 1

        System.out.println(fired);
        effects = session.getObjects();
        Assert.assertTrue("fired", effects.contains("done"));
        // so the above works, let's try it again
        String test2 = "\nrule id4" + (((("\nwhen" + "\neval(0 == 0 )")// this eval doesn't
         + "\nthen") + "\ninsertLogical( \"done2\" );") + "\nend");
        loadPackages(ResourceFactory.newByteArrayResource(test2.getBytes()), DRL);
        ((KnowledgeBaseImpl) (session.getKieBase())).addPackages(kbuilder.getKnowledgePackages());
        fired = session.fireAllRules();// 0

        System.out.println(fired);
        effects = session.getObjects();
        Assert.assertTrue("fired", effects.contains("done2"));// fails

    }

    @Test
    public void testDynamicAdd2() {
        String test = "rule id3\n" + (((((((((((("when\n" + "eval(0 == 0)\n") + "String( this == \"go\" )\n")// this eval works
         + "then\n") + "insertLogical( \"done\" );\n") + "end\n") + "rule id5\n") + "when\n") + "eval(0 == 0)\n") + "Integer( this == 7 )\n")// this eval works
         + "then\n") + "insertLogical( \"done3\" );\n") + "end\n");
        loadPackages(ResourceFactory.newByteArrayResource(test.getBytes()), DRL);
        ((KnowledgeBaseImpl) (session.getKieBase())).addPackages(kbuilder.getKnowledgePackages());
        session.addEventListener(new DebugRuleRuntimeEventListener());
        session.insert("go");
        session.insert(5);
        session.insert(7);
        int fired = session.fireAllRules();// 1

        System.out.println(fired);
        effects = session.getObjects();
        Assert.assertTrue("fired", effects.contains("done"));
        // so the above works, let's try it again
        String test2 = "\nrule id4" + ((((("\nwhen" + "\neval(0 == 0 )")// this eval doesn't
         + "\nInteger( this == 5 )") + "\nthen") + "\ninsertLogical( \"done2\" );") + "\nend");
        loadPackages(ResourceFactory.newByteArrayResource(test2.getBytes()), DRL);
        ((KnowledgeBaseImpl) (session.getKieBase())).addPackages(kbuilder.getKnowledgePackages());
        fired = session.fireAllRules();// 0

        System.out.println(fired);
        effects = session.getObjects();
        Assert.assertTrue("fired", effects.contains("done2"));// fails

        for (Object o : session.getObjects()) {
            System.out.println(o);
        }
    }
}

