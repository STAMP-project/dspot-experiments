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


import java.math.BigDecimal;
import java.sql.SQLException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QTZ283_IgnoreMisfirePolicyJdbcStore_Test extends QuartzDatabaseTestSupport {
    private static final long DURATION_OF_FIRST_SCHEDULING = 10L;

    private static final Logger LOG = LoggerFactory.getLogger(QTZ283_IgnoreMisfirePolicyJdbcStore_Test.class);

    private static final int INTERVAL_IN_SECONDS = 5;

    @Test
    public void checkOldTriggerGetsFired() throws SQLException {
        BigDecimal misfirePolicyIgnoreTimesTriggered = JdbcQuartzDerbyUtilities.timesTriggered("trigger1", "group1");
        Assert.assertThat("The old trigger has never been fired, even if the policy is ignore", misfirePolicyIgnoreTimesTriggered, CoreMatchers.not(CoreMatchers.equalTo(BigDecimal.ZERO)));
    }
}

