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
package org.drools.core.test.model;


import AssertBehaviour.EQUALITY;
import AssertBehaviour.IDENTITY;
import SequentialAgenda.DYNAMIC;
import SequentialAgenda.SEQUENTIAL;
import java.util.Properties;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.junit.Assert;
import org.junit.Test;


public class RuleBaseConfigurationTest {
    @Test
    public void testSystemProperties() {
        RuleBaseConfiguration cfg = new RuleBaseConfiguration();
        Assert.assertEquals(IDENTITY, cfg.getAssertBehaviour());
        System.setProperty("drools.equalityBehavior", "EQUALITY");
        cfg = new RuleBaseConfiguration();
        Assert.assertEquals(EQUALITY, cfg.getAssertBehaviour());
        System.getProperties().remove("drools.equalityBehavior");
    }

    @Test
    public void testProgrammaticPropertiesFile() {
        RuleBaseConfiguration cfg = new RuleBaseConfiguration();
        Assert.assertEquals(true, cfg.isIndexLeftBetaMemory());
        Properties properties = new Properties();
        properties.setProperty("drools.indexLeftBetaMemory", "false");
        cfg = new RuleBaseConfiguration(properties);
        Assert.assertEquals(false, cfg.isIndexLeftBetaMemory());
        System.getProperties().remove("drools.indexLeftBetaMemory");
    }

    @Test
    public void testAssertBehaviour() {
        Properties properties = new Properties();
        properties.setProperty("drools.equalityBehavior", "identity");
        RuleBaseConfiguration cfg = new RuleBaseConfiguration(properties);
        Assert.assertEquals(IDENTITY, cfg.getAssertBehaviour());
        properties = new Properties();
        properties.setProperty("drools.equalityBehavior", "equality");
        cfg = new RuleBaseConfiguration(properties);
        Assert.assertEquals(EQUALITY, cfg.getAssertBehaviour());
    }

    @Test
    public void testSequential() {
        Properties properties = new Properties();
        properties.setProperty("drools.sequential", "false");
        RuleBaseConfiguration cfg = new RuleBaseConfiguration(properties);
        Assert.assertFalse(cfg.isSequential());
        Assert.assertTrue(((cfg.getAgendaGroupFactory()) instanceof PriorityQueueAgendaGroupFactory));
        properties = new Properties();
        properties.setProperty("drools.sequential.agenda", "sequential");
        properties.setProperty("drools.sequential", "true");
        cfg = new RuleBaseConfiguration(properties);
        Assert.assertTrue(cfg.isSequential());
        Assert.assertEquals(SEQUENTIAL, cfg.getSequentialAgenda());
        properties = new Properties();
        properties.setProperty("drools.sequential.agenda", "dynamic");
        properties.setProperty("drools.sequential", "true");
        cfg = new RuleBaseConfiguration(properties);
        Assert.assertTrue(cfg.isSequential());
        Assert.assertEquals(DYNAMIC, cfg.getSequentialAgenda());
        Assert.assertTrue(((cfg.getAgendaGroupFactory()) instanceof PriorityQueueAgendaGroupFactory));
    }
}

