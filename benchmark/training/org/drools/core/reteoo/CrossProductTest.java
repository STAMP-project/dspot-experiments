/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.reteoo;


import java.util.List;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class CrossProductTest {
    private InternalKnowledgePackage pkg;

    private KieSession ksession;

    private List values;

    @Test
    public void testNotRemoveIdentities() throws Exception {
        // Default is remove identity FALSE
        InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase()));
        kBase.addPackage(this.pkg);
        this.ksession = kBase.newKieSession();
        this.ksession.insert("F1");
        this.ksession.insert("F2");
        this.ksession.insert("F3");
        this.ksession.insert("F4");
        this.ksession.fireAllRules();
        // A full cross product is 16, this is just 12
        System.out.println(values);
        Assert.assertEquals(16, this.values.size());
    }

    @Test
    public void testRemoveIdentities() throws Exception {
        System.setProperty("drools.removeIdentities", "true");
        try {
            InternalKnowledgeBase kBase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase()));
            kBase.addPackage(this.pkg);
            this.ksession = kBase.newKieSession();
            this.ksession.insert("F1");
            this.ksession.insert("F2");
            this.ksession.insert("F3");
            this.ksession.insert("F4");
            this.ksession.fireAllRules();
            // A full cross product is 16, this is just 12
            Assert.assertEquals(12, this.values.size());
        } finally {
            System.setProperty("drools.removeIdentities", "false");
        }
    }
}

