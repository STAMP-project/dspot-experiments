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


import ResourceType.CHANGE_SET;
import java.util.ArrayList;
import java.util.List;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class ChangeSetTest {
    @Test
    public void testIntegration() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("changeset1Test.xml", getClass()), CHANGE_SET);
        Assert.assertFalse(kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(new Cheese("cheddar", 42));
        ksession.insert(new Person("michael", "stilton", 25));
        ksession.fireAllRules();
        ksession.dispose();
        Assert.assertEquals(3, list.size());
        Assert.assertEquals("Young man cheddar", list.get(0));
        Assert.assertEquals("rule1", list.get(1));
        Assert.assertEquals("rule2", list.get(2));
    }

    @Test
    public void multipleSheets() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("multipleSheetsChangeSet.xml", getClass()), CHANGE_SET);
        Assert.assertFalse(kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(new Cheese("cheddar", 42));
        ksession.insert(new Person("michael", "stilton", 25));
        ksession.insert(new Person("Jane", "stilton", 55));
        ksession.fireAllRules();
        ksession.dispose();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains("Young man cheddar"));
        Assert.assertTrue(list.contains("Jane eats cheddar"));
    }

    @Test
    public void testCSV() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("changeSetTestCSV.xml", getClass()), CHANGE_SET);
        Assert.assertFalse(kbuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        Assert.assertEquals(1, kbase.getKiePackages().size());
        Assert.assertEquals(3, kbase.getKiePackages().iterator().next().getRules().size());
    }
}

