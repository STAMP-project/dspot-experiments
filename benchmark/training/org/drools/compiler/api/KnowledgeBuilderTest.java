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
package org.drools.compiler.api;


import KieServices.Factory;
import ResourceType.DRL;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class KnowledgeBuilderTest {
    @Test
    public void testKnowledgeProvider() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Assert.assertNotNull(builder);
    }

    @Test
    public void testKnowledgeProviderWithRules() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        String str = "";
        str += "package org.drools.compiler.test1\n";
        str += "rule rule1\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule2\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        str = "package org.drools.compiler.test2\n";
        str += "rule rule3\n";
        str += "when\n";
        str += "then\n";
        str += "end\n\n";
        str += "rule rule4\n";
        str += "when\n";
        str += "then\n";
        str += "end\n";
        builder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        Collection<KiePackage> pkgs = builder.getKnowledgePackages();
        Assert.assertNotNull(pkgs);
        Assert.assertEquals(2, pkgs.size());
        KiePackage test1 = getKnowledgePackage(pkgs, "org.drools.compiler.test1");
        Collection<Rule> rules = test1.getRules();
        Assert.assertEquals(2, rules.size());
        Rule rule = getRule(rules, "rule1");
        Assert.assertEquals("rule1", rule.getName());
        rule = getRule(rules, "rule2");
        Assert.assertEquals("rule2", rule.getName());
        KiePackage test2 = getKnowledgePackage(pkgs, "org.drools.compiler.test2");
        rules = test2.getRules();
        Assert.assertEquals(2, rules.size());
        rule = getRule(rules, "rule3");
        Assert.assertEquals("rule3", rule.getName());
        rule = getRule(rules, "rule4");
        Assert.assertEquals("rule4", rule.getName());
    }

    @Test
    public void testEmptyByteResource() throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            builder.add(ResourceFactory.newByteArrayResource(new byte[0]), DRL);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testMalformedDrl() throws Exception {
        // DROOLS-928
        byte[] content = new byte[]{ 4, 68, 0, 0, 96, 0, 0, 0 };
        KieServices ks = Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", new String(content));
        Results results = ks.newKieBuilder(kfs).buildAll().getResults();
        Assert.assertTrue(((results.getMessages().size()) > 0));
    }
}

