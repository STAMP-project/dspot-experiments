/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.status.support;


import Status.Level;
import Status.Level.ERROR;
import Status.Level.OK;
import Status.Level.WARN;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.common.status.Status;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class StatusUtilsTest {
    @Test
    public void testGetSummaryStatus1() throws Exception {
        Status status1 = new Status(Level.ERROR);
        Status status2 = new Status(Level.WARN);
        Status status3 = new Status(Level.OK);
        Map<String, Status> statuses = new HashMap<String, Status>();
        statuses.put("status1", status1);
        statuses.put("status2", status2);
        statuses.put("status3", status3);
        Status status = StatusUtils.getSummaryStatus(statuses);
        MatcherAssert.assertThat(status.getLevel(), Matchers.is(ERROR));
        MatcherAssert.assertThat(status.getMessage(), Matchers.containsString("status1"));
        MatcherAssert.assertThat(status.getMessage(), Matchers.containsString("status2"));
        MatcherAssert.assertThat(status.getMessage(), Matchers.not(Matchers.containsString("status3")));
    }

    @Test
    public void testGetSummaryStatus2() throws Exception {
        Status status1 = new Status(Level.WARN);
        Status status2 = new Status(Level.OK);
        Map<String, Status> statuses = new HashMap<String, Status>();
        statuses.put("status1", status1);
        statuses.put("status2", status2);
        Status status = StatusUtils.getSummaryStatus(statuses);
        MatcherAssert.assertThat(status.getLevel(), Matchers.is(WARN));
        MatcherAssert.assertThat(status.getMessage(), Matchers.containsString("status1"));
        MatcherAssert.assertThat(status.getMessage(), Matchers.not(Matchers.containsString("status2")));
    }

    @Test
    public void testGetSummaryStatus3() throws Exception {
        Status status1 = new Status(Level.OK);
        Map<String, Status> statuses = new HashMap<String, Status>();
        statuses.put("status1", status1);
        Status status = StatusUtils.getSummaryStatus(statuses);
        MatcherAssert.assertThat(status.getLevel(), Matchers.is(OK));
        MatcherAssert.assertThat(status.getMessage(), Matchers.isEmptyOrNullString());
    }
}

