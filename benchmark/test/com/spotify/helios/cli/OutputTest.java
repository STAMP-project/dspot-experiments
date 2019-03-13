/**
 * -
 * -\-\-
 * Helios Tools
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.cli;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class OutputTest {
    @Test
    public void testHumanDuration() throws Exception {
        Assert.assertThat(Output.humanDuration(standardSeconds(0)), Matchers.is("0 seconds"));
        Assert.assertThat(Output.humanDuration(standardSeconds(1)), Matchers.is("1 second"));
        Assert.assertThat(Output.humanDuration(standardSeconds(2)), Matchers.is("2 seconds"));
        Assert.assertThat(Output.humanDuration(standardSeconds(59)), Matchers.is("59 seconds"));
        Assert.assertThat(Output.humanDuration(standardSeconds(60)), Matchers.is("1 minute"));
        Assert.assertThat(Output.humanDuration(standardMinutes(1)), Matchers.is("1 minute"));
        Assert.assertThat(Output.humanDuration(standardMinutes(2)), Matchers.is("2 minutes"));
        Assert.assertThat(Output.humanDuration(standardMinutes(59)), Matchers.is("59 minutes"));
        Assert.assertThat(Output.humanDuration(standardMinutes(60)), Matchers.is("1 hour"));
        Assert.assertThat(Output.humanDuration(standardHours(1)), Matchers.is("1 hour"));
        Assert.assertThat(Output.humanDuration(standardHours(2)), Matchers.is("2 hours"));
        Assert.assertThat(Output.humanDuration(standardHours(23)), Matchers.is("23 hours"));
        Assert.assertThat(Output.humanDuration(standardHours(24)), Matchers.is("1 day"));
        Assert.assertThat(Output.humanDuration(standardDays(1)), Matchers.is("1 day"));
        Assert.assertThat(Output.humanDuration(standardDays(2)), Matchers.is("2 days"));
        Assert.assertThat(Output.humanDuration(standardDays(365)), Matchers.is("365 days"));
        Assert.assertThat(Output.humanDuration(standardDays(4711)), Matchers.is("4711 days"));
    }
}

