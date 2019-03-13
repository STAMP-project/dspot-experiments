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


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.mockito.Mockito;


public class IntegrationInterfacesTest extends CommonTestMethodBase {
    @SuppressWarnings("unchecked")
    @Test
    public void testGlobals() throws Exception {
        final KieBase kbase = getKnowledgeBase("globals_rule_test.drl");
        KieSession ksession = createKnowledgeSession(kbase);
        final List<Object> list = Mockito.mock(List.class);
        ksession.setGlobal("list", list);
        ksession.setGlobal("string", "stilton");
        final Cheese stilton = new Cheese("stilton", 5);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Mockito.verify(list, Mockito.times(1)).add(new Integer(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGlobals2() throws Exception {
        final KieBase kbase = getKnowledgeBase("test_globalsAsConstraints.drl");
        KieSession ksession = createKnowledgeSession(kbase);
        final List<Object> results = Mockito.mock(List.class);
        ksession.setGlobal("results", results);
        final List<String> cheeseTypes = Mockito.mock(List.class);
        ksession.setGlobal("cheeseTypes", cheeseTypes);
        Mockito.when(cheeseTypes.contains("stilton")).thenReturn(Boolean.TRUE);
        Mockito.when(cheeseTypes.contains("muzzarela")).thenReturn(Boolean.TRUE);
        final Cheese stilton = new Cheese("stilton", 5);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Mockito.verify(results, Mockito.times(1)).add("memberOf");
        final Cheese brie = new Cheese("brie", 5);
        ksession.insert(brie);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        Mockito.verify(results, Mockito.times(1)).add("not memberOf");
    }

    @Test
    public void testGlobalMerge() throws Exception {
        // from JBRULES-1512
        String rule1 = "package com.sample\n" + (((((("rule \"rule 1\"\n" + "    salience 10\n") + "    when\n") + "        l : java.util.List()\n") + "    then\n") + "        l.add( \"rule 1 executed\" );\n") + "end\n");
        String rule2 = "package com.sample\n" + (((((("global String str;\n" + "rule \"rule 2\"\n") + "    when\n") + "        l : java.util.List()\n") + "    then\n") + "        l.add( \"rule 2 executed \" + str);\n") + "end\n");
        StringReader[] readers = new StringReader[2];
        readers[0] = new StringReader(rule1);
        readers[1] = new StringReader(rule2);
        final KieBase kbase = getKnowledgeBase(readers);
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.setGlobal("str", "boo");
        List<String> list = new ArrayList<String>();
        ksession.insert(list);
        ksession.fireAllRules();
        Assert.assertEquals("rule 1 executed", list.get(0));
        Assert.assertEquals("rule 2 executed boo", list.get(1));
    }

    @Test
    public void testChannels() throws IOException, ClassNotFoundException {
        KieBase kbase = getKnowledgeBase("test_Channels.drl");
        KieSession ksession = createKnowledgeSession(kbase);
        Channel someChannel = Mockito.mock(Channel.class);
        ksession.registerChannel("someChannel", someChannel);
        ksession.insert(new Cheese("brie", 30));
        ksession.insert(new Cheese("stilton", 5));
        ksession.fireAllRules();
        Mockito.verify(someChannel).send("brie");
        Mockito.verify(someChannel, Mockito.never()).send("stilton");
    }
}

