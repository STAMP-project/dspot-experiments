/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.decisiontable;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;


public class CalendarTimerResourcesTest {
    private KieSession ksession;

    private SessionPseudoClock clock;

    @Test
    public void test() {
        ksession.getCalendars().set("tuesday", CalendarTimerResourcesTest.TUESDAY);
        clock.advanceTime(4, TimeUnit.DAYS);// README now it is set to monday (test fails with NPE), when is set to tuesday (rule should fire) then test works

        ksession.fireAllRules();
    }

    private static final Calendar TUESDAY = new org.kie.api.time.Calendar() {
        @Override
        public boolean isTimeIncluded(long timestamp) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);
            int day = c.get(Calendar.DAY_OF_WEEK);
            return day == (Calendar.TUESDAY);
        }
    };
}

