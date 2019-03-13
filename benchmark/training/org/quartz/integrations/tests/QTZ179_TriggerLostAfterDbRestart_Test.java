/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
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
package org.quartz.integrations.tests;


import org.junit.Assert;
import org.junit.Test;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QTZ179_TriggerLostAfterDbRestart_Test extends QuartzDatabaseTestSupport {
    private static final long DURATION_OF_FIRST_SCHEDULING = 9L;

    private static final long DURATION_OF_NETWORK_FAILURE = 10L;

    private static final long DURATION_OF_SECOND_SCHEDULING = 10L;

    private static final Logger LOG = LoggerFactory.getLogger(QTZ179_TriggerLostAfterDbRestart_Test.class);

    private static final int INTERVAL_IN_SECONDS = 3;

    private static Trigger trigger1_1;

    private static Trigger trigger2_1;

    private static Trigger trigger1_2;

    private static Trigger trigger2_2;

    @Test
    public void checkAll4TriggersStillRunningTest() throws Exception {
        QTZ179_TriggerLostAfterDbRestart_Test.LOG.info("------- Scheduler Started -----------------");
        // wait long enough so that the scheduler as an opportunity to
        // run the job!
        try {
            Thread.sleep(((QTZ179_TriggerLostAfterDbRestart_Test.DURATION_OF_FIRST_SCHEDULING) * 1000L));
        } catch (Exception e) {
        }
        // there should be maximum 1 trigger in acquired state
        if ((JdbcQuartzDerbyUtilities.triggersInAcquiredState()) > 1) {
            Assert.fail("There should not be more than 1 trigger in ACQUIRED state in the DB.");
        }
        // Shutting down and starting up again the database to simulate a
        // network error
        try {
            QTZ179_TriggerLostAfterDbRestart_Test.LOG.info("------- Shutting down database ! -----------------");
            QuartzDatabaseTestSupport.derbyServer.shutdown();
            Thread.sleep(((QTZ179_TriggerLostAfterDbRestart_Test.DURATION_OF_NETWORK_FAILURE) * 1000L));
            QuartzDatabaseTestSupport.derbyServer.start(null);
            QTZ179_TriggerLostAfterDbRestart_Test.LOG.info("------- Database back online ! -----------------");
            Thread.sleep(((QTZ179_TriggerLostAfterDbRestart_Test.DURATION_OF_SECOND_SCHEDULING) * 1000L));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int triggersInAcquiredState = JdbcQuartzDerbyUtilities.triggersInAcquiredState();
        Assert.assertFalse(("There should not be more than 1 trigger in ACQUIRED state in the DB, but found " + triggersInAcquiredState), (triggersInAcquiredState > 1));
    }
}

