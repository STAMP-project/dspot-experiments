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
package org.drools.template.model;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test rendering and running a whole sample ruleset, from the model classes
 * down.
 */
public class PackageRenderTest {
    @Test
    public void testRulesetRender() {
        final Package ruleSet = new Package("my ruleset");
        addFunctions("my functions");
        ruleSet.addRule(buildRule());
        final Rule rule = buildRule();
        rule.setName("other rule");
        ruleSet.addRule(rule);
        final Import imp = new Import();
        imp.setClassName("clazz name");
        imp.setComment("import comment");
        ruleSet.addImport(imp);
        final DRLOutput out = new DRLOutput();
        ruleSet.renderDRL(out);
        final String drl = out.getDRL();
        Assert.assertNotNull(drl);
        System.out.println(drl);
        Assert.assertTrue(((drl.indexOf("rule \"myrule\"")) > (-1)));
        Assert.assertTrue(((drl.indexOf("salience 42")) > (-1)));
        Assert.assertTrue(((drl.indexOf("//rule comments")) > (-1)));
        Assert.assertTrue(((drl.indexOf("my functions")) > (-1)));
        Assert.assertTrue(((drl.indexOf("package my_ruleset;")) > (-1)));
        Assert.assertTrue(((drl.indexOf("rule \"other rule\"")) > (drl.indexOf("rule \"myrule\""))));
    }

    @Test
    public void testRulesetAttribute() {
        final Package ruleSet = new Package("my ruleset");
        setAgendaGroup("agroup");
        setNoLoop(true);
        setSalience(100);
        final DRLOutput out = new DRLOutput();
        ruleSet.renderDRL(out);
        final String drl = out.getDRL();
        Assert.assertTrue(drl.contains("agenda-group \"agroup\""));
        Assert.assertTrue(drl.contains("no-loop true"));
        Assert.assertTrue(drl.contains("salience 100"));
    }
}

