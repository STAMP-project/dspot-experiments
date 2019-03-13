/**
 * Copyright 2017 LinkedIn Corp.
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


import azkaban.db.DatabaseOperator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class JdbcTriggerImplTest {
    private static DatabaseOperator dbOperator;

    private TriggerLoader loader;

    @Test
    public void testRemoveTriggers() throws Exception {
        final Trigger t1 = createTrigger("testProj1", "testFlow1", "source1");
        final Trigger t2 = createTrigger("testProj2", "testFlow2", "source2");
        this.loader.addTrigger(t1);
        this.loader.addTrigger(t2);
        List<Trigger> ts = this.loader.loadTriggers();
        Assert.assertTrue(((ts.size()) == 2));
        this.loader.removeTrigger(t2);
        ts = this.loader.loadTriggers();
        Assert.assertTrue(((ts.size()) == 1));
        Assert.assertTrue(((ts.get(0).getTriggerId()) == (t1.getTriggerId())));
    }

    @Test
    public void testAddTrigger() throws Exception {
        final Trigger t1 = createTrigger("testProj1", "testFlow1", "source1");
        final Trigger t2 = createTrigger("testProj2", "testFlow2", "source2");
        this.loader.addTrigger(t1);
        List<Trigger> ts = this.loader.loadTriggers();
        Assert.assertTrue(((ts.size()) == 1));
        final Trigger t3 = ts.get(0);
        Assert.assertTrue(t3.getSource().equals("source1"));
        this.loader.addTrigger(t2);
        ts = this.loader.loadTriggers();
        Assert.assertTrue(((ts.size()) == 2));
        for (final Trigger t : ts) {
            if ((t.getTriggerId()) == (t2.getTriggerId())) {
                t.getSource().equals(t2.getSource());
            }
        }
    }

    @Test
    public void testUpdateTrigger() throws Exception {
        final Trigger t1 = createTrigger("testProj1", "testFlow1", "source1");
        t1.setResetOnExpire(true);
        this.loader.addTrigger(t1);
        List<Trigger> ts = this.loader.loadTriggers();
        Assert.assertTrue(((ts.get(0).isResetOnExpire()) == true));
        t1.setResetOnExpire(false);
        this.loader.updateTrigger(t1);
        ts = this.loader.loadTriggers();
        Assert.assertTrue(((ts.get(0).isResetOnExpire()) == false));
    }
}

