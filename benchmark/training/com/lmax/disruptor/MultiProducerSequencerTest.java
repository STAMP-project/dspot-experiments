/**
 * Copyright 2012 LMAX Ltd.
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
package com.lmax.disruptor;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MultiProducerSequencerTest {
    private final Sequencer publisher = new MultiProducerSequencer(1024, new BlockingWaitStrategy());

    @Test
    public void shouldOnlyAllowMessagesToBeAvailableIfSpecificallyPublished() throws Exception {
        publisher.publish(3);
        publisher.publish(5);
        Assert.assertThat(publisher.isAvailable(0), CoreMatchers.is(false));
        Assert.assertThat(publisher.isAvailable(1), CoreMatchers.is(false));
        Assert.assertThat(publisher.isAvailable(2), CoreMatchers.is(false));
        Assert.assertThat(publisher.isAvailable(3), CoreMatchers.is(true));
        Assert.assertThat(publisher.isAvailable(4), CoreMatchers.is(false));
        Assert.assertThat(publisher.isAvailable(5), CoreMatchers.is(true));
        Assert.assertThat(publisher.isAvailable(6), CoreMatchers.is(false));
    }
}

