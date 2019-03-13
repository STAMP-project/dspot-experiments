/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config;


import TimerConfig.TIMER_ONLY_ON_CHANGES;
import TimerConfig.TIMER_SPEC;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TimerConfigTest {
    private TimerConfig timerConfig;

    @Test
    public void shouldPopulateErrorsWhenTimerSpecIsInvalid() {
        timerConfig = new TimerConfig("SOME JUNK TIMER SPEC", false);
        timerConfig.validate(null);
        Assert.assertThat(timerConfig.errors().firstError(), Matchers.startsWith("Invalid cron syntax"));
    }

    @Test
    public void shouldPopulateErrorsWhenTimerSpecIsNull() {
        timerConfig = new TimerConfig(null, true);
        timerConfig.validate(null);
        Assert.assertThat(timerConfig.errors().firstError(), Matchers.is("Timer Spec can not be null."));
    }

    @Test
    public void shouldNotPopulateErrorsWhenTimerSpecIsValid() {
        timerConfig = new TimerConfig("0 0 12 * * ?", false);
        timerConfig.validate(null);
        Assert.assertThat(timerConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldCreateTimerGivenTheAttributeMapIfOnlyOnChangesIsCheckedInUI() {
        HashMap<String, String> mapOfTimerValues = new HashMap<>();
        mapOfTimerValues.put(TIMER_SPEC, "0 0 * * * ?");
        mapOfTimerValues.put(TIMER_ONLY_ON_CHANGES, "1");
        TimerConfig timer = TimerConfig.createTimer(mapOfTimerValues);
        Assert.assertThat(timer.getTimerSpec(), Matchers.is("0 0 * * * ?"));
        Assert.assertThat(timer.shouldTriggerOnlyOnChanges(), Matchers.is(true));
    }

    @Test
    public void shouldCreateTimerGivenTheAttributeMapOnlyOnChangesIsNotPresent() {
        HashMap<String, String> mapOfTimerValues = new HashMap<>();
        mapOfTimerValues.put(TIMER_SPEC, "0 0 * * * ?");
        TimerConfig timer = TimerConfig.createTimer(mapOfTimerValues);
        Assert.assertThat(timer.getTimerSpec(), Matchers.is("0 0 * * * ?"));
        Assert.assertThat(timer.shouldTriggerOnlyOnChanges(), Matchers.is(false));
    }

    @Test
    public void shouldCreateTimerGivenTheAttributeMapIfOnlyOnChangesIsNotCheckedInUI() {
        HashMap<String, String> mapOfTimerValues = new HashMap<>();
        mapOfTimerValues.put(TIMER_SPEC, "0 0 * * * ?");
        mapOfTimerValues.put(TIMER_ONLY_ON_CHANGES, "0");
        TimerConfig timer = TimerConfig.createTimer(mapOfTimerValues);
        Assert.assertThat(timer.getTimerSpec(), Matchers.is("0 0 * * * ?"));
        Assert.assertThat(timer.getOnlyOnChanges(), Matchers.is(false));
    }
}

