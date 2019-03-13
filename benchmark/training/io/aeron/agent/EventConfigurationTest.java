/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.agent;


import EventCode.FRAME_IN;
import EventCode.FRAME_OUT;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EventConfigurationTest {
    @Test
    public void nullPropertyShouldDefaultToProductionEventCodes() {
        Assert.assertThat(EventConfiguration.getEnabledEventCodes(null), Matchers.is(EnumSet.noneOf(EventCode.class)));
    }

    @Test
    public void malformedPropertyShouldDefaultToProductionEventCodes() {
        final PrintStream err = System.err;
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(stderr));
        try {
            final Set<EventCode> enabledEventCodes = EventConfiguration.getEnabledEventCodes("list of invalid options");
            Assert.assertThat(enabledEventCodes.size(), Matchers.is(0));
            Assert.assertThat(stderr.toString(), Matchers.startsWith("unknown event code: list of invalid options"));
        } finally {
            System.setErr(err);
        }
    }

    @Test
    public void allPropertyShouldReturnAllEventCodes() {
        Assert.assertThat(EventConfiguration.getEnabledEventCodes("all"), Matchers.is(EventConfiguration.ALL_LOGGER_EVENT_CODES));
    }

    @Test
    public void eventCodesPropertyShouldBeParsedAsListOfEventCodes() {
        final Set<EventCode> expectedCodes = EnumSet.of(FRAME_OUT, FRAME_IN);
        Assert.assertThat(EventConfiguration.getEnabledEventCodes("FRAME_OUT,FRAME_IN"), Matchers.is(expectedCodes));
    }

    @Test
    public void makeTagBitSet() {
        final Set<EventCode> eventCodes = EnumSet.of(FRAME_OUT, FRAME_IN);
        final long bitSet = EventConfiguration.makeTagBitSet(eventCodes);
        Assert.assertThat(bitSet, Matchers.is(((FRAME_OUT.tagBit()) | (FRAME_IN.tagBit()))));
    }
}

