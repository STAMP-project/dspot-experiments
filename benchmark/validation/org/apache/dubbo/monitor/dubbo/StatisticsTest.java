/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.monitor.dubbo;


import MonitorService.APPLICATION;
import MonitorService.CONCURRENT;
import MonitorService.CONSUMER;
import MonitorService.ELAPSED;
import MonitorService.FAILURE;
import MonitorService.GROUP;
import MonitorService.INTERFACE;
import MonitorService.MAX_CONCURRENT;
import MonitorService.MAX_ELAPSED;
import MonitorService.METHOD;
import MonitorService.SUCCESS;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class StatisticsTest {
    @Test
    public void testEquals() {
        URL statistics = new org.apache.dubbo.common.URLBuilder(Constants.DUBBO_PROTOCOL, "10.20.153.10", 0).addParameter(APPLICATION, "morgan").addParameter(INTERFACE, "MemberService").addParameter(METHOD, "findPerson").addParameter(CONSUMER, "10.20.153.11").addParameter(SUCCESS, 1).addParameter(FAILURE, 0).addParameter(ELAPSED, 3).addParameter(MAX_ELAPSED, 3).addParameter(CONCURRENT, 1).addParameter(MAX_CONCURRENT, 1).build();
        Statistics statistics1 = new Statistics(statistics);
        Statistics statistics2 = new Statistics(statistics);
        MatcherAssert.assertThat(statistics1, Matchers.equalTo(statistics1));
        MatcherAssert.assertThat(statistics1, Matchers.equalTo(statistics2));
        statistics1.setVersion("2");
        MatcherAssert.assertThat(statistics1, Matchers.not(Matchers.equalTo(statistics2)));
        MatcherAssert.assertThat(statistics1.hashCode(), Matchers.not(Matchers.equalTo(statistics2.hashCode())));
        statistics1.setMethod("anotherMethod");
        MatcherAssert.assertThat(statistics1, Matchers.not(Matchers.equalTo(statistics2)));
        MatcherAssert.assertThat(statistics1.hashCode(), Matchers.not(Matchers.equalTo(statistics2.hashCode())));
        statistics1.setClient("anotherClient");
        MatcherAssert.assertThat(statistics1, Matchers.not(Matchers.equalTo(statistics2)));
        MatcherAssert.assertThat(statistics1.hashCode(), Matchers.not(Matchers.equalTo(statistics2.hashCode())));
    }

    @Test
    public void testToString() {
        Statistics statistics = new Statistics(new URL("dubbo", "10.20.153.10", 0));
        statistics.setApplication("demo");
        statistics.setMethod("findPerson");
        statistics.setServer("10.20.153.10");
        statistics.setGroup("unit-test");
        statistics.setService("MemberService");
        MatcherAssert.assertThat(statistics.toString(), Matchers.is("dubbo://10.20.153.10"));
        Statistics statisticsWithDetailInfo = new Statistics(new org.apache.dubbo.common.URLBuilder(Constants.DUBBO_PROTOCOL, "10.20.153.10", 0).addParameter(APPLICATION, "morgan").addParameter(INTERFACE, "MemberService").addParameter(METHOD, "findPerson").addParameter(CONSUMER, "10.20.153.11").addParameter(GROUP, "unit-test").addParameter(SUCCESS, 1).addParameter(FAILURE, 0).addParameter(ELAPSED, 3).addParameter(MAX_ELAPSED, 3).addParameter(CONCURRENT, 1).addParameter(MAX_CONCURRENT, 1).build());
        MatcherAssert.assertThat(statisticsWithDetailInfo.getServer(), Matchers.equalTo(statistics.getServer()));
        MatcherAssert.assertThat(statisticsWithDetailInfo.getService(), Matchers.equalTo(statistics.getService()));
        MatcherAssert.assertThat(statisticsWithDetailInfo.getMethod(), Matchers.equalTo(statistics.getMethod()));
        MatcherAssert.assertThat(statisticsWithDetailInfo.getGroup(), Matchers.equalTo(statistics.getGroup()));
        MatcherAssert.assertThat(statisticsWithDetailInfo, Matchers.not(Matchers.equalTo(statistics)));
    }
}

