/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.trigger;


import azkaban.executor.ExecutorManagerAdapter;
import azkaban.project.ProjectManager;
import azkaban.trigger.builtin.BasicTimeChecker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import static TriggerStatus.PAUSED;
import static TriggerStatus.READY;


// todo HappyRay: fix these slow tests or delete them.
@Ignore
public class TriggerManagerTest {
    private static TriggerLoader triggerLoader;

    private static ExecutorManagerAdapter executorManagerAdapter;

    private static ProjectManager projectManager;

    private TriggerManager triggerManager;

    @Test
    public void neverExpireTriggerTest() throws TriggerManagerException {
        final Trigger t1 = createNeverExpireTrigger("triggerLoader", 10);
        this.triggerManager.insertTrigger(t1);
        t1.setResetOnTrigger(false);
        final ThresholdChecker triggerChecker = ((ThresholdChecker) (t1.getTriggerCondition().getCheckers().values().toArray()[0]));
        final BasicTimeChecker expireChecker = ((BasicTimeChecker) (t1.getExpireCondition().getCheckers().values().toArray()[0]));
        ThresholdChecker.setVal(15);
        sleep(300);
        sleep(300);
        Assert.assertTrue(((triggerChecker.isCheckerMet()) == true));
        Assert.assertTrue(((expireChecker.eval()) == false));
        ThresholdChecker.setVal(25);
        sleep(300);
        Assert.assertTrue(((triggerChecker.isCheckerMet()) == true));
        Assert.assertTrue(((expireChecker.eval()) == false));
    }

    @Test
    public void timeCheckerAndExpireTriggerTest() throws TriggerManagerException {
        final long curr = System.currentTimeMillis();
        final Trigger t1 = createPeriodAndEndCheckerTrigger(curr);
        this.triggerManager.insertTrigger(t1);
        t1.setResetOnTrigger(true);
        final BasicTimeChecker expireChecker = ((BasicTimeChecker) (t1.getExpireCondition().getCheckers().values().toArray()[0]));
        sleep(1000);
        Assert.assertTrue(((expireChecker.eval()) == false));
        Assert.assertTrue(((t1.getStatus()) == (READY)));
        sleep(1000);
        sleep(1000);
        sleep(1000);
        Assert.assertTrue(((expireChecker.eval()) == true));
        Assert.assertTrue(((t1.getStatus()) == (PAUSED)));
        sleep(1000);
        Assert.assertTrue(((expireChecker.eval()) == true));
        Assert.assertTrue(((t1.getStatus()) == (PAUSED)));
    }

    public static class MockTriggerLoader implements TriggerLoader {
        private final Map<Integer, Trigger> triggers = new HashMap<>();

        private int idIndex = 0;

        @Override
        public void addTrigger(final Trigger t) throws TriggerLoaderException {
            t.setTriggerId(((this.idIndex)++));
            this.triggers.put(t.getTriggerId(), t);
        }

        @Override
        public void removeTrigger(final Trigger s) throws TriggerLoaderException {
            this.triggers.remove(s.getTriggerId());
        }

        @Override
        public void updateTrigger(final Trigger t) throws TriggerLoaderException {
            this.triggers.put(t.getTriggerId(), t);
        }

        @Override
        public List<Trigger> loadTriggers() {
            return new java.util.ArrayList(this.triggers.values());
        }

        @Override
        public Trigger loadTrigger(final int triggerId) throws TriggerLoaderException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Trigger> getUpdatedTriggers(final long lastUpdateTime) throws TriggerLoaderException {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

