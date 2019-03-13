/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.rule;


import ClockType.PSEUDO_CLOCK;
import EnabledBoolean.ENABLED_FALSE;
import EnabledBoolean.ENABLED_TRUE;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.base.SalienceInteger;
import org.drools.core.base.mvel.MVELSalienceExpression;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.spi.Salience;
import org.junit.Assert;
import org.junit.Test;


public class RuleTest {
    @Test
    public void testDateEffective() {
        WorkingMemory wm = ((WorkingMemory) (new KnowledgeBaseImpl("x", null).newKieSession()));
        final RuleImpl rule = new RuleImpl("myrule");
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis(10);
        rule.setDateEffective(earlier);
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis(((later.getTimeInMillis()) + 100000000));
        Assert.assertTrue(later.after(Calendar.getInstance()));
        rule.setDateEffective(later);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
    }

    @Test
    public void testDateExpires() throws Exception {
        WorkingMemory wm = ((WorkingMemory) (new KnowledgeBaseImpl("x", null).newKieSession()));
        final RuleImpl rule = new RuleImpl("myrule");
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis(10);
        rule.setDateExpires(earlier);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis(((later.getTimeInMillis()) + 100000000));
        rule.setDateExpires(later);
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
    }

    @Test
    public void testDateEffectiveExpires() {
        WorkingMemory wm = ((WorkingMemory) (new KnowledgeBaseImpl("x", null).newKieSession()));
        final RuleImpl rule = new RuleImpl("myrule");
        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis(10);
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis(((future.getTimeInMillis()) + 100000000));
        rule.setDateEffective(past);
        rule.setDateExpires(future);
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
        rule.setDateExpires(past);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
        rule.setDateExpires(future);
        rule.setDateEffective(future);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
    }

    @Test
    public void testRuleEnabled() {
        WorkingMemory wm = ((WorkingMemory) (new KnowledgeBaseImpl("x", null).newKieSession()));
        final RuleImpl rule = new RuleImpl("myrule");
        rule.setEnabled(ENABLED_FALSE);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis(10);
        rule.setDateEffective(past);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
        rule.setEnabled(ENABLED_TRUE);
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
    }

    @Test
    public void testTimeMachine() {
        SessionConfiguration conf = SessionConfiguration.newInstance();
        conf.setClockType(PSEUDO_CLOCK);
        WorkingMemory wm = ((WorkingMemory) (new KnowledgeBaseImpl("x", null).newKieSession(conf, null)));
        final Calendar future = Calendar.getInstance();
        setStartupTime(future.getTimeInMillis());
        final RuleImpl rule = new RuleImpl("myrule");
        rule.setEnabled(ENABLED_TRUE);
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
        future.setTimeInMillis(((future.getTimeInMillis()) + 100000000));
        rule.setDateEffective(future);
        Assert.assertFalse(rule.isEffective(null, new RuleTerminalNode(), wm));
        advanceTime(1000000000000L, TimeUnit.MILLISECONDS);
        Assert.assertTrue(rule.isEffective(null, new RuleTerminalNode(), wm));
    }

    @Test
    public void testGetSalienceValue() {
        final RuleImpl rule = new RuleImpl("myrule");
        final int salienceValue = 100;
        Salience salience = new SalienceInteger(salienceValue);
        rule.setSalience(salience);
        Assert.assertEquals(salienceValue, rule.getSalienceValue());
        Assert.assertFalse(rule.isSalienceDynamic());
    }

    @Test
    public void testIsSalienceDynamic() {
        final RuleImpl rule = new RuleImpl("myrule");
        Salience salience = new MVELSalienceExpression();
        rule.setSalience(salience);
        Assert.assertTrue(rule.isSalienceDynamic());
    }
}

