/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.conf;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SmallTests.class, ClientTests.class })
public class TestConfigurationManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestConfigurationManager.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestConfigurationManager.class);

    class DummyConfigurationObserver implements ConfigurationObserver {
        private boolean notifiedOnChange = false;

        private ConfigurationManager cm;

        public DummyConfigurationObserver(ConfigurationManager cm) {
            this.cm = cm;
            register();
        }

        @Override
        public void onConfigurationChange(Configuration conf) {
            notifiedOnChange = true;
        }

        // Was the observer notified on Configuration change?
        public boolean wasNotifiedOnChange() {
            return notifiedOnChange;
        }

        public void resetNotifiedOnChange() {
            notifiedOnChange = false;
        }

        public void register() {
            this.cm.registerObserver(this);
        }

        public void deregister() {
            this.cm.deregisterObserver(this);
        }
    }

    /**
     * Test if observers get notified by the <code>ConfigurationManager</code>
     * when the Configuration is reloaded.
     */
    @Test
    public void testCheckIfObserversNotified() {
        Configuration conf = new Configuration();
        ConfigurationManager cm = new ConfigurationManager();
        TestConfigurationManager.DummyConfigurationObserver d1 = new TestConfigurationManager.DummyConfigurationObserver(cm);
        // Check if we get notified.
        cm.notifyAllObservers(conf);
        Assert.assertTrue(d1.wasNotifiedOnChange());
        d1.resetNotifiedOnChange();
        // Now check if we get notified on change with more than one observers.
        TestConfigurationManager.DummyConfigurationObserver d2 = new TestConfigurationManager.DummyConfigurationObserver(cm);
        cm.notifyAllObservers(conf);
        Assert.assertTrue(d1.wasNotifiedOnChange());
        d1.resetNotifiedOnChange();
        Assert.assertTrue(d2.wasNotifiedOnChange());
        d2.resetNotifiedOnChange();
        // Now try deregistering an observer and verify that it was not notified
        d2.deregister();
        cm.notifyAllObservers(conf);
        Assert.assertTrue(d1.wasNotifiedOnChange());
        d1.resetNotifiedOnChange();
        Assert.assertFalse(d2.wasNotifiedOnChange());
    }

    /**
     * Test if out-of-scope observers are deregistered on GC.
     */
    @Test
    public void testDeregisterOnOutOfScope() {
        Configuration conf = new Configuration();
        ConfigurationManager cm = new ConfigurationManager();
        boolean outOfScopeObserversDeregistered = false;
        // On my machine, I was able to cause a GC after around 5 iterations.
        // If we do not cause a GC in 100k iterations, which is very unlikely,
        // there might be something wrong with the GC.
        for (int i = 0; i < 100000; i++) {
            registerLocalObserver(cm);
            cm.notifyAllObservers(conf);
            // 'Suggest' the system to do a GC. We should be able to cause GC
            // atleast once in the 2000 iterations.
            System.gc();
            // If GC indeed happened, all the observers (which are all out of scope),
            // should have been deregistered.
            if ((cm.getNumObservers()) <= i) {
                outOfScopeObserversDeregistered = true;
                break;
            }
        }
        if (!outOfScopeObserversDeregistered) {
            TestConfigurationManager.LOG.warn("Observers were not GC-ed! Something seems to be wrong.");
        }
        Assert.assertTrue(outOfScopeObserversDeregistered);
    }
}

