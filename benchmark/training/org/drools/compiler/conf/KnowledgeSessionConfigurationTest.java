/**
 * Copyright 2008 Red Hat
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
package org.drools.compiler.conf;


import BeliefSystemType.DEFEASIBLE;
import BeliefSystemType.JTMS;
import ClockTypeOption.PROPERTY_NAME;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;


public class KnowledgeSessionConfigurationTest {
    private KieSessionConfiguration config;

    @Test
    public void testClockTypeConfiguration() {
        // setting the option using the type safe method
        config.setOption(ClockTypeOption.get("pseudo"));
        // checking the type safe getOption() method
        Assert.assertEquals(ClockTypeOption.get("pseudo"), config.getOption(ClockTypeOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("pseudo", config.getProperty(PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(PROPERTY_NAME, "realtime");
        // checking the type safe getOption() method
        Assert.assertEquals(ClockTypeOption.get("realtime"), config.getOption(ClockTypeOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals("realtime", config.getProperty(PROPERTY_NAME));
    }

    @Test
    public void testBeliefSystemType() {
        config.setOption(BeliefSystemTypeOption.get(JTMS.toString()));
        Assert.assertEquals(BeliefSystemTypeOption.get(JTMS.toString()), config.getOption(BeliefSystemTypeOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals(JTMS.getId(), config.getProperty(BeliefSystemTypeOption.PROPERTY_NAME));
        // setting the options using the string based setProperty() method
        config.setProperty(BeliefSystemTypeOption.PROPERTY_NAME, DEFEASIBLE.getId());
        // checking the type safe getOption() method
        Assert.assertEquals(BeliefSystemTypeOption.get(DEFEASIBLE.getId()), config.getOption(BeliefSystemTypeOption.class));
        // checking the string based getProperty() method
        Assert.assertEquals(DEFEASIBLE.getId(), config.getProperty(BeliefSystemTypeOption.PROPERTY_NAME));
    }
}

