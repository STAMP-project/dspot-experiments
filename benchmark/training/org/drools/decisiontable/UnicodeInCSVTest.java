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
package org.drools.decisiontable;


import DecisionTableInputType.CSV;
import ResourceType.DTABLE;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.compiler.DecisionTableFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;


public class UnicodeInCSVTest {
    @Test
    public void testUnicodeCSVDecisionTable() throws FileNotFoundException {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("unicode.csv", getClass()), DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            System.out.println(kbuilder.getErrors().toString());
            System.out.println(DecisionTableFactory.loadFromInputStream(getClass().getResourceAsStream("unicode.xls"), dtconf));
            Assert.fail(("Cannot build CSV decision table containing utf-8 characters\n" + (kbuilder.getErrors().toString())));
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        List<Command<?>> commands = new ArrayList<Command<?>>();
        List<UnicodeInCSVTest.?lov?k> dosp?l? = new ArrayList<UnicodeInCSVTest.?lov?k>();
        commands.add(CommandFactory.newSetGlobal("dosp?l?", dosp?l?));
        UnicodeInCSVTest.?lov?k ?eho? = new UnicodeInCSVTest.?lov?k();
        ?eho?.setV?k(30);
        ?eho?.setJm?no("?eho?");
        commands.add(CommandFactory.newInsert(?eho?));
        commands.add(CommandFactory.newFireAllRules());
        ksession.execute(CommandFactory.newBatchExecution(commands));
        // people with age greater than 18 should be added to list of adults
        Assert.assertNotNull(kbase.getRule("org.drools.decisiontable", "p?idej k dosp?l?m"));
        Assert.assertEquals(dosp?l?.size(), 5);
        Assert.assertEquals(dosp?l?.iterator().next().getJm?no(), "?eho?");
        Assert.assertNotNull(kbase.getRule("org.drools.decisiontable", "?????? ???"));
        Assert.assertNotNull(kbase.getRule("org.drools.decisiontable", "????"));
        Assert.assertNotNull(kbase.getRule("org.drools.decisiontable", "hall? v?rlden"));
        Assert.assertNotNull(kbase.getRule("org.drools.decisiontable", "????? ??????"));
        ksession.dispose();
    }

    public static class ?lov?k {
        private int v?k;

        private String jm?no;

        public void setV?k(int v?k) {
            this.v?k = v?k;
        }

        public int getV?k() {
            return v?k;
        }

        public void setJm?no(String jm?no) {
            this.jm?no = jm?no;
        }

        public String getJm?no() {
            return jm?no;
        }
    }
}

